/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scandit.datacapture.pricecapturesample.scan

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import com.scandit.datacapture.core.common.feedback.Feedback
import com.scandit.datacapture.core.common.geometry.Anchor
import com.scandit.datacapture.core.common.geometry.MeasureUnit
import com.scandit.datacapture.core.common.geometry.PointWithUnit
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.ui.style.Brush
import com.scandit.datacapture.label.capture.LabelCapture
import com.scandit.datacapture.label.capture.LabelCaptureListener
import com.scandit.datacapture.label.capture.LabelCaptureSession
import com.scandit.datacapture.label.data.CapturedLabel
import com.scandit.datacapture.label.data.LabelField
import com.scandit.datacapture.label.data.LabelFieldType
import com.scandit.datacapture.label.ui.overlay.LabelCaptureAdvancedOverlay
import com.scandit.datacapture.label.ui.overlay.LabelCaptureBasicOverlay
import com.scandit.datacapture.label.ui.overlay.LabelCaptureBasicOverlayListener
import com.scandit.datacapture.pricecapturesample.data.BarcodePriceDatabase
import com.scandit.datacapture.pricecapturesample.data.ValidationState

/**
 * Drives the AR feedback for the sample. It plays two roles:
 *
 *  - [LabelCaptureBasicOverlayListener]: colors the price field with the validation color.
 *  - [LabelCaptureListener]: derives each captured label's validation state and, via the
 *    [advancedOverlay], pins a status marker just above the price field.
 *
 * The advanced overlay is driven imperatively (push) rather than through its listener on purpose:
 * the marker's appearance depends on the *label's* state (barcode ↔ price), but the advanced
 * overlay's per-field listener callback receives only a `LabelField` with no way back to its label.
 * Pushing from [onSessionUpdated] — where we hold the whole [CapturedLabel] — lets us both compute
 * the state and anchor the view on the price field.
 */
class PriceLabelOverlayHandler(
    private val context: Context,
    private val database: BarcodePriceDatabase,
) : LabelCaptureListener, LabelCaptureBasicOverlayListener {

    var advancedOverlay: LabelCaptureAdvancedOverlay? = null

    private val transparentBrush = Brush(Color.TRANSPARENT, Color.TRANSPARENT, 0f)

    // Lift the marker by exactly its tip-to-center distance so the tail tip lands right on the
    // highlight's top edge — the marker sits snug just above the highlight.
    private val pinOffset = PointWithUnit(0f, -StatusPinView.TIP_BELOW_CENTER_DP, MeasureUnit.DIP)

    // Last state pinned per label, so we only re-push the marker when the state actually changes.
    private val pinnedStates = HashMap<Int, ValidationState>()

    // Played once per newly captured label (see onSessionUpdated). The mode's own automatic
    // feedback is disabled so it does not fire on every frame the label stays in view.
    private val feedback = Feedback.defaultFeedback()

    override fun onSessionUpdated(
        mode: LabelCapture,
        session: LabelCaptureSession,
        data: FrameData,
    ) {
        val overlay = advancedOverlay
        val present = HashSet<Int>()

        session.capturedLabels.forEach { label ->
            val priceField = label.priceField()
            val price = label.capturedPrice()
            // Per the spec the price is never drawn on the label, only logged for debugging.
            if (price != null) {
                Log.d(TAG, "Captured label: barcode=${label.barcodeData()}, price=$price")
            }

            val state = stateFor(label, price)
            if (overlay == null || priceField == null || state == null) return@forEach

            present.add(label.trackingId)
            if (pinnedStates[label.trackingId] != state) {
                // First determined state for this label (it was not pinned yet) means a freshly
                // captured price: play the feedback once. Re-scanning emits again because the
                // label leaves the frame and is dropped from pinnedStates below.
                if (label.trackingId !in pinnedStates) {
                    feedback.emit()
                }
                pinnedStates[label.trackingId] = state
                overlay.setViewForCapturedLabelField(priceField, label, StatusPinView(context, state))
                overlay.setAnchorForCapturedLabelField(priceField, label, Anchor.TOP_CENTER)
                overlay.setOffsetForCapturedLabelField(priceField, label, pinOffset)
            }
        }

        // Forget labels that have left the frame; the overlay removes their views automatically.
        pinnedStates.keys.retainAll(present)
    }

    override fun brushForField(
        overlay: LabelCaptureBasicOverlay,
        field: LabelField,
        label: CapturedLabel,
    ): Brush {
        // Highlight the price (TEXT) field only.
        if (field.type != LabelFieldType.TEXT) return transparentBrush
        val state = stateFor(label, label.capturedPrice()) ?: return transparentBrush
        return brushFor(state)
    }

    override fun brushForLabel(
        overlay: LabelCaptureBasicOverlay,
        label: CapturedLabel,
    ): Brush = transparentBrush

    /**
     * Resolve the validation state of a label, or `null` while there is not yet enough information
     * to show feedback (no barcode read, or a known barcode whose price has not been captured yet).
     */
    private fun stateFor(label: CapturedLabel, capturedPrice: Double?): ValidationState? {
        val barcode = label.barcodeData() ?: return null
        return database.validate(barcode, capturedPrice)
    }

    private fun brushFor(state: ValidationState): Brush {
        val color = ContextCompat.getColor(context, state.colorRes)
        return Brush(Color.TRANSPARENT, color, STROKE_WIDTH_DP)
    }

    // The price-capture definition emits two fields: the price (TEXT) and the SKU (BARCODE).
    private fun CapturedLabel.priceField(): LabelField? =
        fields.firstOrNull { it.type == LabelFieldType.TEXT }

    private fun CapturedLabel.barcodeData(): String? =
        fields.firstOrNull { it.type == LabelFieldType.BARCODE }?.barcode?.data

    private fun CapturedLabel.capturedPrice(): Double? = parsePrice(priceField()?.text)

    private fun parsePrice(raw: String?): Double? {
        if (raw.isNullOrBlank()) return null
        // The price field is captured as text, so we normalize a decimal comma and drop any currency
        // symbol / spaces before parsing. Grouped values with a thousands separator (e.g.
        // "1,234.56") are not expected on a single-item shelf price and are not handled here.
        val normalized = raw.replace(',', '.').replace(PRICE_NOISE, "")
        return normalized.toDoubleOrNull()
    }

    companion object {
        private const val TAG = "PriceCapture"
        private const val STROKE_WIDTH_DP = 2f
        private val PRICE_NOISE = Regex("[^0-9.]")
    }
}

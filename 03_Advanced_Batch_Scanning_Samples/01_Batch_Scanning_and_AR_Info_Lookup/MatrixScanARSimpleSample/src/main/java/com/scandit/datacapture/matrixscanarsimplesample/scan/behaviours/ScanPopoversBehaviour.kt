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

package com.scandit.datacapture.matrixscanarsimplesample.scan.behaviours

import android.content.Context
import android.graphics.Color
import android.view.View
import com.scandit.datacapture.barcode.ar.capture.BarcodeAr
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArAnnotationProvider
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArPopoverAnnotation
import com.scandit.datacapture.barcode.ar.ui.annotations.popover.BarcodeArPopoverAnnotationButton
import com.scandit.datacapture.barcode.ar.ui.annotations.popover.BarcodeArPopoverAnnotationListener
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArCircleHighlight
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArCircleHighlightPreset
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlight
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlightProvider
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.core.ui.icon.ScanditIcon
import com.scandit.datacapture.core.ui.icon.ScanditIconShape
import com.scandit.datacapture.core.ui.icon.ScanditIconType
import com.scandit.datacapture.core.ui.style.Brush

class ScanPopoversBehaviour(
    private val resetView: () -> Unit,
) : ScanBehaviour {

    private enum class BarcodeStatus {
        Accepted,
        Rejected,
        Wrong,
    }

    private val highlights: MutableMap<String, BarcodeArCircleHighlight> = mutableMapOf()
    private val barcodeStatus: MutableMap<String, BarcodeStatus> = mutableMapOf()

    private val redColor = Color.parseColor("#D92121")
    private val greenColor = Color.parseColor("#0D853D")

    override fun highlightForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArHighlightProvider.Callback
    ) {
        // Assign an initial status to each detected barcode.
        barcode.data?.let { data ->
            if (!barcodeStatus.contains(data)) {
                barcodeStatus[data] = if (barcodeStatus.size % 2 == 0)
                    BarcodeStatus.Wrong
                else
                    BarcodeStatus.Accepted
            }
        }

        // Return a circular icon highlight that will be displayed over each detected barcode.
        barcode.data?.let { data ->
            val highlight = getOrCreateHighlightForBarcode(context, barcode)
            updateHighlight(highlight, data)
            callback.onData(highlight)
        }
    }

    // Fetch a highlight from the cache, or create it if it doesn't exist.
    private fun getOrCreateHighlightForBarcode(
        context: Context,
        barcode: Barcode,
    ): BarcodeArCircleHighlight {
        val data = barcode.data
            ?: error("Attempted creating a highlight for a barcode with no data")

        return highlights[data]
            ?: BarcodeArCircleHighlight(context, barcode, BarcodeArCircleHighlightPreset.ICON)
                .also { highlights[data] = it }
    }

    private fun updateHighlight(
        highlight: BarcodeArCircleHighlight,
        barcode: String,
    ) {
        when (barcodeStatus[barcode]) {
            // Set green highlight with checkmark for accepted barcodes.
            BarcodeStatus.Accepted -> {
                highlight.brush = Brush(greenColor, greenColor, 1f)
                highlight.icon = ScanditIcon.builder()
                    .withIcon(ScanditIconType.CHECKMARK)
                    .withIconColor(Color.WHITE)
                    .build()
                highlight.isPulsing = false
            }
            // Set red highlight with exclamation mark for wrong barcodes.
            BarcodeStatus.Wrong -> {
                highlight.brush = Brush(redColor, redColor, 1f)
                highlight.icon = ScanditIcon.builder()
                    .withIcon(ScanditIconType.EXCLAMATION_MARK)
                    .withIconColor(Color.WHITE)
                    .build()
                highlight.isPulsing = true
            }
            // Set red highlight with exclamation mark for rejected barcodes.
            BarcodeStatus.Rejected -> {
                highlight.brush = Brush(redColor, redColor, 1f)
                highlight.icon = ScanditIcon.builder()
                    .withIcon(ScanditIconType.X_MARK)
                    .withIconColor(Color.WHITE)
                    .build()
                highlight.isPulsing = false
            }
            null -> error("Barcode with no data")
        }
    }

    override fun annotationForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArAnnotationProvider.Callback,
    ) {
        if (barcodeStatus[barcode.data] != BarcodeStatus.Wrong) return

        // Create "reject" icon for popover button.
        val rejectIcon = ScanditIcon.builder()
            .withIcon(ScanditIconType.X_MARK)
            .withIconColor(Color.WHITE)
            .withBackgroundShape(ScanditIconShape.CIRCLE)
            .withBackgroundColor(redColor)
            .build()

        // Create "accept" icon for popover button.
        val acceptIcon = ScanditIcon.builder()
            .withIcon(ScanditIconType.CHECKMARK)
            .withIconColor(Color.WHITE)
            .withBackgroundShape(ScanditIconShape.CIRCLE)
            .withBackgroundColor(greenColor)
            .build()

        // Create popover annotation with both buttons.
        val annotation = BarcodeArPopoverAnnotation(
            context, barcode, listOf(
                BarcodeArPopoverAnnotationButton(rejectIcon, "Reject"),
                BarcodeArPopoverAnnotationButton(acceptIcon, "Accept")
            )
        )

        // Set listener to receive tap events on the popover buttons.
        annotation.listener = object : BarcodeArPopoverAnnotationListener {
            override fun onPopoverButtonTapped(
                popover: BarcodeArPopoverAnnotation,
                button: BarcodeArPopoverAnnotationButton,
                buttonIndex: Int
            ) {
                popover.barcode.data?.let { data ->
                    // Update barcode acceptance status and update highlight.
                    when (buttonIndex) {
                        0 -> barcodeStatus[data] = BarcodeStatus.Rejected
                        1 -> barcodeStatus[data] = BarcodeStatus.Accepted
                    }
                    highlights[data]?.let {
                        updateHighlight(it, data)
                    }

                    // Reset the AR view to clear cached annotations and trigger fresh annotations
                    // from the provider when barcodes are detected again.
                    // This will prevent annotations from being displayed again
                    // when a barcode has already been accepted or rejected.
                    resetView()
                }
            }
        }

        callback.onData(annotation)
    }

    override fun onHighlightForBarcodeTapped(
        barcodeAr: BarcodeAr,
        barcode: Barcode,
        highlight: BarcodeArHighlight,
        highlightView: View
    ) {
        // Not needed.
    }
}

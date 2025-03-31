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
package com.scandit.datacapture.priceweightlabelcapture.data

import android.content.Context
import android.graphics.Color
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.ui.style.Brush
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle
import com.scandit.datacapture.label.capture.LabelCapture
import com.scandit.datacapture.label.capture.LabelCaptureListener
import com.scandit.datacapture.label.capture.LabelCaptureSession
import com.scandit.datacapture.label.capture.LabelCaptureSettings
import com.scandit.datacapture.label.capture.labelCaptureSettings
import com.scandit.datacapture.label.data.CapturedLabel
import com.scandit.datacapture.label.data.LabelField
import com.scandit.datacapture.label.ui.overlay.LabelCaptureBasicOverlay
import com.scandit.datacapture.label.ui.overlay.LabelCaptureBasicOverlayListener
import com.scandit.datacapture.priceweightlabelcapture.R
import com.scandit.datacapture.priceweightlabelcapture.Settings
import com.scandit.datacapture.priceweightlabelcapture.utils.withAlpha
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object LabelCaptureProvider {

    /**
     * Building the label capture object, setting a listener to capture labels.
     */
    fun buildLabelCapture(
        dataCaptureContext: DataCaptureContext,
        coroutineScope: CoroutineScope,
        onLabelScanned: suspend (label: ScannedResult) -> Unit,
        onPartialLabelScanned: (label: PartialLabel) -> Unit,
    ): LabelCapture {
        val settings = buildLabelCaptureSettings()
        return LabelCapture.forDataCaptureContext(dataCaptureContext, settings).also { mode ->
            mode.addListener(object : LabelCaptureListener {
                override fun onSessionUpdated(
                    mode: LabelCapture,
                    session: LabelCaptureSession,
                    data: FrameData
                ) {
                    val capturedLabel = session.capturedLabels.firstOrNull() ?: return

                    extractResult(capturedLabel)?.also {
                        mode.isEnabled = false
                        coroutineScope.launch {
                            onLabelScanned(it)
                        }
                    } ?: capturedLabel.toPartialLabel()?.also {
                        mode.isEnabled = false
                        onPartialLabelScanned(it)
                    }
                }
            })
        }
    }

    /**
     * Building a LabelCaptureBasicOverlay with different brushes for different fields.
     */
    fun buildOverlay(context: Context, labelCapture: LabelCapture): LabelCaptureBasicOverlay {
        val upcBrush = Brush(
            context.getColor(R.color.upc).withAlpha(128),
            context.getColor(R.color.upc),
            1f
        )
        val weightBrush = Brush(
            context.getColor(R.color.weight).withAlpha(128),
            context.getColor(R.color.weight),
            1f
        )
        val unitPriceBrush = Brush(
            context.getColor(R.color.unitPrice).withAlpha(128),
            context.getColor(R.color.unitPrice),
            1f
        )
        val transparentBrush = Brush(Color.TRANSPARENT, Color.TRANSPARENT, 0f)

        val labelCaptureBrushListener = object : LabelCaptureBasicOverlayListener {
            override fun brushForField(
                overlay: LabelCaptureBasicOverlay,
                field: LabelField,
                label: CapturedLabel
            ): Brush? = when (field.name) {
                FIELD_BARCODE -> upcBrush
                FIELD_WEIGHT -> weightBrush
                FIELD_UNIT_PRICE -> unitPriceBrush
                else -> null
            }

            override fun brushForLabel(
                overlay: LabelCaptureBasicOverlay,
                label: CapturedLabel
            ): Brush = transparentBrush
        }

        return LabelCaptureBasicOverlay.newInstance(labelCapture, null).also { overlay ->
            overlay.listener = labelCaptureBrushListener
            overlay.viewfinder = RectangularViewfinder(RectangularViewfinderStyle.SQUARE)
        }
    }

    private fun buildLabelCaptureSettings(): LabelCaptureSettings =
        // This is an example of labelCaptureSettings. Here we set two labels, one with a simple
        // barcode, and one label that has price and weight.
        labelCaptureSettings {
            label(LABEL_REGULAR_ITEM) {
                customBarcode(FIELD_BARCODE) {
                    setSymbologies(Symbology.EAN13_UPCA)
                    setPattern(Settings.regularItemPattern)
                }
            }
            label(LABEL_WEIGHT_PRICE) {
                customBarcode(FIELD_BARCODE) {
                    setSymbologies(
                        Symbology.EAN13_UPCA,
                        Symbology.GS1_DATABAR_EXPANDED,
                        Symbology.CODE128
                    )
                    setPattern(Settings.weightLabelPattern)
                    isOptional = false
                }
                unitPriceText(FIELD_UNIT_PRICE) {
                    isOptional = true
                }
                weightText(FIELD_WEIGHT) {
                    isOptional = true
                }
            }
        }

    private fun extractResult(capturedLabel: CapturedLabel): ScannedResult? {
        if (capturedLabel.name == LABEL_REGULAR_ITEM) {
            val data =
                capturedLabel.fields.find { it.name == FIELD_BARCODE }?.barcode?.data ?: return null
            return ScannedResult.Barcode(data)
        }

        return capturedLabel.toLabel()
    }

    private fun CapturedLabel.toLabel(): ScannedResult.Label? {
        val data = fields.find { it.name == FIELD_BARCODE }?.barcode?.data ?: return null
        val unitPrice = fields.find { it.name == FIELD_UNIT_PRICE }?.text ?: return null
        val weight = fields.find { it.name == FIELD_WEIGHT }?.text ?: return null

        return ScannedResult.Label(data, weight, unitPrice)
    }

    private fun CapturedLabel.toPartialLabel(): PartialLabel? {
        val data = fields.find { it.name == FIELD_BARCODE }?.barcode?.data ?: return null
        val unitPrice = fields.find { it.name == FIELD_UNIT_PRICE }?.text
        val weight = fields.find { it.name == FIELD_WEIGHT }?.text

        return PartialLabel(data, weight, unitPrice)
    }

    private const val FIELD_BARCODE = ""
    private const val FIELD_UNIT_PRICE = "value:unit_price"
    private const val FIELD_WEIGHT = "value:weight"
    private const val LABEL_WEIGHT_PRICE = "weighted_item"
    private const val LABEL_REGULAR_ITEM = "regular_item"
}

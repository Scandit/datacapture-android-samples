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
package com.scandit.datacapture.labelcapturesimplesample.data

import android.content.Context
import android.graphics.Color
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.ui.style.Brush
import com.scandit.datacapture.label.capture.LabelCapture
import com.scandit.datacapture.label.capture.LabelCaptureSettings
import com.scandit.datacapture.label.capture.labelCaptureSettings
import com.scandit.datacapture.label.data.CapturedLabel
import com.scandit.datacapture.label.data.LabelField
import com.scandit.datacapture.label.ui.overlay.LabelCaptureBasicOverlay
import com.scandit.datacapture.label.ui.overlay.LabelCaptureBasicOverlayListener
import com.scandit.datacapture.label.ui.overlay.validation.LabelCaptureValidationFlowListener
import com.scandit.datacapture.label.ui.overlay.validation.LabelCaptureValidationFlowOverlay
import com.scandit.datacapture.label.ui.overlay.validation.LabelCaptureValidationFlowSettings
import com.scandit.datacapture.labelcapturesimplesample.R
import com.scandit.datacapture.labelcapturesimplesample.utils.withAlpha

object LabelCaptureProvider {

    /**
     * Building the label capture object, setting a listener to capture labels.
     */
    fun buildLabelCapture(
        dataCaptureContext: DataCaptureContext,
    ): LabelCapture =
        LabelCapture.forDataCaptureContext(dataCaptureContext, buildLabelCaptureSettings())

    /**
     * Building a LabelCaptureBasicOverlay with different brushes for different fields.
     */
    fun buildOverlay(context: Context, labelCapture: LabelCapture): LabelCaptureBasicOverlay {
        val upcBrush = Brush(
            context.getColor(R.color.upc).withAlpha(128),
            context.getColor(R.color.upc),
            1f
        )
        val expiryDateBrush = Brush(
            context.getColor(R.color.expiryDate).withAlpha(128),
            context.getColor(R.color.expiryDate),
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
                FIELD_EXPIRY_DATE -> expiryDateBrush
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
        }
    }

    fun buildValidationFlowOverlay(
        context: Context,
        labelCapture: LabelCapture,
        onLabelScanned: (label: ScannedResult) -> Unit,
    ): LabelCaptureValidationFlowOverlay {
        val flowListener = object : LabelCaptureValidationFlowListener {
            override fun onValidationFlowLabelCaptured(fields: List<LabelField>) {
                fields.toScannedResult()?.let { result ->
                    onLabelScanned(result)
                }
            }
        }

        // Customize the validation flow by changing labels.
        val settings = LabelCaptureValidationFlowSettings.newInstance()
        // settings.manualInputButtonText = "Edit manually"

        return LabelCaptureValidationFlowOverlay.newInstance(context, labelCapture, null)
            .also { overlay ->
                overlay.listener = flowListener
                overlay.applySettings(settings)
            }
    }

    private fun buildLabelCaptureSettings(): LabelCaptureSettings {
        // This is an example of labelCaptureSettings. Here we set two labels, one with a simple
        // barcode, and one label that has price and weight.
        val settings = labelCaptureSettings {
            label(LABEL_WEIGHT_PRICE) {
                customBarcode(FIELD_BARCODE) {
                    setSymbologies(
                        Symbology.EAN13_UPCA,
                        Symbology.GS1_DATABAR_EXPANDED,
                        Symbology.CODE128
                    )
                    isOptional = false
                }
                expiryDateText(FIELD_EXPIRY_DATE) {
                    isOptional = true
                }
                unitPriceText(FIELD_UNIT_PRICE) {
                    isOptional = true
                }
                weightText(FIELD_WEIGHT) {
                    isOptional = true
                }
            }
        }

        // You can customize the label definition to adapt it to your use-case.
        // For example, you can use the following label definition for Smart Devices box Scanning.
        // return labelCaptureSettings {
        //     label("imei_label") {
        //         customBarcode("barcode") {
        //             setSymbologies(Symbology.EAN13_UPCA)
        //             isOptional = false
        //         }
        //         imeiOneBarcode("imei_one") {
        //             isOptional = true
        //         }
        //         imeiTwoBarcode("imei_two") {
        //             isOptional = true
        //         }
        //         serialNumberBarcode("serial") {
        //             isOptional = true
        //         }
        //     }
        // }

        return settings
    }

    private fun List<LabelField>.toScannedResult(): ScannedResult? = ScannedResult(mapNotNull {
        val data = it.barcode?.data ?: it.text
        if (!data.isNullOrBlank()) {
            it.name to data
        } else {
            null
        }
    }.toMap<String, String>())

    private const val FIELD_BARCODE = "barcode"
    private const val FIELD_UNIT_PRICE = "unit_price"
    private const val FIELD_WEIGHT = "weight"
    private const val FIELD_EXPIRY_DATE = "expiry_date"
    private const val LABEL_WEIGHT_PRICE = "weighted_item"
}

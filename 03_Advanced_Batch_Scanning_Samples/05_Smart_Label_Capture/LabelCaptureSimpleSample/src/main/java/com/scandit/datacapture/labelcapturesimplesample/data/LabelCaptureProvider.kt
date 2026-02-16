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
import com.scandit.datacapture.label.data.LabelDateComponentFormat
import com.scandit.datacapture.label.data.LabelDateFormat
import com.scandit.datacapture.label.data.LabelDateResult
import com.scandit.datacapture.label.data.LabelField
import com.scandit.datacapture.label.ui.overlay.LabelCaptureBasicOverlay
import com.scandit.datacapture.label.ui.overlay.LabelCaptureBasicOverlayListener
import com.scandit.datacapture.label.ui.overlay.validation.LabelCaptureValidationFlowListener
import com.scandit.datacapture.label.ui.overlay.validation.LabelCaptureValidationFlowOverlay
import com.scandit.datacapture.label.ui.overlay.validation.LabelCaptureValidationFlowSettings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
        val labelCaptureBrushListener = object : LabelCaptureBasicOverlayListener {

            val transparentBrush = Brush(Color.TRANSPARENT, Color.TRANSPARENT, 0f)

            // val upcBrush = Brush(
            //     Color.parseColor("#CCFF0000"),
            //     Color.parseColor("#FF0000"),
            //     1f
            // )

            // Brushes can be customized by field...
            // override fun brushForField(
            //     overlay: LabelCaptureBasicOverlay,
            //     field: LabelField,
            //     label: CapturedLabel
            // ): Brush? = when (field.name) {
            //     FIELD_BARCODE -> upcBrush
            //     else -> null // Return null to use default brush
            // }

            // ... or by label.
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
        // This is an example of labelCaptureSettings. Here we set a label
        // that has a barcode, date, price and weight fields.
        val settings = labelCaptureSettings {
            label(LABEL_TITLE) {
                customBarcode(FIELD_BARCODE) {
                    setSymbologies(
                        Symbology.EAN13_UPCA,
                        Symbology.GS1_DATABAR_EXPANDED,
                        Symbology.CODE128
                    )
                }
                expiryDateText(FIELD_EXPIRY_DATE) {
                    setLabelDateFormat(
                        LabelDateFormat(
                            componentFormat = LabelDateComponentFormat.MDY,
                            acceptPartialDates = false,
                        )
                    )
                }
                totalPriceText(FIELD_TOTAL_PRICE) {
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
        val date = it.asDate()
        if (date != null) {
            it.name to formatDateResult(date)
        } else {
            val data = it.barcode?.data ?: it.text
            if (!data.isNullOrBlank()) {
                it.name to data
            } else {
                null
            }
        }
    }.toMap<String, String>())

    private fun formatDateResult(date: LabelDateResult): String {
        val calendar = Calendar.getInstance()
        calendar.set(date.year!!, date.month!! - 1, date.day!!)
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(calendar.time)
    }

    private const val LABEL_TITLE = "Retail Item"
    private const val FIELD_BARCODE = "barcode"
    private const val FIELD_EXPIRY_DATE = "expiry date"
    private const val FIELD_TOTAL_PRICE = "Total Price"
    private const val DATE_FORMAT = "dd - MM - yyyy"
}

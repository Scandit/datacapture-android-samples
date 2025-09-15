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
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArStatusIconAnnotation
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlight
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlightProvider
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArRectangleHighlight
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.core.ui.icon.ScanditIcon
import com.scandit.datacapture.core.ui.icon.ScanditIconShape
import com.scandit.datacapture.core.ui.icon.ScanditIconType

class ScanStatusIconsBehaviour : ScanBehaviour {

    private enum class BarcodeStatus {
        CloseToExpiry,
        Expired,
    }

    private val barcodeStatus: MutableMap<String, BarcodeStatus> = mutableMapOf()

    private val redColor = Color.parseColor("#D92121")
    private val yellowColor = Color.parseColor("#FBC02C")

    override fun highlightForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArHighlightProvider.Callback
    ) {
        // Assign an initial status to each detected barcode.
        barcode.data?.let { data ->
            if (!barcodeStatus.contains(data)) {
                barcodeStatus[data] = if (barcodeStatus.size % 2 == 0)
                    BarcodeStatus.CloseToExpiry
                else
                    BarcodeStatus.Expired
            }
        }

        // Return a rectangular highlight that will be displayed over each detected barcode.
        val highlight = BarcodeArRectangleHighlight(
            context,
            barcode,
        )
        callback.onData(highlight)
    }

    override fun annotationForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArAnnotationProvider.Callback
    ) {
        val annotation = BarcodeArStatusIconAnnotation(context, barcode)

        when (barcodeStatus[barcode.data]) {
            // CloseToExpiry icons are yellow with a black exclamation mark,
            // and display the text "Close to expiry" when tapped.
            BarcodeStatus.CloseToExpiry -> {
                annotation.text = "Close to expiry"
                annotation.icon = ScanditIcon.builder()
                    .withBackgroundShape(ScanditIconShape.CIRCLE)
                    .withBackgroundColor(yellowColor)
                    .withIcon(ScanditIconType.EXCLAMATION_MARK)
                    .withIconColor(Color.BLACK)
                    .build()
            }
            // Expired icons are red with a white exclamation mark,
            // and display the text "Item expired" when tapped.
            BarcodeStatus.Expired -> {
                annotation.text = "Item expired"
                annotation.icon = ScanditIcon.builder()
                    .withBackgroundShape(ScanditIconShape.CIRCLE)
                    .withBackgroundColor(redColor)
                    .withIcon(ScanditIconType.EXCLAMATION_MARK)
                    .withIconColor(Color.WHITE)
                    .build()
            }
            null -> error("Barcode with no data")
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

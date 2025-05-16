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

package com.scandit.datacapture.matrixscanarsimplesample.scan

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArAnnotationProvider
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArInfoAnnotation
import com.scandit.datacapture.barcode.ar.ui.annotations.info.BarcodeArInfoAnnotationBodyComponent
import com.scandit.datacapture.barcode.ar.ui.annotations.info.BarcodeArInfoAnnotationHeader
import com.scandit.datacapture.barcode.ar.ui.annotations.info.BarcodeArInfoAnnotationListener
import com.scandit.datacapture.barcode.ar.ui.annotations.info.BarcodeArInfoAnnotationWidthPreset
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArCircleHighlight
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArCircleHighlightPreset
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlightProvider
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.core.ui.style.Brush
import com.scandit.datacapture.matrixscanarsimplesample.scan.data.DiscountDataProvider

class ScanViewModel :
    ViewModel(),
    BarcodeArAnnotationProvider,
    BarcodeArHighlightProvider,
    BarcodeArInfoAnnotationListener {

    override fun annotationForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArAnnotationProvider.Callback
    ) {
        // Get discount data for the barcode
        val discount = DiscountDataProvider.getDataForBarcode(barcode)

        // Create and configure the header section of the annotation.
        val header = BarcodeArInfoAnnotationHeader()
        header.backgroundColor = discount.color
        header.text = discount.percentage

        // Create and configure the body section of the annotation.
        val bodyComponent = BarcodeArInfoAnnotationBodyComponent()
        bodyComponent.text = discount.getDisplayText(true)

        // Create the annotation itself and attach the header and body.
        val annotation = BarcodeArInfoAnnotation(context, barcode)
        annotation.header = header
        annotation.body = listOf(bodyComponent)
        annotation.width = BarcodeArInfoAnnotationWidthPreset.LARGE
        annotation.backgroundColor = Color.parseColor("#E6FFFFFF")

        // Set this ViewModel as the delegate for annotation to handle annotation taps.
        annotation.listener = this

        // Set this property to handle tap in any part of the annotation instead of individual parts.
        annotation.isEntireAnnotationTappable = true

        callback.onData(annotation)
    }

    override fun highlightForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArHighlightProvider.Callback
    ) {
        // Returns a circular dot highlight that will be displayed over each detected barcode.
        val highlight =
            BarcodeArCircleHighlight(context, barcode, BarcodeArCircleHighlightPreset.DOT)
        highlight.brush = Brush(Color.WHITE, Color.WHITE, 1f)
        callback.onData(highlight)
    }

    override fun onInfoAnnotationTapped(annotation: BarcodeArInfoAnnotation) {
        val discount = DiscountDataProvider.getDataForBarcode(annotation.barcode)
        annotation.body.first().text = discount.getDisplayText(false)
    }
}

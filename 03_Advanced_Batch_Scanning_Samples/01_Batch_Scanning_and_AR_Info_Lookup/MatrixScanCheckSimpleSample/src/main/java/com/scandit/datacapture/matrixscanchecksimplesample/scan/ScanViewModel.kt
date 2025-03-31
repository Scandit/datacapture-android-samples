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

package com.scandit.datacapture.matrixscanchecksimplesample.scan

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.scandit.datacapture.barcode.check.ui.annotations.BarcodeCheckAnnotationProvider
import com.scandit.datacapture.barcode.check.ui.annotations.BarcodeCheckInfoAnnotation
import com.scandit.datacapture.barcode.check.ui.annotations.info.BarcodeCheckInfoAnnotationBodyComponent
import com.scandit.datacapture.barcode.check.ui.annotations.info.BarcodeCheckInfoAnnotationHeader
import com.scandit.datacapture.barcode.check.ui.annotations.info.BarcodeCheckInfoAnnotationListener
import com.scandit.datacapture.barcode.check.ui.annotations.info.BarcodeCheckInfoAnnotationWidthPreset
import com.scandit.datacapture.barcode.check.ui.highlight.BarcodeCheckCircleHighlight
import com.scandit.datacapture.barcode.check.ui.highlight.BarcodeCheckCircleHighlightPreset
import com.scandit.datacapture.barcode.check.ui.highlight.BarcodeCheckHighlightProvider
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.core.ui.style.Brush
import com.scandit.datacapture.matrixscanchecksimplesample.scan.data.DiscountDataProvider

class ScanViewModel :
    ViewModel(),
    BarcodeCheckAnnotationProvider,
    BarcodeCheckHighlightProvider,
    BarcodeCheckInfoAnnotationListener {

    override fun annotationForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeCheckAnnotationProvider.Callback
    ) {
        // Get discount data for the barcode
        val discount = DiscountDataProvider.getDataForBarcode(barcode)

        // Create and configure the header section of the annotation.
        val header = BarcodeCheckInfoAnnotationHeader()
        header.backgroundColor = discount.color
        header.text = discount.percentage

        // Create and configure the body section of the annotation.
        val bodyComponent = BarcodeCheckInfoAnnotationBodyComponent()
        bodyComponent.text = discount.getDisplayText(true)

        // Create the annotation itself and attach the header and body.
        val annotation = BarcodeCheckInfoAnnotation(context, barcode)
        annotation.header = header
        annotation.body = listOf(bodyComponent)
        annotation.width = BarcodeCheckInfoAnnotationWidthPreset.LARGE
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
        callback: BarcodeCheckHighlightProvider.Callback
    ) {
        // Returns a circular dot highlight that will be displayed over each detected barcode.
        val highlight =
            BarcodeCheckCircleHighlight(context, barcode, BarcodeCheckCircleHighlightPreset.DOT)
        highlight.brush = Brush(Color.WHITE, Color.WHITE, 1f)
        callback.onData(highlight)
    }

    override fun onInfoAnnotationTapped(annotation: BarcodeCheckInfoAnnotation) {
        val discount = DiscountDataProvider.getDataForBarcode(annotation.barcode)
        annotation.body.first().text = discount.getDisplayText(false)
    }
}

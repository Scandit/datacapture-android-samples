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
import com.scandit.datacapture.barcode.ar.ui.BarcodeArViewDefaults
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArAnnotationProvider
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlight
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlightProvider
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArRectangleHighlight
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.core.internal.sdk.utils.pxFromDp
import com.scandit.datacapture.core.ui.icon.ScanditIcon
import com.scandit.datacapture.core.ui.icon.ScanditIconType
import com.scandit.datacapture.core.ui.style.Brush

class ScanHighlightsBehaviour : ScanBehaviour {

    private val highlights: MutableMap<String, BarcodeArRectangleHighlight> = mutableMapOf()
    private val selectedBarcodes: MutableList<String> = mutableListOf()

    private val unselectedFillColor = Color.parseColor("#6600FFFF")
    private val unselectedStrokeColor = Color.parseColor("#FF00FFFF")
    private val selectedFillColor = Color.parseColor("#660000FF")
    private val selectedStrokeColor = Color.parseColor("#FF0000FF")

    private val strokeWidth by lazy { 1.pxFromDp().toFloat() }

    override fun annotationForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArAnnotationProvider.Callback
    ) {
        // Not needed.
    }

    override fun highlightForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArHighlightProvider.Callback
    ) {
        // Return a rectangular highlight that will be displayed over each detected barcode.
        val highlight = getOrCreateHighlightForBarcode(context, barcode)
        barcode.data?.let { updateHighlight(highlight, it) }
        callback.onData(highlight)
    }

    // Fetch a highlight from the cache, or create it if it doesn't exist.
    private fun getOrCreateHighlightForBarcode(
        context: Context,
        barcode: Barcode,
    ): BarcodeArRectangleHighlight {
        val data = barcode.data
            ?: error("Attempted creating a highlight for a barcode with no data")

        return highlights[data]
            ?: BarcodeArRectangleHighlight(context, barcode).also { highlights[data] = it }
    }

    private fun updateHighlight(
        highlight: BarcodeArRectangleHighlight,
        barcode: String,
    ) {
        if (barcode.isSelected()) {
            // Set a brush and icon for a selected barcode's highlight.
            highlight.brush = Brush(selectedFillColor, selectedStrokeColor, strokeWidth)
            highlight.icon = ScanditIcon.builder()
                .withIcon(ScanditIconType.CHECKMARK)
                .withIconColor(Color.WHITE)
                .build()
        } else {
            // Set a brush and no icon for an unselected barcode's highlight.
            highlight.brush = Brush(unselectedFillColor, unselectedStrokeColor, strokeWidth)
            highlight.icon = null
        }
    }

    private fun String.isSelected(): Boolean = selectedBarcodes.contains(this)

    override fun onHighlightForBarcodeTapped(
        barcodeAr: BarcodeAr,
        barcode: Barcode,
        highlight: BarcodeArHighlight,
        highlightView: View,
    ) {
        toggleSelectedBarcode(barcode)
    }

    private fun toggleSelectedBarcode(barcode: Barcode) {
        barcode.data?.let {
            // Update whether the barcode is selected or not.
            val barcodeWasPresent = selectedBarcodes.remove(it)
            if (!barcodeWasPresent) {
                selectedBarcodes.add(it)
            }

            // Update the highlight.
            updateHighlight(highlights[barcode.data] as BarcodeArRectangleHighlight, it)
        }
    }
}

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
import android.widget.TextView
import com.scandit.datacapture.barcode.ar.capture.BarcodeAr
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArAnnotationProvider
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArInfoAnnotation
import com.scandit.datacapture.barcode.ar.ui.annotations.info.BarcodeArInfoAnnotationBodyComponent
import com.scandit.datacapture.barcode.ar.ui.annotations.info.BarcodeArInfoAnnotationFooter
import com.scandit.datacapture.barcode.ar.ui.annotations.info.BarcodeArInfoAnnotationHeader
import com.scandit.datacapture.barcode.ar.ui.annotations.info.BarcodeArInfoAnnotationListener
import com.scandit.datacapture.barcode.ar.ui.annotations.info.BarcodeArInfoAnnotationWidthPreset
import com.scandit.datacapture.barcode.ar.ui.annotations.responsive.BarcodeArResponsiveAnnotation
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArCircleHighlight
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArCircleHighlightPreset
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlight
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlightProvider
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.core.ui.icon.ScanditIcon
import com.scandit.datacapture.core.ui.icon.ScanditIconShape
import com.scandit.datacapture.core.ui.icon.ScanditIconType
import com.scandit.datacapture.core.ui.style.Brush

class ScanAnnotationsBehaviour : ScanBehaviour {

    private val closeUpAnnotationColor: MutableMap<String, Int> = mutableMapOf()

    private val highlightColor = Color.parseColor("#00FFFF")
    private val blueColor = Color.parseColor("#00FFFF")
    private val yellowColor = Color.parseColor("#F0BD30")

    override fun highlightForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArHighlightProvider.Callback
    ) {
        // Return a circular dot highlight that will be displayed over each detected barcode.
        val highlight = BarcodeArCircleHighlight(
            context,
            barcode,
            BarcodeArCircleHighlightPreset.DOT,
        )
        highlight.brush = Brush(highlightColor, highlightColor, 1f)
        callback.onData(highlight)
    }

    override fun annotationForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArAnnotationProvider.Callback
    ) {
        // Create the icon to use in the annotation.
        val annotationIcon = ScanditIcon.builder()
            .withBackgroundShape(ScanditIconShape.CIRCLE)
            .withBackgroundStrokeColor(Color.BLACK)
            .withIcon(ScanditIconType.CHECKMARK)
            .withIconColor(Color.BLACK)
            .build()

        // Create an info annotation to use when the barcode is far from the user,
        // with medium width.
        val faraway = BarcodeArInfoAnnotation(context, barcode)
        faraway.width = BarcodeArInfoAnnotationWidthPreset.MEDIUM

        // Add a simple body to the annotation.
        faraway.body = listOf(
            BarcodeArInfoAnnotationBodyComponent().apply {
                text = "Body text"
            }
        )

        // Create an info annotation to use when the barcode is close to the user,
        // with large width
        val closeup = BarcodeArInfoAnnotation(context, barcode)
        closeup.width = BarcodeArInfoAnnotationWidthPreset.LARGE

        // Add a header to the annotation.
        closeup.header = BarcodeArInfoAnnotationHeader().apply {
            text = "Header"
            icon = annotationIcon
        }

        // Add a footer to the annotation, with a simple text.
        closeup.footer = BarcodeArInfoAnnotationFooter().apply {
            text = "Tap to change color"
        }

        // Make the entire annotation tappable, and add a listener to update it.
        closeup.isEntireAnnotationTappable = true
        closeup.listener = object : BarcodeArInfoAnnotationListener {
            override fun onInfoAnnotationTapped(annotation: BarcodeArInfoAnnotation) {
                annotation.barcode.data?.let { data ->
                    toggleHeaderColor(data)
                    updateAnnotation(annotation, data)
                }
            }
        }

        // Add a body with several body components.
        closeup.body = listOf(
            BarcodeArInfoAnnotationBodyComponent().apply {
                text = "This is text in a large container.\nIt can have multiple lines"
                textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
            },
            BarcodeArInfoAnnotationBodyComponent().apply {
                text = "Point"
                textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                leftIcon = annotationIcon
            },
            BarcodeArInfoAnnotationBodyComponent().apply {
                text = "Point"
                textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                leftIcon = annotationIcon
            },
        )

        // Update the closeup annotation.
        barcode.data?.let { data -> updateAnnotation(closeup, data) }

        // Build the annotation and send it in the callback.
        val annotation = BarcodeArResponsiveAnnotation(context, barcode, closeup, faraway)
        callback.onData(annotation)
    }

    // Toggle the header color between blue and yellow.
    private fun toggleHeaderColor(data: String) {
        closeUpAnnotationColor[data] = when (closeUpAnnotationColor[data]) {
            blueColor -> yellowColor
            yellowColor -> blueColor
            else -> blueColor
        }
    }

    // Update the annotation header color.
    private fun updateAnnotation(annotation: BarcodeArInfoAnnotation, data: String) {
        if (!closeUpAnnotationColor.contains(data)) {
            closeUpAnnotationColor[data] = blueColor
        }
        annotation.header?.backgroundColor = closeUpAnnotationColor[data] ?: blueColor
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

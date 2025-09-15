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
import android.view.View
import androidx.lifecycle.ViewModel
import com.scandit.datacapture.barcode.ar.capture.BarcodeAr
import com.scandit.datacapture.barcode.ar.ui.BarcodeArViewUiListener
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArAnnotationProvider
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlight
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlightProvider
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.matrixscanarsimplesample.models.BarcodeArMode
import com.scandit.datacapture.matrixscanarsimplesample.scan.behaviours.ScanAnnotationsBehaviour
import com.scandit.datacapture.matrixscanarsimplesample.scan.behaviours.ScanBehaviour
import com.scandit.datacapture.matrixscanarsimplesample.scan.behaviours.ScanHighlightsBehaviour
import com.scandit.datacapture.matrixscanarsimplesample.scan.behaviours.ScanPopoversBehaviour
import com.scandit.datacapture.matrixscanarsimplesample.scan.behaviours.ScanStatusIconsBehaviour
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

// This ViewModel acts as the listener for several Barcode AR events,
// and propagates them to the selected mode behaviour.
class ScanViewModel :
    ViewModel(),
    BarcodeArHighlightProvider,
    BarcodeArAnnotationProvider,
    BarcodeArViewUiListener {

    private val resetViewChannel = Channel<Unit>()
    val resetView = resetViewChannel.receiveAsFlow()

    private lateinit var behaviour: ScanBehaviour

    var mode: BarcodeArMode = BarcodeArMode.Highlights
        set(value) {
            field = value
            behaviour = when (value) {
                BarcodeArMode.Highlights -> ScanHighlightsBehaviour()
                BarcodeArMode.Annotations -> ScanAnnotationsBehaviour()
                BarcodeArMode.Popovers -> ScanPopoversBehaviour { resetViewChannel.trySend(Unit) }
                BarcodeArMode.StatusIcons -> ScanStatusIconsBehaviour()
            }
        }

    override fun annotationForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArAnnotationProvider.Callback
    ) {
        behaviour.annotationForBarcode(context, barcode, callback)
    }

    override fun highlightForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArHighlightProvider.Callback
    ) {
        behaviour.highlightForBarcode(context, barcode, callback)
    }

    override fun onHighlightForBarcodeTapped(
        barcodeAr: BarcodeAr,
        barcode: Barcode,
        highlight: BarcodeArHighlight,
        highlightView: View
    ) {
        behaviour.onHighlightForBarcodeTapped(barcodeAr, barcode, highlight, highlightView)
    }
}

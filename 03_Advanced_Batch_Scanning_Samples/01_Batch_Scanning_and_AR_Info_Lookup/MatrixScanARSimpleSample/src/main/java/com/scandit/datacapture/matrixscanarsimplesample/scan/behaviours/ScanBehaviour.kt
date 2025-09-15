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
import android.view.View
import com.scandit.datacapture.barcode.ar.capture.BarcodeAr
import com.scandit.datacapture.barcode.ar.ui.annotations.BarcodeArAnnotationProvider
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlight
import com.scandit.datacapture.barcode.ar.ui.highlight.BarcodeArHighlightProvider
import com.scandit.datacapture.barcode.data.Barcode

interface ScanBehaviour {

    // Called when a highlight is requested for a barcode.
    fun highlightForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArHighlightProvider.Callback,
    )

    // Called when an annotation is requested for a barcode.
    fun annotationForBarcode(
        context: Context,
        barcode: Barcode,
        callback: BarcodeArAnnotationProvider.Callback,
    )

    // Called when a highlight is tapped by the user.
    fun onHighlightForBarcodeTapped(
        barcodeAr: BarcodeAr,
        barcode: Barcode,
        highlight: BarcodeArHighlight,
        highlightView: View,
    )
}

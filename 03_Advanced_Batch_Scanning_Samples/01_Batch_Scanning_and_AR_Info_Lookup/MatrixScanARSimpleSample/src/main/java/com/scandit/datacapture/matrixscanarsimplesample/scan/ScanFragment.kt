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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.scandit.datacapture.barcode.ar.capture.BarcodeAr
import com.scandit.datacapture.barcode.ar.ui.BarcodeArView
import com.scandit.datacapture.matrixscanarsimplesample.R
import com.scandit.datacapture.matrixscanarsimplesample.models.BarcodeArManager

/**
 * Fragment that will display the AR UI.
 * The [BarcodeArView] will be displayed here.
 */
class ScanFragment : Fragment() {
    private lateinit var viewModel: ScanViewModel

    private var barcodeArView: BarcodeArView? = null
    private var barcodeAr: BarcodeAr? = null
    private val barcodeArManager = BarcodeArManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_scan, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ar = barcodeArManager.createBarcodeAr()
        barcodeAr = ar
        barcodeArView = barcodeArManager.createBarcodeArView(view as ViewGroup, ar)

        // Set view model as the provider for barcode highlight and annotation styling.
        barcodeArView?.annotationProvider = viewModel
        barcodeArView?.highlightProvider = viewModel

        // Start the scanning flow.
        // This will be automatically paused and restored when onResume and onPause are called.
        barcodeArView?.start()
    }

    override fun onResume() {
        super.onResume()
        barcodeArView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeArView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        barcodeArView?.onDestroy()
        barcodeArView = null
        barcodeAr = null
    }

    companion object {
        fun newInstance(): ScanFragment {
            return ScanFragment()
        }
    }
}
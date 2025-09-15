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
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.scandit.datacapture.barcode.ar.capture.BarcodeAr
import com.scandit.datacapture.barcode.ar.ui.BarcodeArView
import com.scandit.datacapture.matrixscanarsimplesample.R
import com.scandit.datacapture.matrixscanarsimplesample.models.BarcodeArManager
import com.scandit.datacapture.matrixscanarsimplesample.models.BarcodeArMode
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Fragment that will display the AR UI.
 * The [BarcodeArView] will be displayed here.
 */
class ScanFragment : Fragment() {
    private lateinit var viewModel: ScanViewModel

    private var barcodeArView: BarcodeArView? = null
    private var barcodeAr: BarcodeAr? = null
    private val barcodeArManager = BarcodeArManager()

    @StringRes
    private var toolbarTitleRes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read the selected scan mode from arguments and pass it to the ViewModel for setup.
        var mode: BarcodeArMode = BarcodeArMode.Highlights
        arguments?.let {
            mode = BarcodeArMode.valueOf(it.getString(ARGS_MODE, BarcodeArMode.Highlights.name))
        }

        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java).also {
            it.mode = mode
            toolbarTitleRes = mode.title()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_scan, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.toolbar).setText(toolbarTitleRes)

        // Set up BarcodeArView in its intended container
        val arViewContainer = view.findViewById<ViewGroup>(R.id.container)
        barcodeArManager.createBarcodeAr().also {
            barcodeAr = it
            barcodeArView = barcodeArManager.createBarcodeArView(arViewContainer, it)
        }

        view.findViewById<View>(R.id.button_return).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Set view model as the provider for barcode highlight and annotation styling.
        barcodeArView?.annotationProvider = viewModel
        barcodeArView?.highlightProvider = viewModel
        barcodeArView?.uiListener = viewModel

        // Listen to events to reset the AR view.
        viewModel.resetView
            .onEach { barcodeArView?.reset() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        // Start the scanning flow.
        // This will be automatically paused and restored when onResume and onPause are called.
        barcodeArView?.start()
    }

    @StringRes
    private fun BarcodeArMode.title(): Int = when (this) {
        BarcodeArMode.Highlights -> R.string.menu_highlights_title
        BarcodeArMode.Annotations -> R.string.menu_annotations_title
        BarcodeArMode.Popovers -> R.string.menu_popovers_title
        BarcodeArMode.StatusIcons -> R.string.menu_status_icons_title
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
        private const val ARGS_MODE = "args_mode"

        fun newInstance(mode: BarcodeArMode): ScanFragment {
            return ScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGS_MODE, mode.name)
                }
            }
        }
    }
}

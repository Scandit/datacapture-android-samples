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

package com.scandit.datacapture.inventoryauditsample.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingAdvancedOverlay
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.core.ui.DataCaptureView.Companion.newInstance
import com.scandit.datacapture.inventoryauditsample.R
import com.scandit.datacapture.inventoryauditsample.scan.bubble.Bubble
import com.scandit.datacapture.inventoryauditsample.scan.bubble.data.BubbleData

class ScanFragment : CameraPermissionFragment(), ScanViewModel.ScanViewModelListener {
    private lateinit var viewModel: ScanViewModel
    private lateinit var dataCaptureView: DataCaptureView
    private lateinit var bubblesOverlay: BarcodeTrackingAdvancedOverlay
    private lateinit var freezeButton: ImageButton

    // We reuse bubble views where possible.
    private var bubbles: MutableMap<Int, Bubble> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val layout: View = inflater.inflate(R.layout.fragment_scan, container, false)

        // To visualize the on-going barcode capturing process on screen,
        // setup a data capture view that renders the camera preview.
        // The view must be connected to the data capture context.
        dataCaptureView = newInstance(requireContext(), viewModel.dataCaptureContext)

        // We create an overlay for the bubbles.
        bubblesOverlay = BarcodeTrackingAdvancedOverlay.newInstance(
                viewModel.barcodeTracking, dataCaptureView
        )
        bubblesOverlay.listener = viewModel

        // We add the data capture view to the root layout.
        (layout.findViewById<View>(R.id.root) as ViewGroup).addView(
                dataCaptureView,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        freezeButton = view.findViewById(R.id.button_freeze)
        freezeButton.setOnClickListener { viewModel.toggleFreeze() }
        onFrozenChanged(viewModel.isFrozen())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataCaptureView.removeOverlay(bubblesOverlay)
        bubblesOverlay.listener = null
    }

    override fun onResume() {
        super.onResume()

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission()
    }

    override fun onCameraPermissionGranted() {
        resumeFrameSource()
    }

    private fun resumeFrameSource() {
        viewModel.setListener(this)
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        viewModel.startFrameSource()
        viewModel.resumeScanning()
    }

    override fun onPause() {
        pauseFrameSource()
        super.onPause()
    }

    private fun pauseFrameSource() {
        viewModel.setListener(null)
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
        viewModel.pauseScanning()
        viewModel.stopFrameSource()
    }

    override fun getOrCreateViewForBubbleData(
            barcode: TrackedBarcode,
            bubbleData: BubbleData
    ): View {
        val identifier = barcode.identifier
        var bubble = bubbles[identifier]
        if (bubble == null) {
            // There's no recyclable bubble for this tracking identifier, so we create one.
            val code = barcode.barcode.data ?: ""
            bubble = Bubble(requireContext(), bubbleData, code)

            // We store the newly created bubble to recycle it in subsequent frames.
            bubbles[identifier] = bubble
        }
        return bubble.root
    }

    override fun removeBubbleView(identifier: Int) {
        // When a barcode is not tracked anymore, we can remove the bubble from our list.
        bubbles.remove(identifier)
    }

    override fun onFrozenChanged(frozen: Boolean) {
        freezeButton.setImageResource(
                if (frozen) R.drawable.freeze_disabled else R.drawable.freeze_enabled
        )
    }

    companion object {
        fun newInstance(): ScanFragment {
            return ScanFragment()
        }
    }
}
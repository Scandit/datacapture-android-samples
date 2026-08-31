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
package com.scandit.datacapture.pricecapturesample

import android.os.Bundle
import android.view.ViewGroup
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.label.ui.overlay.LabelCaptureAdvancedOverlay
import com.scandit.datacapture.label.ui.overlay.LabelCaptureBasicOverlay
import com.scandit.datacapture.pricecapturesample.data.BarcodePriceDatabase
import com.scandit.datacapture.pricecapturesample.databinding.ActivityMainBinding
import com.scandit.datacapture.pricecapturesample.scan.DataCaptureManager
import com.scandit.datacapture.pricecapturesample.scan.PriceLabelOverlayHandler

/**
 * Single-screen price-capture sample: a full-screen camera preview that captures the product
 * barcode and printed price from a shelf price label in one shot, validates the pair against a
 * bundled CSV, and highlights the price field with the validation color plus a centered icon.
 */
class MainActivity : CameraPermissionActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataCaptureManager: DataCaptureManager
    private lateinit var overlayHandler: PriceLabelOverlayHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = BarcodePriceDatabase.fromAsset(this, ASSET_DATABASE)
        dataCaptureManager = DataCaptureManager(Settings.LICENSE_KEY)
        overlayHandler = PriceLabelOverlayHandler(this, database)
        dataCaptureManager.labelCapture.addListener(overlayHandler)

        val dataCaptureView =
            DataCaptureView.newInstance(this, dataCaptureManager.dataCaptureContext)

        LabelCaptureBasicOverlay.newInstance(dataCaptureManager.labelCapture, dataCaptureView)
            .listener = overlayHandler

        // The handler drives the advanced overlay imperatively (see PriceLabelOverlayHandler).
        overlayHandler.advancedOverlay =
            LabelCaptureAdvancedOverlay.newInstance(dataCaptureManager.labelCapture, dataCaptureView)

        binding.dataCaptureViewContainer.addView(
            dataCaptureView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }

    override fun onResume() {
        super.onResume()
        requestCameraPermission()
    }

    override fun onCameraPermissionGranted() {
        dataCaptureManager.camera?.switchToDesiredState(FrameSourceState.ON)
        dataCaptureManager.labelCapture.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        dataCaptureManager.camera?.switchToDesiredState(FrameSourceState.OFF)
    }

    companion object {
        private const val ASSET_DATABASE = "barcode_price_database.csv"
    }
}

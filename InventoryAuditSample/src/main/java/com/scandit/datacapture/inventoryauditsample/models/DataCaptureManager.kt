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

package com.scandit.datacapture.inventoryauditsample.models

import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking.Companion.forDataCaptureContext
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingScenario.A
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSettings
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.capture.DataCaptureContext.Companion.forLicenseKey
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.Camera.Companion.getDefaultCamera
import com.scandit.datacapture.core.source.VideoResolution

object DataCaptureManager {
    private const val SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --"

    val barcodeTracking: BarcodeTracking
    val dataCaptureContext: DataCaptureContext
    val camera: Camera

    init {
        // The barcode tracking process is configured through barcode tracking settings
        // which are then applied to the barcode tracking instance that manages barcode recognition and tracking.
        val barcodeTrackingSettings = BarcodeTrackingSettings.forScenario(A).apply {
            // The settings instance initially has all types of barcodes (symbologies) disabled.
            // For the purpose of this sample we enable a generous set of symbologies.
            // In your own app ensure that you only enable the symbologies that your app requires
            // as every additional enabled symbology has an impact on processing times.
            enableSymbology(Symbology.EAN13_UPCA, true)
            enableSymbology(Symbology.EAN8, true)
            enableSymbology(Symbology.UPCE, true)
        }

        val cameraSettings = BarcodeTracking.createRecommendedCameraSettings().apply {
            preferredResolution = VideoResolution.UHD4K
        }

        camera = getDefaultCamera(cameraSettings) ?: throw IllegalStateException(
                "Sample depends on a camera, which failed to initialize.")

        // Create data capture context using your license key and set the camera as the frame source.
        dataCaptureContext = forLicenseKey(SCANDIT_LICENSE_KEY).apply {
            setFrameSource(camera)
        }

        // Create new barcode tracking mode with the settings from above.
        barcodeTracking = forDataCaptureContext(dataCaptureContext, barcodeTrackingSettings)
    }
}
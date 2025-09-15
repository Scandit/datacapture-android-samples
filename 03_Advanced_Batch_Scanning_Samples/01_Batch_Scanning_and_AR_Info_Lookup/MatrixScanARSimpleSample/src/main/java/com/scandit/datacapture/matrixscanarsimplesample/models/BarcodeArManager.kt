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

package com.scandit.datacapture.matrixscanarsimplesample.models

import android.view.ViewGroup
import com.scandit.datacapture.barcode.ar.capture.BarcodeAr
import com.scandit.datacapture.barcode.ar.capture.BarcodeArSettings
import com.scandit.datacapture.barcode.ar.ui.BarcodeArView
import com.scandit.datacapture.barcode.ar.ui.BarcodeArViewSettings
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.source.VideoResolution
import com.scandit.datacapture.matrixscanarsimplesample.BuildConfig

class BarcodeArManager {
    // Create data capture context using your license key.
    private val dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY)

    fun createBarcodeAr(): BarcodeAr {
        // The barcode ar process is configured through barcode ar settings
        // which are then applied to the barcode ar instance that manages barcode recognition and tracking.
        val barcodeArSettings = BarcodeArSettings().apply {
            // The settings instance initially has all types of barcodes (symbologies) disabled.
            // For the purpose of this sample we enable a generous set of symbologies.
            // In your own app ensure that you only enable the symbologies that your app requires
            // as every additional enabled symbology has an impact on processing times.
            enableSymbology(Symbology.EAN13_UPCA, true)
            enableSymbology(Symbology.EAN8, true)
            enableSymbology(Symbology.UPCE, true)
            enableSymbology(Symbology.CODE39, true)
            enableSymbology(Symbology.CODE128, true)
            enableSymbology(Symbology.QR, true)
            enableSymbology(Symbology.DATA_MATRIX, true)
        }

        // Create new barcode ar mode with the settings from above.
        return BarcodeAr(dataCaptureContext, barcodeArSettings)
    }

    fun createBarcodeArView(
        parent: ViewGroup,
        barcodeAr: BarcodeAr,
    ): BarcodeArView {
        // Create and configure BarcodeArView with default view settings.
        val barcodeArViewSettings = BarcodeArViewSettings()

        // Use the recommended camera settings, preferring 4K resolution.
        val cameraSettings = BarcodeAr.createRecommendedCameraSettings().apply {
            preferredResolution = VideoResolution.UHD4K
        }

        return BarcodeArView(
            parent,
            barcodeAr,
            dataCaptureContext,
            barcodeArViewSettings,
            cameraSettings
        )
    }

    companion object {
        // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
        // `BuildConfig.SCANDIT_LICENSE_KEY`
        private const val SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY
    }
}

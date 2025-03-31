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

package com.scandit.datacapture.matrixscanchecksimplesample.models

import android.view.ViewGroup
import com.scandit.datacapture.barcode.check.capture.BarcodeCheck
import com.scandit.datacapture.barcode.check.capture.BarcodeCheckSettings
import com.scandit.datacapture.barcode.check.ui.BarcodeCheckView
import com.scandit.datacapture.barcode.check.ui.BarcodeCheckViewSettings
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.matrixscanchecksimplesample.BuildConfig

class BarcodeCheckManager {
    // Create data capture context using your license key.
    private val dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY)

    fun createBarcodeCheck(): BarcodeCheck {
        // The barcode check process is configured through barcode check settings
        // which are then applied to the barcode check instance that manages barcode recognition and tracking.
        val barcodeCheckSettings = BarcodeCheckSettings().apply {
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

        // Create new barcode check mode with the settings from above.
        return BarcodeCheck(dataCaptureContext, barcodeCheckSettings)
    }

    fun createBarcodeCheckView(parent: ViewGroup, barcodeCheck: BarcodeCheck): BarcodeCheckView {
        // Create and configure BarcodeCheckView with default view settings.
        val barcodeCheckViewSettings = BarcodeCheckViewSettings()

        // Use the recommended camera settings.
        val cameraSettings = BarcodeCheck.createRecommendedCameraSettings()

        return BarcodeCheckView(
            parent,
            barcodeCheck,
            dataCaptureContext,
            barcodeCheckViewSettings,
            cameraSettings
        )
    }

    companion object {
        // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
        // `BuildConfig.SCANDIT_LICENSE_KEY`
        private const val SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY
    }
}

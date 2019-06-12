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

package com.scandit.datacapture.barcodecaptureviewssample.models;

import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;

public class SettingsManager {

    public static final SettingsManager CURRENT = new SettingsManager();

    public static final String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    public final BarcodeCapture barcodeCapture;
    public final DataCaptureContext dataCaptureContext;
    public final Camera camera = Camera.getDefaultCamera();

    private SettingsManager() {
        // The barcode capturing process is configured through barcode capture settings
        // which are then applied to the barcode capture instance that manages barcode recognition.
        BarcodeCaptureSettings barcodeCaptureSettings = new BarcodeCaptureSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        barcodeCaptureSettings.enableSymbology(Symbology.EAN13_UPCA, true);
        barcodeCaptureSettings.enableSymbology(Symbology.EAN8, true);
        barcodeCaptureSettings.enableSymbology(Symbology.UPCE, true);
        barcodeCaptureSettings.enableSymbology(Symbology.QR, true);
        barcodeCaptureSettings.enableSymbology(Symbology.DATA_MATRIX, true);
        barcodeCaptureSettings.enableSymbology(Symbology.CODE39, true);
        barcodeCaptureSettings.enableSymbology(Symbology.CODE128, true);
        barcodeCaptureSettings.enableSymbology(Symbology.INTERLEAVED_TWO_OF_FIVE, true);

        // Create data capture context using your license key and set the camera as the frame source.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);
        dataCaptureContext.setFrameSource(camera);

        // Create new barcode capture mode with the settings from above.
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, barcodeCaptureSettings);
    }
}

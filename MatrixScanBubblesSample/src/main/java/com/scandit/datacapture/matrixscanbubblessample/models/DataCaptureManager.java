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

package com.scandit.datacapture.matrixscanbubblessample.models;


import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.batch.capture.BarcodeBatch;
import com.scandit.datacapture.barcode.batch.capture.BarcodeBatchSettings;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.VideoResolution;
import com.scandit.datacapture.matrixscanbubblessample.BuildConfig;

public final class DataCaptureManager {

    // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
    // `BuildConfig.SCANDIT_LICENSE_KEY`
    public static final String SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY;
    public static final DataCaptureManager CURRENT = new DataCaptureManager();

    public final BarcodeBatch barcodeBatch;
    public final DataCaptureContext dataCaptureContext;
    public final Camera camera;

    private DataCaptureManager() {
        // The barcode tracking process is configured through barcode batch settings
        // which are then applied to the barcode batch instance that manages barcode recognition
        // and tracking.
        BarcodeBatchSettings barcodeBatchSettings = new BarcodeBatchSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        barcodeBatchSettings.enableSymbology(Symbology.EAN13_UPCA, true);
        barcodeBatchSettings.enableSymbology(Symbology.EAN8, true);
        barcodeBatchSettings.enableSymbology(Symbology.UPCE, true);
        barcodeBatchSettings.enableSymbology(Symbology.CODE39, true);
        barcodeBatchSettings.enableSymbology(Symbology.CODE128, true);

        CameraSettings cameraSettings = BarcodeBatch.createRecommendedCameraSettings();
        cameraSettings.setPreferredResolution(VideoResolution.UHD4K);
        camera = Camera.getDefaultCamera(cameraSettings);

        // Create data capture context using your license key and set the camera as the frame source.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);
        dataCaptureContext.setFrameSource(camera);

        // Create new barcode batch mode with the settings from above.
        barcodeBatch = BarcodeBatch.forDataCaptureContext(dataCaptureContext, barcodeBatchSettings);
    }
}

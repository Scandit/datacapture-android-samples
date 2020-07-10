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

package com.scandit.datacapture.barcodecaptureshelfmanagementsinglesample;

import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.core.area.RectangularLocationSelection;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.common.geometry.SizeWithUnit;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.source.VideoResolution;

public class DataCaptureManager {
    public static final String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private static final DataCaptureManager INSTANCE = new DataCaptureManager();

    public static DataCaptureManager getInstance() {
        return INSTANCE;
    }

    private DataCaptureContext dataCaptureContext;
    private CameraSettings cameraSettings;
    private Camera camera;
    private BarcodeCapture barcodeCapture;

    public DataCaptureManager() {
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        cameraSettings = BarcodeCapture.createRecommendedCameraSettings();
        cameraSettings.setPreferredResolution(VideoResolution.UHD4K);
        cameraSettings.setProperty("api", 2);
        cameraSettings.setProperty("manualLensPosition", 0.8f);
        cameraSettings.setProperty("disableManualLensPositionSupportCheck", true);
        cameraSettings.setProperty("disableRetriggerAndContinuous", true);

        camera = Camera.getDefaultCamera(cameraSettings);
        dataCaptureContext.setFrameSource(camera);

        BarcodeCaptureSettings barcodeCaptureSettings = new BarcodeCaptureSettings();
        barcodeCaptureSettings.enableSymbology(Symbology.EAN13_UPCA, true);
        barcodeCaptureSettings.setLocationSelection(
                RectangularLocationSelection.withSize(
                        new SizeWithUnit(
                                new FloatWithUnit(0.8f, MeasureUnit.FRACTION),
                                new FloatWithUnit(0.2f, MeasureUnit.FRACTION)
                        )
                )
        );

        barcodeCapture =
                BarcodeCapture.forDataCaptureContext(dataCaptureContext, barcodeCaptureSettings);
    }

    public DataCaptureContext dataCaptureContext() {
        return dataCaptureContext;
    }

    public BarcodeCapture barcodeCapture() {
        return barcodeCapture;
    }

    public void turnOnCamera() {
        camera.switchToDesiredState(FrameSourceState.ON);
    }

    public void turnOffCamera() {
        camera.switchToDesiredState(FrameSourceState.OFF);
    }

    public void resetLensPosition() {
        cameraSettings.setProperty("manualLensPosition", 0.8f);
        camera.applySettings(cameraSettings);
    }

    public void triggerAutoFocus() {
        cameraSettings.setProperty("manualLensPosition", -1);
        cameraSettings.setProperty("triggerAf", true);
        camera.applySettings(cameraSettings);
        cameraSettings.setProperty("triggerAf", false);
    }

    public void zoom(float factor) {
        cameraSettings.setZoomFactor(factor);
        camera.applySettings(cameraSettings);
    }
}

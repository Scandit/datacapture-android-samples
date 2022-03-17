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

package com.scandit.datacapture.reorderfromcatalogsample.data;

import com.scandit.datacapture.barcode.selection.capture.BarcodeSelection;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.source.VideoResolution;

/**
 * The repository to interact with the device's camera.
 */
public class CameraRepository {
    /**
     * The wrapper over the device's camera.
     */
    private final Camera camera;

    public CameraRepository(DataCaptureContext dataCaptureContext) {
        /*
         * Set the device's default camera as DataCaptureContext's FrameSource with the recommended
         * settings for the desired mode. DataCaptureContext passes the frames from its FrameSource
         * to the added modes to perform capture or tracking.
         */
        CameraSettings settings = BarcodeSelection.createRecommendedCameraSettings();
        settings.setPreferredResolution(VideoResolution.FULL_HD);

        camera = Camera.getDefaultCamera(settings);
        dataCaptureContext.setFrameSource(camera);
    }

    public void turnOnCamera() {
        /*
         * Switch the camera on. The camera frames will be sent to the modes connected to the
         * DataCaptureContext for processing.
         * Additionally the preview will appear on the screen. The camera is started asynchronously,
         * and you may notice a small delay before the preview appears.
         */
        camera.switchToDesiredState(FrameSourceState.ON);
    }

    public void putCameraInStandby() {
        /*
         * Put the camera in standby mode to stop streaming frames, but allowing for a faster
         * restart compared to switching it completely off.
         * The camera is stopped asynchronously.
         * Check https://docs.scandit.com/data-capture-sdk/android/camera-advanced.html#camera-advanced-standby
         * for more info.
         */
        camera.switchToDesiredState(FrameSourceState.STANDBY);
    }
}

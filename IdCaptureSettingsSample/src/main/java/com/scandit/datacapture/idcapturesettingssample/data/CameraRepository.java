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

package com.scandit.datacapture.idcapturesettingssample.data;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.source.TorchListener;
import com.scandit.datacapture.core.source.TorchState;
import com.scandit.datacapture.id.capture.IdCapture;

/**
 * The repository to interact with the device's camera.
 */
public class CameraRepository {

    /**
     * The wrapper over the device's camera.
     */
    private Camera camera;

    /**
     * Repository containing the desired settings for IdCapture.
     */
    private final SettingsRepository settingsRepository;

    /**
     * The DataCaptureContext that the current IdCapture is attached to.
     */
    private final DataCaptureContext dataCaptureContext;

    /**
     * The listener that is invoked when the torch state changes.
     */
    private TorchListener torchListener;

    public CameraRepository(
            DataCaptureContext dataCaptureContext,
            SettingsRepository settingsRepository
    ) {
        this.settingsRepository = settingsRepository;
        this.dataCaptureContext = dataCaptureContext;
    }

    public void turnOnCamera() {
        /*
         * Switch the camera on. The camera frames will be sent to IdCapture for processing.
         * Additionally the preview will appear on the screen. The camera is started asynchronously,
         * and you may notice a small delay before the preview appears.
         */
        camera.switchToDesiredState(FrameSourceState.ON);
    }

    public void turnOffCamera() {
        /*
         * Switch the camera off to stop streaming frames. The camera is stopped asynchronously.
         */
        camera.switchToDesiredState(FrameSourceState.OFF);
    }

    /**
     * Request a new Camera to be built with the current settings.
     */
    public void refreshCamera() {

        CameraSettings cameraSettings = IdCapture.createRecommendedCameraSettings();

        /*
         * Apply the preferred resolution from the settings.
         */
        cameraSettings.setPreferredResolution(settingsRepository.getPreferredResolution());

        /*
         * Clear the torch listener if necessary.
         */
        if (camera != null && torchListener != null) {
            camera.removeTorchListener(torchListener);
            torchListener = null;
        }

        /*
         * Set the device's default camera as DataCaptureContext's FrameSource. DataCaptureContext
         * passes the frames from it's FrameSource to the added modes to perform capture or
         * tracking.
         */
        camera = Camera.getCamera(settingsRepository.getCameraPosition(), cameraSettings);

        /*
         * Synchronize the torch state and the activation status of the torch in the settings.
         */
        torchListener = state -> {
            if (state == TorchState.AUTO) return;
            settingsRepository.setTorchState(state == TorchState.ON);
        };

        camera.addTorchListener(torchListener);

        /*
         * Apply the desired torch state from the settings.
         */
        camera.setDesiredTorchState(settingsRepository.getTorchState());

        /*
         * Set the camera as the DataCaptureContext's frame source.
         */
        dataCaptureContext.setFrameSource(camera);
    }
}

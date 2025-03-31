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

package com.scandit.datacapture.receivingsample.managers;

import com.scandit.datacapture.barcode.count.capture.BarcodeCount;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.source.TorchState;

// Singleton object that centralises Camera management.
public class BarcodeCountCameraManager {

    // Instance in singleton should only be available through getInstance() method.
    private static BarcodeCountCameraManager sharedInstance = null;

    // This method provides access to the shared BarcodeCountCameraManager instance.
    public static BarcodeCountCameraManager getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new BarcodeCountCameraManager();
        }
        return sharedInstance;
    }

    private Camera camera;

    // The constructor is private, so only this class can instantiate itself.
    private BarcodeCountCameraManager() {
    }

    public void initialize(DataCaptureContext dataCaptureContext) {
        // Use the recommended camera settings for the BarcodeCount mode.
        CameraSettings cameraSettings = BarcodeCount.createRecommendedCameraSettings();
        // Use the default camera and set it as the frame source of the context.
        // The camera is off by default and must be turned on to start streaming frames to the data
        // capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
        camera = Camera.getDefaultCamera(cameraSettings);
        if (camera != null) {
            dataCaptureContext.setFrameSource(camera);
        } else {
            throw new IllegalStateException("Sample depends on a camera, which failed to initialize.");
        }
    }

    public void resetDefaultSettings() {
        if (camera != null) {
            camera.applySettings(BarcodeCount.createRecommendedCameraSettings());
            camera.setDesiredTorchState(TorchState.OFF);
        }
    }

    public void stopFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        camera.switchToDesiredState(FrameSourceState.OFF);
    }

    public void resumeFrameSource() {
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        camera.switchToDesiredState(FrameSourceState.ON, null);
    }
}

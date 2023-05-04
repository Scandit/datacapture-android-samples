package com.scandit.datacapture.matrixscancountsimplesample;

import com.scandit.datacapture.barcode.count.capture.BarcodeCount;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;

// Singleton object that centralises Camera management.
public class CameraManager {

    // Shared instance in singleton should only be available through getInstance() method.
    private static CameraManager sharedInstance = null;

    // This method provides access to the shared CameraManager instance.
    public static CameraManager getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new CameraManager();
        }
        return sharedInstance;
    }

    private Camera camera;

    // The constructor is private, so only this class can instantiate itself.
    private CameraManager() {
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

    public void pauseFrameSource() {
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

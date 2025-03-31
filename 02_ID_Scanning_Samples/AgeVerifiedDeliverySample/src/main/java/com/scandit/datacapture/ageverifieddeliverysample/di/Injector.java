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

package com.scandit.datacapture.ageverifieddeliverysample.di;

import com.scandit.datacapture.ageverifieddeliverysample.data.BarcodeCaptureProvider;
import com.scandit.datacapture.ageverifieddeliverysample.data.CameraRepository;
import com.scandit.datacapture.ageverifieddeliverysample.data.DataCaptureContextProvider;
import com.scandit.datacapture.ageverifieddeliverysample.data.IdCaptureProvider;
import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureFeedback;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

/**
 * A simple dependency injector. It may be replaced with a one from your favorite DI framework
 * like Dagger or Hilt.
 */
public class Injector {
    /**
     * The singleton instance of this dependency injector.
     */
    private static final Injector INSTANCE = new Injector();

    /**
     * Get the singleton instance of this dependency injector.
     */
    public static Injector getInstance() {
        return INSTANCE;
    }

    /**
     * The object that provides the current DataCaptureContext.
     */
    private final DataCaptureContextProvider dataCaptureContextProvider =
            new DataCaptureContextProvider();

    /**
     * The repository that allows interaction with the device's camera during ID capture.
     */
    private final CameraRepository idCameraRepository =
            new CameraRepository(dataCaptureContextProvider.getDataCaptureContext(),
                    IdCapture.createRecommendedCameraSettings());

    /**
     * The repository that allows interaction with the IdCapture mode.
     */
    private final IdCaptureProvider idCaptureProvider = new IdCaptureProvider();

    /**
     * The repository that allows interaction with the device's camera during barcode capture.
     */
    private final CameraRepository barcodeCameraRepository =
            new CameraRepository(dataCaptureContextProvider.getDataCaptureContext(),
                    BarcodeCapture.createRecommendedCameraSettings());

    /**
     * The repository that allows interaction with the BarcodeCapture mode.
     */
    private final BarcodeCaptureProvider barcodeCaptureProvider = new BarcodeCaptureProvider();

    private Injector() {
        // Use `getInstance()`
    }

    /**
     * Get the current DataCaptureContext.
     */
    public DataCaptureContext getDataCaptureContext() {
        return dataCaptureContextProvider.getDataCaptureContext();
    }

    /**
     * Get the current IdCapture.
     */
    public IdCapture getIdCapture() {
        return idCaptureProvider.getIdCapture();
    }

    /**
     * Get the current BarcodeCapture.
     */
    public BarcodeCapture getBarcodeCapture() {
        return barcodeCaptureProvider.getBarcodeCapture();
    }

    /**
     * IdCaptureOverlay displays the additional UI to guide the user through the ID capture process.
     */
    public IdCaptureOverlay getIdCaptureOverlay() {
        return idCaptureProvider.getOverlay();
    }

    /**
     * Get the ID capture Feedback.
     */
    public IdCaptureFeedback getIdCaptureFeedback() {
        return idCaptureProvider.getIdCaptureFeedback();
    }

    /**
     * BarcodeCaptureOverlay displays the additional UI to guide the user through the barcode
     * capture process.
     */
    public BarcodeCaptureOverlay getBarcodeCaptureOverlay() {
        return barcodeCaptureProvider.getOverlay();
    }

    /**
     * Get the repository that allows interaction with the device's camera during ID capture.
     */
    public CameraRepository getIdCameraRepository() {
        return idCameraRepository;
    }

    /**
     * Get the repository that allows interaction with the device's camera during barcode capture.
     */
    public CameraRepository getBarcodeCameraRepository() {
        return barcodeCameraRepository;
    }
}

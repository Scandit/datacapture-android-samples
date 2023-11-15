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

package com.scandit.datacapture.idcapturesimplesample;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.data.IdDocumentType;

/*
 * Initializes DataCapture.
 */
public final class DataCaptureManager {
	// Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    @VisibleForTesting
    public static String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private static DataCaptureManager INSTANCE;

    private DataCaptureContext dataCaptureContext;
    private Camera camera;
    private IdCapture idCapture;

    public static DataCaptureManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataCaptureManager(context.getApplicationContext());
        }

        return INSTANCE;
    }

    private DataCaptureManager(Context context) {
        /*
         * Create DataCaptureContext using your license key.
         */
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        initCamera();
        initIdCapture();
    }

    private void initCamera() {
        /*
         * Set the device's default camera as DataCaptureContext's FrameSource. DataCaptureContext
         * passes the frames from it's FrameSource to the added modes to perform capture or
         * tracking.
         *
         * Since we are going to perform TextCapture in this sample, we initiate the camera with
         * the recommended settings for this mode.
         */
        camera = Camera.getDefaultCamera(IdCapture.createRecommendedCameraSettings());

        if (camera == null) {
            throw new IllegalStateException("Failed to init camera!");
        }

        dataCaptureContext.setFrameSource(camera);
    }

    private void initIdCapture() {
        /*
         * Create a mode responsible for recognizing documents. This mode is automatically added
         * to the passed DataCaptureContext.
         */
        IdCaptureSettings settings = new IdCaptureSettings();

        // Recognize national ID cards & driver's licenses.
        settings.setSupportedDocuments(
                IdDocumentType.ID_CARD_VIZ,
                IdDocumentType.DL_VIZ
        );

        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, settings);
    }

    public DataCaptureContext getDataCaptureContext() {
        return dataCaptureContext;
    }

    public Camera getCamera() {
        return camera;
    }

    public IdCapture getIdCapture() {
        return idCapture;
    }
}

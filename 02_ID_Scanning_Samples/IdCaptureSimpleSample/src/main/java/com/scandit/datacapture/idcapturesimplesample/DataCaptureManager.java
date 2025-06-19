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

import androidx.annotation.VisibleForTesting;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.id.capture.DriverLicense;
import com.scandit.datacapture.id.capture.FullDocumentScanner;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureDocument;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.capture.IdCard;
import com.scandit.datacapture.id.capture.Passport;
import com.scandit.datacapture.id.data.IdCaptureRegion;

import java.util.Arrays;
import java.util.List;

/*
 * Initializes DataCapture.
 */
public final class DataCaptureManager {

    // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
    // `BuildConfig.SCANDIT_LICENSE_KEY`
    @VisibleForTesting
    public static String SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY;

    private static DataCaptureManager INSTANCE;

    private static final List<IdCaptureDocument> ACCEPTED_DOCUMENTS = Arrays.asList(
            new IdCard(IdCaptureRegion.ANY),
            new DriverLicense(IdCaptureRegion.ANY),
            new Passport(IdCaptureRegion.ANY)
    );

    private final DataCaptureContext dataCaptureContext;
    private Camera camera;
    private IdCapture idCapture;

    public static DataCaptureManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataCaptureManager();
        }

        return INSTANCE;
    }

    private DataCaptureManager() {
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

        // Recognize national ID cards, driver's licenses and passports.
        settings.setAcceptedDocuments(ACCEPTED_DOCUMENTS);
        settings.setScannerType(new FullDocumentScanner());

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

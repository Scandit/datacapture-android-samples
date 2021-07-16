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

package com.scandit.datacapture.idcaptureextendedsample;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.data.IdDocumentType;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.SupportedSides;

/*
 * Initializes DataCapture.
 */
public final class DataCaptureManager {
    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    public static final String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private static DataCaptureManager INSTANCE;

    private final DataCaptureContext dataCaptureContext;
    private final Camera camera;
    private IdCapture idCapture;

    // Keep a singleton reference of the DataCaptureContext and Camera.
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

        /*
         * Set the device's default camera as DataCaptureContext's FrameSource. DataCaptureContext
         * passes the frames from it's FrameSource to the added modes to perform capture or
         * tracking.
         *
         * Since we are going to perform IdCapture in this sample, we initiate the camera with
         * the recommended settings for this mode.
         */
        camera = Camera.getDefaultCamera(IdCapture.createRecommendedCameraSettings());

        if (camera == null) {
            throw new IllegalStateException("Failed to init camera!");
        }

        dataCaptureContext.setFrameSource(camera);
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

    public void setUpIdCapture(Mode mode, IdCaptureListener listener) {
        /*
         * Create the IdCaptureSettings and enable the desired document types, supported sides and
         * the image you'd like to have in your result.
         */
        IdCaptureSettings settings;
        switch (mode) {
            case BARCODE:
                settings = getBarcodeSettings();
                break;
            case MRZ:
                settings = getMrzSettings();
                break;
            case VIZ:
                settings = getVizSettings();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
        /*
         * Create a mode responsible for recognizing documents. This mode is automatically added
         * to the passed DataCaptureContext.
         */
        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, settings);
        idCapture.addListener(listener);
    }

    /*
     * Create IdCaptureSettings to scan all the Barcode document types.
     */
    private IdCaptureSettings getBarcodeSettings() {
        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setShouldPassImageTypeToResult(IdImageType.FACE, true);
        settings.setSupportedDocuments(
            IdDocumentType.AAMVA_BARCODE,
            IdDocumentType.ARGENTINA_ID_BARCODE,
            IdDocumentType.COLOMBIA_ID_BARCODE,
            IdDocumentType.SOUTH_AFRICA_DL_BARCODE,
            IdDocumentType.SOUTH_AFRICA_ID_BARCODE,
            IdDocumentType.US_US_ID_BARCODE
        );
        return settings;
    }

    /*
     * Create IdCaptureSettings to scan all the MRZ document types.
     */
    private IdCaptureSettings getMrzSettings() {
        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setSupportedDocuments(
            IdDocumentType.ID_CARD_MRZ,
            IdDocumentType.PASSPORT_MRZ,
            IdDocumentType.VISA_MRZ,
            IdDocumentType.SWISS_DL_MRZ
        );
        return settings;
    }

    /*
     * Create IdCaptureSettings to scan all the VIZ document types.
     */
    private IdCaptureSettings getVizSettings() {
        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setSupportedSides(SupportedSides.FRONT_AND_BACK);
        settings.setShouldPassImageTypeToResult(IdImageType.FACE, true);
        settings.setSupportedDocuments(
            IdDocumentType.ID_CARD_VIZ,
            IdDocumentType.DL_VIZ
        );
        return settings;
    }

    /*
     * Unset the current idCapture mode by removing its listener and itself from
     * the DataCaptureContext.
     */
    public void unsetIdCapture(IdCaptureListener listener) {
        if (idCapture != null) {
            if (listener != null) {
                idCapture.removeListener(listener);
            }
            dataCaptureContext.removeMode(idCapture);
        }
    }

    // The three supported scan settings in this sample.
    enum Mode {
        BARCODE, MRZ, VIZ
    }
}
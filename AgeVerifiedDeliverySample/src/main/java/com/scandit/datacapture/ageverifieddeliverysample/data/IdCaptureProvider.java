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

package com.scandit.datacapture.ageverifieddeliverysample.data;

import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.data.IdDocumentType;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import java.util.Arrays;
import java.util.List;

/**
 * The provider of the IdCapture-related singletons.
 */
public class IdCaptureProvider {
    /**
     * The current IdCapture.
     */
    private final IdCapture idCapture;

    /**
     * IdCaptureOverlay displays the additional UI to guide the user through the capture process.
     */
    private final IdCaptureOverlay overlay;

    public IdCaptureProvider() {
        idCapture = IdCapture.forDataCaptureContext(null, createIdCaptureSettings());
        overlay = createIdCaptureOverlay();
    }

    /**
     * Get the current IdCapture.
     */
    public IdCapture getIdCapture() {
        return idCapture;
    }

    /**
     * IdCaptureOverlay displays the additional UI to guide the user through the capture process.
     */
    public IdCaptureOverlay getOverlay() {
        return overlay;
    }

    /**
     * Create the IdCapture's settings.
     */
    private IdCaptureSettings createIdCaptureSettings() {
        IdCaptureSettings settings =  new IdCaptureSettings();
        settings.setSupportedDocuments(ID_SUPPORTED_DOCUMENT_TYPES);
        return settings;
    }

    /**
     * Create the IdCapture's overlay.
     */
    private IdCaptureOverlay createIdCaptureOverlay() {
        return IdCaptureOverlay.newInstance(idCapture, null);
    }

    private static final List<IdDocumentType> ID_SUPPORTED_DOCUMENT_TYPES =
            Arrays.asList(
                    IdDocumentType.AAMVA_BARCODE,
                    IdDocumentType.DL_VIZ,
                    IdDocumentType.ID_CARD_MRZ,
                    IdDocumentType.ID_CARD_VIZ,
                    IdDocumentType.PASSPORT_MRZ,
                    IdDocumentType.US_US_ID_BARCODE
            );
}

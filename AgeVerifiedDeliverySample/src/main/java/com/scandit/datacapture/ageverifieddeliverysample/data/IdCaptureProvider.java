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

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

/**
 * The provider of the IdCapture-related singletons.
 */
public class IdCaptureProvider {
    /**
     * The current IdCapture.
     */
    private final IdCapture idCapture;

    /*
     * IdCaptureOverlay displays the additional UI to guide the user through the capture process.
     */
    private final IdCaptureOverlay overlay;

    public IdCaptureProvider(DataCaptureContext dataCaptureContext) {
        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, new IdCaptureSettings());
        overlay = IdCaptureOverlay.newInstance(idCapture, null);
    }

    /**
     * Get the current IdCapture.
     */
    public IdCapture getIdCapture() {
        return idCapture;
    }

    /*
     * IdCaptureOverlay displays the additional UI to guide the user through the capture process.
     */
    public IdCaptureOverlay getOverlay() {
        return overlay;
    }
}

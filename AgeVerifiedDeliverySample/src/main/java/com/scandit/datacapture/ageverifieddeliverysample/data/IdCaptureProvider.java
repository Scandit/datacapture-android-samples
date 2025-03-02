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

import com.scandit.datacapture.core.common.feedback.Feedback;
import com.scandit.datacapture.core.common.feedback.Vibration;
import com.scandit.datacapture.id.capture.DriverLicense;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureDocument;
import com.scandit.datacapture.id.capture.IdCaptureFeedback;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.capture.IdCard;
import com.scandit.datacapture.id.capture.Passport;
import com.scandit.datacapture.id.capture.SingleSideScanner;
import com.scandit.datacapture.id.data.IdCaptureRegion;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import java.util.Arrays;
import java.util.List;

/**
 * The provider of the IdCapture-related singletons.
 */
public class IdCaptureProvider {

    /**
     * The legal age in the US.
     */
    private static final int LEGAL_AGE = 21;

    private static final List<IdCaptureDocument> ACCEPTED_DOCUMENTS = Arrays.asList(
            new IdCard(IdCaptureRegion.ANY),
            new DriverLicense(IdCaptureRegion.ANY),
            new Passport(IdCaptureRegion.ANY)
    );

    /**
     * The current IdCapture.
     */
    private final IdCapture idCapture;

    /**
     * IdCaptureOverlay displays the additional UI to guide the user through the capture process.
     */
    private final IdCaptureOverlay overlay;

    /**
     * IdCaptureFeedback emits a sound and/or a vibration when a document is captured or rejected.
     */
    private final IdCaptureFeedback feedback;

    public IdCaptureProvider() {
        idCapture = IdCapture.forDataCaptureContext(null, createIdCaptureSettings());
        // Set an empty Feedback because we want to override the default feedback implemented by
        // the SDK to reject documents without a date of birth.
        idCapture.setFeedback(new IdCaptureFeedback());
        overlay = createIdCaptureOverlay();
        feedback = createIdCaptureFeedback();
    }

    /**
     * Get the current IdCapture.
     */
    public IdCapture getIdCapture() {
        return idCapture;
    }

    /**
     * Get the ID capture Feedback.
     */
    public IdCaptureFeedback getIdCaptureFeedback() {
        return feedback;
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
        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setAcceptedDocuments(ACCEPTED_DOCUMENTS);
        settings.setScannerType(new SingleSideScanner(true, true, true));
        settings.setRejectExpiredIds(true);
        settings.setRejectHolderBelowAge(LEGAL_AGE);

        return settings;
    }

    /**
     * Create the IdCapture's overlay.
     */
    private IdCaptureOverlay createIdCaptureOverlay() {
        return IdCaptureOverlay.newInstance(idCapture, null);
    }

    /**
     * Create the IdCapture's feedback.
     */
    private IdCaptureFeedback createIdCaptureFeedback() {
        IdCaptureFeedback feedback = IdCaptureFeedback.defaultFeedback();
        feedback.setIdRejected(new Feedback(
                Vibration.defaultVibration(),
                IdCaptureFeedback.defaultFailureSound()
        ));
        return feedback;
    }
}

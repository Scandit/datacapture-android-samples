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

package com.scandit.datacapture.vincodessample.data;

import static com.scandit.datacapture.core.common.geometry.MeasureUnit.FRACTION;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scandit.datacapture.core.area.RectangularLocationSelection;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.SizeWithUnit;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle;
import com.scandit.datacapture.text.capture.TextCapture;
import com.scandit.datacapture.text.capture.TextCaptureListener;
import com.scandit.datacapture.text.capture.TextCaptureSession;
import com.scandit.datacapture.text.capture.TextCaptureSettings;
import com.scandit.datacapture.text.data.CapturedText;
import com.scandit.datacapture.text.feedback.TextCaptureFeedback;
import com.scandit.datacapture.text.ui.TextCaptureOverlay;

import org.jetbrains.annotations.NotNull;

/**
 * The repository to interact with the TextCapture mode.
 */
public class TextCaptureRepository implements TextCaptureListener {
    /**
     * Automatically reject any captured text that doesn't look like a VIN.
     */
    // language=JSON
    private static final String SETTINGS_JSON =
            "{\"regex\": \"[0-9A-Z]{17}\"}";

    /**
     * Overlays provide UI to aid the user in the capture process and need to be attached
     * to DataCaptureView.
     */
    private final TextCaptureOverlay overlay;

    /**
     * The stream of captured VIN code texts.
     */
    private final MutableLiveData<CapturedText> capturedTexts = new MutableLiveData<>();

    /**
     * The DataCaptureContext that the current IdCapture is attached to.
     */
    public final DataCaptureContext dataCaptureContext;

    /**
     * The current TextCapture.
     */
    private final TextCapture textCapture;

    public TextCaptureRepository(DataCaptureContext dataCaptureContext) {
        this.dataCaptureContext = dataCaptureContext;

        TextCaptureSettings settings = TextCaptureSettings.fromJson(SETTINGS_JSON);

        /*
         * LocationSelection limits the area of the screen that the OCR will be run on. We limit
         * this area to a narrow stripe in the center of the screen.
         */
        RectangularLocationSelection rectangularSelection =
                RectangularLocationSelection.withSize(
                        new SizeWithUnit(new FloatWithUnit(0.7f, FRACTION),
                                new FloatWithUnit(0.07f, FRACTION)));

        settings.setLocationSelection(rectangularSelection);

        textCapture = TextCapture.forDataCaptureContext(dataCaptureContext, settings);
        textCapture.addListener(this);

        /*
         * Disable the default feedback. We will trigger a feedback manually for successfully
         * decoded VINs.
         */
        textCapture.setFeedback(new TextCaptureFeedback());

        /*
         * The viewfinder is an additional UI that aids the user in the capture process. We use
         * this viewfinder to visualize the scan area that we've earlier set with LocationSelection.
         */
        RectangularViewfinder viewfinder = new RectangularViewfinder(RectangularViewfinderStyle.SQUARE);
        viewfinder.setSize(new SizeWithUnit(new FloatWithUnit(0.7f, FRACTION),
            new FloatWithUnit(0.07f, FRACTION)));
        viewfinder.setDimming(.3f);

        overlay = TextCaptureOverlay.newInstance(textCapture, null);
        overlay.setViewfinder(viewfinder);
    }

    /**
     * Overlays provide UI to aid the user in the capture process and need to be attached
     * to DataCaptureView.
     */
    public TextCaptureOverlay getTextCaptureOverlay() {
        return overlay;
    }

    /**
     * The stream of texts that encode VIN codes.
     */
    public LiveData<CapturedText> capturedTexts() {
        return capturedTexts;
    }

    /**
     * Enable TextCapture so it may process the incoming frames.
     */
    public void enableTextCapture() {
        textCapture.setEnabled(true);
    }

    /**
     * Stop TextCapture from further processing of frames. This happens asynchronously, so some
     * results may still be delivered.
     */
    public void disableTextCapture() {
        textCapture.setEnabled(false);
    }

    @Override
    @WorkerThread
    public void onTextCaptured(
            @NotNull TextCapture mode,
            @NotNull TextCaptureSession session,
            @NotNull FrameData data
    ) {
        if (session.getNewlyCapturedTexts().isEmpty()) {
            return;
        }

        /*
         * Implement to handle the captured VIN codes.
         *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        capturedTexts.postValue(session.getNewlyCapturedTexts().get(0));
    }

    @Override
    @WorkerThread
    public void onObservationStarted(@NotNull TextCapture mode) {
        // In this sample, we are not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStopped(@NotNull TextCapture mode) {
        // In this sample, we are not interested in this callback.
    }
}


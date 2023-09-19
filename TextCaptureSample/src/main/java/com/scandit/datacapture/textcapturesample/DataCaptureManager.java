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

package com.scandit.datacapture.textcapturesample;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import com.scandit.datacapture.core.area.LocationSelection;
import com.scandit.datacapture.core.area.RectangularLocationSelection;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;
import com.scandit.datacapture.core.common.geometry.SizeWithUnit;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.text.capture.TextCapture;
import com.scandit.datacapture.text.capture.TextCaptureSettings;

import static com.scandit.datacapture.core.common.geometry.MeasureUnit.FRACTION;

/*
 * Initializes DataCapture.
 */
public final class DataCaptureManager implements OnSharedPreferenceChangeListener {
    // There is a Scandit sample license key set below here.
    // This license key is enabled for sample evaluation only.
    // If you want to build your own application, get your license key by signing up for a trial at https://ssl.scandit.com/dashboard/sign-up?p=test
    private static final String SCANDIT_LICENSE_KEY = "AYjTKgwFKLhZGtmHmyNAawklGVUpLfmaJ2JN39hPFcbHRdb8Sh3UX45m7PRkJtORsQzsAeBZw7aAZ/VBZlp5ykVZZOOYUI8ZAxAsZ3tOrh5HXX2CzFyh2yNzGtUXQuR5eFHqhXNx8+mfbsvN2zErPt0+TW4TESKXSx4764U8HnIF/01crbTR4/qxeWvIgdmGJkoV2YZc4wfZjpQI2Uvd3/J2jFcv/WrVHgWZ/VAC2lHTzC3JdwtTNJKxxDpsqKp1sDlARxGjw4hlebrAUbft3aWMjbtpVn2T4D+tBN3GVuwlD9Uo7MN3Sto17fSVSD1JLymYPHP7zxsnByy9mCBhKqTf3YKCh8DughdNJpIIWaaoY6t6OTof+TxY25XAboYM1Ii3FdaK1MjK2x9bVujInqaIYzPRYRwQj6lPyVaYSiRRJTsR6l3RLXyorSeqM6Mjyspyb9Gl3ht1grXe8TzMwVUFLYwBlV1zYcKfCVxHIaPo8irO1X7+sImu0166pNeK962FxzUx+rJMsvEIhy8mzF//yRI8WBLZvuBS5AH8EJHBb5p6DcdLgNVf3AwQWw6S5ENIw1Nu+eS2p+nm7msRRWP5jbqo8TfwgoellmtHaljlvmQ47kXfZvo9feDd7qZtGvWuX22yZkb+3k0OEfNKZaBKLrfzKU6X5TlmMvyhU7mF6mMdkBwex+NuKhRl1fYVjzD1hk75j70/QgXyjMv9nJpSEIXEt//AVHZTG4lGvAT0l3hPOie/zS0ixEH11+LJvbzsZQXYngggsJ40oCbajRxnvrMEcJQ5Lcxnp/Ov8qTmApOqK+XmLAV/s+MdeeIatFNTk6o9xGar+cB8";

    // language=JSON
    private static final String JSON_TEMPLATE = "{\"regex\": \"%s\"}";

    private static DataCaptureManager INSTANCE;

    private SettingsPrefs prefs;

    private DataCaptureContext dataCaptureContext;
    private Camera camera;
    private TextCapture textCapture;

    public static DataCaptureManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataCaptureManager(context.getApplicationContext());
        }

        return INSTANCE;
    }

    private DataCaptureManager(Context context) {
        prefs = new SettingsPrefs(context);
        prefs.setListener(this);

        /*
         * Create DataCaptureContext using your license key.
         */
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        initCamera();
        initTextCapture();
    }

    private void initCamera() {
        /*
         * Set the device's default camera as DataCaptureContext's FrameSource. DataCaptureContext
         * passes the frames from its FrameSource to the added modes to perform capture or
         * tracking.
         *
         * Since we are going to perform TextCapture in this sample, we initiate the camera with
         * the recommended settings for this mode.
         */
        camera = Camera.getDefaultCamera(TextCapture.createRecommendedCameraSettings());

        if (camera == null) {
            throw new IllegalStateException("Failed to init camera!");
        }

        dataCaptureContext.setFrameSource(camera);
    }

    private void initTextCapture() {
        String settingsJson = readTextCaptureSettingsJson();
        TextCaptureSettings settings = TextCaptureSettings.fromJson(settingsJson);

        /*
         * We will limit the recognition to the specific area. It's a rectangle taking the full
         * width of a frame, and 40% of it's height. We will move the center of this rectangle
         * depending on whether `BOTTOM`, `CENTER`, and `TOP` RecognitionArea is selected,
         * by controlling TextCapture's `pointOfInterest` property.
         */
        LocationSelection locationSelection =
                RectangularLocationSelection.withSize(
                        new SizeWithUnit(
                                new FloatWithUnit(1f, FRACTION),
                                new FloatWithUnit(0.4f, FRACTION)
                        )
                );
        settings.setLocationSelection(locationSelection);

        /*
         * Create a mode responsible for recognizing the text. This mode is automatically added
         * to the passes DataCaptureContext.
         */
        textCapture = TextCapture.forDataCaptureContext(dataCaptureContext, settings);

        /*
         * We set the center of the location selection.
         */
        textCapture.setPointOfInterest(getPointOfInterest());
    }

    private String readTextCaptureSettingsJson() {
        /*
         * While some of the TextCaptureSettings can be modified directly, some of them, like
         * `regex`, would normally be configured by a JSON you receive from us, tailored to your
         * specific use-case. Since in this sample we demonstrate work with several different
         * regular expressions, we simulate this by reading the setting from SharedPreferences and
         * creating JSON by hand.
         */
        TextType textType = prefs.getTextType();

        return String.format(JSON_TEMPLATE, textType.getRegex());
    }

    private PointWithUnit getPointOfInterest() {
        float centerY = prefs.getRecognitionArea().getCenterY();

        return new PointWithUnit(
                new FloatWithUnit(0.5f, FRACTION),
                new FloatWithUnit(centerY, FRACTION)
        );
    }


    public DataCaptureContext getDataCaptureContext() {
        return dataCaptureContext;
    }

    public Camera getCamera() {
        return camera;
    }

    public TextCapture getTextCapture() {
        return textCapture;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String settingsJson = readTextCaptureSettingsJson();
        TextCaptureSettings settings = TextCaptureSettings.fromJson(settingsJson);

        LocationSelection locationSelection =
                RectangularLocationSelection.withSize(
                        new SizeWithUnit(
                                new FloatWithUnit(1f, FRACTION),
                                new FloatWithUnit(0.4f, FRACTION)
                        )
                );
        settings.setLocationSelection(locationSelection);

        textCapture.applySettings(settings);
        textCapture.setPointOfInterest(getPointOfInterest());
    }
}

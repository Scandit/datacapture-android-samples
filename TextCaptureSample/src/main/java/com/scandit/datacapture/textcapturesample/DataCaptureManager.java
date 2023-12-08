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
    private static final String SCANDIT_LICENSE_KEY = "AW7z5wVbIbJtEL1x2i7B3/cet/ClBNVHZTfPtvJ2n3L/LY6/FDbqtzYItFO0DmhIJ2JP1Vxu7po1f74HqF9UTtRB/1DHY+CJdTiq/6dQ8vFgd9rzwlVfSYFgWPp9fK5nVUmnHyt9W5oRMcXObjYeC7Q/FO0NA0yRHUEtt/aBpnv/AxYTKG8wyVNqZKMJn+bhz/CFbH5pjtdj2aE85TlPGfQK4sBP/K2ONcx2ndbmY82SOquLlcZ55uAFuj4yCuQEI6iuokblpDVsql+vDiw3XMOmqwbmuGnAuCtGbtjyyWyQCKeiKWtZzdy+Cz7NnW/yRdwKY1xBjkaMA+A+NWeBxp9O2Ou6dBCPsRPg0Nqfv92sbv050dQc/+xccvEXWSi8UnD+AQoKp5V3gR/Yae/5+4fII9X3Tqjf/aNvXDw3m7YDQ+b+IJnkzLN5EgwGnzUmI8z3qMx9xcqhkWwBE/SSuIP47tBp5xwz02kN6qb+vZc/1p5EUQ/VtGVBfD1e+5Dii56BHsfPId/JpKpGUX1FFAYuT1uEbf7xLREDtFobn05tDxYPLrCa0hciRwCdWxHbUnYR1BF3zQQHih5Dd5qGyA5yKsgCsg7Na+9gC8O6hxpWlB4SbIFMEDluvJ+0v0ww5nnP2PWAO7v4k+Sgn7cQa7gDhQNee+pfuDvUlprUufio+dUmOUYNbn2TVwRVATmPx4U+p8Acg+Ohj85bSwPk+cNoq3Te6N0Ts5JnwrjCvVq6yrfbqyGFbgIhJiSxtgiZOfMZu8KoCvBfIUFE2A5WlNNaMZmQAtPozR31iX/Z2LuCIBhkFXGdd9CW/YPKhs8m25jlbOKnl0DWiBnM";

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

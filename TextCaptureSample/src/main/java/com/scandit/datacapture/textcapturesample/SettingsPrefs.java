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

/*
 * A class that abstracts access to SharedPreferences, that store the settings for this sample.
 */
public class SettingsPrefs {
    private static final String PREF_NAME = "text_capture_settings_json";

    private static final String PREF_KEY_TEXT_TYPE = "text_type";
    private static final String PREF_KEY_RECOGNITION_AREA = "recognition_area";

    private static final TextType DEFAULT_TEXT_TYPE = TextType.GS1_AI;
    private static final RecognitionArea DEFAULT_RECOGNITION_AREA = RecognitionArea.CENTER;

    private SharedPreferences prefs;

    public SettingsPrefs(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public TextType getTextType() {
        String name = prefs.getString(PREF_KEY_TEXT_TYPE, null);

        if (name != null) {
            return TextType.valueOf(name);
        } else {
            return DEFAULT_TEXT_TYPE;
        }
    }

    public void setTextType(TextType textType) {
        prefs.edit()
            .putString(PREF_KEY_TEXT_TYPE, textType.name())
            .apply();
    }

    public RecognitionArea getRecognitionArea() {
        String name = prefs.getString(PREF_KEY_RECOGNITION_AREA, null);

        if (name != null) {
            return RecognitionArea.valueOf(name);
        } else {
            return DEFAULT_RECOGNITION_AREA;
        }
    }

    public void setRecognitionArea(RecognitionArea recognitionArea) {
        prefs.edit()
                .putString(PREF_KEY_RECOGNITION_AREA, recognitionArea.name())
                .apply();
    }
}

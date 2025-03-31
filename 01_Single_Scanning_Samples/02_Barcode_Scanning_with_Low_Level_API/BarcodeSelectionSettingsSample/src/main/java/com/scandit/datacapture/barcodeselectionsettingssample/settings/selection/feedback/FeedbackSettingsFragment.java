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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.feedback;

import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSwitchPreference;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public class FeedbackSettingsFragment extends BasePreferenceFragment {

    public static FeedbackSettingsFragment newInstance() {
        return new FeedbackSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.feedback);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Feedback entries category
        StyledPreferenceCategory feedbackCategory = new StyledPreferenceCategory(
                requireContext(), FEEDBACK_CATEGORY_KEY, null
        );
        screen.addPreference(feedbackCategory);
        feedbackCategory.addPreference(createSwitchPreference(FEEDBACK_SOUND_KEY, R.string.sound, true));
        feedbackCategory.addPreference(createSwitchPreference(FEEDBACK_VIBRATION_KEY, R.string.vibration, false));
    }

    private Preference createSwitchPreference(String key, @StringRes int title, boolean defaultValue) {
        Preference preference = new StyledSwitchPreference(requireContext(), key, title, false);
        preference.setDefaultValue(defaultValue);
        return preference;
    }
}

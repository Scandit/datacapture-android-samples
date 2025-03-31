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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.autodetection;

import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSwitchPreference;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class AutoDetectionSettingsFragment extends BasePreferenceFragment {

    public static AutoDetectionSettingsFragment newInstance() {
        return new AutoDetectionSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.single_barcode_auto_detection);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // AutoDetection entries category.
        StyledPreferenceCategory autoDetectionCategory = new StyledPreferenceCategory(
                requireContext(), AUTO_DETECTION_CATEGORY_KEY, null
        );
        screen.addPreference(autoDetectionCategory);
        autoDetectionCategory.addPreference(createSwitchPreference(AUTO_DETECTION_KEY, R.string.single_barcode_auto_detection));
    }

    private Preference createSwitchPreference(String key, @StringRes int title) {
        return new StyledSwitchPreference(requireContext(), key, title, false);
    }
}

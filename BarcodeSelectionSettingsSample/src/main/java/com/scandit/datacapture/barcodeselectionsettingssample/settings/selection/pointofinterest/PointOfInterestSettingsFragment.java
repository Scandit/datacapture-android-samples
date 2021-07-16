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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.pointofinterest;

import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.FloatWithUnitPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.FloatWithUnitPreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSwitchPreference;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class PointOfInterestSettingsFragment extends BasePreferenceFragment {

    public static PointOfInterestSettingsFragment newInstance() {
        return new PointOfInterestSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.point_of_interest);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Poi enabled category.
        StyledPreferenceCategory enabledCategory = new StyledPreferenceCategory(
                requireContext(), POINT_OF_INTEREST_CATEGORY_KEY, R.string.enable_poi
        );
        screen.addPreference(enabledCategory);
        enabledCategory.addPreference(
                createSwitchEnabledPreference()
        );

        // Point of Interest coordinates.
        StyledPreferenceCategory coordinatesCategory = new StyledPreferenceCategory(
                requireContext(), POINT_OF_INTEREST_COORDINATES_CATEGORY_KEY, null
        );
        screen.addPreference(coordinatesCategory);
        Preference xPreference = createFloatWithUnitPreference(POINT_OF_INTEREST_X_KEY, R.string.x);
        coordinatesCategory.addPreference(xPreference);
        xPreference.setDependency(POINT_OF_INTEREST_ENABLED_KEY);

        Preference yPreference = createFloatWithUnitPreference(POINT_OF_INTEREST_Y_KEY, R.string.y);
        coordinatesCategory.addPreference(yPreference);
        yPreference.setDependency(POINT_OF_INTEREST_ENABLED_KEY);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(POINT_OF_INTEREST_X_KEY) || key.equals(POINT_OF_INTEREST_Y_KEY)) {
            navigateTo(FloatWithUnitPreferenceFragment.newInstance(key, getString(R.string.point_of_interest)));
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    private Preference createSwitchEnabledPreference() {
        return new StyledSwitchPreference(requireContext(), POINT_OF_INTEREST_ENABLED_KEY, R.string.enabled, false);
    }

    private Preference createFloatWithUnitPreference(String key, @StringRes int title) {
        return new FloatWithUnitPreference(requireContext(), key, title, true);
    }
}

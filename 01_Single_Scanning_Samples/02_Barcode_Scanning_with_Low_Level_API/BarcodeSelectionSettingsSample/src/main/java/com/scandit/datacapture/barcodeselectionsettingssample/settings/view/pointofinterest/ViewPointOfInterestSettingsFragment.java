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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.view.pointofinterest;

import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.DataCaptureDefaults;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.FloatWithUnitPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.FloatWithUnitPreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class ViewPointOfInterestSettingsFragment extends BasePreferenceFragment {

    public static ViewPointOfInterestSettingsFragment newInstance() {
        return new ViewPointOfInterestSettingsFragment();
    }

    private PointWithUnit defaultValue;

    @Override
    protected String getTitle() {
        return getString(R.string.point_of_interest);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        defaultValue = DataCaptureDefaults.getInstance(requireContext()).getViewPointOfInterest();

        // View Point of Interest coordinates category.
        StyledPreferenceCategory coordinatesCategory = new StyledPreferenceCategory(
                requireContext(), VIEW_POINT_OF_INTEREST_CATEGORY_KEY, null
        );
        screen.addPreference(coordinatesCategory);
        coordinatesCategory.addPreference(
                createFloatWithUnitPreference(VIEW_POINT_OF_INTEREST_X_KEY, R.string.x, defaultValue.getX())
        );
        coordinatesCategory.addPreference(
                createFloatWithUnitPreference(VIEW_POINT_OF_INTEREST_Y_KEY, R.string.y, defaultValue.getY())
        );
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        String title = getString(R.string.point_of_interest);
        FloatWithUnit value = null;
        switch (key) {
            case VIEW_POINT_OF_INTEREST_X_KEY: {
                value = defaultValue.getX();
                break;
            }
            case VIEW_POINT_OF_INTEREST_Y_KEY: {
                value = defaultValue.getY();
                break;
            }
        }
        if (value != null) {
            navigateTo(
                    FloatWithUnitPreferenceFragment.newInstance(key, title, value)
            );
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    private Preference createFloatWithUnitPreference(String key, @StringRes int title, FloatWithUnit defaultValue) {
        return new FloatWithUnitPreference(requireContext(), key, title, true, defaultValue);
    }
}

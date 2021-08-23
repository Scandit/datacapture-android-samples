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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.view.scanarea;

import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.DataCaptureDefaults;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.FloatWithUnitPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.FloatWithUnitPreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSwitchPreference;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MarginsWithUnit;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class ScanAreaSettingsFragment extends BasePreferenceFragment {

    public static ScanAreaSettingsFragment newInstance() {
        return new ScanAreaSettingsFragment();
    }

    private MarginsWithUnit defaultValue;

    @Override
    protected String getTitle() {
        return getString(R.string.scan_area);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        defaultValue = DataCaptureDefaults.getInstance(requireContext()).getScanAreaMargins();

        // Scan area margin category.
        StyledPreferenceCategory scanAreaMarginsCategory = new StyledPreferenceCategory(
                requireContext(), SCAN_AREA_MARGINS_CATEGORY_KEY, R.string.margins
        );
        screen.addPreference(scanAreaMarginsCategory);
        scanAreaMarginsCategory.addPreference(
                createMarginPreference(SCAN_AREA_MARGIN_LEFT_KEY, R.string.left, defaultValue.getLeft())
        );
        scanAreaMarginsCategory.addPreference(
                createMarginPreference(SCAN_AREA_MARGIN_TOP_KEY, R.string.top, defaultValue.getTop())
        );
        scanAreaMarginsCategory.addPreference(
                createMarginPreference(SCAN_AREA_MARGIN_RIGHT_KEY, R.string.right, defaultValue.getRight())
        );
        scanAreaMarginsCategory.addPreference(
                createMarginPreference(SCAN_AREA_MARGIN_BOTTOM_KEY, R.string.bottom, defaultValue.getBottom())
        );

        // Scan area guides category.
        StyledPreferenceCategory scanAreaGuidesCategory = new StyledPreferenceCategory(
                requireContext(), SCAN_AREA_GUIDES_CATEGORY_KEY, null
        );
        screen.addPreference(scanAreaGuidesCategory);
        scanAreaGuidesCategory.addPreference(createScanAreaGuidesPreference());
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        String title = getString(R.string.scan_area);
        FloatWithUnit value = null;
        switch (key) {
            case SCAN_AREA_MARGIN_LEFT_KEY: {
                value = defaultValue.getLeft();
                break;
            }
            case SCAN_AREA_MARGIN_TOP_KEY: {
                value = defaultValue.getTop();
                break;
            }
            case SCAN_AREA_MARGIN_RIGHT_KEY: {
                value = defaultValue.getRight();
                break;
            }
            case SCAN_AREA_MARGIN_BOTTOM_KEY: {
                value = defaultValue.getBottom();
                break;
            }
        }
        if (value != null) {
            navigateTo(FloatWithUnitPreferenceFragment.newInstance(key, title, value));
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    private Preference createMarginPreference(String key, @StringRes int title, FloatWithUnit defaultValue) {
        return new FloatWithUnitPreference(requireContext(), key, title, true, defaultValue);
    }

    private Preference createScanAreaGuidesPreference() {
        return new StyledSwitchPreference(requireContext(), SCAN_AREA_GUIDES_KEY, R.string.show_scan_area_guides);
    }
}

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
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.FloatWithUnitPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.FloatWithUnitPreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSwitchPreference;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class ScanAreaSettingsFragment extends BasePreferenceFragment {

    public static ScanAreaSettingsFragment newInstance() {
        return new ScanAreaSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.scan_area);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Scan area margin category.
        StyledPreferenceCategory scanAreaMarginsCategory = new StyledPreferenceCategory(
                requireContext(), SCAN_AREA_MARGINS_CATEGORY_KEY, R.string.margins
        );
        screen.addPreference(scanAreaMarginsCategory);
        scanAreaMarginsCategory.addPreference(createMarginPreference(SCAN_AREA_MARGIN_TOP_KEY, R.string.top));
        scanAreaMarginsCategory.addPreference(createMarginPreference(SCAN_AREA_MARGIN_RIGHT_KEY, R.string.right));
        scanAreaMarginsCategory.addPreference(createMarginPreference(SCAN_AREA_MARGIN_BOTTOM_KEY, R.string.bottom));
        scanAreaMarginsCategory.addPreference(createMarginPreference(SCAN_AREA_MARGIN_LEFT_KEY, R.string.left));

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
        if (key.equals(SCAN_AREA_MARGIN_TOP_KEY) || key.equals(SCAN_AREA_MARGIN_RIGHT_KEY) ||
                key.equals(SCAN_AREA_MARGIN_BOTTOM_KEY) || key.equals(SCAN_AREA_MARGIN_LEFT_KEY)) {
            navigateTo(FloatWithUnitPreferenceFragment.newInstance(key, getString(R.string.scan_area)));
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    private Preference createMarginPreference(String key, @StringRes int title) {
        return new FloatWithUnitPreference(requireContext(), key, title, true);
    }

    private Preference createScanAreaGuidesPreference() {
        return new StyledSwitchPreference(requireContext(), SCAN_AREA_GUIDES_KEY, R.string.show_scan_area_guides);
    }
}

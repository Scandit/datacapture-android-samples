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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.view;

import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.view.overlay.OverlaySettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.view.pointofinterest.ViewPointOfInterestSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.view.scanarea.ScanAreaSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.view.viewfinder.ViewfinderSettingsFragment;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class ViewSettingsFragment extends BasePreferenceFragment {

    public static ViewSettingsFragment newInstance() {
        return new ViewSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.view);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case VIEW_SCAN_AREA_KEY:
                navigateTo(ScanAreaSettingsFragment.newInstance());
                return true;
            case VIEW_POINT_OF_INTEREST_KEY:
                navigateTo(ViewPointOfInterestSettingsFragment.newInstance());
                return true;
            case VIEW_OVERLAY_KEY:
                navigateTo(OverlaySettingsFragment.newInstance());
                return true;
            case VIEW_VIEWFINDER_KEY:
                navigateTo(ViewfinderSettingsFragment.newInstance());
                return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // View entries category.
        StyledPreferenceCategory sectionsCategory = new StyledPreferenceCategory(
                requireContext(), VIEW_CATEGORY_KEY, null
        );
        screen.addPreference(sectionsCategory);
        sectionsCategory.addPreference(createPreference(VIEW_SCAN_AREA_KEY, R.string.scan_area));
        sectionsCategory.addPreference(createPreference(VIEW_POINT_OF_INTEREST_KEY, R.string.point_of_interest));
        sectionsCategory.addPreference(createPreference(VIEW_OVERLAY_KEY, R.string.overlay));
        sectionsCategory.addPreference(createPreference(VIEW_VIEWFINDER_KEY, R.string.viewfinder));
    }

    private Preference createPreference(String key, @StringRes int title) {
        return new StyledPreference(requireContext(), key, title, true);
    }
}

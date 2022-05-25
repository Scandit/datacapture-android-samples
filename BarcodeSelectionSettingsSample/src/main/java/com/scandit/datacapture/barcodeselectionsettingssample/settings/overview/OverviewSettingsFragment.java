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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.overview;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.camera.CameraSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.BarcodeSelectionSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.view.ViewSettingsFragment;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class OverviewSettingsFragment extends BasePreferenceFragment {

    public static OverviewSettingsFragment newInstance() {
        return new OverviewSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.settings);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Overview entries category.
        StyledPreferenceCategory sectionsCategory = new StyledPreferenceCategory(
                requireContext(), OVERVIEW_CATEGORY_KEY, null
        );
        screen.addPreference(sectionsCategory);
        sectionsCategory.addPreference(createOverviewPreference(BARCODE_SELECTION_KEY, R.string.barcode_selection));
        sectionsCategory.addPreference(createOverviewPreference(CAMERA_KEY, R.string.camera));
        sectionsCategory.addPreference(createOverviewPreference(VIEW_KEY, R.string.view));

        screen.addPreference(createDataCaptureVersionPreference());
    }

    private Preference createOverviewPreference(String key, int title) {
        return new StyledPreference(requireContext(), key, title, true);
    }

    private Preference createDataCaptureVersionPreference() {
        return new DataCaptureVersionPreference(requireContext());
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case BARCODE_SELECTION_KEY: {
                navigateTo(BarcodeSelectionSettingsFragment.newInstance());
                return true;
            }
            case CAMERA_KEY: {
                navigateTo(CameraSettingsFragment.newInstance());
                return true;
            }
            case VIEW_KEY: {
                navigateTo(ViewSettingsFragment.newInstance());
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }
}

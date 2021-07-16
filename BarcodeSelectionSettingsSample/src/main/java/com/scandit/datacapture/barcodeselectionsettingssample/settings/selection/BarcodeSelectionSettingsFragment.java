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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.selection;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.autodetection.AutoDetectionSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.duplicatefilter.DuplicateFilterSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.feedback.FeedbackSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.pointofinterest.PointOfInterestSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.symbologies.SymbologiesSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.type.SelectionTypeSettingsFragment;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class BarcodeSelectionSettingsFragment extends BasePreferenceFragment {

    public static BarcodeSelectionSettingsFragment newInstance() {
        return new BarcodeSelectionSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.barcode_selection);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        StyledPreferenceCategory sectionsCategory = new StyledPreferenceCategory(
                requireContext(), OVERVIEW_CATEGORY_KEY, null
        );
        screen.addPreference(sectionsCategory);
        sectionsCategory.addPreference(createOverviewPreference(SYMBOLOGIES_KEY, R.string.symbologies));
        sectionsCategory.addPreference(createOverviewPreference(SELECTION_TYPE_KEY, R.string.selection_type));
        sectionsCategory.addPreference(createOverviewPreference(SINGLE_BARCODE_AUTO_DETECTION_KEY, R.string.single_barcode_auto_detection));
        sectionsCategory.addPreference(createOverviewPreference(FEEDBACK_KEY, R.string.feedback));
        sectionsCategory.addPreference(createOverviewPreference(DUPLICATE_FILTER_OVERVIEW_KEY, R.string.duplicate_filter));
        sectionsCategory.addPreference(createOverviewPreference(POINT_OF_INTEREST_KEY, R.string.point_of_interest));
    }

    private Preference createOverviewPreference(String key, int title) {
        return new StyledPreference(requireContext(), key, title, true);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case SYMBOLOGIES_KEY: {
                navigateTo(SymbologiesSettingsFragment.newInstance());
                return true;
            }
            case SELECTION_TYPE_KEY: {
                navigateTo(SelectionTypeSettingsFragment.newInstance());
                return true;
            }
            case SINGLE_BARCODE_AUTO_DETECTION_KEY: {
                navigateTo(AutoDetectionSettingsFragment.newInstance());
                return true;
            }
            case FEEDBACK_KEY: {
                navigateTo(FeedbackSettingsFragment.newInstance());
                return true;
            }
            case DUPLICATE_FILTER_KEY: {
                navigateTo(DuplicateFilterSettingsFragment.newInstance());
                return true;
            }
            case POINT_OF_INTEREST_KEY: {
                navigateTo(PointOfInterestSettingsFragment.newInstance());
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }
}

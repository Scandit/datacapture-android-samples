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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.duplicatefilter;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledNumericEditTextPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class DuplicateFilterSettingsFragment extends BasePreferenceFragment {

    public static DuplicateFilterSettingsFragment newInstance() {
        return new DuplicateFilterSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.code_duplicate_filter);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // DuplicateFilter entries category.
        StyledPreferenceCategory sectionsCategory = new StyledPreferenceCategory(
                requireContext(), DUPLICATE_FILTER_CATEGORY_KEY, null
        );
        screen.addPreference(sectionsCategory);
        sectionsCategory.addPreference(createCodeDuplicateFilterPreference());
    }

    private Preference createCodeDuplicateFilterPreference() {
        EditTextPreference preference = new StyledNumericEditTextPreference(
                requireContext(), DUPLICATE_FILTER_KEY, R.string.duplicate_filter_with_unit, false
        );
        preference.setDefaultValue(String.valueOf(0.5));
        return preference;
    }
}

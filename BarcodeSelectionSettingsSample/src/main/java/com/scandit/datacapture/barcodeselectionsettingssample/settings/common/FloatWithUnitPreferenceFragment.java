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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.common;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public class FloatWithUnitPreferenceFragment extends BasePreferenceFragment {

    private final static String BUNDLE_KEY_PREFIX = "prefix";
    private final static String BUNDLE_KEY_TITLE = "title";

    public static FloatWithUnitPreferenceFragment newInstance(String prefix, String title) {
        FloatWithUnitPreferenceFragment fragment = new FloatWithUnitPreferenceFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_PREFIX, prefix);
        bundle.putString(BUNDLE_KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String title;
    private String prefix;

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        this.prefix = getArguments().getString(BUNDLE_KEY_PREFIX);
        this.title = getArguments().getString(BUNDLE_KEY_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Wrapping category getFloatWithUnitCategoryKey.
        StyledPreferenceCategory sectionsCategory = new StyledPreferenceCategory(
                requireContext(), getFloatWithUnitCategoryKey(prefix), null
        );
        screen.addPreference(sectionsCategory);
        sectionsCategory.addPreference(createNumberPreference());
        sectionsCategory.addPreference(createMeasureUnitPreference());
    }

    private Preference createNumberPreference() {
        return new StyledNumericEditTextPreference(requireContext(), getFloatWithUnitNumberKey(prefix), R.string.value, true);
    }

    private Preference createMeasureUnitPreference() {
        ListPreference preference = new StyledListPreference(
                requireContext(), getFloatWithUnitMeasureUnitKey(prefix), R.string.measure_unit, true
        );
        MeasureUnit[] measureUnits = MeasureUnit.values();
        String[] entries = new String[measureUnits.length];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = measureUnits[i].name();
        }
        preference.setEntries(entries);
        preference.setEntryValues(entries);
        return preference;
    }
}

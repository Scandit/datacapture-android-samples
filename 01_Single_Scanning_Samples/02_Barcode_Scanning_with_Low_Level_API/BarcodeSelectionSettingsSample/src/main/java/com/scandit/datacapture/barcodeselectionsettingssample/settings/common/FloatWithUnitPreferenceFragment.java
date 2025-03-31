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

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.getFloatWithUnitCategoryKey;
import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.getFloatWithUnitMeasureUnitKey;
import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.getFloatWithUnitNumberKey;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;

public class FloatWithUnitPreferenceFragment extends BasePreferenceFragment {

    private final static String BUNDLE_KEY_PREFIX = "prefix";
    private final static String BUNDLE_KEY_TITLE = "title";
    private final static String BUNDLE_KEY_DEFAULT_VALUE = "default_value";
    private final static String BUNDLE_KEY_DEFAULT_UNIT = "default_unit";

    public static FloatWithUnitPreferenceFragment newInstance(
            String prefix, String title, FloatWithUnit defaultValue
    ) {
        FloatWithUnitPreferenceFragment fragment = new FloatWithUnitPreferenceFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_PREFIX, prefix);
        bundle.putString(BUNDLE_KEY_TITLE, title);
        bundle.putFloat(BUNDLE_KEY_DEFAULT_VALUE, defaultValue.getValue());
        bundle.putSerializable(BUNDLE_KEY_DEFAULT_UNIT, defaultValue.getUnit());
        fragment.setArguments(bundle);
        return fragment;
    }

    private String title;
    private String prefix;
    private FloatWithUnit defaultValue;

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        Bundle args = getArguments();
        this.prefix = args.getString(BUNDLE_KEY_PREFIX);
        this.title = args.getString(BUNDLE_KEY_TITLE);
        MeasureUnit unit;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            unit = args.getSerializable(BUNDLE_KEY_DEFAULT_UNIT, MeasureUnit.class);
        } else {
            unit = (MeasureUnit) args.getSerializable(BUNDLE_KEY_DEFAULT_UNIT);
        }
        this.defaultValue = new FloatWithUnit(args.getFloat(BUNDLE_KEY_DEFAULT_VALUE), unit);
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
        EditTextPreference preference =
                new StyledNumericEditTextPreference(requireContext(), getFloatWithUnitNumberKey(prefix), R.string.value, true);
        preference.setDefaultValue(String.valueOf(defaultValue.getValue()));
        return preference;
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
        preference.setDefaultValue(defaultValue.getUnit().name());
        return preference;
    }
}

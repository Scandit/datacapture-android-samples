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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.symbologies.details;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledCheckboxPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledListPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSwitchPreference;
import com.scandit.datacapture.core.data.Range;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class SymbologyDetailSettingsFragment extends BasePreferenceFragment {

    private static final String BUNDLE_FIELD_SYMBOLOGY_NAME = "symbology_name";

    public static SymbologyDetailSettingsFragment newInstance(Symbology symbology) {
        SymbologyDetailSettingsFragment fragment = new SymbologyDetailSettingsFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_FIELD_SYMBOLOGY_NAME, symbology.name());
        fragment.setArguments(args);
        return fragment;
    }

    private Symbology symbology;
    private SymbologyDescription description;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.symbology = Symbology.valueOf(getArguments().getString(BUNDLE_FIELD_SYMBOLOGY_NAME));
        this.description = SymbologyDescription.create(symbology);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getTitle() {
        return description.getReadableName();
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Enabled/ColorInvertedEnabled category.
        StyledPreferenceCategory enabledCategory = new StyledPreferenceCategory(
                requireContext(), SYMBOLOGY_ENABLED_CATEGORY_KEY, null
        );
        screen.addPreference(enabledCategory);
        enabledCategory.addPreference(createSwitchPreference(getSymbologyEnabledKey(symbology), R.string.enabled));
        if (description.isColorInvertible()) {
            enabledCategory.addPreference(
                    createSwitchPreference(getSymbologyColorInvertedEnabledKey(symbology), R.string.color_inverted)
            );
        }

        // Symbol Count rage category.
        Range range = description.getActiveSymbolCountRange();
        if (!isFixedRange(range)) {
            StyledPreferenceCategory rangeCategory = new StyledPreferenceCategory(
                    requireContext(), SYMBOLOGY_RANGE_CATEGORY_KEY, R.string.range
            );
            screen.addPreference(rangeCategory);
            for (Preference preference: createRangePreferences(range)) {
                rangeCategory.addPreference(preference);
            }
        }

        // Extensions.
        Set<String> extensions = new HashSet<>();
        for (String extension: description.getSupportedExtensions()) {
            // Remove *_add_on extensions as they're not supported.
            if (!extension.endsWith("_add_on")) {
                extensions.add(extension);
            }
        }
        if (!extensions.isEmpty()) {
            StyledPreferenceCategory extensionsCategory = new StyledPreferenceCategory(
                    requireContext(), SYMBOLOGY_EXTENSIONS_CATEGORY_KEY, R.string.extensions
            );
            screen.addPreference(extensionsCategory);
            for (String extension: extensions) {
                extensionsCategory.addPreference(
                        createCheckboxPreference(getSymbologyExtensionKey(symbology, extension), extension)
                );
            }
        }
    }

    private Preference createCheckboxPreference(String key, String title) {
        return new StyledCheckboxPreference(requireContext(), key, title, true);
    }

    private Preference createSwitchPreference(String key, @StringRes int title) {
        return new StyledSwitchPreference(requireContext(), key, title, true);
    }

    private List<Preference> createRangePreferences(final Range range) {
        List<Preference> rangePreferences = new ArrayList<>();
        List<String> entries = new ArrayList<>();
        for (int i = range.getMinimum(); i <= range.getMaximum(); i += range.getStep()) {
            entries.add(String.valueOf(i));
        }
        String[] entriesArray = entries.toArray(new String[0]);

        final ListPreference minPreference = new StyledListPreference(
                requireContext(), getSymbologyMinRangeKey(symbology), R.string.min, true
        );
        final ListPreference maxPreference = new StyledListPreference(
                requireContext(), getSymbologyMaxRangeKey(symbology), R.string.max, true
        );
        rangePreferences.add(minPreference);
        rangePreferences.add(maxPreference);

        setupMinRangePreference(
                minPreference, maxPreference, range, description.getDefaultSymbolCountRange(), entriesArray
        );
        setupMaxRangePreference(
                maxPreference, minPreference, range, description.getDefaultSymbolCountRange(), entriesArray
        );
        return rangePreferences;
    }

    private void setupMaxRangePreference(
            final ListPreference maxPreference,
            final ListPreference minPreference,
            final Range completeRange,
            final Range defaultRange,
            final String[] allRangeEntries
    ) {
        maxPreference.setEntries(allRangeEntries);
        maxPreference.setEntryValues(allRangeEntries);
        maxPreference.setDefaultValue(String.valueOf(defaultRange.getMaximum()));
        maxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int min = Integer.parseInt(minPreference.getValue());
                int max = completeRange.getMaximum();
                int step = completeRange.getStep();

                List<String> entries = new ArrayList<>();
                for (int i = min; i <= max; i += step) {
                    entries.add(String.valueOf(i));
                }
                String[] entriesArray = entries.toArray(new String[0]);
                maxPreference.setEntries(entriesArray);
                maxPreference.setEntryValues(entriesArray);
                return false;
            }
        });
    }

    private void setupMinRangePreference(
            final ListPreference minPreference,
            final ListPreference maxPreference,
            final Range completeRange,
            final Range defaultRange,
            final String[] allRangeEntries
    ) {
        minPreference.setEntries(allRangeEntries);
        minPreference.setEntryValues(allRangeEntries);
        minPreference.setDefaultValue(String.valueOf(defaultRange.getMinimum()));
        minPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int min = completeRange.getMinimum();
                int max = Integer.parseInt(maxPreference.getValue());
                int step = completeRange.getStep();

                List<String> entries = new ArrayList<>();
                for (int i = min; i <= max; i += step) {
                    entries.add(String.valueOf(i));
                }
                String[] entriesArray = entries.toArray(new String[0]);
                minPreference.setEntries(entriesArray);
                minPreference.setEntryValues(entriesArray);
                return false;
            }
        });
    }

    private boolean isFixedRange(Range range) {
        return range.getMaximum() == range.getMinimum() || range.getStep() <= 0;
    }
}

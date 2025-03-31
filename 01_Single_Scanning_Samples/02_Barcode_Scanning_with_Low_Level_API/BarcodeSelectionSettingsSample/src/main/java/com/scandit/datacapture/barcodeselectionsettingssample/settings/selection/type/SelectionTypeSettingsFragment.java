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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.type;

import android.content.SharedPreferences;

import androidx.annotation.StringRes;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionFreezeBehavior;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionTapBehavior;
import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledListPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class SelectionTypeSettingsFragment extends BasePreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static SelectionTypeSettingsFragment newInstance() {
        return new SelectionTypeSettingsFragment();
    }

    private PreferenceCategory tapSelectionCategory;
    private final Preference[] tapSelectionSettings = new Preference[2];
    private PreferenceCategory aimerSelectionCategory;
    private final Preference[] aimerSelectionSettings = new Preference[1];

    @Override
    protected String getTitle() {
        return getString(R.string.selection_type);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SELECTION_TYPES_KEY)) {
            String value = sharedPreferences.getString(SELECTION_TYPES_KEY, null);
            if (value == null) {
                hideTapSelectionSettings();
                hideAimerSelectionSettings();
            } else if (value.equals(SelectionType.TAP_TO_SELECT.name())) {
                hideAimerSelectionSettings();
                showTapSelectionSettings();
            } else if (value.equals(SelectionType.AIM_TO_SELECT.name())) {
                hideTapSelectionSettings();
                showAimerSelectionSettings();
            }
        }
    }

    private void hideTapSelectionSettings() {
        tapSelectionCategory.removeAll();
        getPreferenceScreen().removePreference(tapSelectionCategory);
    }

    private void showTapSelectionSettings() {
        getPreferenceScreen().addPreference(tapSelectionCategory);
        for (Preference preference: tapSelectionSettings) {
            tapSelectionCategory.addPreference(preference);
        }
    }

    private void hideAimerSelectionSettings() {
        aimerSelectionCategory.removeAll();
        getPreferenceScreen().removePreference(aimerSelectionCategory);
    }

    private void showAimerSelectionSettings() {
        getPreferenceScreen().addPreference(aimerSelectionCategory);
        for (Preference preference: aimerSelectionSettings) {
            aimerSelectionCategory.addPreference(preference);
        }
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // SelectionType category.
        StyledPreferenceCategory selectionTypeCategory = new StyledPreferenceCategory(
                requireContext(), SELECTION_TYPE_CATEGORY_KEY, null
        );
        screen.addPreference(selectionTypeCategory);
        selectionTypeCategory.addPreference(createSelectionTypesPreference());

        // TapSelectionType settings category.
        tapSelectionCategory = new StyledPreferenceCategory(
                requireContext(), TAP_SELECTION_SETTINGS_CATEGORY_KEY, null
        );
        tapSelectionSettings[0] = createSelectionTypeFreezeBehaviorPreference();
        tapSelectionSettings[1] = createSelectionTypeTapBehaviorPreference();

        // AimerSelectionType settings category.
        aimerSelectionCategory = new StyledPreferenceCategory(
                requireContext(), AIMER_SELECTION_SETTINGS_CATEGORY_KEY, null
        );
        aimerSelectionSettings[0] = createSelectionTypeAimerStrategyPreference();
        onSharedPreferenceChanged(getPreferenceManager().getSharedPreferences(), SELECTION_TYPES_KEY);
    }

    private Preference createSelectionTypesPreference() {
        ListPreference preference = new StyledListPreference(
                requireContext(), SELECTION_TYPES_KEY, R.string.selection_type, true
        );
        SelectionType[] selectionTypeValues = SelectionType.values();
        String[] entries = new String[selectionTypeValues.length];
        String[] entryValues = new String[selectionTypeValues.length];
        for (int i = 0; i < selectionTypeValues.length; i++) {
            entryValues[i] = selectionTypeValues[i].name();
            entries[i] = getString(selectionTypeValues[i].readableName);
        }
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
        preference.setDefaultValue(SelectionType.TAP_TO_SELECT.name());
        return preference;
    }

    private Preference createSelectionTypeFreezeBehaviorPreference() {
        ListPreference preference = new StyledListPreference(
                requireContext(), FREEZE_BEHAVIOR_KEY, R.string.freeze_behavior, true
        );
        BarcodeSelectionFreezeBehavior[] freezeBehaviors = BarcodeSelectionFreezeBehavior.values();
        String[] entries = new String[freezeBehaviors.length];
        String[] entryValues = new String[freezeBehaviors.length];
        for (int i = 0; i < freezeBehaviors.length; i++) {
            entryValues[i] = freezeBehaviors[i].name();
            entries[i] = getString(getReadableNameForFreezeBehavior(freezeBehaviors[i]));
        }
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
        preference.setDefaultValue(BarcodeSelectionFreezeBehavior.MANUAL.name());
        return preference;
    }

    @StringRes
    private int getReadableNameForFreezeBehavior(BarcodeSelectionFreezeBehavior behavior) {
        switch (behavior) {
            case MANUAL: return R.string.manual;
            case MANUAL_AND_AUTOMATIC: return R.string.manual_and_automatic;
            default: return R.string.unknown;
        }
    }

    private Preference createSelectionTypeTapBehaviorPreference() {
        ListPreference preference = new StyledListPreference(
                requireContext(), TAP_BEHAVIOR_KEY, R.string.tap_behavior, true
        );
        BarcodeSelectionTapBehavior[] tapBehaviors = BarcodeSelectionTapBehavior.values();
        String[] entries = new String[tapBehaviors.length];
        String[] entryValues = new String[tapBehaviors.length];
        for (int i = 0; i < tapBehaviors.length; i++) {
            entryValues[i] = tapBehaviors[i].name();
            entries[i] = getString(getReadableNameForTapBehavior(tapBehaviors[i]));
        }
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
        preference.setDefaultValue(BarcodeSelectionTapBehavior.TOGGLE_SELECTION.name());
        return preference;
    }

    private Preference createSelectionTypeAimerStrategyPreference() {
        ListPreference preference = new StyledListPreference(
                requireContext(), AIMER_STRATEGY_KEY, R.string.selection_strategy, true
        );
        SelectionStrategy[] strategies = SelectionStrategy.values();
        String[] entries = new String[strategies.length];
        String[] entryValues = new String[strategies.length];
        for (int i = 0; i < strategies.length; i++) {
            entryValues[i] = strategies[i].name();
            entries[i] = getString(strategies[i].readableName);
        }
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
        preference.setDefaultValue(SelectionStrategy.MANUAL.name());
        return preference;
    }

    @StringRes
    private int getReadableNameForTapBehavior(BarcodeSelectionTapBehavior behavior) {
        switch (behavior) {
            case TOGGLE_SELECTION: return R.string.toggle_selection;
            case REPEAT_SELECTION: return R.string.repeat_selection;
            default: return R.string.unknown;
        }
    }

    public enum SelectionType {
        TAP_TO_SELECT(R.string.tap_to_select), AIM_TO_SELECT(R.string.aim_to_select);

        @StringRes private final int readableName;

        SelectionType(@StringRes int readableName) {
            this.readableName = readableName;
        }

        public int getReadableName() {
            return readableName;
        }
    }

    public enum SelectionStrategy {
        MANUAL(R.string.manual), AUTO(R.string.auto);

        @StringRes private final int readableName;

        SelectionStrategy(@StringRes int readableName) {
            this.readableName = readableName;
        }
    }
}

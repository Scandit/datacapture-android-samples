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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.view.overlay;

import androidx.annotation.StringRes;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledListPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSwitchPreference;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class OverlaySettingsFragment extends BasePreferenceFragment {

    public static OverlaySettingsFragment newInstance() {
        return new OverlaySettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.overlay);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Overlay brush entries category.
        StyledPreferenceCategory brushesCategory = new StyledPreferenceCategory(
                requireContext(), OVERLAY_BRUSHES_CATEGORY_KEY, null
        );
        screen.addPreference(brushesCategory);
        brushesCategory.addPreference(createBrushPreference(OVERLAY_TRACKED_BRUSH_KEY, R.string.tracked_brush));
        brushesCategory.addPreference(createBrushPreference(OVERLAY_AIMED_BRUSH_KEY, R.string.aimed_brush));
        brushesCategory.addPreference(createBrushPreference(OVERLAY_SELECTING_BRUSH_KEY, R.string.selecting_brush));
        brushesCategory.addPreference(createBrushPreference(OVERLAY_SELECTED_BRUSH_KEY, R.string.selected_brush));

        // Overlay hints category.
        StyledPreferenceCategory hintsCategory = new StyledPreferenceCategory(
                requireContext(), OVERLAY_HINTS_CATEGORY_KEY, null
        );
        screen.addPreference(hintsCategory);
        hintsCategory.addPreference(createHintsEnabledPreference());
    }

    private Preference createHintsEnabledPreference() {
        return new StyledSwitchPreference(requireContext(), OVERLAY_HINTS_KEY, R.string.show_hints, false);
    }

    private Preference createBrushPreference(String key, @StringRes int title) {
        ListPreference preference = new StyledListPreference(requireContext(), key, title, true);
        BrushStyle[] styles = BrushStyle.values();
        String[] entries = new String[styles.length];
        String[] entryValues = new String[styles.length];
        for (int i = 0; i < styles.length; i++) {
            entryValues[i] = styles[i].name();
            entries[i] = getString(styles[i].getReadableName());
        }
        preference.setDefaultValue(BrushStyle.DEFAULT.name());
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
        return preference;
    }

    public enum BrushStyle {
        DEFAULT(R.string.default_), BLUE(R.string.blue);

        private int readableName;

        BrushStyle(@StringRes int readableName) {
            this.readableName = readableName;
        }

        public int getReadableName() {
            return readableName;
        }
    }
}

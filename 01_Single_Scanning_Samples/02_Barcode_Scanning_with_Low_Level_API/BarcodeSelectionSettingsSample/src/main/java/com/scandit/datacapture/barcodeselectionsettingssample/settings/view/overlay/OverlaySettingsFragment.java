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
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.barcode.selection.ui.overlay.BarcodeSelectionBasicOverlayStyle;
import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.DataCaptureDefaults;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledListPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSwitchPreference;

import java.util.ArrayList;
import java.util.List;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class OverlaySettingsFragment extends BasePreferenceFragment {

    public static OverlaySettingsFragment newInstance() {
        return new OverlaySettingsFragment();
    }

    private List<ListPreference> brushPreferences;

    @Override
    protected String getTitle() {
        return getString(R.string.overlay);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        brushPreferences = new ArrayList<>();

        // Overlay style category.
        StyledPreferenceCategory styleCategory = new StyledPreferenceCategory(
                requireContext(), OVERLAY_STYLE_CATEGORY_KEY, null
        );
        screen.addPreference(styleCategory);
        styleCategory.addPreference(createStylesPreference());

        // Overlay brush entries category.
        StyledPreferenceCategory brushesCategory = new StyledPreferenceCategory(
                requireContext(), OVERLAY_BRUSHES_CATEGORY_KEY, R.string.brushes
        );
        screen.addPreference(brushesCategory);
        brushesCategory.addPreference(createBrushPreference(OVERLAY_TRACKED_BRUSH_KEY, R.string.tracked_brush));
        brushesCategory.addPreference(createBrushPreference(OVERLAY_AIMED_BRUSH_KEY, R.string.aimed_brush));
        brushesCategory.addPreference(createBrushPreference(OVERLAY_SELECTING_BRUSH_KEY, R.string.selecting_brush));
        brushesCategory.addPreference(createBrushPreference(OVERLAY_SELECTED_BRUSH_KEY, R.string.selected_brush));

        // Overlay background color.
        StyledPreferenceCategory overlayBackgroundCategory = new StyledPreferenceCategory(
                requireContext(), OVERLAY_BACKGROUND_CATEGORY_KEY, null
        );
        screen.addPreference(overlayBackgroundCategory);
        overlayBackgroundCategory.addPreference(createFrozenOverlayBackgroundColorCategory());

        // Overlay hints category.
        StyledPreferenceCategory hintsCategory = new StyledPreferenceCategory(
                requireContext(), OVERLAY_HINTS_CATEGORY_KEY, null
        );
        screen.addPreference(hintsCategory);
        hintsCategory.addPreference(createHintsEnabledPreference());
    }

    private Preference createHintsEnabledPreference() {
        SwitchPreferenceCompat hintsPreference =
                new StyledSwitchPreference(requireContext(), OVERLAY_HINTS_KEY, R.string.show_hints, false);
        hintsPreference.setDefaultValue(true);
        return hintsPreference;
    }

    private Preference createFrozenOverlayBackgroundColorCategory() {
        ListPreference preference = new StyledListPreference(requireContext(),
                FROZEN_OVERLAY_BACKGROUND_COLOR_KEY, R.string.frozen_overlay_background_color, true
        );
        BackgroundColor[] colors = BackgroundColor.values();
        String[] entries = new String[colors.length];
        String[] entryValues = new String[colors.length];
        for (int i = 0; i < colors.length; i++) {
            entryValues[i] = colors[i].name();
            entries[i] = getString(colors[i].getReadableName());
        }
        preference.setDefaultValue(BackgroundColor.DEFAULT.name());
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
        return preference;
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
        BrushStyle defaultBrush = DataCaptureDefaults.getInstance(requireContext()).getDefaultBrushStyle();
        preference.setDefaultValue(defaultBrush.name());
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
        brushPreferences.add(preference);
        return preference;
    }

    private Preference createStylesPreference() {
        ListPreference preference = new StyledListPreference(requireContext(), OVERLAY_STYLE_KEY, R.string.style, true);
        BarcodeSelectionBasicOverlayStyle[] styles = BarcodeSelectionBasicOverlayStyle.values();
        String[] entries = new String[styles.length];
        String[] entryValues = new String[styles.length];
        for (int i = 0; i < styles.length; i++) {
            String name = styles[i].name();
            entryValues[i] = name;
            entries[i] = nameCaseString(name);
        }
        BarcodeSelectionBasicOverlayStyle defaultStyle =
                DataCaptureDefaults.getInstance(requireContext()).getOverlayStyle();
        preference.setDefaultValue(defaultStyle.name());
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String defaultBrushStyle = DataCaptureDefaults.getInstance(requireContext())
                        .getDefaultBrushStyle().name();
                for (ListPreference brushPreference : brushPreferences) {
                    brushPreference.setValue(defaultBrushStyle);
                }
                return true;
            }
        });
        return preference;
    }

    private String nameCaseString(String string) {
        if (string.isEmpty()) {
            return string;
        } else if (string.length() == 1) {
            return string.toUpperCase();
        } else {
            return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
        }
    }

    public enum BrushStyle {
        DEFAULT(R.string.default_), BLUE(R.string.blue);

        private final int readableName;

        BrushStyle(@StringRes int readableName) {
            this.readableName = readableName;
        }

        public int getReadableName() {
            return readableName;
        }
    }

    public enum BackgroundColor {
        DEFAULT(R.string.default_), BLUE(R.string.blue), TRANSPARENT(R.string.transparent);

        private final int readableName;

        BackgroundColor(@StringRes int readableName) {
            this.readableName = readableName;
        }

        public int getReadableName() {
            return readableName;
        }
    }
}

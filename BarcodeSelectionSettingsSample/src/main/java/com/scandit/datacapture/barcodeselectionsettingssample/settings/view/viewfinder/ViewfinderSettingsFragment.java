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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.view.viewfinder;

import androidx.annotation.StringRes;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledListPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class ViewfinderSettingsFragment extends BasePreferenceFragment {

    public static ViewfinderSettingsFragment newInstance() {
        return new ViewfinderSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.viewfinder);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Viewfinder entries category.
        StyledPreferenceCategory sectionsCategory = new StyledPreferenceCategory(
                requireContext(), VIEWFINDER_CATEGORY_KEY, null
        );
        screen.addPreference(sectionsCategory);
        sectionsCategory.addPreference(createColorPreference(VIEWFINDER_FRAME_COLOR_KEY, R.string.frame_color));
        sectionsCategory.addPreference(createColorPreference(VIEWFINDER_DOT_COLOR_KEY, R.string.dot_color));
    }

    private Preference createColorPreference(String key, @StringRes int title) {
        ListPreference preference = new StyledListPreference(requireContext(), key, title, true);
        ViewfinderColor[] styles = ViewfinderColor.values();
        String[] entries = new String[styles.length];
        String[] entryValues = new String[styles.length];
        for (int i = 0; i < styles.length; i++) {
            entryValues[i] = styles[i].name();
            entries[i] = getString(styles[i].getReadableName());
        }
        preference.setDefaultValue(ViewfinderColor.DEFAULT.name());
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
        return preference;
    }

    public enum ViewfinderColor {
        DEFAULT(R.string.default_), BLUE(R.string.blue);

        private int readableName;

        ViewfinderColor(@StringRes int readableName) {
            this.readableName = readableName;
        }

        public int getReadableName() {
            return readableName;
        }
    }
}

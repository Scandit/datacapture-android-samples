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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.symbologies;

import android.content.SharedPreferences;

import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.symbologies.details.SymbologyDetailSettingsFragment;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class SymbologiesSettingsFragment extends BasePreferenceFragment {

    public static SymbologiesSettingsFragment newInstance() {
        return new SymbologiesSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.symbologies);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Enable/Disable all category.
        StyledPreferenceCategory enableDisableAllCategory = new StyledPreferenceCategory(
                requireContext(), ENABLE_DISABLE_ALL_CATEGORY_KEY, null
        );
        screen.addPreference(enableDisableAllCategory);
        enableDisableAllCategory.addPreference(createPreference(ENABLE_ALL_KEY, R.string.enable_all));
        enableDisableAllCategory.addPreference(createPreference(DISABLE_ALL_KEY, R.string.disable_all));

        // Symbologies categories.
        StyledPreferenceCategory symbologiesCategory = new StyledPreferenceCategory(
                requireContext(), SYMBOLOGIES_LIST_CATEGORY_KEY, null
        );
        screen.addPreference(symbologiesCategory);
        for (Symbology symbology: Symbology.values()) {
            symbologiesCategory.addPreference(createSymbologyPreference(symbology));
        }
    }

    private Preference createPreference(String key, @StringRes int title) {
        return new StyledPreference(requireContext(), key, title, true);
    }

    private Preference createSymbologyPreference(Symbology symbology) {
        return new SymbologyPreference(requireContext(), symbology);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case ENABLE_ALL_KEY: {
                enableDisableAllSymbologies(true);
                return true;
            }
            case DISABLE_ALL_KEY: {
                enableDisableAllSymbologies(false);
                return true;
            }
        }
        if (preference instanceof SymbologyPreference) {
            SymbologyPreference symbologyPreference = (SymbologyPreference) preference;
            navigateTo(SymbologyDetailSettingsFragment.newInstance(symbologyPreference.getSymbology()));
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void enableDisableAllSymbologies(boolean enabled) {
        SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
        for (Symbology symbology: Symbology.values()) {
            editor.putBoolean(SharedPrefsConstants.getSymbologyEnabledKey(symbology), enabled);
        }
        editor.apply();
    }
}

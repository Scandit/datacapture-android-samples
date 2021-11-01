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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SettingsPreferenceBuilder;

public class IdCaptureSettingsFragment extends PreferenceFragmentCompat {

    public static IdCaptureSettingsFragment create() {
        return new IdCaptureSettingsFragment();
    }

    /*
     * The settings repository. Used by the preferences to store and retrieve properties.
     */
    private final SettingsRepository settingsRepository = Injector.getInstance().getSettingsRepository();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(settingsRepository);

        /*
         * Build the desired preferences and attach them to the preference fragment.
         */
        setPreferenceScreen(
                new SettingsPreferenceBuilder(requireContext())
                        .addSection(new IdCapturePreferenceBuilder(requireContext()))
                        .build(getPreferenceManager())
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         * Update the actionBar title and navigation every time we're resuming the fragment.
         */
        setupActionBar();
    }

    private void setupActionBar() {
        /*
         * Make sure the back arrow is shown and the correct title for the fragment is displayed.
         */
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.id_capture_category_title);
    }
}

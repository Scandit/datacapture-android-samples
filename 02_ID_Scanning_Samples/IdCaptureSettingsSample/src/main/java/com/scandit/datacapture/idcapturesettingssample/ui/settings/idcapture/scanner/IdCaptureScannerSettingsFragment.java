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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.scanner;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SettingsPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.mdocelements.MdocElementsSettingsFragment;

public class IdCaptureScannerSettingsFragment extends PreferenceFragmentCompat
        implements IdCaptureScannerPreferenceBuilder.Listener {

    public static IdCaptureScannerSettingsFragment create() {
        return new IdCaptureScannerSettingsFragment();
    }

    private static final String TAG_MDOC_ELEMENTS = "mdoc_elements";

    // The settings repository. Used by the preferences to store and retrieve properties.
    private final SettingsRepository settingsRepository = Injector.getInstance().getSettingsRepository();

    private IdCaptureScannerPreferenceBuilder scannerPreferenceBuilder;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(settingsRepository);

        scannerPreferenceBuilder = new IdCaptureScannerPreferenceBuilder(requireContext());
        scannerPreferenceBuilder.setListener(this);

        // Build the desired preferences and attach them to the preference fragment.
        setPreferenceScreen(
                new SettingsPreferenceBuilder(requireContext())
                        .addSection(scannerPreferenceBuilder)
                        .build(getPreferenceManager())
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update the actionBar title and navigation every time we're resuming the fragment.
        setupActionBar();
        // Refresh preference summaries when returning from child fragments
        scannerPreferenceBuilder.refreshPreferenceSummaries(settingsRepository);

    }

    private void setupActionBar() {
        // Make sure the back arrow is shown and the correct title for the fragment is displayed.
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.id_capture_scanner_title);
        }
    }

    @Override
    public void onMdocElementsClick() {
        goToFragment(MdocElementsSettingsFragment.create(), TAG_MDOC_ELEMENTS);
    }

    private void goToFragment(Fragment fragment, String tag) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.scan_fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }
}

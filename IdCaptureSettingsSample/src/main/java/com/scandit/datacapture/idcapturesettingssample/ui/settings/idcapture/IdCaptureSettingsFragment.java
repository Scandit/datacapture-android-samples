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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SettingsPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.DocumentSelectionType;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.IdCaptureDocumentsSettingsFragment;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.scanner.IdCaptureScannerSettingsFragment;

public class IdCaptureSettingsFragment extends PreferenceFragmentCompat
        implements IdCapturePreferenceBuilder.Listener {

    private static final String TAG_SCANNERS = "SCANNERS";
    private static final String TAG_DOCUMENTS = "DOCUMENTS";

    public static IdCaptureSettingsFragment create() {
        return new IdCaptureSettingsFragment();
    }

    /*
     * The settings repository. Used by the preferences to store and retrieve properties.
     */
    private final SettingsRepository settingsRepository =
            Injector.getInstance().getSettingsRepository();

    private IdCapturePreferenceBuilder preferenceBuilder;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(settingsRepository);

        preferenceBuilder = new IdCapturePreferenceBuilder(requireContext());
        preferenceBuilder.setListener(this);

        /*
         * Build the desired preferences and attach them to the preference fragment.
         */
        setPreferenceScreen(
                new SettingsPreferenceBuilder(requireContext())
                        .addSection(preferenceBuilder)
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

        if (getPreferenceManager().getPreferenceDataStore() != null) {
            preferenceBuilder.refreshPreferenceSummaries(
                    getPreferenceManager().getPreferenceDataStore());
        }
    }

    private void setupActionBar() {
        /*
         * Make sure the back arrow is shown and the correct title for the fragment is displayed.
         */
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.id_capture_category_title);
        }
    }

    @Override
    public void onAcceptedDocumentsClick() {
        goToDocumentSettings(DocumentSelectionType.ACCEPTED);
    }

    @Override
    public void onRejectedDocumentsClick() {
        goToDocumentSettings(DocumentSelectionType.REJECTED);
    }

    @Override
    public void onScannerClick() {
        goToFragment(IdCaptureScannerSettingsFragment.create(), TAG_SCANNERS);
    }

    private void goToDocumentSettings(DocumentSelectionType documentSelectionType) {
        goToFragment(IdCaptureDocumentsSettingsFragment.create(documentSelectionType),
                TAG_DOCUMENTS);
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

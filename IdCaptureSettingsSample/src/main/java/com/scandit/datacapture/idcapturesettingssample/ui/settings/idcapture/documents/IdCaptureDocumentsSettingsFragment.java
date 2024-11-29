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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;

import com.scandit.datacapture.id.data.IdCaptureDocumentType;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SettingsPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.regions.IdCaptureRegionsSettingsFragment;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.subtypes.IdCaptureSubtypesSettingsFragment;

public class IdCaptureDocumentsSettingsFragment extends PreferenceFragmentCompat
        implements IdCaptureDocumentsPreferenceBuilder.Listener {

    private static final String TAG_REGIONS = "regions";
    private static final String TAG_SUBTYPES = "subtypes";
    private static final String KEY_DOCUMENT_SELECTION_TYPE = "DOCUMENT_SELECTION_TYPE";
    private DocumentSelectionType documentSelectionType;

    private IdCaptureDocumentsPreferenceBuilder preferenceBuilder;

    public static IdCaptureDocumentsSettingsFragment create(
            DocumentSelectionType documentSelectionType) {
        IdCaptureDocumentsSettingsFragment fragment = new IdCaptureDocumentsSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DOCUMENT_SELECTION_TYPE, documentSelectionType);
        fragment.setArguments(args);

        return fragment;
    }

    // The settings repository. Used by the preferences to store and retrieve properties.
    private final SettingsRepository settingsRepository =
            Injector.getInstance().getSettingsRepository();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(settingsRepository);

        documentSelectionType =
                (DocumentSelectionType) getArguments().get(KEY_DOCUMENT_SELECTION_TYPE);

        preferenceBuilder =
                new IdCaptureDocumentsPreferenceBuilder(requireContext(), documentSelectionType);
        preferenceBuilder.setListener(this);

        // Build the desired preferences and attach them to the preference fragment.
        setPreferenceScreen(
                new SettingsPreferenceBuilder(requireContext())
                        .addSection(preferenceBuilder)
                        .build(getPreferenceManager())
        );
        preferenceBuilder.setPreferenceDependencies(getPreferenceScreen());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update the actionBar title and navigation every time we're resuming the fragment.
        setupActionBar();

        if (getPreferenceManager().getPreferenceDataStore() != null) {
            preferenceBuilder.refreshDocumentRegions(
                    getPreferenceManager().getPreferenceDataStore(),
                    getPreferenceManager().getPreferenceScreen()
            );
        }
    }

    private void setupActionBar() {
        // Make sure the back arrow is shown and the correct title for the fragment is displayed.
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            switch (documentSelectionType) {
                case ACCEPTED:
                    actionBar.setTitle(R.string.id_capture_accepted_documents_title);
                    break;
                case REJECTED:
                    actionBar.setTitle(R.string.id_capture_rejected_documents_title);
                    break;
            }
        }
    }

    @Override
    public void onDocumentClick(IdCaptureDocumentType documentType) {
        if (documentType == IdCaptureDocumentType.REGION_SPECIFIC) {
            goToFragment(IdCaptureSubtypesSettingsFragment.create(documentSelectionType), TAG_SUBTYPES);
        } else {
            goToFragment(IdCaptureRegionsSettingsFragment.create(documentType, documentSelectionType), TAG_REGIONS);
        }
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

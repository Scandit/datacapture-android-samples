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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.regions;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.preference.PreferenceFragmentCompat;

import com.scandit.datacapture.id.data.IdCaptureDocumentType;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SettingsPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.DocumentSelectionType;

public class IdCaptureRegionsSettingsFragment extends PreferenceFragmentCompat {

    private static final String KEY_DOCUMENT_TYPE = "DOCUMENT_TYPE";
    private static final String KEY_DOCUMENT_SELECTION_TYPE = "DOCUMENT_SELECTION_TYPE";

    private IdCaptureRegionsPreferenceBuilder preferenceBuilder;

    public static IdCaptureRegionsSettingsFragment create(
            IdCaptureDocumentType type,
            DocumentSelectionType documentSelectionType) {
        IdCaptureRegionsSettingsFragment fragment = new IdCaptureRegionsSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DOCUMENT_TYPE, type);
        args.putSerializable(KEY_DOCUMENT_SELECTION_TYPE, documentSelectionType);
        fragment.setArguments(args);

        return fragment;
    }

    // The settings repository. Used by the preferences to store and retrieve properties.
    private final SettingsRepository settingsRepository =
            Injector.getInstance().getSettingsRepository();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(settingsRepository);

        IdCaptureDocumentType documentType =
                (IdCaptureDocumentType) getArguments().get(KEY_DOCUMENT_TYPE);
        DocumentSelectionType documentSelectionType =
                (DocumentSelectionType) getArguments().get(KEY_DOCUMENT_SELECTION_TYPE);

        preferenceBuilder = new IdCaptureRegionsPreferenceBuilder(requireContext(), documentType,
                documentSelectionType);

        // Build the desired preferences and attach them to the preference fragment.
        setPreferenceScreen(
                new SettingsPreferenceBuilder(requireContext())
                        .addSection(preferenceBuilder)
                        .build(getPreferenceManager())
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update the actionBar title and navigation every time we're resuming the fragment.
        setupActionBar();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.region_settings, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            setUpSearchViewListener(searchView);
        }
    }

    /**
     * Sets up the listener for user actions within the SearchView.
     */
    private void setUpSearchViewListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                preferenceBuilder.filterPreferences(newText);
                return false;
            }
        });
    }

    private void setupActionBar() {
        // Make sure the back arrow is shown and the correct title for the fragment is displayed.
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.id_capture_regions_title);
        }
    }
}

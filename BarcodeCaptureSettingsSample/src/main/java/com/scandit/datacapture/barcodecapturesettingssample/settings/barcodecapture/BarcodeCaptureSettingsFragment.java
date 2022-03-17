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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.SettingsOverviewEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes.CompositeTypesSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.duplicatefilter.CodeDuplicateFilterSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.feedback.FeedbackSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.LocationSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.symbologies.SymbologySettingsFragment;

public class BarcodeCaptureSettingsFragment extends NavigationFragment
        implements BarcodeCaptureSettingsAdapter.Callback {

    public static BarcodeCaptureSettingsFragment newInstance() {
        return new BarcodeCaptureSettingsFragment();
    }

    private BarcodeCaptureSettingsViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BarcodeCaptureSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_barcode_capture_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerOptions = view.findViewById(R.id.recycler_barcode_capture_settings);
        recyclerOptions.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerOptions.setAdapter(
                new BarcodeCaptureSettingsAdapter(viewModel.getBarcodeCaptureEntries(), this)
        );
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(SettingsOverviewEntry.BARCODE_CAPTURE.displayNameResource);
    }

    @Override
    public void onEntryClick(BarcodeCaptureSettingsEntry entry) {
        moveToDeeperSettings(entry);
    }

    private void moveToDeeperSettings(BarcodeCaptureSettingsEntry entry) {
        switch (entry) {
            case SYMBOLOGIES:
                moveToFragment(SymbologySettingsFragment.newInstance(), true, null);
                break;
            case COMPOSITE_TYPES:
                moveToFragment(CompositeTypesSettingsFragment.newInstance(), true, null);
                break;
            case LOCATION_SELECTION:
                moveToFragment(LocationSettingsFragment.newInstance(), true, null);
                break;
            case FEEDBACK:
                moveToFragment(FeedbackSettingsFragment.newInstance(), true, null);
                break;
            case CODE_DUPLICATE_FILTER:
                moveToFragment(CodeDuplicateFilterSettingsFragment.newInstance(), true, null);
                break;
        }
    }
}

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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.symbologies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.BarcodeCaptureSettingsEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.symbologies.symbology.SpecificSymbologyFragment;

public class SymbologySettingsFragment extends NavigationFragment
        implements SymbologySettingsAdapter.Callback {

    public static SymbologySettingsFragment newInstance() {
        return new SymbologySettingsFragment();
    }

    private SymbologySettingsViewModel viewModel;
    private SymbologySettingsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SymbologySettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_symbology_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_enable_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setAllSymbologiesEnabled(true);
                refreshSymbologyAdapterData();
            }
        });
        view.findViewById(R.id.button_disable_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setAllSymbologiesEnabled(false);
                refreshSymbologyAdapterData();
            }
        });

        adapter = new SymbologySettingsAdapter(
                viewModel.getSymbologyDescriptionsAndEnabledState(), this
        );
        RecyclerView recyclerSymbologies = view.findViewById(R.id.recycler_symbologies);
        recyclerSymbologies.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSymbologies.setAdapter(adapter);
    }

    private void refreshSymbologyAdapterData() {
        adapter.updateData(viewModel.getSymbologyDescriptionsAndEnabledState());
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(BarcodeCaptureSettingsEntry.SYMBOLOGIES.displayNameResource);
    }

    @Override
    public void onSymbologyClicked(SymbologyDescription symbology) {
        moveToFragment(
                SpecificSymbologyFragment.newInstance(symbology.getIdentifier()), true, null
        );
    }
}

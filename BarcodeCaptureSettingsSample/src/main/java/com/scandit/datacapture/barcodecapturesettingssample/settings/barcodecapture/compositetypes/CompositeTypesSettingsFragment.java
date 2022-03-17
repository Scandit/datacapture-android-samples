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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes;

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
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.BarcodeCaptureSettingsEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes.type.CompositeTypeEntry;

public class CompositeTypesSettingsFragment extends NavigationFragment
        implements CompositeTypesAdapter.Callback {

    public static CompositeTypesSettingsFragment newInstance() {
        return new CompositeTypesSettingsFragment();
    }

    private CompositeTypesSettingsViewModel viewModel;
    private CompositeTypesAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CompositeTypesSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_composite_types_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerCompositeTypes = view.findViewById(R.id.recycler_composite_types);
        recyclerCompositeTypes.setLayoutManager(new LinearLayoutManager(requireContext()));
        CompositeTypeEntry[] types = viewModel.getCompositeTypeEntries();
        adapter = new CompositeTypesAdapter(types, this);
        recyclerCompositeTypes.setAdapter(adapter);
    }

    private void refreshCompositeTypesAdapterData() {
        adapter.updateData(viewModel.getCompositeTypeEntries());
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(BarcodeCaptureSettingsEntry.COMPOSITE_TYPES.displayNameResource);
    }

    @Override
    public void onCompositeTypeClick(CompositeTypeEntry entry) {
        viewModel.toggleCompositeType(entry);
        refreshCompositeTypesAdapterData();
    }
}

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

package com.scandit.datacapture.barcodecapturesettingssample.settings.camera.torchstate;

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
import com.scandit.datacapture.barcodecapturesettingssample.settings.camera.torchstate.type.TorchStateType;

public class TorchStateSettingsFragment extends NavigationFragment
        implements TorchStateTypeAdapter.Callback {

    public static TorchStateSettingsFragment newInstance() {
        return new TorchStateSettingsFragment();
    }

    private TorchStateSettingsViewModel viewModel;
    private TorchStateTypeAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TorchStateSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_torch_state_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerTorchStateType = view.findViewById(R.id.recycler_torch_state_type);
        recyclerTorchStateType.setLayoutManager(new LinearLayoutManager(requireContext()));
        TorchStateType[] torchStateTypes = viewModel.getAllTorchStateTypes();
        adapter = new TorchStateTypeAdapter(torchStateTypes, this);
        recyclerTorchStateType.setAdapter(adapter);
    }

    private void refreshTorchStateTypesAdapterData() {
        adapter.updateData(viewModel.getAllTorchStateTypes());
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.torch_state);
    }

    @Override
    public void onTorchStateTypeClick(TorchStateType entry) {
        viewModel.setTorchStateType(entry);
        refreshTorchStateTypesAdapterData();
    }
}

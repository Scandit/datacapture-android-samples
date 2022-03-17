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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.controls;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.ViewSettingsEntry;

public class ControlsSettingsFragment extends NavigationFragment {

    public static ControlsSettingsFragment newInstance() {
        return new ControlsSettingsFragment();
    }

    private ControlsSettingsViewModel viewModel;

    private Switch switchZoom;
    private Switch switchTorch;
    private Switch switchCamera;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ControlsSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_controls_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchTorch = view.findViewById(R.id.switch_torch_button);
        switchCamera = view.findViewById(R.id.switch_camera_button);
        switchZoom = view.findViewById(R.id.switch_zoom_button);
        refreshSwitchTorchState();
        setupSwitchTorch();
        refreshSwitchCamera();
        setupSwitchCamera();
        refreshSwitchZoomState();
        setupSwitchZoom();
    }

    private void refreshSwitchTorchState() {
        switchTorch.setChecked(viewModel.isTorchButtonEnabled());
    }

    private void setupSwitchTorch() {
        switchTorch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setTorchButtonEnabled(isChecked);
                refreshSwitchTorchState();
            }
        });
    }

    private void refreshSwitchCamera() {
        switchCamera.setChecked(viewModel.isCameraButtonEnabled());
    }

    private void setupSwitchCamera() {
        switchCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setCameraButtonEnabled(isChecked);
                refreshSwitchCamera();
            }
        });
    }

    private void refreshSwitchZoomState() {
        switchZoom.setChecked(viewModel.isZoomButtonEnabled());
    }

    private void setupSwitchZoom() {
        switchZoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setZoomButtonEnabled(isChecked);
                refreshSwitchZoomState();
            }
        });
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(ViewSettingsEntry.CONTROLS.displayNameResource);
    }
}

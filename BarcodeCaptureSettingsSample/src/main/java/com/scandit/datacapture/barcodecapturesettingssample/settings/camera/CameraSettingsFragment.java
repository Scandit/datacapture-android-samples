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

package com.scandit.datacapture.barcodecapturesettingssample.settings.camera;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.SettingsOverviewEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.camera.torchstate.TorchStateSettingsFragment;
import com.scandit.datacapture.core.source.VideoResolution;

public class CameraSettingsFragment extends NavigationFragment
        implements CameraSettingsPositionAdapter.Callback {

    private static final float ZOOM_STEP = 0.1f;
    private static final float ZOOM_MIN = 1f;
    private static final float ZOOM_MAX = 20f;

    public static CameraSettingsFragment newInstance() {
        return new CameraSettingsFragment();
    }

    private CameraSettingsViewModel viewModel;

    private CameraSettingsPositionAdapter positionAdapter;

    private RecyclerView recyclerCameraPositions;
    private View torchEntry;
    private View containerResolution;
    private TextView textResolution, textZoomFactor;
    private SeekBar seekbarZoomFactor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CameraSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_camera_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerCameraPositions = view.findViewById(R.id.recycler_camera_positions);
        setupCameraPositionRecycler();

        torchEntry = view.findViewById(R.id.entry_torch_state);
        setupTorchEntry();

        containerResolution = view.findViewById(R.id.container_preferred_resolution);
        textResolution = view.findViewById(R.id.text_preferred_resolution);
        refreshResolutionData();
        setupResolution();

        seekbarZoomFactor = view.findViewById(R.id.seekbar_zoom_factor);
        textZoomFactor = view.findViewById(R.id.text_zoom_factor);
        setupZoomFactor();
        refreshZoomFactorData();
    }

    private void setupCameraPositionRecycler() {
        recyclerCameraPositions.setLayoutManager(new LinearLayoutManager(requireContext()));
        positionAdapter = new CameraSettingsPositionAdapter(
                viewModel.provideCameraPositionsAndEnableState(), this
        );
        recyclerCameraPositions.setAdapter(positionAdapter);
    }

    private void setupTorchEntry() {
        torchEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(TorchStateSettingsFragment.newInstance(), true, null);
            }
        });
    }

    private void setupResolution() {
        containerResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAndShowResolutionMenu();
            }
        });
    }

    private void setupZoomFactor() {
        seekbarZoomFactor.setMax((int) ((ZOOM_MAX - ZOOM_MIN) / ZOOM_STEP));
        seekbarZoomFactor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float decimalProgress = ZOOM_MIN + (progress * ZOOM_STEP);
                    viewModel.setZoomFactor(decimalProgress);
                    refreshZoomFactorData();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void buildAndShowResolutionMenu() {
        PopupMenu menu = new PopupMenu(requireContext(), containerResolution, Gravity.END);

        for (VideoResolution value : VideoResolution.values()) {
            /*
             * UHD4K is not supported in the Camera API 1. To use the Camera API 2, please contact
             * us at support@scandit.com.
             */
            if (value != VideoResolution.UHD4K) {
                menu.getMenu().add(value.name());
            }
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedResolution = item.getTitle().toString();
                viewModel.setVideoResolution(VideoResolution.valueOf(selectedResolution));
                refreshResolutionData();
                return true;
            }
        });
        menu.show();
    }

    @Override
    public void onCameraPositionClick(CameraSettingsPositionEntry cameraPositionEntry) {
        viewModel.setCameraPosition(cameraPositionEntry.cameraPosition);
        refreshCameraPositionData();
        refreshResolutionData();
        refreshZoomFactorData();
    }

    private void refreshCameraPositionData() {
        positionAdapter.updateData(viewModel.provideCameraPositionsAndEnableState());
    }

    private void refreshResolutionData() {
        textResolution.setText(viewModel.getVideoResolution().name());
    }

    private void refreshZoomFactorData() {
        float value = viewModel.getZoomFactor();
        textZoomFactor.setText(getString(R.string.size_no_unit, value));
        seekbarZoomFactor.setProgress((int) ((value - ZOOM_MIN) / ZOOM_STEP));
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(SettingsOverviewEntry.CAMERA.displayNameResource);
    }
}

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.SettingsOverviewEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.camera.torchstate.TorchStateSettingsFragment;
import com.scandit.datacapture.core.source.FocusGestureStrategy;
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
    private TextView textResolution;
    private TextView textZoomFactor;
    private TextView textZoomGestureZoomFactor;
    private SeekBar seekbarZoomFactor;
    private SeekBar seekbarZoomGestureZoomFactor;
    private View containerFocusGestureStrategy;
    private TextView textFocusGestureStrategy;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CameraSettingsViewModel.class);
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

        seekbarZoomGestureZoomFactor = view.findViewById(
                R.id.seekbar_zoom_gesture_zoom_factor);
        textZoomGestureZoomFactor = view.findViewById(R.id.text_zoom_gesture_zoom_factor);
        setupZoomGestureZoomFactor();
        refreshZoomGestureZoomFactorData();

        containerFocusGestureStrategy = view.findViewById(R.id.container_focus_gesture_strategy);
        textFocusGestureStrategy = view.findViewById(R.id.text_focus_gesture_strategy);
        refreshFocusGestureStrategyData();
        setupFocusGestureStrategy();
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
        seekbarZoomFactor.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
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
                }
        );
    }

    private void setupZoomGestureZoomFactor() {
        seekbarZoomGestureZoomFactor.setMax((int) ((ZOOM_MAX - ZOOM_MIN) / ZOOM_STEP));
        seekbarZoomGestureZoomFactor.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            float decimalProgress = ZOOM_MIN + (progress * ZOOM_STEP);
                            viewModel.setZoomGestureZoomFactor(decimalProgress);
                            refreshZoomGestureZoomFactorData();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                }
        );
    }

    private void setupFocusGestureStrategy() {
        containerFocusGestureStrategy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAndShowFocusGestureStrategyMenu();
            }
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

    private void buildAndShowFocusGestureStrategyMenu() {
        PopupMenu menu = new PopupMenu(
                requireContext(), containerFocusGestureStrategy, Gravity.END);

        for (FocusGestureStrategy value : FocusGestureStrategy.values()) {
            menu.getMenu().add(value.name());
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedStrategy = item.getTitle().toString();
                viewModel.setFocusGestureStrategy(FocusGestureStrategy.valueOf(selectedStrategy));
                refreshFocusGestureStrategyData();
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
        refreshZoomGestureZoomFactorData();
    }

    private void refreshCameraPositionData() {
        positionAdapter.updateData(viewModel.provideCameraPositionsAndEnableState());
    }

    private void refreshResolutionData() {
        textResolution.setText(viewModel.getVideoResolution().name());
    }

    private void refreshZoomFactorData() {
        refreshZoomSeekbar(viewModel.getZoomFactor(), textZoomFactor, seekbarZoomFactor);
    }

    private void refreshZoomGestureZoomFactorData() {
        refreshZoomSeekbar(viewModel.getZoomGestureZoomFactor(),
                textZoomGestureZoomFactor, seekbarZoomGestureZoomFactor);
    }

    private void refreshZoomSeekbar(float value, TextView textView, SeekBar seekBar) {
        textView.setText(getString(R.string.size_no_unit, value));
        seekBar.setProgress((int) ((value - ZOOM_MIN) / ZOOM_STEP));
    }

    private void refreshFocusGestureStrategyData() {
        textFocusGestureStrategy.setText(viewModel.getFocusGestureStrategy().name());
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

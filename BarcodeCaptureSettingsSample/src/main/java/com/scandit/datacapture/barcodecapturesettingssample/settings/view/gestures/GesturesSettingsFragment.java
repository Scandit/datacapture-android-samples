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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.gestures;

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

public class GesturesSettingsFragment extends NavigationFragment {

    public static GesturesSettingsFragment newInstance() {
        return new GesturesSettingsFragment();
    }

    private GesturesSettingsViewModel viewModel;

    private Switch switchTapToFocus;
    private Switch switchSwipeToZoom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GesturesSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_gestures_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchTapToFocus = view.findViewById(R.id.switch_tap_to_focus);
        refreshSwitchTapToFocus();
        setupTapToFocus();
        switchSwipeToZoom = view.findViewById(R.id.switch_swipe_to_zoom);
        refreshSwitchSwipeToZoom();
        setupSwipeToZoom();
    }

    private void refreshSwitchTapToFocus() {
        switchTapToFocus.setChecked(viewModel.isTapToFocusEnabled());
    }

    private void setupTapToFocus() {
        switchTapToFocus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setTapToFocusEnabled(isChecked);
                refreshSwitchTapToFocus();
            }
        });
    }

    private void refreshSwitchSwipeToZoom() {
        switchSwipeToZoom.setChecked(viewModel.isSwipeToZoomEnabled());
    }

    private void setupSwipeToZoom() {
        switchSwipeToZoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setSwipeToZoomEnabled(isChecked);
                refreshSwitchSwipeToZoom();
            }
        });
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(ViewSettingsEntry.GESTURES.displayNameResource);
    }
}

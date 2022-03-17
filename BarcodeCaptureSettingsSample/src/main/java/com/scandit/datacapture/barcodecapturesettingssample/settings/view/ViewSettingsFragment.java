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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view;

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
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.controls.ControlsSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.gestures.GesturesSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.logo.LogoSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.overlay.OverlaySettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.pointofinterest.PointOfInterestSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.scanarea.ScanAreaSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.ViewfinderSettingsFragment;

public class ViewSettingsFragment extends NavigationFragment
        implements ViewSettingsAdapter.Callback {

    public static ViewSettingsFragment newInstance() {
        return new ViewSettingsFragment();
    }

    private ViewSettingsViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ViewSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_view_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerViewOptions = view.findViewById(R.id.recycler_view_options);
        recyclerViewOptions.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewOptions.setAdapter(new ViewSettingsAdapter(viewModel.provideEntries(), this));
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(SettingsOverviewEntry.VIEW.displayNameResource);
    }

    @Override
    public void onViewEntryClicked(ViewSettingsEntry entry) {
        switch (entry) {
            case SCAN_AREA:
                moveToFragment(ScanAreaSettingsFragment.newInstance(), true, null);
                break;
            case POINT_OF_INTEREST:
                moveToFragment(PointOfInterestSettingsFragment.newInstance(), true, null);
                break;
            case OVERLAY:
                moveToFragment(OverlaySettingsFragment.newInstance(), true, null);
                break;
            case VIEWFINDER:
                moveToFragment(ViewfinderSettingsFragment.newInstance(), true, null);
                break;
            case LOGO:
                moveToFragment(LogoSettingsFragment.newInstance(), true, null);
                break;
            case GESTURES:
                moveToFragment(GesturesSettingsFragment.newInstance(), true, null);
                break;
            case CONTROLS:
                moveToFragment(ControlsSettingsFragment.newInstance(), true, null);
                break;
        }
    }
}

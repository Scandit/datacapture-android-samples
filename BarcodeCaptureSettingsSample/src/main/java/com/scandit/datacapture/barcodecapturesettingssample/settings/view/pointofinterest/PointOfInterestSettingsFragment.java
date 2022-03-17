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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.pointofinterest;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.ViewSettingsEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.pointofinterest.x.PointOfInterestXFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.pointofinterest.y.PointOfInterestYFragment;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;

public class PointOfInterestSettingsFragment extends NavigationFragment {

    public static PointOfInterestSettingsFragment newInstance() {
        return new PointOfInterestSettingsFragment();
    }

    private PointOfInterestSettingsViewModel viewModel;

    private View containerX, containerY;
    private TextView textX, textY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PointOfInterestSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_point_of_interest_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        containerX = view.findViewById(R.id.container_x);
        textX = view.findViewById(R.id.text_x);
        refreshXData();
        setupX();

        containerY = view.findViewById(R.id.container_y);
        textY = view.findViewById(R.id.text_y);
        refreshYData();
        setupY();
    }

    private void setupX() {
       containerX.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               moveToFragment(PointOfInterestXFragment.newInstance(), true, null);
           }
       });
    }

    private void setupY() {
        containerY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(PointOfInterestYFragment.newInstance(), true, null);
            }
        });
    }

    private void refreshXData() {
        FloatWithUnit x = viewModel.getPointOfInterestX();
        textX.setText(getString(R.string.size_with_unit, x.getValue(), x.getUnit()));
    }

    private void refreshYData() {
        FloatWithUnit y = viewModel.getPointOfInterestY();
        textY.setText(getString(R.string.size_with_unit, y.getValue(), y.getUnit()));
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(ViewSettingsEntry.POINT_OF_INTEREST.displayNameResource);
    }
}

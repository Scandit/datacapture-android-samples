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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.scanarea;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.ViewSettingsEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.scanarea.bottom.ScanAreaBottomMarginFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.scanarea.left.ScanAreaLeftMarginFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.scanarea.right.ScanAreaRightMarginFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.scanarea.top.ScanAreaTopMarginFragment;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;

public class ScanAreaSettingsFragment extends NavigationFragment {

    public static ScanAreaSettingsFragment newInstance() {
        return new ScanAreaSettingsFragment();
    }

    private ScanAreaSettingsViewModel viewModel;

    private View containerTopMargin, containerRightMargin, containerBottomMargin,
            containerLeftMargin;
    private TextView textTopMargin, textRightMargin, textBottomMargin, textLeftMargin;
    private Switch switchShowGuides;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ScanAreaSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_scan_area_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchShowGuides = view.findViewById(R.id.switch_enable_guides);
        refreshSwitchGuidesData();
        setupSwitchGuides();

        containerTopMargin = view.findViewById(R.id.container_top);
        textTopMargin = view.findViewById(R.id.text_top_margin);
        refreshTextTopData();
        setupTopMarginData();

        containerRightMargin = view.findViewById(R.id.container_right);
        textRightMargin = view.findViewById(R.id.text_right_margin);
        refreshTextRightData();
        setupRightMarginData();

        containerBottomMargin = view.findViewById(R.id.container_bottom);
        textBottomMargin = view.findViewById(R.id.text_bottom_margin);
        refreshTextBottomData();
        setupBottomMarginData();

        containerLeftMargin = view.findViewById(R.id.container_left);
        textLeftMargin = view.findViewById(R.id.text_left_margin);
        refreshTextLeftData();
        setupLeftMarginData();
    }

    private void setupSwitchGuides() {
        switchShowGuides.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setShowGuides(isChecked);
            }
        });
    }

    private void setupTopMarginData() {
        containerTopMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(ScanAreaTopMarginFragment.newInstance(), true, null);
            }
        });
    }

    private void setupRightMarginData() {
        containerRightMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(ScanAreaRightMarginFragment.newInstance(), true, null);
            }
        });
    }

    private void setupBottomMarginData() {
        containerBottomMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(ScanAreaBottomMarginFragment.newInstance(), true, null);
            }
        });
    }

    private void setupLeftMarginData() {
        containerLeftMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(ScanAreaLeftMarginFragment.newInstance(), true, null);
            }
        });
    }

    private void refreshSwitchGuidesData() {
        switchShowGuides.setChecked(viewModel.getShowGuides());
    }

    private void refreshTextTopData() {
        FloatWithUnit topMargin = viewModel.getTopMargin();
        textTopMargin.setText(
                getString(R.string.size_with_unit, topMargin.getValue(), topMargin.getUnit().name())
        );
    }

    private void refreshTextRightData() {
        FloatWithUnit rightMargin = viewModel.getRightMargin();
        textRightMargin.setText(
                getString(
                        R.string.size_with_unit,
                        rightMargin.getValue(),
                        rightMargin.getUnit().name()
                )
        );
    }

    private void refreshTextBottomData() {
        FloatWithUnit bottomMargin = viewModel.getBottomMargin();
        textBottomMargin.setText(
                getString(
                        R.string.size_with_unit,
                        bottomMargin.getValue(),
                        bottomMargin.getUnit().name()
                )
        );
    }

    private void refreshTextLeftData() {
        FloatWithUnit leftMargin = viewModel.getLeftMargin();
        textLeftMargin.setText(
                getString(
                        R.string.size_with_unit,
                        leftMargin.getValue(),
                        leftMargin.getUnit().name()
                )
        );
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(ViewSettingsEntry.SCAN_AREA.displayNameResource);
    }
}

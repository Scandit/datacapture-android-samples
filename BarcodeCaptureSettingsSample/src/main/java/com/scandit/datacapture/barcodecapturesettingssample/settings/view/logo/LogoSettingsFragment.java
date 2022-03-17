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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.logo;

import android.os.Bundle;
import android.view.*;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.ViewSettingsEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.logo.offsetx.OffsetXFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.logo.offsety.OffsetYFragment;
import com.scandit.datacapture.core.common.geometry.Anchor;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;

public class LogoSettingsFragment extends NavigationFragment
        implements LogoStyleAdapter.Callback {

    public static LogoSettingsFragment newInstance() {
        return new LogoSettingsFragment();
    }

    private LogoSettingsViewModel viewModel;

    private RecyclerView recyclerLogoStyles;
    private View containerAnchor, containerOffsetX, containerOffsetY;
    private TextView textAnchor, textOffsetX, textOffsetY;

    private LogoStyleAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LogoSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_logo_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerLogoStyles = view.findViewById(R.id.recycler_logo_styles);

        containerAnchor = view.findViewById(R.id.container_anchor);
        containerOffsetX = view.findViewById(R.id.container_offset_x);
        containerOffsetY = view.findViewById(R.id.container_offset_y);

        textAnchor = view.findViewById(R.id.text_anchor);
        textOffsetX = view.findViewById(R.id.text_offset_x);
        textOffsetY = view.findViewById(R.id.text_offset_y);

        setupRecyclerStyles();
        setupAnchor();
        refreshAnchorData();
        setupOffsetX();
        refreshOffsetXData();
        setupOffsetY();
        refreshOffsetYData();
    }

    private void setupRecyclerStyles() {
        adapter = new LogoStyleAdapter(viewModel.getLogoStyles(), this);
        recyclerLogoStyles.setAdapter(adapter);
    }

    private void setupAnchor() {
        containerAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAndShowAnchorMenu();
            }
        });
    }

    private void setupOffsetX() {
        containerOffsetX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(OffsetXFragment.newInstance(), true, null);
            }
        });
    }

    private void setupOffsetY() {
        containerOffsetY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(OffsetYFragment.newInstance(), true, null);
            }
        });
    }

    private void refreshAnchorData() {
        textAnchor.setText(viewModel.getCurrentAnchor().name());
    }

    private void refreshOffsetXData() {
        FloatWithUnit offsetX = viewModel.getCurrentAnchorXOffset();
        textOffsetX.setText(
                getString(R.string.size_with_unit, offsetX.getValue(), offsetX.getUnit().name())
        );
    }

    private void refreshOffsetYData() {
        FloatWithUnit offsetY = viewModel.getCurrentAnchorYOffset();
        textOffsetY.setText(
                getString(R.string.size_with_unit, offsetY.getValue(), offsetY.getUnit().name())
        );
    }

    private void buildAndShowAnchorMenu() {
        PopupMenu menu = new PopupMenu(requireContext(), containerAnchor, Gravity.END);

        final Anchor[] anchors = viewModel.provideAnchors();
        for (int i = 0; i < anchors.length; i++) {
            Anchor anchor = anchors[i];
            menu.getMenu().add(0, i, i, anchor.name());
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedAnchor = item.getItemId();
                viewModel.setAnchor(anchors[selectedAnchor]);
                refreshAnchorData();
                return true;
            }
        });
        menu.show();
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(ViewSettingsEntry.LOGO.displayNameResource);
    }

    @Override
    public void onLogoStyleClicked(LogoStyleEntry style) {
        viewModel.setCurrentStyle(style);
        refreshRecyclerStylesData();
    }

    private void refreshRecyclerStylesData() {
        adapter.updateData(viewModel.getLogoStyles());
    }
}

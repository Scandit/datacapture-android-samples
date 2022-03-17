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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.overlay;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;

public class OverlaySettingsFragment extends NavigationFragment
        implements OverlayStyleAdapter.Callback {

    public static OverlaySettingsFragment newInstance() {
        return new OverlaySettingsFragment();
    }

    private OverlaySettingsViewModel viewModel;

    private RecyclerView recyclerOverlayStyles;
    private View containerBrush;
    private TextView textBrush;

    private OverlayStyleAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(OverlaySettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_overlay_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textBrush = view.findViewById(R.id.text_brush);
        containerBrush = view.findViewById(R.id.container_brush);
        setupBrush();
        refreshBrushData();

        recyclerOverlayStyles = view.findViewById(R.id.recycler_overlay_styles);
        setupRecyclerStyles();
    }

    private void setupBrush() {
        containerBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAndShowBrushMenu();
            }
        });
    }

    private void refreshBrushData() {
        textBrush.setText(viewModel.getCurrentBrushEntry().style.displayNameRes);
    }

    private void setupRecyclerStyles() {
        adapter = new OverlayStyleAdapter(viewModel.getOverlayStyles(), this);
        recyclerOverlayStyles.setAdapter(adapter);
    }

    private void buildAndShowBrushMenu() {
        PopupMenu menu = new PopupMenu(requireContext(), containerBrush, Gravity.END);

        BrushStyleEntry[] availableBrushes = viewModel.getAvailableBrushes();
        for (int i = 0; i < availableBrushes.length; i++) {
            BrushStyleEntry value = availableBrushes[i];
            menu.getMenu().add(0, i, i, value.style.displayNameRes);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                BrushStyleEntry selectedBrushEntry =
                        viewModel.getAvailableBrushes()[item.getItemId()];
                viewModel.setCurrentBrush(selectedBrushEntry);
                refreshBrushData();
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
        return getString(R.string.overlay);
    }

    @Override
    public void onOverlayStyleClick(OverlayStyleEntry styleEntry) {
        viewModel.setCurrentStyle(styleEntry);
        refreshRecyclerStylesData();
        refreshBrushData();
    }

    private void refreshRecyclerStylesData() {
        adapter.updateData(viewModel.getOverlayStyles());
    }
}

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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.symbologies.symbology;

import android.os.Bundle;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.barcode.data.Checksum;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.core.data.Range;

public class SpecificSymbologyFragment extends NavigationFragment
        implements SymbologyExtensionsAdapter.Callback,
        SymbologyChecksumsAdapter.Callback {

    private static final String KEY_SYMBOLOGY_IDENTIFIER = "symbology_identifier";

    public static SpecificSymbologyFragment newInstance(String symbologyIdentifier) {
        SpecificSymbologyFragment fragment = new SpecificSymbologyFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SYMBOLOGY_IDENTIFIER, symbologyIdentifier);
        fragment.setArguments(args);
        return fragment;
    }

    private SpecificSymbologyViewModel viewModel;

    private Switch switchEnabled;
    private Switch switchColorInverted;
    private View containerRange;
    private View containerRangeMin;
    private View containerRangeMax;
    private View containerExtensions;
    private View containerChecksums;
    private TextView textRangeMin, textRangeMax;
    private RecyclerView recyclerExtensions;
    private RecyclerView recyclerChecksums;

    private SymbologyExtensionsAdapter extensionsAdapter;
    private SymbologyChecksumsAdapter checksumsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String symbologyIdentifier = getArguments().getString(KEY_SYMBOLOGY_IDENTIFIER, null);
            if (symbologyIdentifier == null) {
                throwNoIdentifierException();
            }
            viewModel = new SpecificSymbologyViewModel(symbologyIdentifier);
        } else {
            throwNoIdentifierException();
        }
    }

    private void throwNoIdentifierException() {
        throw new IllegalArgumentException("No identifier given to symbology fragment");
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_specific_symbology_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchEnabled = view.findViewById(R.id.switch_enabled);
        setupEnabledSwitch();

        switchColorInverted = view.findViewById(R.id.switch_color_inverted);
        setupColorInvertedSwitch();

        containerRange = view.findViewById(R.id.card_range);
        containerRangeMin = view.findViewById(R.id.container_range_min);
        containerRangeMax = view.findViewById(R.id.container_range_max);
        textRangeMin = view.findViewById(R.id.text_range_min);
        textRangeMax = view.findViewById(R.id.text_range_max);
        setupRange();

        containerExtensions = view.findViewById(R.id.card_extensions);
        recyclerExtensions = view.findViewById(R.id.recycler_extensions);
        setupExtensions();

        containerChecksums = view.findViewById(R.id.card_checksums);
        recyclerChecksums = view.findViewById(R.id.recycler_checksums);
        setupChecksums();
    }

    private void setupEnabledSwitch() {
        switchEnabled.setChecked(viewModel.isCurrentSymbologyEnabled());
        switchEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setCurrentSymbologyEnabled(isChecked);
            }
        });
    }

    private void setupColorInvertedSwitch() {
        if (viewModel.isColorInvertedSettingsAvailable()) {
            switchColorInverted.setVisibility(View.VISIBLE);
            switchColorInverted.setChecked(viewModel.isCurrentSymbologyColorInverted());
            switchColorInverted.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            viewModel.setCurrentSymbologyColorInverted(isChecked);
                        }
                    });
        }
    }

    private void setupRange() {
        if (viewModel.isRangeSettingsAvailable()) {
            containerRange.setVisibility(View.VISIBLE);
            updateRangeValues();

            containerRangeMin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildAndShowDropdownForMinRange();
                }
            });

            containerRangeMax.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildAndShowDropdownForMaxRange();
                }
            });
        }
    }

    private void setupExtensions() {
        if (viewModel.extensionsAvailable()) {
            containerExtensions.setVisibility(View.VISIBLE);
            extensionsAdapter = new SymbologyExtensionsAdapter(
                    viewModel.getExtensionsAndEnabledState(), this
            );
            recyclerExtensions.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerExtensions.setAdapter(extensionsAdapter);
        }
    }

    private void setupChecksums() {
        if (viewModel.checksumsAvailable()) {
            containerChecksums.setVisibility(View.VISIBLE);
            checksumsAdapter = new SymbologyChecksumsAdapter(
                    viewModel.getChecksumsAndEnabledState(), this
            );
            recyclerChecksums.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerChecksums.setAdapter(checksumsAdapter);
        }
    }

    private void updateRangeValues() {
        textRangeMin.setText("" + viewModel.getCurrentMinActiveSymbolCount());
        textRangeMax.setText("" + viewModel.getCurrentMaxActiveSymbolCount());
    }

    private void buildAndShowDropdownForMinRange() {
        PopupMenu menu = new PopupMenu(requireContext(), textRangeMin, Gravity.END);
        Range range = viewModel.getCompleteRange();

        // We allow selection from the minimum symbol count allowed by the symbology until the
        // currently selected maximum symbol count.
        int minAllowedSymbolCount = range.getMinimum();
        int maxAllowedSymbolCount = viewModel.getCurrentMaxActiveSymbolCount();
        int step = range.getStep();
        for (int i = minAllowedSymbolCount; i <= maxAllowedSymbolCount; i += step) {
            menu.getMenu().add(0, i, i, "" + i);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedSymbolCount = item.getItemId();
                viewModel.setCurrentMinActiveSymbolCount(selectedSymbolCount);
                updateRangeValues();
                return true;
            }
        });
        menu.show();
    }

    private void buildAndShowDropdownForMaxRange() {
        PopupMenu menu = new PopupMenu(requireContext(), textRangeMin, Gravity.END);
        Range range = viewModel.getCompleteRange();

        // We allow selection from the currently selected minimum symbol count until the maximum
        // allowed by the symbology.
        int minAllowedSymbolCount = viewModel.getCurrentMinActiveSymbolCount();
        int maxAllowedSymbolCount = range.getMaximum();
        int step = range.getStep();
        for (int i = minAllowedSymbolCount; i <= maxAllowedSymbolCount; i += step) {
            menu.getMenu().add(0, i, i, "" + i);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedSymbolCount = item.getItemId();
                viewModel.setCurrentMaxActiveSymbolCount(selectedSymbolCount);
                updateRangeValues();
                return true;
            }
        });
        menu.show();
    }

    private void refreshExtensionsAdapterData() {
        extensionsAdapter.updateData(viewModel.getExtensionsAndEnabledState());
    }

    private void refreshChecksumsAdapterData() {
        checksumsAdapter.updateData(viewModel.getChecksumsAndEnabledState());
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return viewModel.getSymbologyReadableName();
    }

    @Override
    public void onExtensionClick(String extension) {
        viewModel.toggleExtension(extension);
        refreshExtensionsAdapterData();
    }

    @Override
    public void onChecksumClick(Checksum checksum) {
        viewModel.toggleChecksum(checksum);
        refreshChecksumsAdapterData();
    }
}

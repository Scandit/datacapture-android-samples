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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location;

import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.BarcodeCaptureSettingsEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationType;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationTypeRadius;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationTypeRectangular;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.radius.LocationRadiusMeasureUnitFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.rectangular.LocationRectangularHeightMeasureUnitFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.rectangular.LocationRectangularWidthMeasureUnitFragment;
import com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;

public class LocationSettingsFragment extends NavigationFragment
        implements LocationTypeAdapter.Callback {

    public static LocationSettingsFragment newInstance() {
        return new LocationSettingsFragment();
    }

    private LocationSettingsViewModel viewModel;

    private View containerRadius, containerRectangular;
    private View containerRadiusSize, containerRectangularSpecification, containerRectangularWidth,
            containerRectangularHeight, containerRectangularWidthAspect,
            containerRectangularHeightAspect;
    private EditText editRectangularHeightAspect, editRectangularWidthAspect;
    private TextView textLocationType, textRadiusSize, textRectangularSizeSpecification,
            textRectangularWidth, textRectangularHeight;
    private LocationTypeAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LocationSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_location_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerLocationType = view.findViewById(R.id.recycler_location_type);
        recyclerLocationType.setLayoutManager(new LinearLayoutManager(requireContext()));
        LocationType[] locationTypes = viewModel.getAllowedLocationTypesAndEnabledState();
        adapter = new LocationTypeAdapter(locationTypes, this);
        recyclerLocationType.setAdapter(adapter);

        textLocationType = view.findViewById(R.id.text_location_type);

        containerRadius = view.findViewById(R.id.card_location_radius);
        containerRadiusSize = view.findViewById(R.id.radius_size_container);
        textRadiusSize = view.findViewById(R.id.text_radius_size);

        containerRectangular = view.findViewById(R.id.card_location_rectangular_size);
        containerRectangularSpecification =
                view.findViewById(R.id.container_rectangular_size_specification);
        containerRectangularWidth = view.findViewById(R.id.container_rectangular_width);
        containerRectangularHeight = view.findViewById(R.id.container_rectangular_height);
        containerRectangularWidthAspect =
                view.findViewById(R.id.container_rectangular_width_aspect);
        containerRectangularHeightAspect =
                view.findViewById(R.id.container_rectangular_height_aspect);
        editRectangularHeightAspect = view.findViewById(R.id.edit_rectangular_height);
        editRectangularWidthAspect = view.findViewById(R.id.edit_rectangular_width);
        textRectangularSizeSpecification =
                view.findViewById(R.id.text_rectangular_size_specification);
        textRectangularWidth = view.findViewById(R.id.text_rectangular_width);
        textRectangularHeight = view.findViewById(R.id.text_rectangular_height);

        setupListeners();
        showHideSubSettings();
    }

    private void refreshLocationTypesAdapterData() {
        adapter.updateData(viewModel.getAllowedLocationTypesAndEnabledState());
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(BarcodeCaptureSettingsEntry.LOCATION_SELECTION.displayNameResource);
    }

    private void setupListeners() {
        containerRadiusSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRadiusSizeFragment();
            }
        });

        containerRectangularSpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAndShowRectangularSizeSpecificationMenu();
            }
        });

        containerRectangularWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRectangularWidthFragment();
            }
        });

        containerRectangularHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRectangularHeightFragment();
            }
        });

        editRectangularWidthAspect.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    parseWidthAndApplyChange(v.getText().toString());
                    dismissKeyboard(editRectangularWidthAspect);
                    editRectangularWidthAspect.clearFocus();
                    return true;
                }
                return false;
            }
        });

        editRectangularHeightAspect.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            parseHeightAndApplyChange(v.getText().toString());
                            dismissKeyboard(editRectangularHeightAspect);
                            editRectangularHeightAspect.clearFocus();
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void goToRadiusSizeFragment() {
        moveToFragment(LocationRadiusMeasureUnitFragment.newInstance(), true, null);
    }

    private void goToRectangularWidthFragment() {
        moveToFragment(LocationRectangularWidthMeasureUnitFragment.newInstance(), true, null);
    }

    private void goToRectangularHeightFragment() {
        moveToFragment(LocationRectangularHeightMeasureUnitFragment.newInstance(), true, null);
    }

    private void showHideSubSettings() {
        LocationType locationType = viewModel.getCurrentLocationType();
        if (locationType instanceof LocationTypeRadius) {
            setupForRadiusLocationSelection((LocationTypeRadius) locationType);
        } else if (locationType instanceof LocationTypeRectangular) {
            setupForRectangularLocationSelection((LocationTypeRectangular) locationType);
        } else {
            setupForNoLocationSelection();
        }
    }

    private void setupForNoLocationSelection() {
        textLocationType.setVisibility(View.GONE);
        containerRadius.setVisibility(View.GONE);
        containerRectangular.setVisibility(View.GONE);
    }

    private void setupForRadiusLocationSelection(LocationTypeRadius location) {
        textLocationType.setVisibility(View.VISIBLE);
        containerRadius.setVisibility(View.VISIBLE);
        containerRectangular.setVisibility(View.GONE);

        refreshRadiusLocationData(location);
    }

    private void setupForRectangularLocationSelection(LocationTypeRectangular location) {
        textLocationType.setVisibility(View.VISIBLE);
        containerRadius.setVisibility(View.GONE);
        containerRectangular.setVisibility(View.VISIBLE);

        SizeSpecification spec = location.getSizeSpecification();

        switch (spec) {
            case WIDTH_AND_HEIGHT:
                containerRectangularHeight.setVisibility(View.VISIBLE);
                containerRectangularWidth.setVisibility(View.VISIBLE);
                containerRectangularHeightAspect.setVisibility(View.GONE);
                containerRectangularWidthAspect.setVisibility(View.GONE);
                break;
            case HEIGHT_AND_WIDTH_ASPECT:
                containerRectangularHeight.setVisibility(View.VISIBLE);
                containerRectangularWidth.setVisibility(View.GONE);
                containerRectangularHeightAspect.setVisibility(View.GONE);
                containerRectangularWidthAspect.setVisibility(View.VISIBLE);
                break;
            case WIDTH_AND_HEIGHT_ASPECT:
                containerRectangularHeight.setVisibility(View.GONE);
                containerRectangularWidth.setVisibility(View.VISIBLE);
                containerRectangularHeightAspect.setVisibility(View.VISIBLE);
                containerRectangularWidthAspect.setVisibility(View.GONE);
                break;
        }

        refreshRectangularLocationData(location);
    }

    private void refreshRadiusLocationData(LocationTypeRadius location) {
        textLocationType.setText(location.displayNameRes);

        textRadiusSize.setText(
                getString(R.string.size_with_unit, location.radius, location.measureUnit.name())
        );
    }

    private void refreshRectangularLocationData(LocationTypeRectangular location) {
        textLocationType.setText(location.displayNameRes);

        textRectangularSizeSpecification.setText(location.getSizeSpecification().displayName);
        FloatWithUnit width = location.getWidth();
        FloatWithUnit height = location.getHeight();
        textRectangularWidth.setText(
                getString(R.string.size_with_unit, width.getValue(), width.getUnit())
        );
        textRectangularHeight.setText(
                getString(R.string.size_with_unit, height.getValue(), height.getUnit())
        );
        editRectangularWidthAspect.setText(
                getString(R.string.size_no_unit, location.getWidthAspectRatio())
        );
        editRectangularHeightAspect.setText(
                getString(R.string.size_no_unit, location.getHeightAspectRatio())
        );
    }

    private void buildAndShowRectangularSizeSpecificationMenu() {
        PopupMenu menu = new PopupMenu(
                requireContext(), containerRectangularSpecification, Gravity.END
        );

        // LocationSelection only supports a subset of SizeSpecifications.
        SizeSpecification[] values = viewModel.getAllowedSizeSpecifications();
        for (int i = 0; i < values.length; i++) {
            SizeSpecification spec = values[i];
            menu.getMenu().add(0, i, i, spec.displayName);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedSizeSpec = item.getItemId();
                viewModel.setRectangularLocationSizeSpec(
                        SizeSpecification.values()[selectedSizeSpec]
                );
                showHideSubSettings();
                return true;
            }
        });
        menu.show();
    }

    private void parseHeightAndApplyChange(String text) {
        try {
            float parsedNumber = getTextAsPositiveFloat(text);
            viewModel.setRectangularLocationHeightAspect(parsedNumber);
            showHideSubSettings();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showInvalidNumberToast();
        }
    }

    private void parseWidthAndApplyChange(String text) {
        try {
            float parsedNumber = getTextAsPositiveFloat(text);
            viewModel.setRectangularLocationWidthAspect(parsedNumber);
            showHideSubSettings();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showInvalidNumberToast();
        }
    }

    private float getTextAsPositiveFloat(String text) throws NumberFormatException {
        float parsedNumber = Float.parseFloat(text);
        if (parsedNumber <= 0 || Float.isInfinite(parsedNumber) || Float.isNaN(parsedNumber)) {
            throw new NumberFormatException("This is not a valid number");
        } else {
            return parsedNumber;
        }
    }

    private void showInvalidNumberToast() {
        Toast.makeText(requireContext(), R.string.number_not_valid, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationTypeClick(LocationType entry) {
        viewModel.setLocationType(entry);
        showHideSubSettings();
        refreshLocationTypesAdapterData();
    }
}

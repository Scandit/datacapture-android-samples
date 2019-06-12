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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder;

import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.ViewSettingsEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.*;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.laserlinewidth.ViewfinderLaserlineWidthMeasureFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.rectangleheight.ViewfinderRectangleHeightMeasureFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.rectanglewidth.ViewfinderRectangleWidthMeasureFragment;
import com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;

public class ViewfinderSettingsFragment extends NavigationFragment
        implements ViewfinderTypeAdapter.Callback {

    public static ViewfinderSettingsFragment newInstance() {
        return new ViewfinderSettingsFragment();
    }

    private ViewfinderSettingsViewModel viewModel;

    private RecyclerView recyclerViewfinderTypes;
    private View cardColor, cardSpecifications, cardMeasures, cardLaserline;
    private View containerHeight, containerWidth, containerSizeSpec, containerHeightAspect,
            containerWidthAspect, containerColor, containerEnabledColor, containerDisabledColor,
            containerLaserlineWidth;
    private TextView textType, textColor, textSizeSpecification, textWidth, textHeight,
            textEnabledColor, textDisabledColor, textLaserlineWidth;
    private EditText editHeightAspect, editWidthAspect;

    private ViewfinderTypeAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ViewfinderSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_viewfinder_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewfinderTypes = view.findViewById(R.id.recycler_viewfinder_types);
        setupRecyclerTypes();

        cardColor = view.findViewById(R.id.card_color);
        cardSpecifications = view.findViewById(R.id.card_size_specification);
        cardMeasures = view.findViewById(R.id.card_measures);
        cardLaserline = view.findViewById(R.id.card_laserline);

        textType = view.findViewById(R.id.text_viewfinder_type);
        textColor = view.findViewById(R.id.text_color);
        textSizeSpecification = view.findViewById(R.id.text_size_specification);
        textWidth = view.findViewById(R.id.text_width);
        textHeight = view.findViewById(R.id.text_height);
        editHeightAspect = view.findViewById(R.id.edit_height);
        editWidthAspect = view.findViewById(R.id.edit_width);

        containerColor = view.findViewById(R.id.container_color);
        containerHeight = view.findViewById(R.id.container_height);
        containerWidth = view.findViewById(R.id.container_width);
        containerHeightAspect = view.findViewById(R.id.container_height_aspect);
        containerWidthAspect = view.findViewById(R.id.container_width_aspect);
        containerSizeSpec = view.findViewById(R.id.container_size_specification);

        containerLaserlineWidth = view.findViewById(R.id.container_laserline_width);
        textLaserlineWidth = view.findViewById(R.id.text_laserline_width);
        textEnabledColor = view.findViewById(R.id.text_enabled_color);
        textDisabledColor = view.findViewById(R.id.text_disabled_color);
        containerEnabledColor = view.findViewById(R.id.container_enabled_color);
        containerDisabledColor = view.findViewById(R.id.container_disabled_color);

        setupListeners();
        showHideSubSettings();
    }

    private void setupRecyclerTypes() {
        adapter = new ViewfinderTypeAdapter(viewModel.getViewfinderTypes(), this);
        recyclerViewfinderTypes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewfinderTypes.setAdapter(adapter);
    }

    private void setupListeners() {
        containerColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAndShowColorMenu();
            }
        });

        containerHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(ViewfinderRectangleHeightMeasureFragment.newInstance(), true, null);
            }
        });

        containerWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(ViewfinderRectangleWidthMeasureFragment.newInstance(), true, null);
            }
        });

        containerSizeSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAndShowSizeSpecificationMenu();
            }
        });

        editHeightAspect.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    parseHeightAndApplyChange(v.getText().toString());
                    dismissKeyboard(editHeightAspect);
                    editHeightAspect.clearFocus();
                    return true;
                }
                return false;
            }
        });

        editWidthAspect.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    parseWidthAndApplyChange(v.getText().toString());
                    dismissKeyboard(editWidthAspect);
                    editWidthAspect.clearFocus();
                    return true;
                }
                return false;
            }
        });

        containerLaserlineWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFragment(ViewfinderLaserlineWidthMeasureFragment.newInstance(), true, null);
            }
        });

        containerEnabledColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAndShowEnabledColorMenu();
            }
        });

        containerDisabledColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAndShowDisabledColorMenu();
            }
        });
    }

    private void parseHeightAndApplyChange(String text) {
        try {
            float parsedNumber = getTextAsPositiveFloat(text);
            viewModel.setRectangularViewfinderHeightAspect(parsedNumber);
            showHideSubSettings();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showInvalidNumberToast();
        }
    }

    private void parseWidthAndApplyChange(String text) {
        try {
            float parsedNumber = getTextAsPositiveFloat(text);
            viewModel.setRectangularViewfinderWidthAspect(parsedNumber);
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

    private void refreshRecyclerTypesData() {
        adapter.updateData(viewModel.getViewfinderTypes());
    }

    private void refreshRectangularViewfinderData(ViewfinderTypeRectangular viewfinder) {
        textType.setText(viewfinder.displayName);
        textColor.setText(viewfinder.getColor().displayName);
        SizeSpecification spec = viewfinder.getSizeSpecification();
        textSizeSpecification.setText(spec.displayName);

        FloatWithUnit height = viewfinder.getHeight();
        FloatWithUnit width = viewfinder.getWidth();
        refreshHeight(height);
        refreshWidth(width);
        refreshHeightAspect(viewfinder.getHeightAspect());
        refreshWidthAspect(viewfinder.getWidthAspect());
    }

    private void refreshLaserlineViewfinderData(ViewfinderTypeLaserline viewfinder) {
        textType.setText(viewfinder.displayName);
        FloatWithUnit width = viewfinder.getWidth();
        textLaserlineWidth.setText(
                getString(R.string.size_with_unit, width.getValue(), width.getUnit().name())
        );
        textEnabledColor.setText(viewfinder.getEnabledColor().displayName);
        textDisabledColor.setText(viewfinder.getDisabledColor().displayName);
    }

    private void refreshHeight(FloatWithUnit height) {
        textHeight.setText(
                getString(R.string.size_with_unit, height.getValue(), height.getUnit().name())
        );
    }

    private void refreshWidth(FloatWithUnit width) {
        textWidth.setText(
                getString(R.string.size_with_unit, width.getValue(), width.getUnit().name())
        );
    }

    private void refreshHeightAspect(float heightAspect) {
        editHeightAspect.setText(getString(R.string.size_no_unit, heightAspect));
    }

    private void refreshWidthAspect(float widthAspect) {
        editWidthAspect.setText(getString(R.string.size_no_unit, widthAspect));
    }

    private void showHideSubSettings() {
        ViewfinderType viewfinderType = viewModel.getCurrentViewfinderType();
        if (viewfinderType instanceof ViewfinderTypeNone) {
            setupForNoViewfinder();
        } else if (viewfinderType instanceof ViewfinderTypeRectangular) {
            setupForRectangularViewfinder((ViewfinderTypeRectangular) viewfinderType);
        } else if (viewfinderType instanceof ViewfinderTypeLaserline) {
            setupForLaserlineViewfinder((ViewfinderTypeLaserline) viewfinderType);
        }
    }

    private void setupForNoViewfinder() {
        textType.setVisibility(View.GONE);
        cardColor.setVisibility(View.GONE);
        cardSpecifications.setVisibility(View.GONE);
        cardMeasures.setVisibility(View.GONE);
        cardLaserline.setVisibility(View.GONE);
    }

    private void setupForRectangularViewfinder(ViewfinderTypeRectangular viewfinder) {
        textType.setVisibility(View.VISIBLE);
        cardColor.setVisibility(View.VISIBLE);
        cardSpecifications.setVisibility(View.VISIBLE);
        cardMeasures.setVisibility(View.VISIBLE);
        cardLaserline.setVisibility(View.GONE);

        SizeSpecification spec = viewfinder.getSizeSpecification();

        switch (spec) {
            case WIDTH_AND_HEIGHT:
                containerHeight.setVisibility(View.VISIBLE);
                containerWidth.setVisibility(View.VISIBLE);
                containerHeightAspect.setVisibility(View.GONE);
                containerWidthAspect.setVisibility(View.GONE);
                break;
            case HEIGHT_AND_WIDTH_ASPECT:
                containerHeight.setVisibility(View.VISIBLE);
                containerWidth.setVisibility(View.GONE);
                containerHeightAspect.setVisibility(View.GONE);
                containerWidthAspect.setVisibility(View.VISIBLE);
                break;
            case WIDTH_AND_HEIGHT_ASPECT:
                containerHeight.setVisibility(View.GONE);
                containerWidth.setVisibility(View.VISIBLE);
                containerHeightAspect.setVisibility(View.VISIBLE);
                containerWidthAspect.setVisibility(View.GONE);
                break;
        }

        refreshRectangularViewfinderData(viewfinder);
    }

    private void setupForLaserlineViewfinder(ViewfinderTypeLaserline viewfinder) {
        textType.setVisibility(View.VISIBLE);
        cardColor.setVisibility(View.GONE);
        cardSpecifications.setVisibility(View.GONE);
        cardMeasures.setVisibility(View.GONE);
        cardLaserline.setVisibility(View.VISIBLE);

        refreshLaserlineViewfinderData(viewfinder);
    }

    private void buildAndShowSizeSpecificationMenu() {
        PopupMenu menu = new PopupMenu(requireContext(), containerSizeSpec, Gravity.END);

        SizeSpecification[] values =
                SizeSpecification.values();
        for (int i = 0; i < values.length; i++) {
            SizeSpecification spec = values[i];
            menu.getMenu().add(0, i, i, spec.displayName);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedSizeSpec = item.getItemId();
                viewModel.setRectangularViewfinderSizeSpec(
                        SizeSpecification.values()[selectedSizeSpec]
                );
                showHideSubSettings();
                return true;
            }
        });
        menu.show();
    }

    private void buildAndShowColorMenu() {
        PopupMenu menu = new PopupMenu(requireContext(), containerColor, Gravity.END);

        ViewfinderTypeRectangular.Color[] values = ViewfinderTypeRectangular.Color.values();
        for (int i = 0; i < values.length; i++) {
            ViewfinderTypeRectangular.Color color = values[i];
            menu.getMenu().add(0, i, i, color.displayName);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedColor = item.getItemId();
                viewModel.setRectangularViewfinderColor(
                        ViewfinderTypeRectangular.Color.values()[selectedColor]
                );
                showHideSubSettings();
                return true;
            }
        });
        menu.show();
    }

    private void buildAndShowEnabledColorMenu() {
        PopupMenu menu = new PopupMenu(requireContext(), containerEnabledColor, Gravity.END);

        ViewfinderTypeLaserline.EnabledColor[] values =
                ViewfinderTypeLaserline.EnabledColor.values();
        for (int i = 0; i < values.length; i++) {
            ViewfinderTypeLaserline.EnabledColor color = values[i];
            menu.getMenu().add(0, i, i, color.displayName);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedColor = item.getItemId();
                viewModel.setLaserlineViewfinderEnabledColor(
                        ViewfinderTypeLaserline.EnabledColor.values()[selectedColor]
                );
                showHideSubSettings();
                return true;
            }
        });
        menu.show();
    }

    private void buildAndShowDisabledColorMenu() {
        PopupMenu menu = new PopupMenu(requireContext(), containerDisabledColor, Gravity.END);

        ViewfinderTypeLaserline.DisabledColor[] values =
                ViewfinderTypeLaserline.DisabledColor.values();
        for (int i = 0; i < values.length; i++) {
            ViewfinderTypeLaserline.DisabledColor color = values[i];
            menu.getMenu().add(0, i, i, color.displayName);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedColor = item.getItemId();
                viewModel.setLaserlineViewfinderDisabledColor(
                        ViewfinderTypeLaserline.DisabledColor.values()[selectedColor]
                );
                showHideSubSettings();
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
        return getString(ViewSettingsEntry.VIEWFINDER.displayNameResource);
    }

    @Override
    public void onViewfinderTypeClick(ViewfinderType entry) {
        viewModel.setViewfinderType(entry);
        refreshRecyclerTypesData();
        showHideSubSettings();
    }
}

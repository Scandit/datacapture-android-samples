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

package com.scandit.datacapture.vincodessample.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.scandit.datacapture.vincodessample.R;
import com.scandit.datacapture.vincodessample.data.VinResult;

public class VinResultFragment extends Fragment {
    /**
     * The view model of this fragment.
     */
    private VinResultViewModel viewModel;

    /**
     * Views to display the data decoded from  a Vehicle Identification Number (VIN). Consult
     * ScanditDataCapture documentation for the meaning of each field.
     */
    private VinResultItemView regionItem;

    private VinResultItemView fullCodeItem;

    private VinResultItemView numberOfVehiclesItem;

    private VinResultItemView vdsItem;

    private VinResultItemView minModelYearItem;

    private VinResultItemView maxModelYearItem;

    private VinResultItemView plantItem;

    private VinResultItemView wmiSuffixItem;

    private VinResultItemView serialNumberItem;

    private VinResultItemView checksumItem;

    private VinResultItemView checksumPassedItem;

    private VinResultItemView standardItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Get a reference to this fragment's view model.
         */
        viewModel = new ViewModelProvider(this).get(VinResultViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        /*
         * Initialize the layout of this fragment and find all the view it needs to interact with.
         */
        View root = inflater.inflate(R.layout.vin_result_screen, container, false);
        initToolbar(root);
        initViews(root);

        return root;
    }

    private void initToolbar(View root) {
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(R.string.app_name);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews(View root) {
        regionItem = root.findViewById(R.id.vin_region);
        fullCodeItem = root.findViewById(R.id.vin_full_code);
        numberOfVehiclesItem = root.findViewById(R.id.vin_number_of_vehicles);
        vdsItem = root.findViewById(R.id.vin_vds);
        minModelYearItem = root.findViewById(R.id.vin_min_model_year);
        maxModelYearItem = root.findViewById(R.id.vin_max_model_year);
        plantItem = root.findViewById(R.id.vin_plant);
        wmiSuffixItem = root.findViewById(R.id.vin_wmi_suffix);
        serialNumberItem = root.findViewById(R.id.vin_serial_number);
        checksumItem = root.findViewById(R.id.vin_checksum);
        checksumPassedItem = root.findViewById(R.id.vin_checksum_passed);
        standardItem = root.findViewById(R.id.vin_standard);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        /*
         * Observe the sequence of desired UI states in order to update the UI.
         */
        viewModel.vins().observe(lifecycleOwner, this::onNewVin);
    }

    /**
     * Update the displayed UI.
     */
    private void onNewVin(VinResult vin) {
        regionItem.setValueText(vin.getRegion());
        fullCodeItem.setValueText(vin.getFullCode());
        numberOfVehiclesItem.setValueText(vin.getNumberOfVehicles());
        vdsItem.setValueText(vin.getVds());
        minModelYearItem.setValueText(vin.getMinModelYear());
        maxModelYearItem.setValueText(vin.getMaxModelYear());
        plantItem.setValueText(vin.getPlant());
        wmiSuffixItem.setValueText(vin.getWmiSuffix());
        serialNumberItem.setValueText(vin.getSerialNumber());
        checksumItem.setValueText(vin.getChecksum());
        checksumPassedItem.setValueText(String.valueOf(vin.isChecksumPassed()));
        standardItem.setValueText(vin.getStandard());
    }
}

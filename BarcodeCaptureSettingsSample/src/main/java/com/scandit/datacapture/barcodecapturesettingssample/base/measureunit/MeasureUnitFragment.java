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

package com.scandit.datacapture.barcodecapturesettingssample.base.measureunit;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;

public abstract class MeasureUnitFragment extends NavigationFragment
        implements MeasureUnitAdapter.Callback {

    protected TextView textValueLabel;
    protected EditText editValue;
    protected RecyclerView recyclerMeasureUnits;

    protected MeasureUnitAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_measure_unit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textValueLabel = view.findViewById(R.id.text_value_label);
        textValueLabel.setText(R.string.value);

        editValue = view.findViewById(R.id.edit_value);
        editValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    parseTextAndApplyChange(v.getText().toString());
                    dismissKeyboard(editValue);
                    editValue.clearFocus();
                    return true;
                }
                return false;
            }
        });

        adapter = new MeasureUnitAdapter(getEntriesAndEnabledState(), this);
        recyclerMeasureUnits = view.findViewById(R.id.recycler_measure_units);
        recyclerMeasureUnits.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerMeasureUnits.setAdapter(adapter);

        displayCurrentValue();
    }

    private void parseTextAndApplyChange(String text) {
        try {
            float parsedNumber = Float.parseFloat(text);
            if (Float.isInfinite(parsedNumber) || Float.isNaN(parsedNumber)) {
                showInvalidNumberToast();
            } else {
                onValueChanged(parsedNumber);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showInvalidNumberToast();
        }
    }

    private void showInvalidNumberToast() {
        Toast.makeText(requireContext(), R.string.number_not_valid, Toast.LENGTH_LONG).show();
    }

    protected void displayCurrentValue() {
        FloatWithUnit currentFloatWithUnit = provideCurrentFloatWithUnit();
        editValue.setText(getString(R.string.size_no_unit, currentFloatWithUnit.getValue()));
    }

    protected void refreshMeasureUnitAdapterData() {
        displayCurrentValue();
        adapter.updateData(getEntriesAndEnabledState());
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    public void onMeasureUnitClick(MeasureUnitEntry measureUnitEntry) {
        onMeasureUnitChanged(measureUnitEntry.measureUnit);
    }

    private MeasureUnitEntry[] getEntriesAndEnabledState() {
        MeasureUnit currentMeasureUnit = provideCurrentFloatWithUnit().getUnit();
        MeasureUnit[] measureUnits = {MeasureUnit.DIP, MeasureUnit.FRACTION, MeasureUnit.PIXEL};
        MeasureUnitEntry[] values = new MeasureUnitEntry[measureUnits.length];

        for (int i = 0; i < measureUnits.length; i++) {
            MeasureUnit measureUnit = measureUnits[i];
            values[i] = new MeasureUnitEntry(measureUnit, measureUnit == currentMeasureUnit);
        }
        return values;
    }

    public abstract FloatWithUnit provideCurrentFloatWithUnit();

    public abstract void onValueChanged(float newValue);

    public abstract void onMeasureUnitChanged(MeasureUnit measureUnit);
}

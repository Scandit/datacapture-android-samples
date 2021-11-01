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

package com.scandit.datacapture.textcapturesample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A fragment displaying the current settings.
 */
public class SettingsFragment extends Fragment implements Spinner.OnItemSelectedListener {
    private Spinner textTypeSpinner;
    private Spinner recognitionAreaSpinner;

    private SettingsPrefs settingsPrefs;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        settingsPrefs = new SettingsPrefs(requireContext());

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);

        textTypeSpinner = root.findViewById(R.id.text_type_spinner);
        recognitionAreaSpinner = root.findViewById(R.id.recognition_area_spinner);

        initSpinnerFromResource(
                textTypeSpinner,
                R.array.text_types,
                settingsPrefs.getTextType().ordinal()
        );

        initSpinnerFromResource(
                recognitionAreaSpinner,
                R.array.recognition_areas,
                settingsPrefs.getRecognitionArea().ordinal()
        );

        return root;
    }

    private void initSpinnerFromResource(
            Spinner spinner,
            @ArrayRes int res,
            int selectedIndex
    ) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                res,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedIndex);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == textTypeSpinner) {
            TextType textType = TextType.values()[position];
            settingsPrefs.setTextType(textType);
        } else if (parent == recognitionAreaSpinner) {
            RecognitionArea recognitionArea = RecognitionArea.values()[position];
            settingsPrefs.setRecognitionArea(recognitionArea);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Not interested in this callback.
    }
}

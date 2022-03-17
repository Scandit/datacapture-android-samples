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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.duplicatefilter;

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
import androidx.lifecycle.ViewModelProvider;

import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.BarcodeCaptureSettingsEntry;

public class CodeDuplicateFilterSettingsFragment extends NavigationFragment {

    public static CodeDuplicateFilterSettingsFragment newInstance() {
        return new CodeDuplicateFilterSettingsFragment();
    }

    private CodeDuplicateFilterSettingsViewModel viewModel;
    private EditText codeDuplicateFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CodeDuplicateFilterSettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_code_duplicate_filter_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        codeDuplicateFilter = view.findViewById(R.id.edit_code_duplicate_filter);
        refreshCodeDuplicateFilter();
        setupEditTextCodeDuplicateFilter();
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(BarcodeCaptureSettingsEntry.CODE_DUPLICATE_FILTER.displayNameResource);
    }

    private void setupEditTextCodeDuplicateFilter() {
        codeDuplicateFilter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    parseTextAndApplyChange(v.getText().toString());
                    dismissKeyboard(codeDuplicateFilter);
                    codeDuplicateFilter.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private void refreshCodeDuplicateFilter() {
        codeDuplicateFilter.setText(getString(R.string.time_millis, viewModel.getCodeDuplicateFilter()));
    }

    private void parseTextAndApplyChange(String text) {
        try {
            long parsedNumber = Long.parseLong(text);
            viewModel.setCodeDuplicateFilter(parsedNumber);
            refreshCodeDuplicateFilter();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showInvalidNumberToast();
        }
    }

    private void showInvalidNumberToast() {
        Toast.makeText(requireContext(), R.string.number_not_valid, Toast.LENGTH_LONG).show();
    }
}

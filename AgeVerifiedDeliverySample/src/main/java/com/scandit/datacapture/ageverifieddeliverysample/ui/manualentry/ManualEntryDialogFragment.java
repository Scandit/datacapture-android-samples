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

package com.scandit.datacapture.ageverifieddeliverysample.ui.manualentry;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.R;
import com.scandit.datacapture.ageverifieddeliverysample.ui.id.DocumentData;
import com.scandit.datacapture.ageverifieddeliverysample.ui.id.IdScanViewModel;
import com.scandit.datacapture.ageverifieddeliverysample.ui.util.EmptyTextWatcher;
import com.scandit.datacapture.ageverifieddeliverysample.ui.util.LocalDatePickerDialogFragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * The fragment where a user may enter the information from the recipient's document, had the capture
 * process failed.
 */
public class ManualEntryDialogFragment extends BottomSheetDialogFragment {

    /**
     * The tag used by the fragment where a user may enter the information from the recipient's
     * document, had the capture process failed.
     */
    public static final String TAG = "MANUAL_ENTRY";

    /**
     * The tag that the recipient's date of birth and the document's date of expiry picker dialogs
     * use.
     */
    private static final String DATE_PICKER_TAG = "DATE_PICKER";

    /**
     * The formatter used to display the selected recipient's date of birth and document's date
     * of expiry.
     */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    /**
     * The ViewModel of the parent fragment.
     */
    private IdScanViewModel parentViewModel;

    /**
     * The ViewModel of this fragment.
     */
    private ManualEntryViewModel viewModel;

    /**
     * The state representing the currently displayed UI.
     */
    private ManualEntryUiState uiState;

    /**
     * The view that displays the entered recipient's date of birth. Click it to display the picker.
     */
    private TextView dateOfBirthText;

    /**
     * The input field for the recipient's full name.
     */
    private TextView fullNameText;

    /**
     * The view that displays the entered document's date of expiry. Click it to display the picker.
     */
    private TextView dateOfExpiryText;

    /**
     * The button to proceed with the verification process. Enabled only when the form is fully
     * filled in.
     */
    private Button confirmButton;

    /**
     * Create an instance of this fragment.
     */
    public static ManualEntryDialogFragment create() {
        return new ManualEntryDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Get a reference to this fragment's view model and it's parent's view model in order
         * to pass the manually entered data for the verification.
         */
        parentViewModel = new ViewModelProvider(requireParentFragment()).get(IdScanViewModel.class);
        viewModel = new ViewModelProvider(this).get(ManualEntryViewModel.class);
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
        View root = inflater.inflate(R.layout.manual_entry_bottom_sheet, container, false);

        dateOfBirthText = root.findViewById(R.id.date_of_birth_text);
        fullNameText = root.findViewById(R.id.full_name_text);
        dateOfExpiryText = root.findViewById(R.id.date_of_expiry_text);
        confirmButton = root.findViewById(R.id.confirm_button);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        /*
         * Observe the sequence of desired UI states in order to update the UI.
         */
        viewModel.uiStates().observe(lifecycleOwner, this::onNewUiState);

        /*
         * Observe the sequences of events in order to navigate to other screens or display dialogs.
         */
        viewModel.goToDateOfBirthPicker().observe(lifecycleOwner, this::goToDateOfBirthPicker);
        viewModel.goToDateOfExpiryPicker().observe(lifecycleOwner, this::goToDateOfExpiryPicker);
        viewModel.goToVerificationResult().observe(lifecycleOwner, this::goToVerificationResult);

        /*
         * Add the listeners necessary to interact with the UI.
         */
        dateOfBirthText.setOnClickListener(this::onDateOfBirthClicked);
        fullNameText.addTextChangedListener(new FullNameWatcher());
        dateOfExpiryText.setOnClickListener(this::onDateOfExpiryClicked);
        confirmButton.setOnClickListener(this::onConfirmClicked);
    }

    /*
     * Inform the view model that the recipient's date of birth field has been clicked. This will
     * ultimately display the picker.
     */
    private void onDateOfBirthClicked(View ignored) {
        viewModel.onDateOfBirthClicked();
    }

    /*
     * Display the picker for the recipient's date of birth. The picker displays the selected date
     * by default or today's if none selected yet.
     */
    private void goToDateOfBirthPicker(GoToDateOfBirthPicker event) {
        LocalDate selectedDate = event.getContentIfNotHandled();

        if (selectedDate != null && getChildFragmentManager().findFragmentByTag(DATE_PICKER_TAG) == null) {
            LocalDatePickerDialogFragment fragment =
                    LocalDatePickerDialogFragment.create(selectedDate, true);
            fragment.setOnDateSetListener(this::onDateOfBirthSet);
            fragment.show(getChildFragmentManager(), DATE_PICKER_TAG);
        }
    }

    /*
     * Inform the view model that the user selected the recipient's date of birth in the picker.
     */
    private void onDateOfBirthSet(LocalDate date) {
        viewModel.onDateOfBirthSet(date);
    }

    /*
     * Inform the view model that the document's date of expiry field has been clicked. This will
     * ultimately display the picker.
     */
    private void onDateOfExpiryClicked(View ignored) {
        viewModel.onDateOfExpiryClicked();
    }

    /*
     * Display the picker for the document's date of expiry. The picker displays the selected date
     * by default or today's if none selected yet.
     */
    private void goToDateOfExpiryPicker(GoToDateOfExpiryPicker event) {
        LocalDate selectedDate = event.getContentIfNotHandled();

        if (selectedDate != null && getChildFragmentManager().findFragmentByTag(DATE_PICKER_TAG) == null) {
            LocalDatePickerDialogFragment fragment =
                    LocalDatePickerDialogFragment.create(selectedDate, false);
            fragment.setOnDateSetListener(this::onDateOfExpirySet);
            fragment.show(getChildFragmentManager(), DATE_PICKER_TAG);
        }
    }

    /*
     * Inform the view model that the user selected the document's date of expiry in the picker.
     */
    private void onDateOfExpirySet(LocalDate date) {
        viewModel.onDateOfExpirySet(date);
    }

    /*
     * Inform the view model that the confirm button has been clicked. This button is enabled only
     * when the form is fully filled in.
     */
    private void onConfirmClicked(View ignored) {
        viewModel.onConfirmClicked();
    }

    /*
     * Pass the manual entry data to this fragment's parent and dismiss it.
     */
    private void goToVerificationResult(GoToVerificationResult event) {
        DocumentData data = event.getContentIfNotHandled();

        if (data != null) {
            parentViewModel.onDataEnteredManually(data);
            dismiss();
        }
    }

    /**
     * Update the displayed UI.
     */
    private void onNewUiState(ManualEntryUiState uiState) {
        this.uiState = uiState;

        updateDateOfBirth();
        updateFullName();
        updateDateOfExpiry();
        updateConfirmButton();
    }

    /**
     * Update the displayed recipient's date of birth, show the hint if none selected.
     */
    private void updateDateOfBirth() {
        if (uiState.getDateOfBirth() != null) {
            dateOfBirthText.setText(DATE_FORMATTER.format(uiState.getDateOfBirth()));
        } else {
            dateOfBirthText.setText(null);
        }
    }

    /**
     * Update the displayed recipient's full name, show the hint if empty.
     */
    private void updateFullName() {
        if (!fullNameText.getText().toString().equals(uiState.getFullName())) {
            fullNameText.setText(uiState.getFullName());
        }
    }

    /**
     * Update the displayed document's date of expiry, show the hint if none selected.
     */
    private void updateDateOfExpiry() {
        if (uiState.getDateOfExpiry() != null) {
            dateOfExpiryText.setText(DATE_FORMATTER.format(uiState.getDateOfExpiry()));
        } else {
            dateOfExpiryText.setText(null);
        }
    }

    /**
     * Update whether the confirm button is enabled.
     */
    private void updateConfirmButton() {
        confirmButton.setEnabled(uiState.isConfirmEnabled());
    }

    /**
     * The text watcher to observe the changes to the entered recipient's full name.
     */
    private class FullNameWatcher extends EmptyTextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            viewModel.onFullNameEntered(s.toString());
        }
    }
}

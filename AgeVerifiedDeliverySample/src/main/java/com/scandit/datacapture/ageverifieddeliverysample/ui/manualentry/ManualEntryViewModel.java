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

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.ageverifieddeliverysample.ui.id.DocumentData;

import java.time.LocalDate;

/**
 * The view model for the screen where the user may enter the information from
 * the recipient's document, had the capture process failed.
 */
public class ManualEntryViewModel extends ViewModel {
    /**
     * The state representing the currently displayed UI.
     */
    private ManualEntryUiState uiState = ManualEntryUiState.builder().build();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<ManualEntryUiState> uiStates = new MutableLiveData<>();

    /**
     * Events to display the recipient's date of birth picker.
     */
    private final MutableLiveData<GoToDateOfBirthPicker> goToDateOfBirthPicker = new MutableLiveData<>();

    /**
     * Events to display the document's date of expiry picker.
     */
    private final MutableLiveData<GoToDateOfExpiryPicker> goToDateOfExpiryPicker = new MutableLiveData<>();

    /**
     * Events to verify the manually entered recipient's document data.
     */
    private final MutableLiveData<GoToVerificationResult> goToVerificationResult = new MutableLiveData<>();

    public ManualEntryViewModel() {
        uiStates.postValue(uiState);
    }

    /**
     * The stream of UI states.
     */
    LiveData<ManualEntryUiState> uiStates() {
        return uiStates;
    }

    /**
     * Events to display the recipient's date of birth picker.
     */
    public LiveData<GoToDateOfBirthPicker> goToDateOfBirthPicker() {
        return goToDateOfBirthPicker;
    }

    /**
     * Events to display the document's date of expiry picker.
     */
    public LiveData<GoToDateOfExpiryPicker> goToDateOfExpiryPicker() {
        return goToDateOfExpiryPicker;
    }

    /**
     * Events to verify the manually entered recipient's document data.
     */
    public LiveData<GoToVerificationResult> goToVerificationResult() {
        return goToVerificationResult;
    }

    /**
     * Display the picker for the recipient's date of birth.
     */
    public void onDateOfBirthClicked() {
        LocalDate selectedDate;

        if (uiState.getDateOfBirth() == null) {
            selectedDate = LocalDate.now();
        } else {
            selectedDate = uiState.getDateOfBirth();
        }

        goToDateOfBirthPicker.postValue(new GoToDateOfBirthPicker(selectedDate));
    }

    /**
     * Update the displayed recipient's date of birth once it's selected in the picker. Enable
     * the confirm button if the form is completed.
     */
    public void onDateOfBirthSet(LocalDate date) {
        uiState = uiState.toBuilder()
                .dateOfBirth(date)
                .build();

        updateConfirmEnabled();

        uiStates.postValue(uiState);
    }

    /**
     * Update the displayed recipient's full name once it's entered. Enable
     * the confirm button if the form is completed.
     */
    public void onFullNameEntered(String fullName) {
        uiState = uiState.toBuilder()
                .fullName(fullName)
                .build();

        updateConfirmEnabled();

        uiStates.postValue(uiState);
    }

    /**
     * Display the picker for the document's date of expiry.
     */
    public void onDateOfExpiryClicked() {
        LocalDate selectedDate;

        if (uiState.getDateOfExpiry() == null) {
            selectedDate = LocalDate.now();
        } else {
            selectedDate = uiState.getDateOfExpiry();
        }

        goToDateOfExpiryPicker.postValue(new GoToDateOfExpiryPicker(selectedDate));
    }

    /**
     * Update the displayed document's date of expiry once it's selected in the picker. Enable
     * the confirm button if the form is completed.
     */
    public void onDateOfExpirySet(LocalDate date) {
        uiState = uiState.toBuilder()
                .dateOfExpiry(date)
                .build();

        updateConfirmEnabled();

        uiStates.postValue(uiState);
    }

    /**
     * Whenever the confirm button is clicked, collect the input and proceed with the verification.
     */
    public void onConfirmClicked() {
        DocumentData documentData = new DocumentData(
                uiState.getDateOfBirth(),
                uiState.getFullName(),
                uiState.getDateOfExpiry()
        );

        goToVerificationResult.postValue(new GoToVerificationResult(documentData));
    }

    /**
     * Update whether the confirm button is enabled in the UI.
     */
    private void updateConfirmEnabled() {
        uiState = uiState.toBuilder()
                .confirmEnabled(isFormCompleted(uiState))
                .build();
    }

    /**
     * Check whether all the required data has been filled into the manual entry form.
     */
    private boolean isFormCompleted(ManualEntryUiState state) {
        return state.getDateOfBirth() != null &&
                !TextUtils.isEmpty(state.getFullName()) &&
                state.getDateOfExpiry() != null;
    }
}

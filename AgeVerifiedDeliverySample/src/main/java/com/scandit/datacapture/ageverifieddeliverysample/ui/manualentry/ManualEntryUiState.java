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

import androidx.annotation.Nullable;

import java.time.LocalDate;

/**
 * The state of the UI for the screen where a user may enter the information from
 * the recipient's document, had the capture process failed.
 */
class ManualEntryUiState {
    /**
     * The currently entered recipient's date of birth, or null if none entered.
     */
    @Nullable
    private LocalDate dateOfBirth;

    /**
     * The currently entered recipient's full name, or empty if none entered.
     */
    private String fullName;

    /**
     * The currently entered document's date of expiry, or null if none entered.
     */
    @Nullable
    private LocalDate dateOfExpiry;

    /**
     * Whether the `confirm` button is clickable.
     */
    private boolean isConfirmEnabled = false;

    /**
     * Use `builder()` to create this UI state.
     */
    private ManualEntryUiState() {}

    /**
     * Get the currently entered recipient's date of birth, or null if none entered.
     */
    @Nullable
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Get the currently entered recipient's full name, or empty if none entered.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Get the currently entered document's date of expiry, or null if none entered.
     */
    @Nullable
    public LocalDate getDateOfExpiry() {
        return dateOfExpiry;
    }

    /**
     * Get whether the `confirm` button is clickable.
     */
    public boolean isConfirmEnabled() {
        return isConfirmEnabled;
    }

    /**
     * The builder pattern is used to create the initial UI state.
     */
    public static Builder builder() {
        return new Builder(new ManualEntryUiState());
    }

    /**
     * The UI state is immutable, turn it into a builder in order to edit it.
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * The builder to create or edit the UI state.
     */
    static class Builder {
        /**
         * The UI state being edited.
         */
        private final ManualEntryUiState state;

        /**
         * Create a new builder from the given UI state.
         */
        private Builder(ManualEntryUiState state) {
            this.state = state;
        }

        /**
         * The currently entered recipient's date of birth, or null if none entered.
         */
        public Builder dateOfBirth(LocalDate value) {
            state.dateOfBirth = value;

            return this;
        }

        /**
         * The currently entered recipient's full name, or empty if none entered.
         */
        public Builder fullName(String value) {
            state.fullName = value;

            return this;
        }

        /**
         * The currently entered document's date of expiry, or null if none entered.
         */
        public Builder dateOfExpiry(LocalDate value) {
            state.dateOfExpiry = value;

            return this;
        }

        /**
         * Whether the `confirm` button is clickable.
         */
        public Builder confirmEnabled(boolean value) {
            state.isConfirmEnabled = value;

            return this;
        }

        /**
         * Create an immutable UI state from this builder.
         */
        public ManualEntryUiState build() {
            return state;
        }
    }
}

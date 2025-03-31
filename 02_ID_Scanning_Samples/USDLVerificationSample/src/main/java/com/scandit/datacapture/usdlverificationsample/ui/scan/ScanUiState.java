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

package com.scandit.datacapture.usdlverificationsample.ui.scan;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.usdlverificationsample.R;

/**
 * The state of the UI for the screen where the user may capture an ID document.
 */
class ScanUiState {
    /**
     * The IdCapture UI to aid the user in the capture process.
     */
    @Nullable
    private IdCaptureOverlay overlay;

    /**
     * Indicate whether barcode verification is running.
     */
    private boolean isBarcodeVerificationRunning = false;

    /**
     * Use `builder()` to create this UI state.
     */
    private ScanUiState() { }

    /**
     * Get the IdCapture UI to aid the user in the capture process.
     */
    @Nullable
    public IdCaptureOverlay getOverlay() {
        return overlay;
    }

    /**
     * Indicate whether barcode verification is running.
     */
    public boolean isBarcodeVerificationRunning() {
        return isBarcodeVerificationRunning;
    }

    /**
     * The builder pattern is used to create the initial UI state.
     */
    public static Builder builder() {
        return new Builder(new ScanUiState());
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
        private final ScanUiState state;

        /**
         * Create a new builder from the given UI state.
         */
        private Builder(ScanUiState state) {
            this.state = state;
        }

        /**
         * The IdCapture UI to aid the user in the capture process.
         */
        public Builder overlay(IdCaptureOverlay value) {
            state.overlay = value;

            return this;
        }

        /**
         * Indicate whether barcode verification is running.
         */
        public Builder isBarcodeVerificationRunning(boolean value) {
            state.isBarcodeVerificationRunning = value;

            return this;
        }

        /**
         * Create an immutable UI state from this builder.
         */
        public ScanUiState build() {
            return state;
        }
    }
}


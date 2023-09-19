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

package com.scandit.datacapture.ageverifieddeliverysample.ui.barcode;

import androidx.annotation.Nullable;

import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;

/**
 * The state of the UI for the screen where the user attempts to capture the package's barcode.
 */
class BarcodeScanUiState {
    /**
     * The BarcodeCapture UI to aid the user in the capture process.
     */
    @Nullable
    private BarcodeCaptureOverlay overlay;

    /**
     * Use `builder()` to create this UI state.
     */
    private BarcodeScanUiState() { }

    /**
     * Get the BarcodeCapture UI to aid the user in the capture process.
     */
    @Nullable
    public BarcodeCaptureOverlay getOverlay() {
        return overlay;
    }

    /**
     * The builder pattern is used to create the initial UI state.
     */
    public static Builder builder() {
        return new Builder(new BarcodeScanUiState());
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
        private final BarcodeScanUiState state;

        /**
         * Create a new builder from the given UI state.
         */
        private Builder(BarcodeScanUiState state) {
            this.state = state;
        }

        /**
         * The BarcodeCapture UI to aid the user in the capture process.
         */
        public Builder overlay(BarcodeCaptureOverlay value) {
            state.overlay = value;

            return this;
        }

        /**
         * Create an immutable UI state from this builder.
         */
        public BarcodeScanUiState build() {
            return state;
        }
    }
}

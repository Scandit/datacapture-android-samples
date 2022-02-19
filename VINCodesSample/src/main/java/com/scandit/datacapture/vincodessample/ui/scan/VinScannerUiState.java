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

package com.scandit.datacapture.vincodessample.ui.scan;

import androidx.annotation.Nullable;

import com.scandit.datacapture.core.ui.overlay.DataCaptureOverlay;

/**
 * The state of the UI of the screen that allows to capture Vehicle Identification Numbers (VINs).
 */
class VinScannerUiState {
    /**
     * The additional UI to aid the user in the capture process.
     */
    @Nullable
    private DataCaptureOverlay overlay;

    /**
     * The currently selected way of capturing VINs - either capture barcodes that encode VINs or
     * OCR VINs in plain text.
     */
    private VinScannerMode mode = VinScannerMode.BARCODE;

    /**
     * Use `builder()` to create this UI state.
     */
    private VinScannerUiState() { }

    /**
     * Get the additional UI to aid the user in the capture process.
     */
    @Nullable
    public DataCaptureOverlay getOverlay() {
        return overlay;
    }

    /**
     * Get the currently selected way of capturing VINs - either capture barcodes that encode VINs or
     * OCR VINs in plain text.
     */
    public VinScannerMode getMode() {
        return mode;
    }

    /**
     * The builder pattern is used to create the initial UI state.
     */
    public static Builder builder() {
        return new Builder(new VinScannerUiState());
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
        private final VinScannerUiState state;

        /**
         * Create a new builder from the given UI state.
         */
        private Builder(VinScannerUiState state) {
            this.state = state;
        }

        /**
         * The additional UI to aid the user in the capture process.
         */
        public Builder overlay(DataCaptureOverlay value) {
            state.overlay = value;

            return this;
        }

        /**
         * The currently selected way of capturing VINs - either capture barcodes that encode VINs or
         * OCR VINs in plain text.
         */
        public Builder mode(VinScannerMode value) {
            state.mode = value;

            return this;
        }

        /**
         * Create an immutable UI state from this builder.
         */
        public VinScannerUiState build() {
            return state;
        }
    }
}

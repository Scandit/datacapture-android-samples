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

package com.scandit.datacapture.idcaptureextendedsample.ui.scan;

import androidx.annotation.Nullable;

import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.idcaptureextendedsample.data.CapturedDataType;

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
     * The kind of ID document element currently being captured. This sample offers the user
     * to capture three kinds of ID document elements - barcodes present on IDs,
     * Machine Readable Zones (MRZ) or human-readable parts of documents (VIZ).
     * By default we capture barcodes.
     */
    private CapturedDataType mode = CapturedDataType.BARCODE;

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
     * Get the kind of ID document element currently being captured. This sample offers the user
     * to capture three kinds of ID document elements - barcodes present on IDs,
     * Machine Readable Zones (MRZ) or human-readable parts of documents (VIZ).
     * By default we capture barcodes.
     */
    public CapturedDataType getMode() {
        return mode;
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
         * The kind of ID document element currently being captured. This sample offers the user
         * to capture three kinds of ID document elements - barcodes present on IDs,
         * Machine Readable Zones (MRZ) or human-readable parts of documents (VIZ).
         */
        public Builder mode(CapturedDataType value) {
            state.mode = value;

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


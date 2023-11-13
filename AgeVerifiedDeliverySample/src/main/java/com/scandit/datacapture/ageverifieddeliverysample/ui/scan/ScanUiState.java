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

package com.scandit.datacapture.ageverifieddeliverysample.ui.scan;

import android.view.View;

import androidx.annotation.Nullable;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

/**
 * The state of the UI for the screen where the user attempts to capture the recipient's document.
 */
class ScanUiState {
    /**
     * The IdCapture UI to aid the user in the capture process.
     */
    @Nullable
    private IdCaptureOverlay overlay;

    /**
     * The kind of document that the user currently attempts to capture.
     */
    private TargetDocument targetDocument = INITIAL_TARGET_DOCUMENT;

    /**
     * The side of the driver's license that the user currently attempts to capture if they attempt
     * to capture a driver's license.
     */
    private DriverLicenseSide driverLicenseSide = INITIAL_DRIVER_LICENSE_SIDE;

    /**
     * Whether the toggle between the driver's license front and back side is displayed.
     * This toggle is shown only when the user attempts to capture a driver's license.
     */
    private int driverLicenseToggleVisibility = View.VISIBLE;

    /**
     * The visibility of the hint that guides the user towards different personal identification
     * document type selection.
     */
    private int selectTargetDocumentHintVisibility = View.VISIBLE;

    /**
     * The visibility hint that informs the user that they may attempt to manually enter
     * recipient's document data.
     */
    private int enterManuallyHintVisibility = View.GONE;

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
     * Get the kind of document that the user currently attempts to capture.
     */
    public TargetDocument getTargetDocument() {
        return targetDocument;
    }

    /**
     * Get the side of the driver's license that the user currently attempts to capture if they
     * attempt to capture a driver's license.
     */
    public DriverLicenseSide getDriverLicenseSide() {
        return driverLicenseSide;
    }

    /**
     * Get whether the toggle between the driver's license front and back side is displayed.
     * This toggle is shown only when the user attempts to capture a driver's license.
     */
    public int getDriverLicenseToggleVisibility() {
        return driverLicenseToggleVisibility;
    }

    /**
     * Get the visibility of the hint that guides the user towards different personal identification
     * document type selection.
     */
    public int getSelectTargetDocumentHintVisibility() {
        return selectTargetDocumentHintVisibility;
    }

    /**
     * Get the visibility of the hint that informs the user that they may attempt to manually enter
     * recipient's document data.
     */
    public int getEnterManuallyHintVisibility() {
        return enterManuallyHintVisibility;
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
     * The initial selected target document.
     */
    public static TargetDocument INITIAL_TARGET_DOCUMENT = TargetDocument.DRIVER_LICENSE;

    /**
     * The initial selected driver license side.
     */
    public static DriverLicenseSide INITIAL_DRIVER_LICENSE_SIDE = DriverLicenseSide.BACK_BARCODE;

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
         * The kind of document that the user currently attempts to capture.
         */
        public Builder targetDocument(TargetDocument value) {
            state.targetDocument = value;

            return this;
        }

        /**
         * The side of the driver's license that the user currently attempts to capture if they attempt
         * to capture a driver's license.
         */
        public Builder driverLicenseSide(DriverLicenseSide value) {
            state.driverLicenseSide = value;

            return this;
        }

        /**
         * Whether the toggle between the driver's license front and back side is displayed.
         * This toggle is shown only when the user attempts to capture a driver's license.
         */
        public Builder driverLicenseToggleVisibility(int value) {
            state.driverLicenseToggleVisibility = value;

            return this;
        }

        /**
         * The visibility of the hint that guides the user towards different personal identification
         * document type selection.
         */
        public Builder selectTargetDocumentHintVisibility(int value) {
            state.selectTargetDocumentHintVisibility = value;

            return this;
        }

        /**
         * The visibility of the hint that informs the user that they may attempt to manually enter
         * recipient's document data.
         */
        public Builder enterManuallyHintVisibility(int value) {
            state.enterManuallyHintVisibility = value;

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

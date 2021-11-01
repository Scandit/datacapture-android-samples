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
import androidx.annotation.StringRes;

import com.scandit.datacapture.ageverifieddeliverysample.R;
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
    private TargetDocument targetDocument = TargetDocument.DRIVER_LICENSE;

    /**
     * The side of the driver's license that the user currently attempts to capture if they attempt
     * to capture a driver's license.
     */
    private DriverLicenseSide driverLicenseSide = DriverLicenseSide.BACK_BARCODE;

    /**
     * Whether the toggle between the driver's license front and back side is displayed.
     * This toggle is shown only when the user attempts to capture a driver's license.
     */
    private int driverLicenseToggleVisibility = View.VISIBLE;

    /**
     * The additional hint to aid the user with the capture process. It reflects the currently
     * selected document kind and/or side.
     */
    @StringRes
    private int scanHint = R.string.scan_hint_driver_license_barcode;

    /**
     * The status of the hint that informs the user that they may attempt to OCR the front side of
     * the recipient's driver's license.
     */
    private ScanDriverLicenseVizHintStatus scanDriverLicenseVizHintStatus =
            ScanDriverLicenseVizHintStatus.SCHEDULED;

    /**
     * The visibility of the hint that informs the user that they may attempt to OCR the front
     * side of the recipient's driver's license.
     */
    private int scanDriverLicenseVizHintVisibility = View.VISIBLE;

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
     * Get the additional hint to aid the user with the capture process. It reflects the currently
     * selected document kind and/or side.
     */
    public int getScanHint() {
        return scanHint;
    }

    /**
     * Get the status of the hint that informs the user that they may attempt to OCR the front
     * side of the recipient's driver's license.
     */
    public ScanDriverLicenseVizHintStatus getScanDriverLicenseVizHintStatus() {
        return scanDriverLicenseVizHintStatus;
    }

    /**
     * Get the visibility of the hint that informs the user that they may attempt to OCR the front
     * side of the recipient's driver's license.
     */
    public int getScanDriverLicenseVizHintVisibility() {
        return scanDriverLicenseVizHintVisibility;
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
         * The additional hint to aid the user with the capture process. It reflects the currently
         * selected document kind and/or side.
         */
        public Builder scanHint(int value) {
            state.scanHint = value;

            return this;
        }

        /**
         * The status of the hint that informs the user that they may attempt to OCR the front side of
         * the recipient's driver's license.
         */
        public Builder scanDriverLicenseVizHintStatus(ScanDriverLicenseVizHintStatus value) {
            state.scanDriverLicenseVizHintStatus = value;

            return this;
        }

        /**
         * The visibility of the hint that informs the user that they may attempt to OCR the front
         * side of the recipient's driver's license.
         */
        public Builder scanDriverLicenseVizHintVisibility(int value) {
            state.scanDriverLicenseVizHintVisibility = value;

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

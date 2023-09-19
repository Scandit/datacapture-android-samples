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

package com.scandit.datacapture.ageverifieddeliverysample.ui.id;

import androidx.annotation.Nullable;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

/**
 * The state of the UI for the screen where the user attempts to capture the recipient's document.
 */
class IdScanUiState {
    /**
     * The IdCapture UI to aid the user in the capture process.
     */
    @Nullable
    private IdCaptureOverlay overlay;

    /**
     * The number of times `onIdCaptureTimedOut` callback has been called.
     */
    private int timeoutCount = 0;

    /**
     * Use `builder()` to create this UI state.
     */
    private IdScanUiState() { }

    /**
     * Get the IdCapture UI to aid the user in the capture process.
     */
    @Nullable
    public IdCaptureOverlay getOverlay() {
        return overlay;
    }

    /**
     * Get the number of times `onIdCaptureTimedOut` callback has been called.
     */
    public int getTimeoutCount() {
        return timeoutCount;
    }

    /**
     * The builder pattern is used to create the initial UI state.
     */
    public static Builder builder() {
        return new Builder(new IdScanUiState());
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
        private final IdScanUiState state;

        /**
         * Create a new builder from the given UI state.
         */
        private Builder(IdScanUiState state) {
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
         * The number of times `onIdCaptureTimedOut` callback has been called.
         */
        public Builder timeoutCount(int value) {
            state.timeoutCount = value;

            return this;
        }

        /**
         * Create an immutable UI state from this builder.
         */
        public IdScanUiState build() {
            return state;
        }
    }
}

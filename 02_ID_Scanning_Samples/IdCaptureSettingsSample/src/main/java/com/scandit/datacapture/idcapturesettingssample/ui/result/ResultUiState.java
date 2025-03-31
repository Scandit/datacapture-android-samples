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

package com.scandit.datacapture.idcapturesettingssample.ui.result;

import androidx.annotation.Nullable;

class ResultUiState {
    @Nullable
    private CaptureResult captureResult;

    /**
     * Use `builder()` to create this UI state.
     */
    private ResultUiState() { }

    /**
     * Get the IdCapture UI to aid the user in the capture process.
     */
    public CaptureResult getCaptureResult() {
        return captureResult;
    }


    /**
     * The builder pattern is used to create the initial UI state.
     */
    public static ResultUiState.Builder builder() {
        return new ResultUiState.Builder(new ResultUiState());
    }

    /**
     * The UI state is immutable, turn it into a builder in order to edit it.
     */
    public ResultUiState.Builder toBuilder() {
        return new ResultUiState.Builder(this);
    }

    /**
     * The builder to create or edit the UI state.
     */
    static class Builder {
        /**
         * The UI state being edited.
         */
        private final ResultUiState state;

        /**
         * Create a new builder from the given UI state.
         */
        private Builder(ResultUiState state) {
            this.state = state;
        }

        /**
         * The IdCapture UI to aid the user in the capture process.
         */
        public ResultUiState.Builder captureResult(CaptureResult value) {
            state.captureResult = value;

            return this;
        }

        /**
         * Create an immutable UI state from this builder.
         */
        public ResultUiState build() {
            return state;
        }
    }
}


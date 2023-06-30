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

package com.scandit.datacapture.idcaptureextendedsample.ui.result;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The state of UI with the result of ID capture.
 */
class ResultUiState {
    /*
     * The image of the front side of the captured document, if relevant.
     */
    @Nullable
    private Bitmap idFrontImage;

    /*
     * The image of the back side of the captured document, if relevant.
     */
    @Nullable
    private Bitmap idBackImage;

    /*
     * The data extracted from the captured document.
     */
    private List<CaptureResult.Entry> data = new ArrayList<>();

    /**
     * Use `builder()` to create this UI state.
     */
    private ResultUiState() { }

    /*
     * Get the image of the front side of the captured document, if any.
     */
    @Nullable
    public Bitmap getIdFrontImage() {
        return idFrontImage;
    }

    /*
     * Get the image of the back side of the captured document, if any.
     */
    @Nullable
    public Bitmap getIdBackImage() {
        return idBackImage;
    }

    /*
     * Get the data extracted from the captured document.
     */
    @Nullable
    public List<CaptureResult.Entry> getData() {
        return new ArrayList<>(data);
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

        /*
         * The image of the front side of the captured document, if relevant.
         */
        public Builder idFrontImage(@Nullable Bitmap value) {
            state.idFrontImage = value;

            return this;
        }

        /*
         * The image of the back side of the captured document, if relevant.
         */
        public Builder idBackImage(@Nullable Bitmap value) {
            state.idBackImage = value;

            return this;
        }

        /*
         * The data extracted from the captured document.
         */
        public Builder data(Collection<CaptureResult.Entry> data) {
            state.data = new ArrayList<>(data);

            return this;
        }

        /*
         * Create an immutable UI state from this builder.
         */
        public ResultUiState build() {
            return state;
        }
    }
}

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
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ResultViewModel extends ViewModel {

    /**
     * The state representing the currently displayed UI.
     */
    private ResultUiState uiState = ResultUiState.builder().build();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<ResultUiState> uiStates = new MutableLiveData<>();

    public ResultViewModel() {
        /*
         * Post the initial UI state.
         */
        uiStates.postValue(uiState);
    }

    public void onResult(CaptureResult result) {
        uiState = uiState.toBuilder()
                .data(result.getEntries())
                .idFrontImage(convertBytesToImage(result.getIdFrontImageBytes()))
                .idBackImage(convertBytesToImage(result.getIdBackImageBytes()))
                .build();

        uiStates.postValue(uiState);
    }

    @Nullable
    private Bitmap convertBytesToImage(byte[] bytes) {
        if (bytes != null) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    /**
     * The stream of UI states.
     */
    public LiveData<ResultUiState> uiStates() {
        return uiStates;
    }
}

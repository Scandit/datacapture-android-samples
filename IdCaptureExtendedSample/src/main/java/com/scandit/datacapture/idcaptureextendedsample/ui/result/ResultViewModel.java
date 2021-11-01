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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.idcaptureextendedsample.data.IdCaptureRepository;
import com.scandit.datacapture.idcaptureextendedsample.di.Injector;
import com.scandit.datacapture.idcaptureextendedsample.mappers.AamvaResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.ArgentinaIdResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.ColombiaIdResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.MrzResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.SouthAfricaDlResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.SouthAfricaIdResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.UsUniformedServicesResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.VizResultMapper;

import java.util.List;

public class ResultViewModel extends ViewModel {
    /**
     * The repository to interact with the IdCapture mode. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final IdCaptureRepository idCaptureRepository = Injector.getInstance().getIdCaptureRepository();

    /**
     * The observer of data captured from personal identification documents or their parts.
     */
    private final Observer<CapturedId> capturedIdsObserver = this::onIdCaptured;

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
         * Observe the streams of data from the lower layer.
         */
        idCaptureRepository.capturedIds().observeForever(capturedIdsObserver);

        /*
         * Post the initial UI state.
         */
        uiStates.postValue(uiState);
    }

    @Override
    protected void onCleared() {
        /*
         * Stop observing the streams of the lower layer and the timer events to avoid memory leak.
         */
        idCaptureRepository.capturedIds().removeObserver(capturedIdsObserver);
    }

    public void onIdCaptured(CapturedId capturedId) {
        uiState = uiState.toBuilder()
                .data(toResult(capturedId))
                .faceImage(capturedId.getImageBitmapForType(IdImageType.FACE))
                .idFrontImage(capturedId.getImageBitmapForType(IdImageType.ID_FRONT))
                .idBackImage(capturedId.getImageBitmapForType(IdImageType.ID_BACK))
                .build();

        uiStates.postValue(uiState);
    }

    private List<ResultEntry> toResult(CapturedId capturedId) {
        switch (capturedId.getCapturedResultType()) {
            case AAMVA_BARCODE_RESULT:
                return new AamvaResultMapper(capturedId).mapResult();
            case COLOMBIA_ID_BARCODE_RESULT:
                return new ColombiaIdResultMapper(capturedId).mapResult();
            case ARGENTINA_ID_BARCODE_RESULT:
                return new ArgentinaIdResultMapper(capturedId).mapResult();
            case SOUTH_AFRICA_ID_BARCODE_RESULT:
                return new SouthAfricaIdResultMapper(capturedId).mapResult();
            case SOUTH_AFRICA_DL_BARCODE_RESULT:
                return new SouthAfricaDlResultMapper(capturedId).mapResult();
            case US_UNIFORMED_SERVICES_BARCODE_RESULT:
                return new UsUniformedServicesResultMapper(capturedId).mapResult();
            case MRZ_RESULT:
                return new MrzResultMapper(capturedId).mapResult();
            case VIZ_RESULT:
                return new VizResultMapper(capturedId).mapResult();
            default:
                throw new AssertionError("Unknown captured result type: " + capturedId.getCapturedResultType());
        }
    }

    /**
     * The stream of UI states.
     */
    public LiveData<ResultUiState> uiStates() {
        return uiStates;
    }
}

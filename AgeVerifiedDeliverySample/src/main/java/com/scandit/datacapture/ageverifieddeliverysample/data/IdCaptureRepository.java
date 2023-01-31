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

package com.scandit.datacapture.ageverifieddeliverysample.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.data.IdDocumentType;

/**
 * The repository to interact with the IdCapture mode.
 */
public class IdCaptureRepository {
    /**
     * The stream of IdCapture modes. We need to recreate the IdCapture every time the selected
     * document kind and/or side changes.
     */
    private final MutableLiveData<IdCapture> idCaptures = new MutableLiveData<>();

    /**
     * The DataCaptureContext that the current IdCapture is attached to.
     */
    public final DataCaptureContext dataCaptureContext;

    /**
     * The current IdCapture.
     */
    private IdCapture idCapture;

    public IdCaptureRepository(DataCaptureContext dataCaptureContext) {
        this.dataCaptureContext = dataCaptureContext;
    }

    /**
     * Get the stream of IdCapture modes. We need to recreate the IdCapture every time the selected
     * document kind and/or side changes.
     */
    public LiveData<IdCapture> idCaptures() {
        return idCaptures;
    }

    /**
     * We need to recreate the IdCapture every time the selected document kind and/or side changes.
     */
    public void recreateIdCapture(IdDocumentType document) {
        if (idCapture != null) {
            dataCaptureContext.removeMode(idCapture);
        }

        IdCaptureSettings idCaptureSettings = new IdCaptureSettings();
        idCaptureSettings.setSupportedDocuments(document);
        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, idCaptureSettings);

        idCaptures.postValue(idCapture);
    }

    /**
     * Make IdCapture process frames. This happens asynchronously.
     */
    public void enableIdCapture() {
        idCapture.setEnabled(true);
    }

    /**
     * Stop IdCapture from further processing of frames. This happens asynchronously, so some
     * results may still be delivered.
     */
    public void disableIdCapture() {
        idCapture.setEnabled(false);
    }
}

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

package com.scandit.datacapture.idcaptureextendedsample.di;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.idcaptureextendedsample.data.CameraRepository;
import com.scandit.datacapture.idcaptureextendedsample.data.DataCaptureContextProvider;
import com.scandit.datacapture.idcaptureextendedsample.data.IdCaptureRepository;

/**
 * A simple dependency injector. It may be replaced with a one from your favorite DI framework
 * like Dagger or Hilt.
 */
public class Injector {
    /**
     * The singleton instance of this dependency injector.
     */
    private static final Injector INSTANCE = new Injector();

    /**
     * Get the singleton instance of this dependency injector.
     */
    public static Injector getInstance() {
        return INSTANCE;
    }

    /**
     * The object that provides the current DataCaptureContext.
     */
    private final DataCaptureContextProvider dataCaptureContextProvider =
            new DataCaptureContextProvider();

    /**
     * The repository that allows interaction with the device's camera.
     */
    private final CameraRepository cameraRepository =
            new CameraRepository(dataCaptureContextProvider.getDataCaptureContext());

    /**
     * The repository that allows interaction with the IdCapture mode.
     */
    private final IdCaptureRepository idCaptureRepository =
            new IdCaptureRepository(dataCaptureContextProvider.getDataCaptureContext());

    private Injector() {
        // Use `getInstance()`
    }

    /**
     * Get the current DataCaptureContext.
     */
    public DataCaptureContext getDataCaptureContext() {
        return dataCaptureContextProvider.getDataCaptureContext();
    }

    /**
     * Get the repository that allows interaction with the device's camera.
     */
    public CameraRepository getCameraRepository() {
        return cameraRepository;
    }

    /**
     * Get the repository that allows interaction with the IdCapture mode.
     */
    public IdCaptureRepository getIdCaptureRepository() {
        return idCaptureRepository;
    }
}

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

package com.scandit.datacapture.idcapturesettingssample.di;

import com.scandit.datacapture.idcapturesettingssample.data.CameraRepository;
import com.scandit.datacapture.idcapturesettingssample.data.DataCaptureContextProvider;
import com.scandit.datacapture.idcapturesettingssample.data.DataCaptureViewRepository;
import com.scandit.datacapture.idcapturesettingssample.data.IdCaptureRepository;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository;

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
     * The repository that stores and returns the desired settings.
     */
    private final SettingsRepository settingsRepository = new SettingsRepository();

    /**
     * The repository that allows interaction with the device's camera.
     */
    private final CameraRepository cameraRepository = new CameraRepository(
            dataCaptureContextProvider.getDataCaptureContext(),
            settingsRepository
    );

    /**
     * The repository that allows interaction with the IdCapture mode.
     */
    private final IdCaptureRepository idCaptureRepository = new IdCaptureRepository(
            dataCaptureContextProvider.getDataCaptureContext(), settingsRepository
    );

    /**
     * The repository that allows interaction with the IdCapture mode.
     */
    private final DataCaptureViewRepository dataCaptureViewRepository = new DataCaptureViewRepository(
            dataCaptureContextProvider.getDataCaptureContext(), settingsRepository
    );

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

    /**
     * Get the repository that allows interactions with DataCaptureViews.
     */
    public DataCaptureViewRepository getDataCaptureViewRepository() {
        return dataCaptureViewRepository;
    }

    /**
     * Get the repository that allows settings to be stored and retrieved.
     */
    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }
}

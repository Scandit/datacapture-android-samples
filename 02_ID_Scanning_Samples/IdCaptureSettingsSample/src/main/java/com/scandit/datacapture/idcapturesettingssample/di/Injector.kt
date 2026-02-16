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

package com.scandit.datacapture.idcapturesettingssample.di

import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.idcapturesettingssample.data.CameraRepository
import com.scandit.datacapture.idcapturesettingssample.data.DataCaptureContextProvider
import com.scandit.datacapture.idcapturesettingssample.data.DataCaptureViewRepository
import com.scandit.datacapture.idcapturesettingssample.data.IdCaptureRepository
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository

/**
 * A simple dependency injector. It may be replaced with a one from your favorite DI framework
 * like Dagger or Hilt.
 */
object Injector {

    /**
     * Get the current DataCaptureContext.
     */
    val dataCaptureContext: DataCaptureContext
        get() = DataCaptureContextProvider.dataCaptureContext

    /**
     * The repository that stores and returns the desired settings.
     */
    val settingsRepository by lazy { SettingsRepository() }

    /**
     * The repository that allows interaction with the device's camera.
     */
    val cameraRepository by lazy { CameraRepository(dataCaptureContext, settingsRepository) }

    /**
     * The repository that allows interaction with the IdCapture mode.
     */
    val idCaptureRepository by lazy { IdCaptureRepository(dataCaptureContext, settingsRepository) }

    /**
     * The repository that allows interaction with DataCaptureViews.
     */
    val dataCaptureViewRepository by lazy {
        DataCaptureViewRepository(dataCaptureContext, settingsRepository)
    }
}

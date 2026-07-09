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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scandit.datacapture.core.source.CameraPosition
import com.scandit.datacapture.core.source.TorchState
import com.scandit.datacapture.core.source.VideoResolution
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.di.Injector
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CameraSettingsViewModel : ViewModel() {

    private val settingsRepository: SettingsRepository = Injector.settingsRepository

    val settings: StateFlow<Settings.CameraSettings> = settingsRepository.settingsStream
        .map { it.camera }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = settingsRepository.settings.camera,
        )

    fun onCameraPositionChanged(position: CameraPosition) {
        settingsRepository.updateCamera { it.copy(position = position) }
    }

    fun onTorchStateChanged(enabled: Boolean) {
        settingsRepository.updateCamera {
            it.copy(torchState = if (enabled) TorchState.ON else TorchState.OFF)
        }
    }

    fun onPreferredResolutionChanged(resolution: VideoResolution) {
        settingsRepository.updateCamera { it.copy(preferredResolution = resolution) }
    }
}

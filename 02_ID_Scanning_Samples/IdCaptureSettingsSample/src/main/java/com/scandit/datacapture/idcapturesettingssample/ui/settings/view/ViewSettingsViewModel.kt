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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scandit.datacapture.core.common.geometry.Anchor
import com.scandit.datacapture.core.common.geometry.FloatWithUnit
import com.scandit.datacapture.core.common.geometry.MeasureUnit
import com.scandit.datacapture.id.ui.IdLayoutLineStyle
import com.scandit.datacapture.id.ui.IdLayoutStyle
import com.scandit.datacapture.id.ui.TextHintPosition
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.di.Injector
import com.scandit.datacapture.idcapturesettingssample.model.BrushStyle
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ViewSettingsViewModel : ViewModel() {

    private val settingsRepository: SettingsRepository = Injector.settingsRepository

    val settings: StateFlow<Settings.ViewSettings> = settingsRepository.settingsStream
        .map { it.view }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = settingsRepository.settings.view,
        )

    fun onOverlayStyleChanged(value: IdLayoutStyle) {
        settingsRepository.updateView { it.copy(overlayStyle = value) }
    }

    fun onOverlayLineStyleChanged(value: IdLayoutLineStyle) {
        settingsRepository.updateView { it.copy(overlayLineStyle = value) }
    }

    fun onCapturedBrushChanged(value: BrushStyle) {
        settingsRepository.updateView { it.copy(capturedBrushStyle = value) }
    }

    fun onTextHintPositionChanged(value: TextHintPosition) {
        settingsRepository.updateView { it.copy(textHintPosition = value) }
    }

    fun onShowTextHintsChanged(value: Boolean) {
        settingsRepository.updateView { it.copy(showTextHints = value) }
    }

    fun onViewfinderFrontTextChanged(value: String) {
        settingsRepository.updateView { it.copy(viewfinderFrontText = value) }
    }

    fun onViewfinderBackTextChanged(value: String) {
        settingsRepository.updateView { it.copy(viewfinderBackText = value) }
    }

    fun onLogoAnchorChanged(value: Anchor) {
        settingsRepository.updateView { it.copy(logoAnchor = value) }
    }

    fun onLogoAnchorOffsetXValueChanged(value: Float) {
        settingsRepository.updateView {
            it.copy(logoAnchorOffsetX = FloatWithUnit(value, it.logoAnchorOffsetX.unit))
        }
    }

    fun onLogoAnchorOffsetXUnitChanged(value: MeasureUnit) {
        settingsRepository.updateView {
            it.copy(logoAnchorOffsetX = FloatWithUnit(it.logoAnchorOffsetX.value, value))
        }
    }

    fun onLogoAnchorOffsetYValueChanged(value: Float) {
        settingsRepository.updateView {
            it.copy(logoAnchorOffsetY = FloatWithUnit(value, it.logoAnchorOffsetY.unit))
        }
    }

    fun onLogoAnchorOffsetYUnitChanged(value: MeasureUnit) {
        settingsRepository.updateView {
            it.copy(logoAnchorOffsetY = FloatWithUnit(it.logoAnchorOffsetY.value, value))
        }
    }

    fun onTapToFocusChanged(value: Boolean) {
        settingsRepository.updateView { it.copy(isTapToFocusEnabled = value) }
    }

    fun onTorchControlChanged(value: Boolean) {
        settingsRepository.updateView { it.copy(isTorchControlEnabled = value) }
    }
}

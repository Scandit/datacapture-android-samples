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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.rejection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.di.Injector
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class IdCaptureRejectionSettingsViewModel : ViewModel() {

    private val settingsRepository: SettingsRepository = Injector.settingsRepository

    val settings: StateFlow<Settings.IdCaptureSettings> = settingsRepository.settingsStream
        .map { it.idCapture }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = settingsRepository.settings.idCapture,
        )

    fun onRejectExpiredIdsChanged(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(rejectExpiredIds = enabled) }
    }

    fun onRejectNotRealIdCompliantChanged(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(rejectNotRealIdCompliant = enabled) }
    }

    fun onRejectForgedAamvaBarcodesChanged(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(rejectForgedAamvaBarcodes = enabled) }
    }

    fun onRejectInconsistentDataChanged(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(rejectInconsistentData = enabled) }
    }

    fun onRejectHolderBelowAgeChanged(value: Int?) {
        settingsRepository.updateIdCapture { it.copy(rejectHolderBelowAge = value) }
    }

    fun onRejectIdsExpiringInDaysChanged(value: Int?) {
        settingsRepository.updateIdCapture { it.copy(rejectIdsExpiringInDays = value) }
    }

    fun onRejectVoidedIdsChanged(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(rejectVoidedIds = enabled) }
    }

    // Keep the last valid value when the input is cleared — rejectionTimeoutSeconds
    // has no meaningful "unset" state, unlike rejectHolderBelowAge / rejectIdsExpiringInDays.
    fun onRejectionTimeoutSecondsChanged(value: Int?) {
        val seconds = value ?: return
        settingsRepository.updateIdCapture { it.copy(rejectionTimeoutSeconds = seconds) }
    }
}

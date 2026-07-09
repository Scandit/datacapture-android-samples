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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scandit.datacapture.id.data.MobileDocumentDataElement
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.di.Injector
import com.scandit.datacapture.idcapturesettingssample.model.PhysicalDocumentScannerType
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class IdCaptureScannerSettingsViewModel : ViewModel() {

    private val settingsRepository: SettingsRepository = Injector.settingsRepository

    val settings: StateFlow<Settings.IdCaptureSettings> = settingsRepository.settingsStream
        .map { it.idCapture }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = settingsRepository.settings.idCapture,
        )

    fun onPhysicalDocumentScannerTypeChanged(type: PhysicalDocumentScannerType) {
        settingsRepository.updateIdCapture { it.copy(physicalDocumentScannerType = type) }
    }

    fun onSingleSideScannerBarcodeChanged(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(singleSideScannerBarcodeEnabled = enabled) }
    }

    fun onSingleSideScannerMrzChanged(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(singleSideScannerMrzEnabled = enabled) }
    }

    fun onSingleSideScannerVizChanged(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(singleSideScannerVizEnabled = enabled) }
    }

    fun onMobileScannerIso1801315Changed(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(mobileScannerIso1801315Enabled = enabled) }
    }

    fun onMobileScannerOcrChanged(enabled: Boolean) {
        settingsRepository.updateIdCapture { it.copy(mobileScannerOcrEnabled = enabled) }
    }

    fun onMdocElementsToRetainChanged(elements: Set<MobileDocumentDataElement>) {
        settingsRepository.updateIdCapture { it.copy(mdocElementsToRetain = elements) }
    }
}

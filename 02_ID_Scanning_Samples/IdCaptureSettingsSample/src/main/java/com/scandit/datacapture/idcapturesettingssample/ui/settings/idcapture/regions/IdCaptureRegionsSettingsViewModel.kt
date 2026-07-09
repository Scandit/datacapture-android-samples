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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.regions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scandit.datacapture.id.data.IdCaptureDocumentType
import com.scandit.datacapture.id.data.IdCaptureRegion
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.di.Injector
import com.scandit.datacapture.idcapturesettingssample.model.DocumentSelectionType
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class IdCaptureRegionsSettingsViewModel : ViewModel() {

    private val settingsRepository: SettingsRepository = Injector.settingsRepository

    val settings: StateFlow<Settings.IdCaptureSettings> = settingsRepository.settingsStream
        .map { it.idCapture }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = settingsRepository.settings.idCapture,
        )

    // Enforces that at least one region stays selected: unchecking the last remaining region
    // replaces it with ANY rather than leaving the document with zero regions. If the last region
    // is ANY itself, unchecking is refused outright.
    fun onRegionToggled(
        region: IdCaptureRegion,
        enabled: Boolean,
        documentType: IdCaptureDocumentType,
        selectionType: DocumentSelectionType,
    ) {
        if (enabled) {
            settingsRepository.toggleDocumentRegion(documentType, region, true, selectionType)
            return
        }

        val selectedRegions = currentlySelectedRegions(documentType, selectionType)
        val isLastSelected = selectedRegions.size <= 1

        if (isLastSelected) {
            if (region == IdCaptureRegion.ANY) return // refuse to uncheck the final ANY
            settingsRepository.toggleDocumentRegion(documentType, region, false, selectionType)
            settingsRepository.toggleDocumentRegion(documentType, IdCaptureRegion.ANY, true, selectionType)
        } else {
            settingsRepository.toggleDocumentRegion(documentType, region, false, selectionType)
        }
    }

    private fun currentlySelectedRegions(
        documentType: IdCaptureDocumentType,
        selectionType: DocumentSelectionType,
    ): Set<IdCaptureRegion> {
        val idCapture = settingsRepository.settings.idCapture
        val documents = when (selectionType) {
            DocumentSelectionType.ACCEPTED -> idCapture.acceptedDocuments
            DocumentSelectionType.REJECTED -> idCapture.rejectedDocuments
        }
        return documents
            .filter { it.documentType == documentType }
            .map { it.region }
            .toSet()
    }
}

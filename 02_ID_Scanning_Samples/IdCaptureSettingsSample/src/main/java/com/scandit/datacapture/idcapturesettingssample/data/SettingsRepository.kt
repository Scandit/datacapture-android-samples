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

package com.scandit.datacapture.idcapturesettingssample.data

import android.graphics.Color
import com.scandit.datacapture.core.ui.style.Brush
import com.scandit.datacapture.id.capture.DriverLicense
import com.scandit.datacapture.id.capture.HealthInsuranceCard
import com.scandit.datacapture.id.capture.IdCaptureDocument
import com.scandit.datacapture.id.capture.IdCard
import com.scandit.datacapture.id.capture.Passport
import com.scandit.datacapture.id.capture.RegionSpecific
import com.scandit.datacapture.id.capture.ResidencePermit
import com.scandit.datacapture.id.capture.VisaIcao
import com.scandit.datacapture.id.capture.VisaLetter
import com.scandit.datacapture.id.data.IdCaptureDocumentType
import com.scandit.datacapture.id.data.IdCaptureRegion
import com.scandit.datacapture.id.data.RegionSpecificSubtype
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay
import com.scandit.datacapture.idcapturesettingssample.model.BrushStyle
import com.scandit.datacapture.idcapturesettingssample.model.DocumentSelectionType
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The repository to store and retrieve settings. For this sample, the preference values are stored
 * in-memory.
 */
class SettingsRepository {

    private val _settingsStream = MutableStateFlow(Settings())
    val settingsStream: StateFlow<Settings> = _settingsStream.asStateFlow()

    val settings: Settings
        get() = settingsStream.value

    fun updateIdCapture(update: (Settings.IdCaptureSettings) -> Settings.IdCaptureSettings) {
        _settingsStream.update { it.copy(idCapture = update(it.idCapture)) }
    }

    fun updateCamera(update: (Settings.CameraSettings) -> Settings.CameraSettings) {
        _settingsStream.update { it.copy(camera = update(it.camera)) }
    }

    fun updateView(update: (Settings.ViewSettings) -> Settings.ViewSettings) {
        _settingsStream.update { it.copy(view = update(it.view)) }
    }

    fun updateResult(update: (Settings.ResultSettings) -> Settings.ResultSettings) {
        _settingsStream.update { it.copy(result = update(it.result)) }
    }

    /**
     * Helper function to toggle a document type in the accepted or rejected documents list.
     * When enabled, adds documents for all regions for the given document type.
     * When disabled, removes all documents of the given type.
     */
    fun toggleDocumentType(
        documentType: IdCaptureDocumentType,
        enabled: Boolean,
        selectionType: DocumentSelectionType,
    ) {
        updateDocuments(selectionType) { currentDocuments ->
            if (enabled) {
                if (currentDocuments.none { it.documentType == documentType }) {
                    val newDocuments = if (documentType == IdCaptureDocumentType.REGION_SPECIFIC) {
                        RegionSpecificSubtype.entries.map { RegionSpecific(it) }
                    } else {
                        listOf(createDocument(documentType, IdCaptureRegion.ANY))
                    }

                    currentDocuments + newDocuments
                } else {
                    currentDocuments
                }
            } else {
                // Remove all documents of this type
                currentDocuments.filterNot { it.documentType == documentType }
            }
        }
    }

    /**
     * Helper function to toggle a specific region for a document type.
     */
    fun toggleDocumentRegion(
        documentType: IdCaptureDocumentType,
        region: IdCaptureRegion,
        enabled: Boolean,
        selectionType: DocumentSelectionType,
    ) {
        updateDocuments(selectionType) { currentDocuments ->
            if (enabled) {
                // Add this specific document if not already present
                val newDocument = createDocument(documentType, region)
                if (currentDocuments.none { it.documentType == documentType && it.region == region }) {
                    currentDocuments + newDocument
                } else {
                    currentDocuments
                }
            } else {
                // Remove this specific document
                currentDocuments.filterNot { it.documentType == documentType && it.region == region }
            }
        }
    }

    /**
     * Generic helper to update documents list based on selection type.
     * Encapsulates the common pattern of extracting, transforming, and updating documents.
     */
    private fun updateDocuments(
        selectionType: DocumentSelectionType,
        transform: (List<IdCaptureDocument>) -> List<IdCaptureDocument>,
    ) {
        updateIdCapture { settings ->
            val currentDocuments = when (selectionType) {
                DocumentSelectionType.ACCEPTED -> settings.acceptedDocuments
                DocumentSelectionType.REJECTED -> settings.rejectedDocuments
            }

            val updatedDocuments = transform(currentDocuments)

            when (selectionType) {
                DocumentSelectionType.ACCEPTED -> settings.copy(
                    acceptedDocuments = updatedDocuments
                )

                DocumentSelectionType.REJECTED -> settings.copy(
                    rejectedDocuments = updatedDocuments
                )
            }
        }
    }

    /**
     * Toggle a specific RegionSpecific subtype in the accepted or rejected documents.
     */
    fun toggleRegionSpecificSubtype(
        subtype: RegionSpecificSubtype,
        enabled: Boolean,
        selectionType: DocumentSelectionType,
    ) {
        updateDocuments(selectionType) { docs ->
            if (enabled) {
                docs + RegionSpecific(subtype)
            } else {
                docs.filterNot { it is RegionSpecific && it.subtype == subtype }
            }
        }
    }

    /**
     * Enable all RegionSpecific subtypes while keeping other document types.
     */
    fun enableAllRegionSpecificSubtypes(selectionType: DocumentSelectionType) {
        updateDocuments(selectionType) { docs ->
            val nonRegionSpecific = docs.filterNot { it is RegionSpecific }
            val allRegionSpecific = RegionSpecificSubtype.entries.map { RegionSpecific(it) }
            nonRegionSpecific + allRegionSpecific
        }
    }

    /**
     * Remove all RegionSpecific subtypes while keeping other document types.
     */
    fun disableAllRegionSpecificSubtypes(selectionType: DocumentSelectionType) {
        updateDocuments(selectionType) { docs ->
            docs.filterNot { it is RegionSpecific }
        }
    }

    private fun createDocument(
        documentType: IdCaptureDocumentType,
        region: IdCaptureRegion,
    ): IdCaptureDocument = when (documentType) {
        IdCaptureDocumentType.ID_CARD -> IdCard(region)
        IdCaptureDocumentType.DRIVER_LICENSE -> DriverLicense(region)
        IdCaptureDocumentType.PASSPORT -> Passport(region)
        IdCaptureDocumentType.VISA_ICAO -> VisaIcao(region)
        IdCaptureDocumentType.RESIDENCE_PERMIT -> ResidencePermit(region)
        IdCaptureDocumentType.HEALTH_INSURANCE_CARD -> HealthInsuranceCard(region)
        IdCaptureDocumentType.VISA_LETTER -> VisaLetter(region)
        IdCaptureDocumentType.REGION_SPECIFIC -> error("RegionSpecific is not supported here.")
    }

    fun buildBrush(brushStyle: BrushStyle): Brush = when (brushStyle) {
        BrushStyle.RED -> Brush(Color.TRANSPARENT, Color.RED, 1f)
        BrushStyle.GREEN -> Brush(Color.TRANSPARENT, Color.GREEN, 1f)
        else -> IdCaptureOverlay.defaultCapturedBrush()
    }
}

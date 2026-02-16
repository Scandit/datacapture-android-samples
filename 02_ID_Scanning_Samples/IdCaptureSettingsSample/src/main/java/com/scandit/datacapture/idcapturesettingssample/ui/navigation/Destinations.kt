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

package com.scandit.datacapture.idcapturesettingssample.ui.navigation

import com.scandit.datacapture.id.data.IdCaptureDocumentType
import com.scandit.datacapture.idcapturesettingssample.model.DocumentSelectionType

object Destinations {
    const val GRAPH_ROUTE = "root"
    const val SCAN_ROUTE = "scan"
    const val RESULT_ROUTE = "result"
    const val SETTINGS_ROUTE = "settings"
    const val CAMERA_SETTINGS_ROUTE = "camera_settings"
    const val ID_CAPTURE_SETTINGS_ROUTE = "id_capture_settings"
    const val RESULT_SETTINGS_ROUTE = "result_settings"
    const val VIEW_SETTINGS_ROUTE = "view_settings"
    const val ID_CAPTURE_ANONYMIZATION_ROUTE = "id_capture_anonymization"
    const val ID_CAPTURE_DOCUMENTS_ROUTE = "id_capture_documents/{documentSelectionType}"
    const val ID_CAPTURE_REGIONS_ROUTE =
        "id_capture_regions/{documentType}/{documentSelectionType}?searchQuery={searchQuery}"
    const val ID_CAPTURE_SUBTYPES_ROUTE =
        "id_capture_subtypes/{documentSelectionType}?searchQuery={searchQuery}"
    const val ID_CAPTURE_SCANNER_ROUTE = "id_capture_scanner"
    const val ID_CAPTURE_FEEDBACK_ROUTE = "id_capture_feedback"
    const val ID_CAPTURE_REJECTION_ROUTE = "id_capture_rejection"

    fun idCaptureDocumentsRoute(documentSelectionType: DocumentSelectionType) =
        "id_capture_documents/${documentSelectionType.name}"

    fun idCaptureRegionsRoute(
        documentType: IdCaptureDocumentType, documentSelectionType: DocumentSelectionType,
    ) = "id_capture_regions/${documentType.name}/${documentSelectionType.name}"

    fun idCaptureSubtypesRoute(documentSelectionType: DocumentSelectionType) =
        "id_capture_subtypes/${documentSelectionType.name}"
}
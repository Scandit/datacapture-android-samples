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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scandit.datacapture.id.data.IdCaptureDocumentType
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.model.DocumentSelectionType
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.switchSetting
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.getSummaryForDocumentType
import com.scandit.datacapture.idcapturesettingssample.utils.toNameCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdCaptureDocumentsSettingsScreen(
    settingsRepository: SettingsRepository,
    documentSelectionType: DocumentSelectionType,
    onBackClick: () -> Unit,
    onDocumentClick: (IdCaptureDocumentType) -> Unit,
) {
    val settings by settingsRepository.settingsStream.collectAsStateWithLifecycle()

    IdCaptureDocumentsSettingsScreen(
        settings = settings,
        documentSelectionType = documentSelectionType,
        onBackClick = onBackClick,
        onDocumentTypeChecked = { documentType, enabled ->
            settingsRepository.toggleDocumentType(documentType, enabled, documentSelectionType)
        },
        onDocumentClick = onDocumentClick,
    )
}

@Composable
private fun IdCaptureDocumentsSettingsScreen(
    settings: Settings,
    documentSelectionType: DocumentSelectionType,
    onBackClick: () -> Unit,
    onDocumentTypeChecked: (IdCaptureDocumentType, Boolean) -> Unit,
    onDocumentClick: (IdCaptureDocumentType) -> Unit,
) {
    val context = LocalContext.current
    val sortedTypes = remember { IdCaptureDocumentType.entries.sortedBy { it.name } }

    val selectedDocuments = remember(settings, documentSelectionType) {
        when (documentSelectionType) {
            DocumentSelectionType.ACCEPTED -> settings.idCapture.acceptedDocuments
            DocumentSelectionType.REJECTED -> settings.idCapture.rejectedDocuments
        }
    }

    val selectedDocumentTypes = remember(selectedDocuments) {
        selectedDocuments.map { it.documentType }
    }

    val title = remember(documentSelectionType) {
        when (documentSelectionType) {
            DocumentSelectionType.ACCEPTED -> R.string.id_capture_accepted_documents_title
            DocumentSelectionType.REJECTED -> R.string.id_capture_rejected_documents_title
        }
    }

    SettingsScaffold(
        title = stringResource(title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        sortedTypes.forEach { documentType ->
            val isEnabled = selectedDocumentTypes.contains(documentType)

            switchSetting(
                key = "document_type_${documentType.name}",
                label = documentType.name.toNameCase(),
                supportingText = getSummaryForDocumentType(context, documentType, selectedDocuments),
                checked = isEnabled,
                onClick = {
                    if (isEnabled) {
                        onDocumentClick(documentType)
                    }
                },
                onCheckedChange = { enabled ->
                    onDocumentTypeChecked(documentType, enabled)
                },
            )
        }
    }
}

@Preview
@Composable
private fun IdCaptureDocumentsSettingsScreenPreview() {
    IdCaptureDocumentsSettingsScreen(
        settings = Settings(),
        documentSelectionType = DocumentSelectionType.ACCEPTED,
        onBackClick = {},
        onDocumentTypeChecked = { _, _ -> },
        onDocumentClick = {},
    )
}

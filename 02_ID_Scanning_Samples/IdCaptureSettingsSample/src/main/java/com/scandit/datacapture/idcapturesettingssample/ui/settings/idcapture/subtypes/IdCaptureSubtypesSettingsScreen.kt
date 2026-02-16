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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.subtypes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scandit.datacapture.id.capture.RegionSpecific
import com.scandit.datacapture.id.data.RegionSpecificSubtype
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.checkboxSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.searchBar
import com.scandit.datacapture.idcapturesettingssample.ui.component.setting
import com.scandit.datacapture.idcapturesettingssample.model.DocumentSelectionType
import com.scandit.datacapture.idcapturesettingssample.utils.toNameCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdCaptureSubtypesSettingsScreen(
    settingsRepository: SettingsRepository,
    documentSelectionType: DocumentSelectionType,
    onBackClick: () -> Unit,
) {
    val settings by settingsRepository.settingsStream.collectAsStateWithLifecycle()

    IdCaptureSubtypesSettingsScreen(
        settings = settings,
        documentSelectionType = documentSelectionType,
        onBackClick = onBackClick,
        onSubtypeChanged = { subtype, enabled ->
            settingsRepository.toggleRegionSpecificSubtype(subtype, enabled, documentSelectionType)
        },
        onEnableAll = {
            settingsRepository.enableAllRegionSpecificSubtypes(documentSelectionType)
        },
        onDisableAll = {
            settingsRepository.disableAllRegionSpecificSubtypes(documentSelectionType)
        }
    )
}

@Composable
private fun IdCaptureSubtypesSettingsScreen(
    settings: Settings,
    documentSelectionType: DocumentSelectionType,
    onBackClick: () -> Unit,
    onSubtypeChanged: (RegionSpecificSubtype, Boolean) -> Unit,
    onEnableAll: () -> Unit,
    onDisableAll: () -> Unit,
) {
    val selectedSubtypes = remember(settings, documentSelectionType) {
        val documents = when (documentSelectionType) {
            DocumentSelectionType.ACCEPTED -> settings.idCapture.acceptedDocuments
            DocumentSelectionType.REJECTED -> settings.idCapture.rejectedDocuments
        }

        documents
            .filterIsInstance<RegionSpecific>()
            .map { it.subtype }
            .toSet()
    }

    val sortedSubtypes = remember {
        RegionSpecificSubtype.entries.sortedBy { it.name }
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredSubtypes = remember(sortedSubtypes, searchQuery) {
        if (searchQuery.isEmpty()) {
            sortedSubtypes
        } else {
            sortedSubtypes.filter { subtype ->
                subtype.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    SettingsScaffold(
        title = stringResource(R.string.id_capture_subtypes_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        searchBar(
            key = "subtype_search",
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
        )
        setting(
            key = "enabled_all_button",
            labelRes = R.string.id_capture_subtype_enable_all_title,
            onClick = onEnableAll
        )

        setting(
            key = "disable_all_button",
            labelRes = R.string.id_capture_subtype_disable_all_title,
            onClick = onDisableAll
        )

        filteredSubtypes.forEach { subtype ->
            checkboxSetting(
                key = "subtype_${subtype.name}",
                label = subtype.name.toNameCase(),
                checked = selectedSubtypes.contains(subtype),
                onCheckedChange = { enabled ->
                    onSubtypeChanged(subtype, enabled)
                }
            )
        }
    }
}

@Preview
@Composable
private fun IdCaptureSubtypesSettingsScreenPreview() {
    IdCaptureSubtypesSettingsScreen(
        settings = Settings(),
        documentSelectionType = DocumentSelectionType.ACCEPTED,
        onBackClick = {},
        onSubtypeChanged = { _, _ -> },
        onEnableAll = {},
        onDisableAll = {}
    )
}

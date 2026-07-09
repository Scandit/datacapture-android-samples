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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scandit.datacapture.id.capture.description
import com.scandit.datacapture.id.data.IdCaptureDocumentType
import com.scandit.datacapture.id.data.IdCaptureRegion
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.DocumentSelectionType
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.checkboxSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.searchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdCaptureRegionsSettingsScreen(
    documentType: IdCaptureDocumentType,
    documentSelectionType: DocumentSelectionType,
    onBackClick: () -> Unit,
    viewModel: IdCaptureRegionsSettingsViewModel = viewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    IdCaptureRegionsSettingsScreen(
        settings = settings,
        documentType = documentType,
        documentSelectionType = documentSelectionType,
        onBackClick = onBackClick,
        onRegionChanged = { region, enabled ->
            viewModel.onRegionToggled(region, enabled, documentType, documentSelectionType)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdCaptureRegionsSettingsScreen(
    settings: Settings.IdCaptureSettings,
    documentType: IdCaptureDocumentType,
    documentSelectionType: DocumentSelectionType,
    onBackClick: () -> Unit,
    onRegionChanged: (IdCaptureRegion, Boolean) -> Unit,
) {
    val selectedRegions = remember(settings, documentType, documentSelectionType) {
        val documents = when (documentSelectionType) {
            DocumentSelectionType.ACCEPTED -> settings.acceptedDocuments
            DocumentSelectionType.REJECTED -> settings.rejectedDocuments
        }

        documents
            .filter { it.documentType == documentType }
            .map { it.region }
            .toSet()
    }

    val sortedRegions = remember {
        IdCaptureRegion.entries.sortedWith(
            compareBy<IdCaptureRegion> { it != IdCaptureRegion.ANY && it != IdCaptureRegion.EU_AND_SCHENGEN }
                .thenBy { it.description }
        )
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredRegions = remember(sortedRegions, searchQuery) {
        if (searchQuery.isEmpty()) {
            sortedRegions
        } else {
            sortedRegions.filter { region ->
                region.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    SettingsScaffold(
        title = stringResource(R.string.id_capture_regions_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        searchBar(
            key = "region_search",
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
        )
        filteredRegions.forEach { region ->
            checkboxSetting(
                key = "region_${region.name}",
                label = region.description,
                checked = selectedRegions.contains(region),
                onCheckedChange = { enabled -> onRegionChanged(region, enabled) },
            )
        }
    }
}

@Preview
@Composable
private fun IdCaptureRegionsSettingsScreenPreview() {
    IdCaptureRegionsSettingsScreen(
        settings = Settings.IdCaptureSettings(),
        documentType = IdCaptureDocumentType.ID_CARD,
        documentSelectionType = DocumentSelectionType.ACCEPTED,
        onBackClick = {},
        onRegionChanged = { _, _ -> },
    )
}

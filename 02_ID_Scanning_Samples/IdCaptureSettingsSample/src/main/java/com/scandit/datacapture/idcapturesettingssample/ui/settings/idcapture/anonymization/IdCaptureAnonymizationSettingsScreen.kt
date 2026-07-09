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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.anonymization

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scandit.datacapture.id.data.IdAnonymizationMode
import com.scandit.datacapture.id.data.IdFieldType
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.checkboxSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.enumDropdownSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.sectionHeader
import com.scandit.datacapture.idcapturesettingssample.ui.component.switchSetting
import com.scandit.datacapture.idcapturesettingssample.utils.toNameCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdCaptureAnonymizationSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: IdCaptureAnonymizationSettingsViewModel = viewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    IdCaptureAnonymizationSettingsScreen(
        settings = settings,
        onBackClick = onBackClick,
        onAnonymizationModeChanged = viewModel::onAnonymizationModeChanged,
        onAnonymizedFieldChanged = viewModel::onAnonymizedFieldChanged,
        onAnonymizeDefaultFieldsChanged = viewModel::onAnonymizeDefaultFieldsChanged,
    )
}

@Composable
private fun IdCaptureAnonymizationSettingsScreen(
    settings: Settings.IdCaptureSettings,
    onBackClick: () -> Unit,
    onAnonymizationModeChanged: (IdAnonymizationMode) -> Unit,
    onAnonymizedFieldChanged: (IdFieldType, Boolean) -> Unit,
    onAnonymizeDefaultFieldsChanged: (Boolean) -> Unit
) {
    SettingsScaffold(
        title = stringResource(R.string.anonymization_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        enumDropdownSetting(
            key = "anonymization_mode",
            labelRes = R.string.anonymization_mode_title,
            value = settings.anonymizationMode,
            onValueChange = onAnonymizationModeChanged,
        )

        switchSetting(
            key = "anonymize_default_fields",
            labelRes = R.string.anonymize_default_fields_title,
            checked = settings.anonymizeDefaultFields,
            onCheckedChange = onAnonymizeDefaultFieldsChanged,
        )

        sectionHeader(
            key = "anonymized_fields_header",
            textRes = R.string.anonymized_fields_category_title,
        )

        IdFieldType.entries.forEach { fieldType ->
            checkboxSetting(
                key = "anonymized_field_${fieldType.name}",
                label = fieldType.name.toNameCase(),
                checked = settings.anonymizedFields.contains(fieldType),
                onCheckedChange = { enabled ->
                    onAnonymizedFieldChanged(fieldType, enabled)
                },
            )
        }
    }
}

@Preview
@Composable
private fun IdCaptureAnonymizationSettingsScreenPreview() {
    IdCaptureAnonymizationSettingsScreen(
        settings = Settings.IdCaptureSettings(),
        onBackClick = {},
        onAnonymizationModeChanged = {},
        onAnonymizedFieldChanged = { _, _ -> },
        onAnonymizeDefaultFieldsChanged = {},
    )
}

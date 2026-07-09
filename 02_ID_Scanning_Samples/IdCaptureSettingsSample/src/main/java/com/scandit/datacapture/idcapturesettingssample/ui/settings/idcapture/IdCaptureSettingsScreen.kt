/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scandit.datacapture.id.data.IdImageType
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.enumMultiSelectDropdownSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.setting
import com.scandit.datacapture.idcapturesettingssample.ui.component.switchSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdCaptureSettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToAnonymization: () -> Unit,
    onNavigateToAcceptedDocuments: () -> Unit,
    onNavigateToRejectedDocuments: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToRejection: () -> Unit,
    viewModel: IdCaptureSettingsViewModel = viewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    IdCaptureSettingsScreen(
        settings = settings,
        onBackClick = onBackClick,
        onNavigateToAnonymization = onNavigateToAnonymization,
        onNavigateToAcceptedDocuments = onNavigateToAcceptedDocuments,
        onNavigateToRejectedDocuments = onNavigateToRejectedDocuments,
        onNavigateToScanner = onNavigateToScanner,
        onNavigateToFeedback = onNavigateToFeedback,
        onNavigateToRejection = onNavigateToRejection,
        onImageTypesChanged = viewModel::onImageTypesChanged,
        onDecodeBackOfEuropeanDrivingLicenseChanged = viewModel::onDecodeBackOfEuropeanDrivingLicenseChanged,
    )
}

@Composable
private fun IdCaptureSettingsScreen(
    settings: Settings,
    onBackClick: () -> Unit,
    onNavigateToAnonymization: () -> Unit,
    onNavigateToAcceptedDocuments: () -> Unit,
    onNavigateToRejectedDocuments: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToRejection: () -> Unit,
    onImageTypesChanged: (Set<IdImageType>) -> Unit,
    onDecodeBackOfEuropeanDrivingLicenseChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current

    SettingsScaffold(
        title = stringResource(R.string.id_capture_category_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        setting(
            key = "anonymization",
            labelRes = R.string.anonymization_title,
            summary = getAnonymizationSummary(settings),
            onClick = onNavigateToAnonymization
        )

        setting(
            key = "accepted_documents",
            labelRes = R.string.id_capture_accepted_documents_title,
            summary = getAcceptedDocumentsSummary(context, settings),
            onClick = onNavigateToAcceptedDocuments
        )

        setting(
            key = "rejected_documents",
            labelRes = R.string.id_capture_rejected_documents_title,
            summary = getRejectedDocumentsSummary(context, settings),
            onClick = onNavigateToRejectedDocuments
        )

        setting(
            key = "scanner",
            labelRes = R.string.id_capture_scanner_title,
            summary = getScannerSummary(context, settings),
            onClick = onNavigateToScanner
        )

        enumMultiSelectDropdownSetting(
            key = "image_types",
            labelRes = R.string.image_types_title,
            selectedValues = settings.idCapture.imageTypes,
            onValuesChange = onImageTypesChanged
        )

        setting(
            key = "feedback",
            labelRes = R.string.id_capture_feedback_title,
            onClick = onNavigateToFeedback
        )

        setting(
            key = "rejection",
            labelRes = R.string.id_capture_rejection_title,
            onClick = onNavigateToRejection
        )

        switchSetting(
            key = "decode_back_of_european_driving_license",
            labelRes = R.string.decode_back_of_european_driving_license,
            checked = settings.idCapture.decodeBackOfEuropeanDrivingLicense,
            onCheckedChange = onDecodeBackOfEuropeanDrivingLicenseChanged
        )
    }
}

@Preview
@Composable
private fun IdCaptureSettingsScreenPreview() {
    IdCaptureSettingsScreen(
        settings = Settings(),
        onBackClick = {},
        onNavigateToAnonymization = {},
        onNavigateToAcceptedDocuments = {},
        onNavigateToRejectedDocuments = {},
        onNavigateToScanner = {},
        onNavigateToFeedback = {},
        onNavigateToRejection = {},
        onImageTypesChanged = {},
        onDecodeBackOfEuropeanDrivingLicenseChanged = {}
    )
}

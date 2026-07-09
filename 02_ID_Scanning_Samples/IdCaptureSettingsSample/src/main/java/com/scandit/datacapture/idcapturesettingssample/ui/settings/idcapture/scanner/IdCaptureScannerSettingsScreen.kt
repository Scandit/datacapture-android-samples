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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.scanner

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scandit.datacapture.id.data.MobileDocumentDataElement
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.PhysicalDocumentScannerType
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.enumDropdownSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.enumMultiSelectDropdownSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.sectionHeader
import com.scandit.datacapture.idcapturesettingssample.ui.component.switchSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdCaptureScannerSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: IdCaptureScannerSettingsViewModel = viewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    IdCaptureScannerSettingsScreen(
        settings = settings,
        onBackClick = onBackClick,
        onPhysicalDocumentScannerTypeChanged = viewModel::onPhysicalDocumentScannerTypeChanged,
        onSingleSideScannerBarcodeChanged = viewModel::onSingleSideScannerBarcodeChanged,
        onSingleSideScannerMrzChanged = viewModel::onSingleSideScannerMrzChanged,
        onSingleSideScannerVizChanged = viewModel::onSingleSideScannerVizChanged,
        onMobileScannerIso1801315Changed = viewModel::onMobileScannerIso1801315Changed,
        onMobileScannerOcrChanged = viewModel::onMobileScannerOcrChanged,
        onMdocElementsToRetainChanged = viewModel::onMdocElementsToRetainChanged,
    )
}

@Composable
private fun IdCaptureScannerSettingsScreen(
    settings: Settings.IdCaptureSettings,
    onBackClick: () -> Unit,
    onPhysicalDocumentScannerTypeChanged: (PhysicalDocumentScannerType) -> Unit,
    onSingleSideScannerBarcodeChanged: (Boolean) -> Unit,
    onSingleSideScannerMrzChanged: (Boolean) -> Unit,
    onSingleSideScannerVizChanged: (Boolean) -> Unit,
    onMobileScannerIso1801315Changed: (Boolean) -> Unit,
    onMobileScannerOcrChanged: (Boolean) -> Unit,
    onMdocElementsToRetainChanged: (Set<MobileDocumentDataElement>) -> Unit
) {
    SettingsScaffold(
        title = stringResource(R.string.id_capture_scanner_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        physicalDocumentScannerSettings(
            settings = settings,
            onPhysicalDocumentScannerTypeChanged = onPhysicalDocumentScannerTypeChanged
        )

        singleSideScannerFeatures(
            settings = settings,
            onBarcodeChanged = onSingleSideScannerBarcodeChanged,
            onMrzChanged = onSingleSideScannerMrzChanged,
            onVizChanged = onSingleSideScannerVizChanged
        )

        mobileDocumentScannerSettings(
            settings = settings,
            onIso1801315Changed = onMobileScannerIso1801315Changed,
            onOcrChanged = onMobileScannerOcrChanged,
            onMdocElementsToRetainChanged = onMdocElementsToRetainChanged
        )
    }
}

private fun LazyListScope.physicalDocumentScannerSettings(
    settings: Settings.IdCaptureSettings,
    onPhysicalDocumentScannerTypeChanged: (PhysicalDocumentScannerType) -> Unit
) {
    sectionHeader(
        key = "physical_document_scanner_header",
        textRes = R.string.physical_document_scanner
    )

    enumDropdownSetting(
        key = "physical_document_scanner_type",
        labelRes = R.string.physical_document_scanner_type,
        value = settings.physicalDocumentScannerType,
        onValueChange = onPhysicalDocumentScannerTypeChanged
    )
}

private fun LazyListScope.singleSideScannerFeatures(
    settings: Settings.IdCaptureSettings,
    onBarcodeChanged: (Boolean) -> Unit,
    onMrzChanged: (Boolean) -> Unit,
    onVizChanged: (Boolean) -> Unit
) {
    val isSingleSideMode = settings.physicalDocumentScannerType == PhysicalDocumentScannerType.SINGLE_SIDE

    if (isSingleSideMode) {
        sectionHeader(
            key = "single_side_scanner_features_header",
            textRes = R.string.single_side_scanner_features
        )

        switchSetting(
            key = "single_side_scanner_barcode",
            labelRes = R.string.single_side_scanner_barcode,
            checked = settings.singleSideScannerBarcodeEnabled,
            onCheckedChange = onBarcodeChanged
        )

        switchSetting(
            key = "single_side_scanner_mrz",
            labelRes = R.string.single_side_scanner_mrz,
            checked = settings.singleSideScannerMrzEnabled,
            onCheckedChange = onMrzChanged
        )

        switchSetting(
            key = "single_side_scanner_viz",
            labelRes = R.string.single_side_scanner_viz,
            checked = settings.singleSideScannerVizEnabled,
            onCheckedChange = onVizChanged
        )
    }
}

private fun LazyListScope.mobileDocumentScannerSettings(
    settings: Settings.IdCaptureSettings,
    onIso1801315Changed: (Boolean) -> Unit,
    onOcrChanged: (Boolean) -> Unit,
    onMdocElementsToRetainChanged: (Set<MobileDocumentDataElement>) -> Unit
) {
    sectionHeader(
        key = "mobile_document_scanner_header",
        textRes = R.string.mobile_document_scanner
    )

    switchSetting(
        key = "mobile_scanner_iso_18013_15",
        labelRes = R.string.mobile_scanner_iso_18013_15,
        checked = settings.mobileScannerIso1801315Enabled,
        onCheckedChange = onIso1801315Changed
    )

    switchSetting(
        key = "mobile_scanner_ocr",
        labelRes = R.string.mobile_scanner_ocr,
        checked = settings.mobileScannerOcrEnabled,
        onCheckedChange = onOcrChanged
    )

    enumMultiSelectDropdownSetting(
        key = "mdoc_elements_to_retain",
        labelRes = R.string.mdoc_elements_to_retain_title,
        selectedValues = settings.mdocElementsToRetain,
        onValuesChange = onMdocElementsToRetainChanged
    )
}

@Preview
@Composable
private fun IdCaptureScannerSettingsScreenPreview() {
    IdCaptureScannerSettingsScreen(
        settings = Settings.IdCaptureSettings(),
        onBackClick = {},
        onPhysicalDocumentScannerTypeChanged = {},
        onSingleSideScannerBarcodeChanged = {},
        onSingleSideScannerMrzChanged = {},
        onSingleSideScannerVizChanged = {},
        onMobileScannerIso1801315Changed = {},
        onMobileScannerOcrChanged = {},
        onMdocElementsToRetainChanged = {}
    )
}

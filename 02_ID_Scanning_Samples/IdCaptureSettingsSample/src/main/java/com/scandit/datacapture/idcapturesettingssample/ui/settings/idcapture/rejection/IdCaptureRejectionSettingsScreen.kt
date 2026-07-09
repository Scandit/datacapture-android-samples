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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.rejection

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.integerInputSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.switchSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdCaptureRejectionSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: IdCaptureRejectionSettingsViewModel = viewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    IdCaptureRejectionSettingsScreen(
        settings = settings,
        onBackClick = onBackClick,
        onRejectExpiredIdsChanged = viewModel::onRejectExpiredIdsChanged,
        onRejectNotRealIdCompliantChanged = viewModel::onRejectNotRealIdCompliantChanged,
        onRejectForgedAamvaBarcodesChanged = viewModel::onRejectForgedAamvaBarcodesChanged,
        onRejectInconsistentDataChanged = viewModel::onRejectInconsistentDataChanged,
        onRejectHolderBelowAgeChanged = viewModel::onRejectHolderBelowAgeChanged,
        onRejectIdsExpiringInDaysChanged = viewModel::onRejectIdsExpiringInDaysChanged,
        onRejectVoidedIdsChanged = viewModel::onRejectVoidedIdsChanged,
        onRejectionTimeoutSecondsChanged = viewModel::onRejectionTimeoutSecondsChanged,
    )
}

@Composable
private fun IdCaptureRejectionSettingsScreen(
    settings: Settings.IdCaptureSettings,
    onBackClick: () -> Unit,
    onRejectExpiredIdsChanged: (Boolean) -> Unit,
    onRejectNotRealIdCompliantChanged: (Boolean) -> Unit,
    onRejectForgedAamvaBarcodesChanged: (Boolean) -> Unit,
    onRejectInconsistentDataChanged: (Boolean) -> Unit,
    onRejectHolderBelowAgeChanged: (Int?) -> Unit,
    onRejectIdsExpiringInDaysChanged: (Int?) -> Unit,
    onRejectVoidedIdsChanged: (Boolean) -> Unit,
    onRejectionTimeoutSecondsChanged: (Int?) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.id_capture_rejection_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        switchSetting(
            key = "reject_expired_ids",
            labelRes = R.string.reject_expired_ids,
            checked = settings.rejectExpiredIds,
            onCheckedChange = onRejectExpiredIdsChanged,
        )

        switchSetting(
            key = "reject_not_real_id_compliant",
            labelRes = R.string.reject_not_real_id_compliant,
            checked = settings.rejectNotRealIdCompliant,
            onCheckedChange = onRejectNotRealIdCompliantChanged,
        )

        switchSetting(
            key = "reject_forged_aamva_barcodes",
            labelRes = R.string.reject_forged_aamva_barcodes,
            checked = settings.rejectForgedAamvaBarcodes,
            onCheckedChange = onRejectForgedAamvaBarcodesChanged,
        )

        switchSetting(
            key = "reject_inconsistent_data",
            labelRes = R.string.reject_inconsistent_data,
            checked = settings.rejectInconsistentData,
            onCheckedChange = onRejectInconsistentDataChanged,
        )

        integerInputSetting(
            key = "reject_holder_below_age",
            labelRes = R.string.reject_holder_below_age,
            value = settings.rejectHolderBelowAge,
            maxLength = 3,
            onValueChange = onRejectHolderBelowAgeChanged,
        )

        integerInputSetting(
            key = "reject_ids_expiring_in_days",
            labelRes = R.string.reject_ids_expiring_in_days,
            value = settings.rejectIdsExpiringInDays,
            maxLength = 4,
            onValueChange = onRejectIdsExpiringInDaysChanged,
        )

        switchSetting(
            key = "reject_voided_ids",
            labelRes = R.string.reject_voided_ids,
            checked = settings.rejectVoidedIds,
            onCheckedChange = onRejectVoidedIdsChanged,
        )

        integerInputSetting(
            key = "rejection_timeout_seconds",
            labelRes = R.string.rejection_timeout_seconds,
            value = settings.rejectionTimeoutSeconds,
            maxLength = 5,
            onValueChange = onRejectionTimeoutSecondsChanged,
        )
    }
}

@Preview
@Composable
private fun IdCaptureRejectionSettingsScreenPreview() {
    IdCaptureRejectionSettingsScreen(
        settings = Settings.IdCaptureSettings(),
        onBackClick = {},
        onRejectExpiredIdsChanged = {},
        onRejectNotRealIdCompliantChanged = {},
        onRejectForgedAamvaBarcodesChanged = {},
        onRejectInconsistentDataChanged = {},
        onRejectHolderBelowAgeChanged = {},
        onRejectIdsExpiringInDaysChanged = {},
        onRejectVoidedIdsChanged = {},
        onRejectionTimeoutSecondsChanged = {},
    )
}

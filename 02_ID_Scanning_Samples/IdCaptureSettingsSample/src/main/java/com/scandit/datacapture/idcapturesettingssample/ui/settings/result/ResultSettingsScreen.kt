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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.result

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.switchSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultSettingsScreen(
    settingsRepository: SettingsRepository,
    onBackClick : () -> Unit,
) {
    val settings by settingsRepository.settingsStream.collectAsStateWithLifecycle()

    ResultSettingsScreen(
        settings = settings,
        onBackClick = onBackClick,
        onContinuousScanChanged = { enabled ->
            settingsRepository.updateResult {
                it.copy(continuousScanEnabled = enabled)
            }
        },
    )
}

@Composable
private fun ResultSettingsScreen(
    settings: Settings,
    onBackClick : () -> Unit,
    onContinuousScanChanged: (Boolean) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.result_category_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        switchSetting(
            key = "continuous_scan",
            labelRes = R.string.continuous_scan_title,
            checked = settings.result.continuousScanEnabled,
            onCheckedChange = onContinuousScanChanged,
        )
    }
}

@Preview
@Composable
private fun ResultSettingsScreenPreview() {
    ResultSettingsScreen(
        settings = Settings(),
        onBackClick = {},
        onContinuousScanChanged = {},
    )
}

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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.camera

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scandit.datacapture.core.source.CameraPosition
import com.scandit.datacapture.core.source.TorchState
import com.scandit.datacapture.core.source.VideoResolution
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.enumDropdownSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.switchSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraSettingsScreen(
    settingsRepository: SettingsRepository,
    onBackClick: () -> Unit,
) {
    val settings by settingsRepository.settingsStream.collectAsStateWithLifecycle()

    CameraSettingsScreen(
        settings = settings,
        onBackClick = onBackClick,
        onCameraPositionChanged = { position ->
            settingsRepository.updateCamera {
                it.copy(position = position)
            }
        },
        onTorchStateChanged = { enabled ->
            settingsRepository.updateCamera {
                it.copy(torchState = if (enabled) TorchState.ON else TorchState.OFF)
            }
        },
        onPreferredResolutionChanged = { resolution ->
            settingsRepository.updateCamera {
                it.copy(preferredResolution = resolution)
            }
        }
    )
}

@Composable
private fun CameraSettingsScreen(
    settings: Settings,
    onBackClick: () -> Unit,
    onCameraPositionChanged: (CameraPosition) -> Unit,
    onTorchStateChanged: (Boolean) -> Unit,
    onPreferredResolutionChanged: (VideoResolution) -> Unit
) {
    SettingsScaffold(
        title = stringResource(R.string.camera_category_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        enumDropdownSetting(
            key = "camera_position",
            labelRes = R.string.camera_position_title,
            value = settings.camera.position,
            onValueChange = onCameraPositionChanged
        )

        switchSetting(
            key = "torch_state",
            labelRes = R.string.camera_torch_title,
            checked = settings.camera.torchState == TorchState.ON,
            onCheckedChange = onTorchStateChanged
        )

        enumDropdownSetting(
            key = "preferred_resolution",
            labelRes = R.string.camera_preferred_resolution_title,
            value = settings.camera.preferredResolution,
            onValueChange = onPreferredResolutionChanged
        )
    }
}

@Preview
@Composable
private fun CameraSettingsScreenPreview() {
    CameraSettingsScreen(
        settings = Settings(),
        onBackClick = {},
        onCameraPositionChanged = {},
        onTorchStateChanged = {},
        onPreferredResolutionChanged = {}
    )
}

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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.feedback

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.FeedbackType
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.enumDropdownSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdCaptureFeedbackSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: IdCaptureFeedbackSettingsViewModel = viewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    IdCaptureFeedbackSettingsScreen(
        settings = settings,
        onBackClick = onBackClick,
        onCapturedFeedbackChanged = viewModel::onCapturedFeedbackChanged,
        onRejectedFeedbackChanged = viewModel::onRejectedFeedbackChanged,
    )
}

@Composable
private fun IdCaptureFeedbackSettingsScreen(
    settings: Settings.IdCaptureSettings,
    onBackClick: () -> Unit,
    onCapturedFeedbackChanged: (FeedbackType) -> Unit,
    onRejectedFeedbackChanged: (FeedbackType) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.id_capture_feedback_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        enumDropdownSetting(
            key = "id_captured_feedback",
            labelRes = R.string.id_captured_feedback,
            value = settings.capturedFeedback,
            onValueChange = onCapturedFeedbackChanged,
        )

        enumDropdownSetting(
            key = "id_rejected_feedback",
            labelRes = R.string.id_rejected_feedback,
            value = settings.rejectedFeedback,
            onValueChange = onRejectedFeedbackChanged,
        )
    }
}

@Preview
@Composable
private fun IdCaptureFeedbackSettingsScreenPreview() {
    IdCaptureFeedbackSettingsScreen(
        settings = Settings.IdCaptureSettings(),
        onBackClick = {},
        onCapturedFeedbackChanged = {},
        onRejectedFeedbackChanged = {},
    )
}

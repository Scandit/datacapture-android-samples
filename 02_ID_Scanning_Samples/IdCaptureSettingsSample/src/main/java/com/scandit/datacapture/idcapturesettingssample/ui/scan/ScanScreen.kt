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

package com.scandit.datacapture.idcapturesettingssample.ui.scan

import android.Manifest.permission.CAMERA
import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.scandit.datacapture.id.data.RejectionReason
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult
import com.scandit.datacapture.idcapturesettingssample.ui.result.ResultViewModel
import com.scandit.datacapture.idcapturesettingssample.utils.toNameCase

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = viewModel(),
    resultViewModel: ResultViewModel,
    onNavigateToResult: (CaptureResult) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(CAMERA)

    if (!cameraPermissionState.status.isGranted) {
        LaunchedEffect(cameraPermissionState) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showRejectionDialog by remember { mutableStateOf<RejectionReason?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is ScanUiEvent.ShowResult -> {
                    resultViewModel.setCaptureResult(event.result)
                    onNavigateToResult(event.result)
                }
                is ScanUiEvent.ShowResultToast -> {
                    val message = context.getString(
                        R.string.captured_result_parametrised,
                        event.result.fullName,
                        event.result.dateOfBirth
                    )
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }
                is ScanUiEvent.ShowRejection -> {
                    showRejectionDialog = event.reason
                }
                is ScanUiEvent.ShowRejectionToast -> {
                    snackbarHostState.showSnackbar(
                        message = event.reason.name.toNameCase(),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                title = { Text(stringResource(R.string.app_readable_name)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.main_settings)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            DataCaptureViewHolder(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    showRejectionDialog?.let { reason ->
        AlertDialog(
            onDismissRequest = {
                showRejectionDialog = null
                viewModel.enableIdCapture()
            },
            title = { Text(stringResource(R.string.document_rejected_title)) },
            text = { Text(reason.name.toNameCase()) },
            confirmButton = {
                TextButton(onClick = {
                    showRejectionDialog = null
                    viewModel.enableIdCapture()
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun DataCaptureViewHolder(
    viewModel: ScanViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as Activity

    val dataCaptureView = remember(context) {
        viewModel.buildDataCaptureView(context)
    }

    LifecycleResumeEffect(dataCaptureView) {
        val overlay = viewModel.getOverlay()
        dataCaptureView.addOverlay(overlay)
        viewModel.enableIdCaptureAndTurnOnCamera()

        onPauseOrDispose {
            viewModel.disableIdCaptureAndTurnOffCamera()
            // Reset scanning state on real navigation. isChangingConfigurations is true on
            // orientation change, skip reset in that case to preserve the scanning state.
            if (!activity.isChangingConfigurations) {
                viewModel.resetIdCapture()
            }
            dataCaptureView.removeOverlay(overlay)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { dataCaptureView }
    )
}

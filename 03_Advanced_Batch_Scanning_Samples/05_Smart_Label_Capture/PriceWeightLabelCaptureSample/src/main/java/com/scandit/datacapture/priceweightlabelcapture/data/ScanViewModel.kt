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
package com.scandit.datacapture.priceweightlabelcapture.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.async.Callback
import com.scandit.datacapture.core.internal.sdk.AppAndroidEnvironment
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.priceweightlabelcapture.R
import com.scandit.datacapture.priceweightlabelcapture.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ScanViewModel : ViewModel() {
    private val context: Context get() = AppAndroidEnvironment.applicationContext

    val dataCaptureContext = DataCaptureContext.forLicenseKey(Settings.LICENSE_KEY)

    private val camera = Camera.getDefaultCamera().also(dataCaptureContext::setFrameSource)

    private val latestResult = Channel<ScannedResult>()
    private val _results: MutableStateFlow<List<ScannedResult>> = MutableStateFlow(emptyList())
    val results = _results.asStateFlow()
    private val _onLabelScanned = Channel<Unit>(capacity = Channel.CONFLATED)
    val onLabelScanned = _onLabelScanned.receiveAsFlow()
    private val _incompleteLabel: MutableStateFlow<PartialLabel?> = MutableStateFlow(null)
    val incompleteLabel = _incompleteLabel.asStateFlow()
    private val _messages = Channel<Message?>(capacity = Channel.CONFLATED)
    val messages = _messages.receiveAsFlow().distinctUntilChanged()

    private val scanStates = MutableStateFlow(ScanState.Label)

    val guidanceText: Flow<String?> = scanStates.map { scanState ->
        when (scanState) {
            ScanState.Label -> context.getString(R.string.fit_label_in_preview)
            ScanState.Confirm, ScanState.FillInfo -> context.getString(R.string.tap_anywhere_to_resume)
        }
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.NoDialog)
    val dialogState = _uiState.asStateFlow()

    private val labelCapture =
        LabelCaptureProvider.buildLabelCapture(dataCaptureContext, coroutineScope = viewModelScope,
            onLabelScanned = { label ->
                latestResult.send(label)
                _onLabelScanned.send(Unit)
            },
            onPartialLabelScanned = { label: PartialLabel ->
                _incompleteLabel.value = label
                _onLabelScanned.trySend(Unit)
            })
    val labelCaptureOverlay = LabelCaptureProvider.buildOverlay(context, labelCapture)

    fun clearResults() {
        _results.value = emptyList()
    }

    fun submitLabel(label: ScannedResult.Label) {
        latestResult.trySendBlocking(label)
        discardPendingLabel()
    }

    fun onTap() {
        if (scanStates.value in arrayOf(ScanState.FillInfo, ScanState.Confirm)) {
            discardPendingLabel()
            _messages.trySend(null)
            scanStates.value = ScanState.Label
        }
    }

    fun onPause() {
        viewModelScope.launch {
            camera?.switchCameraOff()
        }
    }

    suspend fun run() = coroutineScope {
        launch { handleStates() }
        launch { handleResults() }
    }

    private suspend fun handleStates() = coroutineScope {
        launch {
            scanStates.collect { scanState ->
                when (scanState) {
                    ScanState.Label -> {
                        _uiState.value = UiState.NoDialog
                        labelCapture.isEnabled = true
                        camera?.switchCameraOn()
                    }
                    ScanState.Confirm -> {
                        camera?.switchCameraOff()
                        _uiState.value = UiState.ShowResultsDialog
                        labelCapture.isEnabled = false
                    }
                    ScanState.FillInfo -> {
                        camera?.switchCameraOff()
                        labelCapture.isEnabled = false
                        _uiState.value = UiState.CompleteLabelDialog
                    }
                }
            }
        }
    }

    private suspend fun handleResults() = withContext(Dispatchers.Default) {
        launch {
            latestResult.receiveAsFlow().collect { scannedResult ->
                _messages.send(null)

                if (!increaseQuantityIfInList(scannedResult.data)) {
                    // Does not exist, add to list
                    _results.value = listOf(scannedResult) + _results.value
                }

                scanStates.value = ScanState.Confirm
            }
        }
        launch {
            incompleteLabel.collect {
                val item = it ?: return@collect

                if (increaseQuantityIfInList(item.data)) {
                    scanStates.value = ScanState.Confirm
                } else {
                    _messages.send(Message(context.getString(R.string.incomplete_data_scanned)))
                    scanStates.value = ScanState.FillInfo
                }
            }
        }
    }

    private fun increaseQuantityIfInList(itemData: String): Boolean {
        _results.value.find { it.data == itemData }?.let { result ->
            // Item found, increase quantity and bring to top of the list
            val resultList = _results.value.toMutableList()
            resultList.remove(result)
            result.quantity++
            _results.value = listOf(result) + resultList
            return true
        }

        return false
    }

    private suspend fun Camera.switchCameraOn() = suspendCoroutine { continuation ->
        switchToDesiredState(FrameSourceState.ON, object : Callback<Boolean> {
            override fun run(result: Boolean) {
                continuation.resume(result)
            }
        })
    }

    private suspend fun Camera.switchCameraOff() = suspendCoroutine { continuation ->
        switchToDesiredState(FrameSourceState.OFF, object : Callback<Boolean> {
            override fun run(result: Boolean) {
                continuation.resume(result)
            }
        })
    }

    private fun discardPendingLabel() {
        _incompleteLabel.value = null
    }
}

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

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.id.data.RejectionReason
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay
import com.scandit.datacapture.idcapturesettingssample.data.CameraRepository
import com.scandit.datacapture.idcapturesettingssample.data.DataCaptureViewRepository
import com.scandit.datacapture.idcapturesettingssample.data.IdCaptureRepository
import com.scandit.datacapture.idcapturesettingssample.di.Injector
import com.scandit.datacapture.idcapturesettingssample.mappers.IdCaptureResultFactory
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Sealed class representing one-shot UI events for the scan screen.
 */
sealed class ScanUiEvent {
    data class ShowResult(val result: CaptureResult) : ScanUiEvent()
    data class ShowResultToast(val result: CaptureResult) : ScanUiEvent()
    data class ShowRejection(val reason: RejectionReason) : ScanUiEvent()
    data class ShowRejectionToast(val reason: RejectionReason) : ScanUiEvent()
}

class ScanViewModel : ViewModel() {

    /**
     * The repository to interact with the device's camera. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private val cameraRepository: CameraRepository = Injector.cameraRepository

    /**
     * The repository to interact with the IdCapture mode. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private val idCaptureRepository: IdCaptureRepository = Injector.idCaptureRepository

    /**
     * The repository to interact with DataCaptureViews. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private val dataCaptureViewRepository: DataCaptureViewRepository = Injector.dataCaptureViewRepository

    private val settingsRepository: SettingsRepository = Injector.settingsRepository

    /**
     * The UI events flow for one-shot events that should be handled by the UI.
     * Using SharedFlow instead of StateFlow ensures events are not missed and don't require manual reset.
     */
    private val _uiEvents = MutableSharedFlow<ScanUiEvent>()
    val uiEvents: SharedFlow<ScanUiEvent> = _uiEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            idCaptureRepository.scanResults.collect { scanResult ->
                displayCapturedResult(scanResult)
            }
        }

        viewModelScope.launch {
            idCaptureRepository.rejectedIds.collect { reason ->
                displayRejectedId(reason)
            }
        }

        viewModelScope.launch {
            settingsRepository.settingsStream.map { it.idCapture }.distinctUntilChanged().collect {
                idCaptureRepository.applyIdCaptureSettings()
            }
        }

        viewModelScope.launch {
            settingsRepository.settingsStream.map { it.camera }.distinctUntilChanged().collect {
                cameraRepository.applyCameraSettings()
            }
        }

        viewModelScope.launch {
            settingsRepository.settingsStream.map { it.view }.distinctUntilChanged().collect {
                idCaptureRepository.updateOverlaySettings(it)
            }
        }
    }

    /**
     * Shortcut method to both disable idCapture and turn off the camera.
     */
    fun disableIdCaptureAndTurnOffCamera() {
        disableIdCapture()
        turnOffCamera()
    }

    /**
     * Shortcut method to both enable idCapture and turn on the camera.
     */
    fun enableIdCaptureAndTurnOnCamera() {
        enableIdCapture()
        turnOnCamera()
    }

    /**
     * True while a partial result (the front of a double-sided document, delivered when
     * notifyOnSideCapture is enabled) is shown and the remaining side is still to be captured.
     * Resetting the session while this holds would discard the already-captured side, so we skip
     * the reset. Cleared once a complete result is delivered.
     */
    private var awaitingRemainingSide = false

    /**
     * Reset the IdCapture scanning state.
     */
    fun resetIdCapture() {
        if (awaitingRemainingSide) return
        idCaptureRepository.resetIdCapture()
    }

    /**
     * Resume scanning by enabling the IdCapture mode.
     * Once IdCapture is enabled it will start processing the camera frames.
     */
    fun enableIdCapture() {
        idCaptureRepository.enableIdCapture()
    }

    /**
     * Pause scanning by disabling the IdCapture mode.
     * Once IdCapture is disabled it will no longer process camera frames.
     */
    fun disableIdCapture() {
        idCaptureRepository.disableIdCapture()
    }

    /**
     * Start the camera preview.
     */
    fun turnOnCamera() {
        /*
         * Switch the camera on. The camera frames will be sent to IdCapture for processing.
         * Additionally the preview will appear on the screen. The camera is started asynchronously,
         * and you may notice a small delay before the preview appears.
         */
        cameraRepository.turnOnCamera()
    }

    /**
     * Stop the camera preview.
     */
    fun turnOffCamera() {
        cameraRepository.turnOffCamera()
    }

    /**
     * Returns the ID Capture Overlay.
     */
    fun getOverlay(): IdCaptureOverlay = idCaptureRepository.overlay

    /**
     * Request a DataCaptureView to be built
     */
    fun buildDataCaptureView(context: Context): DataCaptureView =
        dataCaptureViewRepository.buildDataCaptureView(context)

    /**
     * Display the UI with the details of a captured personal
     * identification document data.
     */
    private fun displayCapturedResult(scanResult: ScanResult) {
        awaitingRemainingSide = !scanResult.capturedId.isCapturingComplete
        val result = IdCaptureResultFactory.extract(scanResult.capturedId)
        showCapturedResultInSelectedMode(result)
    }

    fun showCapturedResultInSelectedMode(result: CaptureResult) {
        viewModelScope.launch {
            val event = if (idCaptureRepository.isContinuousMode()) {
                // Display the result in a non-blocking way, so the mode doesn't have to stop scanning.
                ScanUiEvent.ShowResultToast(result)
            } else {
                ScanUiEvent.ShowResult(result)
            }
            _uiEvents.emit(event)
        }
    }

    /**
     * Display the UI with the rejection reason.
     */
    private fun displayRejectedId(reason: RejectionReason) {
        viewModelScope.launch {
            val event = if (idCaptureRepository.isContinuousMode()) {
                ScanUiEvent.ShowRejectionToast(reason)
            } else {
                ScanUiEvent.ShowRejection(reason)
            }
            _uiEvents.emit(event)
        }
    }
}

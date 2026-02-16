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

package com.scandit.datacapture.idcapturesettingssample.data

import androidx.annotation.WorkerThread
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.feedback.Feedback
import com.scandit.datacapture.core.common.feedback.Sound
import com.scandit.datacapture.core.common.feedback.Vibration
import com.scandit.datacapture.id.capture.FullDocumentScanner
import com.scandit.datacapture.id.capture.IdCapture
import com.scandit.datacapture.id.capture.IdCaptureFeedback
import com.scandit.datacapture.id.capture.IdCaptureListener
import com.scandit.datacapture.id.capture.IdCaptureScanner
import com.scandit.datacapture.id.capture.IdCaptureSettings
import com.scandit.datacapture.id.capture.MobileDocumentScanner
import com.scandit.datacapture.id.capture.SingleSideScanner
import com.scandit.datacapture.id.data.CapturedId
import com.scandit.datacapture.id.data.CapturedSides
import com.scandit.datacapture.id.data.Duration
import com.scandit.datacapture.id.data.IdImageType
import com.scandit.datacapture.id.data.RejectionReason
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay
import com.scandit.datacapture.idcapturesettingssample.model.FeedbackType
import com.scandit.datacapture.idcapturesettingssample.model.PhysicalDocumentScannerType
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.ui.scan.ScanResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * The repository to interact with the IdCapture mode.
 */
class IdCaptureRepository(
    dataCaptureContext: DataCaptureContext,
    private val settingsRepository: SettingsRepository,
) : IdCaptureListener {

    /**
     * Coroutine scope for emitting events from background threads.
     * Uses SupervisorJob so failures in one emission don't cancel the entire scope.
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * The IdCapture mode.
     */
    private val idCapture: IdCapture = IdCapture.forDataCaptureContext(
        dataCaptureContext,
        IdCaptureSettings()
    )

    /**
     * The stream of Captured IDs.
     */
    private val _scanResults = MutableSharedFlow<ScanResult>()
    val scanResults: SharedFlow<ScanResult> = _scanResults.asSharedFlow()

    /**
     * The stream of rejected IDs.
     */
    private val _rejectedIds = MutableSharedFlow<RejectionReason>()
    val rejectedIds: SharedFlow<RejectionReason> = _rejectedIds.asSharedFlow()

    init {
        idCapture.addListener(this)
    }

    /**
     * Make IdCapture process incoming frames. This happens asynchronously.
     */
    fun enableIdCapture() {
        idCapture.isEnabled = true
    }

    /**
     * Stop IdCapture from further processing of frames. This happens asynchronously, so some
     * results may still be delivered.
     */
    fun disableIdCapture() {
        idCapture.isEnabled = false
    }

    /**
     * Apply settings changes to the existing IdCapture instance without recreation.
     */
    fun applyIdCaptureSettings() {
        val settings = settingsRepository.settings
        idCapture.applySettings(buildIdCaptureSettings(settings.idCapture))
        idCapture.feedback = buildFeedback(settings.idCapture)
    }

    /**
     * Build IdCaptureSettings from the settings model.
     * Extracted for reuse between refreshIdCapture() and applyIdCaptureSettings().
     */
    private fun buildIdCaptureSettings(settings: Settings.IdCaptureSettings) =
        IdCaptureSettings().apply {
            acceptedDocuments = settings.acceptedDocuments
            rejectedDocuments = settings.rejectedDocuments
            val imageTypes = settings.imageTypes
            IdImageType.entries.forEach { type ->
                setShouldPassImageTypeToResult(type, imageTypes.contains(type))
            }

            scanner = buildIdCaptureScanner(settings)
            anonymizationMode = settings.anonymizationMode

            (settings.acceptedDocuments + settings.rejectedDocuments).forEach { document ->
                settings.anonymizedFields.forEach { fieldType ->
                    addAnonymizedField(document, fieldType)
                }
            }
            anonymizeDefaultFields = settings.anonymizeDefaultFields
            decodeBackOfEuropeanDrivingLicense = settings.decodeBackOfEuropeanDrivingLicense
            rejectVoidedIds = settings.rejectVoidedIds
            rejectExpiredIds = settings.rejectExpiredIds

            val expirationTimeWindow = settings.rejectIdsExpiringInDays?.let {
                Duration(it, 0, 0)
            }
            rejectIdsExpiringIn = expirationTimeWindow
            rejectNotRealIdCompliant = settings.rejectNotRealIdCompliant
            rejectForgedAamvaBarcodes = settings.rejectForgedAamvaBarcodes
            rejectInconsistentData = settings.rejectInconsistentData
            rejectHolderBelowAge = settings.rejectHolderBelowAge
            rejectionTimeoutSeconds = settings.rejectionTimeoutSeconds
        }

    fun buildIdCaptureOverlay(): IdCaptureOverlay {
        val settings = settingsRepository.settings
        return IdCaptureOverlay.newInstance(idCapture, null).apply {
            idLayoutStyle = settings.view.overlayStyle
            textHintPosition = settings.view.textHintPosition
            showTextHints = settings.view.showTextHints
            idLayoutLineStyle = settings.view.overlayLineStyle
            capturedBrush = settingsRepository.buildBrush(settings.view.capturedBrushStyle)

            if (settings.view.viewfinderFrontText.isNotEmpty()) {
                setFrontSideTextHint(settings.view.viewfinderFrontText)
            }
            if (settings.view.viewfinderBackText.isNotEmpty()) {
                setBackSideTextHint(settings.view.viewfinderBackText)
            }
        }
    }

    private fun buildIdCaptureScanner(settings: Settings.IdCaptureSettings): IdCaptureScanner =
        IdCaptureScanner(
            physicalDocument = when (settings.physicalDocumentScannerType) {
                PhysicalDocumentScannerType.SINGLE_SIDE -> SingleSideScanner(
                    settings.singleSideScannerBarcodeEnabled,
                    settings.singleSideScannerMrzEnabled,
                    settings.singleSideScannerVizEnabled
                )

                PhysicalDocumentScannerType.FULL -> FullDocumentScanner()
            },
            mobileDocument = MobileDocumentScanner(
                settings.mobileScannerIso1801315Enabled,
                settings.mobileScannerOcrEnabled,
                settings.mdocElementsToRetain
            )
        )

    private fun buildFeedback(settings: Settings.IdCaptureSettings): IdCaptureFeedback =
        IdCaptureFeedback().apply {
            idCaptured = settings.capturedFeedback.asFeedback()
            idRejected = settings.rejectedFeedback.asFeedback()
        }

    private fun FeedbackType.asFeedback(
        vibration: Vibration = Vibration.defaultVibration(),
        sound: Sound = Sound.defaultSound(),
    ): Feedback = when (this) {
        FeedbackType.SOUND -> Feedback(vibration = null, sound = sound)
        FeedbackType.VIBRATION -> Feedback(vibration = vibration, sound = null)
        FeedbackType.SOUND_AND_VIBRATION -> Feedback(vibration = vibration, sound = sound)
        else -> Feedback(null, null)
    }

    fun isContinuousMode(): Boolean = settingsRepository.settings.result.continuousScanEnabled

    /**
     * The callback invoked by IdCapture every time that the document is captured.
     */
    @WorkerThread
    override fun onIdCaptured(mode: IdCapture, id: CapturedId) {
        /*
         * Implement to handle data captured from personal identification documents or their
         * elements like a barcode or the Machine Readable Zone (MRZ).
         *
         * This callback is executed on the background thread. We emit to the SharedFlow
         * which will handle thread dispatching.
         */
        val viz = id.viz
        val needsBackScan = viz != null &&
                viz.isBackSideCaptureSupported &&
                viz.capturedSides == CapturedSides.FRONT_ONLY

        val continuousScanEnabled = isContinuousMode()

        /*
         * Scan of the back of the document is enabled, the mode needs to be disabled while the
         * user decides if they want to continue scanning or just display the partial result.
         *
         * Don't capture unnecessarily when the result is displayed without continuous mode.
         */
        if (needsBackScan || !continuousScanEnabled) {
            disableIdCapture()
        }

        scope.launch {
            _scanResults.emit(
                ScanResult(
                    capturedId = id,
                    continuousMode = continuousScanEnabled,
                    needsBackScan = needsBackScan
                )
            )
        }
    }

    @WorkerThread
    override fun onIdRejected(
        mode: IdCapture,
        id: CapturedId?,
        reason: RejectionReason,
    ) {
        /*
         * Implement to handle documents recognized in a frame, but rejected.
         * A document or its part is considered rejected when:
         *   (a) it's a valid personal identification document, but not enabled in the settings,
         *   (b) it's a PDF417 barcode or a Machine Readable Zone (MRZ), but the data is encoded in an unexpected format,
         *   (c) the document meets the conditions of a rejection rule enabled in the settings,
         *   (d) the document has been localized, but could not be captured within a period of time.
         */

        if (!settingsRepository.settings.result.continuousScanEnabled) {
            disableIdCapture()
        }

        scope.launch {
            _rejectedIds.emit(reason)
        }
    }
}
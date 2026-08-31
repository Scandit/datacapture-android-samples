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

package com.scandit.datacapture.idcaptureimageuploadsample.data

import androidx.annotation.WorkerThread
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.id.capture.DriverLicense
import com.scandit.datacapture.id.capture.FullDocumentScanner
import com.scandit.datacapture.id.capture.IdCapture
import com.scandit.datacapture.id.capture.IdCaptureListener
import com.scandit.datacapture.id.capture.IdCaptureScanner
import com.scandit.datacapture.id.capture.IdCaptureSettings
import com.scandit.datacapture.id.capture.IdCard
import com.scandit.datacapture.id.capture.Passport
import com.scandit.datacapture.id.capture.SingleSideScanner
import com.scandit.datacapture.id.data.CapturedId
import com.scandit.datacapture.id.data.IdCaptureRegion
import com.scandit.datacapture.id.data.RejectionReason
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
) : IdCaptureListener {

    /**
     * Coroutine scope for emitting events from background threads.
     * Uses SupervisorJob so failures in one emission don't cancel the entire scope.
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * The IdCapture mode.
     */
    private val idCapture: IdCapture = buildIdCapture(dataCaptureContext)

    /**
     * The stream of Captured IDs.
     */
    private val _capturedIds = MutableSharedFlow<CapturedId>()
    val capturedIds: SharedFlow<CapturedId> = _capturedIds.asSharedFlow()

    /**
     * The stream of rejected IDs.
     */
    private val _rejectedIds = MutableSharedFlow<RejectionReason>()
    val rejectedIds: SharedFlow<RejectionReason> = _rejectedIds.asSharedFlow()

    init {
        idCapture.addListener(this)
    }

    private fun buildIdCapture(dataCaptureContext: DataCaptureContext): IdCapture =
        IdCapture.forDataCaptureContext(
            dataCaptureContext,
            buildIdCaptureSettings(doubleSided = false),
        )

    /**
     * Reconfigure the scanner for the next document before scanning: a single uploaded image is
     * scanned as a single side, two images as a full (front + back) document.
     */
    fun configureScanner(doubleSided: Boolean) {
        idCapture.applySettings(buildIdCaptureSettings(doubleSided))
    }

    private fun buildIdCaptureSettings(doubleSided: Boolean): IdCaptureSettings =
        IdCaptureSettings().apply {
            acceptedDocuments = listOf(
                IdCard(IdCaptureRegion.ANY),
                DriverLicense(IdCaptureRegion.ANY),
                Passport(IdCaptureRegion.ANY),
            )
            this.scanner = IdCaptureScanner(
                physicalDocument = if (doubleSided) {
                    FullDocumentScanner()
                } else {
                    SingleSideScanner(
                        barcode = true,
                        machineReadableZone = true,
                        visualInspectionZone = true,
                    )
                }
            )
            // Report each captured side separately (`onIdCaptured` per side) instead of only once
            // the whole document is complete. This drives the front-then-back feeding flow: the
            // front-side result tells us to feed the back image. Without it, a double-sided document
            // would never complete, because the back side is only fed after the front is captured.
            _impl().setNotifyOnSideCapture(true)
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
     * The callback invoked by IdCapture every time that a document is captured.
     */
    @WorkerThread
    override fun onIdCaptured(mode: IdCapture, id: CapturedId) {
        scope.launch {
            _capturedIds.emit(id)
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
         *   (b) it's a PDF417 barcode or a Machine Readable Zone (MRZ), but the data is encoded
         *       in an unexpected format,
         *   (c) the document meets the conditions of a rejection rule enabled in the settings,
         *   (d) the document has been localized, but could not be captured within a period of time.
         */
        scope.launch {
            _rejectedIds.emit(reason)
        }
    }
}

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

package com.scandit.datacapture.idcaptureimageuploadsample.ui.scan

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scandit.datacapture.id.data.CapturedId
import com.scandit.datacapture.id.data.RejectionReason
import com.scandit.datacapture.idcaptureimageuploadsample.data.DataCaptureContextRepository
import com.scandit.datacapture.idcaptureimageuploadsample.data.IdCaptureRepository
import com.scandit.datacapture.idcaptureimageuploadsample.data.ImageFrameSourceRepository
import com.scandit.datacapture.idcaptureimageuploadsample.di.Injector
import com.scandit.datacapture.idcaptureimageuploadsample.mapper.IdCaptureResultFactory
import com.scandit.datacapture.idcaptureimageuploadsample.ui.result.CaptureResult
import com.scandit.datacapture.idcaptureimageuploadsample.utils.isPdf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * A side of the document picked by the user, classified at pick time so that decoding does not have
 * to re-inspect the file: either a plain [Image] or a single [PdfPage] of a PDF.
 *
 * A back side may point at the second page of a multi-page front PDF (`PdfPage(frontUri, 1)`).
 */
sealed interface PickedDocument {
    val uri: Uri

    data class Image(override val uri: Uri) : PickedDocument

    data class PdfPage(override val uri: Uri, val pageIndex: Int) : PickedDocument
}

/**
 * UI state of the scan screen: the picked sides and whether a scan is in progress.
 *
 * @param front the picked front side, or null if none has been picked yet.
 * @param back the picked back side, or null for a single-sided document.
 * @param processing whether a scan is currently running.
 * @param contextError the message of an invalid DataCaptureContext status (e.g. an invalid
 *   license), or null when there is none. Held in the state, rather than emitted as a one-shot
 *   event, because it is shown in a dialog that persists until dismissed (surviving e.g. rotation).
 */
data class ScanUiState(
    val front: PickedDocument? = null,
    val back: PickedDocument? = null,
    val processing: Boolean = false,
    val contextError: String? = null,
)

/** One-shot outcomes of a scan. */
sealed interface ScanEvent {
    data class Captured(val result: CaptureResult) : ScanEvent
    data class Rejected(val reason: RejectionReason) : ScanEvent
    data class Error(val error: ScanError) : ScanEvent
}

enum class ScanError {
    /** The picked file could not be read or decoded. */
    READ_FILE,

    /** The picked PDF contained no pages to render. */
    PDF_NO_PAGES,

    /** No document was detected in the supplied image(s). */
    NO_DOCUMENT,
}

/**
 * Orchestrates scanning IDs from picked images / PDFs.
 *
 * Flow:
 *  1. Decode the picked image(s) or PDF page(s) into bitmaps off the main thread.
 *  2. Configure the scanner for the document (which resets the session), then feed the first image
 *     as a single frame and enable IdCapture.
 *  3. For double-sided documents, when the front side is captured, feed the back image; IdCapture
 *     combines both sides and reports the complete document.
 *  4. Surface the result, or an error if nothing was detected (result timeout) or the document was
 *     rejected.
 */
class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ScanEvent>()
    val events: SharedFlow<ScanEvent> = _events.asSharedFlow()

    private val dataCaptureContextRepository: DataCaptureContextRepository =
        Injector.dataCaptureContextRepository
    private val idCaptureRepository: IdCaptureRepository = Injector.idCaptureRepository
    private val imageFrameSourceRepository: ImageFrameSourceRepository =
        Injector.imageFrameSourceRepository
    private val imageLoader = Injector.imageLoader
    private val pdfConverter = Injector.pdfConverter

    // State of the in progress scan.
    // The back side still to be fed once the front has been captured, or null when none remains.
    private var pendingBackImage: Bitmap? = null
    private var resultTimeoutJob: Job? = null

    init {
        viewModelScope.launch {
            idCaptureRepository.capturedIds.collect { onIdCaptured(it) }
        }
        viewModelScope.launch {
            idCaptureRepository.rejectedIds.collect { onIdRejected(it) }
        }
        viewModelScope.launch {
            dataCaptureContextRepository.contextErrors.collect { onContextError(it) }
        }
    }

    /**
     * Handle a front side picked by the user. The file is classified here so the rest of the flow
     * does not have to re-inspect it. A multi-page PDF front carries its back side as the second
     * page; any other front clears the back, which must then be picked separately.
     */
    fun onFrontPicked(uri: Uri) {
        val isPdf = getApplication<Application>().isPdf(uri)
        val front = if (isPdf) PickedDocument.PdfPage(uri, pageIndex = 0) else PickedDocument.Image(uri)
        _uiState.update { it.copy(front = front, back = null) }

        if (isPdf) {
            viewModelScope.launch {
                val pageCount = runCatching { pdfConverter.pageCount(uri) }.getOrDefault(0)
                // Guard against a newer front having been picked while we were reading the page count.
                if (pageCount >= 2 && _uiState.value.front?.uri == uri) {
                    _uiState.update { it.copy(back = PickedDocument.PdfPage(uri, pageIndex = 1)) }
                }
            }
        }
    }

    /** Handle a back side picked separately by the user. */
    fun onBackPicked(uri: Uri) {
        val back = if (getApplication<Application>().isPdf(uri)) {
            PickedDocument.PdfPage(uri, pageIndex = 0)
        } else {
            PickedDocument.Image(uri)
        }
        _uiState.update { it.copy(back = back) }
    }

    /** Start a scan from the currently picked sides. No-op if no front is picked or already running. */
    fun scan() {
        val state = _uiState.value
        val front = state.front ?: return
        if (state.processing) return

        _uiState.update { it.copy(processing = true) }

        viewModelScope.launch {
            val images = try {
                loadImages(front, state.back)
            } catch (e: Exception) {
                resetScan()
                val error = (e as? ScanException)?.error ?: ScanError.READ_FILE
                _events.emit(ScanEvent.Error(error))
                return@launch
            }

            idCaptureRepository.configureScanner(doubleSided = images.back != null)
            pendingBackImage = images.back
            feedImage(images.front)
            idCaptureRepository.enableIdCapture()
        }
    }

    private suspend fun loadImages(front: PickedDocument, back: PickedDocument?): DocumentImages =
        DocumentImages(front = decode(front), back = back?.let { decode(it) })

    /**
     * Decode a picked side into a single bitmap: images are loaded directly, PDF pages are
     * rasterised at the side's page index.
     */
    private suspend fun decode(document: PickedDocument): Bitmap = when (document) {
        is PickedDocument.Image -> imageLoader.loadBitmap(document.uri)
        is PickedDocument.PdfPage -> pdfConverter.renderPage(document.uri, document.pageIndex)
            ?: throw ScanException(ScanError.PDF_NO_PAGES)
    }

    private fun feedImage(image: Bitmap) {
        imageFrameSourceRepository.feedImage(image)
        startResultTimeout()
    }

    private fun onIdCaptured(capturedId: CapturedId) {
        if (!_uiState.value.processing) return

        val back = pendingBackImage
        if (back != null) {
            pendingBackImage = null
            feedImage(back)
        } else {
            val result = IdCaptureResultFactory.extract(capturedId)
            resetScan()
            _uiState.update { it.copy(front = null, back = null) }
            viewModelScope.launch {
                _events.emit(ScanEvent.Captured(result))
            }
        }
    }

    private fun onIdRejected(reason: RejectionReason) {
        if (!_uiState.value.processing) return
        resetScan()
        viewModelScope.launch {
            _events.emit(ScanEvent.Rejected(reason))
        }
    }

    /**
     * A context error (e.g. an invalid license) means scanning cannot proceed, so abort any scan
     * in progress and surface the message.
     */
    private fun onContextError(message: String) {
        if (_uiState.value.processing) {
            resetScan()
        }
        _uiState.update { it.copy(contextError = message) }
    }

    /** Dismiss the context error dialog. */
    fun dismissContextError() {
        _uiState.update { it.copy(contextError = null) }
    }

    private fun resetScan() {
        _uiState.update { it.copy(processing = false) }
        cancelResultTimeout()
        idCaptureRepository.disableIdCapture()
        imageFrameSourceRepository.clear()
        pendingBackImage = null
    }

    private fun startResultTimeout() {
        cancelResultTimeout()
        resultTimeoutJob = viewModelScope.launch {
            delay(RESULT_TIMEOUT_MS.milliseconds)
            if (_uiState.value.processing) {
                resetScan()
                viewModelScope.launch {
                    _events.emit(ScanEvent.Error(ScanError.NO_DOCUMENT))
                }
            }
        }
    }

    private fun cancelResultTimeout() {
        resultTimeoutJob?.cancel()
        resultTimeoutJob = null
    }

    private data class DocumentImages(val front: Bitmap, val back: Bitmap?)

    private class ScanException(val error: ScanError) : Exception()

    companion object {
        private const val RESULT_TIMEOUT_MS = 10_000L
    }
}

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

package com.scandit.datacapture.inventoryauditsample.scan

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModel
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingListener
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSession
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingAdvancedOverlay
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingAdvancedOverlayListener
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.geometry.Anchor
import com.scandit.datacapture.core.common.geometry.FloatWithUnit
import com.scandit.datacapture.core.common.geometry.MeasureUnit
import com.scandit.datacapture.core.common.geometry.PointWithUnit
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.inventoryauditsample.models.DataCaptureManager
import com.scandit.datacapture.inventoryauditsample.scan.bubble.data.BubbleData
import com.scandit.datacapture.inventoryauditsample.scan.bubble.data.BubbleDataProvider
import java.util.concurrent.atomic.AtomicBoolean

class ScanViewModel : ViewModel(), BarcodeTrackingListener, BarcodeTrackingAdvancedOverlayListener {
    private val camera = DataCaptureManager.camera
    val barcodeTracking = DataCaptureManager.barcodeTracking
    private val mainHandler = Handler(Looper.getMainLooper())
    private var listener: ScanViewModelListener? = null
    private val frozen = AtomicBoolean(false)
    val dataCaptureContext: DataCaptureContext
        get() = DataCaptureManager.dataCaptureContext

    init {
        // Register self as a listener to get informed whenever the tracking session is updated.
        DataCaptureManager.barcodeTracking.addListener(this)
    }

    fun setListener(listener: ScanViewModelListener?) {
        this.listener = listener
    }

    fun resumeScanning() {
        if (frozen.get()) return
        resumeScanningInternal()
    }

    fun pauseScanning() {
        if (frozen.get()) return
        pauseScanningInternal()
    }

    fun startFrameSource() {
        if (frozen.get()) return
        startFrameSourceInternal()
    }

    fun stopFrameSource() {
        if (frozen.get()) return
        stopFrameSourceInternal()
    }

    fun toggleFreeze() {
        if (frozen.get()) {
            unfreezeInternal()
        } else {
            freezeInternal()
        }
    }

    fun isFrozen(): Boolean = frozen.get()

    private fun freezeInternal() {
        frozen.set(true)
        pauseScanningInternal()
        stopFrameSourceInternal()
        notifyFrozenListenerInternal()
    }

    private fun unfreezeInternal() {
        frozen.set(false)
        startFrameSourceInternal()
        resumeScanningInternal()
        notifyFrozenListenerInternal()
    }

    private fun startFrameSourceInternal() {
        camera.switchToDesiredState(FrameSourceState.ON)
    }

    private fun resumeScanningInternal() {
        DataCaptureManager.barcodeTracking.isEnabled = true
    }

    private fun pauseScanningInternal() {
        DataCaptureManager.barcodeTracking.isEnabled = false
    }

    private fun stopFrameSourceInternal() {
        camera.switchToDesiredState(FrameSourceState.OFF)
    }

    private fun notifyFrozenListenerInternal() {
        listener?.onFrozenChanged(frozen.get())
    }

    override fun onSessionUpdated(
            mode: BarcodeTracking,
            session: BarcodeTrackingSession,
            data: FrameData
    ) {
        if (frozen.get() || listener == null) return

        for (identifier in session.removedTrackedBarcodes) {
            mainHandler.post { listener?.removeBubbleView(identifier) }
        }
    }

    override fun onObservationStarted(barcodeTracking: BarcodeTracking) {
        // Not interested in this callback.
    }

    override fun onObservationStopped(barcodeTracking: BarcodeTracking) {
        // Not interested in this callback.
    }

    override fun onCleared() {
        super.onCleared()
        barcodeTracking.removeListener(this)
    }

    override fun viewForTrackedBarcode(
            overlay: BarcodeTrackingAdvancedOverlay,
            trackedBarcode: TrackedBarcode
    ): View? = listener?.getOrCreateViewForBubbleData(
                trackedBarcode,
                BubbleDataProvider.getDataForBarcode(trackedBarcode.barcode.data!!)
        )

    override fun anchorForTrackedBarcode(
            overlay: BarcodeTrackingAdvancedOverlay,
            trackedBarcode: TrackedBarcode
    ): Anchor = Anchor.TOP_CENTER

    override fun offsetForTrackedBarcode(
            overlay: BarcodeTrackingAdvancedOverlay,
            trackedBarcode: TrackedBarcode,
            view: View
    ): PointWithUnit {
        // We want to center the view on top of the barcode.
        return PointWithUnit(
                FloatWithUnit(0f, MeasureUnit.FRACTION),
                FloatWithUnit(-1f, MeasureUnit.FRACTION)
        )
    }

    interface ScanViewModelListener {
        fun getOrCreateViewForBubbleData(barcode: TrackedBarcode, bubbleData: BubbleData): View
        fun removeBubbleView(identifier: Int)
        fun onFrozenChanged(frozen: Boolean)
    }
}

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

package com.scandit.datacapture.matrixscanbubblessample.scan;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingListener;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSession;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingAdvancedOverlay;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingAdvancedOverlayListener;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.Anchor;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.matrixscanbubblessample.models.DataCaptureManager;
import com.scandit.datacapture.matrixscanbubblessample.scan.bubble.data.BubbleData;
import com.scandit.datacapture.matrixscanbubblessample.scan.bubble.data.BubbleDataProvider;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScanViewModel extends ViewModel implements BarcodeTrackingListener,
        BarcodeTrackingAdvancedOverlayListener {

    private final DataCaptureManager dataCaptureManager = DataCaptureManager.CURRENT;
    private final Camera camera = dataCaptureManager.camera;
    final BarcodeTracking barcodeTracking = dataCaptureManager.barcodeTracking;

    private final BubbleDataProvider bubbleDataProvider = new BubbleDataProvider();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ScanViewModelListener listener;

    private AtomicBoolean frozen = new AtomicBoolean(false);

    public ScanViewModel() {
        // Register self as a listener to get informed whenever the tracking session is updated.
        dataCaptureManager.barcodeTracking.addListener(this);
    }

    DataCaptureContext getDataCaptureContext() {
        return dataCaptureManager.dataCaptureContext;
    }

    void setListener(@Nullable ScanViewModelListener listener) {
        this.listener = listener;
    }

    void resumeScanning() {
        if (frozen.get()) return;
        resumeScanningInternal();
    }

    void pauseScanning() {
        if (frozen.get()) return;
        pauseScanningInternal();
    }

    void startFrameSource() {
        if (frozen.get()) return;
        startFrameSourceInternal();
    }

    void stopFrameSource() {
        if (frozen.get()) return;
        stopFrameSourceInternal();
    }

    void toggleFreeze() {
        if (frozen.get()) {
            unfreezeInternal();
        } else {
            freezeInternal();
        }
    }

    boolean isFrozen() {
        return frozen.get();
    }

    private void freezeInternal() {
        frozen.set(true);
        pauseScanningInternal();
        stopFrameSourceInternal();
        notifyFrozenListenerInternal();
    }

    private void unfreezeInternal() {
        frozen.set(false);
        startFrameSourceInternal();
        resumeScanningInternal();
        notifyFrozenListenerInternal();
    }

    private void startFrameSourceInternal() {
        if (camera != null) {
            camera.switchToDesiredState(FrameSourceState.ON);
        }
    }

    private void resumeScanningInternal() {
        dataCaptureManager.barcodeTracking.setEnabled(true);
    }

    private void pauseScanningInternal() {
        dataCaptureManager.barcodeTracking.setEnabled(false);
    }

    private void stopFrameSourceInternal() {
        if (camera != null) {
            camera.switchToDesiredState(FrameSourceState.OFF);
        }
    }

    private void notifyFrozenListenerInternal() {
        if (listener != null) {
            listener.onFrozenChanged(frozen.get());
        }
    }

    @Override
    public void onSessionUpdated(
            @NonNull BarcodeTracking barcodeTracking,
            @NonNull BarcodeTrackingSession session,
            @NonNull FrameData data
    ) {
        if (frozen.get() || listener == null) return;

        for (int identifier : session.getRemovedTrackedBarcodes()) {
            removeBubbleViewForIdentifierOnMainThread(identifier);
        }

        for (final TrackedBarcode trackedBarcode : session.getTrackedBarcodes().values()) {
            String code = trackedBarcode.getBarcode().getData();
            if (code == null || code.isEmpty()) continue;

            // We show or hide the bubble depending on its size compared to the device screen.
            setBubbleVisibilityOnMainThread(
                    trackedBarcode, listener.shouldShowBubble(trackedBarcode)
            );
        }
    }

    private void setBubbleVisibilityOnMainThread(
            final TrackedBarcode barcode, final boolean visible
    ) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.setBubbleVisibility(barcode, visible);
                }
            }
        });
    }

    private void removeBubbleViewForIdentifierOnMainThread(final int identifier) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.removeBubbleView(identifier);
                }
            }
        });
    }

    @Override
    public void onObservationStarted(@NonNull BarcodeTracking barcodeTracking) {}

    @Override
    public void onObservationStopped(@NonNull BarcodeTracking barcodeTracking) {}

    @Override
    protected void onCleared() {
        super.onCleared();
        barcodeTracking.removeListener(this);
    }

    @Override
    public View viewForTrackedBarcode(
            @NonNull BarcodeTrackingAdvancedOverlay overlay,
            @NonNull TrackedBarcode trackedBarcode
    ) {
        return listener.getOrCreateViewForBubbleData(
                trackedBarcode,
                bubbleDataProvider.getDataForBarcode(trackedBarcode.getBarcode().getData()),
                listener.shouldShowBubble(trackedBarcode)
        );
    }

    @NonNull
    @Override
    public Anchor anchorForTrackedBarcode(
            @NonNull BarcodeTrackingAdvancedOverlay overlay,
            @NonNull TrackedBarcode trackedBarcode
    ) {
        return Anchor.TOP_CENTER;
    }

    @NonNull
    @Override
    public PointWithUnit offsetForTrackedBarcode(
            @NonNull BarcodeTrackingAdvancedOverlay overlay,
            @NonNull TrackedBarcode trackedBarcode,
            @NonNull View view
    ) {
        // We want to center the view on top of the barcode.
        return new PointWithUnit(
                new FloatWithUnit(0f, MeasureUnit.FRACTION),
                new FloatWithUnit(-1f, MeasureUnit.FRACTION)
        );
    }

    public interface ScanViewModelListener {

        boolean shouldShowBubble(TrackedBarcode barcode);

        @Nullable
        View getOrCreateViewForBubbleData(
                TrackedBarcode barcode,
                BubbleData bubbleData,
                boolean visible
        );

        void setBubbleVisibility(TrackedBarcode barcode, boolean visible);

        void removeBubbleView(int identifier);

        void onFrozenChanged(boolean frozen);
    }
}

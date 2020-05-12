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

package com.scandit.datacapture.searchandfindsample.find;

import android.graphics.Color;
import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlay;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlayListener;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.searchandfindsample.models.DataCaptureManager;
import com.scandit.datacapture.searchandfindsample.models.FindDataCaptureManager;
import org.jetbrains.annotations.NotNull;


public final class FindScanViewModel extends ViewModel implements BarcodeTrackingBasicOverlayListener {

    private FindDataCaptureManager dataCaptureManager = DataCaptureManager.FIND;

    // The data of the barcode to find.
    private final String data;

    final DataCaptureContext dataCaptureContext = dataCaptureManager.dataCaptureContext;
    final BarcodeTracking barcodeTracking = dataCaptureManager.barcodeTracking;

    private final Brush matchBrush =
            new Brush(Color.parseColor("#444CAF50"), Color.parseColor("#4CAF50"), 2f);
    private final Brush rejectBrush = new Brush(Color.TRANSPARENT, Color.WHITE, 2f);

    FindScanViewModel(Symbology symbology, String data) {
        this.data = data;

        // We change the barcode tracking settings to enable only the previously scanned symbology.
        dataCaptureManager.setupForSymbology(symbology);
    }

    void resumeScanning() {
        dataCaptureManager.camera().applySettings(BarcodeTracking.createRecommendedCameraSettings());
        dataCaptureContext.addMode(barcodeTracking);
        barcodeTracking.setEnabled(true);
    }

    void pauseScanning() {
        dataCaptureContext.removeMode(barcodeTracking);
        barcodeTracking.setEnabled(false);
    }

    @NotNull
    @Override
    public Brush brushForTrackedBarcode(
            @NotNull BarcodeTrackingBasicOverlay barcodeTrackingBasicOverlay,
            @NotNull TrackedBarcode trackedBarcode
    ) {
        String barcodeData = trackedBarcode.getBarcode().getData();
        if (barcodeData != null && barcodeData.equals(data)) {
            return matchBrush;
        } else {
            return rejectBrush;
        }
    }

    @Override
    public void onTrackedBarcodeTapped(
            @NotNull BarcodeTrackingBasicOverlay barcodeTrackingBasicOverlay,
            @NotNull TrackedBarcode trackedBarcode
    ) {}
}

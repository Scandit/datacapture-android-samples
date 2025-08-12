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

package com.scandit.datacapture.searchandfindsample.search;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.spark.capture.SparkScan;
import com.scandit.datacapture.barcode.spark.capture.SparkScanListener;
import com.scandit.datacapture.barcode.spark.capture.SparkScanSession;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.searchandfindsample.models.SearchDataCaptureManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SearchScanViewModel extends ViewModel implements SparkScanListener {

    private final SearchDataCaptureManager dataCaptureManager = new SearchDataCaptureManager();

    final DataCaptureContext dataCaptureContext = dataCaptureManager.dataCaptureContext;
    final SparkScan sparkScan = dataCaptureManager.sparkScan;

    private Listener listener = null;

    private final List<Barcode> scannedBarcodes = new ArrayList<>();
    private final Object lock = new Object();

    public SearchScanViewModel() {
        // Register self as a listener to get informed whenever a new barcode got recognized.
        sparkScan.addListener(this);
    }

    @Override
    public void onBarcodeScanned(
            @NotNull SparkScan sparkScan,
            @NotNull SparkScanSession session,
            @Nullable FrameData data
    ) {
        synchronized (lock) {
            Barcode newlyRecognizedBarcode = session.getNewlyRecognizedBarcode();
            if (newlyRecognizedBarcode != null) {
                scannedBarcodes.add(newlyRecognizedBarcode);

                if (listener != null) {
                    listener.onCodeScanned(newlyRecognizedBarcode, data);
                }
            }
        }
    }

    void resetCodes() {
        synchronized (lock) {
            scannedBarcodes.clear();
        }
    }

    void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    void onSearchClicked() {
        synchronized (lock) {
            if (listener != null) {
                listener.goToFind(scannedBarcodes);
            }
        }
    }

    @Override
    public void onSessionUpdated(
            @NotNull SparkScan sparkScan,
            @NotNull SparkScanSession session,
            @Nullable FrameData data
    ) {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Unregister self as listener when clearing the ViewModel.
        sparkScan.removeListener(this);
    }

    interface Listener {
        void onCodeScanned(Barcode barcode, FrameData data);

        void goToFind(List<Barcode> barcodesToFind);
    }
}

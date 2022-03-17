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

package com.scandit.datacapture.reorderfromcatalogsample.data;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelection;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionListener;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionSession;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionSettings;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionTapSelection;
import com.scandit.datacapture.barcode.selection.ui.overlay.BarcodeSelectionBasicOverlay;
import com.scandit.datacapture.barcode.selection.ui.overlay.BarcodeSelectionBasicOverlayStyle;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The repository to interact with the BarcodeSelection mode.
 */
public class BarcodeSelectionRepository implements BarcodeSelectionListener {
    /**
     * Overlays provide UI to aid the user in the capture process and need to be attached
     * to DataCaptureView.
     */
    private final BarcodeSelectionBasicOverlay overlay;

    /**
     * The list of selected barcodes to show in the results screen.
     */
    private volatile List<BarcodeResult> selectedResults = new ArrayList<>();

    /**
     * The DataCaptureContext that the current BarcodeSelection mode is attached to.
     */
    public final DataCaptureContext dataCaptureContext;

    /**
     * The current BarcodeSelection.
     */
    private final BarcodeSelection barcodeSelection;

    public BarcodeSelectionRepository(DataCaptureContext dataCaptureContext) {
        this.dataCaptureContext = dataCaptureContext;

        BarcodeSelectionSettings settings = new BarcodeSelectionSettings();

        settings.setSelectionType(new BarcodeSelectionTapSelection());

        /*
         * The settings instance initially has all types of barcodes (symbologies) disabled.
         * In your own app ensure that you only enable the symbologies that your app requires
         * as every additional enabled symbology has an impact on processing times.
         */
        settings.enableSymbology(Symbology.EAN8, true);
        settings.enableSymbology(Symbology.EAN13_UPCA, true);
        settings.enableSymbology(Symbology.UPCE, true);
        settings.enableSymbology(Symbology.CODE39, true);
        settings.enableSymbology(Symbology.CODE128, true);

        barcodeSelection = BarcodeSelection.forDataCaptureContext(dataCaptureContext, settings);
        barcodeSelection.addListener(this);

        /*
         * Setup an overlay that provides UI to aid the user in the capture and selection
         * process. It later needs to be attached to DataCaptureView.
         */
        overlay = BarcodeSelectionBasicOverlay.newInstance(
                barcodeSelection,
                null,
                BarcodeSelectionBasicOverlayStyle.DOT
        );
        overlay.setFrozenBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * Overlays provide UI to aid the user in the capture process and need to be attached
     * to DataCaptureView.
     */
    public BarcodeSelectionBasicOverlay getBarcodeCaptureOverlay() {
        return overlay;
    }

    /**
     * Retrieve the currently selected barcodes.
     */
    public List<BarcodeResult> getSelectedResults() {
        return selectedResults;
    }

    /**
     * Clears the list of results and resets barcode selection.
     */
    public void clearSelection() {
        selectedResults = new ArrayList<>();
        barcodeSelection.reset();
    }

    /**
     * Select all currently recognised but unselected barcodes.
     */
    public void selectAllUnselectedCodes() {
        barcodeSelection.selectUnselectedBarcodes();
    }

    @Override
    public void onSelectionUpdated(
            @NonNull BarcodeSelection barcodeSelection,
            @NonNull BarcodeSelectionSession session,
            @Nullable FrameData frameData
    ) {
        /*
         * Implement to handle selection updates.
         *
         * This callback is executed on the background thread.
         * In this sample we just need to keep track of what codes are being selected to then
         * display them in the results screen.
         */
        List<BarcodeResult> selectedResults = new ArrayList<>();
        for (Barcode selectedBarcode : session.getSelectedBarcodes()) {
            selectedResults.add(
                    new BarcodeResult(selectedBarcode.getData(), selectedBarcode.getSymbology())
            );
        }
        this.selectedResults = selectedResults;
    }

    @Override
    public void onSessionUpdated(
            @NonNull BarcodeSelection barcodeSelection,
            @NonNull BarcodeSelectionSession session,
            @Nullable FrameData frameData
    ) {
        // In this sample, we are not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStarted(@NotNull BarcodeSelection mode) {
        // In this sample, we are not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStopped(@NotNull BarcodeSelection mode) {
        // In this sample, we are not interested in this callback.
    }
}

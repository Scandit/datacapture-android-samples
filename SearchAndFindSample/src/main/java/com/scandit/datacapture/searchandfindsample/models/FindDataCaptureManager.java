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

package com.scandit.datacapture.searchandfindsample.models;

import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSettings;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.source.Camera;

public final class FindDataCaptureManager {

    private final DataCaptureManager baseDataCaptureManager = DataCaptureManager.CURRENT;
    public final DataCaptureContext dataCaptureContext = baseDataCaptureManager.dataCaptureContext;
    public final BarcodeTracking barcodeTracking;

    FindDataCaptureManager() {
        // Create new barcode tracking mode with default settings.
        barcodeTracking = BarcodeTracking.forDataCaptureContext(
                dataCaptureContext, new BarcodeTrackingSettings()
        );
        dataCaptureContext.removeMode(barcodeTracking);
    }

    public void setupForSymbology(Symbology symbology) {
        // The barcode tracking process is configured through barcode tracking settings
        // which are then applied to the barcode tracking instance that manages barcode tracking.
        BarcodeTrackingSettings settings = new BarcodeTrackingSettings();

        // We enable only the given symbology.
        settings.enableSymbology(symbology, true);

        // We apply the new settings to the barcode tracking.
        barcodeTracking.applySettings(settings);
    }

    public Camera camera() {
        return baseDataCaptureManager.camera;
    }
}

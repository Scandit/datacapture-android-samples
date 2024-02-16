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

import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.core.area.RadiusLocationSelection;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.time.TimeInterval;

import java.util.HashSet;

public final class SearchDataCaptureManager {

    private final DataCaptureManager baseDataCaptureManager = DataCaptureManager.CURRENT;
    public final DataCaptureContext dataCaptureContext = baseDataCaptureManager.dataCaptureContext;
    public final BarcodeCapture barcodeCapture;

    public SearchDataCaptureManager() {
        // The barcode capturing process is configured through barcode capture settings
        // which are then applied to the barcode capture instance that manages barcode recognition.
        BarcodeCaptureSettings settings = new BarcodeCaptureSettings();
        HashSet<Symbology> enabledSymbologies = new HashSet<>();
        enabledSymbologies.add(Symbology.EAN8);
        enabledSymbologies.add(Symbology.EAN13_UPCA);
        enabledSymbologies.add(Symbology.UPCE);
        enabledSymbologies.add(Symbology.CODE128);
        enabledSymbologies.add(Symbology.CODE39);
        enabledSymbologies.add(Symbology.DATA_MATRIX);
        settings.enableSymbologies(enabledSymbologies);

        // In order not to pick up barcodes outside of the view finder,
        // restrict the code location selection to match the laser line's center.
        settings.setLocationSelection(
                new RadiusLocationSelection(new FloatWithUnit(0f, MeasureUnit.FRACTION))
        );

        // Setting the code duplicate filter to one second means that the scanner won't report
        // the same code as recognized for one second once it's recognized.
        settings.setCodeDuplicateFilter(TimeInterval.seconds(1f));

        // Create new barcode capture mode with the settings from above.
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, settings);
        dataCaptureContext.removeMode(barcodeCapture);
    }

    public Camera camera() {
        return baseDataCaptureManager.camera;
    }
}

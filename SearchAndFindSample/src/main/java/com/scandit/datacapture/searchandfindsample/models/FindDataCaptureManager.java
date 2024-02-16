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
import com.scandit.datacapture.barcode.find.capture.BarcodeFind;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindItem;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindItemSearchOptions;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindSettings;
import com.scandit.datacapture.core.capture.DataCaptureContext;

import java.util.HashSet;
import java.util.Set;

public final class FindDataCaptureManager {

    private final DataCaptureManager baseDataCaptureManager = DataCaptureManager.CURRENT;
    public final DataCaptureContext dataCaptureContext = baseDataCaptureManager.dataCaptureContext;
    private BarcodeFind barcodeFind;

    public BarcodeFind getBarcodeFind() {
        if (barcodeFind == null) {
            // Create new barcode find mode with default settings.
            barcodeFind = new BarcodeFind(new BarcodeFindSettings());
        }
        return barcodeFind;
    }

    public void stopScanningSession() {
        // Stop the scanning session on the BarcodeFind object. This way we can reuse
        // the BarcodeFind instance for a new scanning session.
        barcodeFind.stop();
    }

    public void setupForSymbology(Symbology symbology) {
        // The barcode find process is configured through barcode find settings
        // which are then applied to the barcode find instance.
        BarcodeFindSettings settings = new BarcodeFindSettings();

        // We enable only the given symbology.
        settings.setSymbologyEnabled(symbology, true);

        // We apply the new settings to the barcode find.
        getBarcodeFind().applySettings(settings);
    }

    public void setupSearchedItems(String data) {
        Set<BarcodeFindItem> items = new HashSet<>();
        items.add(new BarcodeFindItem(new BarcodeFindItemSearchOptions(data), null));

        // The BarcodeFind can search a set of items. In this simplified sample, we set only
        // one items, but for real case we can set several at once.
        getBarcodeFind().setItemList(items);
    }
}

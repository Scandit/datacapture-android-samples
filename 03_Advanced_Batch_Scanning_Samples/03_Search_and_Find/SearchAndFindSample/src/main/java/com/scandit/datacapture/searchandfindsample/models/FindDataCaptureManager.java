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

import android.graphics.Bitmap;

import com.scandit.datacapture.barcode.capture.SymbologySettings;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.find.capture.BarcodeFind;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindItem;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindItemContent;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindItemSearchOptions;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindSettings;
import com.scandit.datacapture.core.capture.DataCaptureContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class FindDataCaptureManager {

    private final DataCaptureManager baseDataCaptureManager = DataCaptureManager.CURRENT;
    public final DataCaptureContext dataCaptureContext = baseDataCaptureManager.dataCaptureContext;
    private BarcodeFind barcodeFind;

    public BarcodeFind getBarcodeFind() {
        if (barcodeFind == null) {
            // The find process is configured through BarcodeFind settings
            // which are then applied to the barcode find instance.
            BarcodeFindSettings settings = new BarcodeFindSettings();
            HashSet<Symbology> enabledSymbologies = new HashSet<>();
            enabledSymbologies.add(Symbology.EAN8);
            enabledSymbologies.add(Symbology.EAN13_UPCA);
            enabledSymbologies.add(Symbology.UPCE);
            enabledSymbologies.add(Symbology.CODE128);
            enabledSymbologies.add(Symbology.CODE39);
            enabledSymbologies.add(Symbology.DATA_MATRIX);
            settings.enableSymbologies(enabledSymbologies);

            // Some linear/1d barcode symbologies allow you to encode variable-length data.
            // By default, the Scandit Data Capture SDK only scans barcodes in a certain length range.
            // If your application requires scanning of one of these symbologies, and the length is
            // falling outside the default range, you may need to adjust the "active symbol counts"
            // for this symbology. This is shown in the following few lines of code for one of the
            // variable-length symbologies.
            SymbologySettings symbologySettings =
                settings.getSymbologySettings(Symbology.CODE39);

            HashSet<Short> activeSymbolCounts = new HashSet<>(
                Arrays.asList(new Short[]{7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}));

            symbologySettings.setActiveSymbolCounts(activeSymbolCounts);

            // Create new barcode find mode with the settings.
            barcodeFind = new BarcodeFind(settings);
        }
        return barcodeFind;
    }

    public void stopScanningSession() {
        // Stop the scanning session on the BarcodeFind object. This way we can reuse
        // the BarcodeFind instance for a new scanning session.
        barcodeFind.stop();
    }

    public void setupSearchedItems(ArrayList<String> datas) {
        Set<BarcodeFindItem> items = new HashSet<>();

        Bitmap emptyBitmap =
            Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        // Here you can customize the content of the find items.
        // Now we set empty bitmap as find item content to hide the image in the card.
        BarcodeFindItemContent content =
            new BarcodeFindItemContent(null, null, emptyBitmap);

        for (String data : datas) {
            items.add(new BarcodeFindItem(new BarcodeFindItemSearchOptions(data, null), content));
        }

        // The BarcodeFind can search a set of items.
        getBarcodeFind().setItemList(items);
    }
}

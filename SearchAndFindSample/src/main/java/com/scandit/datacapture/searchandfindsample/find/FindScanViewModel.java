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

import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.find.capture.BarcodeFind;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindItem;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindItemSearchOptions;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.searchandfindsample.models.DataCaptureManager;
import com.scandit.datacapture.searchandfindsample.models.FindDataCaptureManager;

import java.util.HashSet;
import java.util.Set;


public final class FindScanViewModel extends ViewModel {

    private final FindDataCaptureManager dataCaptureManager = DataCaptureManager.FIND;

    final DataCaptureContext dataCaptureContext = dataCaptureManager.dataCaptureContext;
    final BarcodeFind barcodeFind = dataCaptureManager.barcodeFind;

    FindScanViewModel(Symbology symbology, String data) {

        // We change the barcode tracking settings to enable only the previously scanned symbology.
        dataCaptureManager.setupForSymbology(symbology);
        // We setup the list of searched items.
        dataCaptureManager.setupSearchedItems(data);
    }
}

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

package com.scandit.datacapture.matrixscancountsimplesample;

import com.scandit.datacapture.barcode.count.capture.BarcodeCountSession;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.matrixscancountsimplesample.data.ScanDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

// Singleton object that centralises barcode management.
public class BarcodeManager {

    // Shared instance in singleton should only be available through getInstance() method.
    private static BarcodeManager sharedInstance = null;

    // This method provides access to the shared BarcodeManager instance.
    public static BarcodeManager getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new BarcodeManager();
        }
        return sharedInstance;
    }

    private Collection<Barcode> scannedBarcodes = new ArrayList<>();

    // Update the list of barcodes with the contents of the current session.
    // getRecognizedBarcodes() carries every barcode counted so far, including the ones counted
    // before a short interruption such as the app being sent to the background.
    public void updateWithSession(BarcodeCountSession session) {
        scannedBarcodes = new ArrayList<>(session.getRecognizedBarcodes());
    }

    // Create a map of barcodes to be passed to the scan results screen.
    public HashMap<String, ScanDetails> getScanResults() {
        HashMap<String, ScanDetails> scanResults = new HashMap<>();

        // Add the scanned Barcode objects to the results map.
        for (Barcode barcode : scannedBarcodes) {
            addBarcodeToResultsMap(barcode, scanResults);
        }

        return scanResults;
    }

    private void addBarcodeToResultsMap(Barcode barcode, HashMap<String, ScanDetails> scanResults) {
        String barcodeData = barcode.getData();
        String symbology = barcode.getSymbology().name();
        if (barcodeData == null) return;

        ScanDetails scanDetails = scanResults.get(barcodeData);
        if (scanDetails != null) {
            scanDetails.increaseQuantity();
            scanResults.put(barcodeData, scanDetails);
        } else {
            scanResults.put(barcodeData, new ScanDetails(barcodeData, symbology));
        }
    }

    // Reset the barcodes list.
    public void reset() {
        scannedBarcodes.clear();
    }
}

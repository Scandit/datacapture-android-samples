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

package com.scandit.datacapture.receivingsample.managers;

import com.scandit.datacapture.barcode.count.capture.BarcodeCountSession;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;

import java.util.ArrayList;
import java.util.List;

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

    private final ArrayList<Barcode> scannedBarcodes = new ArrayList<>();
    private final ArrayList<Barcode> additionalBarcodes = new ArrayList<>();

    private BarcodeManager() {
    }

    // Adds a single scanned barcode to the list.
    public void saveBarcode(Barcode barcode) {
        scannedBarcodes.add(barcode);
    }

    // Update lists of barcodes with the contents of the current session.
    public void updateWithSession(BarcodeCountSession session) {
        scannedBarcodes.clear();
        for (TrackedBarcode trackedBarcode : session.getRecognizedBarcodes().values()) {
            scannedBarcodes.add(trackedBarcode.getBarcode());
        }

        additionalBarcodes.clear();
        additionalBarcodes.addAll(session.getAdditionalBarcodes());
    }

    // Returns all scanned barcodes.
    public List<Barcode> getAllBarcodes() {
        List<Barcode> allBarcodes = new ArrayList<>();
        allBarcodes.addAll(scannedBarcodes);
        allBarcodes.addAll(additionalBarcodes);
        return allBarcodes;
    }

    // Reset the barcodes lists.
    public void reset() {
        scannedBarcodes.clear();
        additionalBarcodes.clear();
    }

    // Remove all instances of a barcode from the list.
    public void removeBarcodesWithData(String data) {
        removeBarcodesFromArray(scannedBarcodes, data);
        removeBarcodesFromArray(additionalBarcodes, data);
    }

    private void removeBarcodesFromArray(ArrayList<Barcode> list, String data) {
        ArrayList<Barcode> barcodesToRemove = new ArrayList<>();
        for (Barcode barcode : list) {
            if (barcode.getData() != null && barcode.getData().equals(data)) {
                barcodesToRemove.add(barcode);
            }
        }
        list.removeAll(barcodesToRemove);
    }
}

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

package com.scandit.datacapture.expirymanagementsample.data;

import java.io.Serializable;

public final class ScanResult implements Serializable {

    public final String barcodeData;
    public final String barcodeSymbology;
    public final boolean isExpired;
    public int quantity;

    public ScanResult(String barcodeData, String barcodeSymbology, boolean isExpired) {
        this.barcodeData = barcodeData;
        this.barcodeSymbology = barcodeSymbology;
        this.isExpired = isExpired;
        this.quantity = 1;
    }

    public void increaseQuantity() {
        this.quantity += 1;
    }
}

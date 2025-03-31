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

import com.scandit.datacapture.barcode.data.Symbology;

public class BarcodeResult {
    private final String barcode;
    private final Symbology symbology;

    public BarcodeResult(String barcode, Symbology symbology) {
        this.barcode = barcode;
        this.symbology = symbology;
    }

    public String getBarcode() {
        return barcode;
    }

    public Symbology getSymbology() {
        return symbology;
    }
}

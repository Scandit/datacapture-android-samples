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

package com.scandit.datacapture.matrixscansimplesample.data;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;

import java.io.Serializable;
import java.util.Objects;

// Final data class holding basic information about a scanned barcode.
public final class ScanResult implements Serializable {

    public final Symbology symbology;
    public final String data;

    public ScanResult(Barcode barcode) {
        symbology = barcode.getSymbology();
        data = barcode.getData() != null ? barcode.getData() : "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbology, data);
    }
}

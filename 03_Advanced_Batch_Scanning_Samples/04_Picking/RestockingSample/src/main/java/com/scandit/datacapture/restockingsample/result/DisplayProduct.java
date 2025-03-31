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

package com.scandit.datacapture.restockingsample.result;

import androidx.annotation.NonNull;

/**
 * Class used in a list to display a Product.
 */
public class DisplayProduct {
    @NonNull
    public final String identifier;

    public final int quantityToPick;

    @NonNull
    public final String barcodeData;

    public final boolean picked;

    public DisplayProduct(
        @NonNull String identifier,
        int quantityToPick,
        @NonNull
        String barcodeData,
        boolean picked
    ) {
        this.identifier = identifier;
        this.quantityToPick = quantityToPick;
        this.barcodeData = barcodeData;
        this.picked = picked;
    }
}


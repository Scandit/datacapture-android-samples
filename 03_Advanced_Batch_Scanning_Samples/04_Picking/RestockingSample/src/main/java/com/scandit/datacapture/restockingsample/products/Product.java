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

package com.scandit.datacapture.restockingsample.products;

import androidx.annotation.NonNull;

import com.scandit.datacapture.restockingsample.result.DisplayProduct;

/**
 * A simple product representation for demo purposes.
 */
public class Product {
    /**
     * Identifier, must be unique per product.
     */
    @NonNull
    public final String identifier;

    public int quantityToPick;

    /**
     * The content of the barcode that match the product. Multiple barcode can point to the same
     * products.
     */
    @NonNull
    public final String[] barcodeData;

    public Product(@NonNull String identifier, int quantityToPick, @NonNull String... barcodeData) {
        this.identifier = identifier;
        this.quantityToPick = quantityToPick;
        this.barcodeData = barcodeData;
    }

    public DisplayProduct getDisplayProduct(String itemData, boolean picked) {
        return new DisplayProduct(identifier, quantityToPick, itemData, picked);
    }
}

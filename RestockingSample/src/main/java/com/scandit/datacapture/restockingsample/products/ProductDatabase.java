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
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fake product database.
 * A real application would consider using an API call to query for a list of products,
 * backed up by a local database.
 */
public class ProductDatabase {

    @NonNull
    private final List<Product> products;

    public ProductDatabase(Product... products) {
        this.products = new ArrayList<>();

        Collections.addAll(this.products, products);
    }

    @Nullable
    public Product getProductForItemData(@NonNull String itemData) {
        for (Product product : products) {
            for (String barcode : product.barcodeData) {
                if (itemData.equals(barcode)) {
                    return product;
                }
            }
        }
        return null;
    }

    @NonNull
    public List<Product> listAllProducts() {
        return new ArrayList<>(products);
    }

    public boolean verifyProductInDatabase(@NonNull String itemData) {
        // Here you would check your local database/API
        return true;
    }

    public int getProductListCount() {
        return products.size();
    }
}

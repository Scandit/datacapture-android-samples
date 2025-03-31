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

import com.scandit.datacapture.barcode.pick.capture.BarcodePickActionCallback;
import com.scandit.datacapture.barcode.pick.data.BarcodePickProduct;
import com.scandit.datacapture.barcode.pick.data.BarcodePickProductProviderCallback;
import com.scandit.datacapture.barcode.pick.data.BarcodePickProductProviderCallbackItem;
import com.scandit.datacapture.restockingsample.result.DisplayProduct;
import com.scandit.datacapture.restockingsample.result.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper class to query the list of products and to maintain the
 * list of items that are picked and scanned.
 * A real application would implement a database and network call, and we simulate it here by
 * using an {@link ExecutorService} and introducing some artificial delay.
 */
public class ProductManager {
    private static ProductManager sharedInstance;

    @NonNull
    public static ProductManager getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new ProductManager();
        }
        return sharedInstance;
    }

    private final ProductDatabase productDatabase;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Random waitRandomizer = new Random();

    private Set<String> allScannedCodes = new HashSet<>();
    private Set<String> allPickedCodes = new HashSet<>();

    public ProductManager() {
        productDatabase = new ProductDatabase(
            /* Add your Products here:
             * new Product("name", "identifier", "barcode1", " barcode2", ...)
             */
            new Product("Item A", 1, "8414792869912", "3714711193285", "4951107312342", "1520070879331"),
            new Product("Item B", 1, "1318890267144", "9866064348233", "4782150677788", "2371266523793"),
            new Product("Item C", 1, "5984430889778", "7611879254123")
        );
    }

    /**
     * Creates the list of {@link BarcodePickProduct} that we need to pick.
     *
     * @return A list of items that can directly be ingested when creating our BarcodePick object.
     */
    @NonNull
    public Set<BarcodePickProduct> getBarcodePickProducts() {
        HashSet<BarcodePickProduct> pickProducts = new HashSet<>();
        for (Product product : productDatabase.listAllProducts()) {
            pickProducts.add(
                new BarcodePickProduct(product.identifier, product.quantityToPick));
        }
        return pickProducts;
    }


    /**
     * Asynchronously match scanned barcode content with our products, to inform the UI if an item
     * is searched for or not.
     *
     * @param itemsData The list of barcode to match against.
     * @param callback  The callback to call with the results.
     */
    public void convertBarcodesToCallbackItemsAsync(
        @NonNull List<String> itemsData, BarcodePickProductProviderCallback callback
    ) {
        executor.submit(() -> callback.onData(convertBarcodesToCallbackItems(itemsData)));
    }

    /**
     * Mark a product as picked. Artificially introduce a lag to simulate a network connection.
     *
     * @param itemData The barcode content of the item that was tapped in the BarcodePick UI.
     * @param callback The callback to call with the results.
     */
    public void pickItemAsync(
        @NonNull String itemData, @NonNull BarcodePickActionCallback callback
    ) {
        executor.submit(() -> {
            try {
                Thread.sleep(250 + waitRandomizer.nextInt(500));
                callback.onFinish(pickItem(itemData));
            } catch (InterruptedException e) {
                callback.onFinish(false);
            }
        });
    }

    /**
     * Unpick a product. Artificially introduce a lag to simulate a network connection.
     *
     * @param itemData The barcode content of the item that was tapped in the BarcodePick UI.
     * @param callback The callback to call with the results.
     */
    public void unpickItemAsync(
        @NonNull String itemData, @NonNull BarcodePickActionCallback callback
    ) {
        executor.submit(() -> {
            try {
                Thread.sleep(250 + waitRandomizer.nextInt(500));
                callback.onFinish(unpickItem(itemData));
            } catch (InterruptedException e) {
                callback.onFinish(false);
            }
        });
    }

    /**
     * For display purposes only, creates the content of the result UI in the form a items bound in
     * a {@link androidx.recyclerview.widget.RecyclerView} later.
     *
     * @return The list of what was picked and scanned with section titles.
     */
    @NonNull
    public List<Object> getPickResultContent() {

        List<Object> pickList = buildPickedProductsListAndSetPickedProductsCount();
        List<Object> inventoryList = getInventoryList();

        List<Object> returnList = new ArrayList<>();
        returnList.addAll(pickList);
        returnList.addAll(inventoryList);

        return returnList;
    }

    /**
     * Builds a pick list.
     *
     * @return list of all products that are picked.
     */
    @NonNull
    private List<Object> buildPickedProductsListAndSetPickedProductsCount() {
        List<DisplayProduct> displayItems = new ArrayList<>();
        List<Product> pickedProducts = new ArrayList<>();

        synchronized (this) {
            for (String item : allPickedCodes) {
                Product product = productDatabase.getProductForItemData(item);

                if (product != null) {
                    pickedProducts.add(product);
                    int numberOfProductsInList = Collections.frequency(pickedProducts, product);

                    // Only show with checkmark in the result list if number of same products in the
                    // displayed list is less or equal to quantity of products to pick.
                    boolean picked = numberOfProductsInList <= product.quantityToPick;
                    displayItems.add(product.getDisplayProduct(item, picked));
                } else {
                    // No product matching item was found
                    displayItems.add(new DisplayProduct("Unknown", 0, item, true));
                }
            }
        }

        Header header = new Header(
            true,
            productDatabase.getProductListCount());

        ArrayList<Object> returnList = new ArrayList<>();
        returnList.add(header);
        returnList.addAll(displayItems);

        return returnList;
    }

    /**
     * Builds an inventory list.
     *
     * @return list of scanned and not picked items.
     */
    @NonNull
    private List<Object> getInventoryList() {
        List<DisplayProduct> scannedItems = new ArrayList<>();

        Set<String> pickedWithoutScannedCodes = allScannedCodes;
        pickedWithoutScannedCodes.removeAll(allPickedCodes);

        Header header = new Header(false, pickedWithoutScannedCodes.size());

        for (String item : pickedWithoutScannedCodes) {
            Product product = productDatabase.getProductForItemData(item);
            DisplayProduct displayProduct;

            if (product == null) {
                displayProduct = new DisplayProduct("Unknown", 0, item, false);
            } else {
                displayProduct = product.getDisplayProduct(item, false);
            }

            scannedItems.add(displayProduct);
        }

        ArrayList<Object> returnList = new ArrayList<>();
        returnList.add(header);
        returnList.addAll(scannedItems);

        return returnList;
    }

    public synchronized void setAllScannedCodes(Set<String> scannedCodes) {
        allScannedCodes = scannedCodes;
    }

    public synchronized void setAllPickedCodes(Set<String> pickedCodes) {
        allPickedCodes = pickedCodes;
    }

    /**
     * Clear all picked and scanned codes so we can start the sample over again.
     */
    public synchronized void clearPickedAndScanned() {
        allPickedCodes.clear();
        allScannedCodes.clear();
    }

    private List<BarcodePickProductProviderCallbackItem> convertBarcodesToCallbackItems(
        @NonNull List<String> itemsData
    ) {
        List<BarcodePickProductProviderCallbackItem> callbackItems = new ArrayList<>();
        for (String itemData : itemsData) {
            Product product = productDatabase.getProductForItemData(itemData);
            if (product != null) {
                callbackItems.add(
                    new BarcodePickProductProviderCallbackItem(itemData, product.identifier));
            } else {
                callbackItems.add(new BarcodePickProductProviderCallbackItem(itemData, null));
            }
        }
        return callbackItems;
    }

    private synchronized boolean pickItem(@NonNull String itemData) {
        return productDatabase.verifyProductInDatabase(itemData);
    }

    private synchronized boolean unpickItem(@NonNull String itemData) {
        return productDatabase.verifyProductInDatabase(itemData);
    }
}

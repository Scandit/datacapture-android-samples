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

package com.scandit.datacapture.restockingsample.pick;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.pick.capture.BarcodePick;
import com.scandit.datacapture.barcode.pick.capture.BarcodePickSettings;
import com.scandit.datacapture.barcode.pick.data.BarcodePickAsyncMapperProductProvider;
import com.scandit.datacapture.barcode.pick.data.BarcodePickAsyncMapperProductProviderCallback;
import com.scandit.datacapture.barcode.pick.data.BarcodePickProduct;
import com.scandit.datacapture.barcode.pick.data.BarcodePickProductProvider;
import com.scandit.datacapture.barcode.pick.ui.BarcodePickView;
import com.scandit.datacapture.barcode.pick.ui.BarcodePickViewSettings;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.restockingsample.BuildConfig;
import com.scandit.datacapture.restockingsample.products.ProductManager;

import java.util.Set;

/**
 * Helper class to create our {@link BarcodePick} and {@link BarcodePickView}.
 */
public class BarcodePickManager {

    // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
    // `BuildConfig.SCANDIT_LICENSE_KEY`
    public static final String SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY;

    private static BarcodePickManager sharedInstance;

    @NonNull
    public static BarcodePickManager getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new BarcodePickManager();
        }
        return sharedInstance;
    }

    private final ProductManager productManager = ProductManager.getInstance();
    private final DataCaptureContext dataCaptureContext =
        DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

    private BarcodePickManager() {
    }

    public BarcodePickView createBarcodePickView(ViewGroup parent, BarcodePick barcodePick) {
        // We create the view settings.
        // We keep the default here, but you can use them to specify your own hints to display,
        // whether to show guidelines or not and so on.
        BarcodePickViewSettings viewSettings = new BarcodePickViewSettings();

        // We finally create the view, passing it a parent. The BarcodePickView will be
        // automatically added to its parent.
        return BarcodePickView.newInstance(parent, dataCaptureContext, barcodePick, viewSettings);
    }

    public BarcodePick createBarcodePick() {
        // We first create settings and enable the symbologies we want to scan.
        BarcodePickSettings settings = new BarcodePickSettings();
        settings.enableSymbology(Symbology.EAN13_UPCA, true);
        settings.enableSymbology(Symbology.EAN8, true);
        settings.enableSymbology(Symbology.CODE39, true);
        settings.enableSymbology(Symbology.CODE128, true);
        settings.enableSymbology(Symbology.UPCE, true);

        // We need the list of products that we want to pick.
        Set<BarcodePickProduct> products = productManager.getBarcodePickProducts();

        // We instantiate our product provider, responsible for matching barcodes and products.
        BarcodePickAsyncMapperProductProviderCallback callback =
            productManager::convertBarcodesToCallbackItemsAsync;
        BarcodePickProductProvider provider =
            new BarcodePickAsyncMapperProductProvider(products, callback);

        // And finally create BarcodePick
        return new BarcodePick(dataCaptureContext, settings, provider);
    }
}

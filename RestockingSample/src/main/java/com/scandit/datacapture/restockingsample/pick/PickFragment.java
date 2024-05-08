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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.scandit.datacapture.barcode.pick.capture.BarcodePick;
import com.scandit.datacapture.barcode.pick.capture.BarcodePickActionCallback;
import com.scandit.datacapture.barcode.pick.capture.BarcodePickActionListener;
import com.scandit.datacapture.barcode.pick.capture.BarcodePickScanningListener;
import com.scandit.datacapture.barcode.pick.capture.BarcodePickScanningSession;
import com.scandit.datacapture.barcode.pick.ui.BarcodePickView;
import com.scandit.datacapture.barcode.pick.ui.BarcodePickViewUiListener;
import com.scandit.datacapture.restockingsample.MainActivity;
import com.scandit.datacapture.restockingsample.R;
import com.scandit.datacapture.restockingsample.products.ProductManager;
import com.scandit.datacapture.restockingsample.result.ProductListFragment;

/**
 * Fragment that will display the picking UI.
 * The {@link BarcodePickView} will be displayed here.
 */
public class PickFragment extends Fragment {
    @NonNull
    public static PickFragment newInstance() {
        return new PickFragment();
    }

    public static String TAG = "PickFragment";

    private BarcodePickView barcodePickView;
    private BarcodePick barcodePick;
    private final ProductManager productManager = ProductManager.getInstance();
    private final BarcodePickManager barcodePickManager = BarcodePickManager.getInstance();

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_pick, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        barcodePick = barcodePickManager.createBarcodePick();
        barcodePickView = barcodePickManager.createBarcodePickView((ViewGroup) view, barcodePick);

        // Sets the listener that gets called when the user interacts with one of the
        // items on screen to pick or unpick them.
        barcodePickView.addActionListener(new BarcodePickActionListener() {
            @Override
            public void onPick(
                @NonNull String itemData, @NonNull BarcodePickActionCallback callback
            ) {
                // On pick, the callback will be notified if the pick action was successful
                // or not, by calling callback.fun onFinish(code, result).
                productManager.pickItemAsync(itemData, callback);
            }

            @Override
            public void onUnpick(
                @NonNull String itemData, @NonNull BarcodePickActionCallback callback
            ) {
                // On unpick, the callback will be notified if the unpick action was successful
                // or not, by calling callback.fun onFinish(code, result).
                productManager.unpickItemAsync(itemData, callback);
            }
        });

        // Sets the UI listener that gets called when the user interacts with one of the
        // UI elements, like the Finish button.
        barcodePickView.setUiListener(new BarcodePickViewUiListener() {
            @Override
            public void onFinishButtonTapped(BarcodePickView view) {
                goToItemList();
            }
        });

        // Sets the scanning listener for listening updates in scanning and picking.
        barcodePick.addScanningListener(new BarcodePickScanningListener() {
            @Override
            public void onScanningSessionUpdated(
                @NonNull BarcodePick barcodePick, @NonNull BarcodePickScanningSession session
            ) {
                productManager.setAllScannedCodes(session.getScannedItems());
                productManager.setAllPickedCodes(session.getPickedItems());
            }

            @Override
            public void onScanningSessionCompleted(
                @NonNull BarcodePick barcodePick, @NonNull BarcodePickScanningSession session
            ) {
                // not relevant in this sample
            }

            @Override
            public void onObservationStopped(@NonNull BarcodePick barcodePick) {
                // not relevant in this sample
            }

            @Override
            public void onObservationStarted(@NonNull BarcodePick barcodePick) {
                // not relevant in this sample
            }
        });

        // Start the scanning flow.
        // This will be automatically paused and restored when onResume and onPause are called.
        barcodePickView.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodePickView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodePickView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        barcodePickView.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            barcodePickView.stop();
        } else {
            barcodePickView.start();
            setupActionBar();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    void goToItemList() {
        FragmentManager manager = getParentFragmentManager();
        Fragment itemListFragment = ProductListFragment.newInstance();
        manager.beginTransaction()
            .add(R.id.container, itemListFragment, ProductListFragment.TAG)
            .hide(this)
            .addToBackStack(PickFragment.TAG)
            .commit();
    }
}

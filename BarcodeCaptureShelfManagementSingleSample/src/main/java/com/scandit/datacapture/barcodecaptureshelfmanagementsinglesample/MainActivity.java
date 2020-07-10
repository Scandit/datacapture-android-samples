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

package com.scandit.datacapture.barcodecaptureshelfmanagementsinglesample;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scandit.datacapture.barcodecaptureshelfmanagementsimplesample.R;

public class MainActivity extends CameraPermissionActivity implements
        MenuFragment.Callbacks,
        ScanTopBarcodeFragment.Callbacks,
        ScanBottomBarcodeFragment.Callbacks {
    private static final int ACTIVE_BUTTON_KEY_CODE = 1015;
    private static final String TAG_MENU_FRAGMENT = "MENU";
    private static final String TAG_SCAN_TOP_BARCODE_FRAGMENT = "SCAN_TOP_BARCODE";
    private static final String TAG_SCAN_BOTTOM_BARCODE_FRAGMENT = "SCAN_BOTTOM_BARCODE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {
            navigateToMenu();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        DataCaptureManager.getInstance().turnOnCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        DataCaptureManager.getInstance().turnOffCamera();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case ACTIVE_BUTTON_KEY_CODE:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                onNavigateToScanTopKeyDown();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void onNavigateToScanTopKeyDown() {
        Fragment menu = getSupportFragmentManager().findFragmentByTag(TAG_MENU_FRAGMENT);

        if (menu != null && menu.isResumed()) {
            navigateToScanTopBarcode();
        }
    }

    @Override
    public void onScanTopBarcodeClicked() {
        navigateToScanTopBarcode();
    }

    @Override
    public void onScanBottomBarcodeClicked() {
        navigateToScanBottomBarcode();
    }

    @Override
    public void onBarcodeScanned(String data, long timeMs) {
        navigateToMenu(data, timeMs);
    }

    private void navigateToMenu() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, MenuFragment.create(), TAG_MENU_FRAGMENT)
                .commitNow();
    }

    private void navigateToMenu(String barcodeData, long scanTimeMs) {
        getSupportFragmentManager().popBackStack();

        MenuFragment menuFragment = (MenuFragment)
                getSupportFragmentManager().findFragmentByTag(TAG_MENU_FRAGMENT);
        menuFragment.onLastScanInfoChanged(barcodeData, scanTimeMs);
    }

    private void navigateToScanTopBarcode() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_SCAN_TOP_BARCODE_FRAGMENT) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            ScanTopBarcodeFragment.create(),
                            TAG_SCAN_TOP_BARCODE_FRAGMENT
                    ).addToBackStack(TAG_SCAN_TOP_BARCODE_FRAGMENT)
                    .commit();
        }
    }

    private void navigateToScanBottomBarcode() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_SCAN_BOTTOM_BARCODE_FRAGMENT) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            ScanBottomBarcodeFragment.create(),
                            TAG_SCAN_BOTTOM_BARCODE_FRAGMENT
                    ).addToBackStack(TAG_SCAN_BOTTOM_BARCODE_FRAGMENT)
                    .commit();
        }
    }
}

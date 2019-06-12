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

package com.scandit.datacapture.barcodecaptureviewssample.modes.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcodecaptureviewssample.R;
import com.scandit.datacapture.barcodecaptureviewssample.modes.ScanViewModel;
import com.scandit.datacapture.core.ui.DataCaptureView;

public class FullscreenScanFragment extends Fragment implements ScanViewModel.ResultListener {

    private static final int PERMISSION_CODE_CAMERA = 100;

    public static FullscreenScanFragment newInstance() {
        return new FullscreenScanFragment();
    }

    private ScanViewModel viewModel;
    private boolean permissionDeniedOnce = false;
    private AlertDialog dialog = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ScanViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // To visualize the on-going barcode capturing process on screen,
        // setup a data capture view that renders the camera preview.
        // The view must be connected to the data capture context.
        return DataCaptureView.newInstance(requireContext(), viewModel.getDataCaptureContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasCameraPermissions()) {
            startScanning();
        } else if (!permissionDeniedOnce) {// We ask for permissions only once.
            requestCameraPermissions();
        }
    }

    private void startScanning() {
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        viewModel.setListener(this);
        viewModel.startFrameSource();
        if (!isShowingDialog()) {
            viewModel.resumeScanning();
        }
    }

    private boolean hasCameraPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissions() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        if (requestCode == PERMISSION_CODE_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanning();
        } else {
            permissionDeniedOnce = true;
        }
    }

    private boolean isShowingDialog() {
        return dialog != null && dialog.isShowing();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
        viewModel.setListener(null);
        viewModel.pauseScanning();
        viewModel.stopFrameSource();
    }

    @Override
    public void onCodeScanned(Barcode barcodeResult) {
        String message = getString(
                R.string.scan_result_parametrised,
                SymbologyDescription.create(barcodeResult.getSymbology()).getReadableName(),
                barcodeResult.getData(),
                barcodeResult.getSymbolCount()
        );

        dialog = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.scanned))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.resumeScanning();
                    }
                })
                .create();
        dialog.show();
    }
}

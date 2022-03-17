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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.barcodecaptureviewssample.R;
import com.scandit.datacapture.barcodecaptureviewssample.modes.ScanViewModel;
import com.scandit.datacapture.barcodecaptureviewssample.modes.base.CameraPermissionFragment;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;

public class FullscreenScanFragment extends CameraPermissionFragment implements ScanViewModel.ResultListener {

    static FullscreenScanFragment newInstance() {
        return new FullscreenScanFragment();
    }

    private ScanViewModel viewModel;
    private AlertDialog dialog = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ScanViewModel.class);
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
        DataCaptureView view = DataCaptureView.newInstance(requireContext(), viewModel.getDataCaptureContext());

        // Add a barcode capture overlay to the data capture view to render the tracked
        // barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        BarcodeCaptureOverlay overlay =
                BarcodeCaptureOverlay.newInstance(viewModel.getBarcodeCapture(), view);

        // Adjust the overlay's barcode highlighting to match the new viewfinder styles and improve
        // the visibility of feedback. With 6.10 we will introduce this visual treatment as a new
        // style for the overlay.
        Brush brush = new Brush(Color.TRANSPARENT, Color.WHITE, 3f);
        overlay.setBrush(brush);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        resumeFrameSource();
    }

    private void resumeFrameSource() {
        viewModel.setListener(this);
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        viewModel.startFrameSource();
        if (!isShowingDialog()) {
            viewModel.resumeScanning();
        }
    }

    private boolean isShowingDialog() {
        return dialog != null && dialog.isShowing();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.setListener(null);
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
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

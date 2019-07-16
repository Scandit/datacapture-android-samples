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

package com.scandit.datacapture.barcodecapturesettingssample.scanning;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.control.TorchSwitchControl;

public class BarcodeScanFragment extends CameraPermissionFragment
        implements BarcodeScanViewModel.Listener {

    public static BarcodeScanFragment newInstance() {
        return new BarcodeScanFragment();
    }

    private BarcodeScanViewModel viewModel;

    private DataCaptureView dataCaptureView;
    private AlertDialog dialog = null;

    private CountDownTimer continuousResultTimer = new CountDownTimer(500L, 500L) {
        @Override
        public void onTick(long millisUntilFinished) {}

        @Override
        public void onFinish() {
            dismissDialog();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(this).get(BarcodeScanViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        dataCaptureView = DataCaptureView.newInstance(requireContext(), null);
        return dataCaptureView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SettingsManager settings = viewModel.getCurrentSettings();
        setupDataCaptureView(settings);
        setupBarcodeCaptureOverlay(settings);
    }

    private void setupDataCaptureView(SettingsManager settings) {
        dataCaptureView.setDataCaptureContext(settings.getDataCaptureContext());
        dataCaptureView.setScanAreaMargins(settings.getScanAreaMargins());
        dataCaptureView.setPointOfInterest(settings.getPointOfInterest());
        dataCaptureView.setLogoAnchor(settings.getLogoAnchor());
        dataCaptureView.setLogoOffset(
                new PointWithUnit(settings.getAnchorXOffset(), settings.getAnchorYOffset())
        );
        if (settings.isTorchButtonEnabled()) {
            dataCaptureView.addControl(new TorchSwitchControl(requireContext()));
        }
    }

    private void setupBarcodeCaptureOverlay(SettingsManager settings) {
        dataCaptureView.addOverlay(settings.getBarcodeCaptureOverlay());
    }

    @Override
    protected boolean shouldShowBackButton() {
        return false;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.app_title);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        viewModel.setListener(this);

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        resumeFrameSource();
    }

    private void resumeFrameSource() {
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        viewModel.startFrameSource();
        viewModel.resumeScanning();
    }

    @Override
    public void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    private void pauseFrameSource() {
        viewModel.setListener(null);
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
        viewModel.pauseScanning();
        viewModel.stopFrameSource();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
    }

    @Override
    public void showDialog(String symbologyName, String data, int symbolCount) {
        String text = getString(R.string.result_parametrised, symbologyName, data, symbolCount);
        if (viewModel.isContinuousScanningEnabled()) {
            showDialogForContinuousScanning(text);
        } else {
            showDialogForOneShotScanning(text);
        }
    }

    private void showDialogForContinuousScanning(String text) {
        continuousResultTimer.cancel();
        continuousResultTimer.start();
        if (showingDialog()) {
            dialog.setMessage(text);
        } else {
            dialog = buildAutoDismissDialog(text);
            dialog.show();
        }
    }

    private void showDialogForOneShotScanning(String text) {
        dialog = buildPermanentDialog(text);
        dialog.show();
    }

    private AlertDialog buildPermanentDialog(String text) {
        return buildBaseDialog(text)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewModel.resumeScanning();
                            }
                        })
                .create();
    }

    private AlertDialog buildAutoDismissDialog(String text) {
        return buildBaseDialog(text).create();
    }

    private AlertDialog.Builder buildBaseDialog(String text) {
        return new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setTitle(getString(R.string.result_title))
                .setMessage(text);
    }

    private boolean showingDialog() {
        return dialog != null && dialog.isShowing();
    }

    private void dismissDialog() {
        if (showingDialog()) {
            dialog.dismiss();
        }
    }
}

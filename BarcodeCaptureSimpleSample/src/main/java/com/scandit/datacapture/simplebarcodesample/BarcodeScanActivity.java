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

package com.scandit.datacapture.simplebarcodesample;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import com.scandit.datacapture.barcode.capture.*;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;

import java.util.Arrays;
import java.util.HashSet;

import static android.content.DialogInterface.OnClickListener;

public class BarcodeScanActivity
        extends CameraPermissionActivity implements BarcodeCaptureListener {

    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    public static final String scanditLicenseKey = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private DataCaptureContext dataCaptureContext;
    private BarcodeCapture barcodeCapture;
    private Camera camera;
    private DataCaptureView dataCaptureView;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize and start the barcode recognition.
        initializeAndStartBarcodeScanning();
    }

    private void initializeAndStartBarcodeScanning() {
        dataCaptureContext = DataCaptureContext.forLicenseKey(scanditLicenseKey);

        // Device's camera will serve as a frame source.
        camera = Camera.getDefaultCamera();
        if (camera != null) {
            // Use the settings recommended by barcode capture.
            camera.applySettings(BarcodeCapture.createRecommendedCameraSettings(), null);
            dataCaptureContext.setFrameSource(camera, null);
        }

        // The scanning behavior is configured through barcode capture settings. We start with empty
        // barcode capture settings and enable a very generous set of symbologies. In your own apps,
        // only enable the symbologies you actually need.
        BarcodeCaptureSettings barcodeCaptureSettings = new BarcodeCaptureSettings();

        HashSet<Symbology> symbologies = new HashSet<>();

        symbologies.add(Symbology.EAN13_UPCA);
        symbologies.add(Symbology.EAN8);
        symbologies.add(Symbology.UPCE);
        symbologies.add(Symbology.QR);
        symbologies.add(Symbology.DATA_MATRIX);
        symbologies.add(Symbology.CODE39);
        symbologies.add(Symbology.CODE128);
        symbologies.add(Symbology.INTERLEAVED_TWO_OF_FIVE);

        barcodeCaptureSettings.enableSymbologies(symbologies);

        // Some 1d barcode symbologies allow you to encode variable-length data. By default, the
        // Scandit Barcode Capture only scans barcodes in a certain length range. If your
        // application requires scanning of one of these symbologies, and the length is falling
        // outside the default range, you may need to adjust the "active symbol counts" for this
        // symbology. This is shown in the following few lines of code.
        SymbologySettings symbologySettings =
                barcodeCaptureSettings.getSymbologySettings(Symbology.CODE39);

        HashSet<Short> activeSymbolCounts = new HashSet<>(
                Arrays.asList(new Short[] { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 }));

        symbologySettings.setActiveSymbolCounts(activeSymbolCounts);

        barcodeCapture = BarcodeCapture.forContext(dataCaptureContext, barcodeCaptureSettings);
        barcodeCapture.addListener(this);
        barcodeCapture.setEnabled(true);

        BarcodeCaptureOverlay overlay = new BarcodeCaptureOverlay(barcodeCapture);
        overlay.setViewfinder(new RectangularViewfinder());

        dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);
        dataCaptureView.addOverlay(overlay);

        setContentView(dataCaptureView);
    }

    @Override
    protected void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dataCaptureContext.removeMode(barcodeCapture);
        super.onDestroy();
    }

    private void pauseFrameSource() {
        camera.switchToDesiredState(FrameSourceState.OFF, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Handle permissions for Marshmallow and onwards...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestCameraPermission();
        } else {
            // Once the activity is in the foreground again, restart scanning.
            resumeFrameSource();
        }
    }

    @Override
    public void onCameraPermissionGranted() {
        resumeFrameSource();
    }

    private void resumeFrameSource() {
        dismissScannedCodesDialog();

        camera.switchToDesiredState(FrameSourceState.ON, null);
    }

    private void dismissScannedCodesDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void displayScannedCodes(BarcodeCaptureSession session) {
        Barcode barcode = session.getNewlyRecognizedBarcodes().get(0);
        String symbology = SymbologyDescription.create(barcode.getSymbology()).getReadableName();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.setCancelable(false)
                         .setTitle("Scanned: " + barcode.getData() + " (" + symbology + ")")
                         .setPositiveButton(android.R.string.ok,
                                 new OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         barcodeCapture.setEnabled(true);
                                     }
                                 })
                         .create();
        dialog.show();
    }

    @Override
    public void onBarcodeScanned(@NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session, @NonNull FrameData frameData) {
        displayScannedCodes(session);

        barcodeCapture.setEnabled(false);

        // If you are not disabling barcode capture here and want to continue scanning, consider
        // setting the codeDuplicateFilter when creating the barcode capture settings to around 500
        // or even -1 if you do not want codes be scanned more than once.
    }

    @Override
    public void onSessionUpdated(@NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session, @NonNull FrameData data) {}

    @Override
    public void onObservationStarted(@NonNull BarcodeCapture barcodeCapture) {}

    @Override
    public void onObservationStopped(@NonNull BarcodeCapture barcodeCapture) {}
}

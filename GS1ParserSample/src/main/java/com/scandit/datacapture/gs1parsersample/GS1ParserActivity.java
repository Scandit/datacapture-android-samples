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

package com.scandit.datacapture.gs1parsersample;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlayStyle;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderLineStyle;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle;
import com.scandit.datacapture.parser.ParsedData;
import com.scandit.datacapture.parser.ParsedField;
import com.scandit.datacapture.parser.Parser;
import com.scandit.datacapture.parser.ParserDataFormat;

import java.util.HashSet;

import static android.content.DialogInterface.OnClickListener;

public class GS1ParserActivity
        extends CameraPermissionActivity implements BarcodeCaptureListener {

    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    public static final String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private DataCaptureContext dataCaptureContext;
    private BarcodeCapture barcodeCapture;
    private Camera camera;
    private DataCaptureView dataCaptureView;
    private Parser parser;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize and start barcode capture and the GS1 parser.
        initializeBarcodeCapture();
        initializeParser();
    }

    private void initializeBarcodeCapture() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);
        // Use the default camera with the recommended camera settings for the BarcodeCapture mode
        // and set it as the frame source of the context. The camera is off by default and must be
        // turned on to start streaming frames to the data capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
        camera = Camera.getDefaultCamera(BarcodeCapture.createRecommendedCameraSettings());
        if (camera != null) {
            dataCaptureContext.setFrameSource(camera);
        } else {
            throw new IllegalStateException(
                    "Sample depends on a camera, which failed to initialize.");
        }

        // The barcode capturing process is configured through barcode capture settings
        // which are then applied to the barcode capture instance that manages barcode recognition.
        BarcodeCaptureSettings barcodeCaptureSettings = new BarcodeCaptureSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable DataMatrix and Code128 symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires as
        // every additional enabled symbology has an impact on processing times.
        HashSet<Symbology> symbologies = new HashSet<>();
        symbologies.add(Symbology.DATA_MATRIX);
        symbologies.add(Symbology.CODE128);

        barcodeCaptureSettings.enableSymbologies(symbologies);

        // Create new barcode capture mode with the settings from above.
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, barcodeCaptureSettings);

        // Register self as a listener to get informed whenever a new barcode got recognized.
        barcodeCapture.addListener(this);

        // To visualize the on-going barcode capturing process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);

        // Add a barcode capture overlay to the data capture view to render the location of captured
        // barcodes on top of the video preview. Viewfinders are visual components only, and as
        // such will not restrict the scan area.
        // This is optional, but recommended for better visual feedback.
        BarcodeCaptureOverlay overlay = BarcodeCaptureOverlay.newInstance(
                barcodeCapture,
                dataCaptureView,
                BarcodeCaptureOverlayStyle.FRAME
        );
        RectangularViewfinder viewfinder = new RectangularViewfinder(
                RectangularViewfinderStyle.SQUARE,
                RectangularViewfinderLineStyle.BOLD
        );
        overlay.setViewfinder(viewfinder);

        setContentView(dataCaptureView);
    }

    private void initializeParser() {
        // Let's create the Parser object that will parse the GS1 strings.
        parser = Parser.forFormat(dataCaptureContext, ParserDataFormat.GS1_AI);
    }

    @Override
    protected void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        barcodeCapture.removeListener(this);
        dataCaptureContext.removeMode(barcodeCapture);
        super.onDestroy();
    }

    private void pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode capture as well.
        barcodeCapture.setEnabled(false);
        camera.switchToDesiredState(FrameSourceState.OFF, null);
    }

    @Override
    protected void onResume() {
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
        dismissParserResultDialog();

        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        barcodeCapture.setEnabled(true);
        camera.switchToDesiredState(FrameSourceState.ON, null);
    }

    private void dismissParserResultDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onBarcodeScanned(
            @NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session,
            @NonNull FrameData frameData
    ) {
        if (session.getNewlyRecognizedBarcodes().isEmpty()) return;

        String barcodeData = session.getNewlyRecognizedBarcodes().get(0).getData();
        try {
            final ParsedData parsedData = parser.parseString(barcodeData);
            final String result = new ParsedDataPrettyPrinter(parsedData).getResult();

            // Stop recognizing barcodes for as long as we are displaying the result. There won't be any
            // new results until the capture mode is enabled again. Note that disabling the capture mode
            // does not stop the camera, the camera continues to stream frames until it is turned off.
            barcodeCapture.setEnabled(false);

            // This callback may be executed on an arbitrary thread. We need to switch back
            // to the main thread.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showResult(result);
                }
            });
        } catch (RuntimeException e) {
            // Print the parser failure and continue.
            e.printStackTrace();
            barcodeCapture.setEnabled(true);
        }
    }

    private void showResult(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.setCancelable(false)
                .setTitle("Parser result")
                .setMessage(result)
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
    public void onSessionUpdated(
            @NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session, @NonNull FrameData data
    ) {
        // In this sample we are not interested in this callback.
    }

    @Override
    public void onObservationStarted(@NonNull BarcodeCapture barcodeCapture) {
        // In this sample we are not interested in this callback.
    }

    @Override
    public void onObservationStopped(@NonNull BarcodeCapture barcodeCapture) {
        // In this sample we are not interested in this callback.
    }
}

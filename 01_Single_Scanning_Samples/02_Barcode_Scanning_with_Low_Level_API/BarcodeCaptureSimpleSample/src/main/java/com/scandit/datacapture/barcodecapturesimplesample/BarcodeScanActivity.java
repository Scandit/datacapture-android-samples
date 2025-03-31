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

package com.scandit.datacapture.barcodecapturesimplesample;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.scandit.datacapture.barcode.capture.*;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlayStyle;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.feedback.Feedback;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle;

import java.util.Arrays;
import java.util.HashSet;

import static android.content.DialogInterface.OnClickListener;

public class BarcodeScanActivity
        extends CameraPermissionActivity implements BarcodeCaptureListener {

    // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
    // `BuildConfig.SCANDIT_LICENSE_KEY`
    public static final String SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY;

    private DataCaptureContext dataCaptureContext;
    private BarcodeCapture barcodeCapture;
    private Camera camera;
    private DataCaptureView dataCaptureView;
    private BarcodeCaptureOverlay overlay;
    private Feedback feedback = Feedback.defaultFeedback(); // Used when emitting feedback manually.

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize and start the barcode recognition.
        initializeAndStartBarcodeScanning();
    }

    private void initializeAndStartBarcodeScanning() {
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
            throw new IllegalStateException("Sample depends on a camera, which failed to initialize.");
        }

        // The barcode capturing process is configured through barcode capture settings
        // which are then applied to the barcode capture instance that manages barcode recognition.
        BarcodeCaptureSettings barcodeCaptureSettings = new BarcodeCaptureSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires as
        // every additional enabled symbology has an impact on processing times.
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

        // Some linear/1d barcode symbologies allow you to encode variable-length data.
        // By default, the Scandit Data Capture SDK only scans barcodes in a certain length range.
        // If your application requires scanning of one of these symbologies, and the length is
        // falling outside the default range, you may need to adjust the "active symbol counts"
        // for this symbology. This is shown in the following few lines of code for one of the
        // variable-length symbologies.
        SymbologySettings symbologySettings =
                barcodeCaptureSettings.getSymbologySettings(Symbology.CODE39);

        HashSet<Short> activeSymbolCounts = new HashSet<>(
                Arrays.asList(new Short[] { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 }));

        symbologySettings.setActiveSymbolCounts(activeSymbolCounts);

        // Create new barcode capture mode with the settings from above.
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, barcodeCaptureSettings);

        // By default, every time a barcode is scanned, a sound (if not in silent mode) and a
        // vibration are played.
        // Uncomment the following line to set a success feedback without sound and vibration.
//        barcodeCapture.getFeedback().setSuccess(new Feedback());
        // Uncomment this line to set a feedback that can be triggered programmatically.
//        feedback = Feedback.defaultFeedback();

        // Register self as a listener to get informed whenever a new barcode got recognized.
        barcodeCapture.addListener(this);

        // To visualize the on-going barcode capturing process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);

        // Add a barcode capture overlay to the data capture view to render the location of captured
        // barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        overlay = BarcodeCaptureOverlay.newInstance(
                barcodeCapture,
                dataCaptureView,
                BarcodeCaptureOverlayStyle.FRAME
        );

        // Add a square viewfinder as we are only scanning square QR codes.
        overlay.setViewfinder(new RectangularViewfinder(RectangularViewfinderStyle.SQUARE));

        // Adjust the overlay's barcode highlighting to match the new viewfinder styles and improve
        // the visibility of feedback. With 6.10 we will introduce this visual treatment as a new
        // style for the overlay.
        Brush brush = new Brush(Color.TRANSPARENT, Color.WHITE, 3f);
        overlay.setBrush(brush);

        setContentView(dataCaptureView);
    }

    @Override
    protected void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        barcodeCapture.removeListener(this);
        dataCaptureContext.removeCurrentMode();
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
        dismissScannedCodesDialog();

        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        barcodeCapture.setEnabled(true);
        camera.switchToDesiredState(FrameSourceState.ON, null);
    }

    private void dismissScannedCodesDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void showResult(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.setCancelable(false)
                .setTitle(result)
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
    public void onBarcodeScanned(
            @NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session,
            @NonNull FrameData frameData
    ) {
        if (session.getNewlyRecognizedBarcode() == null) return;

        Barcode barcode = session.getNewlyRecognizedBarcode();

        // Use the following code to reject barcodes.
        // By uncommenting the following lines, barcodes not starting with 09: are ignored.
//        if (barcode.getData() == null || !barcode.getData().startsWith("09:")) {
//            // We temporarily change the brush, used to highlight recognized barcodes, to a
//            // transparent brush.
//            overlay.setBrush(Brush.transparent());
//            return;
//        }

        // Otherwise, if the barcode is of interest, we want to use a brush to highlight it.
//        Brush brush = new Brush(Color.TRANSPARENT, Color.WHITE, 3f);
//        overlay.setBrush(brush);

        // We also want to emit a feedback (vibration and, if enabled, sound).
        // By default, every time a barcode is scanned, a sound (if not in silent mode) and a vibration are played.
        // To emit a feedback only when necessary, it is necessary to set a success feedback without sound and vibration
        // when setting up Barcode Capture (in this case in the `setupRecognition` method).
//        feedback.emit();

        // Stop recognizing barcodes for as long as we are displaying the result. There won't be any
        // new results until the capture mode is enabled again. Note that disabling the capture mode
        // does not stop the camera, the camera continues to stream frames until it is turned off.
        barcodeCapture.setEnabled(false);

        // If you don't want the codes to be scanned more than once, consider setting the
        // codeDuplicateFilter when creating the barcode capture settings to -1.
        // You can set any other value (e.g. 500) to set a fixed timeout and override the smart
        // behaviour enabled by default.

        // Get the human readable name of the symbology and assemble the result to be shown.
        String symbology = SymbologyDescription.create(barcode.getSymbology()).getReadableName();
        final String result = "Scanned: " + barcode.getData() + " (" + symbology + ")";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showResult(result);
            }
        });
    }

    @Override
    public void onSessionUpdated(@NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session, @NonNull FrameData data) {}

    @Override
    public void onObservationStarted(@NonNull BarcodeCapture barcodeCapture) {}

    @Override
    public void onObservationStopped(@NonNull BarcodeCapture barcodeCapture) {}
}

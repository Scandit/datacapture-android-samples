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

package com.scandit.datacapture.mrzscanneresample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureException;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSession;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.IdDocumentType;
import com.scandit.datacapture.id.data.MrzResult;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static android.content.DialogInterface.OnClickListener;

public class MRZScannerActivity
        extends CameraPermissionActivity implements IdCaptureListener {

    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    public static final String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();

    private DataCaptureContext dataCaptureContext;
    private IdCapture idCapture;
    private Camera camera;
    private DataCaptureView dataCaptureView;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrz_scanner);

        // Initialize id capture.
        initializeIdCapture();
    }

    private void initializeIdCapture() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        // Use the default camera with the recommended camera settings for the IdCapture mode and
        // set it as the frame source of the context. The camera is off by default and must be
        // turned on to start streaming frames to the data capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
        camera = Camera.getDefaultCamera(IdCapture.createRecommendedCameraSettings());
        if (camera != null) {
            dataCaptureContext.setFrameSource(camera);
        } else {
            throw new IllegalStateException("Sample depends on a camera, which failed to initialize.");
        }

        // The id capturing process is configured through id capture settings
        // which are then applied to the id capture instance that manages id recognition.
        IdCaptureSettings idCaptureSettings = new IdCaptureSettings();
        idCaptureSettings.setSupportedDocuments(IdDocumentType.ID_CARD_MRZ, IdDocumentType.PASSPORT_MRZ);

        // Create new id capture mode with the settings from above.
        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, idCaptureSettings);

        // Register self as a listener to get informed whenever a new id got recognized.
        idCapture.addListener(this);

        // To visualize the on-going id capturing process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);

        // Add an IdCaptureOverlay to highlight the scanning area.
        IdCaptureOverlay.newInstance(idCapture, dataCaptureView);

        // Add the DataCaptureView to the container.
        FrameLayout container = findViewById(R.id.data_capture_view_container);
        container.addView(dataCaptureView);
    }

    @Override
    protected void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        idCapture.removeListener(this);
        dataCaptureContext.removeMode(idCapture);
        super.onDestroy();
    }

    private void pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable id capture as well.
        idCapture.setEnabled(false);
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
        idCapture.setEnabled(true);
        camera.switchToDesiredState(FrameSourceState.ON, null);
    }

    private void dismissScannedCodesDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void showResult(@StringRes int titleRes, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.setCancelable(false)
                .setTitle(titleRes)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                idCapture.setEnabled(true);
                            }
                        })
                .create();
        dialog.show();
    }

    @Override
    public void onIdCaptured(
            @NotNull IdCapture mode, @NotNull IdCaptureSession session, @NotNull FrameData data
    ) {
        // Pause the running IdCapture while processing the captured id.
        idCapture.setEnabled(false);

        final CapturedId capturedId = session.getNewlyCapturedId();

        /*
         * This callback may be executed on an arbitrary thread. We post to switch back
         * to the main thread.
         */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "";

                // The recognized fields of the captured Id can vary based on the capturedResultType.
                switch (capturedId.getCapturedResultType()) {
                    case MRZ_RESULT:
                        message = getDescriptionForMrzResult(capturedId);
                        break;
                    // We don't expect any other types with the selected supported documents.
                }
                showResult(R.string.title_alert_result, message);
            }
        });
    }

    private String getDescriptionForMrzResult(CapturedId result) {
        StringBuilder builder = new StringBuilder();

        appendDescriptionForCapturedId(result, builder);

        MrzResult mrz = result.getMrz();

        appendField(builder, "Document Code: ", mrz.getDocumentCode());
        appendField(builder, "Names Are Truncated: ", mrz.getNamesAreTruncated());
        appendField(builder, "Optional: ", mrz.getOptional());
        appendField(builder, "Optional1: ", mrz.getOptional1());

        return builder.toString();
    }

    private void appendDescriptionForCapturedId(CapturedId result, StringBuilder builder) {
        appendField(builder, "Result Type: ", result.getCapturedResultType().toString());
        appendField(builder, "Document Type: ", result.getDocumentType().toString());
        appendField(builder, "First Name: ", result.getFirstName());
        appendField(builder, "Last Name: ", result.getLastName());
        appendField(builder, "Full Name: ", result.getFullName());
        appendField(builder, "Sex: ", result.getSex());
        appendField(builder, "Date of Birth: ", result.getDateOfBirth());
        appendField(builder, "Nationality: ", result.getNationality());
        appendField(builder, "Address: ", result.getAddress());
        appendField(builder, "Issuing Country ISO: ", result.getIssuingCountryIso());
        appendField(builder, "Issuing Country: ", result.getIssuingCountry());
        appendField(builder, "Document Number: ", result.getDocumentNumber());
        appendField(builder, "Date of Expiry: ", result.getDateOfExpiry());
        appendField(builder, "Date of Issue: ", result.getDateOfIssue());
    }

    @Override
    public void onIdLocalized(
            @NotNull IdCapture mode,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        // In this sample we are not interested in this callback.
    }

    @Override
    public void onIdRejected(
            @NotNull IdCapture mode,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        // In this sample we are not interested in this callback.
    }

    @Override
    public void onErrorEncountered(
            @NotNull IdCapture mode,
            @NotNull final Throwable error,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        Log.i(getClass().getSimpleName(), getErrorMessage(error));
    }

    private String getErrorMessage(Throwable error) {
        if (error instanceof IdCaptureException) {
            IdCaptureException idCaptureException = (IdCaptureException) error;

            StringBuilder messageBuilder =
                    new StringBuilder(idCaptureException.getKind().toString());

            if (!TextUtils.isEmpty(idCaptureException.getMessage())) {
                messageBuilder.append(": ")
                        .append(idCaptureException.getMessage());
            }

            return messageBuilder.toString();
        } else {
            return error.getMessage();
        }
    }

    @Override
    public void onObservationStarted(@NotNull IdCapture mode) {
        // In this sample we are not interested in this callback.
    }

    @Override
    public void onObservationStopped(@NotNull IdCapture mode) {
        // In this sample we are not interested in this callback.
    }

    private void appendField(StringBuilder builder, String name, boolean value) {
        builder.append(name)
                .append(value)
                .append("\n");
    }

    private void appendField(StringBuilder builder, String name, String value) {
        builder.append(name);

        if (TextUtils.isEmpty(value)) {
            builder.append("<empty>");
        } else {
            builder.append(value);
        }

        builder.append("\n");
    }

    private void appendField(StringBuilder builder, String name, DateResult value) {
        builder.append(name);

        if (value == null) {
            builder.append("<empty>");
        } else {
            builder.append(dateFormat.format(value.toDate()));
        }

        builder.append("\n");
    }
}

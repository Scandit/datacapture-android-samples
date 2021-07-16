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

package com.scandit.datacapture.idcaptureextendedsample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureException;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSession;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.SupportedSides;
import com.scandit.datacapture.id.data.VizResult;
import com.scandit.datacapture.id.ui.IdLayoutStyle;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.idcaptureextendedsample.mappers.AamvaResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.ArgentinaIdResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.ColombiaIdResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.MrzResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.SouthAfricaDlResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.SouthAfricaIdResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.UsUniformedServicesResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.mappers.VizResultMapper;
import com.scandit.datacapture.idcaptureextendedsample.result.ResultActivity;
import com.scandit.datacapture.idcaptureextendedsample.result.ResultEntry;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class IdCaptureActivity extends CameraPermissionActivity
    implements IdCaptureListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private DataCaptureManager dataCaptureManager;

    private DataCaptureView view;
    private IdCaptureOverlay overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewGroup container = findViewById(R.id.data_capture_view_container);

        dataCaptureManager = DataCaptureManager.getInstance();

        /*
         * Setup the BottomNavigation which will change the enabled document types on selection.
         */
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_bar);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        /*
         * Create a new DataCaptureView and fill the screen with it. DataCaptureView will show
         * the camera preview on the screen. Pass your DataCaptureContext to the view's
         * constructor.
         */
        view = DataCaptureView.newInstance(this, dataCaptureManager.getDataCaptureContext());
        container.addView(
            view,
            new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        );

        /*
         * We setup IdCapture only on first run, as we're using a singleton to handle
         * Scandit Components.
         */
        if (savedInstanceState == null) {
            setupIdCapture(DataCaptureManager.Mode.BARCODE, false);
        }
        setupIdCaptureOverlay(); // We always restore the IdCaptureOverlay.
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * Check for camera permission and request it, if it hasn't yet been granted.
         * Once we have the permission the onCameraPermissionGranted() method will be called.
         */
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        /*
         * Start listening on IdCapture events.
         */
        dataCaptureManager.getIdCapture().addListener(this);

        /*
         * Switch the camera on. The camera frames will be sent to TextCapture for processing.
         * Additionally the preview will appear on the screen. The camera is started asynchronously,
         * and you may notice a small delay before the preview appears.
         */
        dataCaptureManager.getCamera().switchToDesiredState(FrameSourceState.ON);
    }

    @Override
    public boolean onNavigationItemSelected(
        @NonNull MenuItem menuItem
    ) {
        switch (menuItem.getItemId()) {
            case R.id.barcode:
                setupIdCapture(DataCaptureManager.Mode.BARCODE, true);
                return true;
            case R.id.mrz:
                setupIdCapture(DataCaptureManager.Mode.MRZ, true);
                return true;
            case R.id.viz:
                setupIdCapture(DataCaptureManager.Mode.VIZ, true);
                return true;
        }
        return false;
    }

    /*
     * To update the supported IdCapture documents, make sure to:
     * - Remove listeners from the previous IdCapture mode.
     * - Remove the IdCapture mode from the DataCaptureContext.
     * - Remove the previous overlay from the DataCaptureView.
     * - Create a new IdCapture and apply the new IdCaptureSettings. Make sure the new IdCapture is
     *      bound to the DataCaptureContext
     *      ( e.g. the IdCapture's constructor or DataCaptureView.addMode(IdCapture).
     * - Add a listener to the new IdCapture mode.
     * - Create a new IdCaptureOverlay bound to the new IdCapture mode.
     */
    private void setupIdCapture(DataCaptureManager.Mode mode, boolean setupOverlay) {
        dataCaptureManager.unsetIdCapture(this);
        if (setupOverlay) unsetIdCaptureOverlay();
        dataCaptureManager.setUpIdCapture(mode, this);
        if (setupOverlay) setupIdCaptureOverlay();
    }

    private void unsetIdCaptureOverlay() {
        // Remove the overlay from the DataCaptureView.
        if (overlay != null) {
            view.removeOverlay(overlay);
        }
    }

    private void setupIdCaptureOverlay() {
        /*
         * Add an IdCapture overlay to the DataCaptureView to show a visual guide of the expected
         * type of document.
         */
        overlay = IdCaptureOverlay.newInstance(dataCaptureManager.getIdCapture(), view);
        overlay.setIdLayoutStyle(IdLayoutStyle.ROUNDED);
    }

    @Override
    public void onPause() {
        super.onPause();

        /*
         * Switch the camera off to stop streaming frames. The camera is stopped asynchronously.
         */
        dataCaptureManager.getCamera().switchToDesiredState(FrameSourceState.OFF);
        dataCaptureManager.getIdCapture().removeListener(this);
    }

    @Override
    public void onIdCaptured(
        @NotNull final IdCapture mode,
        @NotNull IdCaptureSession session,
        @NotNull FrameData data
    ) {
        final CapturedId capturedId = session.getNewlyCapturedId();

        // Don't capture unnecessarily when we are displaying the result.
        mode.setEnabled(false);

        /*
         * This callback may be executed on an arbitrary thread. We post to switch back
         * to the main thread.
         */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ResultEntry> result = new ArrayList<>();
                Bitmap image = null;

                // The recognized fields of the captured Id can vary based on the
                // capturedResultType.
                switch (capturedId.getCapturedResultType()) {
                    case AAMVA_BARCODE_RESULT:
                        result = new AamvaResultMapper(capturedId).mapResult();
                        break;
                    case COLOMBIA_ID_BARCODE_RESULT:
                        result = new ColombiaIdResultMapper(capturedId).mapResult();
                        break;
                    case ARGENTINA_ID_BARCODE_RESULT:
                        result = new ArgentinaIdResultMapper(capturedId).mapResult();
                        break;
                    case SOUTH_AFRICA_ID_BARCODE_RESULT:
                        result = new SouthAfricaIdResultMapper(capturedId).mapResult();
                        break;
                    case SOUTH_AFRICA_DL_BARCODE_RESULT:
                        result = new SouthAfricaDlResultMapper(capturedId).mapResult();
                        break;
                    case US_UNIFORMED_SERVICES_BARCODE_RESULT:
                        image = capturedId.getImageBitmapForType(IdImageType.FACE);
                        result = new UsUniformedServicesResultMapper(capturedId).mapResult();
                        break;
                    case MRZ_RESULT:
                        result = new MrzResultMapper(capturedId).mapResult();
                        break;
                    case VIZ_RESULT: {
                        VizResult vizResult = capturedId.getViz();
                        /*
                         * Viz result may support scanning the back of the document, you can check
                         * if this is available for the current document from the
                         * flag `vizResult.isBackSideCaptureSupported()`. This can be used in
                         * combination with `vizResult.getCapturedSides()` to check that the back of
                         * the document hasn't already be scanned.
                         *
                         * Additionally, VizResult allows for an image to be retrieved from the
                         * currently scanned document.
                         * You can retrieve it from capturedId.getImageBitmapForType(<IdImageType>).
                         */
                        image = capturedId.getImageBitmapForType(IdImageType.FACE);
                        result = new VizResultMapper(capturedId).mapResult();
                        if (vizResult.isBackSideCaptureSupported() &&
                            vizResult.getCapturedSides() == SupportedSides.FRONT_ONLY) {
                            // We need to scan the back or the document as well.
                            showScanBackDialog(result, image);
                            return;
                        }
                        break;
                    }
                }
                launchResultActivity(result, image);
            }
        });
    }

    private void launchResultActivity(ArrayList<ResultEntry> result, @Nullable Bitmap image) {
        startActivityForResult(ResultActivity.getIntent(this, result, image), REQUEST_CODE);
    }

    private final static int REQUEST_CODE = 100;

    @Override
    protected void onActivityResult(
        int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data
    ) {
        if (requestCode == REQUEST_CODE) {
            dataCaptureManager.getIdCapture().setEnabled(true);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showScanBackDialog(
        final ArrayList<ResultEntry> currentResult,
        final Bitmap image
    ) {
        new AlertDialog.Builder(this)
            .setTitle(R.string.alert_scan_back_title)
            .setMessage(R.string.alert_scan_back_message)
            .setPositiveButton(R.string.scan, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*
                     * If we continue with scanning the back of the document, the IdCapture settings
                     * will automatically allow only for this to be scanned, blocking you from
                     * scanning other front of IDs. The next `onIdCaptured` will contain data from
                     * both the front and the back scans.
                     */
                    dataCaptureManager.getIdCapture().setEnabled(true);
                    dialog.dismiss();
                }
            })
            .setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    /*
                     * If we want to skip scanning the back of the document, we have to call
                     * `IdCapture().reset()` to allow for another front IDs to be scanned.
                     */
                    launchResultActivity(currentResult, image);
                    dataCaptureManager.getIdCapture().reset();
                }
            })
            .setCancelable(false)
            .show();
    }

    @Override
    public void onErrorEncountered(
        @NotNull IdCapture mode,
        @NotNull final Throwable error,
        @NotNull IdCaptureSession session,
        @NotNull FrameData data
    ) {
        mode.setEnabled(false);
        /*
         * This callback may be executed on an arbitrary thread. We post to switch back
         * to the main thread.
         */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showError(getErrorMessage(error));
            }
        });
    }

    private String getErrorMessage(Throwable error) {
        if (error instanceof IdCaptureException) {
            IdCaptureException idCaptureException = (IdCaptureException) error;

            // We do not report parsing errors, they're probably caused by an inaccurate read.
            if (idCaptureException.getKind() != IdCaptureException.Kind.PARSING_ERROR) {
                String message = idCaptureException.getKind().toString();

                if (!TextUtils.isEmpty(idCaptureException.getMessage())) {
                    message += ": " + idCaptureException.getMessage();
                }

                return message;
            }
        }
        return error.getMessage();
    }

    @Override
    public void onObservationStarted(@NotNull IdCapture mode) {
        // In this sample we are not interested in this callback.
    }

    @Override
    public void onObservationStopped(@NotNull IdCapture mode) {
        // In this sample we are not interested in this callback.
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dataCaptureManager.getIdCapture().setEnabled(true);
                }
            })
            .show();
    }
}

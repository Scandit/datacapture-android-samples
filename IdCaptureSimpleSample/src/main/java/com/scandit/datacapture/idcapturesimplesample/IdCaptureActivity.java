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

package com.scandit.datacapture.idcapturesimplesample;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.Toolbar;

import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.RejectionReason;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class IdCaptureActivity extends CameraPermissionActivity
        implements IdCaptureListener, AlertDialogFragment.Callbacks {

    @VisibleForTesting
    public static final String RESULT_FRAGMENT_TAG = "result_fragment";

    private static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();

    @VisibleForTesting
    public DataCaptureManager dataCaptureManager;
    private DataCaptureView dataCaptureView;
    private IdCaptureOverlay overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewGroup container = findViewById(R.id.data_capture_view_container);

        dataCaptureManager = DataCaptureManager.getInstance();

        /*
         * Create a new DataCaptureView and fill the screen with it. DataCaptureView will show
         * the camera preview on the screen. Pass your DataCaptureContext to the view's
         * constructor.
         */
        dataCaptureView =
                DataCaptureView.newInstance(this, dataCaptureManager.getDataCaptureContext());
        container.addView(
                dataCaptureView,
                new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        );

        overlay = IdCaptureOverlay.newInstance(dataCaptureManager.getIdCapture(), null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
         * Add overlay to data capture view.
         */
        dataCaptureView.addOverlay(overlay);

        /*
         * Switch the camera on. The camera frames will be sent to TextCapture for processing.
         * Additionally the preview will appear on the screen. The camera is started asynchronously,
         * and you may notice a small delay before the preview appears.
         */
        dataCaptureManager.getCamera().switchToDesiredState(FrameSourceState.ON);
    }

    @Override
    public void onPause() {
        super.onPause();

        /*
         * Switch the camera off to stop streaming frames. The camera is stopped asynchronously.
         */
        dataCaptureManager.getCamera().switchToDesiredState(FrameSourceState.OFF);
        dataCaptureManager.getIdCapture().removeListener(this);
        dataCaptureView.removeOverlay(overlay);
    }

    @Override
    public void onIdCaptured(@NonNull IdCapture mode, @NonNull CapturedId id) {
        final String message = getDescriptionForCapturedId(id);

        /*
         * Don't capture unnecessarily when the result is displayed.
         */
        dataCaptureManager.getIdCapture().setEnabled(false);

        /*
         * This callback may be executed on an arbitrary thread. We post to switch back
         * to the main thread.
         */
        runOnUiThread(() -> showAlert(R.string.captured_id_title, message));
    }

    @Override
    public void onIdRejected(
            @NonNull IdCapture mode,
            @Nullable CapturedId id,
            @NonNull RejectionReason reason
    ) {
        /*
         * Implement to handle documents recognized in a frame, but rejected.
         * A document or its part is considered rejected when:
         *   (a) it's a valid personal identification document, but not enabled in the settings,
         *   (b) it's a PDF417 barcode or a Machine Readable Zone (MRZ), but the data is encoded
         * in an unexpected format,
         *   (c) it's a voided document and rejectVoidedIds is enabled in the settings,
         *   (d) the document has been localized, but could not be captured within a period of time.
         */

        /*
         * Don't capture unnecessarily when the alert is displayed.
         */
        dataCaptureManager.getIdCapture().setEnabled(false);

        /*
         * This callback may be executed on an arbitrary thread. We post to switch back
         * to the main thread.
         */
        runOnUiThread(
                () -> showAlert(R.string.error_title,
                        reason == RejectionReason.TIMEOUT ? R.string.rejected_document_timeout_message :
                                R.string.rejected_document_not_supported_message));
    }

    private void showAlert(@StringRes int titleRes, @StringRes int messageRes) {
        showAlert(titleRes, getString(messageRes));
    }

    private void showAlert(@StringRes int titleRes, String message) {
        /*
         * Show the result fragment only if we are not displaying one at the moment.
         */
        if (getSupportFragmentManager().findFragmentByTag(RESULT_FRAGMENT_TAG) == null) {
            AlertDialogFragment
                    .newInstance(titleRes, message)
                    .show(getSupportFragmentManager(), RESULT_FRAGMENT_TAG);
        }
    }

    @Override
    public void onAlertDismissed() {
        /*
         * Enable capture again, after the alert dialog is dismissed.
         */
        dataCaptureManager.getIdCapture().setEnabled(true);
    }

    private String getDescriptionForCapturedId(CapturedId result) {
        StringBuilder builder = new StringBuilder();
        appendField(builder, "Full Name: ", result.getFullName());
        appendField(builder, "Date of Birth: ", result.getDateOfBirth());
        appendField(builder, "Date of Expiry: ", result.getDateOfExpiry());
        appendField(builder, "Document Number: ", result.getDocumentNumber());
        appendField(builder, "Nationality: ", result.getNationality());
        if (result.getDocument() != null) {
            appendField(builder, "Document Type: ", result.getDocument().getDocumentType().toString());
        }

        return builder.toString();
    }

    private void appendField(StringBuilder builder, String name, String value) {
        if (!TextUtils.isEmpty(value)) {
            builder.append(name);
            builder.append(value);
            builder.append("\n");
        }
    }

    private void appendField(StringBuilder builder, String name, DateResult value) {
        if (value != null) {
            builder.append(name);
            builder.append(dateFormat.format(value.getLocalDate()));
            builder.append("\n");
        }
    }
}

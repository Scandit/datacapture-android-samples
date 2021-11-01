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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureException;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSession;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.AamvaBarcodeResult;
import com.scandit.datacapture.id.data.VizResult;
import com.scandit.datacapture.id.ui.IdLayoutStyle;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class IdCaptureActivity extends CameraPermissionActivity
        implements IdCaptureListener, AlertDialogFragment.Callbacks {
    private static final String RESULT_FRAGMENT_TAG = "result_fragment";

    private static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();

    private DataCaptureManager dataCaptureManager;
    private DataCaptureView view;
    private IdCaptureOverlay overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewGroup container = findViewById(R.id.data_capture_view_container);

        dataCaptureManager = DataCaptureManager.getInstance(this);

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

        overlay = IdCaptureOverlay.newInstance(dataCaptureManager.getIdCapture(), view);
        overlay.setIdLayoutStyle(IdLayoutStyle.ROUNDED);
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
    }

    @Override
    public void onIdCaptured(
            @NotNull IdCapture mode,
            @NotNull final IdCaptureSession session,
            @NotNull FrameData data
    ) {
        final CapturedId capturedId = session.getNewlyCapturedId();

        final String message;

        // The recognized fields of the captured Id can vary based on the capturedResultType.
        switch (capturedId.getCapturedResultType()) {
            case AAMVA_BARCODE_RESULT:
                message = getDescriptionForAamvaBarcodeResult(capturedId);
                break;
            case VIZ_RESULT:
                message = getDescriptionForViz(capturedId);
                break;
            default: // For other documents only the basic info from CapturedId is returned.
               message = getDescriptionForCapturedId(capturedId);
               break;
        }

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
        /*
         * Implement to handle documents recognized in a frame, but rejected.
         * A document or its part is considered rejected when (a) it's valid, but not enabled in the settings,
         * (b) it's a barcode of a correct symbology or a Machine Readable Zone (MRZ),
         * but the data is encoded in an unexpected/incorrect format.
         */

        /*
         * Don't capture unnecessarily when the alert is displayed.
         */
        dataCaptureManager.getIdCapture().setEnabled(false);

        /*
         * This callback may be executed on an arbitrary thread. We post to switch back
         * to the main thread.
         */
        runOnUiThread(() -> showAlert(R.string.error_title, R.string.document_not_supported_message));
    }

    @Override
    public void onErrorEncountered(
            @NotNull IdCapture mode,
            @NotNull final Throwable error,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        /*
         * Implement to handle an error encountered during the capture process.
         * This callback may be executed on an arbitrary thread.
         */
    }

    @Override
    public void onObservationStarted(@NotNull IdCapture mode) {
        // In this sample we are not interested in this callback.
    }

    @Override
    public void onObservationStopped(@NotNull IdCapture mode) {
        // In this sample we are not interested in this callback.
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

    private String getDescriptionForAamvaBarcodeResult(CapturedId result) {
        StringBuilder builder = new StringBuilder();

        appendDescriptionForCapturedId(result, builder);

        AamvaBarcodeResult aamvaBarcode = result.getAamvaBarcode();

        appendField(builder, "AAMVA Version: ", aamvaBarcode.getAamvaVersion());
        appendField(builder, "Jurisdiction Version: ", aamvaBarcode.getJurisdictionVersion());
        appendField(builder, "IIN: ", aamvaBarcode.getIin());
        appendField(builder, "Issuing Jurisdiction: ", aamvaBarcode.getIssuingJurisdiction());
        appendField(builder, "Issuing Jurisdiction ISO: ", aamvaBarcode.getIssuingJurisdictionIso());
        appendField(builder, "Eye Color: ", aamvaBarcode.getEyeColor());
        appendField(builder, "Hair Color: ", aamvaBarcode.getHairColor());
        appendField(builder, "Height (inch): ", aamvaBarcode.getHeightInch());
        appendField(builder, "Height (cm): ", aamvaBarcode.getHeightCm());
        appendField(builder, "Weight (lbs): ", aamvaBarcode.getWeightLbs());
        appendField(builder, "Weight (kg): ", aamvaBarcode.getWeightKg());
        appendField(builder, "Place Of Birth: ", aamvaBarcode.getPlaceOfBirth());
        appendField(builder, "Race: ", aamvaBarcode.getRace());
        appendField(builder, "Document Discriminator Number: ", aamvaBarcode.getDocumentDiscriminatorNumber());
        appendField(builder, "Vehicle Class: ", aamvaBarcode.getVehicleClass());
        appendField(builder, "Restrictions Code: ", aamvaBarcode.getRestrictionsCode());
        appendField(builder, "Endorsements Code: ", aamvaBarcode.getEndorsementsCode());
        appendField(builder, "Card Revision Date: ", aamvaBarcode.getCardRevisionDate());
        appendField(builder, "Middle Name: ", aamvaBarcode.getMiddleName());
        appendField(builder, "Driver Name Suffix: ", aamvaBarcode.getDriverNameSuffix());
        appendField(builder, "Driver Name Prefix: ", aamvaBarcode.getDriverNamePrefix());
        appendField(builder, "Last Name Truncation: ", aamvaBarcode.getLastNameTruncation());
        appendField(builder, "First Name Truncation: ", aamvaBarcode.getFirstNameTruncation());
        appendField(builder, "Middle Name Truncation: ", aamvaBarcode.getMiddleNameTruncation());
        appendField(builder, "Alias Family Name: ", aamvaBarcode.getAliasFamilyName());
        appendField(builder, "Alias Given Name: ", aamvaBarcode.getAliasGivenName());
        appendField(builder, "Alias Suffix Name: ", aamvaBarcode.getAliasSuffixName());

        return builder.toString();
    }

    private String getDescriptionForViz(CapturedId result) {
        StringBuilder builder = new StringBuilder();

        appendDescriptionForCapturedId(result, builder);

        VizResult viz = result.getViz();

        appendField(builder, "Issuing Authority: ", viz.getIssuingAuthority());
        appendField(builder, "Issuing Jurisdiction: ", viz.getIssuingJurisdiction());
        appendField(builder, "Issuing Jurisdiction ISO: ", viz.getIssuingJurisdictionIso());
        appendField(builder, "Additional Name Information: ", viz.getAdditionalNameInformation());
        appendField(builder, "Additional Address Information: ", viz.getAdditionalAddressInformation());
        appendField(builder, "Place of Birth: ", viz.getPlaceOfBirth());
        appendField(builder, "Race: ", viz.getRace());
        appendField(builder, "Religion: ", viz.getReligion());
        appendField(builder, "Profession: ", viz.getProfession());
        appendField(builder, "Marital Status: ", viz.getMaritalStatus());
        appendField(builder, "Residential Status: ", viz.getResidentialStatus());
        appendField(builder, "Employer: ", viz.getEmployer());
        appendField(builder, "Personal Id Number: ", viz.getPersonalIdNumber());
        appendField(builder, "Document Additional Number: ", viz.getDocumentAdditionalNumber());

        return builder.toString();
    }

    private String getDescriptionForCapturedId(CapturedId result) {
        StringBuilder builder = new StringBuilder();
        appendDescriptionForCapturedId(result, builder);
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

    private void appendField(StringBuilder builder, String name, int value) {
        builder.append(name)
                .append(value)
                .append("\n");
    }

    private void appendField(StringBuilder builder, String name, Integer value) {
        builder.append(name);

        if (value == null) {
            builder.append("<empty>");
        } else {
            builder.append(value);
        }

        builder.append("\n");
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

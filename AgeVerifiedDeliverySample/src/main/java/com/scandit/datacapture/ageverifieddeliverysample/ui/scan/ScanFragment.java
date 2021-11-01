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

package com.scandit.datacapture.ageverifieddeliverysample.ui.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.scandit.datacapture.ageverifieddeliverysample.R;
import com.scandit.datacapture.ageverifieddeliverysample.di.Injector;
import com.scandit.datacapture.ageverifieddeliverysample.ui.manualentry.ManualEntryDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.ui.verificationresult.failure.VerificationFailureDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.ui.verificationresult.success.VerificationSuccessDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.util.EmptyOnTabSelectedListener;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.DriverLicenseSide.FRONT_VIZ;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.ScanDriverLicenseVizHintStatus.DISPLAYED;

/**
 * The fragment where the user may capture the recipient's document.
 *
 * The recommended process is as follows:
 * * If the recipient's identifies themselves with a driver's license, try to capture the barcode
 *   at the back side of the document first.
 * * After a short delay, if the barcode is still not captured, offer the user the option
 *   to OCR the front side of the document.
 * * After a short delay, if the data is still not captured, offer the user the option to enter
 *   the data manually.
 *
 * * If the recipient's identifies themselves with a passport, try to capture the passport MRZ.
 * * After a short delay, if the MRZ is still not captured, offer the user the option to enter
 *   the data manually.
 *
 * * Verify the recipient's document data. If the verification fails for some reason (for example
 *   the document is expired or the recipient is underage) the user may retry the capture process
 *   or to refuse the delivery entirely.
 * * If the verification is successful, the user may proceed with the delivery.
 */
public class ScanFragment extends Fragment {
    /**
     * The tag used by the fragment where a user may enter the information from the recipient's
     * document, had the capture process failed.
     */
    private static final String MANUAL_ENTRY_TAG = "MANUAL_ENTRY";

    /**
     * The tag used by the fragment that informs the user that a given PDF417 barcode
     * can be captured, but its data cannot be parsed.
     */
    private static final String BARCODE_NOT_SUPPORTED_TAG = "BARCODE_NOT_SUPPORTED";

    /**
     * The tag used by the fragment that informs the user the a given ID or MRZ is
     * detected, but cannot be parsed.
     */
    private static final String ID_LOCALIZED_BUT_NOT_CAPTURED_TAG = "ID_LOCALIZED_BUT_NOT_CAPTURED";

    /**
     * The tag used by the fragment that displays the result of the recipient's document data
     * verification.
     */
    private static final String VERIFICATION_RESULT_TAG = "VERIFICATION_RESULT";

    /**
     * DataCaptureContext is necessary to create DataCaptureView.
     */
    private DataCaptureContext dataCaptureContext;

    /**
     * The view model of this fragment.
     */
    private ScanViewModel viewModel;

    /**
     * The state representing the currently displayed UI.
     */
    private ScanUiState uiState;

    /**
     * DataCaptureView displays the camera preview and the additional UI to guide the user through
     * the capture process.
     */
    private DataCaptureView dataCaptureView;

    /**
     * The additional UI to guide the user through the capture process.
     */
    private IdCaptureOverlay idCaptureOverlay;

    /**
     * The toggle to switch between capturing the barcode at the back side of the recipient's
     * driver's license and OCRing the front side of the document.
     */
    private SwitchCompat driverLicenseSideToggle;

    /**
     * The view that optionally displays the hint that informs the user that they may attempt
     * to OCR the front side of the recipient's driver's license. The hint is displayed after
     * a short delay if the user is unable to capture the barcode at the back side of
     * the recipient's driver's license.
     */
    private View scanDriverLicenseVizHintContainer;

    /**
     * The view that optionally displays the hint that guides the user towards different
     * personal identification document type selection.
     */
    private View selectTargetDocumentHintContainer;

    /**
     * The additional hint to aid the user with the capture process. It reflects the currently
     * selected document kind and/or side.
     */
    private TextView scanHintText;

    /**
     * The view that optionally displays the hint that informs the user that they may attempt
     * to manually enter recipient's document data. The hint is displayed after a short delay if
     * the user is unable to OCR the front side of the recipient's driver's license or if
     * the user is unable to OCR the MRZ of the recipient's passport.
     */
    private View enterManuallyHint;

    /**
     * The menu that allows the user to switch between capturing a driver's license and capturing
     * a passport.
     */
    private TabLayout targetDocumentMenu;

    /**
     * The launcher to request the user permission to use their device's camera.
     */
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new RequestPermission(), isGranted -> {
                if (isGranted && isResumed()) {
                    viewModel.turnOnCamera();
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Obtain DataCaptureContext necessary to create DataCaptureView. We use our own
         * dependency injection to obtain it, but you may use your favorite framework, like
         * Dagger or Hilt instead.
         */
        dataCaptureContext = Injector.getInstance().getDataCaptureContext();

        /*
         * Get a reference to this fragment's view model.
         */
        viewModel = new ViewModelProvider(this).get(ScanViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        /*
         * Initialize the layout of this fragment and find all the view it needs to interact with.
         */
        View root = inflater.inflate(R.layout.scan_fragment, container, false);

        initViews(root);
        attachDataCaptureView(root);

        return root;
    }

    private void initViews(@NonNull View root) {
        driverLicenseSideToggle = root.findViewById(R.id.driver_license_side_toggle);
        scanDriverLicenseVizHintContainer = root.findViewById(R.id.scan_driver_license_viz_hint_container);
        selectTargetDocumentHintContainer = root.findViewById(R.id.select_target_document_hint_container);
        scanHintText = root.findViewById(R.id.scan_hint_text);
        enterManuallyHint = root.findViewById(R.id.enter_manually_hint);
        targetDocumentMenu = root.findViewById(R.id.target_document_menu);
    }

    /*
     * Create a new DataCaptureView and fill the screen with it. DataCaptureView will show
     * the camera preview on the screen. Pass your DataCaptureContext to the view's
     * constructor.
     */
    private void attachDataCaptureView(@NonNull View root) {
        ViewGroup dataCaptureViewContainer = root.findViewById(R.id.data_capture_view_container);

        dataCaptureView = DataCaptureView.newInstance(requireContext(), dataCaptureContext);
        dataCaptureViewContainer.addView(dataCaptureView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        dataCaptureView = null;
        idCaptureOverlay = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        /*
         * Observe the sequence of desired UI states in order to update the UI.
         */
        viewModel.uiStates().observe(lifecycleOwner, this::onNewUiState);

        /*
         * Observe the sequences of events in order to navigate to other screens or display dialogs.
         */
        viewModel.goToManualEntry().observe(lifecycleOwner, this::goToManualEntry);
        viewModel.goToBarcodeNotSupported().observe(lifecycleOwner, this::goToBarcodeNotSupported);
        viewModel.goToIdLocalizedButNotCaptured().observe(lifecycleOwner, this::goToIdLocalizedButNotCaptured);
        viewModel.goToVerificationFailure().observe(lifecycleOwner, this::goToVerificationFailure);
        viewModel.goToVerificationSuccess().observe(lifecycleOwner, this::goToVerificationSuccess);

        /*
         * Add the listeners necessary to interact with the UI.
         */
        driverLicenseSideToggle.setOnCheckedChangeListener(this::onDriverLicenseSideToggled);
        scanDriverLicenseVizHintContainer.setOnClickListener(this::onDriverLicenseVizHintClicked);
        selectTargetDocumentHintContainer.setOnClickListener(this::onSelectTargetDocumentHintClicked);
        targetDocumentMenu.addOnTabSelectedListener(new OnTargetDocumentChangedListener());
        enterManuallyHint.setOnClickListener(this::onManualEntryClicked);
    }

    /**
     * The const representing the driver's license tab.
     */
    private static final int TARGET_DOCUMENT_ITEM_DRIVER_LICENSE = 0;

    /**
     * The const representing the passport tab.
     */
    private static final int TARGET_DOCUMENT_ITEM_PASSPORT = 1;

    /**
     * Inform the view model that the user selected a different target document.
     */
    private boolean onTargetDocumentSelected(@NonNull TabLayout.Tab tab) {
        int position = tab.getPosition();

        if (position == TARGET_DOCUMENT_ITEM_DRIVER_LICENSE) {
            viewModel.onDriverLicenseSelected();
        } else if (position == TARGET_DOCUMENT_ITEM_PASSPORT) {
            viewModel.onPassportSelected();
        }

        return true;
    }

    /**
     * Inform the view model that the user selected a different driver's license side.
     */
    private void onDriverLicenseSideToggled(@NonNull CompoundButton toggle, boolean isChecked) {
        viewModel.onFrontBackToggled(isChecked);
    }

    /**
     * Dismiss the hint once the user clicked it.
     */
    private void onDriverLicenseVizHintClicked(@NonNull View view) {
        viewModel.onDriverLicenseVizHintClicked();
    }

    /**
     * Dismiss the hint once the user clicked it.
     */
    private void onSelectTargetDocumentHintClicked(@NonNull View view) {
        viewModel.onSelectTargetDocumentHintClicked();
    }

    /**
     * Inform the view model that the user intends to enter the recipient's document data manually.
     */
    private void onManualEntryClicked(View view) {
        viewModel.onManualEntrySelected();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * Check for camera permission and request it, if it hasn't yet been granted.
         * Once we have the permission start the capture process.
         */
        if (checkSelfPermission(requireContext(), CAMERA) == PERMISSION_GRANTED) {
            viewModel.turnOnCamera();
        } else {
            requestCameraPermission.launch(CAMERA);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        /*
         * Switch the camera off to stop streaming frames. The camera is stopped asynchronously.
         */
        viewModel.turnOffCamera();
    }

    /**
     * Update the displayed UI.
     */
    private void onNewUiState(ScanUiState uiState) {
        this.uiState = uiState;

        updateOverlay();
        updateDriverLicenseSideToggle();
        updateScanDriverLicenseVizHint();
        updateSelectTargetDocumentHint();
        updateScanHint();
        updateEnterManuallyHint();
    }

    /**
     * Update the IdCapture UI to aid the user in the capture process. The UI reflects the currently
     * selected document type and/or side.
     */
    private void updateOverlay() {
        if (idCaptureOverlay == uiState.getOverlay()) {
            return;
        }

        if (idCaptureOverlay != null) {
            dataCaptureView.removeOverlay(idCaptureOverlay);
        }

        idCaptureOverlay = uiState.getOverlay();

        if (uiState.getOverlay() != null) {
            dataCaptureView.addOverlay(idCaptureOverlay);
        }
    }

    /**
     * Update the toggle to switch between capturing the barcode at the back side of the recipient's
     * driver's license and OCRing the front side of the document.
     */
    private void updateDriverLicenseSideToggle() {
        boolean shouldBeChecked = uiState.getDriverLicenseSide() == FRONT_VIZ;

        if (driverLicenseSideToggle.isChecked() != shouldBeChecked) {
            driverLicenseSideToggle.setOnCheckedChangeListener(null);
            driverLicenseSideToggle.setChecked(shouldBeChecked);
            driverLicenseSideToggle.setOnCheckedChangeListener(this::onDriverLicenseSideToggled);
        }

        driverLicenseSideToggle.setVisibility(uiState.getDriverLicenseToggleVisibility());
    }

    /**
     * Update the hint that informs the user that they may attempt to OCR the front side of
     * the recipient's driver's license. The hint is displayed after a short delay if the user
     * is unable to capture the barcode at the back side of the recipient's driver's license.
     */
    private void updateScanDriverLicenseVizHint() {
        if (uiState.getScanDriverLicenseVizHintStatus() == DISPLAYED) {
            scanDriverLicenseVizHintContainer.setVisibility(uiState.getScanDriverLicenseVizHintVisibility());
        } else {
            scanDriverLicenseVizHintContainer.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Update the hint that guides the user towards different personal identification
     * document type selection.
     */
    private void updateSelectTargetDocumentHint() {
        selectTargetDocumentHintContainer.setVisibility(uiState.getSelectTargetDocumentHintVisibility());
    }

    /**
     * Update the additional hint to aid the user with the capture process. It reflects
     * the currently selected document kind and/or side.
     */
    private void updateScanHint() {
        scanHintText.setText(uiState.getScanHint());
    }

    /**
     * Update the hint that informs the user that they may attempt
     * to manually enter recipient's document data. The hint is displayed after a short delay if
     * the user is unable to OCR the front side of the recipient's driver's license or if
     * the user is unable to OCR the MRZ of the recipient's passport.
     */
    private void updateEnterManuallyHint() {
        enterManuallyHint.setVisibility(uiState.getEnterManuallyHintVisibility());
    }

    /**
     * Display the UI to manually enter the recipient's document data.
     */
    private void goToManualEntry(GoToManualEntry event) {
        if (!event.isHandled() && getChildFragmentManager().findFragmentByTag(MANUAL_ENTRY_TAG) == null) {
            ManualEntryDialogFragment.create().show(getChildFragmentManager(), MANUAL_ENTRY_TAG);
        }
    }

    /**
     * Display the UI that informs the user that a given PDF417 barcode can be captured, but its
     * data cannot be parsed.
     */
    private void goToBarcodeNotSupported(GoToBarcodeNotSupported event) {
        if (!event.isHandled() && getChildFragmentManager().findFragmentByTag(BARCODE_NOT_SUPPORTED_TAG) == null) {
            BarcodeNotSupportedDialogFragment.create()
                    .show(getChildFragmentManager(), BARCODE_NOT_SUPPORTED_TAG);
        }
    }

    /**
     * Display the UI that informs the user the a given ID or MRZ is detected, but cannot be parsed.
     */
    private void goToIdLocalizedButNotCaptured(GoToIdLocalizedButNotCaptured event) {
        if (!event.isHandled() && getChildFragmentManager().findFragmentByTag(ID_LOCALIZED_BUT_NOT_CAPTURED_TAG) == null) {
            IdLocalizedButNotCapturedDialogFragment.create()
                    .show(getChildFragmentManager(), ID_LOCALIZED_BUT_NOT_CAPTURED_TAG);
        }
    }

    /**
     * Display the UI that informs the user that the recipient's document data verification failed
     * and explains the reason. That UI allows either to retry the capture process or to refuse
     * the delivery entirely.
     */
    private void goToVerificationFailure(GoToVerificationFailure event) {
        VerificationFailureReason reason = event.getContentIfNotHandled();

        if (reason != null) {
            VerificationFailureDialogFragment.create(reason)
                    .show(getChildFragmentManager(), VERIFICATION_RESULT_TAG);
        }
    }

    /**
     * Display the UI that informs the user that the recipient's document data verification was
     * successful and allows to proceed with the delivery.
     */
    private void goToVerificationSuccess(GoToVerificationSuccess event) {
        if (!event.isHandled()) {
            VerificationSuccessDialogFragment.create()
                    .show(getChildFragmentManager(), VERIFICATION_RESULT_TAG);
        }
    }

    /**
     * The listener to observe the changes of the document type to capture.
     */
    private class OnTargetDocumentChangedListener extends EmptyOnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            onTargetDocumentSelected(tab);
        }
    }
}

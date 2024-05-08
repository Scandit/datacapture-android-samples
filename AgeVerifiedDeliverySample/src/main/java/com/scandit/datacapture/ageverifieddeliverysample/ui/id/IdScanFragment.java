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

package com.scandit.datacapture.ageverifieddeliverysample.ui.id;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.scandit.datacapture.ageverifieddeliverysample.R;
import com.scandit.datacapture.ageverifieddeliverysample.di.Injector;
import com.scandit.datacapture.ageverifieddeliverysample.ui.barcode.BarcodeScanFragment;
import com.scandit.datacapture.ageverifieddeliverysample.ui.manualentry.ManualEntryDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.ui.timeout.IdCaptureFirstTimeoutDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.ui.timeout.IdCaptureSubsequentTimeoutDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.ui.verificationresult.failure.VerificationFailureDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.ui.verificationresult.failure.VerificationFailureReason;
import com.scandit.datacapture.ageverifieddeliverysample.ui.verificationresult.success.VerificationSuccessDialogFragment;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

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
public class IdScanFragment extends Fragment {

    /**
     * The tag that uniquely identifies the ID scanning screen
     */
    public static final String TAG = "ID_SCAN_FRAGMENT";

    /**
     * DataCaptureContext is necessary to create DataCaptureView.
     */
    private DataCaptureContext dataCaptureContext;

    /**
     * The view model of this fragment.
     */
    private IdScanViewModel viewModel;

    /**
     * The state representing the currently displayed UI.
     */
    private IdScanUiState uiState;

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
     * The launcher to request the user permission to use their device's camera.
     */
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new RequestPermission(), isGranted -> {
                if (isGranted && isResumed()) {
                    viewModel.resumeCapture();
                }
            });

    public static IdScanFragment create() {
        return new IdScanFragment();
    }

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
        viewModel = new ViewModelProvider(this).get(IdScanViewModel.class);
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
        View root = inflater.inflate(R.layout.id_scan_fragment, container, false);
        initToolbar(root);
        attachDataCaptureView(root);

        return root;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.setUpIdCaptureMode();

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        /*
         * Observe the sequence of desired UI states in order to update the UI.
         */
        viewModel.uiStates().observe(lifecycleOwner, this::onNewUiState);

        /*
         * Observe the sequences of events in order to navigate to other screens or display dialogs.
         */
        viewModel.goToManualEntry().observe(lifecycleOwner, this::goToManualEntry);
        viewModel.goToFirstTimeoutDialog().observe(lifecycleOwner, this::goToFirstTimeoutDialog);
        viewModel.goToSubsequentTimeoutDialog().observe(lifecycleOwner, this::goToSubsequentTimeoutDialog);
        viewModel.goToVerificationFailure().observe(lifecycleOwner, this::goToVerificationFailure);
        viewModel.goToVerificationSuccess().observe(lifecycleOwner, this::goToVerificationSuccess);
        viewModel.goToBarcodeScanningScreen().observe(lifecycleOwner, event -> {
            if (!event.isHandled()) {
                goToBarcodeScanningScreen();
            }
        });
        viewModel.goToUnsupportedDocument().observe(lifecycleOwner, this::goToUnsupportedDocument);
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * Check for camera permission and request it, if it hasn't yet been granted.
         * Once we have the permission start the capture process.
         */
        if (checkSelfPermission(requireContext(), CAMERA) == PERMISSION_GRANTED) {
            viewModel.resumeCapture();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        viewModel.removeIdCaptureMode();
        dataCaptureView = null;
        idCaptureOverlay = null;
    }

    /**
     * Update the displayed UI.
     */
    private void onNewUiState(IdScanUiState uiState) {
        this.uiState = uiState;

        updateOverlay();
    }

    /**
     * Set up the toolbar and the back navigation.
     */
    private void initToolbar(View root) {
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> viewModel.resetScanningFlow());
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
     * Display the UI to manually enter the recipient's document data.
     */
    private void goToManualEntry(GoToManualEntry event) {
        if (!event.isHandled() &&
                getChildFragmentManager().findFragmentByTag(ManualEntryDialogFragment.TAG) ==
                        null) {
            ManualEntryDialogFragment.create()
                    .show(getChildFragmentManager(), ManualEntryDialogFragment.TAG);
        }
    }

    /**
     * Display the UI that informs the user that the given ID or MRZ is detected, but cannot be parsed.
     */
    private void goToFirstTimeoutDialog(GoToTimeoutDialog event) {
        if (!event.isHandled() && getChildFragmentManager().findFragmentByTag(
                IdCaptureFirstTimeoutDialogFragment.TAG) == null) {
            IdCaptureFirstTimeoutDialogFragment.create()
                    .show(getChildFragmentManager(), IdCaptureFirstTimeoutDialogFragment.TAG);
        }
    }

    /**
     * Display the UI that informs the user another time that the given ID or MRZ is detected,
     * but cannot be parsed.
     */
    private void goToSubsequentTimeoutDialog(GoToSubsequentTimeoutDialog event) {
        if (!event.isHandled() && getChildFragmentManager().findFragmentByTag(
                IdCaptureSubsequentTimeoutDialogFragment.TAG) == null) {
            IdCaptureSubsequentTimeoutDialogFragment.create()
                    .show(getChildFragmentManager(), IdCaptureSubsequentTimeoutDialogFragment.TAG);

        }
    }

    /**
     * Display the UI that informs the user that the recipient's document data verification failed.
     */
    private void goToVerificationFailure(GoToVerificationFailure event) {
        VerificationFailureReason reason = event.getContentIfNotHandled();

        if (reason != null && getChildFragmentManager().findFragmentByTag(
                VerificationFailureDialogFragment.TAG) == null) {
            VerificationFailureDialogFragment.create(reason)
                    .show(getChildFragmentManager(), VerificationFailureDialogFragment.TAG);
        }
    }

    /**
     * Display the UI that informs the user that the recipient's document data verification was
     * successful and allows to proceed with the delivery.
     */
    private void goToVerificationSuccess(GoToVerificationSuccess event) {
        if (!event.isHandled() && getChildFragmentManager().findFragmentByTag(
                VerificationSuccessDialogFragment.TAG) == null) {
            VerificationSuccessDialogFragment.create()
                    .show(getChildFragmentManager(), VerificationSuccessDialogFragment.TAG);
        }
    }

    /**
     * Navigate to the barcode scanning fragment to capture the barcode of the package.
     */
    private void goToBarcodeScanningScreen() {
        if (requireActivity().getSupportFragmentManager()
                .findFragmentByTag(BarcodeScanFragment.TAG) == null) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.scan_fragment_container,
                            BarcodeScanFragment.create(),
                            BarcodeScanFragment.TAG)
                    .commit();
        }
    }

    /**
     * Display the UI that informs the user that the document can be captured, but it is
     * not supported.
     */
    private void goToUnsupportedDocument(GoToUnsupportedDocument event) {
        if (!event.isHandled() && getChildFragmentManager().findFragmentByTag(
                UnsupportedDocumentDialogFragment.TAG) == null) {
            UnsupportedDocumentDialogFragment.create()
                    .show(getChildFragmentManager(), UnsupportedDocumentDialogFragment.TAG);
        }
    }
}

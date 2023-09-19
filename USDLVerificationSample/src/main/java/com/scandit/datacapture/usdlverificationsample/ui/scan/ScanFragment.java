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

package com.scandit.datacapture.usdlverificationsample.ui.scan;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.usdlverificationsample.R;
import com.scandit.datacapture.usdlverificationsample.di.Injector;
import com.scandit.datacapture.usdlverificationsample.ui.result.CaptureResult;
import com.scandit.datacapture.usdlverificationsample.ui.result.ResultBottomSheetFragment;
import com.scandit.datacapture.usdlverificationsample.ui.util.AlertDialogFragment;

public class ScanFragment extends Fragment implements AlertDialogFragment.Callbacks {

    private static final String DL_NOT_SUPPORTED_TAG = "DL_NOT_SUPPORTED";
    private static final String BARCODE_VERIFICATION_FAILURE_TAG = "BARCODE_VERIFICATION_FAILURE";
    private static final String RESULT_SCREEN_TAG = "RESULT_SCREEN";

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
     * The additional text that indicates whether verification is running.
     */
    private TextView barcodeVerificationIndicatorText;

    /**
     * The launcher to request the user permission to use their device's camera.
     */
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted && isResumed()) {
                            viewModel.startIdCapture();
                        }
                    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        View root = inflater.inflate(R.layout.scan_screen, container, false);

        initToolbar(root);
        initViews(root);
        attachDataCaptureView(root);

        return root;
    }

    private void initToolbar(View root) {
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(R.string.app_name);
    }

    private void initViews(@NonNull View root) {
        barcodeVerificationIndicatorText = root.findViewById(R.id.barcode_verification_indicator_text);
    }

    /*
     * Create a new DataCaptureView and fill the screen with it. DataCaptureView will show
     * the camera preview on the screen. Pass your DataCaptureContext to the view's
     * constructor.
     */
    private void attachDataCaptureView(View root) {
        ViewGroup dataCaptureViewContainer = root.findViewById(R.id.data_capture_view_container);

        dataCaptureView = DataCaptureView.newInstance(requireContext(), dataCaptureContext);
        dataCaptureViewContainer.addView(dataCaptureView,
                new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
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
        viewModel.goToResult().observe(lifecycleOwner, this::goToResult);
        viewModel.goToUnsupportedDriverLicense().observe(lifecycleOwner, this::goToUnsupportedDriverLicense);
        viewModel.goToBarcodeVerificationFailure().observe(lifecycleOwner,
                this::goToBarcodeVerificationFailure);

        viewModel.setUpBarcodeVerificationTaskListeners();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * Check for camera permission and request it, if it hasn't yet been granted.
         * Once we have the permission start the capture process.
         */
        if (checkSelfPermission(requireContext(), CAMERA) == PERMISSION_GRANTED) {
            viewModel.startIdCapture();
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
        viewModel.stopIdCapture();
    }

    /**
     * Update the displayed UI.
     */
    private void onNewUiState(ScanUiState uiState) {
        this.uiState = uiState;

        updateOverlay();
        updateVerificationIndicatorVisibility(uiState);
    }

    /**
     * Display a message indicating that the verification is running if it is the case,
     * hide it otherwise.
     */
    private void updateVerificationIndicatorVisibility(ScanUiState uiState) {
        barcodeVerificationIndicatorText.setVisibility(
                uiState.isBarcodeVerificationRunning() ? View.VISIBLE : View.GONE);
    }

    /**
     * Update the IdCapture UI to aid the user in the capture process. The UI reflects the current
     * document side.
     */
    private void updateOverlay() {
        if (idCaptureOverlay == uiState.getOverlay()) {
            return;
        }

        if (idCaptureOverlay != null) {
            dataCaptureView.removeOverlay(idCaptureOverlay);
        }

        idCaptureOverlay = uiState.getOverlay();

        if (idCaptureOverlay != null) {
            dataCaptureView.addOverlay(idCaptureOverlay);
        }
    }

    /**
     * Show a dialog that informs the user that the captured driver's license is not issued in
     * the United States.
     */
    private void goToUnsupportedDriverLicense(GoToUnsupportedDriverLicense event) {
        if (event.getContentIfNotHandled() == null) {
            return;
        }

        if (getChildFragmentManager().findFragmentByTag(DL_NOT_SUPPORTED_TAG) == null) {
            AlertDialogFragment.newInstance(R.string.error,
                            getString(R.string.scanning_dl_not_supported_message))
                    .show(getChildFragmentManager(), DL_NOT_SUPPORTED_TAG);
        }
    }

    /**
     * Show a dialog that informs the user that the verification failed.
     */
    private void goToBarcodeVerificationFailure(GoToBarcodeVerificationFailure event) {
        if (event.getContentIfNotHandled() == null) {
            return;
        }

        if (getChildFragmentManager().findFragmentByTag(BARCODE_VERIFICATION_FAILURE_TAG) == null) {
            AlertDialogFragment.newInstance(R.string.error,
                            getString(R.string.scanning_dl_barcode_verification_error))
                    .show(getChildFragmentManager(), BARCODE_VERIFICATION_FAILURE_TAG);
        }
    }

    @Override
    public void onAlertDismissed() {
        viewModel.startIdCapture();
    }

    /**
     * Navigate to the screen that shows data extracted from a driver's license.
     */
    private void goToResult(GoToResult event) {
        CaptureResult content = event.getContentIfNotHandled();
        if (content == null) {
            return;
        }

        if (getChildFragmentManager().findFragmentByTag(RESULT_SCREEN_TAG) == null) {
            ResultBottomSheetFragment.create(content)
                    .show(getChildFragmentManager(), RESULT_SCREEN_TAG);
        }
    }
}

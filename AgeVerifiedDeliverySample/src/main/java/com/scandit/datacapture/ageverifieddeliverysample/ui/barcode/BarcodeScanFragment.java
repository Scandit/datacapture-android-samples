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

package com.scandit.datacapture.ageverifieddeliverysample.ui.barcode;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.scandit.datacapture.ageverifieddeliverysample.R;
import com.scandit.datacapture.ageverifieddeliverysample.di.Injector;
import com.scandit.datacapture.ageverifieddeliverysample.ui.id.IdScanFragment;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.ui.DataCaptureView;

/**
 * The fragment where the user may capture the package's barcode.
 */
public class BarcodeScanFragment extends Fragment {

    /**
     * The tag that uniquely identifies the barcode scanning screen
     */
    public static final String TAG = "BARCODE_SCAN_FRAGMENT";

    /**
     * DataCaptureContext is necessary to create DataCaptureView.
     */
    private DataCaptureContext dataCaptureContext;

    /**
     * The view model of this fragment.
     */
    private BarcodeScanViewModel viewModel;

    /**
     * The state representing the currently displayed UI.
     */
    private BarcodeScanUiState uiState;

    /**
     * DataCaptureView displays the camera preview and the additional UI to guide the user through
     * the capture process.
     */
    private DataCaptureView dataCaptureView;

    /**
     * The additional UI to guide the user through the capture process.
     */
    private BarcodeCaptureOverlay barcodeCaptureOverlay;

    /**
     * The launcher to request the user permission to use their device's camera.
     */
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new RequestPermission(), isGranted -> {
                if (isGranted && isResumed()) {
                    viewModel.resumeCapture();
                }
            });

    public static BarcodeScanFragment create() {
        return new BarcodeScanFragment();
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
        viewModel = new ViewModelProvider(this).get(BarcodeScanViewModel.class);
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
        View root = inflater.inflate(R.layout.barcode_scan_fragment, container, false);
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

        viewModel.setUpBarcodeCaptureMode();

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        /*
         * Observe the sequence of desired UI states in order to update the UI.
         */
        viewModel.uiStates().observe(lifecycleOwner, this::onNewUiState);

        /*
         * Observe the sequences of events in order to navigate to other screens or display dialogs.
         */
        viewModel.goToAgeVerificationRequired().observe(lifecycleOwner, this::goToAgeVerificationRequired);
        viewModel.goToIdScanningScreen().observe(lifecycleOwner, this::goToIdScanningScreen);
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

        viewModel.removeBarcodeCaptureMode();
        dataCaptureView = null;
        barcodeCaptureOverlay = null;
    }

    /**
     * Update the displayed UI.
     */
    private void onNewUiState(BarcodeScanUiState uiState) {
        this.uiState = uiState;

        updateOverlay();
    }

    /**
     * Update the BarcodeCapture UI to aid the user in the capture process.
     */
    private void updateOverlay() {
        if (barcodeCaptureOverlay == uiState.getOverlay()) {
            return;
        }

        if (barcodeCaptureOverlay != null) {
            dataCaptureView.removeOverlay(barcodeCaptureOverlay);
        }

        barcodeCaptureOverlay = uiState.getOverlay();

        if (uiState.getOverlay() != null) {
            dataCaptureView.addOverlay(barcodeCaptureOverlay);
        }
    }

    /**
     * Display the UI that informs the user that the delivery requires an age verification
     * of the recipient.
     */
    private void goToAgeVerificationRequired(GoToAgeVerificationRequired event) {
        if (event.getContentIfNotHandled() != null) {
            AgeVerificationRequiredDialogFragment.create()
                    .show(getChildFragmentManager(), AgeVerificationRequiredDialogFragment.TAG);
        }
    }

    /**
     * Navigate to the ID scanning fragment to capture the ID of the recipient.
     */
    private void goToIdScanningScreen(GoToIdScanning event) {
        if (!event.isHandled() && requireActivity().getSupportFragmentManager().findFragmentByTag(
                IdScanFragment.TAG) == null) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.scan_fragment_container,
                            IdScanFragment.create(),
                            IdScanFragment.TAG)
                    .commit();
        }
    }
}

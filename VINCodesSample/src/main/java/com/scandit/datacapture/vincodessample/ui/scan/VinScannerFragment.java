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

package com.scandit.datacapture.vincodessample.ui.scan;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.scandit.datacapture.core.ui.overlay.DataCaptureOverlay;
import com.scandit.datacapture.vincodessample.R;
import com.scandit.datacapture.vincodessample.di.Injector;
import com.scandit.datacapture.vincodessample.ui.MainActivity;
import com.scandit.datacapture.vincodessample.ui.util.AlertDialogFragment;

public class VinScannerFragment extends Fragment implements AlertDialogFragment.Callbacks {
    /**
     * DataCaptureContext is necessary to create DataCaptureView.
     */
    private DataCaptureContext dataCaptureContext;

    /**
     * The view model of this fragment.
     */
    private VinScannerViewModel viewModel;

    /**
     * The state representing the currently displayed UI.
     */
    private VinScannerUiState uiState;

    /**
     * DataCaptureView displays the camera preview and the additional UI to guide the user through
     * the capture process.
     */
    private DataCaptureView dataCaptureView;

    /**
     * The additional UI to guide the user through the capture process.
     */
    private DataCaptureOverlay overlay;

    /**
     * Click this button in order to capture barcodes that encode VIN data.
     */
    private Button switchToBarcodeCapture;

    /**
     * Click this button in order to OCR VIN in plain text.
     */
    private Button switchToTextCapture;

    /**
     * The launcher to request the user permission to use their device's camera.
     */
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted && isResumed()) {
                    viewModel.startCapture();
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
        viewModel = new ViewModelProvider(this).get(VinScannerViewModel.class);
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
        View root = inflater.inflate(R.layout.vin_scanner_screen, container, false);

        initToolbar(root);
        attachDataCaptureView(root);
        initViews(root);

        return root;
    }

    private void initToolbar(View root) {
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(R.string.app_name);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews(View root) {
        switchToBarcodeCapture = root.findViewById(R.id.switch_to_barcode_capture_button);
        switchToTextCapture = root.findViewById(R.id.switch_to_text_capture_button);
    }

    /*
     * Create a new DataCaptureView and fill the screen with it. DataCaptureView will show
     * the camera preview on the screen. Pass your DataCaptureContext to the view's
     * constructor.
     */
    private void attachDataCaptureView(View root) {
        ViewGroup dataCaptureViewContainer = root.findViewById(R.id.data_capture_view_container);

        dataCaptureView = DataCaptureView.newInstance(requireContext(), dataCaptureContext);
        dataCaptureViewContainer.addView(dataCaptureView, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        dataCaptureView = null;
        overlay = null;
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
        viewModel.showVinParsingError().observe(lifecycleOwner, this::showVinParsingError);

        /*
         * Add the listeners necessary to interact with the UI.
         */
        switchToBarcodeCapture.setOnClickListener(this::switchToBarcodeCapture);
        switchToTextCapture.setOnClickListener(this::switchToTextCapture);
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * Check for camera permission and request it, if it hasn't yet been granted.
         * Once we have the permission start the capture process.
         */
        if (checkSelfPermission(requireContext(), CAMERA) == PERMISSION_GRANTED) {
            viewModel.startCapture();
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
        viewModel.stopCapture();
    }

    /**
     * Capture barcodes that encode VINs.
     */
    private void switchToBarcodeCapture(View view) {
        viewModel.switchTo(VinScannerMode.BARCODE);
    }

    /**
     * OCR VIns in plain text.
     */
    private void switchToTextCapture(View view) {
        viewModel.switchTo(VinScannerMode.TEXT);
    }

    /**
     * Update the displayed UI.
     */
    private void onNewUiState(VinScannerUiState uiState) {
        this.uiState = uiState;

        updateOverlay();
        updateButtons();
    }

    /**
     * Update the IdCapture UI to aid the user in the capture process. The UI reflects the currently
     * selected document type and/or side.
     */
    private void updateOverlay() {
        if (overlay == uiState.getOverlay()) {
            return;
        }

        if (overlay != null) {
            dataCaptureView.removeOverlay(overlay);
        }

        overlay = uiState.getOverlay();

        if (overlay != null) {
            dataCaptureView.addOverlay(overlay);
        }
    }

    private void updateButtons() {
        if (uiState.getMode() == VinScannerMode.BARCODE) {
            switchToBarcodeCapture.setEnabled(false);
            switchToTextCapture.setEnabled(true);
        } else {
            switchToBarcodeCapture.setEnabled(true);
            switchToTextCapture.setEnabled(false);
        }
    }

    /**
     * Display the data decoded from a Vehicle Identification Number (VIN).
     */
    private void goToResult(GoToResult event) {
        if (event.getContentIfNotHandled() == null) {
            return;
        }

        ((MainActivity) requireActivity()).goToResultScreen();
    }

    /**
     * Display an error encountered when parsing VINs.
     */
    private void showVinParsingError(ShowVinParsingError event) {
        String message = event.getContentIfNotHandled();

        if (message == null) {
            return;
        }

        AlertDialogFragment.newInstance(R.string.vin_scanner_parsing_error_title, message)
                .show(getChildFragmentManager(), "VIN_PARSING_ERROR");
    }

    @Override
    public void onAlertDismissed() {
        /*
         * Resume the capture process once the dialog is dismissed.
         */
        viewModel.enableMode();
    }
}

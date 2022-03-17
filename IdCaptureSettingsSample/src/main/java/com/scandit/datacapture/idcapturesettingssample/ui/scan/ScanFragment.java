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

package com.scandit.datacapture.idcapturesettingssample.ui.scan;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;
import com.scandit.datacapture.idcapturesettingssample.ui.result.ResultFragment;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.overview.OverviewSettingsFragment;
import com.scandit.datacapture.idcapturesettingssample.utils.ShowHideViewTimer;

public class ScanFragment extends Fragment {
    /**
     * The tag used by the fragment that displays the details of a captured personal
     * identification document data.
     */
    private static final String CAPTURE_RESULT_TAG = "CAPTURE_RESULT";
    private static final String SETTINGS_TAG = "SETTINGS";

    /**
     * DataCaptureContext is necessary to create DataCaptureView.
     */
    private DataCaptureContext dataCaptureContext;

    /**
     * The view model of this fragment.
     */
    private ScanViewModel viewModel;

    /**
     * DataCaptureView displays the camera preview and the additional UI to guide the user through
     * the capture process.
     */
    private DataCaptureView dataCaptureView;

    /**
     * The additional UI to guide the user through the capture process.
     */
    private IdCaptureOverlay overlay;

    /**
     * The Toast used to display results in continuous mode.
     */
    private volatile TextView resultView;

    /**
     * A timer used to hide the result view after two seconds in continuous mode.
     */
    private volatile ShowHideViewTimer resultViewTimer;

    /**
     * The launcher to request the user permission to use their device's camera.
     */
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted && isResumed()) {
                    onCameraPermissionGranted();
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * Enable the options menu for the fragment as we need to display the gear icon which takes
         * to the settings screen.
         */
        setHasOptionsMenu(true);

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            showSettingsFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_scan, container, false);

        /*
         * Retrieve the view used to show results in continuous mode
         */
        resultView = layout.findViewById(R.id.text_result);

        setupActionBar();

        /*
         * Create a new DataCaptureView and fill the screen with it. DataCaptureView will show
         * the camera preview on the screen. Pass your DataCaptureContext to the view's
         * constructor.
         */
        dataCaptureView = viewModel.buildDataCaptureView(requireContext());
        layout.addView(
                dataCaptureView,
                new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        );
        return layout;
    }

    private void setupActionBar() {
        /*
         * Make sure the back arrow is hidden and the correct title for the fragment is displayed.
         */
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(R.string.app_readable_name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        /*
         * When destroying the fragment's view, we want to cancel the timer, remove and unset
         * overlays and the data capture view
         */
        cancelCurrentResultTimer();


        if (overlay != null) {
            dataCaptureView.removeOverlay(overlay);
        }
        dataCaptureView = null;
        overlay = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        viewModel.refreshComponents();

        /*
         * Display an IdCaptureOverlay to aid the user in the capture process. The UI reflects the currently
         * selected document type and/or side.
         */
        overlay = viewModel.buildIdCaptureOverlay();
        dataCaptureView.addOverlay(overlay);

        /*
         * Observe the sequences of events in order to navigate to other screens or display dialogs.
         */
        viewModel.showCapturedResult().observe(lifecycleOwner, this::showCapturedResultFragment);
        viewModel.showCapturedResultToast().observe(lifecycleOwner, this::showCapturedResultText);
        viewModel.showScanBack().observe(lifecycleOwner, this::showScanBackAlert);
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * Check for camera permission and request it, if it hasn't yet been granted.
         * Once we have the permission start the capture process.
         */
        if (checkSelfPermission(requireContext(), CAMERA) == PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        } else {
            requestCameraPermission.launch(CAMERA);
        }
    }

    public void onCameraPermissionGranted() {
        /*
         * Switch the camera on. The camera frames will be sent to TextCapture for processing.
         * The preview will appear on the screen. The camera is started asynchronously,
         * and you may notice a small delay before the preview appears.
         *
         * Enable the IdCapture mode when resuming the fragment. This ensures that,
         * when returning from the result fragment, IdCapture starts recognising documents.
         */
        viewModel.enableIdCaptureAndTurnOnCamera();
    }

    @Override
    public void onPause() {
        super.onPause();

        /*
         * Disable the IdCapture mode when pausing the fragment. This ensures that, because turning
         * the camera off is asynchronous and may take some time, no scan happens while the app is
         * pausing or stopping.
         *
         * Switch the camera off to stop streaming frames. The camera is stopped asynchronously.
         */
        viewModel.disableIdCaptureAndTurnOffCamera();
    }

    private void showCapturedResultFragment(ShowCaptureResultEvent event) {
        CaptureResult result = event.getContentIfNotHandled();

        if (result != null && requireActivity().getSupportFragmentManager().findFragmentByTag(CAPTURE_RESULT_TAG) == null) {
            /*
             * Show the result fragment only if we are not displaying one at the moment.
             */
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.scan_fragment_container, ResultFragment.create(result), CAPTURE_RESULT_TAG)
                    .addToBackStack(CAPTURE_RESULT_TAG)
                    .commit();
        }
    }

    private void showCapturedResultText(ShowCaptureResultToastEvent event) {
        CaptureResult result = event.getContentIfNotHandled();


        if (result != null) {
            /*
             * Stop the previous timer.
             */
            cancelCurrentResultTimer();

            /*
             * Update the result.
             */
            resultView.setText(
                    getString(
                            R.string.captured_result_parametrised,
                            result.getFullname(),
                            result.getDateOfBirth()
                    )
            );
            /*
             * Start over the disappearance countdown.
             */
            resultViewTimer = new ShowHideViewTimer(resultView);
            resultViewTimer.start();
        }
    }

    private void showScanBackAlert(ShowBackScanAvailableEvent event) {
        CaptureResult result = event.getContentIfNotHandled();
        if (result == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.alert_scan_back_title)
                .setMessage(R.string.alert_scan_back_message)
                .setPositiveButton(R.string.scan, (dialog, which) -> {
                    /*
                     * If we continue with scanning the back of the document, the IdCapture settings
                     * will automatically allow only for this to be scanned, blocking you from
                     * scanning other front of IDs. The next `onIdCaptured` will contain data from
                     * both the front and the back scans.
                     */
                    viewModel.enableIdCapture();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.skip, (dialog, which) -> {
                    dialog.dismiss();
                    /*
                     * If skipping scanning the back of the document, `IdCapture().reset()` needs
                     * to ba called to allow for another front IDs to be scanned.
                     */
                    viewModel.showCapturedResultInSelectedMode(result);
                    viewModel.resetIdCapture();
                    viewModel.enableIdCapture();
                })
                .setCancelable(false)
                .show();
    }

    private void cancelCurrentResultTimer() {
        if (resultViewTimer != null) {
            resultViewTimer.cancel();
            resultViewTimer = null;
        }
    }

    private void showSettingsFragment() {
        if (requireActivity().getSupportFragmentManager().findFragmentByTag(SETTINGS_TAG) == null) {
            /*
             * Show the settings fragment only if we are not displaying one at the moment.
             */
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.scan_fragment_container, OverviewSettingsFragment.create(), SETTINGS_TAG)
                    .addToBackStack(SETTINGS_TAG)
                    .commit();
        }
    }
}

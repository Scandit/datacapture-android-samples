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

package com.scandit.datacapture.reorderfromcatalogsample.ui.scan;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.scandit.datacapture.barcode.selection.ui.overlay.BarcodeSelectionBasicOverlay;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.reorderfromcatalogsample.R;
import com.scandit.datacapture.reorderfromcatalogsample.di.Injector;
import com.scandit.datacapture.reorderfromcatalogsample.ui.MainActivity;

public class ScanFragment extends Fragment {

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
        View root = inflater.inflate(R.layout.scanner_fragment, container, false);

        initToolbar(root);
        attachDataCaptureView(root);
        attachOverlay();

        return root;
    }

    private void initToolbar(View root) {
        Toolbar toolbar = root.findViewById(R.id.toolbar);

        /*
         * Add the listener necessary to interact with the actionBar entries.
         */
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_item_results) {
                ((MainActivity) requireActivity()).goToResults();
                return true;
            }
            return false;
        });
    }

    /*
     * Create a new DataCaptureView and fill the screen with it. DataCaptureView will show
     * the camera preview on the screen. Pass your DataCaptureContext to the view's
     * constructor.
     */
    private void attachDataCaptureView(View root) {
        SwipeRightInterceptingLayout dataCaptureViewContainer =
                root.findViewById(R.id.data_capture_view_container);

        dataCaptureView = DataCaptureView.newInstance(requireContext(), dataCaptureContext);

        /*
         * Attach a listener to the dataCaptureViewContainer to select all unselected codes when
         * swiping right.
         */
        dataCaptureViewContainer.setSwipeRightListener(() -> viewModel.selectCurrentCodes());

        /*
         * Insert the data capture view inside the container.
         */
        dataCaptureViewContainer.addView(
                dataCaptureView,
                new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        );
    }

    /**
     * Attach the BarcodeSelectionBasicOverlay to the DataCaptureView to aid the user in the capture
     * and selection process.
     */
    private void attachOverlay() {
        BarcodeSelectionBasicOverlay overlay =
                Injector.getInstance().getBarcodeSelectionRepository().getBarcodeCaptureOverlay();
        dataCaptureView.addOverlay(overlay);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        dataCaptureView = null;
    }

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }
}

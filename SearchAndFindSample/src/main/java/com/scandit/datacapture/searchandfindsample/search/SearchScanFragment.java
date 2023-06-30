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

package com.scandit.datacapture.searchandfindsample.search;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlayStyle;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.AimerViewfinder;
import com.scandit.datacapture.searchandfindsample.CameraPermissionFragment;
import com.scandit.datacapture.searchandfindsample.R;
import com.scandit.datacapture.searchandfindsample.find.FindScanFragment;

import org.jetbrains.annotations.NotNull;

public final class SearchScanFragment extends CameraPermissionFragment
        implements SearchScanViewModel.Listener {

    public static SearchScanFragment newInstance() {
        return new SearchScanFragment();
    }

    private SearchScanViewModel viewModel;

    private BottomSheetBehavior<View> behavior;
    private TextView textBarcode;
    private ImageButton buttonSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SearchScanViewModel.class);
    }

    @NotNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        CoordinatorLayout root =
                (CoordinatorLayout) inflater.inflate(R.layout.fragment_search, container, false);
        DataCaptureView dataCaptureView = createAndSetupDataCaptureView();

        // We put the dataCaptureView in its container.
        ViewGroup scannerContainer = root.findViewById(R.id.scanner_container);
        scannerContainer.addView(dataCaptureView,
                new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        View containerScannedCode = root.findViewById(R.id.container_scanned_code);
        behavior = BottomSheetBehavior.from(containerScannedCode);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        behavior.addBottomSheetCallback(sheetCallback);
        textBarcode = root.findViewById(R.id.text_barcode);
        buttonSearch = root.findViewById(R.id.button_search);

        return root;
    }

    private DataCaptureView createAndSetupDataCaptureView() {
        // To visualize the on-going barcode capturing process on screen,
        // setup a data capture view that renders the camera preview.
        // The view must be connected to the data capture context.
        DataCaptureView view = DataCaptureView.newInstance(
                requireContext(), viewModel.dataCaptureContext
        );

        // Add a barcode capture overlay to the data capture view to set a viewfinder UI.
        BarcodeCaptureOverlay overlay = BarcodeCaptureOverlay.newInstance(
                viewModel.barcodeCapture,
                view,
                BarcodeCaptureOverlayStyle.FRAME
        );
        view.addOverlay(overlay);

        // We add the aim viewfinder to the overlay.
        overlay.setViewfinder(new AimerViewfinder());

        // Adjust the overlay's barcode highlighting to display a light green rectangle.
        Brush brush = new Brush(Color.parseColor("#8028D380"), Color.TRANSPARENT, 0f);
        overlay.setBrush(brush);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.onSearchClicked();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        resumeFrameSource();
    }


    @Override
    public void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    @Override
    public void onCodeScanned(String code) {
        showResult(code);
    }


    @Override
    public void goToFind(Barcode barcodeToFind) {
        hideResult();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, FindScanFragment.newInstance(barcodeToFind))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    private void resumeFrameSource() {
        viewModel.setListener(this);

        // Resume scanning by enabling and re-adding the barcode capture mode.
        // Since barcode tracking and barcode capture modes are not compatible (meaning they cannot
        // be added at the same time to the same data capture context) we're also removing and
        // adding them back whenever the scanning is paused/resumed.
        viewModel.resumeScanning();
    }

    private void pauseFrameSource() {
        viewModel.setListener(null);

        // Pause scanning by disabling the barcode capture mode.
        // Since barcode tracking and barcode capture modes are not compatible (meaning they cannot
        // be added at the same time to the same data capture context) we're also removing and
        // adding them back whenever the scanning is paused/resumed.
        viewModel.pauseScanning();
    }

    private void showResult(String code) {
        textBarcode.setText(code);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void hideResult() {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private final BottomSheetBehavior.BottomSheetCallback sheetCallback =
            new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        buttonSearch.setClickable(true);
                    } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        buttonSearch.setClickable(false);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // Nothing to do.
                }
            };
}

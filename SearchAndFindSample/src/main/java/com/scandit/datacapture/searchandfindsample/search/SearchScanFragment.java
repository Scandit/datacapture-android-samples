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

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinder;
import com.scandit.datacapture.searchandfindsample.CameraPermissionFragment;
import com.scandit.datacapture.searchandfindsample.R;
import com.scandit.datacapture.searchandfindsample.find.FindScanFragment;
import org.jetbrains.annotations.NotNull;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public final class SearchScanFragment extends CameraPermissionFragment implements SearchScanViewModel.Listener {

    public static SearchScanFragment newInstance() {
        return new SearchScanFragment();
    }

    private SearchScanViewModel viewModel;

    private SpringAnimation cardTranslationYAnimation;

    private View containerScannedCode;
    private TextView textBarcode;
    private ImageButton buttonSearch, buttonBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SearchScanViewModel.class);
    }

    @NotNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        DataCaptureView dataCaptureView = createAndSetupDataCaptureView();

        // We put the dataCaptureView in its container.
        root.addView(dataCaptureView, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        containerScannedCode = root.findViewById(R.id.container_scanned_code);
        textBarcode = root.findViewById(R.id.text_barcode);
        buttonSearch = root.findViewById(R.id.button_search);
        buttonBack = root.findViewById(R.id.button_back);

        cardTranslationYAnimation = new SpringAnimation(
                containerScannedCode, SpringAnimation.TRANSLATION_Y
        );
        SpringForce spring = new SpringForce();
        spring.setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        cardTranslationYAnimation.setSpring(spring);

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
        BarcodeCaptureOverlay overlay =
                BarcodeCaptureOverlay.newInstance(viewModel.barcodeCapture, view);
        view.addOverlay(overlay);

        // We add the laser line viewfinder to the overlay.
        LaserlineViewfinder viewFinder = new LaserlineViewfinder();
        viewFinder.setWidth(new FloatWithUnit(0.9f, MeasureUnit.FRACTION));
        overlay.setViewfinder(viewFinder);

        // As we don't want to highlight any barcode, we disable the highlighting
        // by setting overlay's brush with the appropriate values.
        overlay.setBrush(new Brush(Color.TRANSPARENT, Color.TRANSPARENT, 0f));
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

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideResult();
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

    @Override
    public void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    @Override
    public void onCodeScanned(String code) {
        showResult();
        textBarcode.setText(code);
    }

    private void showResult() {
        cardTranslationYAnimation.animateToFinalPosition(0f);
        buttonSearch.setClickable(true);
        buttonBack.setVisibility(View.VISIBLE);
    }

    private void hideResult() {
        cardTranslationYAnimation.animateToFinalPosition(containerScannedCode.getHeight());
        buttonSearch.setClickable(false);
        buttonBack.setVisibility(View.GONE);
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
}

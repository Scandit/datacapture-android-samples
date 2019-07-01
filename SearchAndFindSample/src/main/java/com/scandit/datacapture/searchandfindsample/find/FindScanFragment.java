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

package com.scandit.datacapture.searchandfindsample.find;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlay;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.searchandfindsample.R;
import org.jetbrains.annotations.NotNull;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public final class FindScanFragment extends Fragment {

    private static final String FIELD_SYMBOLOGY = "symbology";
    private static final String FIELD_DATA = "data";

    public static FindScanFragment newInstance(Barcode barcode) {
        FindScanFragment fragment = new FindScanFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(FIELD_SYMBOLOGY, barcode.getSymbology());
        arguments.putString(FIELD_DATA, barcode.getData());
        fragment.setArguments(arguments);
        return fragment;
    }

    private FindScanViewModelFactory viewModelFactory;
    private FindScanViewModel viewModel;

    private BarcodeTrackingBasicOverlay overlay;

    private View searchingContainer;
    private ImageButton buttonBack;
    private ImageView imageDot;

    private SpringAnimation dotScaleXAnim;
    private SpringAnimation dotScaleYAnim;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (viewModelFactory == null && arguments != null) {
            Symbology symbology = (Symbology) arguments.getSerializable(FIELD_SYMBOLOGY);
            String data = arguments.getString(FIELD_DATA);
            viewModelFactory = new FindScanViewModelFactory(symbology, data);
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FindScanViewModel.class);
    }

    @NotNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_find, container, false);
        DataCaptureView dataCaptureView = createAndSetupDataCaptureView();

        // We put the dataCaptureView in its container.
        root.addView(dataCaptureView, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        searchingContainer = root.findViewById(R.id.container_finding);
        buttonBack = root.findViewById(R.id.button_back);
        imageDot = root.findViewById(R.id.image_scaling_dot);
        return root;
    }

    private DataCaptureView createAndSetupDataCaptureView() {
        // To visualize the on-going barcode capturing process on screen,
        // setup a data capture view that renders the camera preview.
        // The view must be connected to the data capture context.
        DataCaptureView view = DataCaptureView.newInstance(
                requireContext(), viewModel.dataCaptureContext
        );

        // Add a barcode tracking overlay to the data capture view to render the tracked
        // barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        overlay = BarcodeTrackingBasicOverlay.newInstance(viewModel.barcodeTracking, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animateSearchingCardUp();
        animatePulsatingDot();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    private void animateSearchingCardUp() {
        SpringAnimation cardTranslationYAnim = new SpringAnimation(
                searchingContainer, SpringAnimation.TRANSLATION_Y
        );
        SpringForce spring = new SpringForce(0f);
        spring.setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        cardTranslationYAnim.setSpring(spring);
        cardTranslationYAnim.start();
    }

    private void animatePulsatingDot() {

        SpringForce spring = new SpringForce();
        spring.setStiffness(SpringForce.STIFFNESS_VERY_LOW);
        spring.setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);

        // Animate scale X.
        dotScaleXAnim = new SpringAnimation(imageDot, SpringAnimation.SCALE_X);
        dotScaleXAnim.setSpring(spring);
        dotScaleXAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(
                    DynamicAnimation animation, boolean canceled, float value, float velocity
            ) {
                if (canceled) return;
                if (value == 1.0f) {
                    dotScaleXAnim.animateToFinalPosition(1.5f);
                } else {
                    dotScaleXAnim.animateToFinalPosition(1f);
                }
            }
        });
        dotScaleXAnim.animateToFinalPosition(1.5f);

        // Animate scale Y.
        dotScaleYAnim = new SpringAnimation(imageDot, SpringAnimation.SCALE_Y);
        dotScaleYAnim.setSpring(spring);
        dotScaleYAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(
                    DynamicAnimation animation, boolean canceled, float value, float velocity
            ) {
                if (canceled) return;
                if (value == 1.0f) {
                    dotScaleYAnim.animateToFinalPosition(1.5f);
                } else {
                    dotScaleYAnim.animateToFinalPosition(1f);
                }
            }
        });
        dotScaleYAnim.animateToFinalPosition(1.5f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dotScaleXAnim.cancel();
        dotScaleYAnim.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        overlay.setListener(viewModel);

        // Resume scanning by enabling and re-adding the barcode tracking mode.
        // Since barcode tracking and barcode capture modes are not compatible (meaning they cannot
        // be added at the same time to the same data capture context) we're also removing and
        // adding them back whenever the scanning is paused/resumed.
        viewModel.resumeScanning();
    }

    @Override
    public void onPause() {
        super.onPause();
        overlay.setListener(null);

        // Pause scanning by disabling the barcode tracking mode.
        // Since barcode tracking and barcode capture modes are not compatible (meaning they cannot
        // be added at the same time to the same data capture context) we're also removing and
        // adding them back whenever the scanning is paused/resumed.
        viewModel.pauseScanning();
    }
}

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

package com.scandit.datacapture.matrixscanbubblessample.scan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingAdvancedOverlay;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlay;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.matrixscanbubblessample.R;
import com.scandit.datacapture.matrixscanbubblessample.scan.bubble.Bubble;
import com.scandit.datacapture.matrixscanbubblessample.scan.bubble.BubbleSizeManager;
import com.scandit.datacapture.matrixscanbubblessample.scan.bubble.data.BubbleData;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ScanFragment extends Fragment implements ScanViewModel.ScanViewModelListener {

    private static final int PERMISSION_CODE_CAMERA = 100;

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    private ScanViewModel viewModel;
    private BubbleSizeManager bubbleSizeManager;

    private DataCaptureView dataCaptureView;
    private BarcodeTrackingAdvancedOverlay bubblesOverlay;
    private BarcodeTrackingBasicOverlay highlightOverlay;
    private ImageButton freezeButton;

    // We reuse bubble views where possible.
    private SparseArray<Bubble> bubbles;

    private boolean permissionDeniedOnce = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ScanViewModel.class);
        bubbleSizeManager = new BubbleSizeManager(requireContext());
        bubbles = new SparseArray<>();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View layout = inflater.inflate(R.layout.fragment_scan, container, false);

        // To visualize the on-going barcode capturing process on screen,
        // setup a data capture view that renders the camera preview.
        // The view must be connected to the data capture context.
        dataCaptureView =
                DataCaptureView.newInstance(requireContext(), viewModel.getDataCaptureContext());

        // We create an overlay to highlight the barcodes.
        highlightOverlay = BarcodeTrackingBasicOverlay.newInstance(
                viewModel.barcodeTracking, dataCaptureView
        );
        highlightOverlay.setDefaultBrush(viewModel.defaultBrush);

        // We create an overlay for the bubbles.
        bubblesOverlay = BarcodeTrackingAdvancedOverlay.newInstance(
                viewModel.barcodeTracking, dataCaptureView
        );
        bubblesOverlay.setListener(viewModel);

        // We add the data capture view to the root layout.
        ((ViewGroup) layout.findViewById(R.id.root)).addView(
                dataCaptureView,
                new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        );
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        freezeButton = view.findViewById(R.id.button_freeze);
        freezeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.toggleFreeze();
            }
        });
        onFrozenChanged(viewModel.isFrozen());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataCaptureView.removeOverlay(bubblesOverlay);
        bubblesOverlay.setListener(null);
        highlightOverlay.setListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasCameraPermissions()) {
            startScanning();
        } else if (!permissionDeniedOnce) {// We ask for permissions only once.
            requestCameraPermissions();
        }
    }

    private void startScanning() {
        viewModel.setListener(this);
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        viewModel.startFrameSource();
        viewModel.resumeScanning();
    }

    private boolean hasCameraPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissions() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        if (requestCode == PERMISSION_CODE_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanning();
        } else {
            permissionDeniedOnce = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
        viewModel.setListener(null);
        viewModel.pauseScanning();
        viewModel.stopFrameSource();
    }

    @Override
    public boolean shouldShowBubble(TrackedBarcode barcode) {
        return bubbleSizeManager.isBarcodeLargeEnoughForBubble(
                dataCaptureView.mapFrameQuadrilateralToView(barcode.getPredictedLocation())
        );
    }

    @Nullable
    @Override
    public View getOrCreateViewForBubbleData(
            TrackedBarcode barcode,
            BubbleData bubbleData,
            boolean visible
    ) {
        int identifier = barcode.getIdentifier();
        Bubble bubble = bubbles.get(identifier);
        if (bubble == null) {

            // There's no recyclable bubble for this tracking identifier, so we create one.
            bubble = new Bubble(requireContext(), bubbleData, barcode.getBarcode().getData());

            // We store the newly created bubble to recycle it in subsequent frames.
            bubbles.put(identifier, bubble);
        }
        return bubble.root;
    }

    @Override
    public void setBubbleVisibility(TrackedBarcode barcode, boolean visible) {
        Bubble bubble = bubbles.get(barcode.getIdentifier());
        if (bubble == null) return;

        if (visible) {
            bubble.show();
        } else {
            bubble.hide();
        }
    }

    @Override
    public void removeBubbleView(int identifier) {
        // When a barcode is not tracked anymore, we can remove the bubble from our list.
        bubbles.remove(identifier);
    }

    @Override
    public void onFrozenChanged(boolean frozen) {
        freezeButton.setImageResource(
                frozen ? R.drawable.freeze_disabled : R.drawable.freeze_enabled
        );
    }
}

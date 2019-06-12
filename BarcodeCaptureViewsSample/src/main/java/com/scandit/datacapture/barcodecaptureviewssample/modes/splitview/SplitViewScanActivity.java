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

package com.scandit.datacapture.barcodecaptureviewssample.modes.splitview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.barcodecaptureviewssample.R;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinder;

public class SplitViewScanActivity extends AppCompatActivity
        implements SplitViewScanViewModel.Listener {

    private static final int PERMISSION_CODE_CAMERA = 100;

    public static Intent getIntent(Context context) {
        return new Intent(context, SplitViewScanActivity.class);
    }

    private SplitViewScanViewModel viewModel;
    private boolean permissionDeniedOnce = false;
    private SplitViewAdapter adapter;

    private RecyclerView recyclerResults;
    private View tapToContinueLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SplitViewScanViewModel.class);
        setContentView(R.layout.activity_split_view);
        setupDataCaptureView();

        tapToContinueLabel = findViewById(R.id.tap_to_continue_label);
        tapToContinueLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapToContinueLabel.setVisibility(View.GONE);
                viewModel.resumeScanning();
            }
        });

        recyclerResults = findViewById(R.id.recycler_scan_results);
        recyclerResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerResults.setItemAnimator(new DefaultItemAnimator());
        recyclerResults.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        adapter = new SplitViewAdapter(viewModel.results);
        recyclerResults.setAdapter(adapter);

        Button buttonClear = findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.clearCurrentResults();
            }
        });
    }

    private void setupDataCaptureView() {

        // To visualize the on-going barcode capturing process on screen,
        // setup a data capture view that renders the camera preview.
        // The view must be connected to the data capture context.
        DataCaptureView view = DataCaptureView.newInstance(this, viewModel.dataCaptureContext);

        // Add a barcode capture overlay to the data capture view to render the tracked
        // barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        BarcodeCaptureOverlay overlay =
                BarcodeCaptureOverlay.newInstance(viewModel.barcodeCapture, view);
        view.addOverlay(overlay);

        // We have to add the laser line viewfinder to the overlay.
        LaserlineViewfinder viewFinder = new LaserlineViewfinder();
        viewFinder.setWidth(new FloatWithUnit(0.9f, MeasureUnit.FRACTION));
        overlay.setViewfinder(viewFinder);

        // As we don't want to highlight any barcode, we disable the highlighting
        // by setting overlay's brush with the appropriate values.
        overlay.setBrush(new Brush(Color.TRANSPARENT, Color.TRANSPARENT, 0f));

        // We put the dataCaptureView in its container.
        ((ViewGroup) findViewById(R.id.scanner_container)).addView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasCameraPermissions()) {
            startScanning();
        } else if (!permissionDeniedOnce) {// We ask for permissions only once.
            requestCameraPermissions();
        }
    }

    private void startScanning() {
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        viewModel.setListener(this);
        viewModel.startFrameSource();
        if (!viewModel.isInTimeoutState()) {
            viewModel.resumeScanning();
            tapToContinueLabel.setVisibility(View.GONE);
        } else {
            tapToContinueLabel.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasCameraPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE_CAMERA
        );
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
    public void onCodeAdded(Barcode barcode) {
        adapter.addResult(barcode);
    }

    @Override
    public void onResetCodes() {
        adapter.resetResults();
    }

    @Override
    public void onInactiveTimeout() {
        tapToContinueLabel.setVisibility(View.VISIBLE);
        viewModel.pauseScanning();
    }

    @Override
    public void clearTimeoutOverlay() {
        tapToContinueLabel.setVisibility(View.GONE);
    }
}

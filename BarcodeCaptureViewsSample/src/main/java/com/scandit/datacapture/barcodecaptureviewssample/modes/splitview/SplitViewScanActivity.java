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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlayStyle;
import com.scandit.datacapture.barcodecaptureviewssample.R;
import com.scandit.datacapture.barcodecaptureviewssample.modes.base.CameraPermissionActivity;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinderStyle;

public class SplitViewScanActivity extends CameraPermissionActivity
        implements SplitViewScanViewModel.Listener {

    public static Intent getIntent(Context context) {
        return new Intent(context, SplitViewScanActivity.class);
    }

    private SplitViewScanViewModel viewModel;
    private SplitViewAdapter adapter;

    private RecyclerView recyclerResults;
    private View tapToContinueLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SplitViewScanViewModel.class);
        setContentView(R.layout.activity_split_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        if (selectedItemId == R.id.action_clear) {
            viewModel.clearCurrentResults();
            return true;
        } else if (selectedItemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupDataCaptureView() {
        // To visualize the on-going barcode capturing process on screen,
        // setup a data capture view that renders the camera preview.
        // The view must be connected to the data capture context.
        DataCaptureView view = DataCaptureView.newInstance(this, viewModel.getDataCaptureContext());

        // Add a barcode capture overlay to the data capture view to render the tracked
        // barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        BarcodeCaptureOverlay overlay =
                BarcodeCaptureOverlay.newInstance(viewModel.getBarcodeCapture(), view, BarcodeCaptureOverlayStyle.FRAME);

        // We have to add the laser line viewfinder to the overlay.
        overlay.setViewfinder(new LaserlineViewfinder(LaserlineViewfinderStyle.ANIMATED));

        // Adjust the overlay's barcode highlighting to match the new viewfinder styles and improve
        // the visibility of feedback. With 6.10 we will introduce this visual treatment as a new
        // style for the overlay.
        Brush brush = new Brush(Color.TRANSPARENT, Color.WHITE, 3f);
        overlay.setBrush(brush);

        // We put the dataCaptureView in its container.
        ((ViewGroup) findViewById(R.id.scanner_container)).addView(view);
    }

    @Override
    protected void onResume() {
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

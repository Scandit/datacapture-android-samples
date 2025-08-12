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

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.spark.capture.SparkScanViewUiListener;
import com.scandit.datacapture.barcode.spark.ui.SparkScanCoordinatorLayout;
import com.scandit.datacapture.barcode.spark.ui.SparkScanScanningMode;
import com.scandit.datacapture.barcode.spark.ui.SparkScanView;
import com.scandit.datacapture.barcode.spark.ui.SparkScanViewSettings;
import com.scandit.datacapture.barcode.spark.ui.SparkScanViewState;
import com.scandit.datacapture.core.common.geometry.Point;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.searchandfindsample.CameraPermissionFragment;
import com.scandit.datacapture.searchandfindsample.R;
import com.scandit.datacapture.searchandfindsample.find.FindScanFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SearchScanFragment extends CameraPermissionFragment
    implements SearchScanViewModel.Listener, SparkScanViewUiListener {

    public static SearchScanFragment newInstance() {
        return new SearchScanFragment();
    }

    private static final float CROPPED_IMAGE_PADDING = 1.2f;

    private static final int SCALED_IMAGE_SIZE_IN_PIXELS = 100;

    private SearchScanViewModel viewModel;

    private final ResultListAdapter resultListAdapter = new ResultListAdapter();

    private RecyclerView resultRecycler;
    private Button clearButton;
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private TextView resultCountTextView;

    private SparkScanView sparkScanView;

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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultCountTextView = view.findViewById(R.id.item_count);

        clearButton = view.findViewById(R.id.clear_list);
        clearButton.setOnClickListener(v -> clearList());

        resultRecycler = view.findViewById(R.id.result_recycler);
        resultRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        DividerItemDecoration divider =
            new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        divider.setDrawable(requireContext().getDrawable(R.drawable.recycler_divider));
        resultRecycler.addItemDecoration(divider);

        resultRecycler.setAdapter(resultListAdapter);

        setItemCount(resultListAdapter.getItemCount());

        setupSparkScan(view);
    }

    private void setupSparkScan(View view) {
        // The SparkScanCoordinatorLayout container will make sure that the main layout of the view
        // will not break when the SparkScanView will be attached.
        // When creating the SparkScanView instance use the SparkScanCoordinatorLayout
        // as a parent view.
        SparkScanCoordinatorLayout container = view.findViewById(R.id.spark_scan_coordinator);

        // You can customize the SparkScanView using SparkScanViewSettings.
        SparkScanViewSettings settings = new SparkScanViewSettings();

        // Creating the instance of SparkScanView. The instance will be automatically added
        // to the container.
        sparkScanView =
            SparkScanView.newInstance(container, viewModel.dataCaptureContext, viewModel.sparkScan, settings);
        sparkScanView.setBarcodeFindButtonVisible(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();

        viewModel.setListener(this);
        sparkScanView.setListener(this);
        sparkScanView.onResume();
    }

    @Override
    public void onCameraPermissionGranted() {
    }

    @Override
    public void onPause() {
        viewModel.setListener(null);
        sparkScanView.setListener(null);
        sparkScanView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        backgroundExecutor.shutdown();
    }

    @Override
    public void onCodeScanned(Barcode barcode, FrameData data) {
        if (data != null) {
            data.retain();

            // For simplicity's sake, the generated images are kept in memory.
            // In a real-world scenario, these images would need to be stored in disk and loaded
            // into memory as needed, maybe via an external library, to prevent filling
            // all available memory.
            backgroundExecutor.execute(() -> {
                Bitmap image = cropBarcode(barcode, data.getImageBuffer().toBitmap());
                ScanResult result =
                    new ScanResult(barcode.getData(), barcode.getSymbology().name(), image);
                postResult(result);
                data.release();
            });
        }
    }

    @Override
    public void goToFind(List<Barcode> barcodesToFind) {
        requireActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, FindScanFragment.newInstance(barcodesToFind))
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commit();

        // Clear list when going to find.
        clearList();
    }

    private void clearList() {
        resultListAdapter.clearResults();
        setItemCount(0);
        viewModel.resetCodes();
    }

    private void postResult(ScanResult result) {
        requireActivity().runOnUiThread(() -> {
            resultListAdapter.addResult(result);
            setItemCount(resultListAdapter.getItemCount());
            resultRecycler.scrollToPosition(0);
        });
    }

    private Bitmap cropBarcode(Barcode barcode, Bitmap frame) {
        // safety check when input bitmap is too small
        if (frame.getWidth() == 1 && frame.getHeight() == 1) return frame;

        Point[] corners = {
            barcode.getLocation().getBottomLeft(),
            barcode.getLocation().getTopLeft(),
            barcode.getLocation().getTopRight(),
            barcode.getLocation().getBottomRight()
        };

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

        for (Point p : corners) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }

        float size = Math.max(maxY - minY, maxX - minX) * CROPPED_IMAGE_PADDING;
        int x = Math.max((int)((minX + maxX - size) / 2), 0);
        int y = Math.max((int)((minY + maxY - size) / 2), 0);
        int width = (int)size;
        int height = (int)size;

        if (x + width > frame.getWidth()) {
            width = frame.getWidth() - x;
            height = width;
        }
        if (y + height > frame.getHeight()) {
            height = frame.getHeight() - y;
            width = height;
        }

        if (width <= 0 || height <= 0) return frame;

        float scale = getScaleFactor(width, height, SCALED_IMAGE_SIZE_IN_PIXELS);
        Matrix matrix = new Matrix();
        matrix.postRotate(90f);
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(frame, x, y, width, height, matrix, true);
    }

    private float getScaleFactor(int bitmapWidth, int bitmapHeight, int targetDimension) {
        float smallestDimension = (float) Math.min(bitmapWidth, bitmapHeight);
        return targetDimension / smallestDimension;
    }

    private void setItemCount(int count) {
        resultCountTextView.setText(
            getResources().getQuantityString(R.plurals.results_amount, count, count));
        clearButton.setEnabled(count > 0);
    }

    @Override
    public void onBarcodeFindButtonTap(@NonNull SparkScanView view) {
        viewModel.onSearchClicked();
    }

    @Override
    public void onBarcodeCountButtonTap(@NonNull SparkScanView view) {
        // not relevant in this sample
    }

    @Override
    public void onLabelCaptureButtonTap(@NonNull SparkScanView view) {
        // not relevant in this sample
    }

    @Override
    public void onScanningModeChange(@NonNull SparkScanScanningMode newScanningMode) {
        // not relevant in this sample
    }

    @Override
    public void onViewStateChanged(@NonNull SparkScanViewState newState) {
        // not relevant in this sample
    }
}

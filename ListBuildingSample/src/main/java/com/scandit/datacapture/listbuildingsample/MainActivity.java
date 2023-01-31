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

package com.scandit.datacapture.listbuildingsample;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.barcode.capture.SymbologySettings;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.spark.capture.SparkScan;
import com.scandit.datacapture.barcode.spark.capture.SparkScanListener;
import com.scandit.datacapture.barcode.spark.capture.SparkScanSession;
import com.scandit.datacapture.barcode.spark.capture.SparkScanSettings;
import com.scandit.datacapture.barcode.spark.feedback.SparkScanViewFeedback;
import com.scandit.datacapture.barcode.spark.ui.SparkScanCoordinatorLayout;
import com.scandit.datacapture.barcode.spark.ui.SparkScanView;
import com.scandit.datacapture.barcode.spark.ui.SparkScanViewSettings;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.Point;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.time.TimeInterval;
import com.scandit.datacapture.listbuildingsample.data.ScanResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends CameraPermissionActivity implements SparkScanListener {

    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    public static final String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private static final float CROPPED_IMAGE_PADDING = 1.2f;

    private DataCaptureContext dataCaptureContext;

    private SparkScan sparkScan;

    private SparkScanView sparkScanView;

    private final ResultListAdapter resultListAdapter = new ResultListAdapter();

    private TextView resultCountTextView;

    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize spark scan and start the barcode recognition.
        initialize();

        //Setup RecyclerView for results
        RecyclerView recyclerView = findViewById(R.id.result_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider =
            new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        divider.setDrawable(getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(divider);

        recyclerView.setAdapter(resultListAdapter);

        resultCountTextView = findViewById(R.id.item_count);
        setItemCount(0);

        // Set up the button that clears the result list
        Button clearButton = findViewById(R.id.clear_list);
        clearButton.setOnClickListener(view -> {
            clearList();
        });

    }

    private void initialize() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        // Use the default camera with the recommended camera settings for the SparkScan mode
        // and set it as the frame source of the context.
        Camera camera = Camera.getDefaultCamera(SparkScan.createRecommendedCameraSettings());
        if (camera != null) {
            dataCaptureContext.setFrameSource(camera);
        } else {
            throw new IllegalStateException(
                "Sample depends on a camera, which failed to initialize.");
        }

        // The spark scan process is configured through SparkScan settings
        // which are then applied to the spark scan instance that manages the spark scan.
        SparkScanSettings sparkScanSettings = new SparkScanSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        HashSet<Symbology> symbologies = new HashSet<>();
        symbologies.add(Symbology.EAN13_UPCA);
        symbologies.add(Symbology.EAN8);
        symbologies.add(Symbology.UPCE);
        symbologies.add(Symbology.CODE39);
        symbologies.add(Symbology.CODE128);
        symbologies.add(Symbology.INTERLEAVED_TWO_OF_FIVE);
        sparkScanSettings.enableSymbologies(symbologies);

        // Some linear/1d barcode symbologies allow you to encode variable-length data.
        // By default, the Scandit Data Capture SDK only scans barcodes in a certain length range.
        // If your application requires scanning of one of these symbologies, and the length is
        // falling outside the default range, you may need to adjust the "active symbol counts"
        // for this symbology. This is shown in the following few lines of code for one of the
        // variable-length symbologies.
        SymbologySettings symbologySettings =
            sparkScanSettings.getSymbologySettings(Symbology.CODE39);

        HashSet<Short> activeSymbolCounts = new HashSet<>(
            Arrays.asList(new Short[]{7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}));

        symbologySettings.setActiveSymbolCounts(activeSymbolCounts);

        // Create spark scan and attach to data capture context.
        sparkScan = SparkScan.forDataCaptureContext(dataCaptureContext, sparkScanSettings);

        // Register self as a listener to get informed of tracked barcodes.
        sparkScan.addListener(this);

        // The SparkScanCoordinatorLayout container will make sure that the main layout of the view
        // will not break when the SparkScanView will be attached.
        // When creating the SparkScanView instance use the SparkScanCoordinatorLayout
        // as a parent view.
        SparkScanCoordinatorLayout container = findViewById(R.id.spark_scan_coordinator);

        // You can customize the SparkScanView using SparkScanViewSettings.
        SparkScanViewSettings settings = new SparkScanViewSettings();

        // Creating the instance of SparkScanView. The instance will be automatically added
        // to the container.
        sparkScanView =
            SparkScanView.newInstance(container, dataCaptureContext, sparkScan, settings);
    }

    private void clearList() {
        resultListAdapter.clearResults();
        setItemCount(0);
    }

    @Override
    protected void onPause() {
        sparkScanView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        sparkScan.removeListener(this);
        dataCaptureContext.removeMode(sparkScan);
        backgroundExecutor.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        requestCameraPermission();
    }

    private boolean isValidBarcode(Barcode barcode) {
        return barcode.getData() != null && !barcode.getData().equals("123456789");
    }

    private void validBarcodeScanned(Barcode barcode, FrameData data) {
        // Emit sound and vibration feedback
        sparkScanView.setFeedback(new SparkScanViewFeedback.Success());

        data.retain();

        backgroundExecutor.execute(() -> {
            Bitmap image = cropBarcode(barcode, data.getImageBuffer().toBitmap());
            ScanResult result =
                new ScanResult(barcode.getData(), barcode.getSymbology().name(), image);
            postResult(result);
            data.release();
        });

    }

    private void postResult(ScanResult result) {
        runOnUiThread(() -> {
            resultListAdapter.addResult(result);
            setItemCount(resultListAdapter.getItemCount());
        });
    }

    private Bitmap cropBarcode(Barcode barcode, Bitmap frame) {
        // safety check when input bitmap is too small
        if (frame.getWidth() == 1 && frame.getHeight() == 1) return frame;

        List<Point> points = Arrays.asList(
            barcode.getLocation().getBottomLeft(),
            barcode.getLocation().getTopLeft(),
            barcode.getLocation().getTopRight(),
            barcode.getLocation().getBottomRight()
        );

        float minX = points.get(0).getX();
        float minY = points.get(0).getY();
        float maxX = points.get(0).getX();
        float maxY = points.get(0).getY();

        for (Point point : points) {
            if (point.getX() < minX) {
                minX = point.getX();
            }

            if (point.getX() > maxX) {
                maxX = point.getX();
            }

            if (point.getY() < minY) {
                minY = point.getY();
            }

            if (point.getY() > maxY) {
                maxY = point.getY();
            }
        }

        PointF center = new PointF(((minX + maxX) * 0.5f), ((minY + maxY) * 0.5f));
        float largerSize = Math.max((maxY - minY), (maxX - minX));

        int height = (int) (largerSize * CROPPED_IMAGE_PADDING);
        int width = (int) (largerSize * CROPPED_IMAGE_PADDING);

        int x = (int) (center.x - largerSize * (CROPPED_IMAGE_PADDING / 2));
        int y = (int) (center.y - largerSize * (CROPPED_IMAGE_PADDING / 2));

        if ((y + height) > frame.getHeight()) { // safety check
            y -= ((y + height) - frame.getHeight());
        }

        if ((x + width) > frame.getWidth()) { // safety check
            x -= ((x + width) - frame.getWidth());
        }

        if (y <= 0 || x <= 0) {
            return frame;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(90f);

        return Bitmap.createBitmap(frame, x, y, width, height, matrix, true);
    }

    private void setItemCount(int count) {
        resultCountTextView.setText(
            getResources().getQuantityString(R.plurals.results_amount, count, count));
    }

    private void invalidBarcodeScanned() {
        // Emit sound and vibration feedback
        sparkScanView.setFeedback(
            new SparkScanViewFeedback.Error("This code should not have been scanned",
                TimeInterval.seconds(60f)));
    }

    @Override
    public void onBarcodeScanned(
        @NonNull SparkScan sparkScan, @NonNull SparkScanSession session, @NonNull FrameData data
    ) {
        List<Barcode> barcodes = session.getNewlyRecognizedBarcodes();
        Barcode barcode = barcodes.get(0);

        if (isValidBarcode(barcode)) {
            validBarcodeScanned(barcode, data);
        } else {
            invalidBarcodeScanned();
        }
    }

    @Override
    public void onSessionUpdated(
        @NonNull SparkScan sparkScan, @NonNull SparkScanSession session, @NonNull FrameData data
    ) {
        // Not relevant in this sample
    }

    @Override
    public void onObservationStarted(
        @NonNull SparkScan sparkScan
    ) {
        // Not relevant in this sample
    }

    @Override
    public void onObservationStopped(
        @NonNull SparkScan sparkScan
    ) {
        // Not relevant in this sample
    }
}

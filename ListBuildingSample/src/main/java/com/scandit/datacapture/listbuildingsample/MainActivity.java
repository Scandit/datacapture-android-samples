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
import androidx.annotation.Nullable;
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
import com.scandit.datacapture.barcode.spark.feedback.SparkScanBarcodeFeedback;
import com.scandit.datacapture.barcode.spark.feedback.SparkScanFeedbackDelegate;
import com.scandit.datacapture.barcode.spark.ui.SparkScanCoordinatorLayout;
import com.scandit.datacapture.barcode.spark.ui.SparkScanView;
import com.scandit.datacapture.barcode.spark.ui.SparkScanViewSettings;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.Point;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.time.TimeInterval;
import com.scandit.datacapture.listbuildingsample.data.ScanResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends CameraPermissionActivity implements SparkScanListener, SparkScanFeedbackDelegate {

    // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
    // `BuildConfig.SCANDIT_LICENSE_KEY`
    public static final String SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY;

    private static final float CROPPED_IMAGE_PADDING = 1.2f;

    private static final int SCALED_IMAGE_SIZE_IN_PIXELS = 100;

    private DataCaptureContext dataCaptureContext;

    private SparkScan sparkScan;

    private SparkScanView sparkScanView;

    private final ResultListAdapter resultListAdapter = new ResultListAdapter();

    private TextView resultCountTextView;

    private RecyclerView resultRecycler;
    private Button clearButton;

    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize spark scan and start the barcode recognition.
        initialize();

        // Set up the button that clears the result list
        clearButton = findViewById(R.id.clear_list);
        clearButton.setOnClickListener(view -> clearList());

        // Setup RecyclerView for results
        resultRecycler = findViewById(R.id.result_recycler);
        resultRecycler.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider =
            new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        divider.setDrawable(getDrawable(R.drawable.recycler_divider));
        resultRecycler.addItemDecoration(divider);

        resultRecycler.setAdapter(resultListAdapter);

        resultCountTextView = findViewById(R.id.item_count);
        setItemCount(0);
    }

    private void initialize() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

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

        // Create the spark scan instance.
        // Spark scan will automatically apply and maintain the optimal camera settings.
        sparkScan = new SparkScan(sparkScanSettings);

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

        // Set the feedback delegate
        sparkScanView.setFeedbackDelegate(this);
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
        sparkScanView.setFeedbackDelegate(null);
        backgroundExecutor.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sparkScanView.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        requestCameraPermission();
    }

    private boolean isValidBarcode(Barcode barcode) {
        return barcode.getData() != null && !barcode.getData().equals("123456789");
    }

    private void validBarcodeScanned(Barcode barcode, FrameData data) {
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

    private void postResult(ScanResult result) {
        runOnUiThread(() -> {
            resultListAdapter.addResult(result);
            setItemCount(resultListAdapter.getItemsScannedCount());
            resultRecycler.scrollToPosition(0);
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

        int x = Math.max((int) (center.x - largerSize * (CROPPED_IMAGE_PADDING / 2)), 0);
        int y = Math.max((int) (center.y - largerSize * (CROPPED_IMAGE_PADDING / 2)), 0);

        if ((y + height) > frame.getHeight()) {
            int difference = ((y + height) - frame.getHeight());

            if (y - difference < 0) {
                height -= difference;
                width -= difference;
            } else {
                y -= difference;
            }
        }

        if ((x + width) > frame.getWidth()) {
            int difference = ((x + width) - frame.getWidth());
            if (x - difference < 0) {
                width -= difference;
                height -= difference;
            } else {
                x -= difference;
            }
        }

        if (width <= 0 || height <= 0) { // safety check
            return frame;
        }

        float scaleFactor = getScaleFactor(width, height, SCALED_IMAGE_SIZE_IN_PIXELS);

        Matrix matrix = new Matrix();
        matrix.postRotate(90f);
        matrix.postScale(scaleFactor, scaleFactor);

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

    @Nullable
    @Override
    public SparkScanBarcodeFeedback getFeedbackForBarcode(@NonNull Barcode barcode) {
        if (isValidBarcode(barcode)) {
            return new SparkScanBarcodeFeedback.Success();
        } else {
            return new SparkScanBarcodeFeedback.Error(getString(R.string.wrong_barcode),
                TimeInterval.seconds(60f));
        }
    }

    @Override
    public void onBarcodeScanned(
        @NonNull SparkScan sparkScan, @NonNull SparkScanSession session, @Nullable FrameData data
    ) {
        Barcode barcode = session.getNewlyRecognizedBarcode();

        if (barcode != null && isValidBarcode(barcode)) {
            validBarcodeScanned(barcode, data);
        }
    }

    @Override
    public void onSessionUpdated(
        @NonNull SparkScan sparkScan, @NonNull SparkScanSession session, @Nullable FrameData data
    ) {
        // Not relevant in this sample
    }
}

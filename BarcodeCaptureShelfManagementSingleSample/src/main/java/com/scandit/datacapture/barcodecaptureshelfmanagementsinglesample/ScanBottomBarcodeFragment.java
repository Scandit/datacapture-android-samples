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

package com.scandit.datacapture.barcodecaptureshelfmanagementsinglesample;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.common.geometry.SizeWithUnit;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ScanBottomBarcodeFragment extends Fragment implements BarcodeCaptureListener {
    public static ScanBottomBarcodeFragment create() {
        return new ScanBottomBarcodeFragment();
    }

    private Callbacks callbacks;
    private DataCaptureView dataCaptureView;
    private long scanStartMs;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getHost() instanceof MenuFragment.Callbacks) {
            callbacks = (Callbacks) getHost();
        } else {
            throw new ClassCastException("Host doesn't implement Callbacks!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        DataCaptureContext dataCaptureContext =
                DataCaptureManager.getInstance().dataCaptureContext();
        BarcodeCapture barcodeCapture = DataCaptureManager.getInstance().barcodeCapture();

        dataCaptureView = DataCaptureView.newInstance(requireContext(), dataCaptureContext);
        dataCaptureView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        BarcodeCaptureOverlay overlay =
                BarcodeCaptureOverlay.newInstance(barcodeCapture, dataCaptureView);

        RectangularViewfinder viewfinder = new RectangularViewfinder();
        viewfinder.setSize(new SizeWithUnit(
                new FloatWithUnit(0.8f, MeasureUnit.FRACTION),
                new FloatWithUnit(0.2f, MeasureUnit.FRACTION)
        ));

        overlay.setViewfinder(viewfinder);

        return dataCaptureView;
    }

    @Override
    public void onResume() {
        super.onResume();

        scanStartMs = System.currentTimeMillis();

        DataCaptureManager.getInstance().zoom(2.5f);
        DataCaptureManager.getInstance().triggerAutoFocus();
        DataCaptureManager.getInstance().barcodeCapture().setEnabled(true);
        DataCaptureManager.getInstance().barcodeCapture().addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        DataCaptureManager.getInstance().zoom(1f);
        DataCaptureManager.getInstance().resetLensPosition();
        DataCaptureManager.getInstance().barcodeCapture().removeListener(this);
        DataCaptureManager.getInstance().barcodeCapture().setEnabled(false);
    }

    @Override
    public void onBarcodeScanned(
            @NotNull BarcodeCapture barcodeCapture,
            @NotNull BarcodeCaptureSession session,
            @NotNull FrameData data
    ) {
        if (!isResumed()) {
            return;
        }

        List<Barcode> barcodes = session.getNewlyRecognizedBarcodes();
        Barcode barcode = barcodes.get(0);
        final String barcodeData = barcode.getData();

        final long scanTimeMs = System.currentTimeMillis() - scanStartMs;

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callbacks.onBarcodeScanned(barcodeData, scanTimeMs);
            }
        });

    }

    @Override
    public void onSessionUpdated(
            @NotNull BarcodeCapture barcodeCapture,
            @NotNull BarcodeCaptureSession session,
            @NotNull FrameData data
    ) {
        // Not interested in this callback.
    }

    @Override
    public void onObservationStarted(@NotNull BarcodeCapture barcodeCapture) {
        // Not interested in this callback.
    }

    @Override
    public void onObservationStopped(@NotNull BarcodeCapture barcodeCapture) {
        // Not interested in this callback.
    }

    public interface Callbacks {
        void onBarcodeScanned(String data, long timeMs);
    }
}

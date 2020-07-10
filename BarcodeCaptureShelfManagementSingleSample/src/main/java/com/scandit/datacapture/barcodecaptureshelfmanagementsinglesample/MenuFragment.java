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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scandit.datacapture.barcodecaptureshelfmanagementsimplesample.R;

public class MenuFragment extends Fragment {
    private static final String KEY_LAST_BARCODE_DATA = "LAST_BARCODE_DATA";
    private static final String KEY_LAST_SCAN_TIME_MS = "LAST_SCAN_TIME_MS";

    public static MenuFragment create() {
        return new MenuFragment();
    }

    private String lastBarcodeData;
    private long lastScanTimeMs = -1;

    private View lastBarcodeLabel;
    private TextView lastBarcodeText;
    private View lastScanTimeLabel;
    private TextView lastScanTimeText;

    private Callbacks callbacks;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getHost() instanceof Callbacks) {
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
        View root = inflater.inflate(R.layout.menu_fragment, container, false);

        initLastScanInfo(root, savedInstanceState);
        initButtons(root);

        return root;
    }

    public void onLastScanInfoChanged(@Nullable String barcodeData, long scanTimeMs) {
        onLastBarcodeChanged(barcodeData);
        onLastScanTimeChanged(scanTimeMs);
    }

    private void onLastBarcodeChanged(@Nullable String barcodeData) {
        lastBarcodeData = barcodeData;

        if (lastBarcodeData != null) {
            lastBarcodeLabel.setVisibility(View.VISIBLE);
            lastBarcodeText.setVisibility(View.VISIBLE);

            lastBarcodeText.setText(lastBarcodeData);
        } else {
            lastBarcodeLabel.setVisibility(View.INVISIBLE);
            lastBarcodeText.setVisibility(View.INVISIBLE);
        }
    }

    private void onLastScanTimeChanged(long scanTimeMs) {
        lastScanTimeMs = scanTimeMs;

        if (lastScanTimeMs != -1) {
            lastScanTimeLabel.setVisibility(View.VISIBLE);
            lastScanTimeText.setVisibility(View.VISIBLE);

            lastScanTimeText.setText(getString(R.string.menu_last_scan_time_template, lastScanTimeMs));
        } else {
            lastScanTimeLabel.setVisibility(View.INVISIBLE);
            lastScanTimeText.setVisibility(View.INVISIBLE);
        }
    }

    private void initLastScanInfo(View root, @Nullable Bundle savedInstanceState) {
        lastBarcodeLabel = root.findViewById(R.id.last_barcode_label);
        lastBarcodeText = root.findViewById(R.id.last_barcode_text);
        lastScanTimeLabel = root.findViewById(R.id.last_scan_time_label);
        lastScanTimeText = root.findViewById(R.id.last_scan_time_text);

        if (savedInstanceState != null) {
            String barcodeData = savedInstanceState.getString(KEY_LAST_BARCODE_DATA);
            long scanTimeMs = savedInstanceState.getLong(KEY_LAST_SCAN_TIME_MS, -1);

            onLastScanInfoChanged(barcodeData, scanTimeMs);
        }
    }

    private void initButtons(View root) {
        root.findViewById(R.id.scan_top_barcode_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callbacks.onScanTopBarcodeClicked();
                    }
                });

        root.findViewById(R.id.scan_bottom_barcode_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callbacks.onScanBottomBarcodeClicked();
                    }
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_LAST_BARCODE_DATA, lastBarcodeData);
        outState.putLong(KEY_LAST_SCAN_TIME_MS, lastScanTimeMs);
    }

    @Override
    public void onResume() {
        super.onResume();

        onLastScanInfoChanged(lastBarcodeData, lastScanTimeMs);
    }

    public interface Callbacks {
        void onScanTopBarcodeClicked();

        void onScanBottomBarcodeClicked();
    }
}

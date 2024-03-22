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

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.find.capture.BarcodeFindItem;
import com.scandit.datacapture.barcode.find.ui.BarcodeFindView;
import com.scandit.datacapture.barcode.find.ui.BarcodeFindViewSettings;
import com.scandit.datacapture.barcode.find.ui.BarcodeFindViewUiListener;
import com.scandit.datacapture.searchandfindsample.R;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

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

    private BarcodeFindView barcodeFindView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (viewModelFactory == null && arguments != null) {
            Symbology symbology;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                symbology = arguments.getSerializable(FIELD_SYMBOLOGY, Symbology.class);
            } else {
                symbology = (Symbology) arguments.getSerializable(FIELD_SYMBOLOGY);
            }
            String data = arguments.getString(FIELD_DATA);
            viewModelFactory = new FindScanViewModelFactory(symbology, data);
        }
        viewModel = new ViewModelProvider(this, viewModelFactory).get(FindScanViewModel.class);
    }

    @NotNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_find, container, false);

        // The BarcodeFindView will automatically be added to the root view when created.
        barcodeFindView = BarcodeFindView.newInstance(
                root,
                viewModel.dataCaptureContext,
                viewModel.getBarcodeFind(),
                // With the BarcodeFindViewSettings, we can defined haptic and sound feedback,
                // as well as change the visual feedback for found barcodes.
                new BarcodeFindViewSettings()
        );

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tells the BarcodeFindView to start searching. When in searching is started,
        // the BarcodeFindView will start the camera and search as soon as everything is the view
        // is ready.
        barcodeFindView.startSearching();
        barcodeFindView.setListener(new BarcodeFindViewUiListener() {
            @Override
            public void onFinishButtonTapped(@NonNull Set<BarcodeFindItem> foundItems) {
                // Called from the UI thread, this method is called when the user presses the
                // finish button. It returns the list of all items that were found during
                // the session.
                requireActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resume finding by calling the BarcodeFindView onResume method.
        // Under the hood, it re-enables the BarcodeFind mode and makes sure the view is properly
        // setup.
        barcodeFindView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isRemoving()) {
            // If the user is leaving the screen, we'll be discarding the scanning mode,
            // so we perform some cleanup before that.
            viewModel.cleanup();
        }

        // Pause finding by calling the BarcodeFindView onPause method.
        // Under the hood, it will disable the mode and free resources that are not needed in a
        // paused state.
        barcodeFindView.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}

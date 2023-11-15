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

package com.scandit.datacapture.ageverifieddeliverysample.ui.timeout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.R;
import com.scandit.datacapture.ageverifieddeliverysample.ui.id.IdScanViewModel;

/**
 * A dialog fragment that informs the user that a given document is detected, but its data couldn't
 * be extracted so far.
 */
public class IdCaptureFirstTimeoutDialogFragment extends BottomSheetDialogFragment {

    /**
     * The tag used by the fragment that informs the user that the given ID or MRZ is
     * detected, but cannot be parsed.
     */
    public static final String TAG = "ID_CAPTURE_FIRST_TIMEOUT_DIALOG";

    /**
     * The ViewModel of the parent fragment.
     */
    private IdScanViewModel parentViewModel;

    /**
     * Create an instance of this fragment.
     */
    public static IdCaptureFirstTimeoutDialogFragment create() {
        return new IdCaptureFirstTimeoutDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Get a reference to this fragment's parent view model to pass the user's decision.
         */
        parentViewModel = new ViewModelProvider(requireParentFragment()).get(IdScanViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        /*
         * Initialize the layout of this fragment and find all the view it needs to interact with.
         */
        View root = inflater.inflate(R.layout.id_capture_first_timeout_bottom_sheet, container, false);

        Button okButton = root.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v -> retry());

        setCancelable(false);

        return root;
    }

    /**
     * Retry the capture process.
     */
    private void retry() {
        dismiss();
        parentViewModel.resumeCapture();
    }
}
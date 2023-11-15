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

package com.scandit.datacapture.ageverifieddeliverysample.ui.barcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.R;

/**
 * A dialog fragment that informs the user that the delivery requires an age verification
 * of the recipient.
 */
public class AgeVerificationRequiredDialogFragment extends BottomSheetDialogFragment {

    /**
     * The tag used by the fragment that indicates that the delivery requires an age verification
     * of the recipient.
     */
    public static final String TAG = "AGE_VERIFICATION_REQUIRED_DIALOG";

    /**
     * The ViewModel of the parent fragment.
     */
    private BarcodeScanViewModel parentViewModel;

    /**
     * Create an instance of this fragment.
     */
    public static AgeVerificationRequiredDialogFragment create() {
        return new AgeVerificationRequiredDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Get a reference to this fragment's parent view model to pass the user's decision.
         */
        parentViewModel = new ViewModelProvider(requireParentFragment()).get(BarcodeScanViewModel.class);
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
        View root = inflater.inflate(R.layout.age_verification_required_bottom_sheet, container, false);

        root.findViewById(R.id.scan_id_button).setOnClickListener(v -> captureIdDocument());

        setCancelable(false);

        return root;
    }

    /**
     * Proceed to scan the ID document.
     */
    private void captureIdDocument() {
        parentViewModel.onIdCaptureSelected();
        dismiss();
    }
}

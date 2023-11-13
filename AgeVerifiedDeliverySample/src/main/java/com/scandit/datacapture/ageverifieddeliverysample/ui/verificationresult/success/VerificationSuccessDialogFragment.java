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

package com.scandit.datacapture.ageverifieddeliverysample.ui.verificationresult.success;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.R;
import com.scandit.datacapture.ageverifieddeliverysample.ui.scan.ScanViewModel;

/**
 * The fragment to display the UI that informs about successful verification of
 * the recipient's document data.
 */
public class VerificationSuccessDialogFragment extends BottomSheetDialogFragment {
    /**
     * The ViewModel of the parent fragment.
     */
    private ScanViewModel parentViewModel;

    /**
     * Create a new instance of this fragment.
     */
    public static VerificationSuccessDialogFragment create() {
        return new VerificationSuccessDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Get a reference to this fragment's parent view model.
         */
        parentViewModel = new ViewModelProvider(requireParentFragment()).get(ScanViewModel.class);
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
        View root = inflater.inflate(R.layout.verification_success_bottom_sheet, container, false);

        root.findViewById(R.id.confirm_delivery_button).setOnClickListener(v -> confirmDelivery());

        setCancelable(false);

        return root;
    }

    /**
     * Proceed to confirm the delivery. The exact flow is beyond the scope of this sample, so we
     * just restart it.
     */
    private void confirmDelivery() {
        restart();
    }

    /**
     * Restart the whole capture flow.
     */
    private void restart() {
        dismiss();
        parentViewModel.resetIdCaptureState();
    }
}

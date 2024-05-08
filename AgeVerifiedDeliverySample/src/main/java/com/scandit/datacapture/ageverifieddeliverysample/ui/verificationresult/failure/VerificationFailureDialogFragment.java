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

package com.scandit.datacapture.ageverifieddeliverysample.ui.verificationresult.failure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.scandit.datacapture.ageverifieddeliverysample.R;
import com.scandit.datacapture.ageverifieddeliverysample.ui.id.IdScanViewModel;

/**
 * The fragment to display the UI that informs about failed verification of
 * the recipient's document data.
 */
public class VerificationFailureDialogFragment extends BottomSheetDialogFragment {

    /**
     * The tag used by the fragment that displays the result of the recipient's document data
     * verification.
     */
    public static final String TAG = "VERIFICATION_FAILURE_RESULT";

    /**
     * This fragment arguments' key for the failure reason.
     */
    private static final String KEY_FAILURE_REASON = "FAILURE_REASON";

    /**
     * The ViewModel of the parent fragment.
     */
    private IdScanViewModel parentViewModel;

    /**
     * Create an instance of this fragment with the provided failure reason.
     */
    public static VerificationFailureDialogFragment create(VerificationFailureReason reason) {
        VerificationFailureDialogFragment fragment = new VerificationFailureDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FAILURE_REASON, reason.name());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Get a reference to this fragment's parent view model.
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
        View root = inflater.inflate(R.layout.verification_failure_bottom_sheet, container, false);

        VerificationFailureReason reason =
                VerificationFailureReason.valueOf(getArguments().getString(KEY_FAILURE_REASON));

        TextView reasonText = root.findViewById(R.id.age_verification_failure_message);
        reasonText.setText(getFailureReasonText(reason));

        root.findViewById(R.id.refuse_button).setOnClickListener(v -> restart());
        root.findViewById(R.id.retry_button).setOnClickListener(v -> retry());

        setCancelable(false);

        return root;
    }

    /**
     * Get the message that informs the user about the reason of the verification failure.
     */
    @StringRes
    private int getFailureReasonText(VerificationFailureReason reason) {
        switch (reason) {
            case DOCUMENT_EXPIRED:
                return R.string.age_verification_failure_expired_message;
            case HOLDER_UNDERAGE:
                return R.string.age_verification_failure_underage_message;
            default:
                throw new AssertionError("Unknown failure reason: " + reason);
        }
    }

    /**
     * Restart the whole capture flow.
     */
    private void restart() {
        dismiss();
        parentViewModel.resetScanningFlow();
    }

    /**
     * Retry the capture process.
     */
    private void retry() {
        dismiss();
        parentViewModel.resumeCapture();
    }
}

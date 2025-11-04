package com.scandit.datacapture.usdlverificationsample.ui.result;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.scandit.datacapture.id.data.RejectionReason;
import com.scandit.datacapture.usdlverificationsample.R;
import com.scandit.datacapture.usdlverificationsample.ui.scan.ScanViewModel;

public class ResultBottomSheetFragment extends BottomSheetDialogFragment {
    private final static String KEY_CAPTURE_RESULT = "CAPTURE_RESULT";

    /**
     * The view model of this fragment.
     */
    private ResultViewModel viewModel;

    /**
     * The ViewModel of the parent fragment.
     */
    private ScanViewModel parentViewModel;

    private ResultListAdapter adapter;
    private ImageView userImage;
    private ImageView frontReviewImage;

    private LinearLayout verificationSuccessLayout;
    private LinearLayout verificationErrorLayout;

    /*
     * Store inside a bundle the result data and, if available, the byteArray extracted from the
     * Face image.
     */
    public static ResultBottomSheetFragment create(CaptureResult result) {
        ResultBottomSheetFragment fragment = new ResultBottomSheetFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_CAPTURE_RESULT, result);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * Get a reference to this fragment's view model.
         */
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        /*
         * Get a reference to this fragment's parent view model to pass the user's decision.
         */
        parentViewModel = new ViewModelProvider(requireParentFragment()).get(ScanViewModel.class);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundedCornersBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.result_screen, container, false);

        // Retrieve the results map from the intent's bundle.
        viewModel.onResult(getArguments().getParcelable(KEY_CAPTURE_RESULT));

        RecyclerView recyclerResult = root.findViewById(R.id.scanning_results_recycler_view);
        userImage = root.findViewById(R.id.user_image);
        frontReviewImage = root.findViewById(R.id.front_review_image);
        verificationSuccessLayout = root.findViewById(R.id.verification_success_layout);
        verificationErrorLayout = root.findViewById(R.id.verification_error_layout);
        ImageView closeButton = root.findViewById(R.id.close_button);

        closeButton.setOnClickListener(view -> dismiss());

        /*
         * Set up the result list. Check the ResultListAdapter class for more info on displaying
         * the result items.
         */
        adapter = new ResultListAdapter();
        recyclerResult.setAdapter(adapter);
        recyclerResult.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        );

        return root;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog =
                (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        return bottomSheetDialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        /*
         * Observe the sequence of desired UI states in order to update the UI.
         */
        viewModel.uiStates().observe(lifecycleOwner, this::onNewUiState);
    }

    /**
     * Update the displayed UI.
     */
    private void onNewUiState(ResultUiState uiState) {
        CaptureResult result = uiState.getCaptureResult();

        adapter.submitList(result.getEntries());

        renderFaceImage(result);
        renderVerificationResult(result);
        renderFrontReviewImageResult(result);
    }

    /**
     * Display the face image extracted from the document.
     */
    private void renderFaceImage(CaptureResult result) {
        if (result.getFaceImageBytes() != null) {
            userImage.setImageBitmap(convertBytesToImage(result.getFaceImageBytes()));
        } else {
            userImage.setVisibility(View.GONE);
        }
    }

    /**
     * Display the front review image extracted from the document.
     */
    private void renderFrontReviewImageResult(CaptureResult result) {
        if (result.getFrontReviewImageImageBytes() != null) {
            frontReviewImage.setImageBitmap(convertBytesToImage(result.getFrontReviewImageImageBytes()));
        } else {
            frontReviewImage.setVisibility(View.GONE);
        }
    }

    /**
     * Display verification results based on rejection reason:
     * - If rejection reason exists → show single red tile for that specific failure
     * - If no rejection reason → show 3 green tiles for all verification checks
     */
    private void renderVerificationResult(CaptureResult result) {
        int successIcon = R.drawable.ic_success;
        int errorIcon = R.drawable.ic_error;

        verificationSuccessLayout.removeAllViews();
        verificationErrorLayout.removeAllViews();

        RejectionReason rejectionReason = result.getRejectionReason();

        if (rejectionReason != null) {
            // Show single red tile for the specific rejection reason
            VerificationResultItemView errorView = new VerificationResultItemView(getContext());
            switch (rejectionReason) {
                case DOCUMENT_EXPIRED:
                    errorView.setData(errorIcon, R.string.scanning_dl_expired);
                    break;
                case INCONSISTENT_DATA:
                    errorView.setData(errorIcon, R.string.scanning_dl_front_data_consistency_failure);
                    break;
                case FORGED_AAMVA_BARCODE:
                    errorView.setData(errorIcon, R.string.scanning_dl_barcode_verification_failure);
                    break;
            }
            verificationErrorLayout.addView(errorView);
        } else {
            // Show all 3 green tiles for successful verification
            VerificationResultItemView expirationView = new VerificationResultItemView(getContext());
            expirationView.setData(successIcon, R.string.scanning_dl_not_expired);
            verificationSuccessLayout.addView(expirationView);

            VerificationResultItemView dataConsistencyView = new VerificationResultItemView(getContext());
            dataConsistencyView.setData(successIcon, R.string.scanning_dl_data_consistency_success);
            verificationSuccessLayout.addView(dataConsistencyView);

            VerificationResultItemView barcodeVerificationView = new VerificationResultItemView(getContext());
            barcodeVerificationView.setData(successIcon, R.string.scanning_dl_barcode_verification_success);
            verificationSuccessLayout.addView(barcodeVerificationView);
        }

        verificationSuccessLayout.setVisibility(
                verificationSuccessLayout.getChildCount() > 0 ? View.VISIBLE : View.GONE);

        verificationErrorLayout.setVisibility(
                verificationErrorLayout.getChildCount() > 0 ? View.VISIBLE : View.GONE);
    }

    private Bitmap convertBytesToImage(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        parentViewModel.startIdCapture();
    }
}

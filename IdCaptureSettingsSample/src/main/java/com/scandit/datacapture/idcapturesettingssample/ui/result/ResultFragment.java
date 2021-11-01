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

package com.scandit.datacapture.idcapturesettingssample.ui.result;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.idcapturesettingssample.R;

public class ResultFragment extends Fragment {
    private final static String KEY_CAPTURE_RESULT = "CAPTURE_RESULT";

    /**
     * The view model of this fragment.
     */
    private ResultViewModel viewModel;

    private ResultListAdapter adapter;

    private ImageView faceImage;
    private ImageView idFrontImage;
    private ImageView idBackImage;

    /*
     * Store inside a bundle the result data and, if available, the byteArray extracted from the
     * Face image.
     */
    public static ResultFragment create(CaptureResult result) {
        ResultFragment fragment = new ResultFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        setupActionBar();
        /*
         * Initialize the layout of this fragment and find all the view it needs to interact with.
         */
        View root = inflater.inflate(R.layout.fragment_result, container, false);

        RecyclerView recyclerResult = root.findViewById(R.id.results_list);
        faceImage = root.findViewById(R.id.image_face);
        idFrontImage = root.findViewById(R.id.image_id_front);
        idBackImage = root.findViewById(R.id.image_id_back);

        // Retrieve the results map from the intent's bundle.
        viewModel.onResult(getArguments().getParcelable(KEY_CAPTURE_RESULT));

        /*
         * Setup the results list. Check the ResultListAdapter class for more info on displaying
         * the result items.
         */
        adapter = new ResultListAdapter();

        recyclerResult.addItemDecoration(
            new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        );
        recyclerResult.setAdapter(adapter);

        return root;
    }

    private void setupActionBar() {
        /*
         * Make sure the back arrow is shown and the correct title for the fragment is displayed.
         */
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.captured_id_title);
    }

    private Bitmap convertBytesToImage(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
        adapter.submitList(uiState.getCaptureResult().getEntries());

        if (uiState.getCaptureResult().getFaceImageBytes() != null) {
            faceImage.setImageBitmap(convertBytesToImage(uiState.getCaptureResult().getFaceImageBytes()));
        } else {
            faceImage.setVisibility(View.GONE);
        }

        if (uiState.getCaptureResult().getIdFrontImageBytes() != null) {
            idFrontImage.setImageBitmap(convertBytesToImage(uiState.getCaptureResult().getIdFrontImageBytes()));
        } else {
            idFrontImage.setVisibility(View.GONE);
        }

        if (uiState.getCaptureResult().getIdBackImageBytes() != null) {
            idBackImage.setImageBitmap(convertBytesToImage(uiState.getCaptureResult().getIdBackImageBytes()));
        } else {
            idBackImage.setVisibility(View.GONE);
        }
    }
}

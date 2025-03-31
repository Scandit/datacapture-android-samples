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

package com.scandit.datacapture.idcaptureextendedsample.ui.result;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.idcaptureextendedsample.R;

import java.util.ArrayList;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class ResultFragment extends Fragment {
    private final static String KEY_CAPTURE_RESULT = "CAPTURE_RESULT";

    /**
     * The view model of this fragment.
     */
    private ResultViewModel viewModel;

    /**
     * The state representing the currently displayed UI.
     */
    private ResultUiState uiState;

    /*
     * The image of the front side of the captured document, if relevant.
     */
    private ImageView idFrontImage;

    /*
     * The image of the back side of the captured document, if relevant.
     */
    private ImageView idBackImage;

    /*
     * The data extracted from the captured document.
     */
    private ResultListAdapter resultAdapter;

    /*
     * Store inside a bundle the result data and, if available, the byteArray extracted from the
     * image of the front and the back sides of the document.
     */
    public static ResultFragment create(CaptureResult result) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_CAPTURE_RESULT, result);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        /*
         * Initialize the layout of this fragment and find all the view it needs to interact with.
         */
        View root = inflater.inflate(R.layout.result_screen, container, false);
        initToolbar(root);

        idFrontImage = root.findViewById(R.id.image_id_front);
        idBackImage = root.findViewById(R.id.image_id_back);

        resultAdapter = new ResultListAdapter(new ArrayList<>());

        RecyclerView recyclerResult = root.findViewById(R.id.results_list);
        recyclerResult.addItemDecoration(new DividerItemDecoration(requireContext(), VERTICAL));
        recyclerResult.setAdapter(resultAdapter);

        // Retrieve the results map from the intent's bundle.
        viewModel.onResult(getArguments().getParcelable(KEY_CAPTURE_RESULT));

        return root;
    }

    private void initToolbar(View root) {
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(R.string.app_name);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        this.uiState = uiState;

        updateImage(uiState.getIdFrontImage(), idFrontImage);
        updateImage(uiState.getIdBackImage(), idBackImage);

        resultAdapter.submitList(uiState.getData());
    }

    private void updateImage(@Nullable Bitmap image, ImageView target) {
        if (image != null) {
            target.setVisibility(View.VISIBLE);
            target.setImageBitmap(image);
        } else {
            target.setVisibility(View.GONE);
        }
    }
}

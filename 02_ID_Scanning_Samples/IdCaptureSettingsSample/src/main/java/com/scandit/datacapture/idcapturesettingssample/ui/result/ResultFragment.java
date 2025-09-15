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

import com.scandit.datacapture.id.data.IdImages;
import com.scandit.datacapture.id.data.IdSide;
import com.scandit.datacapture.idcapturesettingssample.R;

public class ResultFragment extends Fragment {
    /**
     * The result ViewModel shared with the ScanFragment.
     */
    private ResultViewModel viewModel;

    private ResultListAdapter adapter;
    private ImageView faceImageView;
    private ImageView idFrontImageView;
    private ImageView idBackImageView;
    private ImageView frameFrontImageView;
    private ImageView frameBackImageView;

    public static ResultFragment create() {
        return new ResultFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);
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
        faceImageView = root.findViewById(R.id.image_face);
        idFrontImageView = root.findViewById(R.id.image_id_front);
        idBackImageView = root.findViewById(R.id.image_id_back);
        frameFrontImageView = root.findViewById(R.id.image_frame_front);
        frameBackImageView = root.findViewById(R.id.image_frame_back);

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
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.captured_id_title);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        /*
         * Observe the sequence of capture results in order to update the UI.
         */
        viewModel.getCaptureResultStream().observe(lifecycleOwner, this::displayResult);
    }

    /**
     * Update the displayed UI.
     */
    private void displayResult(CaptureResult result) {
        adapter.submitList(result.getEntries());

        IdImages images = result.getImages();

        Bitmap faceImage = images.getFace();
        if (faceImage != null) {
            faceImageView.setImageBitmap(faceImage);
        } else {
            faceImageView.setVisibility(View.GONE);
        }

        Bitmap idFrontImage = images.getCroppedDocument(IdSide.FRONT);
        if (idFrontImage != null) {
            idFrontImageView.setImageBitmap(idFrontImage);
        } else {
            idFrontImageView.setVisibility(View.GONE);
        }

        Bitmap idBackImage = images.getCroppedDocument(IdSide.BACK);
        if (idBackImage != null) {
            idBackImageView.setImageBitmap(idBackImage);
        } else {
            idBackImageView.setVisibility(View.GONE);
        }

        Bitmap frameFrontImage = images.getFrame(IdSide.FRONT);
        if (frameFrontImage != null) {
            frameFrontImageView.setImageBitmap(frameFrontImage);
        } else {
            frameFrontImageView.setVisibility(View.GONE);
        }

        Bitmap frameBackImage = images.getFrame(IdSide.BACK);
        if (frameBackImage != null) {
            frameBackImageView.setImageBitmap(frameBackImage);
        } else {
            frameBackImageView.setVisibility(View.GONE);
        }
    }
}

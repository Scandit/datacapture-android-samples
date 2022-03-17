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

package com.scandit.datacapture.reorderfromcatalogsample.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.scandit.datacapture.reorderfromcatalogsample.R;

public class ResultsFragment extends BottomSheetDialogFragment {

    /**
     * The view model of this fragment.
     */
    private ResultsViewModel viewModel;

    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Get a reference to this fragment's view model.
         */
        viewModel = new ViewModelProvider(this).get(ResultsViewModel.class);
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
        View root = inflater.inflate(R.layout.results_fragment, container, false);

        initToolbar(root);
        initTextTotal(root);
        initResultsList(root);
        initActionButtons(root);

        return root;
    }

    private void initToolbar(View root) {
        Toolbar toolbar = root.findViewById(R.id.toolbar);

        /*
         * Add the listener necessary to interact with the actionBar entries.
         */
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_item_close) {
                requireActivity().onBackPressed();
                return true;
            }
            return false;
        });
    }

    private void initTextTotal(View root) {
        TextView textTotal = root.findViewById(R.id.text_results_count);

        /*
         * Display the total count of selected barcodes.
         */
        int resultsCount = viewModel.getSelectedBarcodes().size();
        textTotal.setText(
                getString(R.string.total_items_parametrised, resultsCount)
        );
    }

    private void initResultsList(View root) {
        RecyclerView recyclerResults = root.findViewById(R.id.recycler_results);
        recyclerResults.addItemDecoration(
                new DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)
        );

        /*
         * Add the results adapter to the recycler view to display results.
         */
        recyclerResults.setAdapter(
                new ResultsItemsAdapter(viewModel.getSelectedBarcodes())
        );
    }

    private void initActionButtons(View root) {
        Button resumeScanningButton = root.findViewById(R.id.button_resume);

        /*
         * Close results on resume scanning.
         */
        resumeScanningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        Button clearListButton = root.findViewById(R.id.button_clear);

        /*
         * Clear the list and reset barcode selection state.
         */
        clearListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.clearResults();
                requireActivity().onBackPressed();
            }
        });
    }
}

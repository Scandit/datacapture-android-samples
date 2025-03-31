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

package com.scandit.datacapture.restockingsample.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.restockingsample.MainActivity;
import com.scandit.datacapture.restockingsample.R;
import com.scandit.datacapture.restockingsample.pick.PickFragment;
import com.scandit.datacapture.restockingsample.products.ProductManager;

/**
 * Fragment responsible for displaying the results of the picking, listing all products that still
 * need to be picked.
 */
public class ProductListFragment extends Fragment {
    public static ProductListFragment newInstance() {
        return new ProductListFragment();
    }

    public static String TAG = "ProductListFragment";

    private RecyclerView resultList;

    private final ProductManager productManager = ProductManager.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.result_list);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        resultList = view.findViewById(R.id.result_list);

        Button scanButton = view.findViewById(R.id.continueScanningButton);
        scanButton.setOnClickListener(v -> continueScanning());

        Button finishButton = view.findViewById(R.id.finishScanningButton);
        finishButton.setOnClickListener(v -> finishPickup());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        resultList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        resultList.setAdapter(new ProductListAdapter(productManager.getPickResultContent()));
        resultList.addItemDecoration(new SeparatorDecorator(view.getContext()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void continueScanning() {
        requireActivity().onBackPressed();
    }

    private void finishPickup() {
        // Clear all the products that where scanned and picked so we can start again.
        productManager.clearPickedAndScanned();
        FragmentManager manager = getParentFragmentManager();
        manager.popBackStack(PickFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PickFragment pickFragment = PickFragment.newInstance();
        manager.beginTransaction()
            .replace(R.id.container, pickFragment, PickFragment.TAG)
            .commit();
    }
}

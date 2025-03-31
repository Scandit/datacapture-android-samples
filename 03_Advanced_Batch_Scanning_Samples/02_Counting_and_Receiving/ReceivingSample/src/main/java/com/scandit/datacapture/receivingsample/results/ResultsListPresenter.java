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

package com.scandit.datacapture.receivingsample.results;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.receivingsample.R;
import com.scandit.datacapture.receivingsample.data.ScanResult;
import com.scandit.datacapture.receivingsample.managers.BarcodeManager;

import java.util.List;
import java.util.Objects;

public class ResultsListPresenter {

    private final TextView resultCountTextView;

    private final ResultListAdapter resultListAdapter = new ResultListAdapter();

    private final Context context;

    private ResultsListPresenterListener listener;

    public interface ResultsListPresenterListener {
        void startNewButtonClicked();

        void resumeButtonClicked();

        void clearButtonClicked();
    }

    public void setListener(ResultsListPresenterListener listener) {
        this.listener = listener;
    }

    public ResultsListPresenter(
        Context context,
        ViewGroup container,
        ExtraButtonStyle extraButton,
        boolean canSwipeToDelete
    ) {
        this.context = context;

        //Setup RecyclerView for results
        View resultsList = LayoutInflater.from(context)
            .inflate(R.layout.view_results_list, container, false);
        container.addView(resultsList);

        RecyclerView recyclerView = resultsList.findViewById(R.id.result_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration divider =
            new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(
            AppCompatResources.getDrawable(context, R.drawable.recycler_divider)));
        recyclerView.addItemDecoration(divider);

        recyclerView.setAdapter(resultListAdapter);

        resultCountTextView = resultsList.findViewById(R.id.item_count);
        setItemCount(0);

        // Set up the button that clears the result list.
        Button clearButton = resultsList.findViewById(R.id.clear_list);
        clearButton.setOnClickListener(view -> {
            BarcodeManager.getInstance().reset();
            clearList();
            if (listener != null) {
                listener.clearButtonClicked();
            }
        });

        // Set up the button that resumes the current scanning session.
        Button resumeButton = resultsList.findViewById(R.id.resume_scan);
        resumeButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.resumeButtonClicked();
            }
        });
        resumeButton.setVisibility(
            extraButton == ExtraButtonStyle.RESUME ? View.VISIBLE : View.GONE
        );

        // Set up the button that starts a new scanning session.
        Button newScanButton = resultsList.findViewById(R.id.start_new_scan);
        newScanButton.setOnClickListener(view -> {
            clearList();
            if (listener != null) {
                listener.startNewButtonClicked();
            }
        });
        newScanButton.setVisibility(
            extraButton == ExtraButtonStyle.NEW_SCAN ? View.VISIBLE : View.GONE
        );

        // Setup the swipe-to-delete.
        if (canSwipeToDelete) {
            initializeSwipeCallback(recyclerView);
        }
    }

    private void initializeSwipeCallback(RecyclerView recyclerView) {
        ItemTouchHelper swipeHelper =
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(
                    @NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    @NonNull RecyclerView.ViewHolder target
                ) {
                    return false;
                }

                @Override
                public void onSwiped(
                    @NonNull RecyclerView.ViewHolder viewHolder, int direction
                ) {
                    int position = viewHolder.getAdapterPosition();
                    String data = resultListAdapter.getDataAtIndex(position);
                    BarcodeManager.getInstance().removeBarcodesWithData(data);
                    resultListAdapter.removeResultAtIndex(position);
                    updateListItemCount();
                }
            });
        swipeHelper.attachToRecyclerView(recyclerView);
    }

    // Clear the whole list.
    private void clearList() {
        resultListAdapter.clearResults();
        setItemCount(0);
    }

    // Refresh the item count.
    private void setItemCount(int count) {
        resultCountTextView.setText(
            context.getResources().getQuantityString(R.plurals.results_amount, count, count)
        );
    }

    // Add a single barcode to the list.
    public void addToList(Barcode barcode) {
        ScanResult result = new ScanResult(barcode.getData(), barcode.getSymbology().name());
        resultListAdapter.addResult(result);
        updateListItemCount();
    }

    private void updateListItemCount() {
        setItemCount(resultListAdapter.getItemsQuantity());
    }

    // Get latest contests of the BarcodeManager and refresh the whole recycler view with them.
    public void refresh() {
        clearList();
        List<Barcode> barcodes = BarcodeManager.getInstance().getAllBarcodes();
        for (Barcode barcode : barcodes) {
            addToList(barcode);
        }
    }
}

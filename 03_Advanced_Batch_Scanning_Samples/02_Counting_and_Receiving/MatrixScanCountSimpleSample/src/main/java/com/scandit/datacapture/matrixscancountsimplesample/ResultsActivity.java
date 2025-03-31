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

package com.scandit.datacapture.matrixscancountsimplesample;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.matrixscancountsimplesample.data.ScanDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    public static final int CLEAR_SESSION = 1;

    private static final String ARG_SCAN_RESULTS = "scan-results";
    private static final String ARG_DONE_BUTTON_STYLE = "done-button-style";
    private DoneButtonStyle doneButtonStyle = DoneButtonStyle.RESUME;

    public static Intent getIntent(
        Context context, HashMap<String, ScanDetails> scanResults,
        DoneButtonStyle doneButtonStyle
    ) {
        return new Intent(context, ResultsActivity.class)
            .putExtra(ARG_SCAN_RESULTS, scanResults.values().toArray(new ScanDetails[0]))
            .putExtra(ARG_DONE_BUTTON_STYLE, doneButtonStyle.mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        doneButtonStyle = DoneButtonStyle.valueOf(
            getIntent().getIntExtra(ARG_DONE_BUTTON_STYLE, 1)
        );

        // Setup recycler view.
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
            new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL)
        );

        // Receive results from previous screen and set recycler view items.
        ScanDetails[] scanResults;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            scanResults = getIntent().getSerializableExtra(ARG_SCAN_RESULTS, ScanDetails[].class);
        } else {
            scanResults = (ScanDetails[]) getIntent().getSerializableExtra(ARG_SCAN_RESULTS);
        }

        recyclerView.setAdapter(new ScanResultsAdapter(this, scanResults));

        Button doneButton = findViewById(R.id.done_button);
        if (doneButtonStyle == DoneButtonStyle.RESUME) {
            doneButton.setText(R.string.resume_scanning);
        } else if (doneButtonStyle == DoneButtonStyle.NEW_SCAN) {
            doneButton.setText(R.string.start_new_scanning);
        }
        doneButton.setOnClickListener(view -> {
            finish();
        });

        Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(view -> {
            setResult(CLEAR_SESSION);
            finish();
        });

        TextView resultsAmount = findViewById(R.id.result_items_count);
        if (scanResults != null) {
            resultsAmount.setText(getString(R.string.results_amount, getScanResultsCount(scanResults)));
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private int getScanResultsCount(ScanDetails[] scanResults) {
        int count = 0;

        for (ScanDetails detail : scanResults) {
            count += detail.quantity;
        }

        return count;
    }

    @Override
    protected void onPause() {
        // Pause camera if the app is going to background.
        if (!isFinishing()) {
            CameraManager.getInstance().pauseFrameSource();
        }

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private static class ScanResultsAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final Context context;
        private final List<ScanDetails> nonUniqueItems = new ArrayList<>();
        private final List<ScanDetails> uniqueItems = new ArrayList<>();

        ScanResultsAdapter(Context context, ScanDetails[] items) {
            this.context = context;

            for (ScanDetails item : items) {
                if (item.quantity == 1) {
                    uniqueItems.add(item);
                } else {
                    nonUniqueItems.add(item);
                }
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.scan_result_item, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Pair<Integer, Integer> positionInSection = getPositionInSection(position);
            holder.update(
                positionInSection.first,
                positionInSection.second,
                getItemForPositionInSection(positionInSection.first, positionInSection.second)
            );
        }

        private Pair<Integer, Integer> getPositionInSection(int position) {
            int accumulatedItemCount = 0;
            for (int i = 0; i < getSectionCount(); i++) {
                if (position < accumulatedItemCount + getItemCountForSection(i)) {
                    return new Pair<>(i, position - accumulatedItemCount);
                }
                accumulatedItemCount += getItemCountForSection(i);
            }
            return new Pair<>(-1, 0);
        }

        private ScanDetails getItemForPositionInSection(int section, int position) {
            switch (section) {
                case 0:
                    return nonUniqueItems.get(position);
                case 1:
                    return uniqueItems.get(position);
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            int total = 0;
            for (int i = 0; i < getSectionCount(); i++) {
                total += getItemCountForSection(i);
            }
            return total;
        }

        private int getSectionCount() {
            return 2;
        }

        private int getItemCountForSection(int section) {
            switch (section) {
                case 0:
                    return nonUniqueItems.size();
                case 1:
                    return uniqueItems.size();
                default:
                    return 0;
            }
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productDescriptionTextView;
        private final TextView gtinTextView;
        private final TextView quantityTextView;

        ViewHolder(View itemView) {
            super(itemView);
            productDescriptionTextView = itemView.findViewById(R.id.item_title);
            gtinTextView = itemView.findViewById(R.id.item_description);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
        }

        void update(int section, int position, ScanDetails scanDetails) {
            switch (section) {
                case 0: {
                    productDescriptionTextView.setText(
                        itemView.getContext().getString(
                            R.string.multiple_quantity_product_description, position + 1
                        )
                    );
                    quantityTextView.setVisibility(View.VISIBLE);
                    quantityTextView.setText(
                        itemView.getContext().getString(
                            R.string.quantity, scanDetails.quantity
                        )
                    );
                }
                break;
                case 1: {
                    quantityTextView.setVisibility(View.GONE);
                    productDescriptionTextView.setText(
                        itemView.getContext().getString(
                            R.string.product_description, position + 1
                        )
                    );
                }
                break;
            }

            gtinTextView.setText(
                itemView.getContext().getString(
                    R.string.result_item, scanDetails.symbology, scanDetails.barcodeData
                )
            );
        }
    }

    public enum DoneButtonStyle {
        RESUME(1),
        NEW_SCAN(2);

        public final int mode;

        DoneButtonStyle(int mode) {
            this.mode = mode;
        }

        public static DoneButtonStyle valueOf(int mode) {
            for (DoneButtonStyle style : values()) {
                if (style.mode == mode) {
                    return style;
                }
            }
            return null;
        }
    }
}

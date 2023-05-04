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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.receivingsample.R;
import com.scandit.datacapture.receivingsample.data.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {

    private final List<ScanResult> results = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent, int viewType
    ) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_result_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(
        @NonNull ViewHolder holder, int position
    ) {
        ScanResult item = results.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public int getItemsQuantity() {
        int totalCount = 0;
        for (ScanResult result : results) {
            totalCount += result.quantity;
        }
        return totalCount;
    }

    public void addResult(ScanResult result) {
        int index = getIndexForAlreadyScannedResult(result);
        if (index == -1) {
            results.add(result);
            notifyItemInserted(getItemCount() - 1);
        } else {
            results.get(index).increaseQuantity();
            notifyItemChanged(index);
        }
    }

    public String getDataAtIndex(int index) {
        if (index < results.size()) {
            return results.get(index).barcodeData;
        }
        return null;
    }

    public void removeResultAtIndex(int index) {
        if (index < results.size()) {
            results.remove(index);
            notifyItemRemoved(index);
        }
    }

    private int getIndexForAlreadyScannedResult(ScanResult result) {
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).barcodeData.equals(result.barcodeData)) {
                return i;
            }
        }
        return -1;
    }

    public void clearResults() {
        results.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;
        private final TextView quantity;
        private final ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.item_title);
            this.description = itemView.findViewById(R.id.item_description);
            this.icon = itemView.findViewById(R.id.item_icon);
            this.quantity = itemView.findViewById(R.id.item_quantity);
        }

        public void bind(ScanResult result) {
            title.setText(result.barcodeData);
            description.setText(result.barcodeSymbology);
            icon.setImageResource(R.drawable.product_image);
            quantity.setText(
                itemView.getContext().getString(
                    R.string.quantity, result.quantity
                )
            );
        }
    }
}
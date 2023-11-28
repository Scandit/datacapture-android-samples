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
package com.scandit.datacapture.listbuildingsample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.listbuildingsample.data.ScanResult;
import com.scandit.datacapture.listbuildingsample.ui.StackedImageView;

import java.util.ArrayList;
import java.util.List;

class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {

    private final List<ScanResult> results = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent, int viewType
    ) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false));
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.clear();
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

    public int getItemsScannedCount() {
        int count = 0;

        for (ScanResult result: results) {
            count += result.quantity;
        }

        return count;
    }

    void addResult(ScanResult result) {
        for (int i = 0; i < results.size(); i++) {
            ScanResult next = results.get(i);
            if (next.barcodeData.equals(result.barcodeData) &&
                next.barcodeSymbology.equals(result.barcodeSymbology)) {
                // found an item, increase it's quantity and add its image to the existing list.
                next.increaseQuantity();
                next.addImages(result.images);

                // move result to the top of the list
                results.remove(next);
                results.add(0, next);

                // notify recyclerview that items have changed
                notifyDataSetChanged();
                return;
            }
        }

        // no items found, adding to list.
        results.add(0, result);
        notifyItemInserted(0);
    }

    void clearResults() {
        int resultSize = results.size();
        results.clear();
        notifyItemRangeRemoved(0, resultSize);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;
        private final TextView qty;
        private final StackedImageView imageStack;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.item_title);
            this.description = itemView.findViewById(R.id.item_description);
            this.imageStack = itemView.findViewById(R.id.item_image_stack);
            this.qty = itemView.findViewById(R.id.item_qty);
        }

        public void bind(ScanResult result) {
            title.setText(result.barcodeData);
            description.setText(result.barcodeSymbology);

            qty.setText(itemView.getContext().getString(R.string.item_qty, result.quantity));
            qty.setVisibility(result.quantity > 1 ? View.VISIBLE : View.GONE);

            imageStack.updateStackWith(result.images);
        }

        public void clear() {
            imageStack.clearImages();
        }
    }
}
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.listbuildingsample.data.ScanResult;

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
            LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(
        @NonNull ViewHolder holder, int position
    ) {
        ScanResult item = results.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    void addResult(ScanResult result) {
        results.add(result);
        notifyItemInserted(getItemCount() - 1);
    }

    void clearResults() {
        results.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;
        private final ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.item_title);
            this.description = itemView.findViewById(R.id.item_description);
            this.icon = itemView.findViewById(R.id.item_icon);
        }

        public void bind(ScanResult result, int position) {
            String titleText = itemView.getContext().getString(R.string.item_title, position + 1);
            title.setText(titleText);

            String desc = itemView.getContext()
                .getString(R.string.item_description, result.barcodeSymbology, result.barcodeData);
            description.setText(desc);

            icon.setImageBitmap(result.image);
        }
    }
}
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.reorderfromcatalogsample.R;
import com.scandit.datacapture.reorderfromcatalogsample.data.BarcodeResult;

import java.util.List;

class ResultsItemsAdapter extends RecyclerView.Adapter<ResultsItemsAdapter.ViewHolder> {

    private final List<BarcodeResult> results;

    public ResultsItemsAdapter(List<BarcodeResult> results) {
        this.results = results;
    }

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
        BarcodeResult item = results.get(position);
        holder.getTextSymbology().setText(item.getSymbology().name());
        holder.getTextCode().setText(item.getBarcode());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textSymbology;
        private final TextView textCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textSymbology = itemView.findViewById(R.id.text_symbology);
            this.textCode = itemView.findViewById(R.id.text_code);
        }

        public TextView getTextSymbology() {
            return textSymbology;
        }

        public TextView getTextCode() {
            return textCode;
        }
    }
}

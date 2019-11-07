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

package com.scandit.datacapture.barcodecaptureviewssample.modes.splitview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcodecaptureviewssample.R;

import java.util.ArrayList;
import java.util.List;

public class SplitViewAdapter extends RecyclerView.Adapter<SplitViewAdapter.ViewHolder> {

    private final List<Barcode> results;

    SplitViewAdapter(List<Barcode> results) {
        this.results = new ArrayList<>(results);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(android.R.layout.simple_list_item_2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(results.get(position));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    void addResult(Barcode result) {
        results.add(result);
        notifyItemInserted(getItemCount() - 1);
    }

    void resetResults() {
        int count = getItemCount();
        results.clear();
        notifyItemRangeRemoved(0, count);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textData, textSymbology;

        ViewHolder(View itemView) {
            super(itemView);
            textData = itemView.findViewById(android.R.id.text1);
            textSymbology = itemView.findViewById(android.R.id.text2);
            textSymbology.setTextColor(
                    ContextCompat.getColor(itemView.getContext(), R.color.scanditBlue)
            );
        }

        void bind(Barcode result) {
            textData.setText(result.getData() != null ? result.getData() : "");
            // Get the human readable name of the symbology.
            String symbology = SymbologyDescription.create(result.getSymbology()).getReadableName();
            textSymbology.setText(symbology);
        }
    }
}

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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.symbologies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.TypedDiffUtilCallback;
import com.scandit.datacapture.barcodecapturesettingssample.utils.TwoTextsAndIconViewHolder;

public class SymbologySettingsAdapter
        extends RecyclerView.Adapter<SymbologySettingsAdapter.ViewHolder> {

    private SymbologySettingsEntry[] symbologyDescriptions;
    private Callback callback;

    SymbologySettingsAdapter(
            SymbologySettingsEntry[] symbologyDescriptions, Callback callback
    ) {
        this.symbologyDescriptions = symbologyDescriptions;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.two_texts_and_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SymbologySettingsEntry currentEntry = symbologyDescriptions[position];
        holder.bind(currentEntry.symbologyDescription, currentEntry.enabled);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSymbologyClicked(currentEntry.symbologyDescription);
            }
        });
    }

    @Override
    public int getItemCount() {
        return symbologyDescriptions.length;
    }

    void updateData(SymbologySettingsEntry[] symbologyDescriptions) {
        SymbologySettingsEntryDiff diff =
                new SymbologySettingsEntryDiff(this.symbologyDescriptions, symbologyDescriptions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diff);
        this.symbologyDescriptions = symbologyDescriptions;
        result.dispatchUpdatesTo(this);
    }

    protected class ViewHolder extends TwoTextsAndIconViewHolder {

        ViewHolder(View itemView) {
            super(itemView, R.id.text_field, R.id.text_field_2);
        }

        void bind(SymbologyDescription symbology, boolean enabled) {
            setFirstTextView(symbology.getReadableName());
            setSecondTextViewText(enabled ? "On" : "Off");
        }
    }

    private class SymbologySettingsEntryDiff extends TypedDiffUtilCallback<SymbologySettingsEntry> {

        SymbologySettingsEntryDiff(
                SymbologySettingsEntry[] oldEntries, SymbologySettingsEntry[] newEntries
        ) {
            super(oldEntries, newEntries);
        }

        @Override
        public boolean areItemsTheSame(
                SymbologySettingsEntry oldItem, SymbologySettingsEntry newItem
        ) {
            String oldDescription = oldItem.symbologyDescription.getIdentifier();
            String newDescription = newItem.symbologyDescription.getIdentifier();
            return oldDescription.equals(newDescription);
        }

        @Override
        public boolean areContentsTheSame(
                SymbologySettingsEntry oldItem, SymbologySettingsEntry newItem
        ) {
            return oldItem.enabled == newItem.enabled;
        }
    }

    interface Callback {
        void onSymbologyClicked(SymbologyDescription symbology);
    }
}

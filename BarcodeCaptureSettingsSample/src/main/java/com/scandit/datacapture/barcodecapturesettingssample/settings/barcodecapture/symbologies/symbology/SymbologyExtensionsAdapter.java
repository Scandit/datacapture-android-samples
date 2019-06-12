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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.symbologies.symbology;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.TypedDiffUtilCallback;
import com.scandit.datacapture.barcodecapturesettingssample.utils.TwoTextsAndIconViewHolder;

public class SymbologyExtensionsAdapter
        extends RecyclerView.Adapter<SymbologyExtensionsAdapter.ViewHolder> {

    private Callback callback;
    private SymbologyExtensionEntry[] symbologyExtensions;

    SymbologyExtensionsAdapter(
            SymbologyExtensionEntry[] symbologyExtension,
            Callback callback
    ) {
        this.symbologyExtensions = symbologyExtension;
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
        final SymbologyExtensionEntry currentEntry = symbologyExtensions[position];
        holder.bind(currentEntry);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onExtensionClick(currentEntry.extensionName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return symbologyExtensions.length;
    }

    void updateData(SymbologyExtensionEntry[] symbologyExtensions) {
        ExtensionsSettingsEntryDiff diff =
                new ExtensionsSettingsEntryDiff(this.symbologyExtensions, symbologyExtensions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diff);
        this.symbologyExtensions = symbologyExtensions;
        result.dispatchUpdatesTo(this);
    }

    protected class ViewHolder extends TwoTextsAndIconViewHolder {

        ViewHolder(View itemView) {
            super(itemView, R.id.text_field, R.id.text_field_2);
        }

        void bind(SymbologyExtensionEntry entry) {
            setFirstTextView(entry.extensionName);
            setIcon(entry.enabled ? R.drawable.ic_check : 0);
        }
    }

    private class ExtensionsSettingsEntryDiff
            extends TypedDiffUtilCallback<SymbologyExtensionEntry> {

        ExtensionsSettingsEntryDiff(
                SymbologyExtensionEntry[] oldEntries, SymbologyExtensionEntry[] newEntries
        ) {
            super(oldEntries, newEntries);
        }

        @Override
        public boolean areItemsTheSame(
                SymbologyExtensionEntry oldItem, SymbologyExtensionEntry newItem
        ) {
            return oldItem.extensionName.equals(newItem.extensionName);
        }

        @Override
        public boolean areContentsTheSame(
                SymbologyExtensionEntry oldItem, SymbologyExtensionEntry newItem
        ) {
            return oldItem.enabled == newItem.enabled;
        }
    }

    interface Callback {
        void onExtensionClick(String extension);
    }
}

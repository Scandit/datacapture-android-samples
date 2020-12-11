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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.TypedDiffUtilCallback;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes.type.CompositeTypeEntry;
import com.scandit.datacapture.barcodecapturesettingssample.utils.TwoTextsAndIconViewHolder;

public class CompositeTypesAdapter extends RecyclerView.Adapter<CompositeTypesAdapter.ViewHolder> {

    private CompositeTypeEntry[] types;
    private Callback callback;

    CompositeTypesAdapter(CompositeTypeEntry[] types, Callback callback) {
        this.types = types;
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
        final CompositeTypeEntry currentEntry = types[position];
        holder.bind(currentEntry);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCompositeTypeClick(currentEntry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return types.length;
    }

    void updateData(CompositeTypeEntry[] types) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                new CompositeTypeEntryDiff(this.types, types)
        );
        this.types = types;
        result.dispatchUpdatesTo(this);
    }

    protected class ViewHolder extends TwoTextsAndIconViewHolder {

        ViewHolder(View itemView) {
            super(itemView, R.id.text_field, R.id.text_field_2);
        }

        void bind(CompositeTypeEntry entry) {
            setFirstTextView(entry.displayNameRes);
            setIcon(entry.enabled ? R.drawable.ic_check : 0);
        }
    }

    private class CompositeTypeEntryDiff extends TypedDiffUtilCallback<CompositeTypeEntry> {

        CompositeTypeEntryDiff(CompositeTypeEntry[] oldEntries, CompositeTypeEntry[] newEntries) {
            super(oldEntries, newEntries);
        }

        @Override
        public boolean areItemsTheSame(CompositeTypeEntry oldItem, CompositeTypeEntry newItem) {
            return oldItem.getClass().isInstance(newItem);
        }

        @Override
        public boolean areContentsTheSame(CompositeTypeEntry oldItem, CompositeTypeEntry newItem) {
            return oldItem.enabled == newItem.enabled;
        }
    }

    public interface Callback {
        void onCompositeTypeClick(CompositeTypeEntry entry);
    }
}

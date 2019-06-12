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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.utils.SingleTextViewHolder;

public class BarcodeCaptureSettingsAdapter
        extends RecyclerView.Adapter<BarcodeCaptureSettingsAdapter.ViewHolder> {

    private Callback callback;
    private BarcodeCaptureSettingsEntry[] entries;

    BarcodeCaptureSettingsAdapter(BarcodeCaptureSettingsEntry[] entries, Callback callback) {
        this.entries = entries;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.single_text_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BarcodeCaptureSettingsEntry currentEntry = entries[position];
        holder.setFirstTextView(currentEntry.displayNameResource);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onEntryClick(currentEntry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.length;
    }

    protected class ViewHolder extends SingleTextViewHolder {
        ViewHolder(View itemView) {
            super(itemView, R.id.text_field);
        }
    }

    interface Callback {
        void onEntryClick(BarcodeCaptureSettingsEntry entry);
    }
}

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

package com.scandit.datacapture.barcodecapturesettingssample.settings.camera;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.TypedDiffUtilCallback;
import com.scandit.datacapture.barcodecapturesettingssample.utils.TwoTextsAndIconViewHolder;

public class CameraSettingsPositionAdapter
        extends RecyclerView.Adapter<CameraSettingsPositionAdapter.ViewHolder> {

    private Callback callback;
    private CameraSettingsPositionEntry[] cameraPositions;

    CameraSettingsPositionAdapter(
            CameraSettingsPositionEntry[] cameraPositions,
            Callback callback
    ) {
        this.cameraPositions = cameraPositions;
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
        final CameraSettingsPositionEntry currentPositionEntry = cameraPositions[position];
        holder.bind(currentPositionEntry);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCameraPositionClick(currentPositionEntry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cameraPositions.length;
    }

    void updateData(CameraSettingsPositionEntry[] cameraPositions) {
        CameraSettingsPositionEntryDiff diff =
                new CameraSettingsPositionEntryDiff(this.cameraPositions, cameraPositions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diff);
        this.cameraPositions = cameraPositions;
        result.dispatchUpdatesTo(this);
    }

    private class CameraSettingsPositionEntryDiff
            extends TypedDiffUtilCallback<CameraSettingsPositionEntry> {

        CameraSettingsPositionEntryDiff(
                CameraSettingsPositionEntry[] oldEntries,
                CameraSettingsPositionEntry[] newEntries
        ) {
            super(oldEntries, newEntries);
        }

        @Override
        public boolean areItemsTheSame(
                CameraSettingsPositionEntry oldItem, CameraSettingsPositionEntry newItem
        ) {
            return oldItem.cameraPosition == newItem.cameraPosition;
        }

        @Override
        public boolean areContentsTheSame(
                CameraSettingsPositionEntry oldItem, CameraSettingsPositionEntry newItem
        ) {
            return oldItem.enabled == newItem.enabled;
        }
    }

    protected class ViewHolder extends TwoTextsAndIconViewHolder {

        ViewHolder(View itemView) {
            super(itemView, R.id.text_field, R.id.text_field_2);
        }

        void bind(CameraSettingsPositionEntry currentPositionEntry) {
            setFirstTextView(currentPositionEntry.cameraPosition.name());
            setIcon(currentPositionEntry.enabled ? R.drawable.ic_check : 0);
        }
    }

    public interface Callback {
        void onCameraPositionClick(CameraSettingsPositionEntry cameraPositionEntry);
    }
}

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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.TypedDiffUtilCallback;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationType;
import com.scandit.datacapture.barcodecapturesettingssample.utils.TwoTextsAndIconViewHolder;

public class LocationTypeAdapter extends RecyclerView.Adapter<LocationTypeAdapter.ViewHolder> {

    private LocationType[] locationTypes;
    private Callback callback;

    LocationTypeAdapter(LocationType[] locationTypes, Callback callback) {
        this.locationTypes = locationTypes;
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
        final LocationType currentEntry = locationTypes[position];
        holder.bind(currentEntry);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onLocationTypeClick(currentEntry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationTypes.length;
    }

    void updateData(LocationType[] locationTypes) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                new LocationTypeEntryDiff(this.locationTypes, locationTypes)
        );
        this.locationTypes = locationTypes;
        result.dispatchUpdatesTo(this);
    }

    protected class ViewHolder extends TwoTextsAndIconViewHolder {

        ViewHolder(View itemView) {
            super(itemView, R.id.text_field, R.id.text_field_2);
        }

        void bind(LocationType entry) {
            setFirstTextView(entry.displayNameRes);
            setIcon(entry.enabled ? R.drawable.ic_check : 0);
        }
    }

    private class LocationTypeEntryDiff extends TypedDiffUtilCallback<LocationType> {

        LocationTypeEntryDiff(LocationType[] oldEntries, LocationType[] newEntries) {
            super(oldEntries, newEntries);
        }

        @Override
        public boolean areItemsTheSame(LocationType oldItem, LocationType newItem) {
            return oldItem.getClass().isInstance(newItem);
        }

        @Override
        public boolean areContentsTheSame(LocationType oldItem, LocationType newItem) {
            return oldItem.enabled == newItem.enabled;
        }
    }

    public interface Callback {
        void onLocationTypeClick(LocationType entry);
    }
}

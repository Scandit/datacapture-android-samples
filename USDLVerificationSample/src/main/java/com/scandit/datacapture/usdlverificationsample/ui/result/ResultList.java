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

package com.scandit.datacapture.usdlverificationsample.ui.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.usdlverificationsample.R;

class ResultListAdapter extends ListAdapter<CaptureResult.Entry, ResultListViewHolder> {

    public ResultListAdapter() {
        super(new ItemCallback());
    }

    @NonNull
    @Override
    public ResultListViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent, int viewType
    ) {
        Context context = parent.getContext();
        return new ResultListViewHolder(
            LayoutInflater.from(context).inflate(R.layout.result_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(
        @NonNull ResultListViewHolder holder, int position
    ) {
        holder.bind(getItem(position));
    }
}

class ResultListViewHolder extends RecyclerView.ViewHolder {

    public ResultListViewHolder(@NonNull View itemView) { super(itemView); }

    public void bind(CaptureResult.Entry item) {
        ((TextView) itemView.findViewById(R.id.result_header_text)).setText(item.getKey());
        ((TextView) itemView.findViewById(R.id.result_value_text)).setText(item.getValue());
    }
}

class ItemCallback extends DiffUtil.ItemCallback<CaptureResult.Entry> {
    @Override
    public boolean areItemsTheSame(@NonNull CaptureResult.Entry oldItem, @NonNull CaptureResult.Entry newItem) {
        return oldItem.getKey().equals(newItem.getKey());
    }

    @Override
    public boolean areContentsTheSame(@NonNull CaptureResult.Entry oldItem, @NonNull CaptureResult.Entry newItem) {
        return oldItem.getValue().equals(newItem.getValue());
    }
}

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

package com.scandit.datacapture.idcaptureextendedsample.ui.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.idcaptureextendedsample.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class ResultListAdapter extends ListAdapter<ResultEntry, ResultListViewHolder> {

    public ResultListAdapter(List<ResultEntry> entries) {
        super(new ItemCallback());
        // We set the result entries as our adapter's list.
        submitList(entries);
    }

    @NotNull
    @Override
    public ResultListViewHolder onCreateViewHolder(
        @NotNull ViewGroup parent, int viewType
    ) {
        Context context = parent.getContext();
        return new ResultListViewHolder(
            LayoutInflater.from(context).inflate(R.layout.result_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(
        @NotNull ResultListViewHolder holder, int position
    ) {
        // We want to reverse the results map's order.
        holder.bind(getItem(position));
    }
}

class ResultListViewHolder extends RecyclerView.ViewHolder {

    public ResultListViewHolder(@NotNull View itemView) { super(itemView); }

    public void bind(ResultEntry item) {
        ((TextView) itemView.findViewById(R.id.text_value)).setText(item.getValue());
        ((TextView) itemView.findViewById(R.id.text_key)).setText(item.getKey());
    }
}

class ItemCallback extends DiffUtil.ItemCallback<ResultEntry> {
    @Override
    public boolean areItemsTheSame(@NotNull ResultEntry oldItem, @NotNull ResultEntry newItem) {
        return oldItem.getKey().equals(newItem.getKey());
    }

    @Override
    public boolean areContentsTheSame(@NotNull ResultEntry oldItem, @NotNull ResultEntry newItem) {
        return oldItem.getValue().equals(newItem.getValue());
    }
}

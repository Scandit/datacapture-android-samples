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

package com.scandit.datacapture.restockingsample.result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.restockingsample.R;

import java.util.List;

/**
 * {@link RecyclerView} adapter to display the pick result in the result screen.
 */
public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    @NonNull
    final List<Object> content;

    public ProductListAdapter(@NonNull List<Object> content) {
        this.content = content;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = content.get(position);
        if (item instanceof Header) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return HeaderHolder.create(parent);
        } else {
            return ProductHolder.create(parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = content.get(position);
        if (item instanceof Header) {
            ((HeaderHolder) holder).bind((Header) item);
        } else if (item instanceof DisplayProduct) {
            ((ProductHolder) holder).bind((DisplayProduct) item);
        }
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    private static class HeaderHolder extends RecyclerView.ViewHolder {
        static HeaderHolder create(@NonNull ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header, parent, false);
            return new HeaderHolder(itemView);
        }

        @NonNull
        private final TextView textView;

        private HeaderHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        public void bind(Header header) {
            String text;
            if (header.pickList) {
                text = textView.getContext().getString(R.string.items_picked);
            } else {
                text = textView.getContext().getString(R.string.items_scanned, header.total);
            }

            textView.setText(text);
        }
    }

    private static class ProductHolder extends RecyclerView.ViewHolder {
        public static ProductHolder create(@NonNull ViewGroup parent) {
            return new ProductHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_result, parent, false));
        }

        @NonNull
        private final TextView itemIdentifier;
        @NonNull
        private final TextView itemGtin;
        @NonNull
        private final TextView itemWarning;
        @NonNull
        private final ImageView checkmark;

        private ProductHolder(@NonNull View itemView) {
            super(itemView);

            itemIdentifier = itemView.findViewById(R.id.itemIdentifier);
            itemGtin = itemView.findViewById(R.id.itemGtin);
            itemWarning = itemView.findViewById(R.id.itemWarning);
            checkmark = itemView.findViewById(R.id.checkmark);
        }

        public void bind(@NonNull DisplayProduct product) {
            itemIdentifier.setText(product.identifier);
            itemGtin.setText(itemView.getContext().getString(R.string.item_gtin, product.barcodeData));

            if (product.quantityToPick == 0 && product.picked) {
                checkmark.setVisibility(View.VISIBLE);
                checkmark.setImageResource(R.drawable.ic_warning);
                itemWarning.setVisibility(View.VISIBLE);
            } else if (product.picked) {
                checkmark.setVisibility(View.VISIBLE);
                checkmark.setImageResource(R.drawable.ic_green_check);
                itemWarning.setVisibility(View.GONE);
            } else {
                checkmark.setVisibility(View.INVISIBLE);
                itemWarning.setVisibility(View.GONE);
            }
        }
    }
}

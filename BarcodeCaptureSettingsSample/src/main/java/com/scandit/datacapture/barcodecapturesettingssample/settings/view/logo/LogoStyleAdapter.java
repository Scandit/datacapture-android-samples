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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.logo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.StringUtils;
import com.scandit.datacapture.barcodecapturesettingssample.base.TypedDiffUtilCallback;
import com.scandit.datacapture.barcodecapturesettingssample.utils.TwoTextsAndIconViewHolder;

class LogoStyleAdapter extends RecyclerView.Adapter<LogoStyleAdapter.ViewHolder> {

        private Callback callback;
        private LogoStyleEntry[] styles;

        public LogoStyleAdapter(LogoStyleEntry[] styles, Callback callback) {
            this.styles = styles;
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
            final LogoStyleEntry currentStyleEntry = styles[position];
            holder.bind(currentStyleEntry);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onLogoStyleClicked(currentStyleEntry);
                }
            });
        }

        @Override
        public int getItemCount() {
            return styles.length;
        }

        public void updateData(LogoStyleEntry[] styles) {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                    new LogoStyleDiff(this.styles, styles)
            );
            this.styles = styles;
            result.dispatchUpdatesTo(this);
        }

        protected class ViewHolder extends TwoTextsAndIconViewHolder {

            ViewHolder(View itemView) {
                super(itemView, R.id.text_field, R.id.text_field_2);
            }

            void bind(LogoStyleEntry entry) {
                String name = entry.getStyle().name();
                setFirstTextView(StringUtils.nameCaseString(name));
                setIcon(entry.isEnabled() ? R.drawable.ic_check : 0);
            }
        }

        private class LogoStyleDiff extends TypedDiffUtilCallback<LogoStyleEntry> {

            LogoStyleDiff(LogoStyleEntry[] oldEntries, LogoStyleEntry[] newEntries) {
                super(oldEntries, newEntries);
            }

            @Override
            public boolean areItemsTheSame(LogoStyleEntry oldItem, LogoStyleEntry newItem) {
                return oldItem.getClass().isInstance(newItem);
            }

            @Override
            public boolean areContentsTheSame(LogoStyleEntry oldItem, LogoStyleEntry newItem) {
                return oldItem.getStyle() == newItem.getStyle()
                        && oldItem.isEnabled() == newItem.isEnabled();
            }
        }

        public interface Callback {
            void onLogoStyleClicked(LogoStyleEntry style);
        }
}

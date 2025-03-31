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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.common;

import android.content.Context;

import androidx.annotation.StringRes;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceViewHolder;

import com.scandit.datacapture.barcodeselectionsettingssample.R;

public final class StyledListPreference extends ListPreference {

    private boolean showDivider;

    public StyledListPreference(Context context, String key, String title, boolean showDivider) {
        super(context);
        init(key, title, showDivider);
    }

    public StyledListPreference(Context context, String key, @StringRes int title, boolean showDivider) {
        this(context, key, context.getString(title), showDivider);
    }

    private void init(String key, String title, boolean showDivider) {
        setIconSpaceReserved(false);
        setKey(key);
        setTitle(title);
        setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
        this.showDivider = showDivider;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setBackgroundResource(R.drawable.bg_white_ripple);
        holder.setDividerAllowedAbove(showDivider);
        holder.setDividerAllowedBelow(showDivider);
    }
}

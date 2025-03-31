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
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SeekBarPreference;

import com.scandit.datacapture.barcodeselectionsettingssample.R;

public final class StyledSeekBarPreference extends SeekBarPreference {

    private boolean showDivider;

    public StyledSeekBarPreference(Context context, String key, String title, boolean showDivider, int min, int max) {
        super(context);
        init(key, title, showDivider, min, max);
    }

    public StyledSeekBarPreference(Context context, String key, @StringRes int title, boolean showDivider, int min, int max) {
        this(context, key, context.getString(title), showDivider, min, max);
    }

    private void init(String key, String title, boolean divider, int min, int max) {
        setIconSpaceReserved(false);
        setKey(key);
        setTitle(title);
        this.showDivider = divider;
        setMin(min);
        setMax(max);
        setShowSeekBarValue(true);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setBackgroundResource(R.drawable.bg_white_ripple);
        holder.setDividerAllowedAbove(showDivider);
        holder.setDividerAllowedBelow(showDivider);
    }
}

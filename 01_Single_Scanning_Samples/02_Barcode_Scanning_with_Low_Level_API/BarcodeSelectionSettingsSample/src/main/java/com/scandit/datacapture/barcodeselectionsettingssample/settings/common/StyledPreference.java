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
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.scandit.datacapture.barcodeselectionsettingssample.R;

public class StyledPreference extends Preference {

    private int textColor;
    private boolean showDivider;

    public StyledPreference(Context context, String key, String title, @ColorInt int textColor, boolean showDivider) {
        super(context);
        init(key, title, textColor, showDivider);
    }

    public StyledPreference(Context context, String key, String title) {
        this(context, key, title, -1, false);
    }

    public StyledPreference(Context context, String key, @StringRes int title, boolean showDivider) {
        this(context, key, context.getString(title), -1, showDivider);
    }

    public StyledPreference(Context context, String key, @StringRes int title, @ColorInt int textColor) {
        this(context, key, context.getString(title), textColor, false);
    }

    private void init(String key, String title, int textColor, boolean showDivider) {
        setIconSpaceReserved(false);
        setKey(key);
        setTitle(title);
        this.textColor = textColor;
        this.showDivider = showDivider;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setBackgroundResource(R.drawable.bg_white_ripple);
        if (textColor != -1) {
            TextView titleView = (TextView) holder.findViewById(android.R.id.title);
            titleView.setTextColor(textColor);
        }
        holder.setDividerAllowedAbove(showDivider);
        holder.setDividerAllowedBelow(showDivider);
    }
}

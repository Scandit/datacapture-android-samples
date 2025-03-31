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
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import com.scandit.datacapture.barcodeselectionsettingssample.R;

public final class StyledNumericEditTextPreference extends EditTextPreference
        implements EditTextPreference.OnBindEditTextListener {

    private boolean showDivider;

    public StyledNumericEditTextPreference(Context context, String key, String title, boolean showDivider) {
        super(context);
        init(key, title, showDivider);
    }

    public StyledNumericEditTextPreference(Context context, String key, @StringRes int title, boolean showDivider) {
        this(context, key, context.getString(title), showDivider);
    }

    private void init(String key, String title, boolean showDivider) {
        setIconSpaceReserved(false);
        setKey(key);
        setTitle(title);
        this.showDivider = showDivider;
        setSummaryProvider((SummaryProvider<EditTextPreference>) preference -> {
            String value = preference.getText();
            try {
                float number = Float.parseFloat(value);
                if (Float.isInfinite(number) || Float.isNaN(number)) {
                    value = preference.getContext().getString(R.string.invalid_number);
                } else {
                    value = String.valueOf(number);
                }
            } catch (NumberFormatException e) {
                value = preference.getContext().getString(R.string.invalid_number);
            } catch (NullPointerException e) {
                value = preference.getContext().getString(androidx.preference.R.string.not_set);
            }
            return value;
        });
        setOnBindEditTextListener(this);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setBackgroundResource(R.drawable.bg_white_ripple);
        holder.setDividerAllowedAbove(showDivider);
        holder.setDividerAllowedBelow(showDivider);
    }

    @Override
    public void onBindEditText(@NonNull EditText editText) {
        editText.setSelectAllOnFocus(true);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
    }
}

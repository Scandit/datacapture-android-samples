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

package com.scandit.datacapture.vincodessample.ui.result;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.scandit.datacapture.vincodessample.R;

/**
 * The helper view that displays a single piece of data parsed from a VIN.
 */
public class VinResultItemView extends LinearLayout {
    private TextView valueText;

    public VinResultItemView(Context context) {
        super(context);
    }

    public VinResultItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initLayout(context, attrs);
    }

    public VinResultItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initLayout(context, attrs);
    }

    public VinResultItemView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context, attrs);
    }

    private void initLayout(Context context, @Nullable AttributeSet attrs) {
        View layout = inflate(context, R.layout.vin_result_item, this);

        TextView labelText = layout.findViewById(R.id.result_item_label);
        valueText = layout.findViewById(R.id.result_item_value);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.VinResultItem,
                0, 0);

        try {
            labelText.setText(a.getString(R.styleable.VinResultItem_labelText));
            valueText.setText(a.getString(R.styleable.VinResultItem_valueText));
        } finally {
            a.recycle();
        }
    }

    public void setValueText(@Nullable String text) {
        valueText.setText(text);
    }
}

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
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.scandit.datacapture.usdlverificationsample.R;

public class VerificationResultItemView extends LinearLayout {

    private ImageView iconView;
    private TextView textView;

    public VerificationResultItemView(@NonNull Context context) {
        this(context, null);
    }

    public VerificationResultItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerificationResultItemView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View root = inflate(context, R.layout.view_verification_result_item, this);
        Resources res = context.getResources();

        root.setPadding(
                res.getDimensionPixelSize(R.dimen.default_padding),
                res.getDimensionPixelSize(R.dimen.default_padding_small),
                res.getDimensionPixelSize(R.dimen.default_padding),
                res.getDimensionPixelSize(R.dimen.default_padding_small)
        );

        iconView = root.findViewById(R.id.icon_view);
        textView = root.findViewById(R.id.text_view);
    }

    public void setData(@DrawableRes int iconRes, @StringRes int textRes) {
        iconView.setImageResource(iconRes);
        textView.setText(textRes);
    }
}

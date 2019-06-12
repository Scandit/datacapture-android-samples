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

package com.scandit.datacapture.barcodecapturesettingssample.utils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public abstract class TwoTextsAndIconViewHolder extends SingleTextViewHolder {

    private TextView secondTextView;

    public TwoTextsAndIconViewHolder(
            View itemView,
            @IdRes int resourceId,
            @IdRes int secondTextResourceId
    ) {
        super(itemView, resourceId);
        secondTextView = itemView.findViewById(secondTextResourceId);
    }

    public void setSecondTextViewText(String secondText) {
        secondTextView.setText(secondText);
    }

    public void setSecondTextViewText(@StringRes int secondTextRes) {
        secondTextView.setText(secondTextRes);
    }

    public void setIcon(@Nullable Drawable icon) {
        secondTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
    }

    public void setIcon(@DrawableRes int iconRes) {
        secondTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconRes, 0);
    }
}

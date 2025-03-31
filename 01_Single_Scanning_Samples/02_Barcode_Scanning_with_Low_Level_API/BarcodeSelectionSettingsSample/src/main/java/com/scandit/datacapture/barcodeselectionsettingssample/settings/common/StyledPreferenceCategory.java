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

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.preference.PreferenceCategory;

public final class StyledPreferenceCategory extends PreferenceCategory {

    public StyledPreferenceCategory(Context context, String key, @Nullable String title) {
        super(context);
        init(key, title);
    }

    public StyledPreferenceCategory(Context context, String key, @StringRes int title) {
        this(context, key, context.getString(title));
    }

    private void init(String key, String title) {
        setIconSpaceReserved(false);
        setKey(key);
        if (title != null) {
            setTitle(title);
        }
    }
}

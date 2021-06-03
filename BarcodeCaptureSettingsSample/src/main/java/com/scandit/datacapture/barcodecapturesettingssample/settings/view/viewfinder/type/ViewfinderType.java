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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.scandit.datacapture.core.ui.viewfinder.Viewfinder;

public abstract class ViewfinderType {

    @StringRes public final int displayName;
    public final boolean enabled;

    ViewfinderType(@StringRes int displayName, boolean enabled) {
        this.displayName = displayName;
        this.enabled = enabled;
    }

    @Nullable
    public abstract Viewfinder buildViewfinder();

    public abstract void resetDefaults();
}

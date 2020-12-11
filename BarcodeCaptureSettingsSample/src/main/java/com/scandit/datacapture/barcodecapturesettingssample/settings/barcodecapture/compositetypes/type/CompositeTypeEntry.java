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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes.type;

import androidx.annotation.StringRes;

import com.scandit.datacapture.barcode.data.CompositeType;

public abstract class CompositeTypeEntry {

    @StringRes public final int displayNameRes;
    public final CompositeType compositeType;
    public final boolean enabled;

    CompositeTypeEntry(@StringRes int displayNameRes, boolean enabled, CompositeType compositeType) {
        this.displayNameRes = displayNameRes;
        this.compositeType = compositeType;
        this.enabled = enabled;
    }
}

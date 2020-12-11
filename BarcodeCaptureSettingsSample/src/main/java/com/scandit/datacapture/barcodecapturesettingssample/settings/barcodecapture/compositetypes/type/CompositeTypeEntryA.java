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
import com.scandit.datacapture.barcodecapturesettingssample.R;

import java.util.EnumSet;

public class CompositeTypeEntryA extends CompositeTypeEntry {

    public static CompositeTypeEntryA fromCurrentTypes(EnumSet<CompositeType> types) {
        return new CompositeTypeEntryA(
                R.string.a,
                types.contains(CompositeType.A)
        );
    }

    private CompositeTypeEntryA(@StringRes int displayNameRes, boolean enabled) {
        super(displayNameRes, enabled, CompositeType.A);
    }
}

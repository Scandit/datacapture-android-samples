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

public class CompositeTypeEntryB extends CompositeTypeEntry {

    public static CompositeTypeEntryB fromCurrentTypes(EnumSet<CompositeType> types) {
        return new CompositeTypeEntryB(
                R.string.b,
                types.contains(CompositeType.B)
        );
    }

    private CompositeTypeEntryB(@StringRes int displayNameRes, boolean enabled) {
        super(displayNameRes, enabled, CompositeType.B);
    }
}

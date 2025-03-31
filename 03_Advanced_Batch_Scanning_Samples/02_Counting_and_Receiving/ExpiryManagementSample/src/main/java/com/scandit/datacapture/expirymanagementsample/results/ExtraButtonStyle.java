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

package com.scandit.datacapture.expirymanagementsample.results;

// Defines the style of the extra button in the results screen, if any.
public enum ExtraButtonStyle {
    NONE(0),
    RESUME(1),
    NEW_SCAN(2);

    public final int mode;

    ExtraButtonStyle(int mode) {
        this.mode = mode;
    }

    public static ExtraButtonStyle valueOf(int mode) {
        for (ExtraButtonStyle style : values()) {
            if (style.mode == mode) {
                return style;
            }
        }
        return null;
    }
}

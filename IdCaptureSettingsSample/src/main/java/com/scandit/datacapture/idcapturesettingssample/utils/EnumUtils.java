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

package com.scandit.datacapture.idcapturesettingssample.utils;

import java.util.Arrays;

public class EnumUtils {
    public static String[] getEntryNamesTitleCase(Enum[] enums) {
        return Arrays.stream(getEntryNames(enums))
                .map(StringUtils::toTitleCase)
                .toArray(String[]::new);
    }

    public static String[] getEntryNames(Enum[] enums) {
        return Arrays.stream(enums)
                .map(Enum::name)
                .toArray(String[]::new);
    }
}

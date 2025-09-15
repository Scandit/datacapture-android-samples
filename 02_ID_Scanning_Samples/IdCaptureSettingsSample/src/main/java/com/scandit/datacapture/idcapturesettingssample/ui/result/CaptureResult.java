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

package com.scandit.datacapture.idcapturesettingssample.ui.result;

import androidx.annotation.NonNull;

import com.scandit.datacapture.id.data.IdImages;

import java.util.ArrayList;
import java.util.Collection;

public class CaptureResult {
    private final ArrayList<Entry> entries;
    @NonNull private final String fullName;
    @NonNull private final String dateOfBirth;
    @NonNull private final IdImages images;

    public CaptureResult(
            Collection<Entry> entries,
            @NonNull String fullName,
            @NonNull String dateOfBirth,
            @NonNull IdImages images
    ) {
        this.entries = new ArrayList<>(entries);
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.images = images;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    @NonNull
    public String getFullName() {
        return fullName;
    }

    @NonNull
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @NonNull
    public IdImages getImages() {
        return images;
    }

    public static class Entry {

        private final String key;
        private final String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}

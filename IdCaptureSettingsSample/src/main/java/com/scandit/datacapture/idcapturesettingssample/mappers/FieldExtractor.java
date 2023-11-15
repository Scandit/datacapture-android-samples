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

package com.scandit.datacapture.idcapturesettingssample.mappers;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class FieldExtractor {

    protected static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();

    protected final CapturedId capturedId;

    public FieldExtractor(CapturedId capturedId) {
        this.capturedId = capturedId;
    }

    abstract List<CaptureResult.Entry> extract();

    @NonNull
    protected String extractField(Boolean value) {
        if (value == null) {
            return "<empty>";
        }

        return String.valueOf(value);
    }

    @NonNull
    protected String extractField(int value) {
        return String.valueOf(value);
    }

    @NonNull
    protected String extractField(Integer value) {
        return String.valueOf(value);
    }

    @NonNull
    protected String extractField(@Nullable String value) {
        if (TextUtils.isEmpty(value)) {
            return "<empty>";
        }
        return value;
    }

    @NonNull
    protected String extractField(@Nullable DateResult value) {
        if (value == null) {
            return "<empty>";
        }
        return dateFormat.format(value.getLocalDate());
    }
}

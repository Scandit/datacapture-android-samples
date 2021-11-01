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

package com.scandit.datacapture.idcaptureextendedsample.mappers;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.ResultEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

abstract class ResultMapper {

    protected static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    protected final CapturedId capturedId;

    public ResultMapper(CapturedId capturedId) {
        this.capturedId = capturedId;
    }

    /*
     * We extract all the CapturedId's fields.
     */
    public ArrayList<ResultEntry> mapResult() {
        ArrayList<ResultEntry> result = new ArrayList<>();
        result.add(new ResultEntry("Result Type", extractField(capturedId.getCapturedResultType().toString())));
        result.add(new ResultEntry("Document Type", extractField(capturedId.getDocumentType().toString())));
        result.add(new ResultEntry("First Name", extractField(capturedId.getFirstName())));
        result.add(new ResultEntry("Last Name", extractField(capturedId.getLastName())));
        result.add(new ResultEntry("Full Name", extractField(capturedId.getFullName())));
        result.add(new ResultEntry("Sex", extractField(capturedId.getSex())));
        result.add(new ResultEntry("Date of Birth", extractField(capturedId.getDateOfBirth())));
        result.add(new ResultEntry("Nationality", extractField(capturedId.getNationality())));
        result.add(new ResultEntry("Address", extractField(capturedId.getAddress())));
        result.add(new ResultEntry("Issuing Country ISO", extractField(capturedId.getIssuingCountryIso())));
        result.add(new ResultEntry("Issuing Country", extractField(capturedId.getIssuingCountry())));
        result.add(new ResultEntry("Document Number", extractField(capturedId.getDocumentNumber())));
        result.add(new ResultEntry("Date of Expiry", extractField(capturedId.getDateOfExpiry())));
        result.add(new ResultEntry("Date of Issue", extractField(capturedId.getDateOfIssue())));
        return result;
    }

    @NonNull
    protected String extractField(boolean value) {
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
        return dateFormat.format(value.toDate());
    }
}

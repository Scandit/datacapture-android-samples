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

package com.scandit.datacapture.usdlverificationsample.ui.result;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.IdImageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * A helper class to convert the data captured from a driver's license to a result entity that
 * will be used to present captured data in the result screen.
 */
public class ResultMapper {

    protected static final DateFormat dateFormat;

    static {
        /*
         * DateResult::toDate() returns dates in UTC. We need to use the same timezone for
         * formatting, otherwise we may end up with a wrong date displayed if our local timezone
         * is a day behind/ahead from UTC.
         */
        dateFormat = SimpleDateFormat.getDateInstance();
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    protected final CapturedId capturedId;

    public ResultMapper(CapturedId capturedId) {
        this.capturedId = capturedId;
    }

    /**
     * We extract all the CapturedId's fields.
     */
    public CaptureResult mapResult(
            boolean isExpired,
            boolean isFrontBackComparisonSuccessful,
            boolean isCloudVerificationSuccessful
    ) {
        /*
         * Extract and convert the document face image.
         */
        byte[] faceImageBytes = null;
        Bitmap faceImage = capturedId.getImageBitmapForType(IdImageType.FACE);
        if (faceImage != null) {
            faceImageBytes = convertImageToBytes(faceImage);
        }

        List<CaptureResult.Entry> entries = getBaseEntries();

        return new CaptureResult(
                entries,
                faceImageBytes,
                isExpired,
                isFrontBackComparisonSuccessful,
                isCloudVerificationSuccessful
        );
    }

    /**
     * Extract base fields from the captured driver's license.
     */
    private List<CaptureResult.Entry> getBaseEntries() {
        List<CaptureResult.Entry> entries = new ArrayList<>();

        addFieldIfNotNull(entries, "Full Name", extractField(capturedId.getFullName()));
        addFieldIfNotNull(entries, "Sex", extractField(capturedId.getSex()));
        addFieldIfNotNull(entries, "Nationality", extractField(capturedId.getNationality()));
        addFieldIfNotNull(entries, "Date of Birth", extractField(capturedId.getDateOfBirth()));
        addFieldIfNotNull(entries, "Address", extractField(capturedId.getAddress()));
        addFieldIfNotNull(entries, "Document Number", extractField(capturedId.getDocumentNumber()));
        addFieldIfNotNull(entries, "Date of Expiry", extractField(capturedId.getDateOfExpiry()));
        addFieldIfNotNull(entries, "Issuing Country", extractField(capturedId.getIssuingCountry()));
        addFieldIfNotNull(entries, "Date of Issue", extractField(capturedId.getDateOfIssue()));

        return entries;
    }

    /**
     * Create a result entry and add it to list of entries if the passed value is not null.
     */
    protected void addFieldIfNotNull(
            List<CaptureResult.Entry> entries,
            String key,
            @Nullable String value
    ) {
        if (value != null) {
            entries.add(new CaptureResult.Entry(key, value));
        }
    }

    /**
     * Extract and return a text value if the field is not empty.
     */
    @Nullable
    protected String extractField(@Nullable String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        return value;
    }

    /**
     * Extract a date from a DateResult object and format it to a printable value.
     */
    @Nullable
    protected String extractField(@Nullable DateResult value) {
        if (value == null) {
            return null;
        }
        return dateFormat.format(value.toDate());
    }

    /**
     * Convert a Bitmap image to a byte array.
     */
    private static byte[] convertImageToBytes(Bitmap image) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            return byteArray;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }
}

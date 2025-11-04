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

import com.scandit.datacapture.id.capture.IdCaptureRegions;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.IdCaptureRegion;
import com.scandit.datacapture.id.data.IdImages;
import com.scandit.datacapture.id.data.RejectionReason;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class to convert the data captured from a driver's license to a result entity that
 * will be used to present captured data in the result screen.
 */
public class ResultMapper {

    protected static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();

    public static final String EMPTY_TEXT_VALUE = "<empty>";

    protected final CapturedId capturedId;

    public ResultMapper(CapturedId capturedId) {
        this.capturedId = capturedId;
    }

    /**
     * We extract all the CapturedId's fields.
     */
    public CaptureResult mapResult(
        @Nullable RejectionReason rejectionReason,
        @Nullable Bitmap frontReviewImage
    ) {
        /*
         * Extract and convert the images.
         */
        byte[] faceImageBytes = null;
        IdImages images = capturedId.getImages();
        Bitmap faceImage = images.getFace();
        if (faceImage != null) {
            faceImageBytes = convertImageToBytes(faceImage);
        }

        byte[] frontReviewImageBytes = null;
        if (frontReviewImage != null) {
            frontReviewImageBytes = convertImageToBytes(frontReviewImage);
        }

        List<CaptureResult.Entry> entries = getBaseEntries();

        return new CaptureResult(
                entries,
                faceImageBytes,
                rejectionReason,
                frontReviewImageBytes
        );
    }

    /**
     * Extract base fields from the captured driver's license.
     */
    private List<CaptureResult.Entry> getBaseEntries() {
        List<CaptureResult.Entry> entries = new ArrayList<>();

        addField(entries, "Full Name", extractField(capturedId.getFullName()));
        addField(entries, "Sex", extractField(capturedId.getSexType().toString()));
        addField(entries, "Nationality", extractField(capturedId.getNationality()));
        addField(entries, "Date of Birth", extractField(capturedId.getDateOfBirth()));
        addField(entries, "Address", extractField(capturedId.getAddress()));
        addField(entries, "Document Number", extractField(capturedId.getDocumentNumber()));
        addField(entries, "Document Additional Number",
                extractField(capturedId.getDocumentAdditionalNumber()));
        addField(entries, "Date of Expiry", extractField(capturedId.getDateOfExpiry()));
        addField(entries, "Issuing Country", IdCaptureRegions.getDescription(capturedId.getIssuingCountry()));
        addField(entries, "Date of Issue", extractField(capturedId.getDateOfIssue()));

        return entries;
    }

    /**
     * Create a result entry and add it to list of entries.
     */
    protected void addField(
            List<CaptureResult.Entry> entries,
            String key,
            @Nullable String value
    ) {
        entries.add(new CaptureResult.Entry(key, value));
    }

    /**
     * Extract and return a text value if the field is not empty.
     */
    protected String extractField(@Nullable String value) {
        if (TextUtils.isEmpty(value)) {
            return EMPTY_TEXT_VALUE;
        }
        return value;
    }

    /**
     * Extract a date from a DateResult object and format it to a printable value.
     */
    protected String extractField(@Nullable DateResult value) {
        if (value == null) {
            return EMPTY_TEXT_VALUE;
        }
        return dateFormat.format(value.getLocalDate());
    }

    /**
     * Extract a region and format it to a printable value.
     */
    protected String extractField(@Nullable IdCaptureRegion value) {
        if (value == null) {
            return EMPTY_TEXT_VALUE;
        }
        return value.toString();
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

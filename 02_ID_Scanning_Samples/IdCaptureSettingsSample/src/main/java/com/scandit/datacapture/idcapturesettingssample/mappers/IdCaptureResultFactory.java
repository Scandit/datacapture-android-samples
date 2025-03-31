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

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.IdImages;
import com.scandit.datacapture.id.data.IdSide;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public abstract class IdCaptureResultFactory {

    protected static final DateFormat shortDateFormat;

    static {
        /*
         * DateResult::toDate() returns dates in UTC. We need to use the same timezone for
         * formatting, otherwise we may end up with a wrong date displayed if our local timezone
         * is a day behind/ahead from UTC.
         */
        shortDateFormat = SimpleDateFormat.getDateInstance();
        shortDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /*
     * Extract all the CapturedId's fields for the full result screen.
     */
    public static CaptureResult extract(CapturedId capturedId) {
        /*
         * Extract all the CapturedId's common fields.
         */
        FieldExtractor commonFieldExtractor = new CommonFieldExtractor(capturedId);
        List<CaptureResult.Entry> commonEntries = commonFieldExtractor.extract();
        List<CaptureResult.Entry> entries = new ArrayList<>(commonEntries);

        /*
         * Extract all the CapturedId's type-specific fields.
         */
        for (FieldExtractor fieldExtractor : getFieldExtractors(capturedId)) {
            List<CaptureResult.Entry> typeEntries = fieldExtractor.extract();
            entries.addAll(typeEntries);
        }

        /*
         * Extract and convert the desired document images.
         */
        IdImages images = capturedId.getImages();
        byte[] faceImageBytes = null;
        byte[] idFrontImageBytes = null;
        byte[] idBackImageBytes = null;
        byte[] frameFrontImageBytes = null;
        byte[] frameBackImageBytes = null;

        Bitmap faceImage = images.getFace();
        if (faceImage != null) {
            faceImageBytes = convertImageToBytes(faceImage);
        }

        Bitmap idFrontImage = images.getCroppedDocument(IdSide.FRONT);
        if (idFrontImage != null) {
            idFrontImageBytes = convertImageToBytes(idFrontImage);
        }

        Bitmap idBackImage = images.getCroppedDocument(IdSide.BACK);
        if (idBackImage != null) {
            idBackImageBytes = convertImageToBytes(idBackImage);
        }

        Bitmap frameFrontImage = images.getFrame(IdSide.FRONT);
        if (frameFrontImage != null) {
            frameFrontImageBytes = convertImageToBytes(frameFrontImage);
        }

        Bitmap frameBackImage = images.getFrame(IdSide.BACK);
        if (frameBackImage != null) {
            frameBackImageBytes = convertImageToBytes(frameBackImage);
        }

        /*
         * Extract and format date of birth. This will be shown in continuous mode.
         */
        String dateOfBirth = "";
        if (capturedId.getDateOfBirth() != null) {
            dateOfBirth = shortDateFormat.format(capturedId.getDateOfBirth().getLocalDate());
        }

        /*
         * Extract the fullname. This will be shown in continuous mode.
         */
        String fullName = capturedId.getFullName();
        if (fullName.isEmpty()) {
            fullName = capturedId.getFirstName() + " " + capturedId.getLastName();
        }

        return new CaptureResult(
                entries,
                fullName,
                dateOfBirth,
                faceImageBytes,
                idFrontImageBytes,
                idBackImageBytes,
                frameFrontImageBytes,
                frameBackImageBytes
        );
    }

    @NonNull
    private static List<FieldExtractor> getFieldExtractors(CapturedId capturedId) {
        List<FieldExtractor> extractors = new ArrayList<>();

        if (capturedId.getViz() != null) {
            extractors.add(new VizFieldExtractor(capturedId));
        }
        if (capturedId.getMrz() != null) {
            extractors.add(new MrzFieldExtractor(capturedId));
        }
        if (capturedId.getBarcode() != null) {
            extractors.add(new BarcodeFieldExtractor(capturedId));
        }

        return extractors;
    }

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

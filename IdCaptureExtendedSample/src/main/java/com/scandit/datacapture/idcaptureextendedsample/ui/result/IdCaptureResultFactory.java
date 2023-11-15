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

package com.scandit.datacapture.idcaptureextendedsample.ui.result;

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

public abstract class IdCaptureResultFactory {

    private static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();

    /*
     * Extract all the CapturedId's fields for the full result screen.
     */
    public static CaptureResult extract(CapturedId capturedId) {
        /*
         * Extract all the CapturedId's common fields.
         */
        List<CaptureResult.Entry> entries = extractEntries(capturedId);

        /*
         * Extract and convert the desired document images.
         */
        byte[] idFrontImageBytes = null;
        Bitmap idFrontImage = capturedId.getImageBitmapForType(IdImageType.ID_FRONT);
        if (idFrontImage != null) {
            idFrontImageBytes = convertImageToBytes(idFrontImage);
        }

        byte[] idBackImageBytes = null;
        Bitmap idBackImage = capturedId.getImageBitmapForType(IdImageType.ID_BACK);
        if (idBackImage != null) {
            idBackImageBytes = convertImageToBytes(idBackImage);
        }

        return new CaptureResult(entries, idFrontImageBytes, idBackImageBytes);
    }

    /*
     * We extract all the common fields fields and add them to the ones from
     * CapturedId.
     */
    private static ArrayList<CaptureResult.Entry> extractEntries(CapturedId capturedId) {
        ArrayList<CaptureResult.Entry> result = new ArrayList<>();

        addField(result, "Full Name", capturedId.getFullName());
        addField(result, "Date of Birth", capturedId.getDateOfBirth());
        addField(result, "Date of Expiry", capturedId.getDateOfExpiry());
        addField(result, "Document Number", capturedId.getDocumentNumber());
        addField(result, "Nationality", capturedId.getNationality());

        return result;
    }

    private static void addField(
            ArrayList<CaptureResult.Entry> fields,
            String label,
            @Nullable String value
    ) {
        if (!TextUtils.isEmpty(value)) {
            fields.add(new CaptureResult.Entry(label, value));
        }
    }

    private static void addField(
            ArrayList<CaptureResult.Entry> fields,
            String label,
            @Nullable DateResult value
    ) {
        if (value != null) {
            fields.add(new CaptureResult.Entry(label, dateFormat.format(value.getLocalDate())));
        }
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

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

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.CaptureResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultMapper {

    protected static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    protected final CapturedId capturedId;

    public ResultMapper(CapturedId capturedId) {
        this.capturedId = capturedId;
    }
    
    public static ResultMapper create(CapturedId capturedId) {
        switch (capturedId.getCapturedResultType()) {
            case AAMVA_BARCODE_RESULT:
                return new AamvaResultMapper(capturedId);
            case CHINA_EXIT_ENTRY_PERMIT_MRZ_RESULT:
                return new ChinaExitEntryPermitMrzResultMapper(capturedId);
            case CHINA_MAINLAND_TRAVEL_PERMIT_MRZ_RESULT:
                return new ChinaMainlandTravelPermitMrzResultMapper(capturedId);
            case COLOMBIA_DL_BARCODE_RESULT:
                return new ColombiaDlResultMapper(capturedId);
            case COLOMBIA_ID_BARCODE_RESULT:
                return new ColombiaIdResultMapper(capturedId);
            case ARGENTINA_ID_BARCODE_RESULT:
                return new ArgentinaIdResultMapper(capturedId);
            case SOUTH_AFRICA_ID_BARCODE_RESULT:
                return new SouthAfricaIdResultMapper(capturedId);
            case SOUTH_AFRICA_DL_BARCODE_RESULT:
                return new SouthAfricaDlResultMapper(capturedId);
            case US_UNIFORMED_SERVICES_BARCODE_RESULT:
                return new UsUniformedServicesResultMapper(capturedId);
            case MRZ_RESULT:
                return new MrzResultMapper(capturedId);
            case VIZ_RESULT:
                return new VizResultMapper(capturedId);
            default:
                throw new AssertionError("Unknown captured result type: " + capturedId.getCapturedResultType());
        }
    }
    
    /*
     * We extract all the CapturedId's fields for the full result screen.
     */
    public CaptureResult mapResult() {
        /*
         * Extract and convert the desired document images.
         */
        byte[] faceImageBytes = null;
        Bitmap faceImage = capturedId.getImageBitmapForType(IdImageType.FACE);
        if (faceImage != null) {
            faceImageBytes = convertImageToBytes(faceImage);
        }

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

        List<CaptureResult.Entry> entries = extractFields();

        return new CaptureResult(
                entries,
                faceImageBytes,
                idFrontImageBytes,
                idBackImageBytes
        );
    }

    /*
     * We extract all the CapturedId's fields.
     */
    public ArrayList<CaptureResult.Entry> extractFields() {
        ArrayList<CaptureResult.Entry> result = new ArrayList<>();
        result.add(new CaptureResult.Entry("Result Type", extractField(capturedId.getCapturedResultType().toString())));
        result.add(new CaptureResult.Entry("Document Type", extractField(capturedId.getDocumentType().toString())));
        result.add(new CaptureResult.Entry("First Name", extractField(capturedId.getFirstName())));
        result.add(new CaptureResult.Entry("Last Name", extractField(capturedId.getLastName())));
        result.add(new CaptureResult.Entry("Full Name", extractField(capturedId.getFullName())));
        result.add(new CaptureResult.Entry("Sex", extractField(capturedId.getSex())));
        result.add(new CaptureResult.Entry("Date of Birth", extractField(capturedId.getDateOfBirth())));
        result.add(new CaptureResult.Entry("Nationality", extractField(capturedId.getNationality())));
        result.add(new CaptureResult.Entry("Address", extractField(capturedId.getAddress())));
        result.add(new CaptureResult.Entry("Issuing Country ISO", extractField(capturedId.getIssuingCountryIso())));
        result.add(new CaptureResult.Entry("Issuing Country", extractField(capturedId.getIssuingCountry())));
        result.add(new CaptureResult.Entry("Document Number", extractField(capturedId.getDocumentNumber())));
        result.add(new CaptureResult.Entry("Date of Expiry", extractField(capturedId.getDateOfExpiry())));
        result.add(new CaptureResult.Entry("Date of Issue", extractField(capturedId.getDateOfIssue())));
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

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
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ResultMapper {

    protected static final DateFormat shortDateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
    protected static final DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    protected final CapturedId capturedId;

    public static ResultMapper create(CapturedId capturedId) {
        switch (capturedId.getCapturedResultType()) {
            case AAMVA_BARCODE_RESULT:
                return new AamvaResultMapper(capturedId);
            case ARGENTINA_ID_BARCODE_RESULT:
                return new ArgentinaIdBarcodeResultMapper(capturedId);
            case COLOMBIA_ID_BARCODE_RESULT:
                return new ColombiaIdBarcodeResultMapper(capturedId);
            case MRZ_RESULT:
                return new MrzResultMapper(capturedId);
            case SOUTH_AFRICA_DL_BARCODE_RESULT:
                return new SouthAfricaDlBarcodeResultMapper(capturedId);
            case SOUTH_AFRICA_ID_BARCODE_RESULT:
                return new SouthAfricaIdBarcodeResultMapper(capturedId);
            case US_UNIFORMED_SERVICES_BARCODE_RESULT:
                return new UsUniformedServiceBarcodeResultMapper(capturedId);
            case VIZ_RESULT:
                return new VizResultMapper(capturedId);
            default:
                throw new AssertionError(
                        "Unsupported result type " + capturedId.getCapturedResultType());
        }
    }

    public ResultMapper(CapturedId capturedId) {
        this.capturedId = capturedId;
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

        List<CaptureResult.Entry> entries = getBaseEntries();
        entries.addAll(getSubtypeEntries());

        /*
         * Extract and format date of birth. This will be shown in continuous mode.
         */
        String dateOfBirth = "";
        if (capturedId.getDateOfBirth() != null) {
            dateOfBirth = shortDateFormat.format(capturedId.getDateOfBirth().toDate());
        }

        /*
         * Extract the fullname. This will be shown in continuous mode.
         */
        String fullname = capturedId.getFullName();
        if (fullname.isEmpty()) {
            fullname = capturedId.getFirstName() + " " + capturedId.getLastName();
        }

        return new CaptureResult(
                entries,
                fullname,
                dateOfBirth,
                faceImageBytes,
                idFrontImageBytes,
                idBackImageBytes
        );
    }

    private List<CaptureResult.Entry> getBaseEntries() {
        List<CaptureResult.Entry> entries = new ArrayList<>();

        entries.add(new CaptureResult.Entry("Result Type", extractField(capturedId.getCapturedResultType().toString())));
        entries.add(new CaptureResult.Entry("Document Type", extractField(capturedId.getDocumentType().toString())));
        entries.add(new CaptureResult.Entry("First Name", extractField(capturedId.getFirstName())));
        entries.add(new CaptureResult.Entry("Last Name", extractField(capturedId.getLastName())));
        entries.add(new CaptureResult.Entry("Full Name", extractField(capturedId.getFullName())));
        entries.add(new CaptureResult.Entry("Sex", extractField(capturedId.getSex())));
        entries.add(new CaptureResult.Entry("Date of Birth", extractField(capturedId.getDateOfBirth())));
        entries.add(new CaptureResult.Entry("Nationality", extractField(capturedId.getNationality())));
        entries.add(new CaptureResult.Entry("Address", extractField(capturedId.getAddress())));
        entries.add(new CaptureResult.Entry("Issuing Country ISO", extractField(capturedId.getIssuingCountryIso())));
        entries.add(new CaptureResult.Entry("Issuing Country", extractField(capturedId.getIssuingCountry())));
        entries.add(new CaptureResult.Entry("Document Number", extractField(capturedId.getDocumentNumber())));
        entries.add(new CaptureResult.Entry("Date of Expiry", extractField(capturedId.getDateOfExpiry())));
        entries.add(new CaptureResult.Entry("Date of Issue", extractField(capturedId.getDateOfIssue())));

        return entries;
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

    protected List<CaptureResult.Entry> getSubtypeEntries() {
        return Collections.emptyList();
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

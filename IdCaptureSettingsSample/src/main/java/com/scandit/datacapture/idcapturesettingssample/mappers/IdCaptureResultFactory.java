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
import com.scandit.datacapture.id.data.CapturedResultType;
import com.scandit.datacapture.id.data.IdImageType;
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
        for (CapturedResultType capturedResultType : capturedId.getCapturedResultTypes()) {
            FieldExtractor fieldExtractor = getFieldExtractor(capturedResultType, capturedId);
            List<CaptureResult.Entry> typeEntries = fieldExtractor.extract();
            entries.addAll(typeEntries);
        }

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

    @NonNull
    private static FieldExtractor getFieldExtractor(
            CapturedResultType capturedResultType, CapturedId capturedId
    ) {
        switch (capturedResultType) {
            case AAMVA_BARCODE_RESULT:
                return new AamvaFieldExtractor(capturedId);
            case ARGENTINA_ID_BARCODE_RESULT:
                return new ArgentinaIdBarcodeFieldExtractor(capturedId);
            case CHINA_EXIT_ENTRY_PERMIT_MRZ_RESULT:
                return new ChinaExitEntryPermitMrzFieldExtractor(capturedId);
            case CHINA_MAINLAND_TRAVEL_PERMIT_MRZ_RESULT:
                return new ChinaMainlandTravelPermitMrzFieldExtractor(capturedId);
            case CHINA_ONE_WAY_PERMIT_BACK_MRZ_RESULT:
                return new ChinaOneWayPermitBackMrzFieldExtractor(capturedId);
            case CHINA_ONE_WAY_PERMIT_FRONT_MRZ_RESULT:
                return new ChinaOneWayPermitFrontMrzFieldExtractor(capturedId);
            case COLOMBIA_DL_BARCODE_RESULT:
                return new ColombiaDlBarcodeFieldExtractor(capturedId);
            case COLOMBIA_ID_BARCODE_RESULT:
                return new ColombiaIdBarcodeFieldExtractor(capturedId);
            case MRZ_RESULT:
                return new MrzFieldExtractor(capturedId);
            case SOUTH_AFRICA_DL_BARCODE_RESULT:
                return new SouthAfricaDlBarcodeFieldExtractor(capturedId);
            case SOUTH_AFRICA_ID_BARCODE_RESULT:
                return new SouthAfricaIdBarcodeFieldExtractor(capturedId);
            case US_UNIFORMED_SERVICES_BARCODE_RESULT:
                return new UsUniformedServiceBarcodeFieldExtractor(capturedId);
            case VIZ_RESULT:
                return new VizFieldExtractor(capturedId);
            case APEC_BUSINESS_TRAVEL_CARD_MRZ_RESULT:
                return new ApecBusinessTravelCardMrzFieldExtractor(capturedId);
            case US_VISA_VIZ_RESULT:
                return new UsVisaVizFieldExtractor(capturedId);
            case COMMON_ACCESS_CARD_BARCODE_RESULT:
                return new CommonAccessCardBarcodeFieldExtractor(capturedId);
            default:
                throw new AssertionError(
                        "Unsupported result type " + capturedId.getCapturedResultType());
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

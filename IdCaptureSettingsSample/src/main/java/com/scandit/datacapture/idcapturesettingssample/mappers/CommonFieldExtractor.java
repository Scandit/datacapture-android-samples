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

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class CommonFieldExtractor extends FieldExtractor {

    public CommonFieldExtractor(CapturedId capturedId) {
        super(capturedId);
    }

    /*
     * We extract all the AamvaBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    protected List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Result Types", extractField(capturedId.getCapturedResultTypes().toString())));
        result.add(new CaptureResult.Entry("Document Type", extractField(capturedId.getDocumentType().toString())));
        result.add(new CaptureResult.Entry("First Name", extractField(capturedId.getFirstName())));
        result.add(new CaptureResult.Entry("Last Name", extractField(capturedId.getLastName())));
        result.add(new CaptureResult.Entry("Secondary Last Name", extractField(capturedId.getSecondaryLastName())));
        result.add(new CaptureResult.Entry("Full Name", extractField(capturedId.getFullName())));
        result.add(new CaptureResult.Entry("Sex", extractField(capturedId.getSex())));
        result.add(new CaptureResult.Entry("Date of Birth", extractField(capturedId.getDateOfBirth())));
        result.add(new CaptureResult.Entry("Age", extractField(capturedId.getAge())));
        result.add(new CaptureResult.Entry("Nationality", extractField(capturedId.getNationality())));
        result.add(new CaptureResult.Entry("Address", extractField(capturedId.getAddress())));
        result.add(new CaptureResult.Entry("Issuing Country ISO", extractField(capturedId.getIssuingCountryIso())));
        result.add(new CaptureResult.Entry("Issuing Country", extractField(capturedId.getIssuingCountry())));
        result.add(new CaptureResult.Entry("Document Number", extractField(capturedId.getDocumentNumber())));
        result.add(new CaptureResult.Entry("Document Additional Number", extractField(capturedId.getDocumentAdditionalNumber())));
        result.add(new CaptureResult.Entry("Date of Expiry", extractField(capturedId.getDateOfExpiry())));
        result.add(new CaptureResult.Entry("Is Expired", extractField(capturedId.isExpired())));
        result.add(new CaptureResult.Entry("Date of Issue", extractField(capturedId.getDateOfIssue())));

        return result;
    }
}

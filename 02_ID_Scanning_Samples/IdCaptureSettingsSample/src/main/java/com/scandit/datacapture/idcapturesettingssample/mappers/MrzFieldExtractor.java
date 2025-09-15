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
import com.scandit.datacapture.id.data.MrzResult;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.Arrays;
import java.util.List;

public final class MrzFieldExtractor extends FieldExtractor {

    private final MrzResult mrzResult;

    public MrzFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        mrzResult = capturedId.getMrz();
    }

    /*
     * We extract all the MrzResult's specific fields and add them to the ones from CapturedId.
     */
    @Override
    public List<CaptureResult.Entry> extract() {
        return Arrays.asList(
            new CaptureResult.Entry("MRZ First Name", extractField(mrzResult.getFirstName())),
            new CaptureResult.Entry("MRZ Last Name", extractField(mrzResult.getLastName())),
            new CaptureResult.Entry("MRZ Full Name", extractField(mrzResult.getFullName())),
            new CaptureResult.Entry("MRZ Sex", extractField(mrzResult.getSex())),
            new CaptureResult.Entry("MRZ Date of Birth", extractField(mrzResult.getDateOfBirth())),
            new CaptureResult.Entry("MRZ Nationality", extractField(mrzResult.getNationality())),
            new CaptureResult.Entry("MRZ Address", extractField(mrzResult.getAddress())),
            new CaptureResult.Entry("MRZ Document Number", extractField(mrzResult.getDocumentNumber())),
            new CaptureResult.Entry("MRZ Date of Expiry", extractField(mrzResult.getDateOfExpiry())),
            new CaptureResult.Entry("MRZ Date of Issue", extractField(mrzResult.getDateOfIssue())),
            new CaptureResult.Entry("Document Code", extractField(mrzResult.getDocumentCode())),
            new CaptureResult.Entry("Names are Truncated", extractField(mrzResult.getNamesAreTruncated())),
            new CaptureResult.Entry("Optional Data In Line 1", extractField(mrzResult.getOptionalDataInLine1())),
            new CaptureResult.Entry("Optional Data In Line 2", extractField(mrzResult.getOptionalDataInLine2())),
            new CaptureResult.Entry("Captured MRZ", extractField(mrzResult.getCapturedMrz().trim())),
            new CaptureResult.Entry("Personal ID Number", extractField(mrzResult.getPersonalIdNumber())),
            new CaptureResult.Entry("Passport Number", extractField(mrzResult.getPassportNumber())),
            new CaptureResult.Entry("Passport Issuer ISO", extractField(mrzResult.getPassportIssuerIso())),
            new CaptureResult.Entry("Passport Date of Expiry", extractField(mrzResult.getPassportDateOfExpiry())),
            new CaptureResult.Entry("Renewal Times", extractField(mrzResult.getRenewalTimes())),
            new CaptureResult.Entry("Full Name Simplified Chinese", extractField(mrzResult.getFullNameSimplifiedChinese())),
            new CaptureResult.Entry("Omitted Character Count in GBK Name", extractField(mrzResult.getOmittedCharacterCountInGbkName())),
            new CaptureResult.Entry("Omitted Name Count", extractField(mrzResult.getOmittedNameCount())),
            new CaptureResult.Entry("Issuing Authority Code", extractField(mrzResult.getIssuingAuthorityCode()))
        );
    }
}

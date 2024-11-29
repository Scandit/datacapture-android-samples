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

import java.util.ArrayList;
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
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Document Code", extractField(mrzResult.getDocumentCode())));
        result.add(new CaptureResult.Entry("Names are Truncated", extractField(mrzResult.getNamesAreTruncated())));
        result.add(new CaptureResult.Entry("Optional", extractField(mrzResult.getOptional())));
        result.add(new CaptureResult.Entry("Optional 1", extractField(mrzResult.getOptional1())));
        result.add(new CaptureResult.Entry("Captured MRZ", extractField(mrzResult.getCapturedMrz().trim())));
        result.add(new CaptureResult.Entry(
                "Personal ID Number", extractField(mrzResult.getPersonalIdNumber())));
        result.add(new CaptureResult.Entry(
                "Passport Number", extractField(mrzResult.getPassportNumber())));
        result.add(new CaptureResult.Entry(
                "Passport Issuer ISO", extractField(mrzResult.getPassportIssuerIso())));
        result.add(new CaptureResult.Entry(
                "Passport Date of Expiry", extractField(mrzResult.getPassportDateOfExpiry())));
        result.add(new CaptureResult.Entry(
                "Renewal Times", extractField(mrzResult.getRenewalTimes())));
        result.add(new CaptureResult.Entry("Full Name Simplified Chinese",
                extractField(mrzResult.getFullNameSimplifiedChinese())));
        result.add(new CaptureResult.Entry("Omitted Character Count in GBK Name",
                extractField(mrzResult.getOmittedCharacterCountInGbkName())));
        result.add(new CaptureResult.Entry(
                "Omitted Name Count", extractField(mrzResult.getOmittedNameCount())));
        result.add(new CaptureResult.Entry(
                "Issuing Authority Code", extractField(mrzResult.getIssuingAuthorityCode())));

        return result;
    }
}

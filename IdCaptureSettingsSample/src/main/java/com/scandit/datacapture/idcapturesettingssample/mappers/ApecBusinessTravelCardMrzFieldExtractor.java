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

import com.scandit.datacapture.id.data.ApecBusinessTravelCardMrzResult;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class ApecBusinessTravelCardMrzFieldExtractor extends FieldExtractor {

    private final ApecBusinessTravelCardMrzResult apacBusinessTravelCardMrzResult;

    public ApecBusinessTravelCardMrzFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        apacBusinessTravelCardMrzResult = capturedId.getApecBusinessTravelCardMrz();
    }

    /*
     * We extract all the ApecBusinessTravelCardMrzResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Document Code", extractField(
                apacBusinessTravelCardMrzResult.getDocumentCode())));
        result.add(new CaptureResult.Entry("Passport Number", extractField(
                apacBusinessTravelCardMrzResult.getPassportNumber())));
        result.add(new CaptureResult.Entry("Passport Issuer ISO", extractField(
                apacBusinessTravelCardMrzResult.getPassportIssuerIso())));
        result.add(new CaptureResult.Entry("Passport Date of Expiry", extractField(
                apacBusinessTravelCardMrzResult.getPassportDateOfExpiry())));
        result.add(new CaptureResult.Entry("Captured MRZ", extractField(
                apacBusinessTravelCardMrzResult.getCapturedMrz())));

        return result;
    }
}

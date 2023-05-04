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
import com.scandit.datacapture.id.data.ChinaMainlandTravelPermitMrzResult;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class ChinaMainlandTravelPermitMrzFieldExtractor extends FieldExtractor {

    private final ChinaMainlandTravelPermitMrzResult chinaMainlandTravelPermitMrzResult;

    public ChinaMainlandTravelPermitMrzFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        chinaMainlandTravelPermitMrzResult = capturedId.getChinaMainlandTravelPermitMrz();
    }

    /*
     * We extract all the ChinaMainlandTravelPermitMrzResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Document Code", extractField(
                chinaMainlandTravelPermitMrzResult.getDocumentCode())));
        result.add(new CaptureResult.Entry("Captured MRZ", extractField(
                chinaMainlandTravelPermitMrzResult.getCapturedMrz())));
        result.add(new CaptureResult.Entry("Personal ID Number", extractField(
                chinaMainlandTravelPermitMrzResult.getPersonalIdNumber())));
        result.add(new CaptureResult.Entry("Renewal Times", extractField(
                chinaMainlandTravelPermitMrzResult.getRenewalTimes())));
        result.add(new CaptureResult.Entry("Full Name Simplified Chinese", extractField(
                chinaMainlandTravelPermitMrzResult.getFullNameSimplifiedChinese())));
        result.add(new CaptureResult.Entry("Omitted Character Count in GBK Name", extractField(
                chinaMainlandTravelPermitMrzResult.getOmittedCharacterCountInGBKName())));
        result.add(new CaptureResult.Entry("Omitted Name Count", extractField(
                chinaMainlandTravelPermitMrzResult.getOmittedNameCount())));
        result.add(new CaptureResult.Entry("Issuing Authority Code", extractField(
                chinaMainlandTravelPermitMrzResult.getIssuingAuthorityCode())));

        return result;
    }
}

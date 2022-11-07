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

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.ChinaMainlandTravelPermitMrzResult;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.CaptureResult;

import java.util.ArrayList;

public final class ChinaMainlandTravelPermitMrzResultMapper extends ResultMapper {

    private final ChinaMainlandTravelPermitMrzResult result;

    public ChinaMainlandTravelPermitMrzResultMapper(CapturedId capturedId) {
        super(capturedId);
        result = capturedId.getChinaMainlandTravelPermitMrz();
    }

    /*
     * We extract all the ChinaMainlandTravelPermitMrzResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public ArrayList<CaptureResult.Entry> extractFields() {
        ArrayList<CaptureResult.Entry> result = super.extractFields();

        result.add(new CaptureResult.Entry("Document Code", extractField(
                this.result.getDocumentCode())));
        result.add(new CaptureResult.Entry("Captured MRZ", extractField(
                this.result.getCapturedMrz())));
        result.add(new CaptureResult.Entry("Personal ID Number", extractField(
                this.result.getPersonalIdNumber())));
        result.add(new CaptureResult.Entry("Renewal Times", extractField(
                this.result.getRenewalTimes())));
        result.add(new CaptureResult.Entry("GBK Name", extractField(
                this.result.getGbkName())));
        result.add(new CaptureResult.Entry("Omitted Character Count in GBK Name", extractField(
                this.result.getOmittedCharacterCountInGBKName())));
        result.add(new CaptureResult.Entry("Omitted Name Count", extractField(
                this.result.getOmittedNameCount())));
        result.add(new CaptureResult.Entry("Issuing Authority Code", extractField(
                this.result.getIssuingAuthorityCode())));

        return result;
    }
}

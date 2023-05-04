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
import com.scandit.datacapture.id.data.VizResult;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.CaptureResult;

import java.util.ArrayList;

public final class VizFieldExtractor extends FieldExtractor {

    private final VizResult result;

    public VizFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        result = capturedId.getViz();
    }

    /*
     * We extract all the VizResult's specific fields and add them to the ones from CapturedId.
     */
    @Override
    public ArrayList<CaptureResult.Entry> extract() {
        ArrayList<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Issuing Authority", extractField(this.result.getIssuingAuthority())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction", extractField(this.result.getIssuingJurisdiction())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction ISO", extractField(this.result.getIssuingJurisdictionIso())));
        result.add(new CaptureResult.Entry("Additional Name Information", extractField(this.result.getAdditionalNameInformation())));
        result.add(new CaptureResult.Entry("Additional Address Information", extractField(this.result.getAdditionalAddressInformation())));
        result.add(new CaptureResult.Entry("Place of Birth", extractField(this.result.getPlaceOfBirth())));
        result.add(new CaptureResult.Entry("Race", extractField(this.result.getRace())));
        result.add(new CaptureResult.Entry("Religion", extractField(this.result.getReligion())));
        result.add(new CaptureResult.Entry("Profession", extractField(this.result.getProfession())));
        result.add(new CaptureResult.Entry("Marital Status", extractField(this.result.getMaritalStatus())));
        result.add(new CaptureResult.Entry("Residential Status", extractField(this.result.getResidentialStatus())));
        result.add(new CaptureResult.Entry("Employer", extractField(this.result.getEmployer())));
        result.add(new CaptureResult.Entry("Personal Id Number", extractField(this.result.getPersonalIdNumber())));
        result.add(new CaptureResult.Entry("Document Additional Number", extractField(this.result.getDocumentAdditionalNumber())));

        return result;
    }
}


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
import com.scandit.datacapture.idcaptureextendedsample.ui.result.ResultEntry;

import java.util.ArrayList;

public final class VizResultMapper extends ResultMapper {

    private final VizResult result;

    public VizResultMapper(CapturedId capturedId) {
        super(capturedId);
        result = capturedId.getViz();
    }

    /*
     * We extract all the VizResult's specific fields and add them to the ones from CapturedId.
     */
    @Override
    public ArrayList<ResultEntry> mapResult() {
        ArrayList<ResultEntry> result = super.mapResult();

        result.add(new ResultEntry("Issuing Authority", extractField(this.result.getIssuingAuthority())));
        result.add(new ResultEntry("Issuing Jurisdiction", extractField(this.result.getIssuingJurisdiction())));
        result.add(new ResultEntry("Issuing Jurisdiction ISO", extractField(this.result.getIssuingJurisdictionIso())));
        result.add(new ResultEntry("Additional Name Information", extractField(this.result.getAdditionalNameInformation())));
        result.add(new ResultEntry("Additional Address Information", extractField(this.result.getAdditionalAddressInformation())));
        result.add(new ResultEntry("Place of Birth", extractField(this.result.getPlaceOfBirth())));
        result.add(new ResultEntry("Race", extractField(this.result.getRace())));
        result.add(new ResultEntry("Religion", extractField(this.result.getReligion())));
        result.add(new ResultEntry("Profession", extractField(this.result.getProfession())));
        result.add(new ResultEntry("Marital Status", extractField(this.result.getMaritalStatus())));
        result.add(new ResultEntry("Residential Status", extractField(this.result.getResidentialStatus())));
        result.add(new ResultEntry("Employer", extractField(this.result.getEmployer())));
        result.add(new ResultEntry("Personal Id Number", extractField(this.result.getPersonalIdNumber())));
        result.add(new ResultEntry("Document Additional Number", extractField(this.result.getDocumentAdditionalNumber())));

        return result;
    }
}


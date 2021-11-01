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

    private final VizResult vizResult;

    public VizResultMapper(CapturedId capturedId) {
        super(capturedId);
        vizResult = capturedId.getViz();
    }

    /*
     * We extract all the VizResult's specific fields and add them to the ones from CapturedId.
     */
    @Override
    public ArrayList<ResultEntry> mapResult() {
        ArrayList<ResultEntry> result = super.mapResult();

        result.add(new ResultEntry("Issuing Authority", extractField(vizResult.getIssuingAuthority())));
        result.add(new ResultEntry("Issuing Jurisdiction", extractField(vizResult.getIssuingJurisdiction())));
        result.add(new ResultEntry("Issuing Jurisdiction ISO", extractField(vizResult.getIssuingJurisdictionIso())));
        result.add(new ResultEntry("Additional Name Information", extractField(vizResult.getAdditionalNameInformation())));
        result.add(new ResultEntry("Additional Address Information", extractField(vizResult.getAdditionalAddressInformation())));
        result.add(new ResultEntry("Place of Birth", extractField(vizResult.getPlaceOfBirth())));
        result.add(new ResultEntry("Race", extractField(vizResult.getRace())));
        result.add(new ResultEntry("Religion", extractField(vizResult.getReligion())));
        result.add(new ResultEntry("Profession", extractField(vizResult.getProfession())));
        result.add(new ResultEntry("Marital Status", extractField(vizResult.getMaritalStatus())));
        result.add(new ResultEntry("Residential Status", extractField(vizResult.getResidentialStatus())));
        result.add(new ResultEntry("Employer", extractField(vizResult.getEmployer())));
        result.add(new ResultEntry("Personal Id Number", extractField(vizResult.getPersonalIdNumber())));
        result.add(new ResultEntry("Document Additional Number", extractField(vizResult.getDocumentAdditionalNumber())));

        return result;
    }
}


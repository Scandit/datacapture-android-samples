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

package com.scandit.datacapture.idcapturesettingssample.mappers;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.VizResult;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class VizFieldExtractor extends FieldExtractor {

    private final VizResult vizResult;

    public VizFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        vizResult = capturedId.getViz();
    }

    /*
     * We extract all the VizResult's specific fields and add them to the ones from CapturedId.
     */
    @Override
    protected List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Issuing Authority", extractField(vizResult.getIssuingAuthority())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction", extractField(vizResult.getIssuingJurisdiction())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction ISO", extractField(vizResult.getIssuingJurisdictionIso())));
        result.add(new CaptureResult.Entry("Additional Name Information", extractField(vizResult.getAdditionalNameInformation())));
        result.add(new CaptureResult.Entry("Additional Address Information", extractField(vizResult.getAdditionalAddressInformation())));
        result.add(new CaptureResult.Entry("Place of Birth", extractField(vizResult.getPlaceOfBirth())));
        result.add(new CaptureResult.Entry("Race", extractField(vizResult.getRace())));
        result.add(new CaptureResult.Entry("Religion", extractField(vizResult.getReligion())));
        result.add(new CaptureResult.Entry("Profession", extractField(vizResult.getProfession())));
        result.add(new CaptureResult.Entry("Marital Status", extractField(vizResult.getMaritalStatus())));
        result.add(new CaptureResult.Entry("Residential Status", extractField(vizResult.getResidentialStatus())));
        result.add(new CaptureResult.Entry("Employer", extractField(vizResult.getEmployer())));
        result.add(new CaptureResult.Entry("Personal Id Number", extractField(vizResult.getPersonalIdNumber())));
        result.add(new CaptureResult.Entry("Document Additional Number", extractField(vizResult.getDocumentAdditionalNumber())));

        return result;
    }
}


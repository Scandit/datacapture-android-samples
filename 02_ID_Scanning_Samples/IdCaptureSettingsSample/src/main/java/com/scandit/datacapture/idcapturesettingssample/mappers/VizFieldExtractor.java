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

import androidx.annotation.Nullable;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DrivingLicenseCategory;
import com.scandit.datacapture.id.data.DrivingLicenseDetails;
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

        result.add(new CaptureResult.Entry("VIZ First Name", extractField(vizResult.getFirstName())));
        result.add(new CaptureResult.Entry("VIZ Last Name", extractField(vizResult.getLastName())));
        result.add(new CaptureResult.Entry("VIZ Secondary Last Name", extractField(vizResult.getSecondaryLastName())));
        result.add(new CaptureResult.Entry("VIZ Full Name", extractField(vizResult.getFullName())));
        result.add(new CaptureResult.Entry("VIZ Sex", extractField(vizResult.getSex())));
        result.add(new CaptureResult.Entry("VIZ Date of Birth", extractField(vizResult.getDateOfBirth())));
        result.add(new CaptureResult.Entry("VIZ Nationality", extractField(vizResult.getNationality())));
        result.add(new CaptureResult.Entry("VIZ Address", extractField(vizResult.getAddress())));
        result.add(new CaptureResult.Entry("VIZ Document Number", extractField(vizResult.getDocumentNumber())));
        result.add(new CaptureResult.Entry("VIZ Date of Expiry", extractField(vizResult.getDateOfExpiry())));
        result.add(new CaptureResult.Entry("VIZ Date of Issue", extractField(vizResult.getDateOfIssue())));
        result.add(new CaptureResult.Entry("Additional Name Information", extractField(vizResult.getAdditionalNameInformation())));
        result.add(new CaptureResult.Entry("Additional Address Information", extractField(vizResult.getAdditionalAddressInformation())));
        result.add(new CaptureResult.Entry("Place of Birth", extractField(vizResult.getPlaceOfBirth())));
        result.add(new CaptureResult.Entry("Race", extractField(vizResult.getRace())));
        result.add(new CaptureResult.Entry("Religion", extractField(vizResult.getReligion())));
        result.add(new CaptureResult.Entry("Profession", extractField(vizResult.getProfession())));
        result.add(new CaptureResult.Entry("Marital Status", extractField(vizResult.getMaritalStatus())));
        result.add(new CaptureResult.Entry("Residential Status", extractField(vizResult.getResidentialStatus())));
        result.add(new CaptureResult.Entry("Employer", extractField(vizResult.getEmployer())));
        result.add(new CaptureResult.Entry("Personal ID Number", extractField(vizResult.getPersonalIdNumber())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction", extractField(vizResult.getIssuingJurisdiction())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction ISO", extractField(vizResult.getIssuingJurisdictionIso())));
        result.add(new CaptureResult.Entry("Issuing Authority", extractField(vizResult.getIssuingAuthority())));
        result.add(new CaptureResult.Entry("Blood Type", extractField(vizResult.getBloodType())));
        result.add(new CaptureResult.Entry("Sponsor", extractField(vizResult.getSponsor())));
        result.add(new CaptureResult.Entry("Mother's name", extractField(vizResult.getMothersName())));
        result.add(new CaptureResult.Entry("Father's name", extractField(vizResult.getFathersName())));
        result.add(new CaptureResult.Entry("Captured Sides", vizResult.getCapturedSides().toString()));
        result.add(new CaptureResult.Entry("Backside Supported", extractField(vizResult.isBackSideCaptureSupported())));
        result.add(new CaptureResult.Entry("Visa Number", extractField(vizResult.getVisaNumber())));
        result.add(new CaptureResult.Entry("Passport Number", extractField(vizResult.getPassportNumber())));
        result.add(new CaptureResult.Entry("Vehicle Owner", extractField(vizResult.getVehicleOwner())));

        if (capturedId.getDocument() != null && capturedId.getDocument().isDriverLicense()) {
            result.add(new CaptureResult.Entry("Driver's License Details", extractField(vizResult.getDrivingLicenseDetails())));
        }

        return result;
    }

    private String extractField(@Nullable DrivingLicenseDetails drivingLicenseDetails) {
        if (drivingLicenseDetails == null) {
            return EMPTY_TEXT_VALUE;
        }

        StringBuilder result = new StringBuilder();
        List<DrivingLicenseCategory> drivingLicenseCategories =
                drivingLicenseDetails.getDrivingLicenseCategories();

        result.append("Categories:\n");
        if (drivingLicenseCategories.isEmpty()) {
            result.append(EMPTY_TEXT_VALUE);
        } else {
            for (int i = 0; i < drivingLicenseCategories.size(); i++) {
                DrivingLicenseCategory category = drivingLicenseCategories.get(i);
                result.append(extractField(category));

                if (i < drivingLicenseCategories.size() - 1) {
                    result.append("\n\n");
                }
            }
        }

        result.append("\n\nRestrictions: ");
        result.append(extractField(drivingLicenseDetails.getRestrictions()));

        result.append("\n\nEndorsements: ");
        result.append(extractField(drivingLicenseDetails.getEndorsements()));

        return result.toString();
    }

    private String extractField(DrivingLicenseCategory category) {
        String result = "";
        result += "Code: " + extractField(category.getCode()) + "\n";
        result += "Date of Issue: " + extractField(category.getDateOfIssue()) + "\n";
        result += "Date of Expiry: " + extractField(category.getDateOfExpiry());
        return result;
    }
}


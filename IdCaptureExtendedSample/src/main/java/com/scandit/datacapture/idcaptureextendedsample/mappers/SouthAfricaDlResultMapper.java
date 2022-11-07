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
import com.scandit.datacapture.id.data.ProfessionalDrivingPermit;
import com.scandit.datacapture.id.data.SouthAfricaDlBarcodeResult;
import com.scandit.datacapture.id.data.VehicleRestriction;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class SouthAfricaDlResultMapper extends ResultMapper {

    private final SouthAfricaDlBarcodeResult result;

    public SouthAfricaDlResultMapper(CapturedId capturedId) {
        super(capturedId);
        result = capturedId.getSouthAfricaDlBarcode();
    }

    /*
     * We extract all the SouthAfricaDlBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public ArrayList<CaptureResult.Entry> extractFields() {
        ArrayList<CaptureResult.Entry> result = super.extractFields();

        result.add(new CaptureResult.Entry("Personal Id Number", extractField(this.result.getPersonalIdNumber())));
        result.add(new CaptureResult.Entry("Personal Id Number Type", extractField(this.result.getPersonalIdNumberType())));
        result.add(new CaptureResult.Entry("Document Copy", extractField(this.result.getDocumentCopy())));
        result.add(new CaptureResult.Entry("License Country of Issue", extractField(this.result.getLicenseCountryOfIssue())));
        result.add(new CaptureResult.Entry("Driver Restriction Codes", extractIntFields(this.result.getDriverRestrictionCodes())));
        result.add(new CaptureResult.Entry("Professional Driving Permit", extractField(this.result.getProfessionalDrivingPermit())));
        result.add(new CaptureResult.Entry("Vehicle Restrictions", extractField(this.result.getVehicleRestrictions())));
        result.add(new CaptureResult.Entry("Version", extractField(this.result.getVersion())));

        return result;
    }

    private String extractField(ProfessionalDrivingPermit value) {
        if (value == null) {
            return "<empty>";
        }
        String result = "";
        result += "Codes: " + extractStringFields(value.getCodes()) + "\n";
        result += "Date of Expiry: " + extractField(value.getDateOfExpiry());
        return result;
    }

    private String extractIntFields(List<Integer> values) {
        if (values.isEmpty()) {
            return "<empty>";
        }
        StringBuilder result = new StringBuilder();
        for (Integer value : values) {
            result.append(extractField(value)).append(", ");
        }
        return result.toString();
    }

    private String extractStringFields(List<String> values) {
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            result.append(value).append(", ");
        }
        return result.toString();
    }

    private String extractField(List<VehicleRestriction> values) {
        if (values.isEmpty()) {
            return "<empty>";
        }
        StringBuilder result = new StringBuilder();
        for (VehicleRestriction restriction : values) {
            result.append(extractField(restriction)).append("\n");
        }
        return result.toString();
    }

    private String extractField(VehicleRestriction value) {
        String result = "";
        result += "Vehicle Code: " + extractField(value.getVehicleCode()) + "\n";
        result += "Vehicle Restriction: " + extractField(value.getVehicleRestriction()) + "\n";
        result += "Date of Issue: " + extractField(value.getDateOfIssue());
        return result;
    }
}

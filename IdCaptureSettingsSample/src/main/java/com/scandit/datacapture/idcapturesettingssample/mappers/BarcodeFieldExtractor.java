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

import com.scandit.datacapture.id.data.BarcodeResult;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.ProfessionalDrivingPermit;
import com.scandit.datacapture.id.data.VehicleRestriction;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class BarcodeFieldExtractor extends FieldExtractor {

    private final BarcodeResult barcodeResult;

    public BarcodeFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        barcodeResult = capturedId.getBarcode();
    }

    /*
     * We extract all the AamvaBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    protected List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();
        result.add(new CaptureResult.Entry("AAMVA Version", extractField(barcodeResult.getAamvaVersion())));
        result.add(new CaptureResult.Entry("Alias Family Name", extractField(barcodeResult.getAliasFamilyName())));
        result.add(new CaptureResult.Entry("Alias Given Name", extractField(barcodeResult.getAliasGivenName())));
        result.add(new CaptureResult.Entry("Alias Suffix Name", extractField(barcodeResult.getAliasSuffixName())));
        result.add(new CaptureResult.Entry("Blood Type", extractField(barcodeResult.getBloodType())));
        result.add(new CaptureResult.Entry("Branch Of Service", extractField(barcodeResult.getBranchOfService())));
        result.add(new CaptureResult.Entry("Card Instance Identifier", extractField(barcodeResult.getCardInstanceIdentifier())));
        result.add(new CaptureResult.Entry("Card Revision Date", extractField(barcodeResult.getCardRevisionDate())));
        result.add(new CaptureResult.Entry("Categories", extractStringFields(barcodeResult.getCategories())));
        result.add(new CaptureResult.Entry("Champus Effective Date", extractField(barcodeResult.getChampusEffectiveDate())));
        result.add(new CaptureResult.Entry("Champus Expiry Date", extractField(barcodeResult.getChampusExpiryDate())));
        result.add(new CaptureResult.Entry("Citizenship Status", extractField(barcodeResult.getCitizenshipStatus())));
        result.add(new CaptureResult.Entry("Civilian Health Care Flag Code", extractField(barcodeResult.getCivilianHealthCareFlagCode())));
        result.add(new CaptureResult.Entry("Civilian Health Care Flag Description", extractField(barcodeResult.getCivilianHealthCareFlagDescription())));
        result.add(new CaptureResult.Entry("Commissary Flag Code", extractField(barcodeResult.getCommissaryFlagCode())));
        result.add(new CaptureResult.Entry("Commissary Flag Description", extractField(barcodeResult.getCommissaryFlagDescription())));
        result.add(new CaptureResult.Entry("Country Of Birth", extractField(barcodeResult.getCountryOfBirth())));
        result.add(new CaptureResult.Entry("Country Of Birth Iso", extractField(barcodeResult.getCountryOfBirthIso())));
        result.add(new CaptureResult.Entry("Deers Dependent Suffix Code", extractField(barcodeResult.getDeersDependentSuffixCode())));
        result.add(new CaptureResult.Entry("Deers Dependent Suffix Description", extractField(barcodeResult.getDeersDependentSuffixDescription())));
        result.add(new CaptureResult.Entry("Direct Care Flag Code", extractField(barcodeResult.getDirectCareFlagCode())));
        result.add(new CaptureResult.Entry("Direct Care Flag Description", extractField(barcodeResult.getDirectCareFlagDescription())));
        result.add(new CaptureResult.Entry("Document Copy", extractField(barcodeResult.getDocumentCopy())));
        result.add(new CaptureResult.Entry("Document Discriminator Number", extractField(barcodeResult.getDocumentDiscriminatorNumber())));
        result.add(new CaptureResult.Entry("Driver Name Prefix", extractField(barcodeResult.getDriverNamePrefix())));
        result.add(new CaptureResult.Entry("Driver Name Suffix", extractField(barcodeResult.getDriverNameSuffix())));
        result.add(new CaptureResult.Entry("Driver Restriction Codes", extractIntFields(barcodeResult.getDriverRestrictionCodes())));
        result.add(new CaptureResult.Entry("Edi Person Identifier", extractField(barcodeResult.getEdiPersonIdentifier())));
        result.add(new CaptureResult.Entry("Endorsements Code", extractField(barcodeResult.getEndorsementsCode())));
        result.add(new CaptureResult.Entry("Exchange Flag Code", extractField(barcodeResult.getExchangeFlagCode())));
        result.add(new CaptureResult.Entry("Exchange Flag Description", extractField(barcodeResult.getExchangeFlagDescription())));
        result.add(new CaptureResult.Entry("Eye Color", extractField(barcodeResult.getEyeColor())));
        result.add(new CaptureResult.Entry("Family Sequence Number", extractField(barcodeResult.getFamilySequenceNumber())));
        result.add(new CaptureResult.Entry("First Name Truncation", extractField(barcodeResult.getFirstNameTruncation())));
        result.add(new CaptureResult.Entry("First Name Without Middle Name", extractField(barcodeResult.getFirstNameWithoutMiddleName())));
        result.add(new CaptureResult.Entry("Form Number", extractField(barcodeResult.getFormNumber())));
        result.add(new CaptureResult.Entry("Geneva Convention Category", extractField(barcodeResult.getGenevaConventionCategory())));
        result.add(new CaptureResult.Entry("Hair Color", extractField(barcodeResult.getHairColor())));
        result.add(new CaptureResult.Entry("Height Cm", extractField(barcodeResult.getHeightCm())));
        result.add(new CaptureResult.Entry("Height Inch", extractField(barcodeResult.getHeightInch())));
        result.add(new CaptureResult.Entry("Iin", extractField(barcodeResult.getIin())));
        result.add(new CaptureResult.Entry("Identification Type", extractField(barcodeResult.getIdentificationType())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction", extractField(barcodeResult.getIssuingJurisdiction())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction Iso", extractField(barcodeResult.getIssuingJurisdictionIso())));
        result.add(new CaptureResult.Entry("Jurisdiction Version", extractField(barcodeResult.getJurisdictionVersion())));
        result.add(new CaptureResult.Entry("Last Name Truncation", extractField(barcodeResult.getLastNameTruncation())));
        result.add(new CaptureResult.Entry("License Country Of Issue", extractField(barcodeResult.getLicenseCountryOfIssue())));
        result.add(new CaptureResult.Entry("Middle Name", extractField(barcodeResult.getMiddleName())));
        result.add(new CaptureResult.Entry("Middle Name Truncation", extractField(barcodeResult.getMiddleNameTruncation())));
        result.add(new CaptureResult.Entry("Mwr Flag Code", extractField(barcodeResult.getMwrFlagCode())));
        result.add(new CaptureResult.Entry("Mwr Flag Description", extractField(barcodeResult.getMwrFlagDescription())));
        result.add(new CaptureResult.Entry("Pay Grade", extractField(barcodeResult.getPayGrade())));
        result.add(new CaptureResult.Entry("Pay Plan Code", extractField(barcodeResult.getPayPlanCode())));
        result.add(new CaptureResult.Entry("Pay Plan Grade Code", extractField(barcodeResult.getPayPlanGradeCode())));
        result.add(new CaptureResult.Entry("Person Designator Document", extractField(barcodeResult.getPersonDesignatorDocument())));
        result.add(new CaptureResult.Entry("Person Designator Type Code", extractField(barcodeResult.getPersonDesignatorTypeCode())));
        result.add(new CaptureResult.Entry("Person Middle Initial", extractField(barcodeResult.getPersonMiddleInitial())));
        result.add(new CaptureResult.Entry("Personal Id Number", extractField(barcodeResult.getPersonalIdNumber())));
        result.add(new CaptureResult.Entry("Personal Id Number Type", extractField(barcodeResult.getPersonalIdNumberType())));
        result.add(new CaptureResult.Entry("Personnel Category Code", extractField(barcodeResult.getPersonnelCategoryCode())));
        result.add(new CaptureResult.Entry("Personnel Entitlement Condition Type", extractField(barcodeResult.getPersonnelEntitlementConditionType())));
        result.add(new CaptureResult.Entry("Place Of Birth", extractField(barcodeResult.getPlaceOfBirth())));
        result.add(new CaptureResult.Entry("Race", extractField(barcodeResult.getRace())));
        result.add(new CaptureResult.Entry("Rank", extractField(barcodeResult.getRank())));
        result.add(new CaptureResult.Entry("Raw Data", extractField(barcodeResult.getRawData())));
        result.add(new CaptureResult.Entry("Relationship Code", extractField(barcodeResult.getRelationshipCode())));
        result.add(new CaptureResult.Entry("Relationship Description", extractField(barcodeResult.getRelationshipDescription())));
        result.add(new CaptureResult.Entry("Restrictions Code", extractField(barcodeResult.getRestrictionsCode())));
        result.add(new CaptureResult.Entry("Security Code", extractField(barcodeResult.getSecurityCode())));
        result.add(new CaptureResult.Entry("Service Code", extractField(barcodeResult.getServiceCode())));
        result.add(new CaptureResult.Entry("Sponsor Flag", extractField(barcodeResult.getSponsorFlag())));
        result.add(new CaptureResult.Entry("Sponsor Name", extractField(barcodeResult.getSponsorName())));
        result.add(new CaptureResult.Entry("Sponsor Person Designator Identifier", extractField(barcodeResult.getSponsorPersonDesignatorIdentifier())));
        result.add(new CaptureResult.Entry("Status Code", extractField(barcodeResult.getStatusCode())));
        result.add(new CaptureResult.Entry("Status Code Description", extractField(barcodeResult.getStatusCodeDescription())));
        result.add(new CaptureResult.Entry("Vehicle Class", extractField(barcodeResult.getVehicleClass())));
        result.add(new CaptureResult.Entry("Vehicle Restrictions", extractField(barcodeResult.getVehicleRestrictions())));
        result.add(new CaptureResult.Entry("Version", extractField(barcodeResult.getVersion())));
        result.add(new CaptureResult.Entry("Weight Kg", extractField(barcodeResult.getWeightKg())));
        result.add(new CaptureResult.Entry("Weight Lbs", extractField(barcodeResult.getWeightLbs())));
        result.add(new CaptureResult.Entry("Is Real Id", extractField(barcodeResult.isRealId())));
        result.add(new CaptureResult.Entry("Barcode Elements", extractField(barcodeResult.getBarcodeDataElements())));
        result.add(new CaptureResult.Entry("Professional Driving Permit", extractField(barcodeResult.getProfessionalDrivingPermit())));

        return result;
    }

    private String extractField(ProfessionalDrivingPermit value) {
        if (value == null) {
            return EMPTY_TEXT_VALUE;
        }
        String result = "";
        result += "Codes: " + extractStringFields(value.getCodes()) + "\n";
        result += "Date of Expiry: " + extractField(value.getDateOfExpiry());
        return result;
    }

    private String extractStringFields(List<String> values) {
        if (values.isEmpty()) {
            return EMPTY_TEXT_VALUE;
        }
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            result.append(value).append(", ");
        }
        return result.toString();
    }

    private String extractIntFields(List<Integer> values) {
        if (values.isEmpty()) {
            return EMPTY_TEXT_VALUE;
        }
        StringBuilder result = new StringBuilder();
        for (Integer value : values) {
            result.append(extractField(value)).append(", ");
        }
        return result.toString();
    }

    private String extractField(List<VehicleRestriction> values) {
        if (values.isEmpty()) {
            return EMPTY_TEXT_VALUE;
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

    private String extractField(Map<String, String> value) {
        return value.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }
}

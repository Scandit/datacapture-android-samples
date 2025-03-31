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
import java.util.Arrays;
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
        return Arrays.asList(
            new CaptureResult.Entry("Barcode First Name", extractField(barcodeResult.getFirstName())),
            new CaptureResult.Entry("Barcode Last Name", extractField(barcodeResult.getLastName())),
            new CaptureResult.Entry("Barcode Full Name", extractField(barcodeResult.getFullName())),
            new CaptureResult.Entry("Barcode Sex", extractField(barcodeResult.getSex())),
            new CaptureResult.Entry("Barcode Date of Birth", extractField(barcodeResult.getDateOfBirth())),
            new CaptureResult.Entry("Barcode Nationality", extractField(barcodeResult.getNationality())),
            new CaptureResult.Entry("Barcode Address", extractField(barcodeResult.getAddress())),
            new CaptureResult.Entry("Barcode Document Number", extractField(barcodeResult.getDocumentNumber())),
            new CaptureResult.Entry("Barcode Date of Expiry", extractField(barcodeResult.getDateOfExpiry())),
            new CaptureResult.Entry("Barcode Date of Issue", extractField(barcodeResult.getDateOfIssue())),
            new CaptureResult.Entry("AAMVA Version", extractField(barcodeResult.getAamvaVersion())),
            new CaptureResult.Entry("Alias Family Name", extractField(barcodeResult.getAliasFamilyName())),
            new CaptureResult.Entry("Alias Given Name", extractField(barcodeResult.getAliasGivenName())),
            new CaptureResult.Entry("Alias Suffix Name", extractField(barcodeResult.getAliasSuffixName())),
            new CaptureResult.Entry("Blood Type", extractField(barcodeResult.getBloodType())),
            new CaptureResult.Entry("Branch Of Service", extractField(barcodeResult.getBranchOfService())),
            new CaptureResult.Entry("Card Instance Identifier", extractField(barcodeResult.getCardInstanceIdentifier())),
            new CaptureResult.Entry("Card Revision Date", extractField(barcodeResult.getCardRevisionDate())),
            new CaptureResult.Entry("Categories", extractStringFields(barcodeResult.getCategories())),
            new CaptureResult.Entry("Champus Effective Date", extractField(barcodeResult.getChampusEffectiveDate())),
            new CaptureResult.Entry("Champus Expiry Date", extractField(barcodeResult.getChampusExpiryDate())),
            new CaptureResult.Entry("Citizenship Status", extractField(barcodeResult.getCitizenshipStatus())),
            new CaptureResult.Entry("Civilian Health Care Flag Code", extractField(barcodeResult.getCivilianHealthCareFlagCode())),
            new CaptureResult.Entry("Civilian Health Care Flag Description", extractField(barcodeResult.getCivilianHealthCareFlagDescription())),
            new CaptureResult.Entry("Commissary Flag Code", extractField(barcodeResult.getCommissaryFlagCode())),
            new CaptureResult.Entry("Commissary Flag Description", extractField(barcodeResult.getCommissaryFlagDescription())),
            new CaptureResult.Entry("Country Of Birth", extractField(barcodeResult.getCountryOfBirth())),
            new CaptureResult.Entry("Country Of Birth Iso", extractField(barcodeResult.getCountryOfBirthIso())),
            new CaptureResult.Entry("Deers Dependent Suffix Code", extractField(barcodeResult.getDeersDependentSuffixCode())),
            new CaptureResult.Entry("Deers Dependent Suffix Description", extractField(barcodeResult.getDeersDependentSuffixDescription())),
            new CaptureResult.Entry("Direct Care Flag Code", extractField(barcodeResult.getDirectCareFlagCode())),
            new CaptureResult.Entry("Direct Care Flag Description", extractField(barcodeResult.getDirectCareFlagDescription())),
            new CaptureResult.Entry("Document Copy", extractField(barcodeResult.getDocumentCopy())),
            new CaptureResult.Entry("Document Discriminator Number", extractField(barcodeResult.getDocumentDiscriminatorNumber())),
            new CaptureResult.Entry("Driver Name Prefix", extractField(barcodeResult.getDriverNamePrefix())),
            new CaptureResult.Entry("Driver Name Suffix", extractField(barcodeResult.getDriverNameSuffix())),
            new CaptureResult.Entry("Driver Restriction Codes", extractIntFields(barcodeResult.getDriverRestrictionCodes())),
            new CaptureResult.Entry("Edi Person Identifier", extractField(barcodeResult.getEdiPersonIdentifier())),
            new CaptureResult.Entry("Endorsements Code", extractField(barcodeResult.getEndorsementsCode())),
            new CaptureResult.Entry("Exchange Flag Code", extractField(barcodeResult.getExchangeFlagCode())),
            new CaptureResult.Entry("Exchange Flag Description", extractField(barcodeResult.getExchangeFlagDescription())),
            new CaptureResult.Entry("Eye Color", extractField(barcodeResult.getEyeColor())),
            new CaptureResult.Entry("Family Sequence Number", extractField(barcodeResult.getFamilySequenceNumber())),
            new CaptureResult.Entry("First Name Truncation", extractField(barcodeResult.getFirstNameTruncation())),
            new CaptureResult.Entry("First Name Without Middle Name", extractField(barcodeResult.getFirstNameWithoutMiddleName())),
            new CaptureResult.Entry("Form Number", extractField(barcodeResult.getFormNumber())),
            new CaptureResult.Entry("Geneva Convention Category", extractField(barcodeResult.getGenevaConventionCategory())),
            new CaptureResult.Entry("Hair Color", extractField(barcodeResult.getHairColor())),
            new CaptureResult.Entry("Height Cm", extractField(barcodeResult.getHeightCm())),
            new CaptureResult.Entry("Height Inch", extractField(barcodeResult.getHeightInch())),
            new CaptureResult.Entry("Iin", extractField(barcodeResult.getIin())),
            new CaptureResult.Entry("Identification Type", extractField(barcodeResult.getIdentificationType())),
            new CaptureResult.Entry("Issuing Jurisdiction", extractField(barcodeResult.getIssuingJurisdiction())),
            new CaptureResult.Entry("Issuing Jurisdiction Iso", extractField(barcodeResult.getIssuingJurisdictionIso())),
            new CaptureResult.Entry("Jurisdiction Version", extractField(barcodeResult.getJurisdictionVersion())),
            new CaptureResult.Entry("Last Name Truncation", extractField(barcodeResult.getLastNameTruncation())),
            new CaptureResult.Entry("License Country Of Issue", extractField(barcodeResult.getLicenseCountryOfIssue())),
            new CaptureResult.Entry("Middle Name", extractField(barcodeResult.getMiddleName())),
            new CaptureResult.Entry("Middle Name Truncation", extractField(barcodeResult.getMiddleNameTruncation())),
            new CaptureResult.Entry("Mwr Flag Code", extractField(barcodeResult.getMwrFlagCode())),
            new CaptureResult.Entry("Mwr Flag Description", extractField(barcodeResult.getMwrFlagDescription())),
            new CaptureResult.Entry("Pay Grade", extractField(barcodeResult.getPayGrade())),
            new CaptureResult.Entry("Pay Plan Code", extractField(barcodeResult.getPayPlanCode())),
            new CaptureResult.Entry("Pay Plan Grade Code", extractField(barcodeResult.getPayPlanGradeCode())),
            new CaptureResult.Entry("Person Designator Document", extractField(barcodeResult.getPersonDesignatorDocument())),
            new CaptureResult.Entry("Person Designator Type Code", extractField(barcodeResult.getPersonDesignatorTypeCode())),
            new CaptureResult.Entry("Person Middle Initial", extractField(barcodeResult.getPersonMiddleInitial())),
            new CaptureResult.Entry("Personal Id Number", extractField(barcodeResult.getPersonalIdNumber())),
            new CaptureResult.Entry("Personal Id Number Type", extractField(barcodeResult.getPersonalIdNumberType())),
            new CaptureResult.Entry("Personnel Category Code", extractField(barcodeResult.getPersonnelCategoryCode())),
            new CaptureResult.Entry("Personnel Entitlement Condition Type", extractField(barcodeResult.getPersonnelEntitlementConditionType())),
            new CaptureResult.Entry("Place Of Birth", extractField(barcodeResult.getPlaceOfBirth())),
            new CaptureResult.Entry("Race", extractField(barcodeResult.getRace())),
            new CaptureResult.Entry("Rank", extractField(barcodeResult.getRank())),
            new CaptureResult.Entry("Raw Data", extractField(barcodeResult.getRawData())),
            new CaptureResult.Entry("Relationship Code", extractField(barcodeResult.getRelationshipCode())),
            new CaptureResult.Entry("Relationship Description", extractField(barcodeResult.getRelationshipDescription())),
            new CaptureResult.Entry("Restrictions Code", extractField(barcodeResult.getRestrictionsCode())),
            new CaptureResult.Entry("Security Code", extractField(barcodeResult.getSecurityCode())),
            new CaptureResult.Entry("Service Code", extractField(barcodeResult.getServiceCode())),
            new CaptureResult.Entry("Sponsor Flag", extractField(barcodeResult.getSponsorFlag())),
            new CaptureResult.Entry("Sponsor Name", extractField(barcodeResult.getSponsorName())),
            new CaptureResult.Entry("Sponsor Person Designator Identifier", extractField(barcodeResult.getSponsorPersonDesignatorIdentifier())),
            new CaptureResult.Entry("Status Code", extractField(barcodeResult.getStatusCode())),
            new CaptureResult.Entry("Status Code Description", extractField(barcodeResult.getStatusCodeDescription())),
            new CaptureResult.Entry("Vehicle Class", extractField(barcodeResult.getVehicleClass())),
            new CaptureResult.Entry("Vehicle Restrictions", extractField(barcodeResult.getVehicleRestrictions())),
            new CaptureResult.Entry("Version", extractField(barcodeResult.getVersion())),
            new CaptureResult.Entry("Weight Kg", extractField(barcodeResult.getWeightKg())),
            new CaptureResult.Entry("Weight Lbs", extractField(barcodeResult.getWeightLbs())),
            new CaptureResult.Entry("Is Real Id", extractField(barcodeResult.isRealId())),
            new CaptureResult.Entry("Barcode Elements", extractField(barcodeResult.getBarcodeDataElements())),
            new CaptureResult.Entry("Professional Driving Permit", extractField(barcodeResult.getProfessionalDrivingPermit()))
        );
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

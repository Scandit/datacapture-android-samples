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

package com.scandit.datacapture.idcapturesettingssample.mappers

import com.scandit.datacapture.id.data.CapturedId
import com.scandit.datacapture.id.data.ProfessionalDrivingPermit
import com.scandit.datacapture.id.data.VehicleRestriction

class BarcodeFieldExtractor(capturedId: CapturedId) : FieldExtractor(capturedId) {

    /**
     * Extract all the zone's specific fields and add them to the ones from CapturedId.
     */
    override fun extract(): Map<String, String> {
        val zone = capturedId.barcode ?: return emptyMap()
        return mapOf(
            "Barcode First Name" to zone.firstName.toDisplayValue(),
            "Barcode Last Name" to zone.lastName.toDisplayValue(),
            "Barcode Full Name" to zone.fullName.toDisplayValue(),
            "Barcode Sex" to zone.sex.toDisplayValue(),
            "Barcode Date of Birth" to zone.dateOfBirth.toDisplayValue(),
            "Barcode Nationality" to zone.nationality.toDisplayValue(),
            "Barcode Address" to zone.address.toDisplayValue(),
            "Barcode Document Number" to zone.documentNumber.toDisplayValue(),
            "Barcode Date of Expiry" to zone.dateOfExpiry.toDisplayValue(),
            "Barcode Date of Issue" to zone.dateOfIssue.toDisplayValue(),
            "AAMVA Version" to zone.aamvaVersion.toDisplayValue(),
            "Alias Family Name" to zone.aliasFamilyName.toDisplayValue(),
            "Alias Given Name" to zone.aliasGivenName.toDisplayValue(),
            "Alias Suffix Name" to zone.aliasSuffixName.toDisplayValue(),
            "Blood Type" to zone.bloodType.toDisplayValue(),
            "Branch Of Service" to zone.branchOfService.toDisplayValue(),
            "Card Instance Identifier" to zone.cardInstanceIdentifier.toDisplayValue(),
            "Card Revision Date" to zone.cardRevisionDate.toDisplayValue(),
            "Categories" to zone.categories.toDisplayValue(),
            "Champus Effective Date" to zone.champusEffectiveDate.toDisplayValue(),
            "Champus Expiry Date" to zone.champusExpiryDate.toDisplayValue(),
            "Citizenship Status" to zone.citizenshipStatus.toDisplayValue(),
            "Civilian Health Care Flag Code" to zone.civilianHealthCareFlagCode.toDisplayValue(),
            "Civilian Health Care Flag Description" to zone.civilianHealthCareFlagDescription.toDisplayValue(),
            "Commissary Flag Code" to zone.commissaryFlagCode.toDisplayValue(),
            "Commissary Flag Description" to zone.commissaryFlagDescription.toDisplayValue(),
            "Country Of Birth" to zone.countryOfBirth.toDisplayValue(),
            "Country Of Birth Iso" to zone.countryOfBirthIso.toDisplayValue(),
            "Deers Dependent Suffix Code" to zone.deersDependentSuffixCode.toDisplayValue(),
            "Deers Dependent Suffix Description" to zone.deersDependentSuffixDescription.toDisplayValue(),
            "Direct Care Flag Code" to zone.directCareFlagCode.toDisplayValue(),
            "Direct Care Flag Description" to zone.directCareFlagDescription.toDisplayValue(),
            "Document Copy" to zone.documentCopy.toDisplayValue(),
            "Document Discriminator Number" to zone.documentDiscriminatorNumber.toDisplayValue(),
            "Driver Name Prefix" to zone.driverNamePrefix.toDisplayValue(),
            "Driver Name Suffix" to zone.driverNameSuffix.toDisplayValue(),
            "Driver Restriction Codes" to zone.driverRestrictionCodes.toDisplayValue(),
            "Edi Person Identifier" to zone.ediPersonIdentifier.toDisplayValue(),
            "Endorsements Code" to zone.endorsementsCode.toDisplayValue(),
            "Exchange Flag Code" to zone.exchangeFlagCode.toDisplayValue(),
            "Exchange Flag Description" to zone.exchangeFlagDescription.toDisplayValue(),
            "Eye Color" to zone.eyeColor.toDisplayValue(),
            "Family Sequence Number" to zone.familySequenceNumber.toDisplayValue(),
            "First Name Truncation" to zone.firstNameTruncation.toDisplayValue(),
            "First Name Without Middle Name" to zone.firstNameWithoutMiddleName.toDisplayValue(),
            "Form Number" to zone.formNumber.toDisplayValue(),
            "Geneva Convention Category" to zone.genevaConventionCategory.toDisplayValue(),
            "Hair Color" to zone.hairColor.toDisplayValue(),
            "Height Cm" to zone.heightCm.toDisplayValue(),
            "Height Inch" to zone.heightInch.toDisplayValue(),
            "Iin" to zone.iin.toDisplayValue(),
            "Identification Type" to zone.identificationType.toDisplayValue(),
            "Issuing Jurisdiction" to zone.issuingJurisdiction.toDisplayValue(),
            "Issuing Jurisdiction Iso" to zone.issuingJurisdictionIso.toDisplayValue(),
            "Jurisdiction Version" to zone.jurisdictionVersion.toDisplayValue(),
            "Last Name Truncation" to zone.lastNameTruncation.toDisplayValue(),
            "License Country Of Issue" to zone.licenseCountryOfIssue.toDisplayValue(),
            "Middle Name" to zone.middleName.toDisplayValue(),
            "Middle Name Truncation" to zone.middleNameTruncation.toDisplayValue(),
            "Mwr Flag Code" to zone.mwrFlagCode.toDisplayValue(),
            "Mwr Flag Description" to zone.mwrFlagDescription.toDisplayValue(),
            "Pay Grade" to zone.payGrade.toDisplayValue(),
            "Pay Plan Code" to zone.payPlanCode.toDisplayValue(),
            "Pay Plan Grade Code" to zone.payPlanGradeCode.toDisplayValue(),
            "Person Designator Document" to zone.personDesignatorDocument.toDisplayValue(),
            "Person Designator Type Code" to zone.personDesignatorTypeCode.toDisplayValue(),
            "Person Middle Initial" to zone.personMiddleInitial.toDisplayValue(),
            "Personal Id Number" to zone.personalIdNumber.toDisplayValue(),
            "Personal Id Number Type" to zone.personalIdNumberType.toDisplayValue(),
            "Personnel Category Code" to zone.personnelCategoryCode.toDisplayValue(),
            "Personnel Entitlement Condition Type" to zone.personnelEntitlementConditionType.toDisplayValue(),
            "Place Of Birth" to zone.placeOfBirth.toDisplayValue(),
            "Race" to zone.race.toDisplayValue(),
            "Rank" to zone.rank.toDisplayValue(),
            "Raw Data" to zone.rawData.toDisplayValue(),
            "Relationship Code" to zone.relationshipCode.toDisplayValue(),
            "Relationship Description" to zone.relationshipDescription.toDisplayValue(),
            "Restrictions Code" to zone.restrictionsCode.toDisplayValue(),
            "Security Code" to zone.securityCode.toDisplayValue(),
            "Service Code" to zone.serviceCode.toDisplayValue(),
            "Sponsor Flag" to zone.sponsorFlag.toDisplayValue(),
            "Sponsor Name" to zone.sponsorName.toDisplayValue(),
            "Sponsor Person Designator Identifier" to zone.sponsorPersonDesignatorIdentifier.toDisplayValue(),
            "Status Code" to zone.statusCode.toDisplayValue(),
            "Status Code Description" to zone.statusCodeDescription.toDisplayValue(),
            "Vehicle Class" to zone.vehicleClass.toDisplayValue(),
            "Vehicle Restrictions" to zone.vehicleRestrictions.toDisplayValue(),
            "Version" to zone.version.toDisplayValue(),
            "Weight Kg" to zone.weightKg.toDisplayValue(),
            "Weight Lbs" to zone.weightLbs.toDisplayValue(),
            "Is Real Id" to zone.isRealId.toDisplayValue(),
            "Barcode Elements" to zone.barcodeDataElements.toDisplayValue(),
            "Professional Driving Permit" to zone.professionalDrivingPermit.toDisplayValue(),
        )
    }

    @JvmName("intListToDisplayValue")
    private fun List<Int>.toDisplayValue(): String =
        joinToString { it.toDisplayValue() }.orEmptyText()

    @JvmName("stringListToDisplayValue")
    private fun List<String>.toDisplayValue(): String =
        joinToString { it.toDisplayValue() }.orEmptyText()

    @JvmName("vehicleRestrictionListToDisplayValue")
    private fun List<VehicleRestriction>.toDisplayValue(): String =
        joinToString("\n") { it.toDisplayValue() }.orEmptyText()

    private fun VehicleRestriction.toDisplayValue(): String = buildString {
        append("Vehicle Code: ${vehicleCode.toDisplayValue()}\n")
        append("Vehicle Restriction: ${vehicleRestriction.toDisplayValue()}\n")
        append("Date of Issue: ${dateOfIssue.toDisplayValue()}")
    }

    private fun Map<String, String>.toDisplayValue(): String =
        entries.joinToString("\n") { "${it.key}: ${it.value}" }.orEmptyText()

    private fun ProfessionalDrivingPermit?.toDisplayValue(): String = this?.let { permit ->
        buildString {
            append("Codes: ${codes.toDisplayValue()}\n")
            append("Date of Expiry: ${dateOfExpiry.toDisplayValue()}")
        }
    }.orEmptyText()
}

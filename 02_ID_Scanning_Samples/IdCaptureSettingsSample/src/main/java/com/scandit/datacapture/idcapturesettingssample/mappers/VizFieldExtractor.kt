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
import com.scandit.datacapture.id.data.DrivingLicenseCategory
import com.scandit.datacapture.id.data.DrivingLicenseDetails

class VizFieldExtractor(capturedId: CapturedId) : FieldExtractor(capturedId) {

    /**
     * Extract all the zone's specific fields and add them to the ones from CapturedId.
     */
    override fun extract(): Map<String, String> {
        val zone = capturedId.viz ?: return emptyMap()
        return buildMap {
            put("VIZ First Name", zone.firstName.toDisplayValue())
            put("VIZ Last Name", zone.lastName.toDisplayValue())
            put("VIZ Full Name", zone.fullName.toDisplayValue())
            put("VIZ Sex", zone.sex.toDisplayValue())
            put("VIZ Date of Birth", zone.dateOfBirth.toDisplayValue())
            put("VIZ Nationality", zone.nationality.toDisplayValue())
            put("VIZ Address", zone.address.toDisplayValue())
            put("VIZ Document Number", zone.documentNumber.toDisplayValue())
            put("VIZ Date of Expiry", zone.dateOfExpiry.toDisplayValue())
            put("VIZ Date of Issue", zone.dateOfIssue.toDisplayValue())
            put("Additional Name Information", zone.additionalNameInformation.toDisplayValue())
            put(
                "Additional Address Information",
                zone.additionalAddressInformation.toDisplayValue()
            )
            put("Place of Birth", zone.placeOfBirth.toDisplayValue())
            put("Race", zone.race.toDisplayValue())
            put("Religion", zone.religion.toDisplayValue())
            put("Profession", zone.profession.toDisplayValue())
            put("Marital Status", zone.maritalStatus.toDisplayValue())
            put("Residential Status", zone.residentialStatus.toDisplayValue())
            put("Employer", zone.employer.toDisplayValue())
            put("Personal ID Number", zone.personalIdNumber.toDisplayValue())
            put("Issuing Jurisdiction", zone.issuingJurisdiction.toDisplayValue())
            put("Issuing Jurisdiction ISO", zone.issuingJurisdictionIso.toDisplayValue())
            put("Issuing Authority", zone.issuingAuthority.toDisplayValue())
            put("Blood Type", zone.bloodType.toDisplayValue())
            put("Sponsor", zone.sponsor.toDisplayValue())
            put("Mother's name", zone.mothersName.toDisplayValue())
            put("Father's name", zone.fathersName.toDisplayValue())
            put("Captured Sides", zone.capturedSides.toString().toDisplayValue())
            put("Backside Supported", zone.isBackSideCaptureSupported.toDisplayValue())
            put("VIZ Visa Number", zone.visaNumber.toDisplayValue())
            put("VIZ Passport Number", zone.passportNumber.toDisplayValue())
            put("Vehicle Owner", zone.vehicleOwner.toDisplayValue())

            val document = capturedId.document
            if (document != null && document.isDriverLicense()) {
                put("Driver's License Details", zone.drivingLicenseDetails.toDisplayValue())
            }
        }
    }

    private fun DrivingLicenseDetails?.toDisplayValue(): String {
        if (this == null) {
            return EMPTY_TEXT_VALUE
        }

        return buildString {
            append("Categories:\n")
            val categories = drivingLicenseCategories
            if (categories.isEmpty()) {
                append(EMPTY_TEXT_VALUE)
            } else {
                categories.forEachIndexed { index, category ->
                    append(category.toDisplayValue())
                    if (index < categories.size - 1) {
                        append("\n\n")
                    }
                }
            }

            append("\n\nRestrictions: ")
            append(restrictions.toDisplayValue())

            append("\n\nEndorsements: ")
            append(endorsements.toDisplayValue())
        }
    }

    private fun DrivingLicenseCategory.toDisplayValue(): String = buildString {
        append("Code: ${code.toDisplayValue()}\n")
        append("Date of Issue: ${dateOfIssue.toDisplayValue()}\n")
        append("Date of Expiry: ${dateOfExpiry.toDisplayValue()}")
    }
}

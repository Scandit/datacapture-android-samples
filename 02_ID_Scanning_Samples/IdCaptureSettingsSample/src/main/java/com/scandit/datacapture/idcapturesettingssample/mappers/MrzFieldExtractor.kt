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

class MrzFieldExtractor(capturedId: CapturedId) : FieldExtractor(capturedId) {

    /**
     * Extract all the zone's specific fields and add them to the ones from CapturedId.
     */
    override fun extract(): Map<String, String> {
        val zone = capturedId.mrz ?: return emptyMap()
        return mapOf(
            "MRZ First Name" to zone.firstName.toDisplayValue(),
            "MRZ Last Name" to zone.lastName.toDisplayValue(),
            "MRZ Full Name" to zone.fullName.toDisplayValue(),
            "MRZ Sex" to zone.sex.toDisplayValue(),
            "MRZ Date of Birth" to zone.dateOfBirth.toDisplayValue(),
            "MRZ Nationality" to zone.nationality.toDisplayValue(),
            "MRZ Address" to zone.address.toDisplayValue(),
            "MRZ Document Number" to zone.documentNumber.toDisplayValue(),
            "MRZ Date of Expiry" to zone.dateOfExpiry.toDisplayValue(),
            "MRZ Date of Issue" to zone.dateOfIssue.toDisplayValue(),
            "Document Code" to zone.documentCode.toDisplayValue(),
            "Names are Truncated" to zone.namesAreTruncated.toDisplayValue(),
            "Optional Data In Line 1" to zone.optionalDataInLine1.toDisplayValue(),
            "Optional Data In Line 2" to zone.optionalDataInLine2.toDisplayValue(),
            "Captured MRZ" to zone.capturedMrz.toDisplayValue(),
            "Personal ID Number" to zone.personalIdNumber.toDisplayValue(),
            "Passport Type" to zone.passportType?.name.toDisplayValue(),
            "Passport Number" to zone.passportNumber.toDisplayValue(),
            "Passport Issuer ISO" to zone.passportIssuerIso.toDisplayValue(),
            "Passport Date of Expiry" to zone.passportDateOfExpiry.toDisplayValue(),
            "Renewal Times" to zone.renewalTimes.toDisplayValue(),
            "Full Name Simplified Chinese" to zone.fullNameSimplifiedChinese.toDisplayValue(),
            "Omitted Character Count in GBK Name" to zone.omittedCharacterCountInGbkName.toDisplayValue(),
            "Omitted Name Count" to zone.omittedNameCount.toDisplayValue(),
            "Issuing Authority Code" to zone.issuingAuthorityCode.toDisplayValue()
        )
    }
}

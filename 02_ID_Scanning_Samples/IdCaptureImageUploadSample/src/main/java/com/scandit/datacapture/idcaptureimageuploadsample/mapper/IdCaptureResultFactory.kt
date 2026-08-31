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

package com.scandit.datacapture.idcaptureimageuploadsample.mapper

import com.scandit.datacapture.id.capture.IdCaptureDocument
import com.scandit.datacapture.id.data.CapturedId
import com.scandit.datacapture.id.data.DateResult
import com.scandit.datacapture.id.data.IdCaptureDocumentType
import com.scandit.datacapture.idcaptureimageuploadsample.ui.result.CaptureResult
import com.scandit.datacapture.idcaptureimageuploadsample.ui.result.ResultField
import com.scandit.datacapture.idcaptureimageuploadsample.utils.toNameCase
import java.text.DateFormat
import java.text.SimpleDateFormat

object IdCaptureResultFactory {
    /**
     * Extract the common CapturedId's fields for the result screen. Values are raw display text
     * (empty when absent); labels and the empty-value placeholder are resolved in the UI layer.
     */
    fun extract(capturedId: CapturedId): CaptureResult = CaptureResult(
        fields = listOf(
            CaptureResult.Entry(ResultField.FULL_NAME, capturedId.fullName.toDisplayValue()),
            CaptureResult.Entry(ResultField.DATE_OF_BIRTH, capturedId.dateOfBirth.toDisplayValue()),
            CaptureResult.Entry(ResultField.DATE_OF_EXPIRY, capturedId.dateOfExpiry.toDisplayValue()),
            CaptureResult.Entry(ResultField.DOCUMENT_NUMBER, capturedId.documentNumber.toDisplayValue()),
            CaptureResult.Entry(ResultField.NATIONALITY, capturedId.nationality.toDisplayValue()),
            CaptureResult.Entry(ResultField.DOCUMENT_TYPE, capturedId.document.toDisplayValue()),
        ),
    )

    private fun String?.toDisplayValue(): String = this?.trim().orEmpty()

    private fun DateResult?.toDisplayValue(): String =
        this?.let { dateFormat.format(it.localDate) }.orEmpty()

    fun IdCaptureDocument?.toDisplayValue(): String =
        this?.documentType?.let(::displayValue).orEmpty()

    fun displayValue(documentType: IdCaptureDocumentType): String =
        documentType.toString().toNameCase()

    val dateFormat: DateFormat = SimpleDateFormat.getDateInstance()
}

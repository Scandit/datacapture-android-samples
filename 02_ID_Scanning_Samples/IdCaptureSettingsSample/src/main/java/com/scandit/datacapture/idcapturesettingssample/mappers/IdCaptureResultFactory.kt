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
import com.scandit.datacapture.id.data.IdSide
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.TimeZone

object IdCaptureResultFactory {

    private val shortDateFormat: DateFormat = SimpleDateFormat.getDateInstance().apply {
        /*
         * DateResult::toDate() returns dates in UTC. We need to use the same timezone for
         * formatting, otherwise we may end up with a wrong date displayed if our local timezone
         * is a day behind/ahead from UTC.
         */
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /*
     * Extract all the CapturedId's fields for the full result screen.
     */
    fun extract(capturedId: CapturedId): CaptureResult {
        val fields = mutableMapOf<String, String>()

        // Extract all the CapturedId's common fields.
        val commonFieldExtractor = CommonFieldExtractor(capturedId)
        fields.putAll(commonFieldExtractor.extract())

        // Extract all the CapturedId's type-specific fields.
        getFieldExtractors(capturedId).forEach { fieldExtractor ->
            fields.putAll(fieldExtractor.extract())
        }

        // Extract and format date of birth. This will be shown in continuous mode.
        val dateOfBirth = capturedId.dateOfBirth?.let {
            shortDateFormat.format(it.localDate)
        } ?: ""

        // Extract the full name. This will be shown in continuous mode.
        val fullName = capturedId.fullName
            ?: "${capturedId.firstName} ${capturedId.lastName}"

        val images = capturedId.images

        return CaptureResult(
            fields = fields.map { CaptureResult.Entry(it.key, it.value) },
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            faceImage = images.face,
            frontDocumentImage = images.getCroppedDocument(IdSide.FRONT),
            backDocumentImage = images.getCroppedDocument(IdSide.BACK),
            frontFrameImage = images.getFrame(IdSide.FRONT),
            backFrameImage = images.getFrame(IdSide.BACK),
        )
    }

    private fun getFieldExtractors(capturedId: CapturedId): List<FieldExtractor> = buildList {
        capturedId.viz?.let { add(VizFieldExtractor(capturedId)) }
        capturedId.mobileDocumentOcr?.let { add(MobileDocumentOcrFieldExtractor(capturedId)) }
        capturedId.mrz?.let { add(MrzFieldExtractor(capturedId)) }
        capturedId.barcode?.let { add(BarcodeFieldExtractor(capturedId)) }
    }
}

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

import com.scandit.datacapture.id.capture.IdCaptureDocument
import com.scandit.datacapture.id.capture.RegionSpecific
import com.scandit.datacapture.id.data.CapturedId
import com.scandit.datacapture.id.data.DateResult
import com.scandit.datacapture.id.data.RegionSpecificSubtype
import com.scandit.datacapture.idcapturesettingssample.utils.toNameCase
import java.text.DateFormat
import java.text.SimpleDateFormat

abstract class FieldExtractor(protected val capturedId: CapturedId) {

    abstract fun extract(): Map<String, String>

    protected fun Boolean?.toDisplayValue(): String = when (this) {
        null -> EMPTY_TEXT_VALUE
        true -> "Yes"
        false -> "No"
    }

    protected fun Int?.toDisplayValue(): String = this?.toString().orEmptyText()

    protected fun String?.toDisplayValue(): String =
        this?.trim()?.takeIf { it.isNotEmpty() }.orEmptyText()

    protected fun DateResult?.toDisplayValue(): String =
        this?.let { dateFormat.format(it.localDate) }.orEmptyText()

    fun String?.orEmptyText(): String = this?.takeIf { it.isNotBlank() } ?: EMPTY_TEXT_VALUE

    companion object {
        protected val dateFormat: DateFormat = SimpleDateFormat.getDateInstance()
        const val EMPTY_TEXT_VALUE = "<empty>"
    }
}

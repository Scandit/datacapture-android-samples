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

package com.scandit.datacapture.idcaptureimageuploadsample.ui.result

data class CaptureResult(val fields: List<Entry>) {

    data class Entry(
        val field: ResultField,
        val value: String,
    )
}

enum class ResultField {
    FULL_NAME,
    DATE_OF_BIRTH,
    DATE_OF_EXPIRY,
    DOCUMENT_NUMBER,
    NATIONALITY,
    DOCUMENT_TYPE,
}

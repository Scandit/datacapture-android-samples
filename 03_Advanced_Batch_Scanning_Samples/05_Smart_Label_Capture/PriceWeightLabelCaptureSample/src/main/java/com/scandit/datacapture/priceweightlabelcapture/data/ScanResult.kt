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
package com.scandit.datacapture.priceweightlabelcapture.data

sealed interface ScannedResult {
    val data: String
    var quantity: Int

    data class Barcode(override val data: String, override var quantity: Int = 1) : ScannedResult
    data class Label(
        override val data: String,
        val weight: String,
        val unitPrice: String,
        override var quantity: Int = 1,
    ) : ScannedResult
}

data class PartialLabel(
    val data: String,
    val weight: String?,
    val unitPrice: String?
)

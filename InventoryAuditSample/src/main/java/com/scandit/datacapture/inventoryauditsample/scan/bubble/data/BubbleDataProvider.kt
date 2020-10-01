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

package com.scandit.datacapture.inventoryauditsample.scan.bubble.data

import com.scandit.datacapture.inventoryauditsample.scan.bubble.data.BubbleColor.RED
import com.scandit.datacapture.inventoryauditsample.scan.bubble.data.BubbleColor.WHITE

object BubbleDataProvider {
    private const val DEFAULT_COUNT = 4

    private val barcodes = mutableMapOf<String, Int>()

    // Get the information you want to show from your back end system/database.
    // In this sample we're just returning hardcoded data.
    fun getDataForBarcode(barcode: String): BubbleData {
        val maybeCount = barcodes[barcode]

        return if (maybeCount == null) {
            val lastDigit = Character.getNumericValue(barcode.last())
            val color = if (lastDigit % 2 == 0) RED else WHITE

            BubbleData(color, DEFAULT_COUNT)
        } else {
            BubbleData(WHITE, maybeCount)
        }
    }

    fun saveDataForBarcode(barcode: String, count: Int) {
        barcodes[barcode] = count
    }
}

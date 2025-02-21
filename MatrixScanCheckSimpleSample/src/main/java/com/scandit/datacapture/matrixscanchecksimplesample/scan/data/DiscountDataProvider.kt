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

package com.scandit.datacapture.matrixscanchecksimplesample.scan.data

import com.scandit.datacapture.barcode.data.Barcode

object DiscountDataProvider {
    private val barcodeDiscounts = mutableMapOf<String, DiscountData>()
    private var currentIndex = 0

    // Get the information you want to show from your back end system/database.
    // In this sample we're just returning hardcoded data.
    fun getDataForBarcode(barcode: Barcode): DiscountData {
        val barcodeData = barcode.data ?: ""

        barcodeDiscounts[barcodeData]?.let { discount ->
            return discount
        }

        val discount = Discount.values()[currentIndex]
        val discountData = DiscountData(discount)

        barcodeDiscounts[barcodeData] = discountData
        currentIndex = (currentIndex + 1) % Discount.values().count()

        return discountData
    }
}

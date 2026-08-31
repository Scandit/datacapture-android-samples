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
package com.scandit.datacapture.pricecapturesample.data

import android.content.Context
import kotlin.math.abs

/**
 * The reference price list the app validates captured labels against.
 *
 * The data is loaded once at startup from a bundled CSV asset into an in-memory map and is
 * read-only at runtime. To update the reference data, edit the CSV and rebuild the app — see the
 * README for the format.
 */
class BarcodePriceDatabase private constructor(
    private val pricesByBarcode: Map<String, Double>,
) {

    /**
     * Validate a captured (barcode, price) pair.
     *
     * @param barcode the barcode data read from the label's barcode (SKU) field.
     * @param capturedPrice the price parsed from the label's price field, or `null` if it has not
     *   been read yet.
     * @return [ValidationState.UNKNOWN] when the barcode is not in the database; otherwise, once a
     *   price is available, [ValidationState.CORRECT] on an exact-cent match and
     *   [ValidationState.INCORRECT] otherwise. Returns `null` while the barcode is known but its
     *   price has not been captured yet, so nothing is highlighted prematurely.
     */
    fun validate(barcode: String, capturedPrice: Double?): ValidationState? {
        val expectedPrice = pricesByBarcode[barcode] ?: return ValidationState.UNKNOWN
        if (capturedPrice == null) return null
        // Exact-cent comparison. The epsilon only guards against Double representation noise
        // (e.g. 0.1 + 0.2 != 0.3); it is not a real price tolerance.
        return if (abs(expectedPrice - capturedPrice) < PRICE_EPSILON) {
            ValidationState.CORRECT
        } else {
            ValidationState.INCORRECT
        }
    }

    companion object {
        private const val PRICE_EPSILON = 0.001

        /**
         * Load the database from a two-column `barcode,price` CSV bundled in `assets/`.
         * Blank lines, `#` comment lines, and malformed lines are skipped.
         */
        fun fromAsset(context: Context, assetName: String): BarcodePriceDatabase {
            val prices = mutableMapOf<String, Double>()
            context.assets.open(assetName).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEach
                    val parts = trimmed.split(',')
                    if (parts.size < 2) return@forEach
                    val barcode = parts[0].trim()
                    val price = parts[1].trim().toDoubleOrNull()
                    if (barcode.isNotEmpty() && price != null) {
                        prices[barcode] = price
                    }
                }
            }
            return BarcodePriceDatabase(prices)
        }
    }
}

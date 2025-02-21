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

import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DiscountData(
    private val discount: Discount
) {

    private var showingExpirationMessage: Boolean = false

    val percentage: String
        get() = when (discount) {
            Discount.SMALL -> "25% off"
            Discount.MEDIUM -> "50% off"
            Discount.LARGE -> "75% off"
        }

    val color: Int
        get() = when (discount) {
            Discount.SMALL -> Color.parseColor("#E6FFDC32")
            Discount.MEDIUM -> Color.parseColor("#E6FA8719")
            Discount.LARGE -> Color.parseColor("#E6FA4446")
        }

    fun getDisplayText(annotationBeingCreated: Boolean): String {
        if (annotationBeingCreated) showingExpirationMessage = false

        val displayText = if (showingExpirationMessage) {
            expirationDate
        } else {
            expirationMessage
        }

        showingExpirationMessage = !showingExpirationMessage

        return displayText
    }

    private val expirationMessage: String
        get() = when (discount) {
            Discount.SMALL -> "Expires in 3 days"
            Discount.MEDIUM -> "Expires in 2 days"
            Discount.LARGE -> "Expires in 1 day"
        }

    private val expirationDate: String
        get() {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DATE, daysUntilExpiry)

            return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        }

    private val daysUntilExpiry: Int
        get() = when (discount) {
            Discount.SMALL -> 3
            Discount.MEDIUM -> 2
            Discount.LARGE -> 1
        }
}

enum class Discount {
    SMALL,
    MEDIUM,
    LARGE,
}
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

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.scandit.datacapture.pricecapturesample.R

/**
 * The result of validating a captured (barcode, price) pair against the reference database.
 *
 * Each state carries the resources used to render it: a color (used both for the field highlight
 * brush and the badge background), a centered icon, and the tint applied to that icon (chosen for
 * contrast against the badge background).
 */
enum class ValidationState(
    @ColorRes val colorRes: Int,
    @DrawableRes val iconRes: Int,
    @ColorRes val glyphTintRes: Int,
) {
    /** Barcode found and the captured price matches the expected price to the cent. */
    CORRECT(R.color.state_correct, R.drawable.ic_state_correct, R.color.state_glyph_light),

    /** Barcode found but the captured price differs from the expected price. */
    INCORRECT(R.color.state_incorrect, R.drawable.ic_state_incorrect, R.color.state_glyph_light),

    /** Barcode not present in the reference database. */
    UNKNOWN(R.color.state_unknown, R.drawable.ic_state_unknown, R.color.state_glyph_dark),
}

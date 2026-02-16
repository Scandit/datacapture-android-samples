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

package com.scandit.datacapture.idcapturesettingssample.utils

/**
 * Convert a SNAKE_CASE string to Name Case (e.g., "SNAKE_CASE" -> "Snake Case")
 */
fun String.toNameCase(): String {
    if (isEmpty()) return this

    return trim()
        .lowercase()
        .split("_")
        .joinToString(" ") { word ->
            when (word.length) {
                0 -> ""
                1 -> word.uppercase()
                else -> word.replaceFirstChar { it.uppercase() }
            }
        }
}

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
package com.scandit.datacapture.listbuildingsamplecompose.ui

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import com.scandit.datacapture.barcode.data.Barcode
import kotlin.math.max
import kotlin.math.min

fun Bitmap.cropAroundBarcode(
    barcode: Barcode,
    scaledImageSizeInPx: Int,
    padding: Float
): Bitmap {
    // safety check when input bitmap is too small
    if (width == 1 && height == 1) return this

    val points = listOf(
        barcode.location.bottomLeft,
        barcode.location.topLeft,
        barcode.location.topRight,
        barcode.location.bottomRight
    )
    val minX = points.minOf { point -> point.x }
    val minY = points.minOf { point -> point.y }
    val maxX = points.maxOf { point -> point.x }
    val maxY = points.maxOf { point -> point.y }

    val center = PointF(((minX + maxX) * 0.5f), ((minY + maxY) * 0.5f))
    val largerSize = max((maxY - minY), (maxX - minX))

    var height = (largerSize * padding).toInt()
    var width = (largerSize * padding).toInt()

    var x = (center.x - largerSize * (padding / 2)).toInt()
        .coerceAtLeast(0)
    var y = (center.y - largerSize * (padding / 2)).toInt()
        .coerceAtLeast(0)

    if ((y + height) > this.height) {
        val difference = ((y + height) - this.height)

        if (y - difference < 0) {
            height -= difference
            width -= difference
        } else {
            y -= difference
        }
    }

    if ((x + width) > this.width) {
        val difference = ((x + width) - this.width)
        if (x - difference < 0) {
            width -= difference
            height -= difference
        } else {
            x -= difference
        }
    }

    if (width <= 0 || height <= 0) { // safety check
        return this
    }

    val scaleFactor: Float = getScaleFactor(
        width,
        height,
        scaledImageSizeInPx.coerceAtLeast(1)
    )

    val matrix = Matrix()
    matrix.postRotate(90f)
    matrix.postScale(scaleFactor, scaleFactor)

    return Bitmap.createBitmap(this, x, y, width, height, matrix, true)
}

private fun getScaleFactor(bitmapWidth: Int, bitmapHeight: Int, targetDimension: Int): Float =
    targetDimension / min(bitmapWidth, bitmapHeight).toFloat()

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

package com.scandit.datacapture.idcaptureimageuploadsample.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Loads an image picked by the user (a content [Uri]) into an `ARGB_8888` [Bitmap] suitable for
 * feeding into the SDK via `BitmapFrameSource`.
 *
 * Pitfalls handled here:
 *  - The SDK reads the bitmap pixels, so the result must be a software `ARGB_8888` bitmap, never
 *    a `HARDWARE` one. [ImageDecoder] defaults to `HARDWARE`, so we force a software allocation.
 *  - Photos from the camera/gallery often carry an EXIF orientation. [ImageDecoder] (API 28+)
 *    applies it automatically; on older devices we read and apply it ourselves.
 *  - Very large images are downscaled (longest side capped at [MAX_DIMENSION]) to bound memory.
 */
class ImageLoader(
    private val context: Context,
) {

    /**
     * Decode [uri] into an `ARGB_8888` bitmap, downscaled and correctly oriented.
     */
    suspend fun loadBitmap(uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            loadWithImageDecoder(uri)
        } else {
            loadWithBitmapFactory(uri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun loadWithImageDecoder(uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            // The SDK needs to read the pixels, so a HARDWARE bitmap (the default) is not usable.
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val sampleSize = calculateSampleSize(info.size.width, info.size.height)
            if (sampleSize > 1) {
                decoder.setTargetSampleSize(sampleSize)
            }
        }
        return bitmap.ensureArgb8888()
    }

    private fun loadWithBitmapFactory(uri: Uri): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        } ?: throw IOException("Cannot open image: $uri")

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val decoded = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: throw IOException("Cannot decode image: $uri")

        return decoded.applyExifRotation(uri).ensureArgb8888()
    }

    private fun Bitmap.applyExifRotation(uri: Uri): Bitmap {
        val orientation = context.contentResolver.openInputStream(uri)?.use { stream ->
            ExifInterface(stream).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } ?: ExifInterface.ORIENTATION_NORMAL

        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        if (degrees == 0f) return this

        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        if (rotated != this) {
            recycle()
        }
        return rotated
    }

    private fun Bitmap.ensureArgb8888(): Bitmap =
        if (config == Bitmap.Config.ARGB_8888) {
            this
        } else {
            copy(Bitmap.Config.ARGB_8888, false)
        }

    /**
     * The smallest power-of-two sample size that keeps the longest side at or below
     * [MAX_DIMENSION].
     */
    private fun calculateSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        var longestSide = maxOf(width, height)
        while (longestSide > MAX_DIMENSION) {
            sampleSize *= 2
            longestSide /= 2
        }
        return sampleSize
    }

    companion object {
        /** The longest side a decoded image is allowed to have, in pixels. */
        const val MAX_DIMENSION = 4096
    }
}

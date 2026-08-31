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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import java.io.IOException
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Converts pages of a PDF picked by the user (a content [Uri]) into `ARGB_8888` [Bitmap]s that
 * can be fed into the SDK via `BitmapFrameSource`.
 *
 * This sample uses Android's built-in [PdfRenderer] (AOSP, available since API 21):
 *  - it is part of the platform, so it adds no third-party dependency and no licensing concerns,
 *  - it covers the common case of rasterising a page to a bitmap.
 *
 * Pages are rendered at [TARGET_DPI] for legible text, with the longest side capped at
 * [MAX_DIMENSION]. PDF pages may be transparent, so each page is rendered onto a white
 * background which is what document detection expects.
 */
class PdfConverter(
    private val context: Context,
) {

    /**
     * Render the page at the [index] of the PDF at [uri].
     *
     * @return the rendered page as an `ARGB_8888` bitmap, or null if the document has no such page.
     * @throws IOException if the PDF cannot be opened.
     */
    suspend fun renderPage(uri: Uri, index: Int): Bitmap? = withContext(Dispatchers.IO) {
        pdfLock.withLock {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                PdfRenderer(descriptor).use { renderer ->
                    if (index in 0 until renderer.pageCount) renderPage(renderer, index) else null
                }
            } ?: throw IOException("Cannot open PDF: $uri")
        }
    }

    /**
     * The number of pages in the PDF at [uri].
     *
     * @throws IOException if the PDF cannot be opened.
     */
    suspend fun pageCount(uri: Uri): Int = withContext(Dispatchers.IO) {
        pdfLock.withLock {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                PdfRenderer(descriptor).use { renderer -> renderer.pageCount }
            } ?: throw IOException("Cannot open PDF: $uri")
        }
    }

    private fun renderPage(renderer: PdfRenderer, index: Int): Bitmap {
        // PdfRenderer only allows a single page to be open at a time.
        renderer.openPage(index).use { page ->
            val scale = computeScale(page.width, page.height)
            val width = (page.width * scale).roundToInt().coerceAtLeast(1)
            val height = (page.height * scale).roundToInt().coerceAtLeast(1)

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            // PDF pages can be transparent; render onto white for reliable document detection.
            Canvas(bitmap).drawColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            return bitmap
        }
    }

    /**
     * Scale factor that renders a page at [TARGET_DPI], reduced if necessary so the longest side
     * stays within [MAX_DIMENSION]. Page dimensions from [PdfRenderer] are in PostScript points
     * (1/72 inch).
     */
    private fun computeScale(pageWidth: Int, pageHeight: Int): Float {
        val dpiScale = TARGET_DPI / POINTS_PER_INCH
        val longestSide = maxOf(pageWidth, pageHeight)
        val scaledLongestSide = longestSide * dpiScale
        return if (scaledLongestSide > MAX_DIMENSION) {
            MAX_DIMENSION.toFloat() / longestSide
        } else {
            dpiScale
        }
    }

    companion object {
        // PdfRenderer is not thread-safe and historically relies on a process-global native lock,
        // so concurrent renders (e.g. the front and back thumbnails of the same multi-page PDF)
        // are serialised through this shared mutex.
        private val pdfLock = Mutex()

        const val MIME_TYPE = "application/pdf"

        /** Target rendering resolution in dots per inch. */
        const val TARGET_DPI = 200f

        /** PostScript points per inch (PdfRenderer page units). */
        private const val POINTS_PER_INCH = 72f

        /** The longest side a rendered page is allowed to have, in pixels. */
        const val MAX_DIMENSION = 4096
    }
}

/** Whether [uri] points at a PDF document. */
fun Context.isPdf(uri: Uri): Boolean =
    contentResolver.getType(uri) == PdfConverter.MIME_TYPE

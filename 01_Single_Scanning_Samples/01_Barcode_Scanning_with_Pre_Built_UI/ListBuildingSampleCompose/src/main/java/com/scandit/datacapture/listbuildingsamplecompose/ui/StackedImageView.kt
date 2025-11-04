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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

private val IMAGE_PEEK_SIZE = 4.dp
private val MIN_IMAGE_SIZE = 24.dp
private val BOX_SIZE = 70.dp

@Composable
internal fun StackedImageView(images: List<Bitmap>) {
    Box(modifier = Modifier.size(BOX_SIZE)) {
        val maxOffset = IMAGE_PEEK_SIZE * (images.size - 1)
        val imageSize = (BOX_SIZE - maxOffset).coerceAtLeast(MIN_IMAGE_SIZE)

        images.forEachIndexed { index, bitmap ->
            val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
            val stackIndex = images.size - 1 - index
            val offsetDp = IMAGE_PEEK_SIZE * stackIndex

            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize)
                    .offset {
                        IntOffset(
                            x = offsetDp.roundToPx(),
                            y = offsetDp.roundToPx()
                        )
                    }
            )
        }
    }
}
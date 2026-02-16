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

package com.scandit.datacapture.idcapturesettingssample.ui.result

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scandit.datacapture.id.data.IdSide
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton

@Composable
fun ResultScreen(
    viewModel: ResultViewModel,
    onBackClick: () -> Unit,
) {
    val captureResult by viewModel.captureResult.collectAsStateWithLifecycle()

    captureResult?.let { result ->
        ResultScreen(
            result = result,
            onBackClick = onBackClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultScreen(
    result: CaptureResult,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                title = { Text(stringResource(R.string.captured_id_title)) },
                navigationIcon = { BackButton(onBackClick) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        ) {
            result.faceImage?.let { face ->
                item(
                    key = "face_image",
                    contentType = "image"
                ) {
                    ResultImage(bitmap = face)
                }
            }

            result.frontDocumentImage?.let { front ->
                item(
                    key = "id_front_image",
                    contentType = "image"
                ) {
                    ResultImage(bitmap = front)
                }
            }

            result.backDocumentImage?.let { back ->
                item(
                    key = "id_back_image",
                    contentType = "image"
                ) {
                    ResultImage(bitmap = back)
                }
            }

            result.frontFrameImage?.let { frameFront ->
                item(
                    key = "frame_front_image",
                    contentType = "image"
                ) {
                    ResultImage(bitmap = frameFront)
                }
            }

            result.backFrameImage?.let { frameBack ->
                item(
                    key = "frame_back_image",
                    contentType = "image"
                ) {
                    ResultImage(bitmap = frameBack)
                }
            }

            items(
                items = result.fields,
                key = { entry -> entry.key },
                contentType = { "field_item" }
            ) { entry ->
                ResultItem(entry = entry)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ResultImage(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun ResultItem(entry: CaptureResult.Entry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = entry.value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = entry.key,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun ResultScreenPreview() {
    ResultScreen(
        result = CaptureResult(
            fields = listOf(
                CaptureResult.Entry("Full Name", "John Doe"),
                CaptureResult.Entry("Date of Birth", "1990-01-15"),
                CaptureResult.Entry("Document Number", "AB123456"),
                CaptureResult.Entry("Expiry Date", "2030-12-31"),
                CaptureResult.Entry("Nationality", "USA"),
                CaptureResult.Entry("Sex", "M")
            ),
            fullName = "John Doe",
            dateOfBirth = "1990-01-15",
            faceImage = null,
            frontDocumentImage = null,
            backDocumentImage = null,
            frontFrameImage = null,
            backFrameImage = null
        ),
        onBackClick = {}
    )
}

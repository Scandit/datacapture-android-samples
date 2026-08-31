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

package com.scandit.datacapture.idcaptureimageuploadsample.ui.scan

import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scandit.datacapture.id.data.RejectionReason
import com.scandit.datacapture.idcaptureimageuploadsample.R
import com.scandit.datacapture.idcaptureimageuploadsample.di.Injector
import com.scandit.datacapture.idcaptureimageuploadsample.ui.result.CaptureResult
import com.scandit.datacapture.idcaptureimageuploadsample.ui.theme.AppTheme
import com.scandit.datacapture.idcaptureimageuploadsample.utils.ImageLoader
import com.scandit.datacapture.idcaptureimageuploadsample.utils.PdfConverter
import kotlin.math.roundToInt
import androidx.core.graphics.scale

private val ACCEPTED_MIME_TYPES = arrayOf("image/*", PdfConverter.MIME_TYPE)

@Composable
fun ScanScreen(
    onResult: (CaptureResult) -> Unit,
    viewModel: ScanViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val pickFront = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let(viewModel::onFrontPicked) }
    val pickBack = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let(viewModel::onBackPicked) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ScanEvent.Captured -> onResult(event.result)
                is ScanEvent.Rejected ->
                    snackbarHostState.showSnackbar(rejectionMessage(context, event.reason))
                is ScanEvent.Error ->
                    snackbarHostState.showSnackbar(errorMessage(context, event.error))
            }
        }
    }

    ScanScreen(
        processing = uiState.processing,
        front = uiState.front,
        back = uiState.back,
        snackbarHostState = snackbarHostState,
        onPickFront = { pickFront.launch(ACCEPTED_MIME_TYPES) },
        onPickBack = { pickBack.launch(ACCEPTED_MIME_TYPES) },
        onScan = viewModel::scan,
    )

    uiState.contextError?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissContextError,
            title = { Text(stringResource(R.string.error_context_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissContextError) {
                    Text(stringResource(R.string.dialog_ok))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    processing: Boolean,
    front: PickedDocument?,
    back: PickedDocument?,
    onPickFront: () -> Unit,
    onPickBack: () -> Unit,
    onScan: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.scan_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        ) {
            UploadArea(
                label = stringResource(R.string.scan_front_side),
                thumbnail = rememberThumbnail(front),
                enabled = !processing,
                onClick = onPickFront,
                testTag = "tile.front",
            )

            Spacer(Modifier.height(16.dp))

            UploadArea(
                label = stringResource(R.string.scan_back_side_optional),
                thumbnail = rememberThumbnail(back),
                // Only allow a back side once a front side has been chosen.
                enabled = !processing && front != null,
                onClick = onPickBack,
                testTag = "tile.back",
            )

            Spacer(Modifier.weight(1f))

            if (processing) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.scan_in_progress))
                }
            } else {
                Button(
                    onClick = onScan,
                    enabled = front != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button.scan"),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(stringResource(R.string.scan_button))
                }
            }
        }
    }
}

@Composable
private fun UploadArea(
    label: String,
    thumbnail: ImageBitmap?,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String,
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val disabledColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val borderColor = if (enabled) activeColor else disabledColor
    val contentAlpha = if (enabled) 1f else 0.38f
    val cornerRadius = 12.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .testTag(testTag)
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f),
                    ),
                )
            }
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
            )
            Spacer(Modifier.height(8.dp))
            if (thumbnail != null) {
                Image(
                    bitmap = thumbnail,
                    contentDescription = label,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
            } else {
                Text(
                    text = stringResource(R.string.scan_upload_hint),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                )
            }
        }
    }
}

/**
 * Produce a small preview thumbnail for [document], recomputed only when the picked document
 * changes. Decoding happens off the main thread; the result is null until it is ready, or if
 * [document] is null or cannot be decoded.
 */
@Composable
private fun rememberThumbnail(document: PickedDocument?): ImageBitmap? {
    val thumbnail by produceState<ImageBitmap?>(initialValue = null, document) {
        value = document?.let {
            loadThumbnail(it, Injector.imageLoader, Injector.pdfConverter)?.asImageBitmap()
        }
    }
    return thumbnail
}

private suspend fun loadThumbnail(
    document: PickedDocument,
    imageLoader: ImageLoader,
    pdfConverter: PdfConverter,
): Bitmap? = runCatching {
    val bitmap = when (document) {
        is PickedDocument.Image -> imageLoader.loadBitmap(document.uri)
        is PickedDocument.PdfPage -> pdfConverter.renderPage(document.uri, document.pageIndex)
    }
    bitmap?.scaledForThumbnail()
}.getOrNull()

/**
 * Downscale a full-size decoded bitmap to a preview-sized one so a thumbnail does not hold onto a
 * multi-megapixel allocation. Returns the original if it is already small enough.
 */
private fun Bitmap.scaledForThumbnail(): Bitmap {
    val longestSide = maxOf(width, height)
    if (longestSide <= THUMBNAIL_MAX_PX) return this
    val scale = THUMBNAIL_MAX_PX.toFloat() / longestSide
    return this.scale(
        (width * scale).roundToInt().coerceAtLeast(1),
        (height * scale).roundToInt().coerceAtLeast(1),
    )
}

private fun rejectionMessage(context: Context, reason: RejectionReason): String = when (reason) {
    RejectionReason.TIMEOUT -> context.getString(R.string.error_capture_failed)
    else -> context.getString(R.string.error_document_rejected, reason)
}

private fun errorMessage(context: Context, error: ScanError): String = context.getString(
    when (error) {
        ScanError.READ_FILE -> R.string.error_read_file
        ScanError.PDF_NO_PAGES -> R.string.error_pdf_no_pages
        ScanError.NO_DOCUMENT -> R.string.error_no_document
    },
)

private const val THUMBNAIL_MAX_PX = 512

@Preview(name = "Scan screen – idle", showBackground = true)
@Composable
private fun ScanScreenIdlePreview() {
    AppTheme {
        ScanScreen(
            processing = false,
            front = null,
            back = null,
            onPickFront = {},
            onPickBack = {},
            onScan = {},
        )
    }
}


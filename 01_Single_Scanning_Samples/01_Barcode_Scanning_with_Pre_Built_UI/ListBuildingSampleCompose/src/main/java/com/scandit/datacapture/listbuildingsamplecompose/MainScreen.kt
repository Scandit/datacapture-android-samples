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
package com.scandit.datacapture.listbuildingsamplecompose

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scandit.datacapture.listbuildingsamplecompose.data.ScanResult
import com.scandit.datacapture.listbuildingsamplecompose.ui.ListItem
import com.scandit.datacapture.listbuildingsamplecompose.ui.cropAroundBarcode
import com.scandit.datacapture.listbuildingsamplecompose.ui.DarkGrey
import com.scandit.datacapture.listbuildingsamplecompose.ui.OffWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CROPPED_IMAGE_PADDING = 1.2f
private const val SCALED_IMAGE_SIZE_IN_PIXELS = 100

@Composable
fun MainScreen(padding: PaddingValues) {
    val coroutineScope = rememberCoroutineScope()
    val listData = remember { mutableStateListOf<ScanResult>() }
    val lazyListState = rememberLazyListState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .background(OffWhite)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalQty = listData.sumOf { it.quantity }

            Text(
                text = pluralStringResource(
                    R.plurals.results_amount,
                    totalQty,
                    totalQty,
                ),
                color = DarkGrey,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            TextButton(
                onClick = { listData.clear() },
                enabled = listData.isNotEmpty(),
            ) {
                Text(
                    text = stringResource(R.string.clear_button),
                    color = if (LocalContentColor.current.alpha == 1f) Color.Red else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = listData,
                key = { scanResult ->
                    "${scanResult.barcodeData ?: ""}_${scanResult.barcodeSymbology}"
                }
            ) { scanResult ->
                HorizontalDivider(thickness = 4.dp, color = OffWhite)
                ListItem(scanResult)
            }
        }
    }

    SparkScanComponent(
        padding = padding,
        onValidBarcodeScanned = { barcode, data ->
            data?.retain()

            coroutineScope.launch(Dispatchers.IO) {
                // For simplicity's sake, the generated images are kept in memory.
                // In a real-world scenario, these images would need to be stored in disk and loaded
                // into memory as needed, maybe via an external library, to prevent filling
                // all available memory.
                val src: Bitmap = try {
                    val tmp = data?.getImageBuffer()?.toBitmap()
                    tmp?.copy(tmp.config ?: Bitmap.Config.ARGB_8888, false)
                        ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                } finally {
                    data?.release()
                }

                val image = src.cropAroundBarcode(
                    barcode, SCALED_IMAGE_SIZE_IN_PIXELS, CROPPED_IMAGE_PADDING
                )

                val result = ScanResult(barcode.data, barcode.symbology.name, image)

                withContext(Dispatchers.Main) {
                    for (index in listData.indices) {
                        val next = listData[index]

                        if (next.barcodeData == result.barcodeData &&
                            next.barcodeSymbology == result.barcodeSymbology
                        ) {
                            next.increaseQuantity()
                            next.addImages(result.images)

                            listData.add(0, listData.removeAt(index))
                            lazyListState.animateScrollToItem(0)
                            return@withContext
                        }
                    }

                    listData.add(0, result)
                    lazyListState.animateScrollToItem(0)
                }
            }
        }
    )
}
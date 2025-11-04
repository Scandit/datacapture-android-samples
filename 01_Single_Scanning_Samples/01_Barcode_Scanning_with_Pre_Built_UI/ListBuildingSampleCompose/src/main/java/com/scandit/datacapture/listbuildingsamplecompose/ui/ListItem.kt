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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scandit.datacapture.listbuildingsamplecompose.R
import com.scandit.datacapture.listbuildingsamplecompose.data.ScanResult


@Composable
fun ListItem(data: ScanResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StackedImageView(data.images)

        Column(
            modifier = Modifier
                .height(70.dp)
                .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.item_title, data.barcodeData ?: ""),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
            )
            Text(
                text = stringResource(R.string.item_description, data.barcodeSymbology ?: ""),
                color = Grey,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))

        if (data.quantity > 1) {
            Text(
                text = stringResource(R.string.item_qty, data.quantity),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
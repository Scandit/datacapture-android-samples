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

package com.scandit.datacapture.idcaptureimageuploadsample.ui.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scandit.datacapture.idcaptureimageuploadsample.R
import com.scandit.datacapture.idcaptureimageuploadsample.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: CaptureResult?,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.result_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.result_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (result != null) {
            ResultContent(result = result, contentPadding = padding)
        }
    }
}

@Composable
private fun ResultContent(
    result: CaptureResult,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
    ) {
        itemsIndexed(
            items = result.fields,
            key = { _, item -> item.field },
        ) { index, entry ->
            Column(modifier = Modifier.fillMaxWidth()) {
                ResultItem(entry)
                if (index != result.fields.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ResultItem(entry: CaptureResult.Entry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = stringResource(entry.field.labelRes()), fontWeight = FontWeight.SemiBold)
        Text(
            text = entry.value.ifBlank { stringResource(R.string.result_value_empty) },
            modifier = Modifier.testTag("result.${entry.field.tagName()}"),
        )
    }
}

@StringRes
private fun ResultField.labelRes(): Int = when (this) {
    ResultField.FULL_NAME -> R.string.result_field_full_name
    ResultField.DATE_OF_BIRTH -> R.string.result_field_date_of_birth
    ResultField.DATE_OF_EXPIRY -> R.string.result_field_date_of_expiry
    ResultField.DOCUMENT_NUMBER -> R.string.result_field_document_number
    ResultField.NATIONALITY -> R.string.result_field_nationality
    ResultField.DOCUMENT_TYPE -> R.string.result_field_document_type
}

private fun ResultField.tagName(): String = when (this) {
    ResultField.FULL_NAME -> "fullName"
    ResultField.DATE_OF_BIRTH -> "dateOfBirth"
    ResultField.DATE_OF_EXPIRY -> "dateOfExpiry"
    ResultField.DOCUMENT_NUMBER -> "documentNumber"
    ResultField.NATIONALITY -> "nationality"
    ResultField.DOCUMENT_TYPE -> "documentType"
}

@Preview(name = "Result", showBackground = true)
@Composable
private fun ResultScreenPreview() {
    AppTheme {
        ResultScreen(result = sampleResult(), onBack = {})
    }
}

private fun sampleResult(): CaptureResult {
    return CaptureResult(
        fields = listOf(
            CaptureResult.Entry(ResultField.DATE_OF_BIRTH, "1990-01-01"),
            CaptureResult.Entry(ResultField.DOCUMENT_NUMBER, "X1234567"),
            CaptureResult.Entry(ResultField.NATIONALITY, ""),
        ),
    )
}

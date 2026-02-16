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

package com.scandit.datacapture.idcapturesettingssample.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.scandit.datacapture.idcapturesettingssample.R

@Composable
fun IntegerInputSetting(
    label: String,
    value: Int?,
    maxLength: Int = 10,
    onValueChange: (Int?) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var textValue by rememberSaveable(value) { mutableStateOf(value?.toString() ?: "") }
    var showError by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Setting(
        label = label,
        value = value?.toString() ?: stringResource(R.string.settings_value_none),
        onClick = {
            showDialog = true
            textValue = value?.toString().orEmpty()
            showError = false
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = {
                Column {
                    TextField(
                        value = textValue,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } && newValue.length <= maxLength) {
                                textValue = newValue
                                showError = false
                            }
                        },
                        modifier = Modifier.focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val parsedValue = textValue.toIntOrNull()
                                if (textValue.isEmpty() || parsedValue != null) {
                                    onValueChange(parsedValue)
                                    showDialog = false
                                } else {
                                    showError = true
                                }
                            }
                        ),
                        isError = showError,
                        supportingText = if (showError) {
                            { Text(stringResource(R.string.invalid_number)) }
                        } else {
                            null
                        },
                        singleLine = true
                    )

                    // Request focus when dialog appears
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsedValue = textValue.toIntOrNull()
                        if (textValue.isEmpty() || parsedValue != null) {
                            onValueChange(parsedValue)
                            showDialog = false
                        } else {
                            showError = true
                        }
                    }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
}

fun LazyListScope.integerInputSetting(
    key: String,
    @StringRes labelRes: Int,
    value: Int?,
    maxLength: Int = 10,
    onValueChange: (Int?) -> Unit,
) {
    item(
        key = key,
        contentType = SettingContentType.SETTING
    ) {
        IntegerInputSetting(
            label = stringResource(labelRes),
            value = value,
            maxLength = maxLength,
            onValueChange = onValueChange,
        )
    }
}

@Preview
@Composable
private fun IntegerInputSettingPreview() {
    IntegerInputSetting(
        label = "Rejection Timeout",
        value = 14,
        onValueChange = {},
    )
}

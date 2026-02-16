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

@Composable
fun TextInputSetting(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var textValue by rememberSaveable(value) { mutableStateOf(value) }
    val focusRequester = remember { FocusRequester() }

    Setting(
        label = label,
        value = value,
        onClick = {
            showDialog = true
            textValue = value
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
                        onValueChange = { textValue = it },
                        modifier = Modifier.focusRequester(focusRequester),
                        placeholder = { Text(label) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onValueChange(textValue)
                                showDialog = false
                            }
                        ),
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
                        onValueChange(textValue)
                        showDialog = false
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

fun LazyListScope.textInputSetting(
    key: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    item(
        key = key,
        contentType = SettingContentType.SETTING
    ) {
        TextInputSetting(
            label = label,
            value = value,
            onValueChange = onValueChange,
        )
    }
}

fun LazyListScope.textInputSetting(
    key: String,
    @StringRes labelRes: Int,
    value: String,
    onValueChange: (String) -> Unit,
) {
    item(
        key = key,
        contentType = SettingContentType.SETTING
    ) {
        TextInputSetting(
            label = stringResource(labelRes),
            value = value,
            onValueChange = onValueChange,
        )
    }
}

@Preview
@Composable
private fun TextInputSettingPreview() {
    TextInputSetting(
        label = "Viewfinder Front Text",
        value = "Scan front of document",
        onValueChange = {},
    )
}

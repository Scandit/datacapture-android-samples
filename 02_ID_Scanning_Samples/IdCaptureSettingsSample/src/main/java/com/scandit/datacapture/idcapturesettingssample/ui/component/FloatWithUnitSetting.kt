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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scandit.datacapture.core.common.geometry.MeasureUnit
import com.scandit.datacapture.idcapturesettingssample.utils.toNameCase
import java.util.Locale
import com.scandit.datacapture.idcapturesettingssample.R

@Composable
fun FloatWithUnitSetting(
    label: String,
    floatValue: Float,
    measureUnit: MeasureUnit,
    supportedUnits: List<MeasureUnit>,
    onFloatValueChange: (Float) -> Unit,
    onMeasureUnitChange: (MeasureUnit) -> Unit,
) {
    var showValueDialog by rememberSaveable { mutableStateOf(false) }
    var showUnitDialog by rememberSaveable { mutableStateOf(false) }
    var textValue by rememberSaveable(floatValue) { mutableStateOf(floatValue.toString()) }
    var showError by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val formattedValue = remember(floatValue) {
        String.format(Locale.US, "%.2f", floatValue)
    }

    ListItem(
        modifier = Modifier.clickable { showValueDialog = true },
        headlineContent = { Text(label) },
        supportingContent = {
            Text(formattedValue)
        },
        trailingContent = {
            InputChip(
                selected = false,
                onClick = { showUnitDialog = true },
                label = { Text(measureUnit.name.toNameCase()) }
            )
        }
    )

    if (showValueDialog) {
        AlertDialog(
            onDismissRequest = { showValueDialog = false },
            title = { Text(label) },
            text = {
                Column {
                    TextField(
                        value = textValue,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                                textValue = newValue
                                showError = false
                            }
                        },
                        modifier = Modifier.focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val parsedValue = textValue.toFloatOrNull()
                                if (parsedValue != null) {
                                    onFloatValueChange(parsedValue)
                                    showValueDialog = false
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
                        val parsedValue = textValue.toFloatOrNull()
                        if (parsedValue != null) {
                            onFloatValueChange(parsedValue)
                            showValueDialog = false
                        } else {
                            showError = true
                        }
                    }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showValueDialog = false
                        textValue = floatValue.toString()
                        showError = false
                    }
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }

    if (showUnitDialog) {
        AlertDialog(
            onDismissRequest = { showUnitDialog = false },
            title = { Text("$label - Unit") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    supportedUnits.forEach { unit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onMeasureUnitChange(unit)
                                    showUnitDialog = false
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = unit == measureUnit,
                                onClick = {
                                    onMeasureUnitChange(unit)
                                    showUnitDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(unit.name.toNameCase())
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

fun LazyListScope.floatWithUnitSetting(
    key: String,
    @StringRes labelRes: Int,
    floatValue: Float,
    measureUnit: MeasureUnit,
    supportedUnits: List<MeasureUnit>,
    onFloatValueChange: (Float) -> Unit,
    onMeasureUnitChange: (MeasureUnit) -> Unit,
) {
    item(
        key = key,
        contentType = SettingContentType.SETTING
    ) {
        FloatWithUnitSetting(
            label = stringResource(labelRes),
            floatValue = floatValue,
            measureUnit = measureUnit,
            supportedUnits = supportedUnits,
            onFloatValueChange = onFloatValueChange,
            onMeasureUnitChange = onMeasureUnitChange,
        )
    }
}

@Preview
@Composable
private fun FloatWithUnitSettingPreview() {
    FloatWithUnitSetting(
        label = "Logo Anchor Offset X",
        floatValue = 5.25f,
        measureUnit = MeasureUnit.FRACTION,
        supportedUnits = MeasureUnit.entries,
        onFloatValueChange = {},
        onMeasureUnitChange = {},
    )
}

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scandit.datacapture.core.source.CameraPosition
import com.scandit.datacapture.idcapturesettingssample.utils.toNameCase

@Composable
fun <T> MultiSelectDropdownSetting(
    label: String,
    selectedValues: Set<T>,
    options: List<T>,
    displayName: (T) -> String,
    onValuesChange: (Set<T>) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val displayValue = when {
        selectedValues.isEmpty() -> "None"
        else -> selectedValues.joinToString(", ") { displayName(it) }
    }

    Setting(
        label = label,
        value = displayValue,
        onClick = { showDialog = true }
    )

    if (showDialog) {
        var tempSelection by rememberSaveable { mutableStateOf(selectedValues) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempSelection = if (option in tempSelection) {
                                        tempSelection - option
                                    } else {
                                        tempSelection + option
                                    }
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = option in tempSelection,
                                onCheckedChange = { checked ->
                                    tempSelection = if (checked) {
                                        tempSelection + option
                                    } else {
                                        tempSelection - option
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(displayName(option))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValuesChange(tempSelection)
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
inline fun <reified T : Enum<T>> EnumMultiSelectDropdownSetting(
    label: String,
    selectedValues: Set<T>,
    noinline onValuesChange: (Set<T>) -> Unit
) {
    MultiSelectDropdownSetting(
        label = label,
        selectedValues = selectedValues,
        options = enumValues<T>().toList(),
        displayName = { it.name.toNameCase() },
        onValuesChange = onValuesChange
    )
}

inline fun <reified T : Enum<T>> LazyListScope.enumMultiSelectDropdownSetting(
    key: String,
    @StringRes labelRes: Int,
    selectedValues: Set<T>,
    noinline onValuesChange: (Set<T>) -> Unit
) {
    item(
        key = key,
        contentType = SettingContentType.SETTING,
    ) {
        EnumMultiSelectDropdownSetting(
            label = stringResource(labelRes),
            selectedValues = selectedValues,
            onValuesChange = onValuesChange,
        )
    }
}

@Preview
@Composable
private fun EnumMultiSelectDropdownSettingPreview() {
    EnumMultiSelectDropdownSetting(
        label = "Camera Positions",
        selectedValues = setOf(CameraPosition.WORLD_FACING, CameraPosition.USER_FACING),
        onValuesChange = {},
    )
}

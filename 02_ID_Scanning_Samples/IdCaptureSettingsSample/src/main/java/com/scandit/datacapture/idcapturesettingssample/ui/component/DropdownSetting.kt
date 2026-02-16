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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
fun <T> DropdownSetting(
    label: String,
    value: T,
    options: List<T>,
    displayName: (T) -> String,
    onValueChange: (T) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Setting(
        label = label,
        value = displayName(value),
        onClick = { showDialog = true }
    )

    if (showDialog) {
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
                                    onValueChange(option)
                                    showDialog = false
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = option == value,
                                onClick = {
                                    onValueChange(option)
                                    showDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(displayName(option))
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
inline fun <reified T : Enum<T>> EnumDropdownSetting(
    label: String,
    value: T,
    noinline onValueChange: (T) -> Unit
) {
    DropdownSetting(
        label = label,
        value = value,
        options = enumValues<T>().toList(),
        displayName = { it.name.toNameCase() },
        onValueChange = onValueChange
    )
}

inline fun <reified T : Enum<T>> LazyListScope.enumDropdownSetting(
    key: String,
    @StringRes labelRes: Int,
    value: T,
    noinline onValueChange: (T) -> Unit
) {
    item(
        key = key,
        contentType = SettingContentType.SETTING,
    ) {
        EnumDropdownSetting(
            label = stringResource(labelRes),
            value = value,
            onValueChange = onValueChange,
        )
    }
}

@Preview
@Composable
private fun EnumDropdownSettingPreview() {
    EnumDropdownSetting(
        label = "Camera Position",
        value = CameraPosition.WORLD_FACING,
        onValueChange = {},
    )
}
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SwitchSetting(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    onClick: () -> Unit = { onCheckedChange(!checked) },
) {
    ListItem(
        modifier = modifier.clickable { onClick() },
        headlineContent = { Text(label) },
        supportingContent = supportingText?.let { { Text(supportingText)} },
        trailingContent = {
            Switch(
                modifier = Modifier.semantics { contentDescription = label },
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    )
}

fun LazyListScope.switchSetting(
    key: String,
    @StringRes labelRes: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    supportingText: String? = null,
) {
    item(
        key = key,
        contentType = SettingContentType.SWITCH
    ) {
        SwitchSetting(
            label = stringResource(labelRes),
            supportingText = supportingText,
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

fun LazyListScope.switchSetting(
    key: String,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    supportingText: String? = null,
    onClick: () -> Unit = { onCheckedChange(!checked) },
) {
    item(
        key = key,
        contentType = SettingContentType.SWITCH
    ) {
        SwitchSetting(
            label = label,
            supportingText = supportingText,
            checked = checked,
            onClick = onClick,
            onCheckedChange = onCheckedChange,
        )
    }
}


@Preview
@Composable
private fun SingleRowSwitchSettingPreview() {
    SwitchSetting(
        label = "Torch enabled",
        checked = true,
        onCheckedChange = {},
    )
}

@Preview
@Composable
private fun TwoRowSwitchSettingPreview() {
    SwitchSetting(
        label = "Driver License",
        supportingText = "Any region",
        checked = true,
        onCheckedChange = {},
    )
}

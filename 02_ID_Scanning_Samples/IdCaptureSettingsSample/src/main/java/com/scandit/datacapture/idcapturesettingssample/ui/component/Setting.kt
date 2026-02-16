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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Setting(
    label: String,
    value: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick),
        headlineContent = { Text(label) },
        supportingContent = value.takeIf { it.isNotBlank() }?.let { { Text(it) } }
    )
}

fun LazyListScope.setting(
    key: String,
    @StringRes labelRes: Int,
    summary: String = "",
    onClick: () -> Unit,
) {
    item(
        key = key,
        contentType = SettingContentType.SETTING,
    ) {
        Setting(
            label = stringResource(labelRes),
            value = summary,
            onClick = onClick,
        )
    }
}

@Preview
@Composable
private fun SettingPreview() {
    Setting(
        label = "Logo Anchor",
        value = "Bottom Right",
        onClick = {},
    )
}

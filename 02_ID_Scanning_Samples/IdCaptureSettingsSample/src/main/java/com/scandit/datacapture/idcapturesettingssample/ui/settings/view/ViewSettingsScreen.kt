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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.view

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scandit.datacapture.core.common.geometry.Anchor
import com.scandit.datacapture.core.common.geometry.MeasureUnit
import com.scandit.datacapture.id.ui.IdLayoutLineStyle
import com.scandit.datacapture.id.ui.IdLayoutStyle
import com.scandit.datacapture.id.ui.TextHintPosition
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.BrushStyle
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.ui.component.BackButton
import com.scandit.datacapture.idcapturesettingssample.ui.component.SettingsScaffold
import com.scandit.datacapture.idcapturesettingssample.ui.component.enumDropdownSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.floatWithUnitSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.sectionHeader
import com.scandit.datacapture.idcapturesettingssample.ui.component.switchSetting
import com.scandit.datacapture.idcapturesettingssample.ui.component.textInputSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: ViewSettingsViewModel = viewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    ViewSettingsScreen(
        settings = settings,
        onBackClick = onBackClick,
        onOverlayStyleChanged = viewModel::onOverlayStyleChanged,
        onOverlayLineStyleChanged = viewModel::onOverlayLineStyleChanged,
        onCapturedBrushChanged = viewModel::onCapturedBrushChanged,
        onTextHintPositionChanged = viewModel::onTextHintPositionChanged,
        onShowTextHintsChanged = viewModel::onShowTextHintsChanged,
        onViewfinderFrontTextChanged = viewModel::onViewfinderFrontTextChanged,
        onViewfinderBackTextChanged = viewModel::onViewfinderBackTextChanged,
        onLogoAnchorChanged = viewModel::onLogoAnchorChanged,
        onLogoAnchorOffsetXValueChanged = viewModel::onLogoAnchorOffsetXValueChanged,
        onLogoAnchorOffsetXUnitChanged = viewModel::onLogoAnchorOffsetXUnitChanged,
        onLogoAnchorOffsetYValueChanged = viewModel::onLogoAnchorOffsetYValueChanged,
        onLogoAnchorOffsetYUnitChanged = viewModel::onLogoAnchorOffsetYUnitChanged,
        onTapToFocusChanged = viewModel::onTapToFocusChanged,
        onTorchControlChanged = viewModel::onTorchControlChanged,
    )
}

@Composable
private fun ViewSettingsScreen(
    settings: Settings.ViewSettings,
    onBackClick: () -> Unit,
    onOverlayStyleChanged: (IdLayoutStyle) -> Unit,
    onOverlayLineStyleChanged: (IdLayoutLineStyle) -> Unit,
    onCapturedBrushChanged: (BrushStyle) -> Unit,
    onTextHintPositionChanged: (TextHintPosition) -> Unit,
    onShowTextHintsChanged: (Boolean) -> Unit,
    onViewfinderFrontTextChanged: (String) -> Unit,
    onViewfinderBackTextChanged: (String) -> Unit,
    onLogoAnchorChanged: (Anchor) -> Unit,
    onLogoAnchorOffsetXValueChanged: (Float) -> Unit,
    onLogoAnchorOffsetXUnitChanged: (MeasureUnit) -> Unit,
    onLogoAnchorOffsetYValueChanged: (Float) -> Unit,
    onLogoAnchorOffsetYUnitChanged: (MeasureUnit) -> Unit,
    onTapToFocusChanged: (Boolean) -> Unit,
    onTorchControlChanged: (Boolean) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.view_category_title),
        navigationIcon = { BackButton(onBackClick) },
    ) {
        overlaySettings(
            settings = settings,
            onOverlayStyleChanged = onOverlayStyleChanged,
            onOverlayLineStyleChanged = onOverlayLineStyleChanged,
            onCapturedBrushChanged = onCapturedBrushChanged,
            onTextHintPositionChanged = onTextHintPositionChanged,
            onShowTextHintsChanged = onShowTextHintsChanged,
            onViewfinderFrontTextChanged = onViewfinderFrontTextChanged,
            onViewfinderBackTextChanged = onViewfinderBackTextChanged
        )

        logoSettings(
            settings = settings,
            onLogoAnchorChanged = onLogoAnchorChanged,
            onLogoAnchorOffsetXValueChanged = onLogoAnchorOffsetXValueChanged,
            onLogoAnchorOffsetXUnitChanged = onLogoAnchorOffsetXUnitChanged,
            onLogoAnchorOffsetYValueChanged = onLogoAnchorOffsetYValueChanged,
            onLogoAnchorOffsetYUnitChanged = onLogoAnchorOffsetYUnitChanged
        )

        gesturesSettings(
            settings = settings,
            onTapToFocusChanged = onTapToFocusChanged
        )
        controlsSettings(
            settings = settings,
            onTorchControlChanged = onTorchControlChanged
        )
    }
}

private fun LazyListScope.overlaySettings(
    settings: Settings.ViewSettings,
    onOverlayStyleChanged: (IdLayoutStyle) -> Unit,
    onOverlayLineStyleChanged: (IdLayoutLineStyle) -> Unit,
    onCapturedBrushChanged: (BrushStyle) -> Unit,
    onTextHintPositionChanged: (TextHintPosition) -> Unit,
    onShowTextHintsChanged: (Boolean) -> Unit,
    onViewfinderFrontTextChanged: (String) -> Unit,
    onViewfinderBackTextChanged: (String) -> Unit,
) {
    sectionHeader(
        key = "overlay_header",
        textRes = R.string.overlay_category_title
    )

    enumDropdownSetting(
        key = "overlay_style",
        labelRes = R.string.overlay_style_title,
        value = settings.overlayStyle,
        onValueChange = onOverlayStyleChanged,
    )

    enumDropdownSetting(
        key = "overlay_line_style",
        labelRes = R.string.overlay_line_style_title,
        value = settings.overlayLineStyle,
        onValueChange = onOverlayLineStyleChanged,
    )

    enumDropdownSetting(
        key = "captured_brush",
        labelRes = R.string.captured_brush_title,
        value = settings.capturedBrushStyle,
        onValueChange = onCapturedBrushChanged,
    )

    enumDropdownSetting(
        key = "text_hint_position",
        labelRes = R.string.text_hint_position_title,
        value = settings.textHintPosition,
        onValueChange = onTextHintPositionChanged,
    )

    switchSetting(
        key = "show_text_hints",
        labelRes = R.string.show_text_hints_title,
        checked = settings.showTextHints,
        onCheckedChange = onShowTextHintsChanged,
    )

    textInputSetting(
        key = "viewfinder_front_text",
        labelRes = R.string.viewfinder_front_text_title,
        value = settings.viewfinderFrontText,
        onValueChange = onViewfinderFrontTextChanged,
    )

    textInputSetting(
        key = "viewfinder_back_text",
        labelRes = R.string.viewfinder_back_text_title,
        value = settings.viewfinderBackText,
        onValueChange = onViewfinderBackTextChanged,
    )
}

private fun LazyListScope.logoSettings(
    settings: Settings.ViewSettings,
    onLogoAnchorChanged: (Anchor) -> Unit,
    onLogoAnchorOffsetXValueChanged: (Float) -> Unit,
    onLogoAnchorOffsetXUnitChanged: (MeasureUnit) -> Unit,
    onLogoAnchorOffsetYValueChanged: (Float) -> Unit,
    onLogoAnchorOffsetYUnitChanged: (MeasureUnit) -> Unit,
) {
    sectionHeader(
        key = "logo_header",
        textRes = R.string.logo_category_title
    )

    enumDropdownSetting(
        key = "logo_anchor",
        labelRes = R.string.logo_anchor_title,
        value = settings.logoAnchor,
        onValueChange = onLogoAnchorChanged,
    )

    floatWithUnitSetting(
        key = "logo_anchor_offset_x",
        labelRes = R.string.logo_anchor_offset_x_title,
        floatValue = settings.logoAnchorOffsetX.value,
        measureUnit = settings.logoAnchorOffsetX.unit,
        supportedUnits = MeasureUnit.entries,
        onFloatValueChange = onLogoAnchorOffsetXValueChanged,
        onMeasureUnitChange = onLogoAnchorOffsetXUnitChanged,
    )

    floatWithUnitSetting(
        key = "logo_anchor_offset_y",
        labelRes = R.string.logo_anchor_offset_y_title,
        floatValue = settings.logoAnchorOffsetY.value,
        measureUnit = settings.logoAnchorOffsetY.unit,
        supportedUnits = MeasureUnit.entries,
        onFloatValueChange = onLogoAnchorOffsetYValueChanged,
        onMeasureUnitChange = onLogoAnchorOffsetYUnitChanged,
    )
}

private fun LazyListScope.gesturesSettings(
    settings: Settings.ViewSettings,
    onTapToFocusChanged: (Boolean) -> Unit,
) {
    sectionHeader(
        key = "gestures_header",
        textRes = R.string.gestures_category_title
    )

    switchSetting(
        key = "tap_to_focus",
        labelRes = R.string.tap_to_focus_title,
        checked = settings.isTapToFocusEnabled,
        onCheckedChange = onTapToFocusChanged,
    )
}

private fun LazyListScope.controlsSettings(
    settings: Settings.ViewSettings,
    onTorchControlChanged: (Boolean) -> Unit,
) {
    sectionHeader(
        key = "controls_header",
        textRes = R.string.controls_category_title
    )

    switchSetting(
        key = "torch_control",
        labelRes = R.string.torch_control_title,
        checked = settings.isTorchControlEnabled,
        onCheckedChange = onTorchControlChanged,
    )
}

@Preview
@Composable
private fun ViewSettingsScreenPreview() {
    ViewSettingsScreen(
        settings = Settings.ViewSettings(),
        onBackClick = {},
        onOverlayStyleChanged = {},
        onOverlayLineStyleChanged = {},
        onCapturedBrushChanged = {},
        onTextHintPositionChanged = {},
        onShowTextHintsChanged = {},
        onViewfinderFrontTextChanged = {},
        onViewfinderBackTextChanged = {},
        onLogoAnchorChanged = {},
        onLogoAnchorOffsetXValueChanged = {},
        onLogoAnchorOffsetXUnitChanged = {},
        onLogoAnchorOffsetYValueChanged = {},
        onLogoAnchorOffsetYUnitChanged = {},
        onTapToFocusChanged = {},
        onTorchControlChanged = {}
    )
}

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
import com.scandit.datacapture.core.common.geometry.Anchor
import com.scandit.datacapture.core.common.geometry.FloatWithUnit
import com.scandit.datacapture.core.common.geometry.MeasureUnit
import com.scandit.datacapture.id.ui.IdLayoutLineStyle
import com.scandit.datacapture.id.ui.IdLayoutStyle
import com.scandit.datacapture.id.ui.TextHintPosition
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.model.BrushStyle
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
    settingsRepository: SettingsRepository,
    onBackClick: () -> Unit,
) {
    val settings by settingsRepository.settingsStream.collectAsStateWithLifecycle()

    ViewSettingsScreen(
        settings = settings,
        onBackClick = onBackClick,
        onOverlayStyleChanged = { value ->
            settingsRepository.updateView {
                it.copy(overlayStyle = value)
            }
        },
        onOverlayLineStyleChanged = { value ->
            settingsRepository.updateView {
                it.copy(overlayLineStyle = value)
            }
        },
        onCapturedBrushChanged = { value ->
            settingsRepository.updateView {
                it.copy(capturedBrushStyle = value)
            }
        },
        onTextHintPositionChanged = { value ->
            settingsRepository.updateView {
                it.copy(textHintPosition = value)
            }
        },
        onShowTextHintsChanged = { value ->
            settingsRepository.updateView {
                it.copy(showTextHints = value)
            }
        },
        onViewfinderFrontTextChanged = { value ->
            settingsRepository.updateView {
                it.copy(viewfinderFrontText = value)
            }
        },
        onViewfinderBackTextChanged = { value ->
            settingsRepository.updateView {
                it.copy(viewfinderBackText = value)
            }
        },
        onLogoAnchorChanged = { value ->
            settingsRepository.updateView {
                it.copy(logoAnchor = value)
            }
        },
        onLogoAnchorOffsetXValueChanged = { value ->
            settingsRepository.updateView {
                it.copy(logoAnchorOffsetX = FloatWithUnit(value, it.logoAnchorOffsetX.unit))
            }
        },
        onLogoAnchorOffsetXUnitChanged = { value ->
            settingsRepository.updateView {
                it.copy(logoAnchorOffsetX = FloatWithUnit(it.logoAnchorOffsetX.value, value))
            }
        },
        onLogoAnchorOffsetYValueChanged = { value ->
            settingsRepository.updateView {
                it.copy(logoAnchorOffsetY = FloatWithUnit(value, it.logoAnchorOffsetY.unit))
            }
        },
        onLogoAnchorOffsetYUnitChanged = { value ->
            settingsRepository.updateView {
                it.copy(logoAnchorOffsetY = FloatWithUnit(it.logoAnchorOffsetY.value, value))
            }
        },
        onTapToFocusChanged = { value ->
            settingsRepository.updateView {
                it.copy(isTapToFocusEnabled = value)
            }
        },
        onTorchControlChanged = { value ->
            settingsRepository.updateView {
                it.copy(isTorchControlEnabled = value)
            }
        },
    )
}

@Composable
private fun ViewSettingsScreen(
    settings: Settings,
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
    settings: Settings,
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
        value = settings.view.overlayStyle,
        onValueChange = onOverlayStyleChanged,
    )

    enumDropdownSetting(
        key = "overlay_line_style",
        labelRes = R.string.overlay_line_style_title,
        value = settings.view.overlayLineStyle,
        onValueChange = onOverlayLineStyleChanged,
    )

    enumDropdownSetting(
        key = "captured_brush",
        labelRes = R.string.captured_brush_title,
        value = settings.view.capturedBrushStyle,
        onValueChange = onCapturedBrushChanged,
    )

    enumDropdownSetting(
        key = "text_hint_position",
        labelRes = R.string.text_hint_position_title,
        value = settings.view.textHintPosition,
        onValueChange = onTextHintPositionChanged,
    )

    switchSetting(
        key = "show_text_hints",
        labelRes = R.string.show_text_hints_title,
        checked = settings.view.showTextHints,
        onCheckedChange = onShowTextHintsChanged,
    )

    textInputSetting(
        key = "viewfinder_front_text",
        labelRes = R.string.viewfinder_front_text_title,
        value = settings.view.viewfinderFrontText,
        onValueChange = onViewfinderFrontTextChanged,
    )

    textInputSetting(
        key = "viewfinder_back_text",
        labelRes = R.string.viewfinder_back_text_title,
        value = settings.view.viewfinderBackText,
        onValueChange = onViewfinderBackTextChanged,
    )
}

private fun LazyListScope.logoSettings(
    settings: Settings,
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
        value = settings.view.logoAnchor,
        onValueChange = onLogoAnchorChanged,
    )

    floatWithUnitSetting(
        key = "logo_anchor_offset_x",
        labelRes = R.string.logo_anchor_offset_x_title,
        floatValue = settings.view.logoAnchorOffsetX.value,
        measureUnit = settings.view.logoAnchorOffsetX.unit,
        supportedUnits = MeasureUnit.entries,
        onFloatValueChange = onLogoAnchorOffsetXValueChanged,
        onMeasureUnitChange = onLogoAnchorOffsetXUnitChanged,
    )

    floatWithUnitSetting(
        key = "logo_anchor_offset_y",
        labelRes = R.string.logo_anchor_offset_y_title,
        floatValue = settings.view.logoAnchorOffsetY.value,
        measureUnit = settings.view.logoAnchorOffsetY.unit,
        supportedUnits = MeasureUnit.entries,
        onFloatValueChange = onLogoAnchorOffsetYValueChanged,
        onMeasureUnitChange = onLogoAnchorOffsetYUnitChanged,
    )
}

private fun LazyListScope.gesturesSettings(
    settings: Settings,
    onTapToFocusChanged: (Boolean) -> Unit,
) {
    sectionHeader(
        key = "gestures_header",
        textRes = R.string.gestures_category_title
    )

    switchSetting(
        key = "tap_to_focus",
        labelRes = R.string.tap_to_focus_title,
        checked = settings.view.isTapToFocusEnabled,
        onCheckedChange = onTapToFocusChanged,
    )
}

private fun LazyListScope.controlsSettings(
    settings: Settings,
    onTorchControlChanged: (Boolean) -> Unit,
) {
    sectionHeader(
        key = "controls_header",
        textRes = R.string.controls_category_title
    )

    switchSetting(
        key = "torch_control",
        labelRes = R.string.torch_control_title,
        checked = settings.view.isTorchControlEnabled,
        onCheckedChange = onTorchControlChanged,
    )
}

@Preview
@Composable
private fun ViewSettingsScreenPreview() {
    ViewSettingsScreen(
        settings = Settings(),
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

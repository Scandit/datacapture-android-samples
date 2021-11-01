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

package com.scandit.datacapture.idcapturesettingssample.data;

import com.scandit.datacapture.core.common.geometry.Anchor;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.source.CameraPosition;
import com.scandit.datacapture.core.source.VideoResolution;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.id.data.IdDocumentType;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.SupportedSides;
import com.scandit.datacapture.id.ui.IdLayoutLineStyle;
import com.scandit.datacapture.id.ui.IdLayoutStyle;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.idcapturesettingssample.ui.BrushStyle;
import com.scandit.datacapture.idcapturesettingssample.utils.EnumUtils;

/**
 * Class containing the allowed and default preference values needed to programmatically setup the
 * settings fragment.
 */
public final class Defaults {

    // IdCapture.
    private static final IdDocumentType[] SUPPORTED_DOCUMENTS = IdDocumentType.values();
    private static final SupportedSides[] SUPPORTED_SIDES = SupportedSides.values();
    private static final IdImageType[] SUPPORTED_IMAGES = IdImageType.values();

    public static String[] getSupportedDocumentsEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_DOCUMENTS);
    }

    public static String[] getSupportedDocumentsValues() {
        return EnumUtils.getEntryNames(SUPPORTED_DOCUMENTS);
    }

    public static String[] getSupportedSidesEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_SIDES);
    }

    public static String[] getSupportedSidesValues() {
        return EnumUtils.getEntryNames(SUPPORTED_SIDES);
    }

    public static SupportedSides getDefaultSupportedSides() {
        return SupportedSides.FRONT_ONLY;
    }

    public static String[] getSupportedImagesEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_IMAGES);
    }

    public static String[] getSupportedImagesValues() {
        return EnumUtils.getEntryNames(SUPPORTED_IMAGES);
    }

    // Camera.
    private final static CameraPosition[] SUPPORTED_POSITIONS = new CameraPosition[] { CameraPosition.WORLD_FACING, CameraPosition.USER_FACING };
    private final static VideoResolution[] SUPPORTED_RESOLUTIONS = VideoResolution.values();

    public static String[] getSupportedCameraPositionEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_POSITIONS);
    }
    public static String[] getSupportedCameraPositionValues() {
        return EnumUtils.getEntryNames(SUPPORTED_POSITIONS);
    }

    public static CameraPosition getDefaultCameraPosition() {
        return CameraPosition.WORLD_FACING;
    }

    public static boolean getTorchEnabledDefault() {
        return false;
    }

    public static String[] getSupportedResolutionsEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_RESOLUTIONS);
    }

    public static String[] getSupportedResolutionsValues() {
        return EnumUtils.getEntryNames(SUPPORTED_RESOLUTIONS);
    }

    public static VideoResolution getDefaultResolution() {
        return VideoResolution.AUTO;
    }

    public static int getMinZoomFactor() {
        return 1;
    }

    public static int getMaxZoomFactor() {
        return 20;
    }

    public static int getDefaultZoomFactor() {
        return 1;
    }

    // Logo.
    private final static Anchor[] SUPPORTED_LOGO_ANCHORS = Anchor.values();
    private final static MeasureUnit[] SUPPORTED_MEASURE_UNITS = MeasureUnit.values();

    public static String[] getSupportedLogoAnchorsEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_LOGO_ANCHORS);
    }

    public static String[] getSupportedLogoAnchorsValues() {
        return EnumUtils.getEntryNames(SUPPORTED_LOGO_ANCHORS);
    }

    public static Anchor getDefaultLogoAnchor() {
        return Anchor.BOTTOM_RIGHT;
    }

    public static String[] getSupportedMeasureUnitEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_MEASURE_UNITS);
    }

    public static String[] getSupportedMeasureUnitValues() {
        return EnumUtils.getEntryNames(SUPPORTED_MEASURE_UNITS);
    }

    public static FloatWithUnit getDefaultLogoAnchorX() {
        return new FloatWithUnit(0, MeasureUnit.FRACTION);
    }

    public static FloatWithUnit getDefaultLogoAnchorY() {
        return new FloatWithUnit(0, MeasureUnit.FRACTION);
    }

    // Overlay.
    private static final IdLayoutStyle[] SUPPORTED_OVERLAY_STYLES = IdLayoutStyle.values();
    private static final IdLayoutLineStyle[] SUPPORTED_OVERLAY_LINE_STYLES = IdLayoutLineStyle.values();
    private static final BrushStyle[] SUPPORTED_OVERLAY_CAPTURED_BRUSHES = BrushStyle.values();
    private static final Brush DEFAULT_CAPTURED_BRUSH = IdCaptureOverlay.DEFAULT_CAPTURED_BRUSH;

    public static String[] getSupportedOverlayStylesEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_OVERLAY_STYLES);
    }

    public static String[] getSupportedOverlayStylesValues() {
        return EnumUtils.getEntryNames(SUPPORTED_OVERLAY_STYLES);
    }

    public static IdLayoutStyle getDefaultOverlayStyle() {
        return IdLayoutStyle.ROUNDED;
    }

    public static String[] getSupportedOverlayLineStylesEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_OVERLAY_LINE_STYLES);
    }

    public static String[] getSupportedOverlayLineStylesValues() {
        return EnumUtils.getEntryNames(SUPPORTED_OVERLAY_LINE_STYLES);
    }

    public static IdLayoutLineStyle getDefaultOverlayLineStyle() {
        return IdLayoutLineStyle.BOLD;
    }

    public static String[] getSupportedCapturedBrushEntries() {
        return EnumUtils.getEntryNamesTitleCase(SUPPORTED_OVERLAY_CAPTURED_BRUSHES);
    }

    public static String[] getSupportedCapturedBrushValues() {
        return EnumUtils.getEntryNames(SUPPORTED_OVERLAY_CAPTURED_BRUSHES);
    }

    public static BrushStyle getDefaultCapturedBrushStyle() {
        return BrushStyle.DEFAULT;
    }

    public static Brush getDefaultBrush() {
        return DEFAULT_CAPTURED_BRUSH;
    }

    // Gestures.
    public static boolean getDefaultTapToFocusEnabled() {
        return true;
    }

    public static boolean getDefaultSwipeToZoomEnabled() {
        return true;
    }

    // Controls.
    public static boolean getDefaultTorchControlEnabled() {
        return false;
    }

    // Result.
    public static boolean getDefaultContinuousScanEnabled() {
        return false;
    }
}

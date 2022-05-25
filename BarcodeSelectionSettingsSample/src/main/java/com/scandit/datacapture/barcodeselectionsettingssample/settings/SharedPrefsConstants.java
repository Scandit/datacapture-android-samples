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

package com.scandit.datacapture.barcodeselectionsettingssample.settings;

import com.scandit.datacapture.barcode.data.Symbology;

public final class SharedPrefsConstants {
    public static final String SHARED_PREFS_NAME = "barcode-selection-settings-prefs";

    public static String getFloatWithUnitCategoryKey(String prefix) {
        return prefix + "-category";
    }
    public static String getFloatWithUnitNumberKey(String prefix) {
        return prefix + "-value";
    }
    public static String getFloatWithUnitMeasureUnitKey(String prefix) {
        return prefix + "-measure-unit";
    }

    // Overview.
    public static final String OVERVIEW_CATEGORY_KEY = "key_overview_category";
    public static final String BARCODE_SELECTION_KEY = "key_barcode_selection";
    public static final String CAMERA_KEY = "key_camera";
    public static final String VIEW_KEY = "key_view";
    public static final String DATACAPTURE_VERSION_KEY = "key_scandit_version";

    // Barcode Selection.
    public static final String SYMBOLOGIES_KEY = "key_symbologies";
    public static final String SELECTION_TYPE_KEY = "key_selection_type";
    public static final String FEEDBACK_KEY = "key_feedback";
    public static final String SINGLE_BARCODE_AUTO_DETECTION_KEY = "key_single_barcode_auto_detection";
    public static final String DUPLICATE_FILTER_OVERVIEW_KEY = "key_duplicate_filter";
    public static final String POINT_OF_INTEREST_KEY = "key_point_of_interest";

    // Symbologies.
    public static final String ENABLE_DISABLE_ALL_CATEGORY_KEY = "key_enable_disable_all_category";
    public static final String ENABLE_ALL_KEY = "key_enable_all";
    public static final String DISABLE_ALL_KEY = "key_disable_all";
    public static final String SYMBOLOGIES_LIST_CATEGORY_KEY = "key_symbology_list_category";

    // Symbology Detail.
    public static final String SYMBOLOGY_ENABLED_CATEGORY_KEY = "key_symbology_enabled_category";
    public static final String SYMBOLOGY_RANGE_CATEGORY_KEY = "key_symbology_range_category";
    public static final String SYMBOLOGY_EXTENSIONS_CATEGORY_KEY = "key_symbology_extensions_category";
    public static String getSymbologyEnabledKey(Symbology symbology) { return symbology.name() + "-enabled"; }
    public static String getSymbologyColorInvertedEnabledKey(Symbology symbology) { return symbology.name() + "-color-inverted-enabled"; }
    public static String getSymbologyMinRangeKey(Symbology symbology) { return symbology.name() + "-min-range"; }
    public static String getSymbologyMaxRangeKey(Symbology symbology) { return symbology.name() + "-max-range"; }
    public static String getSymbologyExtensionKey(Symbology symbology, String extension) { return symbology.name() + "-extension-" + extension; }

    // Auto Detection.
    public static final String AUTO_DETECTION_CATEGORY_KEY = "key_auto_detection_category";
    public static final String AUTO_DETECTION_KEY = "key_auto_detection";

    // Feedback
    public static final String FEEDBACK_CATEGORY_KEY = "key_feedback_category";
    public static final String FEEDBACK_SOUND_KEY = "key_feedback_sound";
    public static final String FEEDBACK_VIBRATION_KEY = "key_feedback_vibration";

    // Duplicate Filter.
    public static final String DUPLICATE_FILTER_CATEGORY_KEY = "key_duplicate_filter_category";
    public static final String DUPLICATE_FILTER_KEY = "key_duplicate_filter";

    // Selection Type.
    public static final String SELECTION_TYPE_CATEGORY_KEY = "key_selection_type_category";
    public static final String SELECTION_TYPES_KEY = "key_selection_types";
    public static final String TAP_SELECTION_SETTINGS_CATEGORY_KEY = "key_tap_selection_settings";
    public static final String FREEZE_BEHAVIOR_KEY = "key_freeze_behavior";
    public static final String TAP_BEHAVIOR_KEY = "key_tap_behavior";
    public static final String AIMER_SELECTION_SETTINGS_CATEGORY_KEY = "key_tap_selection_settings";
    public static final String AIMER_STRATEGY_KEY = "key_selection_strategy";

    // Point of Interest
    public static final String POINT_OF_INTEREST_CATEGORY_KEY = "key_point_of_interest_category";
    public static final String POINT_OF_INTEREST_ENABLED_KEY = "key_point_of_interest_enabled";
    public static final String POINT_OF_INTEREST_COORDINATES_CATEGORY_KEY = "key_point_of_interest_coordinates_category";
    public static final String POINT_OF_INTEREST_X_KEY = "key_point_of_interest_x";
    public static final String POINT_OF_INTEREST_Y_KEY = "key_point_of_interest_y";

    // Views.
    public static final String VIEW_CATEGORY_KEY = "key_view_category";
    public static final String VIEW_SCAN_AREA_KEY = "key_view_scan_area";
    public static final String VIEW_POINT_OF_INTEREST_KEY = "key_view_point_of_interest";
    public static final String VIEW_OVERLAY_KEY = "key_view_overlay";
    public static final String VIEW_VIEWFINDER_KEY = "key_view_viewfinder";

    // Scan Area.
    public static final String SCAN_AREA_MARGINS_CATEGORY_KEY = "key_scan_area_margins_category";
    public static final String SCAN_AREA_MARGIN_TOP_KEY = "key_scan_area_margin_top";
    public static final String SCAN_AREA_MARGIN_RIGHT_KEY = "key_scan_area_margin_right";
    public static final String SCAN_AREA_MARGIN_BOTTOM_KEY = "key_scan_area_margin_bottom";
    public static final String SCAN_AREA_MARGIN_LEFT_KEY = "key_scan_area_margin_left";
    public static final String SCAN_AREA_GUIDES_CATEGORY_KEY = "key_scan_area_guides_category";
    public static final String SCAN_AREA_GUIDES_KEY = "key_scan_area_guides";

    // View Point of Interest.
    public static final String VIEW_POINT_OF_INTEREST_CATEGORY_KEY = "key_view_point_of_interest_category";
    public static final String VIEW_POINT_OF_INTEREST_X_KEY = "key_view_point_of_interest_x";
    public static final String VIEW_POINT_OF_INTEREST_Y_KEY = "key_view_point_of_interest_y";

    // Overlay.
    public static final String OVERLAY_BRUSHES_CATEGORY_KEY = "key_overlay_brushes_category";
    public static final String OVERLAY_BACKGROUND_CATEGORY_KEY = "key_overlay_background_category";
    public static final String FROZEN_OVERLAY_BACKGROUND_COLOR_KEY = "key_frozen_overlay_background_background";
    public static final String OVERLAY_TRACKED_BRUSH_KEY = "key_overlay_tracked_brush";
    public static final String OVERLAY_AIMED_BRUSH_KEY = "key_overlay_aimed_brush";
    public static final String OVERLAY_SELECTING_BRUSH_KEY = "key_overlay_selecting_brush";
    public static final String OVERLAY_SELECTED_BRUSH_KEY = "key_overlay_selected_brush";
    public static final String OVERLAY_HINTS_CATEGORY_KEY = "key_overlay_hints_category";
    public static final String OVERLAY_HINTS_KEY = "key_overlay_hints";
    public static final String OVERLAY_STYLE_CATEGORY_KEY = "key_overlay_style_category";
    public static final String OVERLAY_STYLE_KEY = "key_overlay_style";

    // Viewfinder.
    public static final String VIEWFINDER_CATEGORY_KEY = "key_viewfinder_category";
    public static final String VIEWFINDER_FRAME_COLOR_KEY = "key_viewfinder_frame_color";
    public static final String VIEWFINDER_DOT_COLOR_KEY = "key_viewfinder_dot_color";

    // Camera.
    public static final String TOP_CAMERA_CATEGORY_KEY = "key_top_camera_category";
    public static final String CAMERA_POSITION_KEY = "key_camera_position";
    public static final String TORCH_KEY = "key_torch";
    public static final String CAMERA_SETTINGS_CATEGORY_KEY = "key_camera_settings_category";
    public static final String CAMERA_PREFERRED_RESOLUTION_KEY = "key_camera_preferred_resolution";
    public static final String CAMERA_ZOOM_FACTOR_KEY = "key_camera_zoom_factor";
    public static final String CAMERA_FOCUS_RANGE_KEY = "key_camera_focus_range";
}

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

import com.scandit.datacapture.id.data.IdCaptureDocumentType;
import com.scandit.datacapture.id.data.IdCaptureRegion;
import com.scandit.datacapture.id.data.RegionSpecificSubtype;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.DocumentSelectionType;

/*
 * Class containing programmatically accessible keys.
 */
public final class Keys {

    // IdCapture.
    public final static String FACE_IMAGE = "face_image";
    public final static String ANONYMIZATION_MODE = "anonymization_mode";
    public final static String ID_CAPTURED_FEEDBACK = "id_captured_feedback";
    public final static String ID_REJECTED_FEEDBACK = "id_rejected_feedback";
    public final static String DECODE_BACK_OF_EUROPEAN_DRIVING_LICENSE = "decode_back_of_european_driving_license";
    public final static String REJECT_VOIDED_IDS = "reject_voided_ids";
    public final static String REJECT_EXPIRED_IDS = "reject_expired_ids";
    public final static String REJECT_IDS_EXPIRING_IN_DAYS = "reject_ids_expiring_in_days";
    public final static String REJECT_NOT_REAL_ID_COMPLIANT = "reject_not_real_id_compliant";
    public final static String REJECT_FORGED_AAMVA_BARCODES = "reject_forged_aamva_barcodes";
    public final static String REJECT_INCONSISTENT_DATA = "reject_inconsistent_data";
    public final static String REJECT_HOLDER_BELOW_AGE = "reject_holder_below_age";
    public final static String FULL_SCANNER = "full_scanner";
    public final static String SINGLE_SIDE_SCANNER_BARCODE = "single_side_scanner_barcode";
    public final static String SINGLE_SIDE_SCANNER_MRZ = "single_side_scanner_mrz";
    public final static String SINGLE_SIDE_SCANNER_VIZ = "single_side_scanner_viz";

    public static String getDocumentTypeKey(IdCaptureDocumentType type, DocumentSelectionType documentSelectionType) {
        return type.name() + "-" + documentSelectionType.name();
    }

    public static String getDocumentDetailsKey(IdCaptureDocumentType type, DocumentSelectionType documentSelectionType) {
        return type.name() + "-" + documentSelectionType.name() + "-details";
    }

    public static String getDocumentRegionKey(IdCaptureDocumentType type, IdCaptureRegion region, DocumentSelectionType documentSelectionType) {
        return type.name() + "-" + region.name() + "-" + documentSelectionType.name();
    }

    public static String getSubtypeKey(RegionSpecificSubtype subtype, DocumentSelectionType documentSelectionType) {
        return subtype.name() + "-" + documentSelectionType.name();
    }
    public final static String ID_SUBTYPE_ENABLE_ALL = "id_subtype_enable_all";
    public final static String ID_SUBTYPE_DISABLE_ALL = "id_subtype_disable_all";

    // Camera.
    public final static String CAMERA_POSITION = "camera_position";
    public final static String TORCH_STATE = "torch_state";
    public final static String PREFERRED_RESOLUTION = "preferred_resolution";

    // Logo.
    public final static String LOGO_ANCHOR = "logo_anchor";
    public final static String LOGO_ANCHOR_OFFSET_X_VALUE = "logo_anchor_offset_x_value";
    public final static String LOGO_ANCHOR_OFFSET_Y_VALUE = "logo_anchor_offset_y_value";
    public final static String LOGO_ANCHOR_OFFSET_X_MEASURE_UNIT = "logo_anchor_offset_x_measure_unit";
    public final static String LOGO_ANCHOR_OFFSET_Y_MEASURE_UNIT = "logo_anchor_offset_y_measure_unit";

    // Overlay.
    public final static String OVERLAY_STYLE = "style";
    public final static String OVERLAY_LINE_STYLE = "line_style";
    public final static String CAPTURED_BRUSH = "captured_brush";
    public final static String VIEWFINDER_FRONT_TEXT = "viewfinder_front_text";
    public final static String VIEWFINDER_BACK_TEXT = "viewfinder_back_text";
    public final static String TEXT_HINT_POSITION = "text_hint_position_text";
    public final static String SHOW_TEXT_HINTS = "show_text_hint_text";

    // Gestures.
    public final static String TAP_TO_FOCUS = "tap_to_focus";

    // Controls.
    public final static String TORCH_CONTROL = "torch_control";

    // Result.
    public final static String CONTINUOUS_SCAN = "continuous_scan";
}

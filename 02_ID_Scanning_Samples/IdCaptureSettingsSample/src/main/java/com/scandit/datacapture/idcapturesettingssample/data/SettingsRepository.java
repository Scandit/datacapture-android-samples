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

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import com.scandit.datacapture.core.common.feedback.Feedback;
import com.scandit.datacapture.core.common.feedback.Sound;
import com.scandit.datacapture.core.common.feedback.Vibration;
import com.scandit.datacapture.core.common.geometry.Anchor;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.source.CameraPosition;
import com.scandit.datacapture.core.source.TorchState;
import com.scandit.datacapture.core.source.VideoResolution;
import com.scandit.datacapture.core.ui.gesture.FocusGesture;
import com.scandit.datacapture.core.ui.gesture.TapToFocus;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.id.capture.DriverLicense;
import com.scandit.datacapture.id.capture.HealthInsuranceCard;
import com.scandit.datacapture.id.capture.IdCaptureDocument;
import com.scandit.datacapture.id.capture.IdCaptureFeedback;
import com.scandit.datacapture.id.capture.IdCard;
import com.scandit.datacapture.id.capture.Passport;
import com.scandit.datacapture.id.capture.RegionSpecific;
import com.scandit.datacapture.id.capture.ResidencePermit;
import com.scandit.datacapture.id.capture.VisaIcao;
import com.scandit.datacapture.id.data.IdAnonymizationMode;
import com.scandit.datacapture.id.data.IdCaptureDocumentType;
import com.scandit.datacapture.id.data.IdCaptureRegion;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.RegionSpecificSubtype;
import com.scandit.datacapture.id.ui.IdLayoutLineStyle;
import com.scandit.datacapture.id.ui.IdLayoutStyle;
import com.scandit.datacapture.id.ui.TextHintPosition;
import com.scandit.datacapture.idcapturesettingssample.ui.BrushStyle;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.DocumentSelectionPreferenceUtils;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.DocumentSelectionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The repository to store and retrieve settings. For this sample, the preference values are stored
 * in-memory.
 */
public class SettingsRepository extends PreferenceDataStore {

    @ColorInt
    private static final int RED = Color.parseColor("#FFFF0000");
    @ColorInt
    private static final int GREEN = Color.parseColor("#FF00FF00");

    private final HashMap<String, Object> entries = new HashMap<>();

    public SettingsRepository() {}

    @Override
    public void putString(String key, @Nullable String value) {
        entries.put(key, value);
    }

    @Override
    public void putStringSet(String key, @Nullable Set<String> values) {
        entries.put(key, values);
    }

    @Override
    public void putInt(String key, int value) {
        entries.put(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        entries.put(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        entries.put(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        entries.put(key, value);
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        if (entries.containsKey(key)) {
            return (String) entries.get(key);
        } else {
            return defValue;
        }
    }

    @NonNull
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        if (entries.containsKey(key)) {
            return (Set<String>) entries.get(key);
        } else {
            if (defValues == null) {
                return new HashSet<>();
            } else {
                return defValues;
            }
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        if (entries.containsKey(key)) {
            return (int) entries.get(key);
        } else {
            return defValue;
        }
    }

    public @Nullable Integer getNullableInteger(String key, @Nullable Integer defValue) {
        if (!entries.containsKey(key)) {
            return defValue;
        }

        String stringValue = getString(key, defValue != null ? defValue.toString() : null);

        Integer integerValue;
        if (stringValue != null) {
            try {
                integerValue = Integer.parseInt(stringValue);
            } catch (Exception e) {
                integerValue = null;
            }
        } else {
            integerValue = null;
        }

        return integerValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        if (entries.containsKey(key)) {
            return (long) entries.get(key);
        } else {
            return defValue;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (entries.containsKey(key)) {
            return (float) entries.get(key);
        } else {
            return defValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (entries.containsKey(key)) {
            return (boolean) entries.get(key);
        } else {
            return defValue;
        }
    }

    /*
     * Retrieves from settings the selected accepted documents.
     */
    public List<IdCaptureDocument> getAcceptedDocuments() {
        return getSelectedDocuments(DocumentSelectionType.ACCEPTED);
    }

    /*
     * Retrieves from settings the selected rejected documents.
     */
    public List<IdCaptureDocument> getRejectedDocuments() {
        return getSelectedDocuments(DocumentSelectionType.REJECTED);
    }

    /*
     * Retrieves from settings the selected documents with the specified DocumentSelectionType.
     */
    private List<IdCaptureDocument> getSelectedDocuments(
            DocumentSelectionType documentSelectionType) {
        List<IdCaptureDocument> selectedDocuments = new ArrayList<>();
        List<IdCaptureDocumentType> selectedDocumentTypes =
                DocumentSelectionPreferenceUtils.getSelectedDocumentTypes(this,
                        documentSelectionType);

        for (IdCaptureDocumentType documentType : selectedDocumentTypes) {
            if (documentType == IdCaptureDocumentType.REGION_SPECIFIC) {
                List<RegionSpecificSubtype> selectedSubtypes =
                        DocumentSelectionPreferenceUtils.getSelectedSubtypes(this,
                                documentSelectionType);

                for (RegionSpecificSubtype subtype : selectedSubtypes) {
                    selectedDocuments.add(new RegionSpecific(subtype));
                }
            } else {
                List<IdCaptureRegion> selectedRegions =
                        DocumentSelectionPreferenceUtils.getSelectedRegions(this,
                                documentType, documentSelectionType);

                for (IdCaptureRegion region : selectedRegions) {
                    IdCaptureDocument document = createDocument(documentType, region);

                    if (document != null) {
                        selectedDocuments.add(document);
                    }
                }
            }
        }

        return selectedDocuments;
    }

    // Creates an IdCaptureDocument from a document type and a region.
    private IdCaptureDocument createDocument(IdCaptureDocumentType documentType, IdCaptureRegion region) {
        switch (documentType) {
            case ID_CARD:
                return new IdCard(region);
            case DRIVER_LICENSE:
                return new DriverLicense(region);
            case PASSPORT:
                return new Passport(region);
            case VISA_ICAO:
                return new VisaIcao(region);
            case RESIDENCE_PERMIT:
                return new ResidencePermit(region);
            case HEALTH_INSURANCE_CARD:
                return new HealthInsuranceCard(region);
            default:
                return null;
        }
    }

    /*
     * Retrieves from settings the image types that should be passed to the IdCapture result.
     */
    public Set<IdImageType> getImageTypes() {
        return getStringSet(Keys.IMAGE_TYPES, null).stream()
                .map(IdImageType::valueOf)
                .collect(Collectors.toSet());
    }

    // Retrieves from settings whether the full scanner is enabled.
    public boolean isFullScannerEnabled() {
        return getBoolean(Keys.FULL_SCANNER, Defaults.isFullScannerEnabled());
    }

    // Retrieves from settings whether barcode scanning is enabled in single side scanner.
    public boolean isSingleSideScannerBarcodeEnabled() {
        return getBoolean(Keys.SINGLE_SIDE_SCANNER_BARCODE,
                Defaults.isSingleSideScannerBarcodeEnabled());
    }

    // Retrieves from settings whether Machine Readable Zone scanning is enabled in single side
    // scanner.
    public boolean isSingleSideScannerMrzEnabled() {
        return getBoolean(Keys.SINGLE_SIDE_SCANNER_MRZ, Defaults.isSingleSideScannerMrzEnabled());
    }

    // Retrieves from settings whether Visual Inspection Zone scanning is enabled in single side
    // scanner.
    public boolean isSingleSideScannerVizEnabled() {
        return getBoolean(Keys.SINGLE_SIDE_SCANNER_VIZ, Defaults.isSingleSideScannerVizEnabled());
    }

    /*
     * Retrieves from settings whether sensitive data should be removed from images, result fields
     * or both before passing to the IdCapture result.
     */
    public IdAnonymizationMode getAnonymizationMode() {
        String anonymizationMode = getString(Keys.ANONYMIZATION_MODE, null);
        if (anonymizationMode == null) {
            return Defaults.getDefaultAnonymizationMode();
        } else {
            return IdAnonymizationMode.valueOf(anonymizationMode);
        }
    }

    /*
     * Retrieves from settings whether extra information should be extracted from the back side
     * of European driver's licenses.
     */
    public boolean shouldDecodeBackOfEuropeanDrivingLicense() {
        return getBoolean(Keys.DECODE_BACK_OF_EUROPEAN_DRIVING_LICENSE,
                Defaults.shouldDecodeBackOfEuropeanDrivingLicense());
    }

    /*
     * Retrieves from settings whether mobile driver licenses should be decoded.
     */
    public boolean shouldDecodeMobileDrivingLicenses() {
        return getBoolean(Keys.DECODE_MOBILE_DRIVING_LICENSES,
                Defaults.shouldDecodeMobileDrivingLicenses());
    }

    /*
     * Retrieves from settings whether voided IDs should be rejected. This feature has been
     * primarily developed for US Driver’s Licenses, the results might not be accurate when
     * scanning other document types.
     */
    public boolean shouldRejectVoidedIds() {
        return getBoolean(Keys.REJECT_VOIDED_IDS, Defaults.shouldRejectVoidedIds());
    }

    /*
     * Retrieves from settings whether expired IDs should be rejected.
     */
    public boolean shouldRejectExpiredIds() {
        return getBoolean(Keys.REJECT_EXPIRED_IDS, Defaults.shouldRejectExpiredIds());
    }

    /*
     * Retrieves from settings whether IDs expiring within the specified number of days
     * should be rejected.
     */
    public @Nullable Integer shouldRejectIdsExpiringInDays() {
        return getNullableInteger(Keys.REJECT_IDS_EXPIRING_IN_DAYS, Defaults.shouldRejectIdsExpiringInDays());
    }

    /*
     * Retrieves from settings whether not Real ID compliant should be rejected.
     */
    public boolean shouldRejectNotRealIdCompliant() {
        return getBoolean(Keys.REJECT_NOT_REAL_ID_COMPLIANT, Defaults.shouldRejectNotRealIdCompliant());
    }

    /*
     * Retrieves from settings whether forged AAMVA barcodes should be rejected.
     */
    public boolean shouldRejectForgedAamvaBarcodes() {
        return getBoolean(Keys.REJECT_FORGED_AAMVA_BARCODES, Defaults.shouldRejectForgedAamvaBarcodes());
    }

    /*
     * Retrieves from settings whether IDs with inconsistent data should be rejected.
     */
    public boolean shouldRejectInconsistentData() {
        return getBoolean(Keys.REJECT_INCONSISTENT_DATA, Defaults.shouldRejectInconsistentData());
    }

    /*
     * Retrieves from settings whether IDs whose holder is below the specified age
     * should be rejected.
     */
    public @Nullable Integer shouldRejectHolderBelowAge() {
        return getNullableInteger(Keys.REJECT_HOLDER_BELOW_AGE, Defaults.shouldRejectHolderBelowAge());
    }

    private Feedback buildFeedbackFromChoice(@Nullable String selected, Vibration vibration,
                                             Sound sound) {
        if (selected == null)
            return new Feedback(null, null);
        FeedbackType selectedFeedbackType = FeedbackType.valueOf(selected);
        switch (selectedFeedbackType) {
            case NONE:
                return new Feedback(null, null);
            case VIBRATION:
                return new Feedback(vibration, null);
            case SOUND:
                return new Feedback(null, sound);
            case SOUND_AND_VIBRATION:
                return new Feedback(vibration, sound);
            default:
                throw new IllegalArgumentException("Unknown feedback type " + selectedFeedbackType);
        }
    }

    public IdCaptureFeedback buildFeedbackFromSettings() {
        final IdCaptureFeedback defaultFeedback = IdCaptureFeedback.defaultFeedback();
        IdCaptureFeedback result = new IdCaptureFeedback();

        Feedback idCapturedFeedback = buildFeedbackFromChoice(
                getString(Keys.ID_CAPTURED_FEEDBACK, Defaults.getDefaultCapturedFeedback().name()),
                defaultFeedback.getIdCaptured().getVibration(),
                defaultFeedback.getIdCaptured().getSound()
        );
        result.setIdCaptured(idCapturedFeedback);

        Feedback idRejectedFeedback = buildFeedbackFromChoice(
                getString(Keys.ID_REJECTED_FEEDBACK, Defaults.getDefaultRejectedFeedback().name()),
                Vibration.defaultVibration(),
                IdCaptureFeedback.defaultFailureSound()
        );
        result.setIdRejected(idRejectedFeedback);

        return result;
    }

    /*
     * Retrieves from settings the Camera preferred resolution.
     */
    public VideoResolution getPreferredResolution() {
        String preferredResolution = getString(Keys.PREFERRED_RESOLUTION, null);
        if (preferredResolution == null) {
            return Defaults.getDefaultResolution();
        } else {
            return VideoResolution.valueOf(preferredResolution);
        }
    }

    /*
     * Retrieves from settings the Camera preferred resolution.
     */
    public CameraPosition getCameraPosition() {
        String cameraPosition = getString(Keys.CAMERA_POSITION, null);
        if (cameraPosition == null) {
            return Defaults.getDefaultCameraPosition();
        } else {
            return CameraPosition.valueOf(cameraPosition);
        }
    }

    /*
     * Retrieves from settings the desired torch state.
     */
    public TorchState getTorchState() {
        boolean torchEnabled = getBoolean(Keys.TORCH_STATE, Defaults.getTorchEnabledDefault());
        if (torchEnabled) {
            return TorchState.ON;
        } else {
            return TorchState.OFF;
        }
    }

    /*
     * Sets the desired torch state in the settings.
     */
    public void setTorchState(boolean torchEnabled) {
        putBoolean(Keys.TORCH_STATE, torchEnabled);
    }

    /*
     * Retrieves from settings the logo anchor.
     */
    public Anchor getLogoAnchor() {
        String logoAnchor = getString(Keys.LOGO_ANCHOR, null);
        if (logoAnchor == null) {
            return Defaults.getDefaultLogoAnchor();
        } else {
            return Anchor.valueOf(logoAnchor);
        }
    }

    /*
     * Retrieves from settings the logo offset X.
     */
    public FloatWithUnit getLogoAnchorOffsetX() {
        FloatWithUnit defaultValue = Defaults.getDefaultLogoAnchorX();
        String valueString = getString(
                Keys.LOGO_ANCHOR_OFFSET_X_VALUE, String.valueOf(defaultValue.getValue())
        );
        String measureUnitString = getString(
                Keys.LOGO_ANCHOR_OFFSET_X_MEASURE_UNIT, defaultValue.getUnit().name()
        );

        try {
            float value = Float.parseFloat(valueString);
            MeasureUnit measureUnit = MeasureUnit.valueOf(measureUnitString);
            return new FloatWithUnit(value, measureUnit);
        } catch (NullPointerException | IllegalArgumentException e) {
            return defaultValue;
        }
    }

    /*
     * Retrieves from settings the logo offset Y.
     */
    public FloatWithUnit getLogoAnchorOffsetY() {
        FloatWithUnit defaultValue = Defaults.getDefaultLogoAnchorY();
        String valueString = getString(
                Keys.LOGO_ANCHOR_OFFSET_Y_VALUE, String.valueOf(defaultValue.getValue())
        );
        String measureUnitString = getString(
                Keys.LOGO_ANCHOR_OFFSET_Y_MEASURE_UNIT, defaultValue.getUnit().name()
        );

        try {
            float value = Float.parseFloat(valueString);
            MeasureUnit measureUnit = MeasureUnit.valueOf(measureUnitString);
            return new FloatWithUnit(value, measureUnit);
        } catch (NullPointerException | IllegalArgumentException e) {
            return defaultValue;
        }
    }

    /*
     * Retrieves from settings the overlay style.
     */
    public IdLayoutStyle getOverlayStyle() {
        String style = getString(Keys.OVERLAY_STYLE, null);
        if (style == null) {
            return Defaults.getDefaultOverlayStyle();
        } else {
            return IdLayoutStyle.valueOf(style);
        }
    }

    public TextHintPosition getTextHintPosition() {
        String p = getString(Keys.TEXT_HINT_POSITION, null);
        if (p == null) {
            return Defaults.getDefaultTextHintPosition();
        } else {
            return TextHintPosition.valueOf(p);
        }
    }

    public boolean getShowTextHints() {
        return getBoolean(Keys.SHOW_TEXT_HINTS, Defaults.getDefaultShowTextHints());
    }

    /*
     * Retrieves from settings the overlay line style.
     */
    public IdLayoutLineStyle getOverlayLineStyle() {
        String style = getString(Keys.OVERLAY_LINE_STYLE, null);
        if (style == null) {
            return Defaults.getDefaultOverlayLineStyle();
        } else {
            return IdLayoutLineStyle.valueOf(style);
        }
    }

    /*
     * Retrieves from settings the overlay brush.
     */
    public Brush getCapturedBrush() {
        String style = getString(Keys.CAPTURED_BRUSH, null);
        BrushStyle brushStyle;
        if (style == null) {
            brushStyle = Defaults.getDefaultCapturedBrushStyle();
        } else {
            brushStyle = BrushStyle.valueOf(style);
        }
        Brush defaultBrush = Defaults.getDefaultBrush();
        switch (brushStyle) {
            case RED:
                return defaultBrush.copy(RED, RED, true);
            case GREEN:
                return defaultBrush.copy(GREEN, GREEN, true);
            case DEFAULT:
            default:
                return defaultBrush;
        }
    }

    public String getViewfinderFrontText() {
        return getString(Keys.VIEWFINDER_FRONT_TEXT, "");
    }

    public String getViewfinderBackText() {
        return getString(Keys.VIEWFINDER_BACK_TEXT, "");
    }

    /*
     * Retrieves from settings the focus gesture.
     */
    public FocusGesture getFocusGesture() {
        boolean tapToFocusEnabled =
                getBoolean(Keys.TAP_TO_FOCUS, Defaults.getDefaultTapToFocusEnabled());
        if (tapToFocusEnabled) {
            return new TapToFocus();
        } else {
            return null;
        }
    }

    /*
     * Retrieves from the settings whether the torch control should be shown.
     */
    public boolean getShouldShowTorchControl() {
        return getBoolean(Keys.TORCH_CONTROL, Defaults.getDefaultTorchControlEnabled());
    }

    /*
     * Retrieves from the settings whether continuous scan is enabled.
     */
    public boolean isContinuousScanEnabled() {
        return getBoolean(Keys.CONTINUOUS_SCAN, Defaults.getDefaultContinuousScanEnabled());
    }
}

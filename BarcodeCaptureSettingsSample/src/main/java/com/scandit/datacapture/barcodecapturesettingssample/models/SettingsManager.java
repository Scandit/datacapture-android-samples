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

package com.scandit.datacapture.barcodecapturesettingssample.models;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.capture.SymbologySettings;
import com.scandit.datacapture.barcode.data.Checksum;
import com.scandit.datacapture.barcode.data.CompositeType;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlayStyle;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.overlay.BrushStyle;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.overlay.BrushStyleEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.LaserlineDisabledColor;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.LaserlineEnabledColor;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.RectangularDisabledColor;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.RectangularEnabledColor;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.ViewfinderTypeAimer;
import com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification;
import com.scandit.datacapture.core.area.LocationSelection;
import com.scandit.datacapture.core.area.RadiusLocationSelection;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.feedback.Feedback;
import com.scandit.datacapture.core.common.feedback.Sound;
import com.scandit.datacapture.core.common.feedback.Vibration;
import com.scandit.datacapture.core.common.geometry.Anchor;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MarginsWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraPosition;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FocusGestureStrategy;
import com.scandit.datacapture.core.source.TorchState;
import com.scandit.datacapture.core.source.VideoResolution;
import com.scandit.datacapture.core.time.TimeInterval;
import com.scandit.datacapture.core.ui.LogoStyle;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinderStyle;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderLineStyle;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle;
import com.scandit.datacapture.core.ui.viewfinder.Viewfinder;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SettingsManager {
	// There is a Scandit sample license key set below here.
	// This license key is enabled for sample evaluation only.
	// If you want to build your own application, get your license key by signing up for a trial at https://ssl.scandit.com/dashboard/sign-up?p=test
    private static final String SCANDIT_LICENSE_KEY = "AZ707AsCLmJWHbYO4RjqcVAEgAxmNGYcF3Ytg4RiKa/lWTQ3IXkfVZhSSi0yOzuabn9STRdnzTLybIiJVkVZU2QK5jeqbn1HGCGXQ+9lqsN8VUaTw1IeuHJo+9mYVdv3I1DhedtSy89aKA4QugKI5d9ykKaXGohXjlI+PB5ju8Tyc80FPAC3WP9D8oKBcWyemTLQjoUu0Nl3T7mVyFIXMPshQeYddkjMQ1sVV9Jcuf1CbI9riUJWzbNUb4NcB4MoV0BHuyALUPtuM2+cBkX3bPN0AxjD9WC7KflL2UrsZeenvl/aDx2yU4t5vsa2BImNTyEqdVs+rmrGUzRdbYvSUFzKBeiBncLAASqnexTuSzh9KfEm/cKrVlWekP+zOkrilICkn3KVNY6g9RQd8FrsHTBI9OBbMpC79BTwuzHcnlFUG5S3ru/viJ2+f9JEEejxDbdJ7u4JohfBuUYBSEBQ/XzEPMdpqWcmxHGWF4j7jQy83B9Wlgrhd8xNWKjgAViI0bcebjnB7o6yuKacXICH/lo787RhnXSjqjQhJBCbEvwxHQZiEfWPdVKtY7EM+x8HFr6j3orKllKOMJ9asZ5bJYz9aIHlOWeRGm90guQn0KWiPwuKbUOQIMxFAOem2zcSTt4OfqS6Ci0Y6lk7FIrgpbaz8L1PW64kkjrZB6FtQ8OppmsyZ/QTvrHYFQFTH7MpamDviRjEKMyiD2ID6ypl+Meeme6cZYRJVujr6b4tweQCsfNEYhuDiMJaWQ57R0n2XdF0zkepLVc0yA2Q3wWhxSIASLnv6GTCYYVnDJnkrr6VaTv8RVUOp8h8U34wGDanamQ+39+rESMD59E288OKgFvZZWN9Ltu/VQCcjYCYT1RTDcA9co3Y18aGpDxvtLVEGJ8QDPv1E//IYAYEhXqu8r9xbsx/hTwZmLpNKyXGPRr9+hpufTAcAj908f2kuQ==";

    @ColorInt private static final int RED = Color.parseColor("#FFFF0000");
    @ColorInt private static final int GREEN = Color.parseColor("#FF00FF00");

    private static final SettingsManager CURRENT = new SettingsManager();

    public static SettingsManager getCurrentSettings() {
        return CURRENT;
    }

    private DataCaptureContext dataCaptureContext;
    private BarcodeCaptureSettings barcodeCaptureSettings;
    private BarcodeCapture barcodeCapture;

    private TorchState torchState = TorchState.OFF;
    private CameraPosition cameraPosition = CameraPosition.WORLD_FACING;
    private Camera camera = Camera.getCamera(cameraPosition);
    private CameraSettings cameraSettings = BarcodeCapture.createRecommendedCameraSettings();

    private BarcodeCaptureOverlay barcodeCaptureOverlay;
    private MarginsWithUnit scanAreaMargins = new MarginsWithUnit(
            new FloatWithUnit(0, MeasureUnit.DIP),
            new FloatWithUnit(0, MeasureUnit.DIP),
            new FloatWithUnit(0, MeasureUnit.DIP),
            new FloatWithUnit(0, MeasureUnit.DIP)
    );

    private PointWithUnit pointOfInterest = new PointWithUnit(
            new FloatWithUnit(0.5f, MeasureUnit.FRACTION),
            new FloatWithUnit(0.5f, MeasureUnit.FRACTION)
    );

    private SizeSpecification locationSelectionRectangularSizeSpecification =
            SizeSpecification.WIDTH_AND_HEIGHT;
    private FloatWithUnit locationSelectionRectangularWidth =
            new FloatWithUnit(0f, MeasureUnit.DIP);
    private FloatWithUnit locationSelectionRectangularHeight =
            new FloatWithUnit(0f, MeasureUnit.DIP);
    private float locationSelectionRectangularWidthAspect = 0f;
    private float locationSelectionRectangularHeightAspect = 0f;

    private final Map<BarcodeCaptureOverlayStyle, BrushStyleEntry[]> brushes = new HashMap<>();

    private RectangularEnabledColor rectangularColor = RectangularEnabledColor.DEFAULT;
    private RectangularDisabledColor rectangularDisabledColor = RectangularDisabledColor.DEFAULT;
    private SizeSpecification rectangularViewfinderSizeSpecification =
            SizeSpecification.WIDTH_AND_HEIGHT;
    private FloatWithUnit rectangularViewfinderWidth;
    private FloatWithUnit rectangularViewfinderHeight;
    private FloatWithUnit rectangularViewfinderShorterDimension;
    private float rectangularViewfinderWidthAspect = 0f;
    private float rectangularViewfinderHeightAspect = 0f;
    private float rectangularViewfinderLongerDimensionAspect = 0f;
    private RectangularViewfinderStyle rectangularViewfinderStyle =
            RectangularViewfinderStyle.LEGACY;
    private RectangularViewfinderLineStyle rectangularViewfinderLineStyle =
            RectangularViewfinderLineStyle.LIGHT;
    private float rectangularViewfinderDimming = 0.0f;
    private boolean rectangularViewfinderAnimation = false;
    private boolean rectangularViewfinderLooping = false;

    private FloatWithUnit laserlineViewfinderWidth = new FloatWithUnit(0.75f, MeasureUnit.FRACTION);
    private LaserlineEnabledColor laserlineViewfinderEnabledColor = LaserlineEnabledColor.DEFAULT;
    private LaserlineDisabledColor laserlineViewfinderDisabledColor = LaserlineDisabledColor.DEFAULT;
    private LaserlineViewfinderStyle laserlineViewfinderStyle = LaserlineViewfinderStyle.LEGACY;

    private ViewfinderTypeAimer.FrameColor aimerViewfinderFrameColor =
            ViewfinderTypeAimer.FrameColor.DEFAULT;
    private ViewfinderTypeAimer.DotColor aimerViewfinderDotColor =
            ViewfinderTypeAimer.DotColor.DEFAULT;

    private LogoStyle logoStyle = LogoStyle.EXTENDED;
    private Anchor logoAnchor = Anchor.BOTTOM_RIGHT;
    private FloatWithUnit anchorXOffset = new FloatWithUnit(0f, MeasureUnit.FRACTION);
    private FloatWithUnit anchorYOffset = new FloatWithUnit(0f, MeasureUnit.FRACTION);

    private boolean zoomButtonEnabled = false;
    private boolean torchButtonEnabled = false;
    private boolean cameraButtonEnabled = false;

    private boolean tapToFocusEnabled = true;
    private boolean swipeToZoomEnabled = true;

    private boolean continuousScanningEnabled = false;

    private SettingsManager() {
        // The barcode capturing process is configured through barcode capture settings
        // which are then applied to the barcode capture instance that manages barcode recognition.
        barcodeCaptureSettings = new BarcodeCaptureSettings();
        camera.applySettings(cameraSettings);

        // Create data capture context using your license key and set the camera as the frame source.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);
        dataCaptureContext.setFrameSource(camera);

        // Create new barcode capture mode with the settings from above.
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, barcodeCaptureSettings);
        barcodeCapture.setEnabled(true);

        // Create a new overlay with the barcode capture from above, and retrieve the default brush.
        barcodeCaptureOverlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, null, BarcodeCaptureOverlayStyle.FRAME);

        initBrushStyles();

        // Create a temporary RectangularViewfinder instance to get default values for width and height.
        RectangularViewfinder tempRectangularViewfinder = new RectangularViewfinder(RectangularViewfinderStyle.LEGACY);
        rectangularViewfinderWidth = tempRectangularViewfinder.getSizeWithUnitAndAspect().getWidthAndHeight().getWidth();
        rectangularViewfinderHeight = tempRectangularViewfinder.getSizeWithUnitAndAspect().getWidthAndHeight().getHeight();
        rectangularViewfinderShorterDimension = new FloatWithUnit(1.0f, MeasureUnit.FRACTION);
    }

    /*
     * For each BarcodeCaptureOverlayStyle, create a list of brushes with the default one plus
     * for each BrushStyle a clone of it with different tint.
     */
    private void initBrushStyles() {
        BarcodeCaptureOverlayStyle[] overlayStyles = BarcodeCaptureOverlayStyle.values();
        for (int i = 0; i< overlayStyles.length; i++) {// Loop OverlayStyles.
            BarcodeCaptureOverlayStyle style = BarcodeCaptureOverlayStyle.values()[i];
            Brush defaultBrush = BarcodeCaptureOverlay.newInstance(barcodeCapture, null, style).getBrush();

            BrushStyle[] brushStyles = BrushStyle.values();
            BrushStyleEntry[] styleBrushes = new BrushStyleEntry[brushStyles.length];
            for (int j = 0; j < brushStyles.length; j++) {// Loop BrushStyle for each OverlayStyle.
                BrushStyle brushStyle = brushStyles[j];
                Brush styledBrush = defaultBrush;
                switch (brushStyle) {
                    case DEFAULT:
                        break;
                    case RED:
                        styledBrush = defaultBrush.copy(RED, RED, true);
                        break;
                    case GREEN:
                        styledBrush = defaultBrush.copy(GREEN, GREEN, true);
                        break;
                }
                styleBrushes[j] = new BrushStyleEntry(styledBrush, brushStyle);
            }
            brushes.put(style, styleBrushes);
        }
    }

    public BarcodeCapture getBarcodeCapture() {
        return barcodeCapture;
    }

    public DataCaptureContext getDataCaptureContext() {
        return dataCaptureContext;
    }

    public Camera getCamera() {
        return camera;
    }

    public CameraSettings getCameraSettings() {
        return cameraSettings;
    }

    public BarcodeCaptureOverlay getBarcodeCaptureOverlay() {
        return barcodeCaptureOverlay;
    }

    public MarginsWithUnit getScanAreaMargins() {
        return scanAreaMargins;
    }

    private void updateAndSetBarcodeCaptureSettings() {
        barcodeCapture.applySettings(barcodeCaptureSettings, null);
    }

    private void updateAndSetCameraSettings() {
        camera.applySettings(cameraSettings, null);
    }

    //region Symbology Settings.
    public SymbologySettings getSymbologySettings(Symbology symbology) {
        return barcodeCaptureSettings.getSymbologySettings(symbology);
    }

    public boolean isSymbologyEnabled(String symbologyIdentifier) {
        Symbology symbology = SymbologyDescription.forIdentifier(symbologyIdentifier).getSymbology();
        return isSymbologyEnabled(symbology);
    }

    public boolean isSymbologyEnabled(Symbology symbology) {
        return getSymbologySettings(symbology).isEnabled();
    }

    public void enableAllSymbologies(boolean enabled) {
        for (Symbology symbology : Symbology.values()) {
            enableSymbology(symbology, enabled, false);
        }
        updateAndSetBarcodeCaptureSettings();
    }

    public void enableSymbology(
            Symbology symbology,
            boolean enabled,
            boolean updateBarcodeCaptureSettings
    ) {
        barcodeCaptureSettings.enableSymbology(symbology, enabled);
        if (updateBarcodeCaptureSettings) {
            updateAndSetBarcodeCaptureSettings();
        }
    }

    public boolean isAnySymbologyEnabled() {
        return !barcodeCaptureSettings.getEnabledSymbologies().isEmpty();
    }

    public boolean isColorInverted(Symbology symbology) {
        return getSymbologySettings(symbology).isColorInvertedEnabled();
    }

    public void setColorInverted(Symbology symbology, boolean colorInvertible) {
        getSymbologySettings(symbology).setColorInvertedEnabled(colorInvertible);
        updateAndSetBarcodeCaptureSettings();
    }

    public boolean isExtensionEnabled(Symbology symbology, String extension) {
        return getSymbologySettings(symbology).isExtensionEnabled(extension);
    }

    public void setExtensionEnabled(Symbology symbology, String extension, boolean enabled) {
        getSymbologySettings(symbology).setExtensionEnabled(extension, enabled);
        updateAndSetBarcodeCaptureSettings();
    }

    public boolean isChecksumEnabled(Symbology symbology, Checksum checksum) {
        return getSymbologySettings(symbology).getChecksums().contains(checksum);
    }

    public void setChecksumEnabled(Symbology symbology, Checksum checksum, boolean enabled) {
        EnumSet<Checksum> enabledChecksums = getSymbologySettings(symbology).getChecksums();
        if (enabled) {
            enabledChecksums.add(checksum);
        } else {
            enabledChecksums.remove(checksum);
        }
        getSymbologySettings(symbology).setChecksums(enabledChecksums);
        updateAndSetBarcodeCaptureSettings();
    }

    public short getMinSymbolCount(Symbology symbology) {
        return Collections.min(getSymbologySettings(symbology).getActiveSymbolCounts());
    }

    public void setMinSymbolCount(Symbology symbology, short minSymbolCount) {
        SymbologySettings symbologySettings = getSymbologySettings(symbology);
        short maxSymbolCount = Collections.max(symbologySettings.getActiveSymbolCounts());
        setSymbolCount(symbologySettings, minSymbolCount, maxSymbolCount);
    }

    public short getMaxSymbolCount(Symbology symbology) {
        return Collections.max(getSymbologySettings(symbology).getActiveSymbolCounts());
    }

    public void setMaxSymbolCount(Symbology symbology, short maxSymbolCount) {
        SymbologySettings symbologySettings = getSymbologySettings(symbology);
        short minSymbologyCount = Collections.min(symbologySettings.getActiveSymbolCounts());
        setSymbolCount(symbologySettings, minSymbologyCount, maxSymbolCount);
    }

    private void setSymbolCount(
            SymbologySettings symbologySettings, short minSymbolCount, short maxSymbolCount
    ) {
        HashSet<Short> symbolCount = new HashSet<>();
        if (minSymbolCount >= maxSymbolCount) {
            symbolCount.add(maxSymbolCount);
        } else {
            for (short i = minSymbolCount; i <= maxSymbolCount; i++) {
                symbolCount.add(i);
            }
        }
        symbologySettings.setActiveSymbolCounts(symbolCount);
        updateAndSetBarcodeCaptureSettings();
    }
    //endregion

    //region Feedback settings.
    public void setSoundEnabled(boolean enabled) {
        barcodeCapture.getFeedback().setSuccess(
                new Feedback(getCurrentVibration(), enabled ? Sound.defaultSound() : null)
        );
    }

    public boolean isSoundEnabled() {
        return getCurrentSound() != null;
    }

    @Nullable
    private Sound getCurrentSound() {
        return barcodeCapture.getFeedback().getSuccess().getSound();
    }

    public void setVibrationEnabled(boolean enabled) {
        barcodeCapture.getFeedback().setSuccess(
                new Feedback(enabled ? Vibration.defaultVibration() : null, getCurrentSound())
        );
    }

    public boolean isVibrationEnabled() {
        return getCurrentVibration() != null;
    }

    @Nullable
    private Vibration getCurrentVibration() {
        return barcodeCapture.getFeedback().getSuccess().getVibration();
    }
    //endregion

    //region Code Duplicate Filter settings.
    public void setCodeDuplicateFilter(long value) {
        barcodeCaptureSettings.setCodeDuplicateFilter(TimeInterval.millis(value));
        updateAndSetBarcodeCaptureSettings();
    }

    public long getCodeDuplicateFilter() {
        return barcodeCaptureSettings.getCodeDuplicateFilter().asMillis();
    }
    //endregion

    //region Composite Type settings.
    public void setEnabledCompositeTypes(EnumSet<CompositeType> types) {
        barcodeCaptureSettings.setEnabledCompositeTypes(types);
        barcodeCaptureSettings.enableSymbologies(types);
        updateAndSetBarcodeCaptureSettings();
    }

    public EnumSet<CompositeType> getEnabledCompositeTypes() {
        return barcodeCaptureSettings.getEnabledCompositeTypes();
    }
    //endregion

    //region Location settings.
    public LocationSelection getLocationSelection() {
        return barcodeCaptureSettings.getLocationSelection();
    }

    public void setLocationSelection(LocationSelection locationSelection) {
        barcodeCaptureSettings.setLocationSelection(locationSelection);
        updateAndSetBarcodeCaptureSettings();
    }

    public void setLocationSelectionRadiusValue(float value) {
        LocationSelection locationSelection = getLocationSelection();
        if (locationSelection instanceof RadiusLocationSelection) {
            FloatWithUnit currentRadius = ((RadiusLocationSelection) locationSelection).getRadius();
            FloatWithUnit newRadius = new FloatWithUnit(value, currentRadius.getUnit());
            setLocationSelection(new RadiusLocationSelection(newRadius));
        }
    }

    public void setLocationSelectionRadiusMeasureUnit(MeasureUnit measureUnit) {
        LocationSelection locationSelection = getLocationSelection();
        if (locationSelection instanceof RadiusLocationSelection) {
            FloatWithUnit currentRadius = ((RadiusLocationSelection) locationSelection).getRadius();
            FloatWithUnit newRadius = new FloatWithUnit(currentRadius.getValue(), measureUnit);
            setLocationSelection(new RadiusLocationSelection(newRadius));
        }
    }

    public SizeSpecification getLocationSelectionRectangularSizeSpecification() {
        return locationSelectionRectangularSizeSpecification;
    }

    public void setLocationSelectionRectangularSizeSpecification(
            SizeSpecification locationSelectionRectangularSizeSpecification
    ) {
        this.locationSelectionRectangularSizeSpecification =
                locationSelectionRectangularSizeSpecification;
    }

    public FloatWithUnit getLocationSelectionRectangularWidth() {
        return locationSelectionRectangularWidth;
    }

    public void setLocationSelectionRectangularWidth(
            FloatWithUnit locationSelectionRectangularWidth
    ) {
        this.locationSelectionRectangularWidth = locationSelectionRectangularWidth;
    }

    public FloatWithUnit getLocationSelectionRectangularHeight() {
        return locationSelectionRectangularHeight;
    }

    public void setLocationSelectionRectangularHeight(
            FloatWithUnit locationSelectionRectangularHeight
    ) {
        this.locationSelectionRectangularHeight = locationSelectionRectangularHeight;
    }

    public float getLocationSelectionRectangularWidthAspect() {
        return locationSelectionRectangularWidthAspect;
    }

    public void setLocationSelectionRectangularWidthAspect(
            float locationSelectionRectangularWidthAspect
    ) {
        this.locationSelectionRectangularWidthAspect = locationSelectionRectangularWidthAspect;
    }

    public float getLocationSelectionRectangularHeightAspect() {
        return locationSelectionRectangularHeightAspect;
    }

    public void setLocationSelectionRectangularHeightAspect(
            float locationSelectionRectangularHeightAspect
    ) {
        this.locationSelectionRectangularHeightAspect = locationSelectionRectangularHeightAspect;
    }
    //endregion

    //region Camera Settings.
    public CameraPosition getCurrentCameraPosition() {
        return cameraPosition;
    }

    public void setCameraPosition(CameraPosition cameraPosition) {
        if (this.cameraPosition != cameraPosition) {
            this.cameraPosition = cameraPosition;
            Camera camera = Camera.getCamera(cameraPosition);
            if (camera != null) {
                camera.applySettings(cameraSettings, null);
                dataCaptureContext.setFrameSource(camera, null);
                this.camera = camera;
            }
        }
    }

    public TorchState getTorchState() {
        return torchState;
    }

    public void setTorchState(TorchState torchState) {
        this.torchState = torchState;
        this.camera.setDesiredTorchState(torchState);
    }

    public VideoResolution getVideoResolution() {
        return cameraSettings.getPreferredResolution();
    }

    public void setVideoResolution(VideoResolution videoResolution) {
        cameraSettings.setPreferredResolution(videoResolution);
        updateAndSetCameraSettings();
    }

    public float getZoomFactor() {
        return cameraSettings.getZoomFactor();
    }

    public void setZoomFactor(float zoomFactor) {
        cameraSettings.setZoomFactor(zoomFactor);
        updateAndSetCameraSettings();
    }

    public float getZoomGestureZoomFactor() {
        return cameraSettings.getZoomGestureZoomFactor();
    }

    public void setZoomGestureZoomFactor(float zoomFactor) {
        cameraSettings.setZoomGestureZoomFactor(zoomFactor);
        updateAndSetCameraSettings();
    }

    public FocusGestureStrategy getFocusGestureStrategy() {
        return cameraSettings.getFocusGestureStrategy();
    }

    public void setFocusGestureStrategy(FocusGestureStrategy strategy) {
        cameraSettings.setFocusGestureStrategy(strategy);
        updateAndSetCameraSettings();
    }
    //endregion

    //region View Settings.
    //region Scan Area Settings.
    public void setShouldShowScanAreaGuides(boolean showGuides) {
        barcodeCaptureOverlay.setShouldShowScanAreaGuides(showGuides);
    }

    public boolean getShouldShowScanAreaGuides() {
        return barcodeCaptureOverlay.getShouldShowScanAreaGuides();
    }

    public FloatWithUnit getScanAreaTopMargin() {
        return scanAreaMargins.getTop();
    }

    public FloatWithUnit getScanAreaRightMargin() {
        return scanAreaMargins.getRight();
    }

    public FloatWithUnit getScanAreaBottomMargin() {
        return scanAreaMargins.getBottom();
    }

    public FloatWithUnit getScanAreaLeftMargin() {
        return scanAreaMargins.getLeft();
    }

    public void setScanAreaTopMargin(FloatWithUnit topMargin) {
        scanAreaMargins = new MarginsWithUnit(
                getScanAreaLeftMargin(),
                topMargin,
                getScanAreaRightMargin(),
                getScanAreaBottomMargin()
        );
    }

    public void setScanAreaRightMargin(FloatWithUnit rightMargin) {
        scanAreaMargins = new MarginsWithUnit(
                getScanAreaLeftMargin(),
                getScanAreaTopMargin(),
                rightMargin,
                getScanAreaBottomMargin()
        );
    }

    public void setScanAreaBottomMargin(FloatWithUnit bottomMargin) {
        scanAreaMargins = new MarginsWithUnit(
                getScanAreaLeftMargin(),
                getScanAreaTopMargin(),
                getScanAreaRightMargin(),
                bottomMargin
        );
    }

    public void setScanAreaLeftMargin(FloatWithUnit leftMargin) {
        scanAreaMargins = new MarginsWithUnit(
                leftMargin,
                getScanAreaTopMargin(),
                getScanAreaRightMargin(),
                getScanAreaBottomMargin()
        );
    }
    //endregion

    //region Point of Interest Settings.
    public PointWithUnit getPointOfInterest() {
        return pointOfInterest;
    }

    public FloatWithUnit getPointOfInterestX() {
        return pointOfInterest.getX();
    }

    public FloatWithUnit getPointOfInterestY() {
        return pointOfInterest.getY();
    }

    public void setPointOfInterestX(FloatWithUnit pointOfInterestX) {
        pointOfInterest = new PointWithUnit(
                pointOfInterestX,
                getPointOfInterestY()
        );
    }

    public void setPointOfInterestY(FloatWithUnit pointOfInterestY) {
        pointOfInterest = new PointWithUnit(
                getPointOfInterestX(),
                pointOfInterestY
        );
    }
    //endregion

    //region Overlay Settings.
    public void setNewBrush(Brush brush) {
        barcodeCaptureOverlay.setBrush(brush);
    }

    public Brush getCurrentBrush() {
        return barcodeCaptureOverlay.getBrush();
    }

    public void setOverlayStyle(BarcodeCaptureOverlayStyle style) {
        // Get previous settings.
        boolean shouldShowScanAreaGuides = getShouldShowScanAreaGuides();
        Viewfinder previousViewfinder = getCurrentViewfinder();
        // Create new overlay with given style, reapply previous settings.
        barcodeCaptureOverlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, null, style);
        setNewBrush(barcodeCaptureOverlay.getBrush());
        setShouldShowScanAreaGuides(shouldShowScanAreaGuides);
        setViewfinder(previousViewfinder);
    }

    public BarcodeCaptureOverlayStyle getOverlayStyle() {
        return barcodeCaptureOverlay.getStyle();
    }

    public Map<BarcodeCaptureOverlayStyle, BrushStyleEntry[]> getBrushes() {
        return brushes;
    }
    //endregion

    //region Viewfinder Settings.
    public Viewfinder getCurrentViewfinder() {
        return barcodeCaptureOverlay.getViewfinder();
    }

    public void setViewfinder(Viewfinder viewfinder) {
        barcodeCaptureOverlay.setViewfinder(viewfinder);
    }

    public RectangularEnabledColor getRectangularViewfinderColor() {
        return rectangularColor;
    }

    public void setRectangularViewfinderColor(RectangularEnabledColor rectangularEnabledColor) {
        this.rectangularColor = rectangularEnabledColor;
    }

    public RectangularDisabledColor getRectangularViewfinderDisabledColor() {
        return rectangularDisabledColor;
    }

    public void setRectangularViewfinderDisabledColor(
            RectangularDisabledColor rectangularDisabledColor) {
        this.rectangularDisabledColor = rectangularDisabledColor;
    }

    public SizeSpecification getRectangularViewfinderSizeSpecification() {
        return rectangularViewfinderSizeSpecification;
    }

    public void setRectangularViewfinderSizeSpecification(
            SizeSpecification rectangularViewfinderSizeSpecification
    ) {
        this.rectangularViewfinderSizeSpecification = rectangularViewfinderSizeSpecification;
    }

    public FloatWithUnit getRectangularViewfinderWidth() {
        return rectangularViewfinderWidth;
    }

    public void setRectangularViewfinderWidth(FloatWithUnit rectangularViewfinderWidth) {
        this.rectangularViewfinderWidth = rectangularViewfinderWidth;
    }

    public FloatWithUnit getRectangularViewfinderHeight() {
        return rectangularViewfinderHeight;
    }

    public void setRectangularViewfinderHeight(FloatWithUnit rectangularViewfinderHeight) {
        this.rectangularViewfinderHeight = rectangularViewfinderHeight;
    }

    public FloatWithUnit getRectangularViewfinderShorterDimension() {
        return rectangularViewfinderShorterDimension;
    }

    public void setRectangularViewfinderShorterDimension(
            FloatWithUnit rectangularViewfinderShorterDimension
    ) {
        this.rectangularViewfinderShorterDimension = rectangularViewfinderShorterDimension;
    }

    public float getRectangularViewfinderWidthAspect() {
        return rectangularViewfinderWidthAspect;
    }

    public void setRectangularViewfinderWidthAspect(float rectangularViewfinderWidthAspect) {
        this.rectangularViewfinderWidthAspect = rectangularViewfinderWidthAspect;
    }

    public float getRectangularViewfinderHeightAspect() {
        return rectangularViewfinderHeightAspect;
    }

    public void setRectangularViewfinderHeightAspect(float rectangularViewfinderHeightAspect) {
        this.rectangularViewfinderHeightAspect = rectangularViewfinderHeightAspect;
    }

    public float getRectangularViewfinderLongerDimensionAspect() {
        return rectangularViewfinderLongerDimensionAspect;
    }

    public void setRectangularViewfinderLongerDimensionAspect(
            float rectangularViewfinderLongerDimensionAspect
    ) {
        this.rectangularViewfinderLongerDimensionAspect = rectangularViewfinderLongerDimensionAspect;
    }

    public RectangularViewfinderStyle getRectangularViewfinderStyle() {
        return rectangularViewfinderStyle;
    }

    public void setRectangularViewfinderStyle(RectangularViewfinderStyle style) {
        this.rectangularViewfinderStyle = style;
    }

    public RectangularViewfinderLineStyle getRectangularViewfinderLineStyle() {
        return rectangularViewfinderLineStyle;
    }

    public void setRectangularViewfinderLineStyle(RectangularViewfinderLineStyle style) {
        this.rectangularViewfinderLineStyle = style;
    }

    public float getRectangularViewfinderDimming() {
        return rectangularViewfinderDimming;
    }

    public void setRectangularViewfinderDimming(float dimming) {
        this.rectangularViewfinderDimming = dimming;
    }

    public boolean getRectangularViewfinderAnimationEnabled() {
        return rectangularViewfinderAnimation;
    }

    public void setRectangularViewfinderAnimationEnabled(boolean enabled) {
        this.rectangularViewfinderAnimation = enabled;
    }

    public boolean getRectangularViewfinderLoopingEnabled() {
        return rectangularViewfinderLooping;
    }

    public void setRectangularViewfinderLoopingEnabled(boolean enabled) {
        this.rectangularViewfinderLooping = enabled;
    }

    public LaserlineEnabledColor getLaserlineViewfinderEnabledColor() {
        return laserlineViewfinderEnabledColor;
    }

    public void setLaserlineViewfinderEnabledColor(
            LaserlineEnabledColor laserlineViewfinderEnabledColor
    ) {
        this.laserlineViewfinderEnabledColor = laserlineViewfinderEnabledColor;
    }

    public LaserlineDisabledColor getLaserlineViewfinderDisabledColor() {
        return laserlineViewfinderDisabledColor;
    }

    public void setLaserlineViewfinderDisabledColor(
            LaserlineDisabledColor laserlineViewfinderDisabledColor
    ) {
        this.laserlineViewfinderDisabledColor = laserlineViewfinderDisabledColor;
    }

    public FloatWithUnit getLaserlineViewfinderWidth() {
        return laserlineViewfinderWidth;
    }

    public void setLaserlineViewfinderWidth(FloatWithUnit laserlineViewfinderWidth) {
        this.laserlineViewfinderWidth = laserlineViewfinderWidth;
    }

    public LaserlineViewfinderStyle getLaserlineViewfinderStyle() {
        return laserlineViewfinderStyle;
    }

    public void setLaserlineViewfinderStyle(LaserlineViewfinderStyle style) {
        this.laserlineViewfinderStyle = style;
    }

    public ViewfinderTypeAimer.FrameColor getAimerViewfinderFrameColor() {
        return aimerViewfinderFrameColor;
    }

    public void setAimerViewfinderFrameColor(
            ViewfinderTypeAimer.FrameColor aimerViewfinderFrameColor
    ) {
        this.aimerViewfinderFrameColor = aimerViewfinderFrameColor;
    }

    public ViewfinderTypeAimer.DotColor getAimerViewfinderDotColor() {
        return aimerViewfinderDotColor;
    }

    public void setAimerViewfinderDotColor(
            ViewfinderTypeAimer.DotColor aimerViewfinderDotColor
    ) {
        this.aimerViewfinderDotColor = aimerViewfinderDotColor;
    }
    //endregion

    //region Logo Settings.
    public Anchor getLogoAnchor() {
        return logoAnchor;
    }

    public void setLogoAnchor(Anchor logoAnchor) {
        this.logoAnchor = logoAnchor;
    }

    public FloatWithUnit getAnchorXOffset() {
        return anchorXOffset;
    }

    public void setAnchorXOffset(FloatWithUnit anchorXOffset) {
        this.anchorXOffset = anchorXOffset;
    }

    public FloatWithUnit getAnchorYOffset() {
        return anchorYOffset;
    }

    public void setAnchorYOffset(FloatWithUnit anchorYOffset) {
        this.anchorYOffset = anchorYOffset;
    }

    public LogoStyle getLogoStyle() {
        return logoStyle;
    }

    public void setLogoStyle(LogoStyle logoStyle) {
        this.logoStyle = logoStyle;
    }
    //endregion

    //region Gestures Settings.
    public boolean isTapToFocusEnabled() {
        return tapToFocusEnabled;
    }

    public void setTapToFocusEnabled(boolean tapToFocusEnabled) {
        this.tapToFocusEnabled = tapToFocusEnabled;
    }

    public boolean isSwipeToZoomEnabled() {
        return swipeToZoomEnabled;
    }

    public void setSwipeToZoomEnabled(boolean swipeToZoomEnabled) {
        this.swipeToZoomEnabled = swipeToZoomEnabled;
    }
    //endregion

    //region Controls Settings.
    public boolean isZoomButtonEnabled() {
        return zoomButtonEnabled;
    }

    public void setZoomButtonEnabled(boolean zoomButtonEnabled) {
        this.zoomButtonEnabled = zoomButtonEnabled;
    }

    public boolean isTorchButtonEnabled() {
        return torchButtonEnabled;
    }

    public void setTorchButtonEnabled(boolean torchButtonEnabled) {
        this.torchButtonEnabled = torchButtonEnabled;
    }

    public boolean isCameraButtonEnabled() {
        return cameraButtonEnabled;
    }

    public void setCameraButtonEnabled(boolean cameraButtonEnabled) {
        this.cameraButtonEnabled = cameraButtonEnabled;
    }
    //endregion
    //endregion

    //region Result Handling Settings.
    public boolean isContinuousScanningEnabled() {
        return continuousScanningEnabled;
    }

    public void setContinuousScanningEnabled(boolean enabled) {
        continuousScanningEnabled = enabled;
    }
    //endregion
}

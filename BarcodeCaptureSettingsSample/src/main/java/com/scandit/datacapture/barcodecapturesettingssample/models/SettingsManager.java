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

import androidx.annotation.Nullable;
import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.capture.SymbologySettings;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.ViewfinderTypeLaserline;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.ViewfinderTypeRectangular;
import com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification;
import com.scandit.datacapture.core.area.LocationSelection;
import com.scandit.datacapture.core.area.RadiusLocationSelection;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.feedback.Feedback;
import com.scandit.datacapture.core.common.feedback.Sound;
import com.scandit.datacapture.core.common.feedback.Vibration;
import com.scandit.datacapture.core.common.geometry.*;
import com.scandit.datacapture.core.source.*;
import com.scandit.datacapture.core.time.TimeInterval;
import com.scandit.datacapture.core.ui.overlay.DataCaptureOverlay;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.Viewfinder;

import java.util.Collections;
import java.util.HashSet;

public class SettingsManager {

    private static final String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private static SettingsManager CURRENT = new SettingsManager();

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

    private Brush defaultBrush;

    private ViewfinderTypeRectangular.Color rectangularViewfinderColor =
            ViewfinderTypeRectangular.Color.DEFAULT;
    private SizeSpecification rectangularViewfinderSizeSpecification =
            SizeSpecification.WIDTH_AND_HEIGHT;
    private FloatWithUnit rectangularViewfinderWidth;
    private FloatWithUnit rectangularViewfinderHeight;
    private float rectangularViewfinderWidthAspect = 0f;
    private float rectangularViewfinderHeightAspect = 0f;

    private FloatWithUnit laserlineViewfinderWidth = new FloatWithUnit(0.75f, MeasureUnit.FRACTION);
    private ViewfinderTypeLaserline.EnabledColor laserlineViewfinderEnabledColor =
            ViewfinderTypeLaserline.EnabledColor.DEFAULT;
    private ViewfinderTypeLaserline.DisabledColor laserlineViewfinderDisabledColor =
            ViewfinderTypeLaserline.DisabledColor.DEFAULT;

    private Anchor logoAnchor = Anchor.BOTTOM_RIGHT;
    private FloatWithUnit anchorXOffset = new FloatWithUnit(0f, MeasureUnit.FRACTION);
    private FloatWithUnit anchorYOffset = new FloatWithUnit(0f, MeasureUnit.FRACTION);

    private boolean torchButtonEnabled = false;

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
        barcodeCaptureOverlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, null);
        defaultBrush = barcodeCaptureOverlay.getBrush();

        // Create a temporary RectangularViewfinder instance to get default values for width and height.
        RectangularViewfinder tempRectangularViewfinder = new RectangularViewfinder();
        rectangularViewfinderWidth = tempRectangularViewfinder.getSizeWithUnitAndAspect().getWidthAndHeight().getWidth();
        rectangularViewfinderHeight = tempRectangularViewfinder.getSizeWithUnitAndAspect().getWidthAndHeight().getHeight();
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

    public DataCaptureOverlay getBarcodeCaptureOverlay() {
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
    public Brush getDefaultBrush() {
        return defaultBrush;
    }

    public void setNewBrush(Brush brush) {
        barcodeCaptureOverlay.setBrush(brush);
    }

    public Brush getCurrentBrush() {
        return barcodeCaptureOverlay.getBrush();
    }
    //endregion

    //region Viewfinder Settings.
    public Viewfinder getCurrentViewfinder() {
        return barcodeCaptureOverlay.getViewfinder();
    }

    public void setViewfinder(Viewfinder viewfinder) {
        barcodeCaptureOverlay.setViewfinder(viewfinder);
    }

    public ViewfinderTypeRectangular.Color getRectangularViewfinderColor() {
        return rectangularViewfinderColor;
    }

    public void setRectangularViewfinderColor(
            ViewfinderTypeRectangular.Color rectangularViewfinderColor
    ) {
        this.rectangularViewfinderColor = rectangularViewfinderColor;
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

    public ViewfinderTypeLaserline.EnabledColor getLaserlineViewfinderEnabledColor() {
        return laserlineViewfinderEnabledColor;
    }

    public void setLaserlineViewfinderEnabledColor(
            ViewfinderTypeLaserline.EnabledColor laserlineViewfinderEnabledColor
    ) {
        this.laserlineViewfinderEnabledColor = laserlineViewfinderEnabledColor;
    }

    public ViewfinderTypeLaserline.DisabledColor getLaserlineViewfinderDisabledColor() {
        return laserlineViewfinderDisabledColor;
    }

    public void setLaserlineViewfinderDisabledColor(
            ViewfinderTypeLaserline.DisabledColor laserlineViewfinderDisabledColor
    ) {
        this.laserlineViewfinderDisabledColor = laserlineViewfinderDisabledColor;
    }

    public FloatWithUnit getLaserlineViewfinderWidth() {
        return laserlineViewfinderWidth;
    }

    public void setLaserlineViewfinderWidth(FloatWithUnit laserlineViewfinderWidth) {
        this.laserlineViewfinderWidth = laserlineViewfinderWidth;
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
    //endregion

    //region Controls Settings.
    public boolean isTorchButtonEnabled() {
        return torchButtonEnabled;
    }

    public void setTorchButtonEnabled(boolean torchButtonEnabled) {
        this.torchButtonEnabled = torchButtonEnabled;
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

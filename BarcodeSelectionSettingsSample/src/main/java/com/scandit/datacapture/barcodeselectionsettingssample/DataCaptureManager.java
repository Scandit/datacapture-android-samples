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

package com.scandit.datacapture.barcodeselectionsettingssample;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.scandit.datacapture.barcode.capture.SymbologySettings;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelection;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionAimerSelection;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionAutoSelectionStrategy;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionFreezeBehavior;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionManualSelectionStrategy;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionSettings;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionTapBehavior;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionTapSelection;
import com.scandit.datacapture.barcode.selection.feedback.BarcodeSelectionFeedback;
import com.scandit.datacapture.barcode.selection.ui.overlay.BarcodeSelectionBasicOverlay;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.SettingsManager;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.type.SelectionTypeSettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.view.overlay.OverlaySettingsFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.view.viewfinder.ViewfinderSettingsFragment;
import com.scandit.datacapture.core.common.feedback.Feedback;
import com.scandit.datacapture.core.common.feedback.Vibration;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MarginsWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;
import com.scandit.datacapture.core.data.Range;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraPosition;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.TorchState;
import com.scandit.datacapture.core.source.VideoResolution;
import com.scandit.datacapture.core.time.TimeInterval;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.AimerViewfinder;

import java.util.HashSet;
import java.util.Set;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

class DataCaptureManager {

    private static DataCaptureManager INSTANCE;

    static DataCaptureManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataCaptureManager(context);
        }
        return INSTANCE;
    }

    private final SettingsManager settingsManager;

    @ColorInt private final int scanditBlue;
    private final Brush blueBrush;
    private final Brush filledBlueBrush;

    private DataCaptureManager(Context context) {
        settingsManager = new SettingsManager(context);
        scanditBlue = ContextCompat.getColor(context, R.color.colorAccent);
        blueBrush = new Brush(Color.TRANSPARENT, scanditBlue, 1f);
        filledBlueBrush = new Brush(scanditBlue, scanditBlue, 1f);
    }

    public Camera buildCamera() {
        // Build a Camera object applying the CameraPosition retrieved from the settings or default to WORLD_FACING.
        CameraPosition cameraPosition = settingsManager.retrieveEnum(
                CameraPosition.class, CAMERA_POSITION_KEY, CameraPosition.WORLD_FACING
        );
        // The camera might be null, e.g. the device doesn't have a specific or any camera.
        Camera camera = Camera.getCamera(cameraPosition);
        if (camera != null) {
            // Apply the desired torch state from the settings or default to OFF.
            retrieveAndApplyTorchState(camera);
        }
        return camera;
    }

    private void retrieveAndApplyTorchState(Camera camera) {
        // Retrieve the torch enable flag from the desired settings and map it to TorchState.
        boolean torchEnabled = settingsManager.retrieveBoolean(TORCH_KEY, false);
        TorchState torchState;
        if (torchEnabled) {
            torchState = TorchState.ON;
        } else {
            torchState = TorchState.OFF;
        }
        // Apply the TorchState to the camera.
        camera.setDesiredTorchState(torchState);
    }

    public CameraSettings buildCameraSettings() {
        // Build a CameraSettings object with the default recommended settings for BarcodeSelection mode.
        CameraSettings settings = BarcodeSelection.createRecommendedCameraSettings();
        // Apply the preferred resolution from the settings or default to AUTO.
        retrieveAndApplyVideoResolution(settings);
        // Apply the zoom factor from the settings or default to 1.
        retrieveAndApplyZoomFactor(settings);
        return settings;
    }

    private void retrieveAndApplyVideoResolution(CameraSettings settings) {
        // Retrieve the video resolution from the desired settings.
        VideoResolution resolution = settingsManager.retrieveEnum(
                VideoResolution.class, CAMERA_PREFERRED_RESOLUTION_KEY, VideoResolution.AUTO
        );
        // Apply it to the camera settings.
        settings.setPreferredResolution(resolution);
    }

    private void retrieveAndApplyZoomFactor(CameraSettings settings) {
        // Retrieve the zoom factor from the desired settings.
        int zoomFactor = settingsManager.retrieveInt(CAMERA_ZOOM_FACTOR_KEY, 1);
        // Apply it to the camera settings
        settings.setZoomFactor(zoomFactor);
    }

    public BarcodeSelectionSettings buildBarcodeSelectionSettings() {
        // The barcode selection process is configured through barcode selection settings
        // which are then applied to the barcode selection instance that manages barcode recognition.
        BarcodeSelectionSettings settings = new BarcodeSelectionSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable the symbology from the settings.
        // In your own app ensure that you only enable the symbologies that your app requires as
        // every additional enabled symbology has an impact on processing times.
        for (Symbology symbology: Symbology.values()) {
            setupSymbology(settings, symbology);
        }

        // BarcodeSelection allows you to choose the way selected barcodes are used.
        // In this sample, we're applying the SelectionType from the settings.
        setupBarcodeSelectionType(settings);

        // BarcodeSelection allows you to automatically select a barcode when it's the only one
        // on the screen. In this sample, we're applying this flag from the settings.
        setupBarcodeSelectionAutoDetection(settings);

        // Applying the code duplicate filter means that the scanner won't report the same code
        // as recognized for the set time once it's recognized.
        setupCodeDuplicateFilter(settings);

        return settings;
    }

    private void setupSymbology(BarcodeSelectionSettings settings, Symbology symbology) {
        // Enable or disable the symbology from the settings.
        boolean enabled = settingsManager.retrieveBoolean(getSymbologyEnabledKey(symbology), false);
        settings.enableSymbology(symbology, enabled);
        if (enabled) { // If enabled, further setup.
            SymbologySettings symbologySettings = settings.getSymbologySettings(symbology);
            // Enable/Disable colorInverted flag from settings.
            retrieveAndApplyColorInverted(symbologySettings, symbology);
            // Setup symbolCount from settings.
            retrieveAndApplySymbolCount(symbologySettings, symbology);
            // Setup enabled extensions from settings.
            retrieveAndApplyExtensions(symbologySettings, symbology);
        }
    }

    private void retrieveAndApplyColorInverted(SymbologySettings symbologySettings, Symbology symbology) {
        // If the colorInvertedEnabled flag is set in the desired settings, retrieve and apply it.
        String key = getSymbologyColorInvertedEnabledKey(symbology);
        if (settingsManager.isKeySet(key)) {
            boolean colorInvertedEnabled = settingsManager.retrieveBoolean(key, false);
            symbologySettings.setColorInvertedEnabled(colorInvertedEnabled);
        }
    }

    private void retrieveAndApplySymbolCount(SymbologySettings symbologySettings, Symbology symbology) {
        String minSymbolCountKey = getSymbologyMinRangeKey(symbology);
        String maxSymbolCountKey = getSymbologyMaxRangeKey(symbology);
        // If the min and max symbolCount is set in the desired settings, retrieve and apply it.
        if (settingsManager.isKeySet(minSymbolCountKey) && settingsManager.isKeySet(maxSymbolCountKey)) {
            Range defaultRange = SymbologyDescription.create(symbology).getDefaultSymbolCountRange();

            short minSymbolCounts = settingsManager.retrieveShortByCasting(
                    minSymbolCountKey, (short) defaultRange.getMinimum()
            );
            short maxSymbolCounts = settingsManager.retrieveShortByCasting(
                    maxSymbolCountKey, (short) defaultRange.getMaximum()
            );

            // Create a Set<Short> from the retrieved minimum/maximum.
            Set<Short> symbolCounts = new HashSet<>();
            for (short i = minSymbolCounts; i <= maxSymbolCounts; i++) {
                symbolCounts.add(i);
            }
            symbologySettings.setActiveSymbolCounts(symbolCounts);
        }
    }

    private void retrieveAndApplyExtensions(SymbologySettings symbologySettings, Symbology symbology) {
        // If an extension has been set in the desired settings, retrieve and apply its enabled state.
        Set<String> extensions = SymbologyDescription.create(symbology).getSupportedExtensions();
        for (String extension: extensions) {
            String key = getSymbologyExtensionKey(symbology, extension);
            if (settingsManager.isKeySet(key)) {
                boolean enabled = settingsManager.retrieveBoolean(key, symbologySettings.isExtensionEnabled(extension));
                symbologySettings.setExtensionEnabled(extension, enabled);
            }
        }
    }

    private void setupBarcodeSelectionType(BarcodeSelectionSettings settings) {
        SelectionTypeSettingsFragment.SelectionType selectionType = settingsManager.retrieveEnum(
                SelectionTypeSettingsFragment.SelectionType.class, SELECTION_TYPES_KEY, SelectionTypeSettingsFragment.SelectionType.TAP_TO_SELECT
        );
        switch (selectionType) {
            case AIM_TO_SELECT:
                setupBarcodeSelectionAimerSelection(settings);
                break;
            case TAP_TO_SELECT:
                setupBarcodeSelectionTapSelection(settings);
                break;
        }
    }

    private void setupBarcodeSelectionTapSelection(BarcodeSelectionSettings settings) {
        // Create and set BarcodeSelectionTapSelection.
        BarcodeSelectionTapSelection tapSelection = new BarcodeSelectionTapSelection();
        settings.setSelectionType(tapSelection);
        // Retrieve desired FreezeBehavior / TapBehavior and apply them.
        setupFreezeBehavior(tapSelection);
        setupTapBehavior(tapSelection);
    }

    private void setupFreezeBehavior(BarcodeSelectionTapSelection tapSelection) {
        BarcodeSelectionFreezeBehavior freezeBehavior = settingsManager.retrieveEnum(
                BarcodeSelectionFreezeBehavior.class, FREEZE_BEHAVIOR_KEY, BarcodeSelectionFreezeBehavior.MANUAL
        );
        tapSelection.setFreezeBehavior(freezeBehavior);
    }

    private void setupTapBehavior(BarcodeSelectionTapSelection tapSelection) {
        BarcodeSelectionTapBehavior tapBehavior = settingsManager.retrieveEnum(
                BarcodeSelectionTapBehavior.class, TAP_BEHAVIOR_KEY, BarcodeSelectionTapBehavior.TOGGLE_SELECTION
        );
        tapSelection.setTapBehavior(tapBehavior);
    }

    private void setupBarcodeSelectionAimerSelection(BarcodeSelectionSettings settings) {
        // Create and set BarcodeSelectionAimerSelection.
        BarcodeSelectionAimerSelection aimerSelection = new BarcodeSelectionAimerSelection();
        settings.setSelectionType(aimerSelection);
        // Retrieve desired AimerSelectionStrategy and apply it.
        setupAimerSelectionStrategy(aimerSelection);
    }

    private void setupAimerSelectionStrategy(BarcodeSelectionAimerSelection aimerSelection) {
        SelectionTypeSettingsFragment.SelectionStrategy selectionStrategy = settingsManager.retrieveEnum(
                SelectionTypeSettingsFragment.SelectionStrategy.class, AIMER_STRATEGY_KEY, SelectionTypeSettingsFragment.SelectionStrategy.MANUAL
        );
        switch (selectionStrategy) {
            case MANUAL:
                aimerSelection.setSelectionStrategy(new BarcodeSelectionManualSelectionStrategy());
                break;
            case AUTO:
                aimerSelection.setSelectionStrategy(new BarcodeSelectionAutoSelectionStrategy());
                break;
        }
    }

    private void setupBarcodeSelectionAutoDetection(BarcodeSelectionSettings settings) {
        boolean autoDetection = settingsManager.retrieveBoolean(AUTO_DETECTION_KEY, false);
        settings.setSingleBarcodeAutoDetection(autoDetection);
    }

    private void setupCodeDuplicateFilter(BarcodeSelectionSettings settings) {
        float codeDuplicateFilter = settingsManager.retrieveFloatByCasting(DUPLICATE_FILTER_KEY, .5f);
        settings.setCodeDuplicateFilter(TimeInterval.seconds(codeDuplicateFilter));
    }

    public void setupBarcodeSelection(BarcodeSelection barcodeSelection) {
        // Set the center of the location selection and aimer from the desired settings.
        setupPointOfInterest(barcodeSelection);
        // Reset the barcodeSelectionSession if requested from the settings.
        checkAndResetBarcodeSelection(barcodeSelection);
        // Set the feedback from the settings.
        // Feedbacks determine what sound and/or vibration should be emitted when selecting barcodes.
        setupFeedback(barcodeSelection);
    }

    private void setupPointOfInterest(BarcodeSelection barcodeSelection) {
        boolean pointOfInterestEnabled = settingsManager.retrieveBoolean(POINT_OF_INTEREST_ENABLED_KEY, false);
        if (!pointOfInterestEnabled) {
            barcodeSelection.setPointOfInterest(null);
        } else {
            PointWithUnit poi = settingsManager.retrievePointWithUnit(POINT_OF_INTEREST_X_KEY, POINT_OF_INTEREST_Y_KEY);
            barcodeSelection.setPointOfInterest(poi);
        }
    }

    private void checkAndResetBarcodeSelection(BarcodeSelection barcodeSelection) {
        boolean resetRequested = settingsManager.retrieveBoolean(RESET_KEY, false);
        if (resetRequested) {
            barcodeSelection.reset();
            settingsManager.setBoolean(RESET_KEY, false);
        }
    }

    private void setupFeedback(BarcodeSelection barcodeSelection) {
        boolean sound = settingsManager.retrieveBoolean(FEEDBACK_SOUND_KEY, true);
        boolean vibration = settingsManager.retrieveBoolean(FEEDBACK_VIBRATION_KEY, false);
        Feedback defaultFeedback = BarcodeSelectionFeedback.defaultFeedback().getSelection();
        Feedback newFeedback = new Feedback(
                vibration ? Vibration.defaultVibration() : null, // Default vibration or null.
                sound ? defaultFeedback.getSound() : null // Default BarcodeSelectionFeedback sound or null.
        );
        BarcodeSelectionFeedback bsFeedback = new BarcodeSelectionFeedback();
        bsFeedback.setSelection(newFeedback);
        barcodeSelection.setFeedback(bsFeedback);
    }

    public void setupDataCaptureView(DataCaptureView view) {
        // If you want to further restrict the scanning area, e.g. to leave a region around the
        // border of the preview, you can pass the desired scan area margins.
        setupScanAreaMargins(view);

        // The point of interest is used to control the *center of attention* for the following subsystems:
        // - Auto focus and exposure metering of the camera.
        // - Rendered Viewfinders.
        setupPointOfInterest(view);
    }

    private void setupScanAreaMargins(DataCaptureView view) {
        // Retrieve the scanAreaMargins from the desired settings.
        MarginsWithUnit margins = settingsManager.retrieveMarginsWithUnit(
                SCAN_AREA_MARGIN_LEFT_KEY, SCAN_AREA_MARGIN_TOP_KEY, SCAN_AREA_MARGIN_RIGHT_KEY, SCAN_AREA_MARGIN_BOTTOM_KEY
        );
        // Apply the scanAreaMargins, if some data is missing, we apply empty margins.
        if (margins != null) {
            view.setScanAreaMargins(margins);
        } else {
            view.setScanAreaMargins(
                    new MarginsWithUnit(
                            new FloatWithUnit(0, MeasureUnit.FRACTION),
                            new FloatWithUnit(0, MeasureUnit.FRACTION),
                            new FloatWithUnit(0, MeasureUnit.FRACTION),
                            new FloatWithUnit(0, MeasureUnit.FRACTION)
                    )
            );
        }
    }

    private void setupPointOfInterest(DataCaptureView view) {
        // Retrieve the pointOfInterest from the desired settings.
        PointWithUnit pointOfInterest = settingsManager.retrievePointWithUnit(
                VIEW_POINT_OF_INTEREST_X_KEY, VIEW_POINT_OF_INTEREST_Y_KEY
        );
        // Apply the pointOfInterest, if some data is missing, we apply the center of the preview.
        if (pointOfInterest != null) {
            view.setPointOfInterest(pointOfInterest);
        } else {
            view.setPointOfInterest(
                    new PointWithUnit(
                            new FloatWithUnit(.5f, MeasureUnit.FRACTION),
                            new FloatWithUnit(.5f, MeasureUnit.FRACTION)
                    )
            );
        }
    }

    public void setupBarcodeSelectionBasicOverlay(BarcodeSelectionBasicOverlay overlay) {
        // Settings shouldShowScanAreaGuides flag to true will make the overlay visualize the
        // active scan area used for BarcodeSelection.
        setupShouldShowScanAreaGuides(overlay);

        // Setting the brush applied to Tracked Barcodes.
        setupTrackedBarcodeBrush(overlay);
        // Setting the brush applied to Aimed Barcodes.
        setupAimedBarcodeBrush(overlay);
        // Setting the brush applied to Selecting Barcodes.
        setupSelectingBarcodeBrush(overlay);
        // Setting the brush applied to Selected Barcodes.
        setupSelectedBarcodeBrush(overlay);

        // Settings shouldShowHints to the overlay will visualize some hints explaining how to
        // use barcode selection.
        setupShouldShowHints(overlay);

        // Setting the viewfinder's frame color.
        setupViewfinderFrameColor(overlay);
        // Setting the viewfinder's dot color.
        setupViewfinderDotColor(overlay);
    }

    private void setupShouldShowScanAreaGuides(BarcodeSelectionBasicOverlay overlay) {
        // Retrieve shouldShowScanAreaGuides flag from the desired settings.
        boolean shouldShowScanAreaGuides = settingsManager.retrieveBoolean(SCAN_AREA_GUIDES_KEY, false);
        // Apply it to the barcodeSelectionBasicOverlay.
        overlay.setShouldShowScanAreaGuides(shouldShowScanAreaGuides);
    }

    private void setupTrackedBarcodeBrush(BarcodeSelectionBasicOverlay overlay) {
        // Retrieve the brush style from the desired settings and convert it to a Scandit Brush.
        OverlaySettingsFragment.BrushStyle style = settingsManager.retrieveEnum(
                OverlaySettingsFragment.BrushStyle.class, OVERLAY_TRACKED_BRUSH_KEY, OverlaySettingsFragment.BrushStyle.DEFAULT
        );
        Brush brush = null;
        switch (style) {
            case DEFAULT:
                brush = BarcodeSelectionBasicOverlay.DEFAULT_TRACKED_BRUSH;
                break;
            case BLUE:
                brush = blueBrush;
                break;
        }
        // Apply the brush to the overlay.
        overlay.setTrackedBrush(brush);
    }

    private void setupAimedBarcodeBrush(BarcodeSelectionBasicOverlay overlay) {
        // Retrieve the brush style from the desired settings and convert it to a Scandit Brush.
        OverlaySettingsFragment.BrushStyle style = settingsManager.retrieveEnum(
                OverlaySettingsFragment.BrushStyle.class, OVERLAY_AIMED_BRUSH_KEY, OverlaySettingsFragment.BrushStyle.DEFAULT
        );
        Brush brush = null;
        switch (style) {
            case DEFAULT:
                brush = BarcodeSelectionBasicOverlay.DEFAULT_AIMED_BRUSH;
                break;
            case BLUE:
                brush = filledBlueBrush;
                break;
        }
        // Apply the brush to the overlay.
        overlay.setAimedBrush(brush);
    }

    private void setupSelectingBarcodeBrush(BarcodeSelectionBasicOverlay overlay) {
        // Retrieve the brush style from the desired settings and convert it to a Scandit Brush.
        OverlaySettingsFragment.BrushStyle style = settingsManager.retrieveEnum(
                OverlaySettingsFragment.BrushStyle.class, OVERLAY_SELECTING_BRUSH_KEY, OverlaySettingsFragment.BrushStyle.DEFAULT
        );
        Brush brush = null;
        switch (style) {
            case DEFAULT:
                brush = BarcodeSelectionBasicOverlay.DEFAULT_SELECTING_BRUSH;
                break;
            case BLUE:
                brush = blueBrush;
                break;
        }
        // Apply the brush to the overlay.
        overlay.setSelectingBrush(brush);
    }

    private void setupSelectedBarcodeBrush(BarcodeSelectionBasicOverlay overlay) {
        // Retrieve the brush style from the desired settings and convert it to a Scandit Brush.
        OverlaySettingsFragment.BrushStyle style = settingsManager.retrieveEnum(
                OverlaySettingsFragment.BrushStyle.class, OVERLAY_SELECTED_BRUSH_KEY, OverlaySettingsFragment.BrushStyle.DEFAULT
        );
        Brush brush = null;
        switch (style) {
            case DEFAULT:
                brush = BarcodeSelectionBasicOverlay.DEFAULT_SELECTED_BRUSH;
                break;
            case BLUE:
                brush = blueBrush;
                break;
        }
        // Apply the brush to the overlay.
        overlay.setSelectedBrush(brush);
    }

    private void setupShouldShowHints(BarcodeSelectionBasicOverlay overlay) {
        // Retrieve shouldShowHints flag from the desired settings.
        boolean shouldShowHints = settingsManager.retrieveBoolean(OVERLAY_HINTS_KEY, false);
        // Apply it to the barcodeSelectionBasicOverlay.
        overlay.setShouldShowHints(shouldShowHints);
    }

    private void setupViewfinderFrameColor(BarcodeSelectionBasicOverlay overlay) {
        // Retrieve the viewfinder color from the desired settings and convert it to color.
        ViewfinderSettingsFragment.ViewfinderColor viewfinderColor = settingsManager.retrieveEnum(
                ViewfinderSettingsFragment.ViewfinderColor.class, VIEWFINDER_FRAME_COLOR_KEY, ViewfinderSettingsFragment.ViewfinderColor.DEFAULT
        );
        int color = Color.WHITE;
        switch (viewfinderColor) {
            case DEFAULT:
                color = Color.WHITE;
                break;
            case BLUE:
                color = scanditBlue;
                break;
        }
        // Apply it to the viewfinder.
        ((AimerViewfinder) overlay.getViewfinder()).setFrameColor(color);
    }

    private void setupViewfinderDotColor(BarcodeSelectionBasicOverlay overlay) {
        // Retrieve the viewfinder color from the desired settings and convert it to color.
        ViewfinderSettingsFragment.ViewfinderColor viewfinderColor = settingsManager.retrieveEnum(
                ViewfinderSettingsFragment.ViewfinderColor.class, VIEWFINDER_DOT_COLOR_KEY, ViewfinderSettingsFragment.ViewfinderColor.DEFAULT
        );
        int color = Color.WHITE;
        switch (viewfinderColor) {
            case DEFAULT:
                color = Color.WHITE;
                break;
            case BLUE:
                color = scanditBlue;
                break;
        }
        // Apply it to the viewfinder.
        ((AimerViewfinder) overlay.getViewfinder()).setDotColor(color);
    }
}

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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.symbologies.symbology;

import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.barcode.data.Checksum;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.data.Range;

import java.util.Objects;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class SpecificSymbologyViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();
    private final Symbology symbology;
    private final SymbologyDescription symbologyDescription;

    public SpecificSymbologyViewModel(String symbologyIdentifier) {
        symbology = Objects.requireNonNull(
            SymbologyDescription.forIdentifier(symbologyIdentifier)
        ).getSymbology();
        symbologyDescription = SymbologyDescription.create(symbology);
    }

    String getSymbologyReadableName() {
        return symbologyDescription.getReadableName();
    }

    boolean isCurrentSymbologyEnabled() {
        return settingsManager.isSymbologyEnabled(symbology);
    }

    void setCurrentSymbologyEnabled(boolean enabled) {
        settingsManager.enableSymbology(symbology, enabled, true);
    }

    boolean isColorInvertedSettingsAvailable() {
        return symbologyDescription.isColorInvertible();
    }

    boolean isCurrentSymbologyColorInverted() {
        return settingsManager.isColorInverted(symbology);
    }

    void setCurrentSymbologyColorInverted(boolean colorInverted) {
        settingsManager.setColorInverted(symbology, colorInverted);
    }

    boolean isRangeSettingsAvailable() {
        Range range = getCompleteRange();
        return range.getMinimum() != range.getMaximum();
    }

    Range getCompleteRange() {
        return symbologyDescription.getActiveSymbolCountRange();
    }

    int getCurrentMinActiveSymbolCount() {
        return settingsManager.getMinSymbolCount(symbology);
    }

    void setCurrentMinActiveSymbolCount(int minSymbolCount) {
        settingsManager.setMinSymbolCount(symbology, (short) minSymbolCount);
    }

    int getCurrentMaxActiveSymbolCount() {
        return settingsManager.getMaxSymbolCount(symbology);
    }

    void setCurrentMaxActiveSymbolCount(int minSymbolCount) {
        settingsManager.setMaxSymbolCount(symbology, (short) minSymbolCount);
    }

    boolean extensionsAvailable() {
        return !symbologyDescription.getSupportedExtensions().isEmpty();
    }

    SymbologyExtensionEntry[] getExtensionsAndEnabledState() {

        Set<String> supportedExtensions = symbologyDescription.getSupportedExtensions();
        SymbologyExtensionEntry[] extensionsAndEnabledState =
                new SymbologyExtensionEntry[supportedExtensions.size()];

        int counter = 0;
        for (String extension : supportedExtensions) {
            extensionsAndEnabledState[counter] = new SymbologyExtensionEntry(
                    extension, settingsManager.isExtensionEnabled(symbology, extension)
            );
            counter++;
        }
        return extensionsAndEnabledState;
    }

    void toggleExtension(String extension) {
        settingsManager.setExtensionEnabled(
                symbology, extension, !settingsManager.isExtensionEnabled(symbology, extension)
        );
    }

    boolean checksumsAvailable() {
        return !symbologyDescription.getSupportedChecksums().isEmpty();
    }

    SymbologyChecksumEntry[] getChecksumsAndEnabledState() {

        Set<Checksum> supportedChecksums = symbologyDescription.getSupportedChecksums();
        SymbologyChecksumEntry[] checksumsAndEnabledState =
                new SymbologyChecksumEntry[supportedChecksums.size()];

        int counter = 0;
        for (Checksum checksum : supportedChecksums) {
            checksumsAndEnabledState[counter] = new SymbologyChecksumEntry(
                    checksum, settingsManager.isChecksumEnabled(symbology, checksum)
            );
            counter++;
        }
        return checksumsAndEnabledState;
    }

    void toggleChecksum(Checksum checksum) {
        settingsManager.setChecksumEnabled(
                symbology, checksum, !settingsManager.isChecksumEnabled(symbology, checksum)
        );
    }
}

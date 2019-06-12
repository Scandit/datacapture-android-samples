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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder;

import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type.*;
import com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification;

@SuppressWarnings("WeakerAccess")
public class ViewfinderSettingsViewModel extends ViewfinderTypeViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    ViewfinderType[] getViewfinderTypes() {
        return new ViewfinderType[]{
                ViewfinderTypeNone.fromCurrentViewFinder(settingsManager.getCurrentViewfinder()),
                ViewfinderTypeRectangular.fromCurrentViewfinderAndSettings(
                        settingsManager.getCurrentViewfinder(), settingsManager
                ),
                ViewfinderTypeLaserline.fromCurrentViewfinderAndSettings(
                        settingsManager.getCurrentViewfinder(), settingsManager
                )
        };
    }

    void setViewfinderType(ViewfinderType viewfinderType) {
        if (viewfinderType instanceof ViewfinderTypeRectangular) {
            ViewfinderTypeRectangular casted = (ViewfinderTypeRectangular) viewfinderType;
            settingsManager.setRectangularViewfinderColor(casted.getColor());
            settingsManager.setRectangularViewfinderSizeSpecification(
                    casted.getSizeSpecification()
            );
            settingsManager.setRectangularViewfinderWidth(casted.getWidth());
            settingsManager.setRectangularViewfinderWidthAspect(casted.getWidthAspect());
            settingsManager.setRectangularViewfinderHeight(casted.getHeight());
            settingsManager.setRectangularViewfinderHeightAspect(casted.getHeightAspect());
        } else if (viewfinderType instanceof ViewfinderTypeLaserline) {
            ViewfinderTypeLaserline casted = (ViewfinderTypeLaserline) viewfinderType;
            settingsManager.setLaserlineViewfinderWidth(casted.getWidth());
            settingsManager.setLaserlineViewfinderEnabledColor(casted.getEnabledColor());
            settingsManager.setLaserlineViewfinderDisabledColor(casted.getDisabledColor());
        }
        updateViewfinder(viewfinderType);
    }

    void setRectangularViewfinderColor(ViewfinderTypeRectangular.Color color) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setColor(color);
            setViewfinderType(currentViewfinder);
        }
    }

    void setRectangularViewfinderSizeSpec(SizeSpecification spec) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setSizeSpecification(spec);
            setViewfinderType(currentViewfinder);
        }
    }

    void setRectangularViewfinderHeightAspect(float aspect) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setHeightAspect(aspect);
            setViewfinderType(currentViewfinder);
        }
    }

    void setRectangularViewfinderWidthAspect(float aspect) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setWidthAspect(aspect);
            setViewfinderType(currentViewfinder);
        }
    }

    void setLaserlineViewfinderEnabledColor(ViewfinderTypeLaserline.EnabledColor color) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeLaserline) {
            ((ViewfinderTypeLaserline) currentViewfinder).setEnabledColor(color);
            setViewfinderType(currentViewfinder);
        }
        settingsManager.setLaserlineViewfinderEnabledColor(color);
    }

    void setLaserlineViewfinderDisabledColor(ViewfinderTypeLaserline.DisabledColor color) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeLaserline) {
            ((ViewfinderTypeLaserline) currentViewfinder).setDisabledColor(color);
            setViewfinderType(currentViewfinder);
        }
        settingsManager.setLaserlineViewfinderDisabledColor(color);
    }
}

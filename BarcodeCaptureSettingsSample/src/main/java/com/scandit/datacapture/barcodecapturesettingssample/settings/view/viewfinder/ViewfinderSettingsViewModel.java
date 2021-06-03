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
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinderStyle;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderLineStyle;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle;

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
                ),
                ViewfinderTypeAimer.fromCurrentViewfinderAndSettings(
                        settingsManager.getCurrentViewfinder(), settingsManager
                )
        };
    }

    void setViewfinderType(ViewfinderType viewfinderType) {
        if (viewfinderType instanceof ViewfinderTypeRectangular) {
            ViewfinderTypeRectangular casted = (ViewfinderTypeRectangular) viewfinderType;
            settingsManager.setRectangularViewfinderColor(casted.getColor());
            settingsManager.setRectangularViewfinderDisabledColor(casted.getDisabledColor());
            settingsManager.setRectangularViewfinderDimming(casted.getDimming());
            settingsManager.setRectangularViewfinderAnimationEnabled(casted.getAnimation());
            settingsManager.setRectangularViewfinderLoopingEnabled(casted.getLooping());
            settingsManager.setRectangularViewfinderSizeSpecification(
                    casted.getSizeSpecification()
            );
            settingsManager.setRectangularViewfinderWidth(casted.getWidth());
            settingsManager.setRectangularViewfinderWidthAspect(casted.getWidthAspect());
            settingsManager.setRectangularViewfinderHeight(casted.getHeight());
            settingsManager.setRectangularViewfinderHeightAspect(casted.getHeightAspect());
            settingsManager.setRectangularViewfinderShorterDimension(casted.getShorterDimension());
            settingsManager.setRectangularViewfinderLongerDimensionAspect(casted.getLongerDimensionAspect());
            settingsManager.setRectangularViewfinderStyle(casted.getStyle());
            settingsManager.setRectangularViewfinderLineStyle(casted.getLineStyle());
        } else if (viewfinderType instanceof ViewfinderTypeLaserline) {
            ViewfinderTypeLaserline casted = (ViewfinderTypeLaserline) viewfinderType;
            settingsManager.setLaserlineViewfinderWidth(casted.getWidth());
            settingsManager.setLaserlineViewfinderEnabledColor(casted.getEnabledColor());
            settingsManager.setLaserlineViewfinderDisabledColor(casted.getDisabledColor());
            settingsManager.setLaserlineViewfinderStyle(casted.getStyle());
        } else if (viewfinderType instanceof ViewfinderTypeAimer) {
            ViewfinderTypeAimer casted = (ViewfinderTypeAimer) viewfinderType;
            settingsManager.setAimerViewfinderFrameColor(casted.getFrameColor());
            settingsManager.setAimerViewfinderDotColor(casted.getDotColor());
        }
        updateViewfinder(viewfinderType);
    }

    void setRectangularViewfinderColor(RectangularEnabledColor color) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setColor(color);
            setViewfinderType(currentViewfinder);
        }
    }

    void setRectangularViewfinderDisabledColor(RectangularDisabledColor color) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setDisabledColor(color);
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

    void setRectangularViewfinderShorterDimension(float fraction) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setShorterDimension(
                    new FloatWithUnit(fraction, MeasureUnit.FRACTION));
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

    void setRectangularViewfinderLongerDimensionAspect(float aspect) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setLongerDimensionAspect(aspect);
            setViewfinderType(currentViewfinder);
        }
    }

    void setRectangularViewfinderStyle(RectangularViewfinderStyle style) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setStyle(style);
            currentViewfinder.resetDefaults();
            setViewfinderType(currentViewfinder);
        }
    }

    void setRectangularViewfinderLineStyle(RectangularViewfinderLineStyle style) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setLineStyle(style);
            setViewfinderType(currentViewfinder);
        }
    }

    void setRectangularDimming(float dimming) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setDimming(dimming);
            setViewfinderType(currentViewfinder);
        }
        settingsManager.setRectangularViewfinderDimming(dimming);
    }

    void setRectangularAnimationEnabled(boolean enabled) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setAnimation(enabled);
            setViewfinderType(currentViewfinder);
        }
        settingsManager.setRectangularViewfinderAnimationEnabled(enabled);
    }

    void setRectangularLoopingEnabled(boolean enabled) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeRectangular) {
            ((ViewfinderTypeRectangular) currentViewfinder).setLooping(enabled);
            setViewfinderType(currentViewfinder);
        }
        settingsManager.setRectangularViewfinderLoopingEnabled(enabled);
    }

    void setLaserlineViewfinderEnabledColor(LaserlineEnabledColor color) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeLaserline) {
            ((ViewfinderTypeLaserline) currentViewfinder).setEnabledColor(color);
            setViewfinderType(currentViewfinder);
        }
        settingsManager.setLaserlineViewfinderEnabledColor(color);
    }

    void setLaserlineViewfinderDisabledColor(LaserlineDisabledColor color) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeLaserline) {
            ((ViewfinderTypeLaserline) currentViewfinder).setDisabledColor(color);
            setViewfinderType(currentViewfinder);
        }
        settingsManager.setLaserlineViewfinderDisabledColor(color);
    }

    void setLaserlineViewfinderStyle(LaserlineViewfinderStyle style) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeLaserline) {
            ((ViewfinderTypeLaserline) currentViewfinder).setStyle(style);
            currentViewfinder.resetDefaults();
            setViewfinderType(currentViewfinder);
        }
    }

    void setAimerViewfinderFrameColor(ViewfinderTypeAimer.FrameColor color) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeAimer) {
            ((ViewfinderTypeAimer) currentViewfinder).setFrameColor(color);
            setViewfinderType(currentViewfinder);
        }
        settingsManager.setAimerViewfinderFrameColor(color);
    }

    void setAimerViewfinderDotColor(ViewfinderTypeAimer.DotColor color) {
        ViewfinderType currentViewfinder = getCurrentViewfinderType();
        if (currentViewfinder instanceof ViewfinderTypeAimer) {
            ((ViewfinderTypeAimer) currentViewfinder).setDotColor(color);
            setViewfinderType(currentViewfinder);
        }
        settingsManager.setAimerViewfinderDotColor(color);
    }
}

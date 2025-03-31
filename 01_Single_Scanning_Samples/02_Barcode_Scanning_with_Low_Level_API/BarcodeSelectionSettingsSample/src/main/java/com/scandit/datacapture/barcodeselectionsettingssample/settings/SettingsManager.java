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

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MarginsWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.*;

public final class SettingsManager {

    private final SharedPreferences sharedPrefs;

    public SettingsManager(Context context) {
        sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public PointWithUnit retrievePointWithUnit(String xKey, String yKey, PointWithUnit defaultValue) {
        FloatWithUnit x = retrieveFloatWithUnit(xKey, defaultValue.getX());
        FloatWithUnit y = retrieveFloatWithUnit(yKey, defaultValue.getY());
        return new PointWithUnit(x, y);
    }

    public FloatWithUnit retrieveFloatWithUnit(String key, FloatWithUnit defaultValue) {
        String valueKey = getFloatWithUnitNumberKey(key);
        String measureUnitKey = getFloatWithUnitMeasureUnitKey(key);

        try {
            float value = Float.parseFloat(sharedPrefs.getString(valueKey, null));
            if (Float.isNaN(value) || Float.isInfinite(value)) {
                return defaultValue;
            }
            MeasureUnit measureUnit = MeasureUnit.valueOf(sharedPrefs.getString(measureUnitKey, null));
            return new FloatWithUnit(value, measureUnit);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean retrieveBoolean(String key, boolean defaultValue) {
        return sharedPrefs.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        sharedPrefs.edit()
                .putBoolean(key, value)
                .apply();
    }

    public int retrieveInt(String key, int defaultValue) {
        return sharedPrefs.getInt(key, defaultValue);
    }

    public String retrieveString(String key, @Nullable String defaultValue) {
        return sharedPrefs.getString(key, defaultValue);
    }

    public <T extends Enum<T>> T retrieveEnum(Class<T> c, String key, T defaultValue) {
        String retrievedValue = sharedPrefs.getString(key, null);
        if (retrievedValue != null) {
            try {
                return Enum.valueOf(c, retrievedValue);
            } catch (Exception e) {}
        }
        return defaultValue;
    }

    public short retrieveShortByCasting(String key, short defaultValue) {
        String stringValue = retrieveString(key, null);
        try {
            return Short.parseShort(stringValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float retrieveFloatByCasting(String key, float defaultValue) {
        String stringValue = retrieveString(key, null);
        try {
            float value = Float.parseFloat(stringValue);
            if (!Float.isInfinite(value) && !Float.isNaN(value)) {
                return value;
            }
        } catch (Exception e) {}
        return defaultValue;
    }

    public boolean isKeySet(String key) {
        return sharedPrefs.contains(key);
    }
}

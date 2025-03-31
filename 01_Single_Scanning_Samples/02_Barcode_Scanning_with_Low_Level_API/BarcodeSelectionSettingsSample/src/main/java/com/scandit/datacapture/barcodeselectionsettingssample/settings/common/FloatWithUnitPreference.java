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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.common;

import android.content.Context;

import androidx.annotation.StringRes;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;

public final class FloatWithUnitPreference extends StyledPreference {

   private final String prefix;
   private final FloatWithUnit defaultValue;

   public FloatWithUnitPreference(
           final Context context, String key, @StringRes int title, boolean showDivider, FloatWithUnit defaultValue
   ) {
      super(context, key, context.getString(title), -1, showDivider);
      this.prefix = key;
      this.defaultValue = defaultValue;
      setSummaryProvider(new SummaryProvider<FloatWithUnitPreference>() {
         @Override
         public CharSequence provideSummary(FloatWithUnitPreference preference) {
            return buildSummary();
         }
      });
   }

   private String buildSummary() {
      String valueKey = SharedPrefsConstants.getFloatWithUnitNumberKey(prefix);
      String measureUnitKey = SharedPrefsConstants.getFloatWithUnitMeasureUnitKey(prefix);
      String valueString = getSharedPreferences().getString(valueKey, null);
      String measureUnitString = getSharedPreferences().getString(measureUnitKey, null);

      try {
          float value = Float.parseFloat(valueString);
          MeasureUnit measureUnit = MeasureUnit.valueOf(measureUnitString);
          return getContext().getString(
                  R.string.float_with_unit_parametrised, value, measureUnit.name()
          );
      } catch (Exception e) {
         return getContext().getString(
                 R.string.float_with_unit_parametrised,
                 defaultValue.getValue(),
                 defaultValue.getUnit().name()
         );
      }
   }
}

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

public final class FloatWithUnitPreference extends StyledPreference {

   private final String prefix;

   public FloatWithUnitPreference(final Context context, String key, @StringRes int title, boolean showDivider) {
      super(context, key, context.getString(title), -1, showDivider);
      this.prefix = key;
      setSummaryProvider(new SummaryProvider<FloatWithUnitPreference>() {
         @Override
         public CharSequence provideSummary(FloatWithUnitPreference preference) {
            String valueKey = SharedPrefsConstants.getFloatWithUnitNumberKey(prefix);
            String measureUnitKey = SharedPrefsConstants.getFloatWithUnitMeasureUnitKey(prefix);
            String value = getSharedPreferences().getString(valueKey, null);
            String finalValue = null;
            try {
               finalValue = String.valueOf(Float.parseFloat(value));
            } catch (Exception e) { }
            String measureUnitString = getSharedPreferences().getString(measureUnitKey, null);
            if (finalValue == null || measureUnitString == null) {
               return context.getString(R.string.not_set);
            } else {
               return finalValue + " (" + measureUnitString + ")";
            }
         }
      });
   }
}

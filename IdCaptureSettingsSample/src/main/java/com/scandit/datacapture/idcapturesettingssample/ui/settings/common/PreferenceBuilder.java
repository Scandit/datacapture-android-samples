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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.common;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;

import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.utils.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class PreferenceBuilder {

   public static PreferenceCategory category(Context context) {
      return category(context, "");
   }

   public static PreferenceCategory category(
           Context context,
           String title
   ) {
      PreferenceCategory category = new PreferenceCategory(context);
      category.setIconSpaceReserved(false);
      category.setTitle(title);
      return category;
   }

   public static SwitchPreferenceCompat _switch(
           Context context,
           String key,
           String title,
           boolean defaultValue
   ) {
      SwitchPreferenceCompat preference = new SwitchPreferenceCompat(context);
      preference.setIconSpaceReserved(false);
      preference.setKey(key);
      preference.setTitle(title);
      preference.setDefaultValue(defaultValue);
      return preference;
   }

   public static SeekBarPreference seekBar(
           Context context,
           String key,
           String title,
           int min,
           int max,
           int defaultValue
   ) {
      SeekBarPreference preference = new SeekBarPreference(context);
      preference.setIconSpaceReserved(false);
      preference.setKey(key);
      preference.setTitle(title);
      preference.setMin(min);
      preference.setMax(max);
      preference.setDefaultValue(defaultValue);
      preference.setShowSeekBarValue(true);
      return preference;
   }

   public static DropDownPreference dropDown(
           Context context,
           String key,
           String title,
           CharSequence[] entries,
           CharSequence[] entryValues,
           String defaultValue
   ) {
      DropDownPreference preference = new DropDownPreference(context);
      preference.setIconSpaceReserved(false);
      preference.setKey(key);
      preference.setTitle(title);
      preference.setEntries(entries);
      preference.setEntryValues(entryValues);
      preference.setDefaultValue(defaultValue);
      preference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
      return preference;
   }

   public static MultiSelectListPreference multiSelectList(
           Context context,
           String key,
           String title,
           CharSequence[] entries,
           CharSequence[] entryValues
   ) {
      return multiSelectList(
              context,
              key,
              title,
              entries,
              entryValues,
              new MultiSelectListPreferenceSummaryProvider()
      );
   }

   public static MultiSelectListPreference multiSelectList(
           Context context,
           String key,
           String title,
           CharSequence[] entries,
           CharSequence[] entryValues,
           Preference.SummaryProvider<MultiSelectListPreference> summaryProvider
   ) {
      MultiSelectListPreference preference = new MultiSelectListPreference(context);
      preference.setIconSpaceReserved(false);
      preference.setKey(key);
      preference.setTitle(title);
      preference.setEntries(entries);
      preference.setEntryValues(entryValues);
      preference.setSummaryProvider(summaryProvider);
      return preference;
   }

   public static EditTextPreference numericEditText(
           Context context,
           String key,
           String title,
           int maxLength,
           String defaultValue
   ) {
      EditTextPreference preference = new EditTextPreference(context);
      preference.setIconSpaceReserved(false);
      preference.setKey(key);
      preference.setTitle(title);
      preference.setDefaultValue(defaultValue);
      preference.setOnBindEditTextListener(editText -> {
         editText.setSelectAllOnFocus(true);
         editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
         editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
      });
      preference.setSummaryProvider(new NumericEditTextSummaryProvider());
      return preference;
   }

   public static EditTextPreference editText(
           Context context,
           String key,
           String title
   ) {
      EditTextPreference preference = new EditTextPreference(context);
      preference.setIconSpaceReserved(false);
      preference.setKey(key);
      preference.setTitle(title);
      preference.setSummaryProvider(new EditTextSummaryProvider());
      return preference;
   }

   public static Preference floatWithUnitPreviewPreference(
           Context context,
           String valueKey,
           String measureUnitKey,
           FloatWithUnit defaultValue
   ) {
      Preference preference = preference(context);
      preference.setSummaryProvider(
              new FloatWithUnitPreviewSummary(valueKey, measureUnitKey, defaultValue)
      );
      return preference;
   }

   public static Preference preference(Context context) {
      return preference(context, null);
   }

   public static Preference preference(Context context, String title) {
      Preference preference = new Preference(context);
      preference.setIconSpaceReserved(false);
      preference.setTitle(title);
      return preference;
   }

   private static class FloatWithUnitPreviewSummary
           implements Preference.SummaryProvider<Preference> {

      private final String valueKey;
      private final String measureUnitKey;
      private final FloatWithUnit defaultValue;

      FloatWithUnitPreviewSummary(String valueKey, String measureUnitKey, FloatWithUnit defaultValue) {
         this.valueKey = valueKey;
         this.measureUnitKey = measureUnitKey;
         this.defaultValue = defaultValue;
      }

      @Override
      public CharSequence provideSummary(Preference preference) {
         String valueString = preference.getPreferenceDataStore().getString(
                 valueKey, String.valueOf(defaultValue.getValue())
         );
         String measureUnitString = preference.getPreferenceDataStore().getString(
                 measureUnitKey, defaultValue.getUnit().name()
         );

         try {
            float value = Float.parseFloat(valueString);
            MeasureUnit measureUnit = MeasureUnit.valueOf(measureUnitString);
            return preference.getContext().getString(
                    R.string.float_with_unit_parametrised, value, measureUnit.name()
            );
         } catch (NullPointerException | IllegalArgumentException e) {
            return preference.getContext().getString(
                    R.string.float_with_unit_parametrised,
                    defaultValue.getValue(),
                    defaultValue.getUnit().name()
            );
         }
      }
   }

   private static class MultiSelectListPreferenceSummaryProvider
           implements Preference.SummaryProvider<MultiSelectListPreference> {
      @Override
      public CharSequence provideSummary(MultiSelectListPreference preference) {
         Set<String> values = preference.getValues()
                 .stream().map(StringUtils::toTitleCase).collect(Collectors.toSet());

         if (values.isEmpty()) {
            return preference.getContext().getString(R.string.settings_value_none);
         } else {
            return values.stream().collect(Collectors.joining(", "));
         }
      }
   }

   private static class NumericEditTextSummaryProvider implements Preference.SummaryProvider<EditTextPreference> {

      @Override
      public CharSequence provideSummary(EditTextPreference preference) {
            String value = preference.getText();
            try {
               float number = Float.parseFloat(value);
               if (Float.isInfinite(number) || Float.isNaN(number)) {
                  value = preference.getContext().getString(R.string.invalid_number);
               } else {
                  value = String.valueOf(number);
               }
            } catch (NumberFormatException e) {
               value = preference.getContext().getString(R.string.invalid_number);
            } catch (NullPointerException e) {
               value = preference.getContext().getString(R.string.settings_value_none);
            }
            return value;
      }
   }

   private static class EditTextSummaryProvider implements Preference.SummaryProvider<EditTextPreference> {

      @Override
      public CharSequence provideSummary(EditTextPreference preference) {
         return preference.getText();
      }
   }

}

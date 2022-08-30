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

package com.scandit.datacapture.idcaptureextendedsample.mappers;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.ChinaExitEntryPermitMrzResult;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.ResultEntry;

import java.util.ArrayList;

public final class ChinaExitEntryPermitMrzResultMapper extends ResultMapper {

   private final ChinaExitEntryPermitMrzResult result;

   public ChinaExitEntryPermitMrzResultMapper(CapturedId capturedId) {
      super(capturedId);
      result = capturedId.getChinaExitEntryPermitMrz();
   }

   /*
    * We extract all the ChinaExitEntryPermitMrzResult's specific fields and add them to the ones from
    * CapturedId.
    */
   @Override
   public ArrayList<ResultEntry> mapResult() {
      ArrayList<ResultEntry> result = super.mapResult();

      result.add(new ResultEntry("Document Code", extractField(
              this.result.getDocumentCode())));
      result.add(new ResultEntry("Captured MRZ", extractField(
              this.result.getCapturedMrz())));

      return result;
   }
}


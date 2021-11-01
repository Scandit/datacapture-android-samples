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
import com.scandit.datacapture.id.data.MrzResult;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.ResultEntry;

import java.util.ArrayList;

public final class MrzResultMapper extends ResultMapper {

    private final MrzResult mrzResult;

    public MrzResultMapper(CapturedId capturedId) {
        super(capturedId);
        mrzResult = capturedId.getMrz();
    }

    /*
     * We extract all the MrzResult's specific fields and add them to the ones from CapturedId.
     */
    @Override
    public ArrayList<ResultEntry> mapResult() {
        ArrayList<ResultEntry> result = super.mapResult();

        result.add(new ResultEntry("Document Code", extractField(mrzResult.getDocumentCode())));
        result.add(new ResultEntry("Names Are Truncated", extractField(mrzResult.getNamesAreTruncated())));
        result.add(new ResultEntry("Optional", extractField(mrzResult.getOptional())));
        result.add(new ResultEntry("Optional1", extractField(mrzResult.getOptional1())));

        return result;
    }
}

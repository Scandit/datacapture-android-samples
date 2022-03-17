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

package com.scandit.datacapture.reorderfromcatalogsample.ui.result;

import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.reorderfromcatalogsample.data.BarcodeResult;
import com.scandit.datacapture.reorderfromcatalogsample.data.BarcodeSelectionRepository;
import com.scandit.datacapture.reorderfromcatalogsample.di.Injector;

import java.util.List;

public class ResultsViewModel extends ViewModel {

    /**
     * The repository to interact with the BarcodeSelection mode. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final BarcodeSelectionRepository barcodeSelectionRepository =
            Injector.getInstance().getBarcodeSelectionRepository();

    /**
     * Retrieves the currently selected barcodes from the repository
     */
    public List<BarcodeResult> getSelectedBarcodes() {
        return barcodeSelectionRepository.getSelectedResults();
    }

    /**
     * Clear the current selection results.
     */
    public void clearResults() {
        barcodeSelectionRepository.clearSelection();
    }
}

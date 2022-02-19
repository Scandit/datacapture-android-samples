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

package com.scandit.datacapture.vincodessample.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.vincodessample.data.VinParserRepository;
import com.scandit.datacapture.vincodessample.data.VinResult;
import com.scandit.datacapture.vincodessample.di.Injector;

/**
 * The ViewModel for the screen that displays the data decoded from a Vehicle Identification Number (VIN).
 */
public class VinResultViewModel extends ViewModel {
    /**
     * The repository that sends the data decoded from a Vehicle Identification Number (VIN).
     * We use our own dependency injection to obtain it, but you may use your favorite framework,
     * like Dagger or Hilt instead.
     */
    private final VinParserRepository vinParserRepository = Injector.getInstance().getVinParserRepository();

    /**
     * The observer of data captured from personal identification documents or their parts.
     */
    private final Observer<VinResult> vinObserver = this::onVinParsed;

    /**
     * The stream of data decoded from a Vehicle Identification Number (VIN).
     */
    private final MutableLiveData<VinResult> vins = new MutableLiveData<>();

    public VinResultViewModel() {
        /*
         * Observe the streams of data from the lower layer.
         */
        vinParserRepository.getVins().observeForever(vinObserver);
    }

    @Override
    protected void onCleared() {
        /*
         * Stop observing the streams of the lower layer and the timer events to avoid memory leak.
         */
        vinParserRepository.getVins().removeObserver(vinObserver);
    }

    public void onVinParsed(VinResult vin) {
        /*
         * Display the data decoded from a Vehicle Identification Number (VIN).
         */
        vins.postValue(vin);
    }

    /**
     * The stream of data decoded from a Vehicle Identification Number (VIN).
     */
    public LiveData<VinResult> vins() {
        return vins;
    }
}

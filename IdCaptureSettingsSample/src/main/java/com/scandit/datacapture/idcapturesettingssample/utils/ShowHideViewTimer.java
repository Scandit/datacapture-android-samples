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

package com.scandit.datacapture.idcapturesettingssample.utils;

import android.os.CountDownTimer;
import android.view.View;

/**
 * Timer class which hides the given view on finish.
 */
public final class ShowHideViewTimer extends CountDownTimer {

    private final View view;

    public ShowHideViewTimer(View view) {
        super(2000, 2000);
        this.view = view;
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTick(long millisUntilFinished) {}

    @Override
    public void onFinish() {
        view.setVisibility(View.GONE);
    }
}

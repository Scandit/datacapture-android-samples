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

package com.scandit.datacapture.idcapturesettingssample.utils

import android.os.CountDownTimer
import android.view.View

/**
 * Timer class which hides the given view on finish.
 */
class ShowHideViewTimer(
    private val view: View,
    durationMillis: Long = 2000
) : CountDownTimer(durationMillis, durationMillis) {

    init {
        view.visibility = View.VISIBLE
    }

    override fun onTick(millisUntilFinished: Long) {}

    override fun onFinish() {
        view.visibility = View.GONE
    }
}

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

package com.scandit.datacapture.idcaptureimageuploadsample

import android.app.Application
import com.scandit.datacapture.idcaptureimageuploadsample.di.Injector

/**
 * Provides the application [Context] to the [Injector] so its context-bound singletons (the image
 * loader and the PDF converter) can be shared across the view models and the UI.
 */
class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Injector.initialize(this)
    }
}

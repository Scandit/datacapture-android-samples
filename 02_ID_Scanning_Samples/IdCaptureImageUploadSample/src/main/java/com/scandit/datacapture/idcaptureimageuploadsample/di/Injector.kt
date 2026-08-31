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

package com.scandit.datacapture.idcaptureimageuploadsample.di

import android.content.Context
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.idcaptureimageuploadsample.data.DataCaptureContextProvider
import com.scandit.datacapture.idcaptureimageuploadsample.data.DataCaptureContextRepository
import com.scandit.datacapture.idcaptureimageuploadsample.data.IdCaptureRepository
import com.scandit.datacapture.idcaptureimageuploadsample.data.ImageFrameSourceRepository
import com.scandit.datacapture.idcaptureimageuploadsample.utils.ImageLoader
import com.scandit.datacapture.idcaptureimageuploadsample.utils.PdfConverter

/**
 * A simple dependency injector. It may be replaced with a one from your favorite DI framework
 * like Dagger or Hilt.
 */
object Injector {

    private lateinit var applicationContext: Context

    /**
     * Provide the application [Context] used by the context-bound singletons below. Called once
     * from `SampleApplication.onCreate`.
     */
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    /**
     * Get the current DataCaptureContext.
     */
    val dataCaptureContext: DataCaptureContext
        get() = DataCaptureContextProvider.dataCaptureContext

    /**
     * The repository that observes the DataCaptureContext status to surface errors.
     */
    val dataCaptureContextRepository by lazy { DataCaptureContextRepository(dataCaptureContext) }

    /**
     * The repository that allows interaction with the IdCapture mode.
     */
    val idCaptureRepository by lazy { IdCaptureRepository(dataCaptureContext) }

    /**
     * The repository that feeds static images into the SDK.
     */
    val imageFrameSourceRepository by lazy { ImageFrameSourceRepository(dataCaptureContext) }

    /**
     * Decodes picked images into bitmaps. Stateless and safe to share across the app.
     */
    val imageLoader by lazy { ImageLoader(applicationContext) }

    /**
     * Rasterises picked PDF pages into bitmaps. Stateless and safe to share across the app; renders
     * are serialised internally through a process-global lock.
     */
    val pdfConverter by lazy { PdfConverter(applicationContext) }
}
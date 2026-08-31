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

package com.scandit.datacapture.idcaptureimageuploadsample.data

import android.graphics.Bitmap
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.async.Callback
import com.scandit.datacapture.core.source.BitmapFrameSource
import com.scandit.datacapture.core.source.FrameSourceState

/**
 * Feeds static images into the SDK via [BitmapFrameSource].
 *
 * A [BitmapFrameSource] wraps a single bitmap and emits exactly one frame: after the frame is
 * emitted it turns itself off. To process another image (for example the back side of a
 * double-sided document) create a new frame source and feed it again.
 *
 * Feed the next side only once the current side has actually been captured (signaled through
 * IdCapture's per-side `onIdCaptured`, which the sample enables via `notifyOnSideCapture`). The
 * data capture context only keeps a single in-flight frame, so changing the frame source before
 * the current frame has been processed would discard it.
 *
 * Typical flow per image:
 *  1. Wrap the bitmap in a [BitmapFrameSource] (the bitmap must be `ARGB_8888`).
 *  2. Set it as the frame source on the [DataCaptureContext].
 *  3. Switch the frame source on, which emits the single frame to IdCapture for processing.
 */
class ImageFrameSourceRepository(
    private val dataCaptureContext: DataCaptureContext,
) {

    /**
     * Feed [bitmap] to the SDK as a single frame. The capture result (or rejection) is delivered
     * asynchronously through [IdCaptureRepository]'s flows.
     */
    fun feedImage(bitmap: Bitmap) {
        val frameSource = BitmapFrameSource.of(bitmap.asArgb8888())

        // setFrameSource is asynchronous; only switch the source on once it is actually attached.
        dataCaptureContext.setFrameSource(frameSource) {
            frameSource.switchToDesiredState(FrameSourceState.ON)
        }
    }

    /**
     * Detach the current frame source from the context when leaving the scan flow.
     */
    fun clear() {
        dataCaptureContext.setFrameSource(null)
    }

    /**
     * [BitmapFrameSource] requires an `ARGB_8888` bitmap. Convert if necessary.
     */
    private fun Bitmap.asArgb8888(): Bitmap =
        if (config == Bitmap.Config.ARGB_8888) {
            this
        } else {
            copy(Bitmap.Config.ARGB_8888, false)
        }
}

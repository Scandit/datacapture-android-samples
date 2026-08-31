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

import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.capture.DataCaptureContextListener
import com.scandit.datacapture.core.common.ContextStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Observes the [DataCaptureContext] status and surfaces error states to the UI.
 */
class DataCaptureContextRepository(
    dataCaptureContext: DataCaptureContext,
) : DataCaptureContextListener {

    /**
     * Coroutine scope for emitting events from the SDK callback thread.
     * Uses SupervisorJob so failures in one emission don't cancel the entire scope.
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * The stream of context error messages. Replays the last one so a collector that subscribes
     * after the error occurred (e.g. an error emitted while the context is created) still receives
     * it.
     */
    private val _contextErrors = MutableSharedFlow<String>(replay = 1)
    val contextErrors: SharedFlow<String> = _contextErrors.asSharedFlow()

    init {
        dataCaptureContext.addListener(this)
    }

    override fun onStatusChanged(
        dataCaptureContext: DataCaptureContext,
        contextStatus: ContextStatus,
    ) {
        if (contextStatus.isValid) return
        scope.launch {
            _contextErrors.emit(contextStatus.message)
        }
    }
}

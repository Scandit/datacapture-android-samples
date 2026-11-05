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
package com.scandit.datacapture.listbuildingsamplecompose.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

class ScannedItemsViewModel : ViewModel() {

    /**
     * The barcodes scanned so far, newest first. The view model keeps them across a
     * configuration change such as a device rotation, and releases them once the activity
     * finishes for good. Composables reading this list recompose whenever it changes.
     */
    val items: SnapshotStateList<ScanResult> = mutableStateListOf()
}

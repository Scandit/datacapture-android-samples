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

package com.scandit.datacapture.matrixscancounttotemappingsample;

import android.content.Context;

import com.scandit.datacapture.barcode.count.capture.map.BarcodeSpatialGrid;
import com.scandit.datacapture.barcode.count.capture.map.BarcodeSpatialGridEditorView;
import com.scandit.datacapture.barcode.count.capture.map.BarcodeSpatialGridEditorViewSettings;

// Singleton object that centralises the management of the spatial grid.
public class SpatialGridManager {

    // Shared instance in singleton should only be available through getInstance() method.
    private static SpatialGridManager sharedInstance = null;

    // This method provides access to the shared SpatialGridManager instance.
    public static SpatialGridManager getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new SpatialGridManager();
        }
        return sharedInstance;
    }

    private BarcodeSpatialGrid spatialGrid;

    // Store a BarcodeSpatialGrid instance, to use later when creating the grid view.
    void setBarcodeSpatialGrid(BarcodeSpatialGrid spatialGrid) {
        this.spatialGrid = spatialGrid;
    }

    // Uses the BarcodeSpatialGrid instance to create a BarcodeSpatialGridEditorView, if possible.
    // Returns null otherwise.
    BarcodeSpatialGridEditorView createBarcodeSpatialGridEditorView(Context context) {
        if (spatialGrid != null) {
            return BarcodeSpatialGridEditorView.newInstance(
                context,
                spatialGrid,
                new BarcodeSpatialGridEditorViewSettings()
            );
        } else {
            return null;
        }
    }
}

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

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scandit.datacapture.barcode.count.capture.map.BarcodeSpatialGrid;
import com.scandit.datacapture.barcode.count.capture.map.BarcodeSpatialGridEditorView;
import com.scandit.datacapture.barcode.count.capture.map.BarcodeSpatialGridEditorViewListener;

public class MapGridActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create the editor view from the spatial map stored during scanning.
        BarcodeSpatialGridEditorView editorView =
            SpatialGridManager.getInstance().createBarcodeSpatialGridEditorView(this);

        if (editorView == null) {
            // If there is a problem generating the map, we show an error message.
            // This can happen if the barcodes aren't in a grid-like pattern, if there are not
            // enough barcodes to create a grid, or if the grid doesn't conform to the
            // right number of rows and columns.
            Toast.makeText(
                MapGridActivity.this,
                R.string.editor_view_error,
                Toast.LENGTH_SHORT
            ).show();
        } else {
            // Set a listener to the editorView, so we get notified
            // when any of the buttons is tapped.
            editorView.setListener(new BarcodeSpatialGridEditorViewListener() {
                @Override
                public void onEditingCancelled(@NonNull BarcodeSpatialGridEditorView view) {
                    finish();
                }

                @Override
                public void onEditingFinished(
                    @NonNull BarcodeSpatialGridEditorView view,
                    @NonNull BarcodeSpatialGrid spatialGrid
                ) {
                    // At this point, the BarcodeSpatialGrid instance can be used.
                    Toast.makeText(
                        MapGridActivity.this,
                        R.string.editor_view_success,
                        Toast.LENGTH_SHORT
                    ).show();
                }
            });

            // Set the editorView as content view.
            setContentView(editorView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}

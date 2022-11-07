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

package com.scandit.datacapture.idcaptureextendedsample.ui;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.scandit.datacapture.idcaptureextendedsample.R;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.CaptureResult;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.ResultFragment;

/**
 * The singleton Activity that displays the UI of this sample.
 */
public class MainActivity extends AppCompatActivity {
    private static final String KEY_RESULT_SCREEN = "RESULT_SCREEN";

    public MainActivity() {
        super(R.layout.main_activity);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Navigate to the screen that shows data extracted from a personal identification document
     * or its part.
     */
    public void goToResultScreen(CaptureResult captureResult) {
        if (getSupportFragmentManager().findFragmentByTag(KEY_RESULT_SCREEN) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scan_fragment_container, ResultFragment.create(captureResult), KEY_RESULT_SCREEN)
                    .addToBackStack(null)
                    .commit();
        }
    }
}

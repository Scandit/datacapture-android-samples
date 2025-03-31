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

package com.scandit.datacapture.restockingsample;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.scandit.datacapture.restockingsample.pick.PickFragment;

public class MainActivity extends CameraPermissionActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize(savedInstanceState);
    }

    private void initialize(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // Create the MainActivity with the PickFragment visible.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PickFragment.newInstance(), PickFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
    }
}

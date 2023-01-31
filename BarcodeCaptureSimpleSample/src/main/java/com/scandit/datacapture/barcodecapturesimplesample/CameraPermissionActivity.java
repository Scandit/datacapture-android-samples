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

package com.scandit.datacapture.barcodecapturesimplesample;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * An activity to request the camera permission.
 */
public abstract class CameraPermissionActivity extends AppCompatActivity {

    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int CAMERA_PERMISSION_REQUEST = 0;

    private boolean permissionDeniedOnce = false;
    private boolean paused = true;

    @Override
    protected void onPause() {
        super.onPause();

        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        paused = false;
    }

    protected boolean hasCameraPermission() {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || checkSelfPermission(CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestCameraPermission() {
        // For Android M and onwards we need to request the camera permission from the user.
        if (!hasCameraPermission()) {
            // The user already denied the permission once, we don't ask twice.
            if (!permissionDeniedOnce) {
                // It's clear why the camera is required. We don't need to give a detailed reason.
                requestPermissions(new String[] { CAMERA_PERMISSION }, CAMERA_PERMISSION_REQUEST);
            }

        } else {
            // We already have the permission or don't need it.
            onCameraPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionDeniedOnce = false;
                if (!paused) {
                    // Only call the function if not paused - camera should not be used otherwise.
                    onCameraPermissionGranted();
                }
            } else {
                // The user denied the permission - we are not going to ask again.
                permissionDeniedOnce = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public abstract void onCameraPermissionGranted();
}

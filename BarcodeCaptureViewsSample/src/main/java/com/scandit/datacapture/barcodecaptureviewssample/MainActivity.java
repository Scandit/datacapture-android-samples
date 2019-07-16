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

package com.scandit.datacapture.barcodecaptureviewssample;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.scandit.datacapture.barcodecaptureviewssample.modes.activity.FullscreenScanActivity;
import com.scandit.datacapture.barcodecaptureviewssample.modes.fragment.FullscreenScanFragmentContainerActivity;
import com.scandit.datacapture.barcodecaptureviewssample.modes.splitview.SplitViewScanActivity;
import com.scandit.datacapture.core.capture.DataCaptureVersion;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_title);

        final TextView textSdkVersion = findViewById(R.id.text_sdk_version);
        textSdkVersion.setText(getString(R.string.sdk_version, DataCaptureVersion.VERSION_STRING));

        final TextView textFullscreenFragment = findViewById(R.id.text_fullscreen);
        final TextView textSplitView = findViewById(R.id.text_split_view);
        final TextView textPickerActivity = findViewById(R.id.text_picker_activity);

        textFullscreenFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(FullscreenScanFragmentContainerActivity.getIntent(MainActivity.this));
            }
        });

        textSplitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SplitViewScanActivity.getIntent(MainActivity.this));
            }
        });

        textPickerActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(FullscreenScanActivity.getIntent(MainActivity.this));
            }
        });
    }
}

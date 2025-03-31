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

package com.scandit.datacapture.expirymanagementsample.results;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.scandit.datacapture.expirymanagementsample.R;
import com.scandit.datacapture.expirymanagementsample.managers.BarcodeCountCameraManager;

public class ResultsActivity extends AppCompatActivity {

    public static final int CLEAR_SESSION = 1;

    private static final String ARG_DONE_BUTTON_STYLE = "done-button-style";

    public static Intent getIntent(
        Context context,
        ExtraButtonStyle doneButtonStyle
    ) {
        return new Intent(context, ResultsActivity.class)
            .putExtra(ARG_DONE_BUTTON_STYLE, doneButtonStyle.mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ExtraButtonStyle extraButtonStyle = ExtraButtonStyle.valueOf(
            getIntent().getIntExtra(ARG_DONE_BUTTON_STYLE, 0)
        );

        // Setup ResultsListPresenter.
        FrameLayout container = findViewById(R.id.container);
        ResultsListPresenter resultsListPresenter = new ResultsListPresenter(
            this,
            container,
            extraButtonStyle,
            false
        );
        resultsListPresenter.setListener(new ResultsListPresenter.ResultsListPresenterListener() {

            @Override
            public void startNewButtonClicked() {
                finish();
            }

            @Override
            public void resumeButtonClicked() {
                finish();
            }

            @Override
            public void clearButtonClicked() {
                setResult(CLEAR_SESSION);
                finish();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        resultsListPresenter.refresh();
    }

    @Override
    protected void onPause() {
        // Pause camera if the app is going to background.
        if (!isFinishing()) {
            BarcodeCountCameraManager.getInstance().stopFrameSource();
        }

        super.onPause();
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

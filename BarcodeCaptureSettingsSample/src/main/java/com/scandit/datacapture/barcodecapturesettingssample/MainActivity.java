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

package com.scandit.datacapture.barcodecapturesettingssample;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.scandit.datacapture.barcodecapturesettingssample.scanning.BarcodeScanFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.SettingsOverviewFragment;

public class MainActivity extends AppCompatActivity {

    private static final String BACKSTACK_TAG_SCANNER = "scanner";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, BarcodeScanFragment.newInstance())
                    .commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnTouchListener(new OnDoubleTapGestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        popToScanner();
                        return true;
                    }
                })
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        if (selectedItemId == R.id.action_settings) {
            goToSettings();
            return true;
        } else if (selectedItemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void popToScanner() {
        getSupportFragmentManager()
                .popBackStack(BACKSTACK_TAG_SCANNER, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void goToSettings() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, SettingsOverviewFragment.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(BACKSTACK_TAG_SCANNER)
                .commit();
    }

    class OnDoubleTapGestureDetector extends GestureDetector implements View.OnTouchListener {

        OnDoubleTapGestureDetector(Context context, SimpleOnGestureListener listener) {
            super(context, listener);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return onTouchEvent(event);
        }
    }
}

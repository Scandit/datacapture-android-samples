package com.scandit.datacapture.usdlverificationsample.ui;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.scandit.datacapture.usdlverificationsample.R;

/**
 * The singleton Activity that displays the UI of this sample.
 */
public class MainActivity extends AppCompatActivity {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

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

package com.scandit.datacapture.idcaptureextendedsample.result;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.idcaptureextendedsample.R;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private final static String RESULT_FIELD = "result";
    private final static String IMAGE_FIELD = "image";

    /*
     * Store inside a bundle the result data and, if available, the byteArray extracted from the
     * Face image.
     */
    public static Intent getIntent(
            Context context, ArrayList<ResultEntry> result, Bitmap faceImage
    ) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putParcelableArrayListExtra(RESULT_FIELD, result);
        if (faceImage != null) {
            byte[] imageBytes = convertImageToBytes(faceImage);
            if (imageBytes.length > 0) {
                intent.putExtra(IMAGE_FIELD, imageBytes);
            }
        }
        return intent;
    }

    private static byte[] convertImageToBytes(Bitmap image) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            return byteArray;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        /*
         * Get and setup the toolbar to show the back arrow on the top left.
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RecyclerView recyclerResult = findViewById(R.id.results_list);
        ImageView imageFace = findViewById(R.id.image_face);

        // Retrieve the results map from the intent's bundle.
        ArrayList<ResultEntry> result = getIntent().getParcelableArrayListExtra(RESULT_FIELD);

        /*
         * Extract the image bytes and convert it back to Bitmap. This image is optional as some
         * document types do not support it.
         */
        Bitmap image = null;
        if (getIntent().hasExtra(IMAGE_FIELD)) {
            byte[] imageBytes = getIntent().getByteArrayExtra(IMAGE_FIELD);
            image = convertBytesToImage(imageBytes);
        }
        if (image != null) {
            imageFace.setImageBitmap(image);
        } else {
            imageFace.setVisibility(View.GONE);
        }

        /*
         * Setup the results list. Check the ResultListAdapter class for more info on displaying
         * the result items.
         */
        recyclerResult.addItemDecoration(
            new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        recyclerResult.setAdapter(new ResultListAdapter(result));
    }

    private Bitmap convertBytesToImage(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public boolean onOptionsItemSelected(
        @NonNull @NotNull MenuItem item
    ) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

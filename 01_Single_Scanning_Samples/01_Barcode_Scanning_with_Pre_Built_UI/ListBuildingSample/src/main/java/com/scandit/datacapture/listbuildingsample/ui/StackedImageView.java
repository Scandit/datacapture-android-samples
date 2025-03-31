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

package com.scandit.datacapture.listbuildingsample.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/*
 * Class to display a stack of images.
 *
 * Will show a maximum MAX_IMAGES of bitmaps stacked on top of each other.
 *
 * PEEK_SIZE_IN_DP will determine how much of one image is seen below it.
 */
public class StackedImageView extends FrameLayout {

    // how much of the underlying image is seen
    private static final int PEEK_SIZE_IN_DP = 4;

    private final float peekSizeInPixels =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PEEK_SIZE_IN_DP,
            getResources().getDisplayMetrics());

    // maximum number of image views that can be shown
    private static final int MAX_IMAGES = 3;

    private final List<ImageView> imageViews = new ArrayList<>();

    public StackedImageView(@NonNull Context context) {
        super(context);
        init();
    }

    public StackedImageView(
        @NonNull Context context, @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    public StackedImageView(
        @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT);

        for (int i = 0; i < MAX_IMAGES; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setVisibility(View.GONE);
            this.addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageViews.add(imageView);
        }
    }

    public void clearImages() {
        for (ImageView img : imageViews) {
            img.setVisibility(View.GONE);
            img.setImageBitmap(null);
        }
    }

    public void updateStackWith(List<Bitmap> images) {
        // take the latest MAX_IMAGES from the images list
        List<Bitmap> latestImages =
            images.subList(Math.max(images.size() - MAX_IMAGES, 0), images.size());

        // loop through the images to be shown
        for (int i = 0; i < latestImages.size(); i++) {

            // get corresponding image view and set the bitmap
            Bitmap img = latestImages.get(i);
            ImageView imageView = imageViews.get(i);

            imageView.setImageBitmap(img);
            imageView.setVisibility(View.VISIBLE);

            FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams) imageView.getLayoutParams();

            // calculate margins for sizing the image view
            int topAndLeftMargin = (int) ((latestImages.size() - 1 - i) * peekSizeInPixels);
            int bottomAndRightMargin = (int) (i * peekSizeInPixels);
            params.topMargin = topAndLeftMargin;
            params.leftMargin = topAndLeftMargin;
            params.bottomMargin = bottomAndRightMargin;
            params.rightMargin = bottomAndRightMargin;

            imageView.setLayoutParams(params);
        }
    }
}

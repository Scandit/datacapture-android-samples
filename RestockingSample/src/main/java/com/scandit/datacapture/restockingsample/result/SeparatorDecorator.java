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

package com.scandit.datacapture.restockingsample.result;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scandit.datacapture.restockingsample.R;

public class SeparatorDecorator extends RecyclerView.ItemDecoration {
    final private int headerSeparatorHeight;
    final private int headerSeparatorColor;
    final private int itemSeparatorHeight;
    final private int itemSeparatorColor;
    @NonNull
    final private Rect bounds = new Rect();
    @NonNull
    final private Paint paint;

    public SeparatorDecorator(Context context) {
        headerSeparatorHeight = dpToPx(context, 2);
        itemSeparatorHeight = dpToPx(context, 4);
        headerSeparatorColor = context.getColor(R.color.grey_slim_separator);
        itemSeparatorColor = context.getColor(R.color.grey_separator);
        paint = new Paint();
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        //noinspection rawtypes
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) return;

        int adapterPosition = parent.getChildAdapterPosition(view);
        int viewType = adapter.getItemViewType(adapterPosition);

        if (viewType == ProductListAdapter.TYPE_HEADER) {
            outRect.bottom = headerSeparatorHeight;
        } else if (viewType == ProductListAdapter.TYPE_ITEM) {
            outRect.bottom = itemSeparatorHeight;
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        //noinspection rawtypes
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) return;

        for (int index = 0; index < parent.getChildCount(); index++) {
            View child = parent.getChildAt(index);
            int adapterPosition = parent.getChildAdapterPosition(child);
            int viewType = adapter.getItemViewType(adapterPosition);

            int separatorHeight, color;
            if (viewType == ProductListAdapter.TYPE_HEADER) {
                separatorHeight = headerSeparatorHeight;
                color = headerSeparatorColor;
            } else {
                separatorHeight = itemSeparatorHeight;
                color = itemSeparatorColor;
            }

            parent.getDecoratedBoundsWithMargins(child, bounds);
            bounds.left = 0;
            bounds.right = parent.getWidth();
            bounds.bottom = bounds.bottom + Math.round(child.getTranslationY());
            bounds.top = bounds.bottom - separatorHeight;
            drawSeparator(c, bounds, color);
        }
    }

    private void drawSeparator(@NonNull Canvas canvas, Rect bounds, int color) {
        paint.setColor(color);
        canvas.drawRect(bounds, paint);
    }

    private static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}

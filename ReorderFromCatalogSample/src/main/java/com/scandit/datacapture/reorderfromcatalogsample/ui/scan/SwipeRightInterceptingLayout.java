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

package com.scandit.datacapture.reorderfromcatalogsample.ui.scan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Layout which contains the dataCaptureView, forwarding touch events to it, but also handling
 * swipe and fling right gestures.
 */
public class SwipeRightInterceptingLayout extends FrameLayout {

    private final GestureDetector gestureDetector;
    private OnSwipeRightListener listener = null;

    public SwipeRightInterceptingLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeRightInterceptingLayout(
            @NonNull Context context, @Nullable AttributeSet attrs
    ) {
        this(context, attrs, 0);
    }

    public SwipeRightInterceptingLayout(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr
    ) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SwipeRightInterceptingLayout(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        gestureDetector = new GestureDetector(
                getContext(),
                new GestureListener(getContext().getResources().getDisplayMetrics().density)
        );
    }

    public void setSwipeRightListener(OnSwipeRightListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev) || super.onInterceptTouchEvent(ev);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int FLING_THRESHOLD = 80;
        private static final int SWIPE_THRESHOLD = 160;

        private final int adjustedFlingThreshold;
        private final int adjustedSwipeThreshold;

        private GestureListener(float density) {
            adjustedFlingThreshold = Math.round(FLING_THRESHOLD * density);
            adjustedSwipeThreshold = Math.round(SWIPE_THRESHOLD * density);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return handleGesture(e1, e2, adjustedSwipeThreshold);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return handleGesture(e1, e2, adjustedFlingThreshold);
        }

        private boolean handleGesture(MotionEvent e1, MotionEvent e2, int threshold) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY) && diffX > threshold) {
                listener.onSwipeRight();
                return true;
            }
            return false;
        }
    }

    interface OnSwipeRightListener {
        void onSwipeRight();
    }
}

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

package com.scandit.datacapture.matrixscanbubblessample.scan.bubble;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import com.scandit.datacapture.matrixscanbubblessample.R;
import com.scandit.datacapture.matrixscanbubblessample.scan.bubble.data.BubbleData;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Bubble {

    public final View root;
    private final View containerShelfData;
    private final TextView textCode;

    private Transition transition = new Fade();
    private boolean showShelfData = true;

    public Bubble(Context context, BubbleData data, String code) {
        root = View.inflate(context, R.layout.bubble, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        containerShelfData = root.findViewById(R.id.shelf_container);
        textCode = root.findViewById(R.id.text_code);
        TextView textShelfData = root.findViewById(R.id.text_shelf_data);

        transition.setDuration(100);

        // We want to show the scanned code when tapping on the bubble. To do so, it's enough to
        // just add a regular View.OnClickListener on the view.
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleText();
            }
        });

        textShelfData.setText(
                context.getString(R.string.shelf_text, data.shelfCount, data.backroomCount)
        );
        textCode.setText(code);

        showCurrentData();
    }

    private void startTransition() {
        TransitionManager.beginDelayedTransition((ViewGroup) root, transition);
    }

    private void toggleText() {
        showShelfData = !showShelfData;
        showCurrentData();
    }

    private void showCurrentData() {
        if (showShelfData) {
            showShelfDataInternal();
        } else {
            showCodeInternal();
        }
    }

    public void show() {
        startTransition();
        root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        startTransition();
        root.setVisibility(View.GONE);
    }

    private void showShelfDataInternal() {
        containerShelfData.setVisibility(View.VISIBLE);
        textCode.setVisibility(View.GONE);
    }

    private void showCodeInternal() {
        containerShelfData.setVisibility(View.GONE);
        textCode.setVisibility(View.VISIBLE);
    }
}

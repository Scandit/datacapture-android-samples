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

package com.scandit.datacapture.inventoryauditsample.scan.bubble

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import androidx.core.content.ContextCompat.getDrawable
import com.scandit.datacapture.inventoryauditsample.R
import com.scandit.datacapture.inventoryauditsample.scan.bubble.data.BubbleColor.RED
import com.scandit.datacapture.inventoryauditsample.scan.bubble.data.BubbleColor.WHITE
import com.scandit.datacapture.inventoryauditsample.scan.bubble.data.BubbleData
import com.scandit.datacapture.inventoryauditsample.scan.bubble.data.BubbleDataProvider

class Bubble(context: Context, data: BubbleData, private val code: String) : TextWatcher {
    private val backgrounds = mapOf(
            RED to getDrawable(context, R.drawable.bubble_background_red),
            WHITE to getDrawable(context, R.drawable.bubble_background_white)
    )

    val root: View = (View.inflate(context, R.layout.bubble, null) as EditText).apply {
        layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        background = backgrounds.getValue(data.color)
        setText(data.count.toString())
        addTextChangedListener(this@Bubble)
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isNotBlank()) {
            root.background = backgrounds.getValue(WHITE)
            BubbleDataProvider.saveDataForBarcode(code, s.toString().toInt())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not interested in this callback.
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not interested in this callback.
    }
}
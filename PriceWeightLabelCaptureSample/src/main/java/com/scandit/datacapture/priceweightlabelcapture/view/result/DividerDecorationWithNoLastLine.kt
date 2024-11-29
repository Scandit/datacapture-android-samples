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
package com.scandit.datacapture.priceweightlabelcapture.view.result

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scandit.datacapture.priceweightlabelcapture.R

class DividerDecorationWithNoLastLine(context: Context) : RecyclerView.ItemDecoration() {
    private val divider: Drawable = lineDrawable(context)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        val childCount = parent.childCount
        // We skip the last children, to make sure we don't draw a line after it.
        for (i in 0 until childCount - 1) {
            val child: View = parent.getChildAt(i)
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            val dividerTop: Int = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + (divider.intrinsicHeight)
            divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider.draw(c)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun lineDrawable(context: Context): Drawable {
        return context.resources.getDrawable(R.drawable.divider, null)
    }
}

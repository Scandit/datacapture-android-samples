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
package com.scandit.datacapture.pricecapturesample.scan

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.scandit.datacapture.pricecapturesample.data.ValidationState
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * The status marker drawn above the price-field highlight: a white, drop-shadowed circle with a
 * downward tail (so it points at the label), a state-colored inner disc, and the state's glyph
 * centered on it.
 */
class StatusPinView(context: Context, state: ValidationState) : View(context) {

    private val density = resources.displayMetrics.density

    private val circleRadius = CIRCLE_RADIUS_DP.px
    private val ringWidth = RING_WIDTH_DP.px
    private val tailHalfWidth = TAIL_HALF_WIDTH_DP.px
    private val tailHeight = TAIL_HEIGHT_DP.px
    private val shadowPad = SHADOW_PAD_DP.px

    private val widthPx = (circleRadius * 2 + shadowPad * 2).toInt()
    private val heightPx = (shadowPad + circleRadius * 2 + tailHeight + shadowPad).toInt()

    private val centerX = widthPx / 2f
    private val centerY = shadowPad + circleRadius

    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        setShadowLayer(3f.px, 0f, 2f.px, 0x40000000)
    }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, state.colorRes)
    }
    private val pinPath = Path().apply {
        // The tail attaches where its base meets the circle, so the two base points lie exactly on
        // the circle. Build one convex closed outline: arc along the top of the circle from the left
        // base point all the way round to the right one, then two straight edges down to the tip and
        // back. Using an arc (not a full circle plus an overlapping triangle) avoids a
        // self-intersecting path and any seam.
        val baseAngle = Math.toDegrees(
            atan2(sqrt(circleRadius * circleRadius - tailHalfWidth * tailHalfWidth), tailHalfWidth)
                .toDouble()
        ).toFloat()
        val oval = RectF(
            centerX - circleRadius,
            centerY - circleRadius,
            centerX + circleRadius,
            centerY + circleRadius,
        )
        arcTo(oval, 180f - baseAngle, 180f + 2f * baseAngle, true)
        lineTo(centerX, centerY + circleRadius + tailHeight)
        close()
    }

    private val glyph = AppCompatResources.getDrawable(context, state.iconRes)?.mutate()?.also {
        DrawableCompat.setTint(it, ContextCompat.getColor(context, state.glyphTintRes))
        val half = ((circleRadius - ringWidth) * 0.85f).toInt()
        it.setBounds(
            (centerX - half).toInt(),
            (centerY - half).toInt(),
            (centerX + half).toInt(),
            (centerY + half).toInt(),
        )
    }

    init {
        // setShadowLayer needs software rendering.
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthPx, heightPx)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(pinPath, whitePaint)
        canvas.drawCircle(centerX, centerY, circleRadius - ringWidth, fillPaint)
        glyph?.draw(canvas)
    }

    private val Float.px: Float get() = this * density

    companion object {
        private const val CIRCLE_RADIUS_DP = 16f
        private const val RING_WIDTH_DP = 4f
        private const val TAIL_HALF_WIDTH_DP = 7f
        private const val TAIL_HEIGHT_DP = 6f
        private const val SHADOW_PAD_DP = 6f

        /**
         * Distance, in dp, from the view's center to the tail tip. Callers use this as the upward
         * anchor offset so the tip lands exactly on the highlight's top edge.
         */
        const val TIP_BELOW_CENTER_DP = CIRCLE_RADIUS_DP + TAIL_HEIGHT_DP / 2f
    }
}

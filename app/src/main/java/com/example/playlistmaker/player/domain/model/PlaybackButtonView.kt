package com.example.playlistmaker.player.domain.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0

) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val imagePlayBitmap: Bitmap?
    private val imagePauseBitmap: Bitmap?
    private var imageRect = RectF(0f, 0f, 0f, 0f)

    private var isPlay = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr, defStyleRes
        ).apply {
            try {
                imagePlayBitmap = getDrawable(R.styleable.PlaybackButtonView_imagePlay)?.toBitmap()
                imagePauseBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_imagePause)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (!isPlay) {
            imagePlayBitmap?.let {
                canvas.drawBitmap(imagePlayBitmap, null, imageRect, null)
            }
        } else {
            imagePauseBitmap?.let {
                canvas.drawBitmap(imagePauseBitmap, null, imageRect, null)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return false
            }

            MotionEvent.ACTION_UP -> {
                toggleIsPlaying()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun toggleIsPlaying() {
        isPlay = !isPlay
        invalidate()
    }


}
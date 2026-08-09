package com.aida.assistant

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Canvas
import android.view.View

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(8, 2, 18)
        window.navigationBarColor = Color.BLACK

        setContentView(AidaView())
    }

    inner class AidaView : View(this@MainActivity) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun onDraw(canvas: Canvas) {

            canvas.drawColor(
                Color.rgb(10, 2, 20)
            )

            val cx = width / 2f
            val cy = height / 2f

            paint.color =
                Color.rgb(190, 50, 255)

            paint.setShadowLayer(
                40f,
                0f,
                0f,
                Color.MAGENTA
            )

            canvas.drawCircle(
                cx,
                cy,
                100f,
                paint
            )

            paint.clearShadowLayer()

            paint.color = Color.WHITE
            paint.textSize = 32f
            paint.textAlign = Paint.Align.CENTER

            canvas.drawText(
                "AIDA",
                cx,
                cy + 160f,
                paint
            )

            paint.textSize = 16f

            canvas.drawText(
                "Sistema online",
                cx,
                cy + 190f,
                paint
            )
        }
    }
}

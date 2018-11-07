package com.kamelong.aodia.databaseTimeTable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 */

/**
 * 独自Viewの基本形。
 * とは言いつつ独自Viewの作り方は最近では変わってきているので、
 * 時刻表とダイヤグラム関係しか使われていない。
 * 利点はこのViewを継承したViewは一括で文字サイズを変更できること。
 * @author kamelong
 */
open class KLView
/**
 * デフォルトコンストラクタ
 */
(context: Context) : View(context) {

    /**
     * 横幅をここで指定する
     * @return
     */
    open val xSize: Int
        get() = (textPaint.textSize * 2.5 + 2).toInt()
    /**
     * 縦幅をここで指定する
     * @return
     */
    open val ySize: Int
        get() = textPaint.textSize.toInt()

    var layoutTop:Int=0

    /**
     * onMeasureは結構いじっている。
     * このViewは常に縦横のサイズは固定して使いたいため
     * getXsize,getYsizeを別途作成し、その値をもとにサイズを決定する。
     * @see View.measure
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layoutTop=heightMeasureSpec
        this.setMeasuredDimension(xSize, ySize)
    }

    companion object {
        var textPaint: Paint//時刻表など普通の文字列用　色を変えてもよい
        var grayPaint: Paint//灰色の線をひくためのペイント
        var blackPaint: Paint//駅名などの黒色指定部分　細い枠線に用いる
        var blackBPaint: Paint//駅名などの黒色指定部分　細い枠線に用いる
        var blackBBPaint: Paint//太い枠線部分に用いる
        var blackBig: Paint//主要駅（２段使う）駅名の既出に使う
        var textSize: Int = 0
            set(value) {
                field=value
                textPaint.textSize = value.toFloat()

                blackPaint.textSize = value.toFloat()
                grayPaint.textSize = value.toFloat()

                blackBig.textSize = (value* 1.2).toInt().toFloat()
                textPaint.strokeWidth = value / 12.0f
                blackPaint.strokeWidth = value / 20.0f
                blackBPaint.strokeWidth = value / 12.0f
                blackBBPaint.strokeWidth = value / 6.0f
            }


        /**
         * staticなコンストラクタ。
         * 最初にKLViewが呼ばれたときに実行されるはず
         * 各種Paintオブジェクトを初期化する
         */
        init {
            blackBig = Paint()
            blackBPaint = Paint()
            blackBBPaint = Paint()
            blackPaint = Paint()
            textPaint = Paint()
            grayPaint = Paint()

            blackPaint.color = Color.BLACK
            grayPaint.color = Color.GRAY

            textPaint.isAntiAlias = true
            blackBig.isAntiAlias = true
            blackPaint.isAntiAlias = true
            blackBPaint.isAntiAlias = true
            blackBBPaint.isAntiAlias = true
            grayPaint.isAntiAlias = true
            textSize=30

        }

    }
    fun drawTextCenter(canvas: Canvas, text: String, y: Float, paint: Paint) {
        canvas.drawText(text, (this.width.toFloat() - 2f - paint.measureText(text)) / 2, y, paint)
    }
    fun drawText(canvas: Canvas, text: String, y: Float, paint: Paint) {
        canvas.drawText(text, 2f, y, paint)
    }


}
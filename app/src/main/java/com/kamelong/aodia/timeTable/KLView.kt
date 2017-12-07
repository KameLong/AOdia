package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/**
 * 独自Viewの基本形。
 * とは言いつつ独自Viewの作り方は最近では変わってきているので、
 * 時刻表とダイヤグラム関係しか使われていない。
 * 利点はこのViewを継承したViewは一括で文字サイズを変更できること。
 * @author kamelong
 */
abstract class KLView
/**
 * デフォルトコンストラクタ
 */
(context: Context) : View(context) {

    /**
     * 横幅をここで指定する
     * @return
     */
    abstract val xsize: Int
    /**
     * 縦幅をここで指定する
     * @return
     */
    abstract val ysize: Int

    /**
     * onMeasureは結構いじっている。
     * このViewは常に縦横のサイズは固定して使いたいため
     * getXsize,getYsizeを別途作成し、その値をもとにサイズを決定する。
     * @see android.view.View.measure
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        this.setMeasuredDimension(xsize, ysize)
    }

    /**
     * 文字列を中央に書くメソッド
     */
    fun drawTextCenter(canvas:Canvas,text:String,y:Float,paint:Paint){
        canvas.drawText(text, (xsize.toFloat() - 2f - paint.measureText(text)) / 2, y, paint)

    }

    companion object {
        var textPaint: Paint//時刻表など普通の文字列用　色を変えてもよい
        var grayPaint: Paint//灰色の線をひくためのペイント
        var blackPaint: Paint//単純駅名＆細い枠線に用いる
        var blackBPaint: Paint//主要駅名＆中枠線に用いる
        var blackBBPaint: Paint//太い枠線部分に用いる
        /**
         * 文字サイズを変更する。
         * staticな関数なので、ここで変更したものはKLViewを継承したすべてのViewで適用される。
         * @param size
         */

        var textSize:Int=30
        set(value) {
            textPaint.textSize = value.toFloat()

            blackPaint.textSize = value.toFloat()
            blackBPaint.textSize = (value * 1.2).toInt().toFloat()
            grayPaint.textSize = value.toFloat()

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
            textPaint = Paint()
            grayPaint = Paint()
            blackPaint = Paint()
            blackBPaint = Paint()
            blackBBPaint = Paint()

            grayPaint.color = Color.GRAY

            textPaint.isAntiAlias = true
            blackPaint.isAntiAlias = true
            blackBPaint.isAntiAlias = true
            blackBBPaint.isAntiAlias = true
            grayPaint.isAntiAlias = true
            textSize=30
        }

    }
}
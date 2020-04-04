package com.kamelong.aodia.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;



/**
 * 単純なテキストを表示するだけのモノ
 */
public class LabelView extends View {
    private Paint textPaint;   // 文字を書く色
    private String text = "";  // 文字列
    private int textSize=30;  // テキストサイズ
    private int textColor = 0xff000000;    // テキストカラー

    private int height; // 1行の高さ


    /**
     * コンストラクタ
     */
    public LabelView(Context context) {
        super(context);
        this.initLabelView();
    }
    public void setHeight(int height) {
        this.height = height;
    }
    /**
     * ラベルビューの初期化
     */
    private final void initLabelView() {
        // 色を変えたりするためのペイントを用意
        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        // スクリーンに合わせたテキストサイズに変更する
        this.textPaint.setTextSize(textSize * getResources().getDisplayMetrics().density);
        this.textPaint.setColor(textColor);
    }


    /**
     * 表示するテキスト
     * @param text 表示したいテキスト
     */
    public void setText(String text) {
        // テキストに変更がある場合だけ修正する
        if (!this.text.equals(text)) {
            this.text = text;
            this.invalidate();
        }
    }

    /**
     * ラベルのテキストサイズを変える
     * @param size テキストサイズ
     */
    public void setTextSize(int size) {
        // サイズに変更があった時だけ変更する
        if (this.textSize != size) {
            this.textSize = size;
            this.textPaint.setTextSize(size* getResources().getDisplayMetrics().density);
            this.invalidate();
        }
    }

    /**
     * テキストカラーを変更する
     * @param color 色
     */
    public void setColor(int color) {

        // 変更があったときだけ変える
        if (this.textColor != color) {
            this.textColor = color;
            this.textPaint.setColor(color);
            this.invalidate();
        }
    }

    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension( this.measureWidth(widthMeasureSpec), (int)(height* getResources().getDisplayMetrics().density));
    }

    /**
     * ビューの幅を求める
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // 大きさが指定されてる？
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {

            // テキストの長さを測る
            result = (int) textPaint.measureText(text) + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }



    /**
     * テキストを表示する
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // テキストが無いときは何もせんよ
        if (this.text == null) {
            return ;
        }
        // 文字を書き始める最初の位置
        int baseTop = (int)((height+ textSize *0.8)* getResources().getDisplayMetrics().density/2);
        canvas.drawText(this.text, getPaddingLeft(), baseTop, textPaint);
    }
    /**
     * テキスト表示用のペイントを取り出す
     * @return ペイント
     */
    public Paint getPaint() {
        return this.textPaint;
    }

}
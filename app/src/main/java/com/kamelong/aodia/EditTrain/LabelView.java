package com.kamelong.aodia.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;



/**
 * EditViewはなんか重いから単純なテキストを表示するだけのモノを作るよ
 * @author a5
 *
 */
public class LabelView extends View {

    private Paint mTextPaint;   // 文字を書く色
    private String mText = "";  // 文字列
    private int mTextSize;  // テキストサイズ
    private int mTextColor = 0xff000000;    // テキストカラー

    private int height;    // 1行の高さ


    /**
     * コンストラクタ
     * @param context
     */
    public LabelView(Context context) {
        super(context);

        // ラベルビューを初期化するよ
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
        this.mTextPaint = new Paint();
        this.mTextPaint.setAntiAlias(false);

        // スクリーンに合わせたテキストサイズに変更する
        this.mTextPaint.setTextSize(30 * getResources().getDisplayMetrics().density);
        this.mTextPaint.setColor(0xff000000);
    }


    /**
     * 表示するテキスト
     * @param text 表示したいテキスト
     */
    public void setText(String text) {
        // テキストに変更がある場合だけ修正する
        if (!this.mText.equals(text)) {
            this.mText = text;
            this.invalidate();
        }
    }

    /**
     * ラベルのテキストサイズを変える
     * @param size テキストサイズ
     */
    public void setTextSize(int size) {
        // サイズに変更があった時だけ変更する
        if (this.mTextSize != size) {
            this.mTextSize = size;
            this.mTextPaint.setTextSize(size* getResources().getDisplayMetrics().density);
            this.invalidate();
        }
    }

    /**
     * テキストカラーを変更する
     * @param color 色
     */
    public void setColor(int color) {

        // 変更があったときだけ変える
        if (this.mTextColor != color) {
            this.mTextColor = color;
            this.mTextPaint.setColor(color);
            this.invalidate();
        }
    }

    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // ビューの幅を求める

        // ビューの高さを求める

        // ビューのサイズをセットする
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
            result = (int) mTextPaint.measureText(mText) + getPaddingLeft() + getPaddingRight();
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
        if (this.mText == null) {
            return ;
        }
        // 文字を書き始める最初の位置
        int baseTop = (int)((height+mTextSize*0.8)* getResources().getDisplayMetrics().density/2);

        // 1行に収まる
            // テキストをそのまま書く
            canvas.drawText(this.mText, getPaddingLeft(), baseTop, mTextPaint);

    }


    /**
     * テキスト表示用のペイントを取り出す
     * @return ペイント
     */
    public Paint getPaint() {
        return this.mTextPaint;
    }

}
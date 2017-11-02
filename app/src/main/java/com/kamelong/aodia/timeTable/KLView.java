package com.kamelong.aodia.timeTable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
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
 *
 */
public class KLView extends View {
    protected static Paint textPaint;//時刻表など普通の文字列用　色を変えてもよい
    private static Paint grayPaint;//灰色の線をひくためのペイント
    static Paint blackPaint;//駅名などの黒色指定部分　細い枠線に用いる
    static Paint blackBPaint;//駅名などの黒色指定部分　細い枠線に用いる
    static Paint blackBBPaint;//太い枠線部分に用いる
    static Paint blackBig;//主要駅（２段使う）駅名の既出に使う
    static int textSize;

    /**
     * staticなコンストラクタ。
     * 最初にKLViewが呼ばれたときに実行されるはず
     * 各種Paintオブジェクトを初期化する
     */
    static {
        blackBig = new Paint();
        blackBPaint=new Paint();
        blackBBPaint = new Paint();
        blackPaint = new Paint();
        textPaint = new Paint();
        grayPaint=new Paint();

        blackPaint.setColor(Color.BLACK);
        grayPaint.setColor(Color.GRAY);

        textPaint.setAntiAlias(true);
        blackBig.setAntiAlias(true);
        blackPaint.setAntiAlias(true);
        blackBPaint.setAntiAlias(true);
        blackBBPaint.setAntiAlias(true);
        grayPaint.setAntiAlias(true);
        setTextSize(30);

    }
    /**
     デフォルトコンストラクタ
     */
    public KLView(Context context) {
        super(context);
    }

    /**
     * 文字サイズを変更する。
     * staticな関数なので、ここで変更したものはKLViewを継承したすべてのViewで適用される。
     * @param size
     */
    public static void setTextSize(int size) {
        textSize=size;
        textPaint.setTextSize(size);
        blackPaint.setTextSize(size);
        grayPaint.setTextSize(size);
        blackBig.setTextSize((int) (size * 1.2));
        blackPaint.setStrokeWidth(size / 20.0f);
        blackBPaint.setStrokeWidth(size / 12.0f);
        blackBBPaint.setStrokeWidth(size / 6.0f);
    }
    /**
     * onMeasureは結構いじっている。
     * このViewは常に縦横のサイズは固定して使いたいため
     * getXsize,getYsizeを別途作成し、その値をもとにサイズを決定する。
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(getXsize(), getYsize());
    }

    /**
     * 横幅をここで指定する
     * @return
     */
    protected int getXsize() {
        return (int) (textPaint.getTextSize() * 2.5 + 2);
    }
    /**
     * 縦幅をここで指定する
     * @return
     */
    protected int getYsize() {
        return (int) textPaint.getTextSize() *10;
    }
}
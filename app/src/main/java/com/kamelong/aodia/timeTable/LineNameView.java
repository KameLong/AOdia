package com.kamelong.aodia.timeTable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;

import com.kamelong.aodia.diadata.AOdiaDiaFile;

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
 * 時刻表画面の左上に路線名とダイヤ名を表示する部分がある。
 * そのためのView
 * @author kamelong
 */
class LineNameView extends KLView {
    private AOdiaDiaFile dia;
    private int diaNum;

    public LineNameView(Context context) {
        super(context);
    }

    /**
     * デフォルトコンストラクタ
     * @param context　このViewを表示するactivity
     * @param diaFile　このViewに表示するダイヤファイル
     * @param num　このViewに表示するダイヤのインデックス
     */
    public LineNameView(Context context, AOdiaDiaFile diaFile, int num){
        this(context);
        dia=diaFile;
        diaNum=num;
    }

    /**
     * onDraw.
     * 路線名とダイヤ名を表示してから、
     * 設定画面の列車名の表示のフラグを確認し、"列車名"の表示を行う
     * @param canvas
     */
    public void onDraw(Canvas canvas){
        int startLine=(int)textPaint.getTextSize();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSkewX(0);
        textPaint.setTypeface(Typeface.DEFAULT);
        startLine=startLine+(int)textPaint.getTextSize();
        canvas.drawText(dia.getDiaName(diaNum), 5,startLine, textPaint);
    }

    /**
     * 列車名の表示を行う際は縦サイズを大きくする
     * @return　縦サイズ
     */
    protected int getYsize(){
            return (int)(textPaint.getTextSize()*2.2f);
    }

    /**
     * 駅名欄と同じく横は５文字分のサイズ
     * @return　横サイズ
     */
    protected int getXsize(){
        return (int)(textPaint.getTextSize()*5);
    }
}

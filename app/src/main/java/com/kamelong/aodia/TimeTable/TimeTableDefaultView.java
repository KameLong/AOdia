package com.kamelong.aodia.TimeTable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.kamelong.aodia.MainActivity;

public abstract class TimeTableDefaultView extends View {
    protected MainActivity activity;
    protected TimeTableOptions options;

    public static Paint textPaint;//時刻表など普通の文字列用　色を変えてもよい
    public static Paint grayPaint;//灰色の線をひくためのペイント
    public static Paint blackPaint;//駅名などの黒色指定部分　細い枠線に用いる
    public static Paint blackBPaint;//太い枠線部分に用いる
    public static Paint blackBig;//主要駅（２段使う）駅名の既出に使う
    public static int textSize;
    public static int stationWidth=5;

    public static int smallSpace;//細線を入れたときのスペース
    public static int normalSpace;//太線を入れたときのスペース

    /*
     * staticなコンストラクタ。
     * 最初にKLViewが呼ばれたときに実行されるはず
     * 各種Paintオブジェクトを初期化する
     */
    static {
        blackBig = new Paint();
        blackBPaint = new Paint();
        blackPaint = new Paint();
        textPaint = new Paint();
        grayPaint=new Paint();

        blackPaint.setColor(Color.BLACK);
        grayPaint.setColor(Color.GRAY);

        textPaint.setAntiAlias(true);
        blackBig.setAntiAlias(true);
        blackPaint.setAntiAlias(true);
        blackBPaint.setAntiAlias(true);
        grayPaint.setAntiAlias(true);
        setTextSize(30);

    }
    /**
     デフォルトコンストラクタ
     */
    public TimeTableDefaultView(Context context,TimeTableOptions options) {
        super(context);
        activity=(MainActivity)context;
        this.options=options;
    }

    /**
     * 文字サイズを変更する。
     * staticな関数なので、ここで変更したものはKLViewを継承したすべてのViewで適用される。
     */
    public static void setTextSize(int size) {
        textSize=size;
        textPaint.setTextSize(size);
        blackPaint.setTextSize(size);
        grayPaint.setTextSize(size);
        blackBig.setTextSize((int) (size * 1.2));
        blackPaint.setStrokeWidth(size / 20f);
        blackBPaint.setStrokeWidth(size / 6f);
        smallSpace=size/6;
        normalSpace=size/3;
    }
    public static void setStationWidth(int width){
        stationWidth=width;
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
     */
    abstract int getXsize();
    /**
     * 縦幅をここで指定する
     */
    abstract int getYsize();

    boolean charIsEng(char c){
        return c<256;
    }

}

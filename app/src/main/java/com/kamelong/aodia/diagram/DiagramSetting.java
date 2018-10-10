package com.kamelong.aodia.Diagram;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;

import java.util.ArrayList;

public class DiagramSetting {
    /**
     * スケールは秒あたりのピクセル数
     */
    public float scaleX = 1;
    public float scaleY = 1;
    /**
     * スクロールはピクセル単位
     */
    public float scrollX = 0;
    public float scrollY = 0;

    /**
     * 時刻刻み
     * 0:1時間
     * 1:30分
     * 2:20分
     * 3:15分
     * 4:10分
     * 5:5分
     * 6:2分
     * 7:1分
     */
    public int verticalAxis = 3;
    /**
     * 非表示：0
     * 現時刻縦線のみ表示：1
     * オートスクロール：2
     */
    public int autoScrollState = 0;
    /**
     * nameFragとnumberFragの切り替え状態を表す
     * 0：両方とも非表示
     * 1：列車番号のみ表示
     * 2：列車名のみ表示
     * 3：両方とも表示
     */
    public int numberState = 0;
    public boolean showTrainStop = false;
    public boolean showOperationLine = false;
    public boolean showOperationName = false;

    public boolean showUpTrain = true;
    public boolean showDownTrain = true;
    AOdiaActivity activity;
    DiagramFragment fragment;
    DiaFile diaFile;

    private static final int DURATION = 200;
    private boolean fabDiagramVisible;//ダイヤ詳細徹底を開いているか

    public DiagramSetting(AOdiaActivity activity, DiagramFragment fragment, DiaFile diaFile, int diaNumber) {
        this.activity = activity;
        this.fragment = fragment;
        this.diaFile = diaFile;
        //デフォルトscaleはTrainNumに依存する
        if (diaFile.getTrainSize(diaNumber, 0) > 100) {
            scaleX = 0.1f;
            scaleY = 0.3f;
        } else {
            scaleX = 0.05f;
            scaleY = 0.3f;
        }
    }
    public void setDefault(int[] value){
        scrollX=value[0];
        scrollY=value[1];
        scaleX=value[2]/100.0f;
        scaleY=value[3]/100.0f;
        if(scaleX<0.01){
            scaleX=0.01f;
        }
        if(scaleY<0.05){
            scaleY=0.05f;
        }
    }

    /**
     * FloatingActionButtonのチェック状態を変える
     * checkedの場合は背景色をandroid.R.color.holo_blue_lightにし
     * uncheckedの場合は背景色をandroid.R.color.darker_grayにする
     *
     * @param fab
     * @param check
     */
    private void checkFab(FloatingActionButton fab, boolean check) {
        if (check) {
            fab.getBackground().setColorFilter(ContextCompat.getColor(fragment.getAOdiaActivity(), android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        } else {
            fab.getBackground().setColorFilter(ContextCompat.getColor(fragment.getAOdiaActivity(), android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * それぞれのボタンの初期設定を行う
     */
    private void buttonInit() {
        FloatingActionButton fabAuto = (FloatingActionButton) fragment.findViewById(R.id.autoScroll);

        switch (autoScrollState) {
            case 0:
                fabAuto.setImageResource(R.drawable.auto_scroll);
                checkFab(fabAuto, false);
                break;
            case 1:
                checkFab(fabAuto, true);
                break;
            case 2:
                fabAuto.setImageResource(R.drawable.pause);
                checkFab(fabAuto, true);
                break;
        }
        checkFab((FloatingActionButton) fragment.findViewById(R.id.fabDown), showDownTrain);
        checkFab((FloatingActionButton) fragment.findViewById(R.id.fabUp), showUpTrain);
        checkFab((FloatingActionButton) fragment.findViewById(R.id.fabStop), showTrainStop);

        FloatingActionButton fabNumber = (FloatingActionButton) fragment.findViewById(R.id.fabNumber);
        TextView textName = (TextView) fragment.findViewById(R.id.textname);
        TextView textNumber = (TextView) fragment.findViewById(R.id.textNumber);
        if (numberState % 2 == 0) {
            textNumber.setVisibility(View.INVISIBLE);
        } else {
            textNumber.setVisibility(View.VISIBLE);
        }
        if (numberState / 2 == 0) {
            textName.setVisibility(View.INVISIBLE);
        } else {
            textName.setVisibility(View.VISIBLE);
        }
        if (numberState == 0) {
            checkFab(fabNumber, false);
        } else {
            checkFab(fabNumber, true);
        }


    }

    /**
     * 移動アニメーションを行う
     * animatorのリストを与えることで、そこに新しくアニメーションンを追加する
     * X、Y方向への平行移動、透明度の変更を行う
     *
     * @param animators
     * @param view
     * @param startTranslationX
     * @param endTranslationX
     * @param startTranslationY
     * @param endTranslationY
     * @param startAlpfa
     * @param endAlpfa
     */
    private void addAnimation(ArrayList<Animator> animators, View view, int startTranslationX, int endTranslationX,
                              int startTranslationY, int endTranslationY, float startAlpfa, float endAlpfa) {
        animators.add(ObjectAnimator.ofPropertyValuesHolder(view
                , PropertyValuesHolder.ofFloat("translationX", startTranslationX, endTranslationX)
                , PropertyValuesHolder.ofFloat("translationY", startTranslationY, endTranslationY))
                .setDuration(DURATION));
        animators.add(ObjectAnimator.ofPropertyValuesHolder(view
                , PropertyValuesHolder.ofFloat("alpha", startAlpfa, endAlpfa)));

    }

    /**
     * DiagramFragmentが与えられた時にダイヤ詳細設定ボタンについて
     *
     * @param f
     */
    public void create(DiagramFragment f) {
        fragment = f;
        fabDiagramVisible = false;
        fragment.findViewById(R.id.fabFinely).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabRough).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabFit).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabNumber).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabDown).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabStop).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabUp).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.autoScroll).setVisibility(View.VISIBLE);

        FloatingActionButton fabDiagram = (FloatingActionButton) fragment.findViewById(R.id.fabDiagram);
        fabDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabDiagramVisible) {
                    int fabSize = fragment.findViewById(R.id.fitFrame).getWidth();

                    ArrayList<Animator> animatorList = new ArrayList<Animator>();
                    //アニメーションを追加していく
                    addAnimation(animatorList, fragment.findViewById(R.id.settingFrame), -fabSize, 0, -fabSize, 0, 1, 1);

                    addAnimation(animatorList, fragment.findViewById(R.id.upFrame), -2 * fabSize, 0, -2 * fabSize, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.stopFrame), -fabSize, 0, -2 * fabSize, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.downFrame), 0, 0, -2 * fabSize, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.FinelyFrame), -2 * fabSize, 0, -fabSize, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.roughFrame), -2 * fabSize, 0, 0, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.nameFrame), -fabSize, 0, 0, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.fitFrame), 0, 0, 0, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.autoFrame), 0, 0, -fabSize, 0, 1, 0);
                    final AnimatorSet animatorSet = new AnimatorSet();
                    // リストのAnimatorを順番に実行します
                    animatorSet.playTogether(animatorList);
                    // アニメーションを開始します
                    animatorSet.start();
                    fabDiagramVisible = false;
                } else {

                    fabDiagramVisible = true;
                    int fabSize = fragment.findViewById(R.id.fitFrame).getWidth();
                    ArrayList<Animator> animatorList = new ArrayList<Animator>();
                    //アニメーションを追加していく

                    addAnimation(animatorList, fragment.findViewById(R.id.settingFrame), 0, -fabSize, 0, -fabSize, 1, 1);

                    addAnimation(animatorList, fragment.findViewById(R.id.upFrame), 0, -2 * fabSize, 0, -2 * fabSize, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.stopFrame), 0, -fabSize, 0, -2 * fabSize, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.downFrame), 0, 0, 0, -2 * fabSize, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.FinelyFrame), 0, -2 * fabSize, 0, -fabSize, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.roughFrame), 0, -2 * fabSize, 0, 0, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.nameFrame), 0, -fabSize, 0, 0, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.fitFrame), 0, 0, 0, 0, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.autoFrame), 0, 0, 0, -fabSize, 0, 1);

                    final AnimatorSet animatorSet = new AnimatorSet();
                    // リストのAnimatorを順番に実行します
                    animatorSet.playTogether(animatorList);
                    // アニメーションを開始します
                    animatorSet.start();
                }
            }
        });


        //時間軸を細かくする
        FloatingActionButton fabFinely = (FloatingActionButton) fragment.findViewById(R.id.fabFinely);
        fabFinely.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(verticalAxis<7){
                    verticalAxis++;
                    //Viewの再描画
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                    ((FrameLayout)fragment.findViewById(R.id.time)).getChildAt(0).invalidate();
                }
            }
        });
        //時間軸を粗くする
        FloatingActionButton fabRough = (FloatingActionButton) fragment.findViewById(R.id.fabRough);
        fabRough.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(verticalAxis>0){
                    verticalAxis--;
                    //Viewの再描画
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                    ((FrameLayout)fragment.findViewById(R.id.time)).getChildAt(0).invalidate();
                }
            }
        });
        //下り時刻表の表示を変える
        FloatingActionButton fabDown = (FloatingActionButton) fragment.findViewById(R.id.fabDown);
        fabDown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showDownTrain=!showDownTrain;
                //Viewと詳細設定ボタンの再描画
                ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                DiagramSetting.this.buttonInit();

            }
        });
        //上り時刻表の表示を変える
        FloatingActionButton fabUp = (FloatingActionButton) fragment.findViewById(R.id.fabUp);
        fabUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showUpTrain=!showUpTrain;
                //Viewと詳細設定ボタンの再描画
                ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                DiagramSetting.this.buttonInit();
            }
        });
        //停車表示を変える
        FloatingActionButton fabStop = (FloatingActionButton) fragment.findViewById(R.id.fabStop);
        fabStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showTrainStop=!showTrainStop;
                //Viewと詳細設定ボタンの再描画
                ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                DiagramSetting.this.buttonInit();
            }
        });
        //ダイヤの上下をそろえる
        FloatingActionButton fabFit = (FloatingActionButton) fragment.findViewById(R.id.fabFit);
        fabFit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
//                fragment.fitVertical();
            }
        });
        //列車番号・列車名の表示を切り替える
        FloatingActionButton fabNumber = (FloatingActionButton) fragment.findViewById(R.id.fabNumber);
        fabNumber.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                numberState=(numberState+1)%4;
                DiagramSetting.this.buttonInit();
                ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
            }
        });
        buttonInit();

    }
    public int veriticalAxis(){
        return verticalAxis;
    }
}

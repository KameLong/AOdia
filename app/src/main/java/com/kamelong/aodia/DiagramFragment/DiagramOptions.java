package com.kamelong.aodia.DiagramFragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;

import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * ダイヤグラムで使用するオプション
 */
public class DiagramOptions {
    /**
     * スケールは秒あたりのピクセル数
     */
    public float scaleX = 1;
    public float scaleY = 1;
    /**
     * スクロールはピクセル単位
     */
    public int scrollX = 0;
    public int scrollY = 0;

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
    MainActivity activity;
    DiagramFragment fragment;
    LineFile diaFile;
    SharedPreferences diagramPreference;

    private static final int DURATION = 200;
    private boolean fabDiagramVisible;//ダイヤ詳細徹底を開いているか

    public DiagramOptions(MainActivity activity, DiagramFragment fragment, LineFile diaFile, int diaNumber) {
        this.activity = activity;
        this.fragment = fragment;
        this.diaFile = diaFile;
        diagramPreference= PreferenceManager.getDefaultSharedPreferences(activity);
        if(diaFile==null)return;
        //デフォルトscaleはTrainNumに依存する
        if (diaFile.getDiagram(diaNumber).getTrainNum(0) > 100) {
            scaleX = 0.1f;
            scaleY = 0.3f;
        } else {
            scaleX = 0.05f;
            scaleY = 0.3f;
        }
        verticalAxis=diagramPreference.getInt("diagramVerticalAxis",0);
        numberState=diagramPreference.getInt("diagramNumberState",0);
        showUpTrain=diagramPreference.getBoolean("diagramShowUp",true);
        showDownTrain=diagramPreference.getBoolean("diagramShowDown",true);
        showTrainStop=diagramPreference.getBoolean("diagramShowStop",false);
    }
    public void setDefault(int[] value){
        scrollX=value[0];
        scrollY=value[1];
        scaleX = value[2] / 1000f;
        scaleY = value[3] / 1000f;
        if(scaleX<0.01){
            scaleX=0.1f;
        }
        if(scaleY<0.05){
            scaleY=0.2f;
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
            fab.getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        } else {
            fab.getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * それぞれのボタンの初期設定を行う
     */
    private void buttonInit() {
        FloatingActionButton fabAuto = findViewById(R.id.autoScroll);

        switch (autoScrollState) {
            case 0:
                fabAuto.setImageResource(R.drawable.diagram_auto_scroll);
                checkFab(fabAuto, false);
                break;
            case 1:
                checkFab(fabAuto, true);
                break;
            case 2:
                fabAuto.setImageResource(R.drawable.diagram_pause);
                checkFab(fabAuto, true);
                break;
        }
        checkFab((FloatingActionButton) findViewById(R.id.fabDown), showDownTrain);
        checkFab((FloatingActionButton) findViewById(R.id.fabUp), showUpTrain);
        checkFab((FloatingActionButton) findViewById(R.id.fabStop), showTrainStop);

        FloatingActionButton fabNumber = findViewById(R.id.fabNumber);
        TextView textName = findViewById(R.id.textname);
        TextView textNumber = findViewById(R.id.stationIndex);
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
        findViewById(R.id.fabFinely).setVisibility(View.VISIBLE);
        findViewById(R.id.fabRough).setVisibility(View.VISIBLE);
        findViewById(R.id.fabFit).setVisibility(View.VISIBLE);
        findViewById(R.id.fabNumber).setVisibility(View.VISIBLE);
        findViewById(R.id.fabDown).setVisibility(View.VISIBLE);
        findViewById(R.id.fabStop).setVisibility(View.VISIBLE);
        findViewById(R.id.fabUp).setVisibility(View.VISIBLE);
        findViewById(R.id.autoScroll).setVisibility(View.VISIBLE);

        FloatingActionButton fabDiagram = findViewById(R.id.fabDiagram);
        fabDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabDiagramVisible) {
                    int fabSize = findViewById(R.id.fitFrame).getWidth();

                    ArrayList<Animator> animatorList = new ArrayList<Animator>();
                    //アニメーションを追加していく
                    addAnimation(animatorList, findViewById(R.id.settingFrame), -fabSize, 0, -fabSize, 0, 1, 1);

                    addAnimation(animatorList, findViewById(R.id.upFrame), -2 * fabSize, 0, -2 * fabSize, 0, 1, 0);
                    addAnimation(animatorList, findViewById(R.id.stopFrame), -fabSize, 0, -2 * fabSize, 0, 1, 0);
                    addAnimation(animatorList, findViewById(R.id.downFrame), 0, 0, -2 * fabSize, 0, 1, 0);
                    addAnimation(animatorList, findViewById(R.id.FinelyFrame), -2 * fabSize, 0, -fabSize, 0, 1, 0);
                    addAnimation(animatorList, findViewById(R.id.roughFrame), -2 * fabSize, 0, 0, 0, 1, 0);
                    addAnimation(animatorList, findViewById(R.id.nameFrame), -fabSize, 0, 0, 0, 1, 0);
                    addAnimation(animatorList, findViewById(R.id.fitFrame), 0, 0, 0, 0, 1, 0);
                    addAnimation(animatorList, findViewById(R.id.autoFrame), 0, 0, -fabSize, 0, 1, 0);
                    final AnimatorSet animatorSet = new AnimatorSet();
                    // リストのAnimatorを順番に実行します
                    animatorSet.playTogether(animatorList);
                    // アニメーションを開始します
                    animatorSet.start();
                    fabDiagramVisible = false;
                } else {

                    fabDiagramVisible = true;
                    int fabSize = findViewById(R.id.fitFrame).getWidth();
                    ArrayList<Animator> animatorList = new ArrayList<Animator>();
                    //アニメーションを追加していく

                    addAnimation(animatorList, findViewById(R.id.settingFrame), 0, -fabSize, 0, -fabSize, 1, 1);

                    addAnimation(animatorList, findViewById(R.id.upFrame), 0, -2 * fabSize, 0, -2 * fabSize, 0, 1);
                    addAnimation(animatorList, findViewById(R.id.stopFrame), 0, -fabSize, 0, -2 * fabSize, 0, 1);
                    addAnimation(animatorList, findViewById(R.id.downFrame), 0, 0, 0, -2 * fabSize, 0, 1);
                    addAnimation(animatorList, findViewById(R.id.FinelyFrame), 0, -2 * fabSize, 0, -fabSize, 0, 1);
                    addAnimation(animatorList, findViewById(R.id.roughFrame), 0, -2 * fabSize, 0, 0, 0, 1);
                    addAnimation(animatorList, findViewById(R.id.nameFrame), 0, -fabSize, 0, 0, 0, 1);
                    addAnimation(animatorList, findViewById(R.id.fitFrame), 0, 0, 0, 0, 0, 1);
                    addAnimation(animatorList, findViewById(R.id.autoFrame), 0, 0, 0, -fabSize, 0, 1);

                    final AnimatorSet animatorSet = new AnimatorSet();
                    // リストのAnimatorを順番に実行します
                    animatorSet.playTogether(animatorList);
                    // アニメーションを開始します
                    animatorSet.start();
                }
            }
        });

        //オートスクロールについての処理
        //処理の後はbuttonInitを呼び出してボタンを再構成する
        FloatingActionButton fabAuto = findViewById(R.id.autoScroll);
        fabAuto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                autoScrollState=(autoScrollState+1)%3;
                switch(autoScrollState){
                    case 0:
                        break;
                    case 1:
                        fragment.autoScroll();//スクロール開始
                        break;
                    case 2:
                        fragment.stopAutoScroll();//スクロール終了
                        break;
                }
                //diagramFrameを再構成
                ((FrameLayout)findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                DiagramOptions.this.buttonInit();
            }
        });
        //時間軸を細かくする
        FloatingActionButton fabFinely = findViewById(R.id.fabFinely);
        fabFinely.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(verticalAxis<7){
                    verticalAxis++;
                    diagramPreference.edit().putInt("diagramVerticalAxis",verticalAxis).apply();
                    //Viewの再描画
                    ((FrameLayout)findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                    ((FrameLayout)findViewById(R.id.time)).getChildAt(0).invalidate();
                }
            }
        });
        //時間軸を粗くする
        FloatingActionButton fabRough = findViewById(R.id.fabRough);
        fabRough.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(verticalAxis>0){
                    verticalAxis--;
                    diagramPreference.edit().putInt("diagramVerticalAxis",verticalAxis).apply();

                    ((FrameLayout)findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                    ((FrameLayout)findViewById(R.id.time)).getChildAt(0).invalidate();
                }
            }
        });
        //下り時刻表の表示を変える
        FloatingActionButton fabDown = findViewById(R.id.fabDown);
        fabDown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showDownTrain=!showDownTrain;
                diagramPreference.edit().putBoolean("diagramShowDown",showDownTrain).apply();
                //Viewと詳細設定ボタンの再描画
                ((FrameLayout)findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                DiagramOptions.this.buttonInit();

            }
        });
        //上り時刻表の表示を変える
        FloatingActionButton fabUp = findViewById(R.id.fabUp);
        fabUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showUpTrain=!showUpTrain;
                diagramPreference.edit().putBoolean("diagramShowUp",showUpTrain).apply();
                //Viewと詳細設定ボタンの再描画
                ((FrameLayout)findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                DiagramOptions.this.buttonInit();
            }
        });
        //停車表示を変える
        FloatingActionButton fabStop = findViewById(R.id.fabStop);
        fabStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showTrainStop=!showTrainStop;
                diagramPreference.edit().putBoolean("diagramShowStop",showTrainStop).apply();
                //Viewと詳細設定ボタンの再描画
                ((FrameLayout)findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                DiagramOptions.this.buttonInit();
            }
        });
        //ダイヤの上下をそろえる
        FloatingActionButton fabFit = findViewById(R.id.fabFit);
        fabFit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                fragment.fitVertical();
            }
        });
        //列車番号・列車名の表示を切り替える
        FloatingActionButton fabNumber = findViewById(R.id.fabNumber);
        fabNumber.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                numberState=(numberState+1)%4;
                diagramPreference.edit().putInt("diagramNumberState",numberState).apply();

                DiagramOptions.this.buttonInit();
                ((FrameLayout)findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
            }
        });
        buttonInit();

    }
    private <T extends View> T findViewById(int id){
        return activity.findViewById(id);
    }
    public int veriticalAxis(){
        return verticalAxis;
    }
}

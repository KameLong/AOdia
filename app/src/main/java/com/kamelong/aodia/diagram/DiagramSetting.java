package com.kamelong.aodia.diagram;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;

import java.util.ArrayList;

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
/*
v1.0.5変更
各ボタンの開くスピードを500msから200msに変更
preferenceにデータを保持する機能を搭載
 */
/**
 * @author KameLong
 * DiagramFragmetに対しての詳細設定項目を扱う
 * これらの設定項目はすべてダイヤグラム画面の右下にある詳細設定ボタン群によって操作される
 */

public class DiagramSetting {
    private Activity context;//起動しているアクティビティー
    private DiagramFragment fragment;//この設定項目が載っているFragment

    private boolean fabDiagramVisible;//ダイヤ詳細徹底を開いているか

    public boolean downFrag;//下り列車の表示切替
    public boolean upFrag;//上り列車の表示切替
    public boolean stopFrag;//停車駅表示（小さい○)の表示切替

    /**
     * nameFragとnumberFragの切り替え状態を表す
     * 0：両方とも表示
     * 1：両方とも非表示
     * 2：列車番号のみ表示
     * 3：列車名のみ表示
     * @see #nameFrag
     * @see #numberFrag
     */
    public int numberState=0;
    public boolean nameFrag;
    public  boolean numberFrag;

    /*
    非表示：0
    現時刻縦線のみ表示：1
    オートスクロール：2
     */
    public int autoScrollState=0;
    /*
    60分目：0
    30分目：1
    20分目：2
    15分目：3
    10分目：4
    5分目：5
    2分目：6
    1分目：7
    */
    private int verticalAxis=3;
    private static final int DURATION=200;

    public DiagramSetting(Context c) {
        //初期設定
        context = (MainActivity) c;
        downFrag = true;
        upFrag = true;
        stopFrag = false;
        nameFrag = true;
        numberFrag = true;
        fabDiagramVisible = false;
        loadChange();
    }

    /**
     * 詳細設定のデータをpreferenceに保存する
     * このメソッドはDiagramFragmentがstopされるときに呼び出されるべき
     * @see #loadChange() 呼び出し
     */
    public void saveChange(){
        SharedPreferences spf= PreferenceManager.getDefaultSharedPreferences(context);
        spf.edit().putBoolean("DiagramSetting.downFrag",downFrag).apply();
        spf.edit().putBoolean("DiagramSetting.upFrag",upFrag).apply();
        spf.edit().putBoolean("DiagramSetting.stopFrag",stopFrag).apply();
        spf.edit().putInt("DiagramSetting.numberState",numberState).apply();
        spf.edit().putInt("DiagramSetting.verticalAxis",verticalAxis).apply();
    }

    /**
     * 保存された詳細設定のデータを読み込む
     * @see #saveChange()
     */
    public void loadChange(){
        try {
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
            downFrag = spf.getBoolean("DiagramSetting.downFrag", downFrag);
            upFrag = spf.getBoolean("DiagramSetting.upFrag", upFrag);
            stopFrag = spf.getBoolean("DiagramSetting.stopFrag", stopFrag);
            numberState = spf.getInt("DiagramSetting.numberState", numberState);
            verticalAxis = spf.getInt("DiagramSetting.verticalAxis", verticalAxis);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * FloatingActionButtonのチェック状態を変える
     * checkedの場合は背景色をandroid.R.color.holo_blue_lightにし
     * uncheckedの場合は背景色をandroid.R.color.darker_grayにする
     * @param fab
     * @param check
     */
    private void checkFab(FloatingActionButton fab,boolean check){
        if(check){
            fab.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        }else{
            fab.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }
    }
    /**
     * それぞれのボタンの初期設定を行う
     */
    private void buttonInit(){
        FloatingActionButton fabAuto = (FloatingActionButton) fragment.findViewById(R.id.autoScroll);

        switch(autoScrollState){
            case 0:
                fabAuto.setImageResource(R.drawable.auto_scroll);
                checkFab(fabAuto,false);
                break;
            case 1:
                checkFab(fabAuto,true);
                break;
            case 2:
                fabAuto.setImageResource(R.drawable.pause);
                checkFab(fabAuto,true);
                break;
        }
        checkFab((FloatingActionButton) fragment.findViewById(R.id.fabDown),downFrag);
        checkFab((FloatingActionButton) fragment.findViewById(R.id.fabUp),upFrag);
        checkFab((FloatingActionButton) fragment.findViewById(R.id.fabStop),stopFrag);

        FloatingActionButton fabNumber = (FloatingActionButton) fragment.findViewById(R.id.fabNumber);
        TextView textName=(TextView) fragment.findViewById(R.id.textname);
        TextView textNumber=(TextView) fragment.findViewById(R.id.textNumber);
        switch(numberState){
            case 0:
                numberFrag=true;
                nameFrag=true;
                textName.setVisibility(View.VISIBLE);
                textNumber.setVisibility(View.VISIBLE);
                checkFab(fabNumber,true);
                break;
            case 1:
                numberFrag=false;
                nameFrag=false;
                textName.setVisibility(View.VISIBLE);
                textNumber.setVisibility(View.VISIBLE);
                checkFab(fabNumber,false);
                break;
            case 2:
                numberFrag=true;
                nameFrag=false;
                textName.setVisibility(View.INVISIBLE);
                textNumber.setVisibility(View.VISIBLE);
                checkFab(fabNumber,true);
                break;
            case 3:
                numberFrag=false;
                nameFrag=true;
                textName.setVisibility(View.VISIBLE);
                textNumber.setVisibility(View.INVISIBLE);
                checkFab(fabNumber,true);
                break;
        }


    }

    /**
     * 移動アニメーションを行う
     * animatorのリストを与えることで、そこに新しくアニメーションンを追加する
     * X、Y方向への平行移動、透明度の変更を行う
     * @param animators
     * @param view
     * @param startTranslationX
     * @param endTranslationX
     * @param startTranslationY
     * @param endTranslationY
     * @param startAlpfa
     * @param endAlpfa
     */
    private void addAnimation(ArrayList<Animator> animators,View view,int startTranslationX,int endTranslationX,
                              int startTranslationY,int endTranslationY,float startAlpfa,float endAlpfa){
        animators.add(ObjectAnimator.ofPropertyValuesHolder(view
                , PropertyValuesHolder.ofFloat( "translationX",startTranslationX,endTranslationX)
                , PropertyValuesHolder.ofFloat( "translationY",startTranslationY,endTranslationY))
                .setDuration(DURATION));
        animators.add(ObjectAnimator.ofPropertyValuesHolder(view
                , PropertyValuesHolder.ofFloat( "alpha", startAlpfa,endAlpfa)));

    }

    /**
     * DiagramFragmentが与えられた時にダイヤ詳細設定ボタンについて
     * @param f
     */
    public void create(DiagramFragment f){
        fragment=f;
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
                    int fabSize=fragment.findViewById(R.id.fitFrame).getWidth();

                    ArrayList<Animator> animatorList= new ArrayList<Animator>();
                    //アニメーションを追加していく
                    addAnimation(animatorList,fragment.findViewById(R.id.settingFrame),-fabSize ,0, -fabSize,0, 1,1);

                    addAnimation(animatorList,fragment.findViewById(R.id.upFrame),-2*fabSize,0,-2*fabSize,0, 1,0);
                    addAnimation(animatorList,fragment.findViewById(R.id.stopFrame),-fabSize,0,-2*fabSize,0, 1,0);
                    addAnimation(animatorList,fragment.findViewById(R.id.downFrame),0,0,-2*fabSize,0, 1,0);
                    addAnimation(animatorList,fragment.findViewById(R.id.FinelyFrame),-2*fabSize,0,-fabSize,0, 1,0);
                    addAnimation(animatorList,fragment.findViewById(R.id.roughFrame),-2*fabSize,0,0,0, 1,0);
                    addAnimation(animatorList,fragment.findViewById(R.id.nameFrame),-fabSize,0,0,0, 1,0);
                    addAnimation(animatorList,fragment.findViewById(R.id.fitFrame),0,0,0,0, 1,0);
                    addAnimation(animatorList,fragment.findViewById(R.id.autoFrame),0,0,-fabSize,0, 1,0);
                    final AnimatorSet animatorSet = new AnimatorSet();
                    // リストのAnimatorを順番に実行します
                    animatorSet.playTogether( animatorList );
                    // アニメーションを開始します
                    animatorSet.start();
                    fabDiagramVisible = false;
                } else {

                    fabDiagramVisible=true;
                    int fabSize=fragment.findViewById(R.id.fitFrame).getWidth();
                    ArrayList<Animator> animatorList= new ArrayList<Animator>();
                    //アニメーションを追加していく

                    addAnimation(animatorList,fragment.findViewById(R.id.settingFrame),0,-fabSize ,0, -fabSize, 1,1);

                    addAnimation(animatorList,fragment.findViewById(R.id.upFrame),0,-2*fabSize,0,-2*fabSize,0, 1);
                    addAnimation(animatorList,fragment.findViewById(R.id.stopFrame),0,-fabSize,0,-2*fabSize,0, 1);
                    addAnimation(animatorList,fragment.findViewById(R.id.downFrame),0,0,0,-2*fabSize,0, 1);
                    addAnimation(animatorList,fragment.findViewById(R.id.FinelyFrame),0,-2*fabSize,0,-fabSize,0, 1);
                    addAnimation(animatorList,fragment.findViewById(R.id.roughFrame),0,-2*fabSize,0,0,0, 1);
                    addAnimation(animatorList,fragment.findViewById(R.id.nameFrame),0,-fabSize,0,0,0, 1);
                    addAnimation(animatorList,fragment.findViewById(R.id.fitFrame),0,0,0,0,0, 1);
                    addAnimation(animatorList,fragment.findViewById(R.id.autoFrame),0,0,0,-fabSize,0, 1);

                    final AnimatorSet animatorSet = new AnimatorSet();
                    // リストのAnimatorを順番に実行します
                    animatorSet.playTogether( animatorList );
                    // アニメーションを開始します
                    animatorSet.start();
                }
            }
        });

        //オートスクロールについての処理
        //処理の後はbuttonInitを呼び出してボタンを再構成する
        FloatingActionButton fabAuto = (FloatingActionButton) fragment.findViewById(R.id.autoScroll);
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
                ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                DiagramSetting.this.buttonInit();
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
                downFrag=!downFrag;
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
                upFrag=!upFrag;
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
                stopFrag=!stopFrag;
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
                fragment.fitVertical();
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

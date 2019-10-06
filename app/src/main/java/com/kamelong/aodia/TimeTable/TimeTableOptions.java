package com.kamelong.aodia.TimeTable;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamelong.aodia.R;

import java.util.ArrayList;

public class TimeTableOptions {
    private static final int DURATION = 200;

    public Activity activity;
    public TimeTableFragment fragment;
    public View container;

    public boolean showDetailOption=false;

    public boolean showOperation=false;
    public boolean showTrainName=false;
    public boolean showStartStation=true;
    public boolean showEndStation=true;
    public boolean showRemark=true;

    public boolean showPassTime=false;

    private int trainWidth=5;
    public int getTrainWidth(){
        if(showSecond){
            return trainWidth+3;
        }
        return trainWidth;
    }
    public boolean showSecond=false;
    public boolean trainEdit=true;

    public TimeTableOptions(final Activity activity,final View container, final TimeTableFragment fragment){
        this.activity=activity;
        this.fragment=fragment;
        this.container =container;
        try {
            trainWidth = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activity).getString("lineTimetableWidth", "5"));
        }catch (NumberFormatException e){
            trainWidth=5;
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("lineTimetableWidth", "5").apply();
        }
        //メインボタン
        this.container.findViewById(R.id.fabTrainTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrainTimeOption();
            }
        });
        //通過駅
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabShowPass),showPassTime);
        this.container.findViewById(R.id.fabShowPass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassTime=!showPassTime;
                checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabShowPass),showPassTime);
                fragment.invalidate();


            }
        });
        //秒表示
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabShowSeconds),showSecond);
        this.container.findViewById(R.id.fabShowSeconds).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSecond=!showSecond;
                checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabShowSeconds),showSecond);
                fragment.invalidate();
            }
        });
        //列車名
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabShowTrainName),showTrainName);
        this.container.findViewById(R.id.fabShowTrainName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrainName=!showTrainName;
                checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabShowTrainName),showTrainName);
                fragment.invalidate();
            }
        });
        //始終点
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabStartEnd),showStartStation);
        this.container.findViewById(R.id.fabStartEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartStation=!showStartStation;
                showEndStation=showStartStation;
                checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabStartEnd),showStartStation);
                fragment.invalidate();

            }
        });
        //備考
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabShowRemark),showRemark);
        this.container.findViewById(R.id.fabShowRemark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemark=!showRemark;
                checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabShowRemark),showRemark);
                fragment.invalidate();
            }
        });
        //編集
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabTrainEdit),trainEdit);
        if (trainEdit) {
            this.container.findViewById(R.id.fabTrainEdit).getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
        } else {
            this.container.findViewById(R.id.fabTrainEdit).getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }

        this.container.findViewById(R.id.fabTrainEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrainEdit();
                if (trainEdit) {
                    container.findViewById(R.id.fabTrainEdit).getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
                } else {
                    container.findViewById(R.id.fabTrainEdit).getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
                }
                fragment.invalidate();
            }
        });
        //編集
        this.container.findViewById(R.id.fabTrainCopy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.trainCopy();
            }
        });
        this.container.findViewById(R.id.fabTrainPaste).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.trainPaste();
            }
        });
        this.container.findViewById(R.id.fabTrainCut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.trainCut();
            }
        });


    }

    public void openTrainTimeOption(){
        showDetailOption=!showDetailOption;
        int fabSize = container.findViewById(R.id.fabTrainTime).getWidth();
        if(showDetailOption) {
            ArrayList<Animator> animatorList = new ArrayList<Animator>();
            //アニメーションを追加していく

            addAnimation(animatorList, container.findViewById(R.id.fabTrainTime), 0, -fabSize, 0, -fabSize, 1, 1);

            addAnimation(animatorList, container.findViewById(R.id.frameShowPass), 0, -2 * fabSize, 0, -2 * fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainEdit), 0, -fabSize, 0, -2 * fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameShowSecond), 0, 0, 0, -2 * fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameRenewOperation), 0, -2 * fabSize, 0, -fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameShowTrainName), 0, -2 * fabSize, 0, 0, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameStartEnd), 0, -fabSize, 0, 0, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameShowRemark), 0, 0, 0, 0, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainDelete), 0, 0, 0, -fabSize, 0, 1);
            if(trainEdit){
                addAnimation(animatorList, container.findViewById(R.id.frameTrainCopy), 0, -2*fabSize, 0, -3*fabSize, 0, 1);
                addAnimation(animatorList, container.findViewById(R.id.frameTrainPaste), 0, -1*fabSize, 0, -3*fabSize, 0, 1);
                addAnimation(animatorList, container.findViewById(R.id.frameTrainCut), 0, 0, 0, -3*fabSize, 0, 1);
            }else{
                addAnimation(animatorList, container.findViewById(R.id.frameTrainCopy), 0, -1*fabSize, 0, -2*fabSize, 0, 1);
                addAnimation(animatorList, container.findViewById(R.id.frameTrainPaste), 0, -1*fabSize, 0, -2*fabSize, 0, 1);
                addAnimation(animatorList, container.findViewById(R.id.frameTrainCut), 0, -1*fabSize, 0, -2*fabSize, 0, 1);

            }



            final AnimatorSet animatorSet = new AnimatorSet();
            // リストのAnimatorを順番に実行します
            animatorSet.playTogether(animatorList);
            // アニメーションを開始します
            animatorSet.start();
        }else{
            ArrayList<Animator> animatorList = new ArrayList<Animator>();
            //アニメーションを追加していく
            addAnimation(animatorList, container.findViewById(R.id.fabTrainTime), -fabSize, 0, -fabSize, 0, 1, 1);

            addAnimation(animatorList, container.findViewById(R.id.frameShowPass), -2 * fabSize, 0, -2 * fabSize, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainEdit), -fabSize, 0, -2 * fabSize, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameShowSecond), 0, 0, -2 * fabSize, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameRenewOperation), -2 * fabSize, 0, -fabSize, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameShowTrainName), -2 * fabSize, 0, 0, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameStartEnd), -fabSize, 0, 0, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameShowRemark), 0, 0, 0, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainDelete), 0, 0, -fabSize, 0, 1, 0);
            if(trainEdit) {
                addAnimation(animatorList, container.findViewById(R.id.frameTrainCopy), -2 * fabSize, 0, -3 * fabSize, 0, 1, 0);
                addAnimation(animatorList, container.findViewById(R.id.frameTrainPaste), -1 * fabSize, 0, -3 * fabSize, 0, 1, 0);
                addAnimation(animatorList, container.findViewById(R.id.frameTrainCut), 0, 0, -3 * fabSize, 0, 1, 0);
            }else{
                addAnimation(animatorList, container.findViewById(R.id.frameTrainCopy), -1 * fabSize, 0, -2 * fabSize, 0, 1, 0);
                addAnimation(animatorList, container.findViewById(R.id.frameTrainPaste), -1 * fabSize, 0, -2 * fabSize, 0, 1, 0);
                addAnimation(animatorList, container.findViewById(R.id.frameTrainCut), -1*fabSize, 0, -2* fabSize, 0, 1, 0);

            }
            final AnimatorSet animatorSet = new AnimatorSet();
            // リストのAnimatorを順番に実行します
            animatorSet.playTogether(animatorList);
            // アニメーションを開始します
            animatorSet.start();


        }
    }
    public void openTrainEdit(){
        trainEdit=!trainEdit;
        int fabSize = container.findViewById(R.id.fabTrainTime).getWidth();
        if(trainEdit) {
            ArrayList<Animator> animatorList = new ArrayList<Animator>();
            //アニメーションを追加していく


            addAnimation(animatorList, container.findViewById(R.id.frameTrainCopy), -1*fabSize, -2*fabSize, -2*fabSize, -3*fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainPaste), -1*fabSize, -1*fabSize, -2*fabSize, -3*fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainCut), -1*fabSize, 0, -2*fabSize, -3*fabSize, 0, 1);


            final AnimatorSet animatorSet = new AnimatorSet();
            // リストのAnimatorを順番に実行します
            animatorSet.playTogether(animatorList);
            // アニメーションを開始します
            animatorSet.start();
        }else{
            ArrayList<Animator> animatorList = new ArrayList<Animator>();
            //アニメーションを追加していく
            addAnimation(animatorList, container.findViewById(R.id.frameTrainCopy), -2*fabSize, -1*fabSize, -3*fabSize,-2*fabSize, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainPaste), -1*fabSize,-1*fabSize , -3*fabSize,-2*fabSize, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainCut), 0, -1*fabSize, -3*fabSize,-2*fabSize , 1, 0);
            final AnimatorSet animatorSet = new AnimatorSet();
            // リストのAnimatorを順番に実行します
            animatorSet.playTogether(animatorList);
            // アニメーションを開始します
            animatorSet.start();


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
                , PropertyValuesHolder.ofFloat("alpha", startAlpfa, endAlpfa)).setDuration(DURATION));

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

}

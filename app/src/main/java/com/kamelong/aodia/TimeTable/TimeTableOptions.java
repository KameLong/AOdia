package com.kamelong.aodia.TimeTable;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamelong.aodia.MainActivity;
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

    public SharedPreferences preference;

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
        preference= PreferenceManager.getDefaultSharedPreferences(activity);

        try {
            trainWidth = Integer.parseInt(preference.getString("lineTimetableWidth", "5"));
        }catch (NumberFormatException e){
            trainWidth=5;
            preference.edit().putString("lineTimetableWidth", "5").apply();
        }
        //メインボタン
        this.container.findViewById(R.id.fabTrainTime).setOnClickListener(v -> openTrainTimeOption());
        showPassTime=preference.getBoolean("timetableShowPass",false);
        //通過駅
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabShowPass),showPassTime);
        this.container.findViewById(R.id.fabShowPass).setOnClickListener(v -> {
            showPassTime=!showPassTime;
            preference.edit().putBoolean("timetableShowPass",showPassTime).apply();
            checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabShowPass),showPassTime);
            fragment.invalidate();
        });
        //秒表示
        showSecond=preference.getBoolean("timetableShowSecond",false);

        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabShowSeconds),showSecond);
        this.container.findViewById(R.id.fabShowSeconds).setOnClickListener(v -> {
            showSecond=!showSecond;
            preference.edit().putBoolean("timetableShowSecond",showSecond).apply();
            checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabShowSeconds),showSecond);
            fragment.invalidate();
        });
        //列車名
        showTrainName=preference.getBoolean("timetableShowName",false);
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabShowTrainName),showTrainName);
        this.container.findViewById(R.id.fabShowTrainName).setOnClickListener(v -> {
            showTrainName=!showTrainName;
            preference.edit().putBoolean("timetableShowName",showTrainName).apply();
            checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabShowTrainName),showTrainName);
            fragment.invalidate();
        });
        //始終点
        showStartStation=preference.getBoolean("timetableShowStart",false);
        showEndStation=showStartStation;
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabStartEnd),showStartStation);
        this.container.findViewById(R.id.fabStartEnd).setOnClickListener(v -> {
            showStartStation=!showStartStation;
            showEndStation=showStartStation;
            preference.edit().putBoolean("timetableShowStart",showStartStation).apply();

            checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabStartEnd),showStartStation);
            fragment.invalidate();

        });
        //備考
        showRemark=preference.getBoolean("timetableShowRemark",false);
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabShowRemark),showRemark);
        this.container.findViewById(R.id.fabShowRemark).setOnClickListener(v -> {
            showRemark=!showRemark;
            preference.edit().putBoolean("timetableShowRemark",showRemark).apply();
            checkFab((FloatingActionButton) TimeTableOptions.this.container.findViewById(R.id.fabShowRemark),showRemark);
            fragment.invalidate();
        });
        this.container.findViewById(R.id.fabFilter).setOnClickListener(v -> {
            TrainTypeFilter dialog=new TrainTypeFilter((MainActivity) activity,fragment.getLineFile(),fragment);
            dialog.show();
            dialog.setOnDismissListener(dialog1 -> fragment.allTrainChange());
        });

        //編集
        checkFab((FloatingActionButton) this.container.findViewById(R.id.fabTrainEdit),trainEdit);
        if (trainEdit) {
            this.container.findViewById(R.id.fabTrainEdit).getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
        } else {
            this.container.findViewById(R.id.fabTrainEdit).getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }

        this.container.findViewById(R.id.fabTrainEdit).setOnClickListener(v -> {
            openTrainEdit();
            if (trainEdit) {
                container.findViewById(R.id.fabTrainEdit).getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);
            } else {
                container.findViewById(R.id.fabTrainEdit).getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
            }
            fragment.invalidate();
        });
        //編集
        this.container.findViewById(R.id.fabTrainCopy).setOnClickListener(v -> fragment.trainCopy());
        this.container.findViewById(R.id.fabTrainPaste).setOnClickListener(v -> fragment.trainPaste());
        this.container.findViewById(R.id.fabTrainCut).setOnClickListener(v -> fragment.trainCut());


    }

    public void openTrainTimeOption(){
        showDetailOption=!showDetailOption;
        int fabSize = container.findViewById(R.id.fabTrainTime).getWidth();
        if(showDetailOption) {
            ArrayList<Animator> animatorList = new ArrayList<>();
            //アニメーションを追加していく

            addAnimation(animatorList, container.findViewById(R.id.fabTrainTime), 0, -fabSize, 0, -fabSize, 1, 1);

            addAnimation(animatorList, container.findViewById(R.id.frameShowPass), 0, -2 * fabSize, 0, -2 * fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainEdit), 0, -fabSize, 0, -2 * fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameShowSecond), 0, 0, 0, -2 * fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameRenewOperation), 0, -2 * fabSize, 0, -fabSize, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameShowTrainName), 0, -2 * fabSize, 0, 0, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameStartEnd), 0, -fabSize, 0, 0, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameShowRemark), 0, 0, 0, 0, 0, 1);
            addAnimation(animatorList, container.findViewById(R.id.frameFilter), 0, 0, 0, -fabSize, 0, 1);
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
            ArrayList<Animator> animatorList = new ArrayList<>();
            //アニメーションを追加していく
            addAnimation(animatorList, container.findViewById(R.id.fabTrainTime), -fabSize, 0, -fabSize, 0, 1, 1);

            addAnimation(animatorList, container.findViewById(R.id.frameShowPass), -2 * fabSize, 0, -2 * fabSize, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameTrainEdit), -fabSize, 0, -2 * fabSize, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameShowSecond), 0, 0, -2 * fabSize, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameRenewOperation), -2 * fabSize, 0, -fabSize, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameShowTrainName), -2 * fabSize, 0, 0, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameStartEnd), -fabSize, 0, 0, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameShowRemark), 0, 0, 0, 0, 1, 0);
            addAnimation(animatorList, container.findViewById(R.id.frameFilter), 0, 0, -fabSize, 0, 1, 0);
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
            ArrayList<Animator> animatorList = new ArrayList<>();
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
            ArrayList<Animator> animatorList = new ArrayList<>();
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
     */
    private void checkFab(FloatingActionButton fab, boolean check) {
        if (check) {
            fab.getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        } else {
            fab.getBackground().setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }
    }

}

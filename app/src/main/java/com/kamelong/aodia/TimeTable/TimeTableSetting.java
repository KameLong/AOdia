package com.kamelong.aodia.TimeTable;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.Diagram.DiagramFragment;
import com.kamelong.aodia.R;

import java.util.ArrayList;

public class TimeTableSetting {
    FloatingActionButton trainTimeSetting;
    FloatingActionButton renewOperation;
    FloatingActionButton showTrainName;
    FloatingActionButton showRemark;
    FloatingActionButton showSecond;
    FloatingActionButton showPassTime;
    FloatingActionButton deleteTrain;
    TimeTableFragment fragment;
    int fabSize=54;

    boolean fabTrainTimeVisible=false;


    private void checkFab(FloatingActionButton fab, boolean check) {
        if (check) {
            fab.getBackground().setColorFilter(ContextCompat.getColor(fragment.getAOdiaActivity(), android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        } else {
            fab.getBackground().setColorFilter(ContextCompat.getColor(fragment.getAOdiaActivity(), android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }
    }
    private void addAnimation(ArrayList<Animator> animators, View view, int startTranslationX, int endTranslationX,
                              int startTranslationY, int endTranslationY, float startAlpfa, float endAlpfa) {
        animators.add(ObjectAnimator.ofPropertyValuesHolder(view
                , PropertyValuesHolder.ofFloat("translationX", startTranslationX, endTranslationX)
                , PropertyValuesHolder.ofFloat("translationY", startTranslationY, endTranslationY))
                .setDuration(500));
        animators.add(ObjectAnimator.ofPropertyValuesHolder(view
                , PropertyValuesHolder.ofFloat("alpha", startAlpfa, endAlpfa)));
    }
    public void create(final TimeTableFragment f) {
        final SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(f.getAOdiaActivity());
        fragment = f;
        fabTrainTimeVisible = false;
        /*
        fragment.findViewById(R.id.fabFinely).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabRough).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabFit).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabNumber).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabDown).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabStop).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.fabUp).setVisibility(View.VISIBLE);
        fragment.findViewById(R.id.autoScroll).setVisibility(View.VISIBLE);
        */
        trainTimeSetting=(FloatingActionButton) fragment.findViewById(R.id.fabTrainTime);
        renewOperation=(FloatingActionButton) fragment.findViewById(R.id.fabRenewOperation);
        showTrainName=(FloatingActionButton) fragment.findViewById(R.id.fabShowTrainName);
        showRemark=(FloatingActionButton) fragment.findViewById(R.id.fabShowRemark);
        showSecond=(FloatingActionButton) fragment.findViewById(R.id.fabShowSeconds);
        showPassTime=(FloatingActionButton) fragment.findViewById(R.id.fabShowPass);
        deleteTrain=(FloatingActionButton) fragment.findViewById(R.id.fabTrainDelete);

        trainTimeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fabSize = trainTimeSetting.getWidth();
                if(fabTrainTimeVisible){
                    ArrayList<Animator> animatorList = new ArrayList<Animator>();
                    //アニメーションを追加していく
                    addAnimation(animatorList, fragment.findViewById(R.id.fabTrainTime), -fabSize, 0, -fabSize, 0, 1, 1);

//                    addAnimation(animatorList, fragment.findViewById(R.id.upFrame), -2 * fabSize, 0, -2 * fabSize, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameTrainDelete), -fabSize, 0, -2 * fabSize, 0, 1, 0);
//                    addAnimation(animatorList, fragment.findViewById(R.id.downFrame), 0, 0, -2 * fabSize, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameShowSecond), -2 * fabSize, 0, -fabSize, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameShowTrainName), -2 * fabSize, 0, 0, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameShowRemark), -fabSize, 0, 0, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameShowPass), 0, 0, 0, 0, 1, 0);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameRenewOperation), 0, 0, -fabSize, 0, 1, 0);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animatorList);
                    animatorSet.start();

                    fabTrainTimeVisible=false;

                }else{

                    fragment.findViewById(R.id.frameRenewOperation).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.frameShowPass).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.frameShowRemark).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.frameShowTrainName).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.frameShowSecond).setVisibility(View.VISIBLE);
//                    fragment.findViewById(R.id.frameTrainDelete).setVisibility(View.VISIBLE);
                    ArrayList<Animator> animatorList = new ArrayList<Animator>();

                    addAnimation(animatorList, fragment.findViewById(R.id.fabTrainTime), 0, -fabSize, 0, -fabSize, 1, 1);

//                    addAnimation(animatorList, fragment.findViewById(R.id.upFrame), 0, -2 * fabSize, 0, -2 * fabSize, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameTrainDelete), 0, -fabSize, 0, -2 * fabSize, 0, 1);
//                    addAnimation(animatorList, fragment.findViewById(R.id.downFrame), 0, 0, 0, -2 * fabSize, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameShowSecond), 0, -2 * fabSize, 0, -fabSize, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameShowTrainName), 0, -2 * fabSize, 0, 0, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameShowRemark), 0, -fabSize, 0, 0, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameShowPass), 0, 0, 0, 0, 0, 1);
                    addAnimation(animatorList, fragment.findViewById(R.id.frameRenewOperation), 0, 0, 0, -fabSize, 0, 1);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animatorList);
                    animatorSet.start();
                    fabTrainTimeVisible=true;
                }
            }
        });
        renewOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.trainReset();
            }
        });
        checkFab(showTrainName,spf.getBoolean("trainName",false));
        showTrainName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spf.edit().putBoolean("trainName",!spf.getBoolean("trainName",false)).apply();
                f.trainReset();
                checkFab(showTrainName,spf.getBoolean("trainName",false));
            }
        });
        checkFab(showRemark,spf.getBoolean("showRemark",false));
        showRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spf.edit().putBoolean("showRemark",!spf.getBoolean("showRemark",false)).apply();
                f.trainReset();
                checkFab(showRemark,spf.getBoolean("showRemark",false));
            }
        });
        checkFab(showSecond,spf.getBoolean("secondSystem",false));
        showSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spf.edit().putBoolean("secondSystem",!spf.getBoolean("secondSystem",false)).apply();
                f.trainReset();
                checkFab(showSecond,spf.getBoolean("secondSystem",false));
            }
        });
        checkFab(showPassTime,spf.getBoolean("showPass",false));
        showPassTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spf.edit().putBoolean("showPass",!spf.getBoolean("showPass",false)).apply();
                f.trainReset();
                checkFab(showPassTime,spf.getBoolean("showPass",false));
            }
        });


    }

    }

package com.fc2.web.kamelong.aodia.diagram;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fc2.web.kamelong.aodia.MainActivity;
import com.fc2.web.kamelong.aodia.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kame on 2016/12/23.
 */
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
 * あと、これは強制というわけではないですが、このソースコードを利用したときは、
 * 作者に一言メールなりで連絡して欲しいなと思ってます。
 * こちらが全く知らないところで使われていたりするのは、ちょっと気分悪いですよね。
 * まあ、強制はできないので、皆さんの良識におまかせします。
 */
public class DiagramSetting {
    public boolean downFrag;
    public boolean upFrag;
    public boolean stopFrag;

    public boolean nameFrag;
    public  boolean numberFrag;
    public int numberState=0;

    private Activity context;
    private DiagramFragment fragment;
    private boolean fabDiagramVisible;
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

    public DiagramSetting(Context c) {
        context = (MainActivity) c;
        downFrag = true;
        upFrag = true;
        stopFrag = false;
        nameFrag = true;
        numberFrag = true;
        fabDiagramVisible = false;
    }
    private void buttonInit(){
        FloatingActionButton fabAuto = (FloatingActionButton) fragment.findViewById(R.id.autoScroll);
        switch(autoScrollState){
            case 1:
                fabAuto.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
                break;
            case 2:
                fabAuto.setImageResource(R.drawable.pause);
                fabAuto.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
                break;
            case 0:
                fabAuto.setImageResource(R.drawable.auto_scroll);
                fabAuto.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
                break;
        }
        FloatingActionButton fabDown = (FloatingActionButton) fragment.findViewById(R.id.fabDown);
        if(!downFrag){
            fabDown.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }else{
            fabDown.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        }
        FloatingActionButton fabUp = (FloatingActionButton) fragment.findViewById(R.id.fabUp);
        if(!upFrag){
            fabUp.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }else{
            fabUp.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        }
        FloatingActionButton fabStop = (FloatingActionButton) fragment.findViewById(R.id.fabStop);
        if(!stopFrag){
            fabStop.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
        }else{
            fabStop.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        }
        FloatingActionButton fabNumber = (FloatingActionButton) fragment.findViewById(R.id.fabNumber);
        TextView textName=(TextView) fragment.findViewById(R.id.textname);
        TextView textNumber=(TextView) fragment.findViewById(R.id.textNumber);
        switch(numberState){
            case 0:
                numberFrag=true;
                nameFrag=true;
                textName.setVisibility(View.VISIBLE);
                textNumber.setVisibility(View.VISIBLE);
                fabNumber.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
                break;
            case 1:
                numberFrag=false;
                nameFrag=false;
                textName.setVisibility(View.VISIBLE);
                textNumber.setVisibility(View.VISIBLE);
                fabNumber.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
                break;
            case 2:
                numberFrag=true;
                nameFrag=false;
                textName.setVisibility(View.INVISIBLE);
                textNumber.setVisibility(View.VISIBLE);
                fabNumber.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
                break;
            case 3:
                numberFrag=false;
                nameFrag=true;
                textName.setVisibility(View.VISIBLE);
                textNumber.setVisibility(View.INVISIBLE);
                fabNumber.getBackground().setColorFilter(ContextCompat.getColor(context,android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
                break;
        }


    }

    public void  create(DiagramFragment f){
        fragment=f;
        fabDiagramVisible = false;
        FloatingActionButton fabDiagram = (FloatingActionButton) fragment.findViewById(R.id.fabDiagram);
        fabDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabDiagramVisible) {
                    int fabSize=fragment.findViewById(R.id.fitFrame).getWidth();

                    ArrayList<Animator> animatorList= new ArrayList<Animator>();
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.settingFrame)
                            , PropertyValuesHolder.ofFloat( "translationX",  -fabSize ,0f)
                            , PropertyValuesHolder.ofFloat( "translationY",  -fabSize,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.upFrame)
                            , PropertyValuesHolder.ofFloat( "translationX",  -2*fabSize ,0f)
                            , PropertyValuesHolder.ofFloat( "translationY",  -2*fabSize,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabUp)
                            , PropertyValuesHolder.ofFloat( "alpha", 1f,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.stopFrame)
                            , PropertyValuesHolder.ofFloat( "translationX",  -fabSize ,0f)
                            , PropertyValuesHolder.ofFloat( "translationY",  -2*fabSize,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabStop)
                            , PropertyValuesHolder.ofFloat( "alpha",  1f,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.downFrame)
                            , PropertyValuesHolder.ofFloat( "translationY", -2*fabSize,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabDown)
                            , PropertyValuesHolder.ofFloat( "alpha", 1f,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.FinelyFrame)
                            , PropertyValuesHolder.ofFloat( "translationX",  -2*fabSize,0f )
                            , PropertyValuesHolder.ofFloat( "translationY", -fabSize,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabFinely)
                            , PropertyValuesHolder.ofFloat( "alpha", 1f,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.roughFrame)
                            , PropertyValuesHolder.ofFloat( "translationX", -2*fabSize,0f ))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabRough)
                            , PropertyValuesHolder.ofFloat( "alpha", 1f,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.nameFrame)
                            , PropertyValuesHolder.ofFloat( "translationX", -fabSize,0f ))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabFit)
                            , PropertyValuesHolder.ofFloat( "alpha",  1f,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.textname)
                            , PropertyValuesHolder.ofFloat( "alpha",  1f,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.textNumber)
                            , PropertyValuesHolder.ofFloat( "alpha",  1f,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabNumber)
                            , PropertyValuesHolder.ofFloat( "alpha",  1f,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.autoFrame)
                            , PropertyValuesHolder.ofFloat( "translationY", -fabSize,0f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.autoScroll)
                            , PropertyValuesHolder.ofFloat( "alpha", 1f,0f))
                            .setDuration(500));

                    final AnimatorSet animatorSet = new AnimatorSet();
                    // リストのAnimatorを順番に実行します
                    animatorSet.playTogether( animatorList );

                    // アニメーションを開始します
                    animatorSet.start();



/*                    fragment.findViewById(R.id.fabFinely).setVisibility(View.GONE);
                    fragment.findViewById(R.id.fabRough).setVisibility(View.GONE);
                    fragment.findViewById(R.id.fitFrame).setVisibility(View.GONE);
                    fragment.findViewById(R.id.fabName).setVisibility(View.GONE);
                    fragment.findViewById(R.id.fabDown).setVisibility(View.GONE);
                    fragment.findViewById(R.id.fabStop).setVisibility(View.GONE);
                    fragment.findViewById(R.id.fabUp).setVisibility(View.GONE);
                    fragment.findViewById(R.id.autoScroll).setVisibility(View.GONE);
*/
                    fabDiagramVisible = false;

                } else {
                    fragment.findViewById(R.id.fabFinely).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.fabRough).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.fabFit).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.fabNumber).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.fabDown).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.fabStop).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.fabUp).setVisibility(View.VISIBLE);
                    fragment.findViewById(R.id.autoScroll).setVisibility(View.VISIBLE);
                    fabDiagramVisible=true;
                    int fabSize=fragment.findViewById(R.id.fitFrame).getWidth();
                    ArrayList<Animator> animatorList= new ArrayList<Animator>();
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.settingFrame)
                            , PropertyValuesHolder.ofFloat( "translationX", 0f, -fabSize )
                            , PropertyValuesHolder.ofFloat( "translationY", 0f, -fabSize))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.upFrame)
                            , PropertyValuesHolder.ofFloat( "translationX", 0f, -2*fabSize )
                            , PropertyValuesHolder.ofFloat( "translationY", 0f, -2*fabSize))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabUp)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.stopFrame)
                            , PropertyValuesHolder.ofFloat( "translationX", 0f, -fabSize )
                            , PropertyValuesHolder.ofFloat( "translationY", 0f, -2*fabSize))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabStop)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.downFrame)
                            , PropertyValuesHolder.ofFloat( "translationY", 0f, -2*fabSize))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabDown)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.FinelyFrame)
                            , PropertyValuesHolder.ofFloat( "translationX", 0f, -2*fabSize )
                            , PropertyValuesHolder.ofFloat( "translationY", 0f, -fabSize))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabFinely)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.roughFrame)
                            , PropertyValuesHolder.ofFloat( "translationX", 0f, -2*fabSize ))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabRough)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.nameFrame)
                            , PropertyValuesHolder.ofFloat( "translationX", 0f, -fabSize ))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabFit)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.textname)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.textNumber)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.fabNumber)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.autoFrame)
                            , PropertyValuesHolder.ofFloat( "translationY", 0f, -fabSize))
                            .setDuration(500));
                    animatorList.add(ObjectAnimator.ofPropertyValuesHolder(fragment.findViewById(R.id.autoScroll)
                            , PropertyValuesHolder.ofFloat( "alpha", 0f, 1f))
                            .setDuration(500));

                    final AnimatorSet animatorSet = new AnimatorSet();
                    // リストのAnimatorを順番に実行します
                    animatorSet.playTogether( animatorList );

                    // アニメーションを開始します
                    animatorSet.start();
                }
            }
        });
        FloatingActionButton fabAuto = (FloatingActionButton) fragment.findViewById(R.id.autoScroll);
        fabAuto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                switch(autoScrollState){
                    case 0:
                        autoScrollState++;
                        fragment.autoScroll();
                        break;
                    case 1:
                        autoScrollState++;
                        fragment.stopAutoScroll();
                        break;
                    case 2:
                        autoScrollState=0;
                        break;
                }
                ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                DiagramSetting.this.buttonInit();
            }
        });
        FloatingActionButton fabFinely = (FloatingActionButton) fragment.findViewById(R.id.fabFinely);
        fabFinely.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(verticalAxis<7){
                    verticalAxis++;
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                    ((FrameLayout)fragment.findViewById(R.id.time)).getChildAt(0).invalidate();
                }
            }
        });
        FloatingActionButton fabRough = (FloatingActionButton) fragment.findViewById(R.id.fabRough);
        fabRough.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(verticalAxis>0){
                    verticalAxis--;
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                    ((FrameLayout)fragment.findViewById(R.id.time)).getChildAt(0).invalidate();
                }
            }
        });
        FloatingActionButton fabDown = (FloatingActionButton) fragment.findViewById(R.id.fabDown);
        fabDown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(downFrag){
                    downFrag=false;
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                }else{
                    downFrag=true;
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                }
                DiagramSetting.this.buttonInit();

            }
        });
        FloatingActionButton fabUp = (FloatingActionButton) fragment.findViewById(R.id.fabUp);
        fabUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(upFrag){
                    upFrag=false;
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                }else{
                    upFrag=true;
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                }
                DiagramSetting.this.buttonInit();
            }
        });
        FloatingActionButton fabStop = (FloatingActionButton) fragment.findViewById(R.id.fabStop);
        fabStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(stopFrag){
                    stopFrag=false;
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                }else{
                    stopFrag=true;
                    ((FrameLayout)fragment.findViewById(R.id.diagramFrame)).getChildAt(0).invalidate();
                }
                DiagramSetting.this.buttonInit();
            }
        });
        FloatingActionButton fabName = (FloatingActionButton) fragment.findViewById(R.id.fabFit);
        fabName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                fragment.fitVertical();
            }
        });
        FloatingActionButton fabNumber = (FloatingActionButton) fragment.findViewById(R.id.fabNumber);
        fabNumber.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                numberState++;
                if(numberState==4){
                    numberState=0;
                }
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

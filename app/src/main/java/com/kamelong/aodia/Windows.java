package com.kamelong.aodia;

/**
 * Created by kame on 2017/04/29.
 */

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * AOdiaの複数画面の処理などを管轄するクラス
 * 画面設定関係の処理をMainActivityから分離しています
 * @since v1.1.1
 * @author KameLong
 */
public class Windows {
    public boolean chooseContainer=false;
    private boolean showMiniTitle=true;
    public boolean showContainer=true;
    public boolean tabletStyle=false;

    private MainActivity activity;

    public Windows(MainActivity mActivity){
        activity=mActivity;
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(activity);
        tabletStyle=spf.getBoolean("fixedMenu",false);


    }

    /**
     * サイズ変更モードの時に用いるGestureDetector
     */
    private final GestureDetector gestureForSizeChange = new GestureDetector(activity,new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            changeWindowSize();
            return true;
        }
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }
        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }
        @Override
        public void onLongPress(MotionEvent motionEvent) {
        }
        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
            if(!changeWindowSize)return true;
            FrameLayout movingFrame=(FrameLayout)findViewById(R.id.movingFrame);
            ViewGroup.LayoutParams lp = movingFrame.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
            int leftMargin=mlp.leftMargin-(int)vx;
            int topMargin=mlp.topMargin-(int)vy;
            if(leftMargin<0){
                leftMargin=0;
            }
            if(leftMargin>activity.getResources().getDisplayMetrics().widthPixels){
                leftMargin=activity.getResources().getDisplayMetrics().widthPixels;
            }
            if(topMargin<0){
                topMargin=0;
            }
            if(topMargin>activity.getResources().getDisplayMetrics().heightPixels){
                topMargin=activity.getResources().getDisplayMetrics().heightPixels;
            }
            mlp.setMargins(leftMargin,topMargin, mlp.rightMargin, mlp.bottomMargin);
            //マージンを設定
            movingFrame.setLayoutParams(mlp);
            findViewById(R.id.layout1).setLayoutParams(new RelativeLayout.LayoutParams(leftMargin,topMargin));
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(activity);
            spf.edit().putString("width", ""+leftMargin).apply();
            spf.edit().putString("height", ""+topMargin).apply();

            return false;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
            return false;
        }
    });
    private final GestureDetector toolbarGesture = new GestureDetector(activity,new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    if(chooseContainer){

                    }else{
                        if(showMiniTitle){
                            hiddenMiniTitle();
                            showMiniTitle=false;
                        }else{
                            showMiniTitle();
                            showMiniTitle=true;
                        }
                    }
                    return true;
                }
                @Override
                public void onLongPress(MotionEvent motionEvent) {
                }
                @Override
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
                    return false;
                }
            });
    View.OnClickListener frameTitleClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(chooseContainer){

                KLFragment fragment=activity.moveFragment(R.id.container,((LinearLayout)view.getParent().getParent()).getChildAt(1).getId());
                chooseContainer=false;
                showContainer=false;
                ((TextView)view).setText(fragment.fragmentName());
                activity.invalidateOptionsMenu();
            }
        }};
    View.OnClickListener closeButtonClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                FragmentManager fragmentManager = activity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                KLFragment fragment = (KLFragment) fragmentManager.findFragmentById(((LinearLayout) view.getParent().getParent()).getChildAt(1).getId());
                if(fragment==null)return;
                fragmentTransaction.remove(fragment).commit();
                ((TextView)((LinearLayout) view.getParent()).getChildAt(1)).setText("ここにウインドウを移動できます");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };



    /**
     * windowsの初期設定
     * MainActivityにてsetContentViewが呼ばれた後に呼び出すこと
     */
    public void windowsInit(){
        activity.findViewById(R.id.sizeChangeLayuout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureForSizeChange.onTouchEvent(event);
            }
        });
        findViewById(R.id.frameTitle1).setOnClickListener(frameTitleClickListener);
        findViewById(R.id.frameTitle2).setOnClickListener(frameTitleClickListener);
        findViewById(R.id.frameTitle3).setOnClickListener(frameTitleClickListener);
        findViewById(R.id.frameTitle4).setOnClickListener(frameTitleClickListener);
        findViewById(R.id.button1).setOnClickListener(closeButtonClickListener);
        findViewById(R.id.button2).setOnClickListener(closeButtonClickListener);
        findViewById(R.id.button3).setOnClickListener(closeButtonClickListener);
        findViewById(R.id.button4).setOnClickListener(closeButtonClickListener);
        findViewById(R.id.toolbar).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return toolbarGesture.onTouchEvent(event);
            }
        });



    }
    public boolean optionMenu(int id){
        if(id==R.id.action_change_window_size){
            changeWindowSize();
            return true;
        }
        if(id==R.id.action_shrinking){
            if(chooseContainer) {
                View frontFragment = findViewById(R.id.container);
                ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.setMargins(0, 0, 0, 0);
                //マージンを設定
                frontFragment.setLayoutParams(mlp);
                showContainer(255);
                chooseContainer=false;
            }else{
                try {
                    KLFragment fragment = (KLFragment) activity.getFragmentManager().findFragmentById(R.id.container);
                    fragment.fragmentName();
                } catch (Exception e) {
                    Toast.makeText(activity, "このウインドウは分割できません", Toast.LENGTH_SHORT).show();
                    return true;
                }
                findViewById(R.id.backFragments).bringToFront();
                findViewById(R.id.backFragments).setBackgroundColor(Color.argb(0, 255, 255, 255));

                View frontFragment = findViewById(R.id.container);
                ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.setMargins(200, 200, 200, 200);
                //マージンを設定
                frontFragment.setLayoutParams(mlp);

                showContainer(200);
                showMiniTitle();
                chooseContainer = true;

            }
            activity.invalidateOptionsMenu();
            return true;

        }
        if(id==R.id.action_split_window){
            if(showContainer){
                View frontFragment = findViewById(R.id.container);
                ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.setMargins(0, 0, 0, 0);
                //マージンを設定
                frontFragment.setLayoutParams(mlp);
                hiddenContainer(255);
                showContainer=false;
            }else{
                View frontFragment = findViewById(R.id.container);
                ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.setMargins(0, 0, 0, 0);
                //マージンを設定
                frontFragment.setLayoutParams(mlp);
                showContainer(255);
                showContainer=true;
            }
            activity.invalidateOptionsMenu();
            return true;

        }
        return false;

    }
    private void hiddenMiniTitle(){
        findViewById(R.id.titlelayout1).setVisibility(View.GONE);
        findViewById(R.id.titlelayout2).setVisibility(View.GONE);
        findViewById(R.id.titlelayout3).setVisibility(View.GONE);
        findViewById(R.id.titlelayout4).setVisibility(View.GONE);

    }
    private void showMiniTitle(){
        findViewById(R.id.titlelayout1).setVisibility(View.VISIBLE);
        findViewById(R.id.titlelayout2).setVisibility(View.VISIBLE);
        findViewById(R.id.titlelayout3).setVisibility(View.VISIBLE);
        findViewById(R.id.titlelayout4).setVisibility(View.VISIBLE);
    }
    public void hiddenContainer(int alfa){
        findViewById(R.id.backFragments).bringToFront();
        findViewById(R.id.backFragments).setBackgroundColor(Color.argb(alfa,255,255,255));
        findViewById(R.id.container1).setBackgroundColor(Color.argb(alfa,255,255,255));
        findViewById(R.id.container2).setBackgroundColor(Color.argb(alfa,255,255,255));
        findViewById(R.id.container3).setBackgroundColor(Color.argb(alfa,255,255,255));
        findViewById(R.id.container4).setBackgroundColor(Color.argb(alfa,255,255,255));
        if(alfa==255) {
            View frontFragment = findViewById(R.id.container);
            ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mlp.setMargins(0, 0, 0, 0);
            frontFragment.setLayoutParams(mlp);
        }
        changeWindowSize=false;
    }
    public void showContainer(int alfa){
        if(alfa==255){
            showContainer=true;
            chooseContainer=false;
            View frontFragment = findViewById(R.id.container);
            ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mlp.setMargins(0, 0, 0, 0);
            frontFragment.setLayoutParams(mlp);
            activity.invalidateOptionsMenu();

        }
        findViewById(R.id.container).bringToFront();
        findViewById(R.id.container).setBackgroundColor(Color.argb(alfa,255,255,255));
        changeWindowSize=false;
    }
    boolean changeWindowSize=false;
    private void changeWindowSize(){
        if(changeWindowSize){
            findViewById(R.id.backFragments).bringToFront();
            changeWindowSize=false;
        }else {
            findViewById(R.id.sizeChangeLayuout).bringToFront();
            changeWindowSize=true;
        }
    }
    private View findViewById(int id){
        return activity.findViewById(id);
    }


}

package com.kamelong.aodia;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * アニメーション付きのViewの高さ方向の開閉を実装するクラス
 * https://qiita.com/farman0629/items/ed86059845551449a359
 */
public class ReSizeAnimation extends Animation {
    /**
     * アニメーション対象のView
     */
    View view;
    /**
     * アニメーション変化量
     */
    int addHeight;
    /**
     * 初期の高さ
     */
    int startHeight;

    public ReSizeAnimation(View view, int addHeight, int startHeight) {
        this.view = view;
        if(addHeight==-1){
            view.measure(view.getMeasuredWidthAndState(),0);
            addHeight=view.getMeasuredHeightAndState();
        }
        this.addHeight = addHeight;
        this.startHeight = startHeight;
    }

    /**
     * 開閉選択コンストラクタ
     * @param view
     * @param option 1:open,-1:close
     */
    public ReSizeAnimation(View view,int option) {
        this.view = view;
        switch (option){
            case 1:
                view.measure(view.getMeasuredWidthAndState(),0);
                addHeight=view.getMeasuredHeight();
                startHeight=0;
                break;
            case -1:
                addHeight=-view.getHeight();
                startHeight=view.getHeight();
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight = (int) (startHeight + addHeight * interpolatedTime);
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);

    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
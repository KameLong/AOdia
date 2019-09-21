package com.kamelong.aodia.TimeTable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.kamelong.aodia.AOdiaData.Train;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

public class TrainNameView extends TimeTableDefaultView {
    TimeTableFragment fragment;
    Train train;
    public boolean selected=false;
    final GestureDetector gesture = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(options.trainEdit){
                int bitmapSize=(int)(textSize*2.5);
                if(bitmapSize>getXsize()){
                    bitmapSize=getXsize();
                }
                bitmapSize+=textSize*0.8;
                if(e.getY()< bitmapSize){
                    selected=!selected;
                    invalidate();
                    return true;
                }
                if(e.getY()<bitmapSize+textSize*1.4){
                    fragment.openTrainEditFragment(train);

                    return true;

                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    });
    public TrainNameView(Context context,final TimeTableFragment fragment, final TimeTableOptions options , Train train){
        super(context,options);
        this.train=train;
        this.fragment=fragment;

        setClickable(true);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
    }
    public void onDraw(Canvas canvas){
        int startLine=0;
        if(options.trainEdit){
            int bitmapSize=(int)(textSize*2.5);
            if(bitmapSize>getXsize()){
                bitmapSize=getXsize();
            }
            Drawable d;
            if(selected){
                d=getResources().getDrawable(R.drawable.timetable_checkedbox, null);
            }else{
                d=getResources().getDrawable(R.drawable.timetable_box, null);
            }
            d.setBounds((getWidth()-bitmapSize)/2,0,getWidth()-(getWidth()-bitmapSize)/2,bitmapSize);
            d.draw(canvas);

            startLine+=bitmapSize;
            startLine+=textSize;
            startLine+=textSize;
            textPaint.setColor(Color.BLUE);
            drawText(canvas,"編集", 5, startLine, textPaint,true);
            startLine+=normalSpace;
            canvas.drawLine(0,startLine,getWidth(),startLine,blackPaint);
            startLine+=smallSpace;

        }
        textPaint.setColor(train.getTextColor().getAndroidColor());
        if(options.showOperation) {
            startLine+=textSize;
            canvas.drawText(train.getOperationNumber(), 5, startLine, textPaint);
        }
        startLine += textSize;
        canvas.drawText(train.number, 5,startLine, textPaint);
        startLine+=textSize;
        canvas.drawText(train.getTypeShortName(), 5,startLine, textPaint);
        if(options.showTrainName){
            startLine+=smallSpace;
            canvas.drawLine(0,startLine,getWidth(),startLine,blackPaint);
            drawTrainName(canvas,startLine);
            startLine+=textSize*8;

        }
        startLine+=smallSpace;

        canvas.drawLine(getWidth()-1,0,getWidth()-1,getHeight(),blackPaint);

    }

    private void drawTrainName(Canvas canvas,int startLine){
        try {
            int heightSpace = 12;
            String value= train.name;
            value=value.replace('ー','｜');
            value=value.replace('（','(');
            value=value.replace('）',')');
            value=value.replace('「','┐');
            value=value.replace('」','└');

            char[] str =value.toCharArray();
            int lineNum = 1;
            int space = heightSpace;
            for (int i = 0; i < str.length; i++) {
                if (space <= 0) {
                    space = heightSpace;
                    lineNum++;
                }
                if (!charIsEng(str[i])) {
                    space--;
                }
                space--;
            }
            space = heightSpace;
            int startX = (int) ((getWidth() - lineNum * textSize*1.2f) / 2 + (lineNum-1) * textSize*1.2f);
            int startY = startLine;
            for (int i = 0; i < str.length; i++) {
                if (space <= 0) {
                    space = heightSpace;
                    startX = startX - (int)(textSize*1.2f);
                    startY = startLine;
                }
                if (charIsEng(str[i])) {
                    space--;
                    canvas.save();
                    canvas.rotate(90,0,0);
                    drawText(canvas,String.valueOf(str[i]), startY+2,(int)( -startX-(textSize*0.2f)), textPaint,false);
                    canvas.restore();
                    startY = startY + (int) textPaint.measureText(String.valueOf(str[i]));
                } else {
                    space = space - 2;
                    startY = startY +textSize;
                    drawText(canvas,String.valueOf(str[i]), startX, startY, textPaint,false);
                }

            }
        }catch(Exception e){
            SDlog.log(e);
        }
        startLine+=6.7*textSize;


        if(train.count.length()>0){
            String gousuu=train.count;
            drawText(canvas,gousuu,0,startLine,textPaint,true);
            startLine+=textSize;
            drawText(canvas,"号",0,startLine,textPaint,true);
        }
    }
    private void drawText(Canvas canvas, String text, int x, int y, Paint paint, boolean centerFrag){
        if(centerFrag){
            canvas.drawText(text,(this.getWidth()-2- paint.measureText(text))/2,y,paint);
        }else{
            canvas.drawText(text,x,y,paint);
        }
    }
    @Override
    protected int getXsize(){
        return textSize*options.getTrainWidth()/2;
    }
    @Override
    protected int getYsize() {
        int startLine=0;
        if(options.trainEdit){
            int bitmapSize=(int)(textSize*2.5);
            if(bitmapSize>getXsize()){
                bitmapSize=getXsize();
            }
            startLine+=bitmapSize;
            startLine+=2*textSize;
            startLine+=normalSpace;
            startLine+=smallSpace;

        }

        if(options.showOperation) {
            startLine+=textSize;
        }
        startLine+=2*textSize;
        if(options.showTrainName){
            startLine+=smallSpace;
            startLine+=normalSpace;
            startLine+=textSize*8;
        }

        startLine+=smallSpace;

        return startLine;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(options.trainEdit){
            int bitmapSize=(int)(textSize*2.5);
            if(bitmapSize>getXsize()){
                bitmapSize=getXsize();
            }
            bitmapSize+=textSize*0.8;
            if(event.getY()<bitmapSize+textSize*1.4){
                return super.dispatchTouchEvent(event);

            }
        }
            return false;
    }
    public void setSelected(boolean value){
        selected=value;
        invalidate();

    }

}

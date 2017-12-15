package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.kamelong.OuDia2nd.Train
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaStation
import com.kamelong.aodia.diadata.AOdiaTrain

class TrainView (context: Context, val train: AOdiaTrain, override val xsize: Int) :KLView(context) {
    val diaFile=train.diaFile
    val direct=train.direction
    val showPassTime=false

    /**
     * 駅数の３倍がmax
     * focusPoint/3:駅Index
     * focusPoint%3:
     * ->0:着時刻
     * ->1:番線
     * ->2:発時刻
     */
    var focusPoint=-1
    override val ysize: Int
        get() {
            var result=0.4* textSize
            for(station in diaFile.getStationList()){
                if((station.getViewStyle(direct) and 0b01)!=0){
                    result+= textSize
                }
                if((station.getViewStyle(direct) and 0b10)!=0){
                    result+= textSize
                }
                if((station.getViewStyle(direct) and 0b11)==0b11){
                    result+= (textSize*0.2).toInt()
                }
                if((station.getViewStyle(direct) and 0b100)==0b100){
                    result+= (textSize*1.2).toInt()
                }

                if(station.branchEnd()>=0){
                    result+=(textSize*0.2).toInt()
                }
                if(station.branchStart()>=0){
                    result+=(textSize*0.2).toInt()
                }

            }
            return result.toInt()
        }
    override fun onDraw(canvas: Canvas?){
        if(canvas==null)return
        textPaint.color=train.trainType.textColor.androidColor
        var linePos=0.1f* textSize
        val stationList=diaFile.getStationList().toMutableList()

        for(i in if(direct==0){0 until stationList.size}else{stationList.size-1 downTo 0}){
            val station=stationList[i]

            if(station.branchStart()>=0){
                linePos+=(textSize*0.2f)
                canvas.drawLine(0f,linePos,width.toFloat(),linePos, blackBPaint)
            }

            val viewStyle=station.getViewStyle(direct)


            if((viewStyle and 0b0010) ==0b0010){
                if(focusPoint==i*3){
                    canvas.drawRect(2f,linePos+textSize*0.1f,width-2f,linePos+ textSize*1.1f, focusPaint);
                }
                linePos+=textSize
                when(train.getStopType(i)) {
                    Train.STOP_TYPE_NOSERVICE ->
                        drawNoService(canvas, linePos, textPaint)
                    Train.STOP_TYPE_STOP ->
                        if (train.getArrivalTime(i) >= 0) {
                            drawTime(canvas, linePos, train.getArrivalTime(i),textPaint)
                        } else if (((viewStyle and 0b0001) !=0b0001) &&train.getDepartureTime(i) >= 0) {
                            drawTime(canvas, linePos, train.getDepartureTime(i),textPaint)
                        } else {
                            if(i>0){
                                when(train.getStopType(i-1)){
                                    Train.STOP_TYPE_NOSERVICE ->
                                        drawNoService(canvas, linePos, textPaint)
                                    Train.STOP_TYPE_NOVIA->
                                        drawNoVia(canvas, linePos, textPaint)
                                    else->
                                        drawTextCenter(canvas, "○", linePos, textPaint)
                                }
                            }else {
                                drawTextCenter(canvas, "○", linePos, textPaint)
                            }
                        }
                    Train.STOP_TYPE_PASS->
                        if(showPassTime){
                            if (train.getArrivalTime(i) >= 0) {
                                textPaint.color= Color.GRAY
                                drawTime(canvas, linePos, train.getArrivalTime(i),textPaint)
                                textPaint.color= train.trainType.textColor.androidColor
                            } else if (train.getDepartureTime(i) >= 0) {
                                textPaint.color= Color.GRAY
                                drawTime(canvas, linePos, train.getDepartureTime(i),textPaint)
                                textPaint.color= train.trainType.textColor.androidColor
                            } else {
                                drawPass(canvas,linePos, textPaint)
                            }
                        }else{
                            drawPass(canvas,linePos, textPaint)
                        }
                    Train.STOP_TYPE_NOVIA->
                        drawNoVia(canvas, linePos, textPaint)

                }
            }
            if((viewStyle and 0b0011) ==0b0011){
                linePos+=textSize*0.2f
                canvas.drawLine(0f,linePos,width.toFloat(),linePos, blackPaint)
            }
            if((viewStyle and 0b0111) ==0b0110) {
                linePos += textSize * 0.2f
                if(focusPoint==i*3+1){
                    canvas.drawRect(2f,linePos+textSize*0.1f,width-2f,linePos+ textSize*1.1f, focusPaint);
                }
                canvas.drawLine(0f, linePos, width.toFloat(), linePos, blackPaint)
                linePos += textSize
                    drawStop(canvas, i,linePos, textPaint)
            } else if ((viewStyle and 0b0100) == 0b0100) {
                linePos += textSize
                drawStop(canvas,i, linePos, textPaint)
                linePos += textSize * 0.2f
                canvas.drawLine(0f, linePos, width.toFloat(), linePos, blackPaint)
            }

            if((viewStyle and 0b0001) ==0b0001){
                if(focusPoint==i*3+2){
                    canvas.drawRect(2f,linePos+textSize*0.1f,width-2f,linePos+ textSize*1.1f, focusPaint);
                }

                linePos+=textSize

                when(train.getStopType(i)) {
                    Train.STOP_TYPE_NOSERVICE ->
                        drawNoService(canvas, linePos, textPaint)
                    Train.STOP_TYPE_STOP ->
                        if (train.getDepartureTime(i) >= 0) {
                            drawTime(canvas, linePos, train.getDepartureTime(i),textPaint)
                        } else if (((viewStyle and 0b0001) !=0b0001) &&train.getArrivalTime(i) >= 0) {
                            drawTime(canvas, linePos, train.getArrivalTime(i),textPaint)
                        } else {
                            if(i<stationList.size-1){
                                when(train.getStopType(i+1)){
                                    Train.STOP_TYPE_NOSERVICE ->
                                        drawNoService(canvas, linePos, textPaint)
                                    Train.STOP_TYPE_NOVIA->
                                        drawNoVia(canvas, linePos, textPaint)
                                    else->
                                        drawTextCenter(canvas, "○", linePos, textPaint)
                                }
                            }else{
                                drawTextCenter(canvas, "○", linePos, textPaint)
                            }
                        }
                    Train.STOP_TYPE_PASS->
                        if(showPassTime){
                            if (train.getDepartureTime(i) >= 0) {
                                textPaint.color= Color.GRAY
                                drawTime(canvas, linePos, train.getDepartureTime(i),textPaint)
                                textPaint.color= train.trainType.textColor.androidColor
                            } else if (train.getArrivalTime(i) >= 0) {
                                textPaint.color= Color.GRAY
                                drawTime(canvas, linePos, train.getArrivalTime(i),textPaint)
                                textPaint.color= train.trainType.textColor.androidColor
                            } else {
                                drawPass(canvas,linePos, textPaint)
                            }
                        }else{
                            drawPass(canvas,linePos, textPaint)
                        }
                    Train.STOP_TYPE_NOVIA->
                        drawNoVia(canvas, linePos, textPaint)

                }
            }

            if(station.branchEnd()>=0){
                linePos+=(textSize*0.2f)
                canvas.drawLine(0f,linePos,width.toFloat(),linePos, blackBPaint)
            }


        }
    }

    fun drawTextFit(canvas:Canvas,value:String,ypos:Float,paint: Paint){
        val textWidth=width.toFloat()/ textSize.toFloat()
        var string=value
        if(value.length>2){
            string=string.substring(0,2)
        }
        for(c in string){
            if(c.toInt()<256){
                drawTextCenter(canvas,string,ypos,paint)
                return
            }
        }
        for(i in 0 until string.length) {
            val c=string[i]
            if(string.length>1){
                canvas.drawText(c.toString(),textSize*(0.25f+(textWidth-1.5f)/(string.length.toFloat()-1)*i),ypos, paint)
            }else{
                drawTextCenter(canvas,string,ypos,paint)
            }
        }
    }

    fun drawPass(canvas: Canvas, y: Float, paint: Paint) {
        canvas.drawLine((width / 2).toFloat(), y + KLView.textSize * 0.1f, (width / 2).toFloat(), y - KLView.textSize * 0.8f, paint)
        canvas.drawLine((width / 2).toFloat(), y + KLView.textSize * 0.1f, width / 2 + KLView.textSize * 0.6f, y - KLView.textSize * 0.5f, paint)

    }

    fun drawNoVia(canvas: Canvas, y: Float, paint: Paint) {
        canvas.drawLine(width * 0.4f, y + KLView.textSize * 0.1f, width * 0.4f, y - KLView.textSize * 0.8f, paint)
        canvas.drawLine(width * 0.6f, y + KLView.textSize * 0.1f, width * 0.6f, y - KLView.textSize * 0.8f, paint)

    }

    fun drawNoService(canvas: Canvas, y: Float, paint: Paint) {
        val dotSize = KLView.textSize * 0.1f
        canvas.drawOval(width * 0.4f - dotSize, y.toFloat() - KLView.textSize * 0.35f - dotSize, width * 0.4f + dotSize, y - KLView.textSize * 0.35f + dotSize, paint)
        canvas.drawOval(width * 0.6f - dotSize, y.toFloat() - KLView.textSize * 0.35f - dotSize, width * 0.6f + dotSize, y - KLView.textSize * 0.35f + dotSize, paint)
    }
    fun drawTime(canvas:Canvas,y:Float,time:Int,paint: Paint){
        val hh=(time/3600)%24
        val mm=(time/60)%60
        drawTextCenter(canvas,hh.toString()+String.format("%02d",mm),y, textPaint)
    }
    fun drawStop(canvas: Canvas,i:Int,y:Float,paint: Paint){
        val string=diaFile.getStation(i).getShortName(train.getStopNumber(if(i==0){if(direct==0){train.diaFile.getStation(i).downMain}else{train.diaFile.getStation(i).upMain}}else{i-1}))

        when(train.getStopType(i)){
            1->{
                textPaint.style = Paint.Style.STROKE
                canvas.drawOval(width / 2 - textSize * 1.1f, y - textSize * 0.9f, width / 2 + textSize * 1.1f, y + textSize * 0.1f, textPaint)
                textPaint.style = Paint.Style.FILL
                drawTextCenter(canvas,string,y-0.05f* textSize,paint)
            }
            2->{
                if(false) {
                    textPaint.color = Color.GRAY
                    textPaint.style = Paint.Style.STROKE
                    canvas.drawOval(width / 2 - textSize * 1.1f, y - textSize * 0.9f, width / 2 + textSize * 1.1f, y + textSize * 0.1f, textPaint)
                    textPaint.style = Paint.Style.FILL
                    drawTextCenter(canvas, string, y - 0.05f * textSize, paint)
                    textPaint.color = train.trainType.textColor.androidColor
                }else{
                    drawTextCenter(canvas,"↓", y - 0.05f * textSize, paint)
                }
            }

        }
    }
    fun setTrain(newTrain:AOdiaTrain){

    }

}
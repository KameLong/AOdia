package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaStationHistory
import com.kamelong.aodia.diadataOld.AOdiaStation
import com.kamelong.aodia.diadataOld.AOdiaTrain

/**
 * 駅名のViewを作成する
 */
class StationView(context: Context, val direct:Int, val diaFile: AOdiaDiaFile, override val xsize: Int) :KLView(context) {
    val borderFrag=ArrayList<Boolean>()
    var prefer=context.getSharedPreferences("AOdia-LineTrainTime", Context.MODE_PRIVATE)
    var editAllTime=prefer.getBoolean("editAllTime",false)
    var editAllStop=prefer.getBoolean("editAllStop",false)

    override var ysize:Int=0
    get(){
        var result=0.4* textSize
        for(station in diaFile.getStationList()){
            if(editAllTime||(station.getViewStyle(direct) and 0b01)!=0){
                result+= textSize
            }
            if(editAllTime||(station.getViewStyle(direct) and 0b10)!=0){
                result+= textSize
            }
            if(editAllTime||(station.getViewStyle(direct) and 0b11)==0b11){
                result+= (textSize*0.2).toInt()
            }
            if(editAllStop||(station.getViewStyle(direct) and 0b100)==0b100){
                result+= (textSize*1.2).toInt()
            }
            if(station.branchEnd()>=0){
                result+=(textSize*0.2).toInt()
            }
            if(station.branchStart()>=0){
                result+=(textSize*0.2).toInt()
            }
            borderFrag.add(false)

        }
        return result.toInt()

    }
    init{
        for(i in 0 until diaFile.stationNum){
            if(diaFile.getStation(i).branchStation>=0){
                if((direct==0) xor (i<diaFile.getStation(i).branchStation)){

                }else{

                }
            }

        }

    }
    override fun onDraw(canvas:Canvas?){
        if(canvas==null)return
        var linePos=0.1f* textSize
        val stationList=diaFile.getStationList().toMutableList()

        if(direct==1){
            stationList.reverse()
        }
        for(station in stationList){
            val viewStyle=station.getViewStyle(direct)
            var lineNum=0
            if(editAllTime||(viewStyle and 0b1) !=0)lineNum++
            if(editAllTime||(viewStyle and 0b10) !=0)lineNum++
            if(editAllStop||(viewStyle and 0b100) !=0)lineNum++
            if(station.branchStart()>=0){
                linePos+=(textSize*0.2f)
                canvas.drawLine(0f,linePos-0.1f* textSize,width.toFloat(),linePos-0.1f* textSize, blackBPaint)
            }
            linePos+= textSize*(lineNum*1.2f-0.2f)
            when(lineNum){
                1->drawTextFit(canvas,station.name,linePos.toFloat(), blackPaint)
                2->drawTextFit(canvas,station.name,linePos.toFloat()-0.5f* textSize, blackPaint)
                3->drawTextFit(canvas,station.name,linePos.toFloat()- textSize, blackBPaint)
            }
            if(station.branchEnd()>=0){
                linePos+=(textSize*0.2f)
                canvas.drawLine(0f,linePos-0.1f* textSize,width.toFloat(),linePos-0.1f* textSize, blackBPaint)
            }

        }
    }
    fun drawTextFit(canvas:Canvas,value:String,ypos:Float,paint: Paint){
        val textWidth=width.toFloat()/ textSize.toFloat()
        var string=value
        if(value.length>5){
            string=string.substring(0,5)
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
    fun postion(ypos: Float):Int{
        var linePos=0.1f* textSize
        val stationList=diaFile.getStationList().toMutableList()

        for(i in if(direct==0){0 until stationList.size}else{stationList.size-1 downTo 0}){
            val station=stationList[i]

            if(station.branchStart()>=0){
                linePos+=(textSize*0.2f)
            }
            val viewStyle=station.getViewStyle(direct)
            if(editAllTime||(viewStyle and 0b0010) ==0b0010){
                linePos+=textSize
            }
            if(linePos>ypos)return 3*i
            if(editAllTime||(viewStyle and 0b0011) ==0b0011){
                linePos+=textSize*0.2f
            }
            if((viewStyle and 0b0111) ==0b0110) {
                linePos += textSize * 0.2f
                linePos += textSize
            } else if (editAllStop||(viewStyle and 0b0100) == 0b0100) {
                linePos += textSize
                linePos += textSize * 0.2f
            }
            if(linePos>ypos)return 3*i+1

            if(editAllTime||(viewStyle and 0b0001) ==0b0001){
                linePos+=textSize

            }

            if(station.branchEnd()>=0){
                linePos+=(textSize*0.2f)
            }
            if(linePos>ypos)return 3*i+2
        }
        return -1
    }
    fun reNewPreference(){
        editAllTime=prefer.getBoolean("editAllTime",false)
        editAllStop=prefer.getBoolean("editAllStop",false)

        invalidate()
    }

}
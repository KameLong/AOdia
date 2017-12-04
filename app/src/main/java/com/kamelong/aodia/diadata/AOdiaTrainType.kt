package com.kamelong.aodia.diadata

import com.kamelong.tool.Color
import com.kamelong.tool.Font

/**
 * Created by user2017 on 2017/12/04.
 */
interface AOdiaTrainType {
    var name:String
    var shortName:String
    var textColor:Color
    var diaColor:Color

    var lineStyle:Int
    var lineBold:Boolean
    var showStop:Boolean
    var font:Font

}
package com.kamelong.OuDia2nd

/**
 * Created by kame on 2017/12/16.
 */
class Test {
    fun splitTest(){
        val strList=ArrayList<String>()
        for(i in 0 until 1000000){
            val str=StringBuffer()
            for(j in 0 until 10) {
                str.append((Math.random() * 80 + 35).toChar())
            }
            strList.add(str.toString())
//            println(str)
        }
        val str2=ArrayList<String>()
        println("start")
        val time=System.currentTimeMillis()
        for(i in 0 until 1000000){
           str2.add( strList[i].split("a")[0])
        }
        println("end"+(System.currentTimeMillis()-time))
            println(str2[(Math.random()*10000).toInt()])
    }

}
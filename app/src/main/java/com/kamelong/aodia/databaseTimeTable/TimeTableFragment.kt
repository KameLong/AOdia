package com.kamelong.aodia.databaseTimeTable

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

import com.kamelong.aodia.AOdiaFragment
import com.kamelong.aodia.R
import com.kamelong.aodia.SdLog

import java.util.ArrayList

import com.kamelong.aodia.diadata.AOdiaService
import java.io.File
import kotlin.concurrent.thread

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
 */

/**
 * 路線時刻表のFragment。
 * 一つの路線時刻表（上り、下りで独立している）につき一つの生成が必要
 * @author kamelong
 */
class TimeTableFragment : AOdiaFragment() {
    internal lateinit var database: SQLiteDatabase
    internal var routeID = ArrayList<Int>()
    internal var direction = ArrayList<Int>()
    internal var calendarID = 0
    internal lateinit var service :AOdiaService
    internal var fling = false
    internal lateinit var stationView:StationViewGroup
    internal lateinit var trainLinear:TrainListViewGroup
    private val handler = Handler()

    var scrollX=0f
    var scrollY=0f


    internal val gesture = GestureDetector(getActivity(),
            object : GestureDetector.SimpleOnGestureListener() {

                private val flingV = 0f

                override fun onDown(motionEvent: MotionEvent): Boolean {
                    fling = false
                    return true
                }

                override fun onDoubleTap(event: MotionEvent): Boolean {
                    return false
                }

                override fun onLongPress(motionEvent: MotionEvent) {
                }

                override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, vx: Float, vy: Float): Boolean {
                    this@TimeTableFragment.scrollBy(vx, vy)
                    return false
                }

                override fun onFling(e1: MotionEvent, e2: MotionEvent, v1: Float, v2: Float): Boolean {
                    val flingV = -v1
                    fling = true
                    Thread(Runnable {

                        while (fling) {
                            try {
                                Thread.sleep(16)
                                this@TimeTableFragment.scrollByThread((flingV * 16 / 1000f), 0f)
                            } catch (e: Exception) {
                                fling = false
                                SdLog.log(e)
                            }

                        }
                    }).start()
                    return false
                }
            })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val serviceID = arguments.getInt("serviceID")
        calendarID = arguments.getInt("calendarID")
        try {
            service = aOdiaActivity.getService(serviceID)
            database=SQLiteDatabase.openOrCreateDatabase(File(service.dataBaseDirectory+service.dataBaseName), null)
        }catch (e:Exception){
            e.printStackTrace()
            aOdiaActivity.killFragment(this)
            return View(activity)
        }


        //Fragmentのレイアウトxmlファイルを指定し、メインのViewをfragmentContainerに代入する（つまり消すな）
        fragmentContainer = inflater.inflate(R.layout.time_table_new, container, false)
        //このFragment上でのタッチジェスチャーの管理


        fragmentContainer.setOnTouchListener { v, event -> gesture.onTouchEvent(event) }

        return fragmentContainer
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onStart() {
        super.onStart()
        try {


        } catch (e: Exception) {
            SdLog.log(e)
        }

    }

    private fun init() {
        try {
            routeID=service.routeID
            direction=service.direction
            val stationFrame = findViewById(R.id.stationFrame) as FrameLayout
            stationFrame.removeAllViews()
            stationView= StationViewGroup(this)
            stationFrame.addView(stationView)
            trainLinear= TrainListViewGroup(this,10)
            val trainFrame = findViewById(R.id.trainFrame) as FrameLayout
            trainFrame.addView(trainLinear)



        } catch (e: Exception) {
            SdLog.log(e)
        }

    }
    private fun scrollByThread(dx: Float, dy: Float) {
        scrollX += dx
        scrollY += dy
        handler.post(Runnable {
            trainLinear.scrollBy(dx)
            scrollTo()
        })
    }
    private fun scrollBy(dx: Float, dy: Float) {
        scrollX += dx
        scrollY += dy
        trainLinear.scrollBy(dx)
        scrollTo()
    }

    private fun scrollTo() {
        try {
            if(scrollY>stationView.maxScroll){
                scrollY=stationView.maxScroll.toFloat()
            }
            if(scrollY<0){
                scrollY=0f
            }
            try {
                stationView.scrollTo(scrollY.toInt())
                trainLinear.yScrollTo(scrollY.toInt())
                return
            } catch (e: Exception) {
                SdLog.log(e)
            }
        } catch (e: Exception) {
            SdLog.log(e)
            fling = false
        }

    }

    fun sortTrain(station: Int) {
        if (station >= 0 && station < diaFile.station.stationNum) {
            //timeTable.sortTrain(station)
            this.init()
        }

    }

    fun goTrain(trainNum: Int) {
        Thread(Runnable {
            var trainTimeLinear: LinearLayout? = null
            while (true) {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    SdLog.log(e)
                    break
                }

                try {
                    break
                } catch (e: Exception) {
                    SdLog.log(e)

                }

            }
        }).start()


    }

    override fun findViewById(id: Int): View? {
        try {
            return fragmentContainer.findViewById(id)
        } catch (e: Exception) {
            SdLog.log(e)
        }

        return null
    }

    override fun fragmentName(): String {
        return "時刻表"
    }

    override fun fragmentHash(): String {
        return ""
    }
}

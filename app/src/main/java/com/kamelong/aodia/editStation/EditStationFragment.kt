package com.kamelong.aodia.editStation

import android.app.Fragment
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.kamelong.OuDia2nd.DiaFile
import com.kamelong.OuDia2nd.Station
import com.kamelong.aodia.AOdiaActivity
import com.kamelong.aodia.AOdiaFragmentInterface
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaStation
import java.util.*
import kotlin.collections.ArrayList
import com.kamelong.aodia.SdLog


/**
 * 駅編集用Fragment
 * 駅編集Fragment内での編集はTrainには反映されないので、onStop時には別途処理しないといけない。
 * アプリ回転や画面回転などの不慮のFragment破棄についてはまだ未検証
 * 
 */
class EditStationFragment : Fragment(), AOdiaFragmentInterface, CopyPasteInsertAddDeleteDialog.CopyPasteInsertAddDeleteInterface {
    override fun onClickCopyButton() {
        stationCopyList = ArrayList<Int>()
        for (i in 0 until stationSelected.size) {
            if (stationSelected[i]) {
                stationCopyList.add(i)
            }
        }
        for (i in 0 until stationSelected.size) {
            stationLinear.getChildAt(i).isSelected = false
        }
    }

    override fun onClickPasteButton() {
        var insertIndex = -1
        for (i in 0 until stationSelected.size) {
            if (stationSelected[i]) {
                insertIndex = i
                break
            }
        }
        for (i in 0 until stationCopyList.size) {
            addStation(insertIndex + 1+i, stationList[ stationCopyList[i]].station.clone())
            for (j in 0 until stationCopyList.size) {
                if (insertIndex < stationCopyList[j]) {
                    stationCopyList[j]++
                }
            }
        }
        makeStationList()
    }

    override fun onClickInsertButton() {
        stationCopyList = ArrayList<Int>()
        var insertIndex = -1
        for (i in 0 until stationSelected.size) {
            if (stationSelected[i]) {
                insertIndex = i
                break
            }
        }
        if (insertIndex != -1) {
            addStation(insertIndex)
        }
        makeStationList()

    }

    override fun onClickAddButton() {
        stationCopyList = ArrayList<Int>()
        var insertIndex = -1
        for (i in 0 until stationSelected.size) {
            if (stationSelected[i]) {
                insertIndex = i
                break
            }
        }
        if (insertIndex != -1) {
            addStation(insertIndex + 1)
        }
        makeStationList()

    }

    override fun onClickDeleteButton() {
        stationCopyList = ArrayList<Int>()
        var i = 0
        while (i < stationSelected.size) {
            if (stationSelected[i]) {
                if (deleteStaiton(i)) {
                    i--
                }else{
                    SdLog.toast(stationList[i].station.name + "駅は削除できません")

                }
            }
            i++
        }
            makeStationList()

    }

    override var fragment = this as Fragment
    override lateinit var diaFile: AOdiaDiaFile
    override lateinit var aodiaActivity: AOdiaActivity

    var fileIndex = 0
    lateinit var fragmentContainer: View
    lateinit var stationLinear: LinearLayout

    val stationSelected = ArrayList<Boolean>()
    var stationCopyList = ArrayList<Int>()

    var stationList=ArrayList<AOdiaStationEdit>()


    val backStack = ArrayDeque<AOdiaStationHistory>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        try {
            aodiaActivity = getActivity() as AOdiaActivity
            fragment = this
            val bundle = arguments
            fileIndex = bundle.getInt("fileNum")
            diaFile = aodiaActivity.diaFiles[fileIndex]
            for(i in 0 until diaFile.stationNum){
                stationList.add(AOdiaStationEdit(diaFile.getStation(i).clone()))

            }
        } catch (e: Exception) {
            e.printStackTrace()
            //activity.killFragment(this)
        }
        fragmentContainer = inflater.inflate(R.layout.edit_station_fragment, container, false)
        fragmentContainer.setFocusableInTouchMode(true)
        fragmentContainer.requestFocus();
        fragmentContainer.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                println("key")

                return if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    val frameLayout = fragmentContainer.findViewById<FrameLayout>(R.id.frameLayout)
                    if(frameLayout.visibility == View.VISIBLE){
                        val editor=frameLayout.getChildAt(0)as EditStation
                        closeStationEdit(false,editor.index,editor.editStation)
                    }else{
                        back()
                    }
                    true
                } else false
            }
        })
        return fragmentContainer
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stationLinear = fragmentContainer.findViewById(R.id.stationLinear)
        for (i in 0 until diaFile.stationNum) {
            stationSelected.add(false)
        }

        makeStationList()
        fragmentContainer.setOnLongClickListener {
            var copyValue = false
            for (boolean in stationSelected) {
                if (boolean) {
                    copyValue = true
                }
            }
            val dialog = CopyPasteInsertAddDeleteDialog(aodiaActivity, this, copyValue && (stationCopyList.size != 0)).show()
            true
        }
    }

    /**
     * stationListLineaを作成する
     */
    fun makeStationList() {
        stationLinear.removeAllViews()

        for (i in 0 until stationList.size) {
            val stationView= EditStaitonView(getActivity(), this, stationList[i].station, i)
            stationView.isSelected=stationSelected[i]
            stationLinear.addView(stationView)
        }
    }

    /**
     * 新規駅を追加する
     */
    fun addStation(index: Int) {
        val station = Station(diaFile as DiaFile)
        station.name = "new"
        station.trackName.add("１番線")
        station.trackName.add("２番線")
        station.trackRyakusyou.add("1")
        station.trackRyakusyou.add("2")
        station.upMain = 2
        station.downMain = 2
        addStation(index, station)
    }

    /**
     * 既存の駅を追加する
     */
    fun addStation(index: Int, station: AOdiaStation) {
        val history = AOdiaStationHistory()
        history.addIndex = index
        backStack.addLast(history)
        stationList.add(index, AOdiaStationEdit(station))
        stationSelected.add(index, false)
        for(i in index until stationList.size){
            if(stationList[i].station.branchStation>=index)stationList[i].station.branchStation++
            if(stationList[i].station.loopStation>=index)stationList[i].station.loopStation++
        }

    }

    /**
     * 駅を削除する
     */
    fun deleteStaiton(index: Int): Boolean {
        for (station in stationList) {
            if (station.station.branchStation == index || station.station.loopStation == index) {
                return false
            }
        }
        val history = AOdiaStationHistory()
        history.station = stationList[index]
        history.deleteIndex = index
        backStack.addLast(history)
        stationList.remove(stationList[index])
        stationSelected.remove(stationSelected[index])
        for(i in index until stationList.size){
            if(stationList[i].station.branchStation>index)stationList[i].station.branchStation--
            if(stationList[i].station.loopStation>index)stationList[i].station.loopStation--
        }
        return true
    }

    /**
     * StationEditerを起動する
     */
    fun openStationEdit(index: Int) {
        val frameLayout = fragmentContainer.findViewById<FrameLayout>(R.id.frameLayout)
        val history = AOdiaStationHistory()
        frameLayout.addView(EditStation(this, index))
        frameLayout.visibility = View.VISIBLE
        history.changeIndex = index
        history.station = stationList[index].clone()
        backStack.addLast(history)
    }

    /**
     * StationEditerを閉じる
     * 引数は駅を更新する時はtrue更新しないときはfalse
     */
    fun closeStationEdit(renewFrag: Boolean,index:Int,station: AOdiaStationEdit) {
        val frameLayout = fragmentContainer.findViewById<FrameLayout>(R.id.frameLayout)
        frameLayout.removeAllViews()
        frameLayout.visibility = View.GONE
        if (renewFrag) {
            stationList[index]=station
        }else{
            backStack.pollLast()
        }
        makeStationList()
    }
    fun back(){
        val history=backStack.pollLast()
        if(history==null){
            SdLog.toast("これ以上駅を戻すことはできません")
        }else if(history.deleteIndex>=0){
            stationList.add(history.deleteIndex, history.station!!)
            stationSelected.add(history.deleteIndex, false)
            for(i in history.deleteIndex until stationList.size){
                if(stationList[i].station.branchStation>=history.deleteIndex)stationList[i].station.branchStation++
                if(stationList[i].station.loopStation>=history.deleteIndex)stationList[i].station.loopStation++
            }


        }else if(history.addIndex>=0){
            stationList.remove(stationList[history.addIndex])
            stationSelected.remove(stationSelected[history.addIndex])
            for(i in history.addIndex until stationList.size){
                if(stationList[i].station.branchStation>history.addIndex)stationList[i].station.branchStation--
                if(stationList[i].station.loopStation>history.addIndex)stationList[i].station.loopStation--
            }

        }else if(history.changeIndex>=0){
            stationList[history.changeIndex]=history.station!!
        }
        makeStationList()
        stationCopyList=ArrayList<Int>()


    }

    override fun onStop() {
        super.onStop()
        for(history in backStack){
            if(history.addIndex>=0){
                diaFile.addStationRenew(history.addIndex)

            }
            if(history.deleteIndex>=0){
                diaFile.deleteStation(history.deleteIndex)
            }
            if(history.changeIndex>=0){
                diaFile.setStationRenew(history.changeIndex,history.station!!.editStopList)

            }
        }
        diaFile.resetStation()
        for(i in 0 until stationList.size){
            diaFile.setStation(stationList[i].station)
        }
    }


}
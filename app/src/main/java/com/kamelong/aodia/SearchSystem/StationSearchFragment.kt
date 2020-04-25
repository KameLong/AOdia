package com.kamelong.aodia.SearchSystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.annotation.NonNull
import com.kamelong.OuDia.LineFile
import com.kamelong.aodia.AOdia
import com.kamelong.aodia.AOdiaFragmentCustom
import com.kamelong.aodia.KLdatabase.KLdetabase
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R
import com.kamelong.tool.SDlog
import kotlinx.android.synthetic.main.search_framgent.*

class StationSearchFragment():AOdiaFragmentCustom(){
    companion object{
        const val SEARCH_WORD="searchWord"
    }
    private lateinit var fragmentContainer:View
    private lateinit var searchWord:String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        try {//まずBundleを確認し、fileNum,diaIndex,directを更新する
            val bundle = arguments
            searchWord = bundle!!.getString(SEARCH_WORD, "")
        } catch (e: Exception) {
            SDlog.log(e)
        }

        fragmentContainer = inflater.inflate(R.layout.search_framgent, container, false)


        return fragmentContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stationListAdapter=StationAdapter(activity as MainActivity,searchWord)
        val stationListView=fragmentContainer.findViewById<ListView>(R.id.stationList)
        stationListView.adapter=stationListAdapter
    }

    override fun getHash(): String {
        return "StationSearchFragment"
    }

    override fun getName(): String {
        return "検索"
    }

    override fun getLineFile(): LineFile? {
        return null
    }

}
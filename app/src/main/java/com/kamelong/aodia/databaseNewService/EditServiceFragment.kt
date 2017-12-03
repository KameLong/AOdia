package com.kamelong.aodia.databaseNewService

import android.app.Fragment
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kamelong.aodia.AOdiaFragment
import com.kamelong.aodia.R
import java.io.File
import com.kamelong.aodia.R.id.spinner
import android.widget.Spinner
import android.widget.ArrayAdapter
import com.toptoche.searchablespinnerlibrary.SearchableSpinner


/**
 * Created by kame on 2017/11/28.
 */
class EditServiceFragment : AOdiaFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val serviceID = arguments.getInt("serviceID")

        fragmentContainer = inflater.inflate(R.layout.edit_service_fragment, container, false)
        //このFragment上でのタッチジェスチャーの管理

        val adapter = ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
// Adapterにアイテムを追加
        adapter.add("red")
        adapter.add("green")
        adapter.add("blue")
        val spinner = findViewById(R.id.spinner) as SearchableSpinner

// SpinnerにAdapterを設定
        spinner.adapter = adapter
        spinner.setTitle("路線選択")
        spinner.setPositiveButton("決定")


        return fragmentContainer
    }

}
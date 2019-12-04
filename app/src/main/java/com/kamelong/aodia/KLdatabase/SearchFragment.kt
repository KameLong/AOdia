package com.kamelong.aodia.KLdatabase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kamelong.OuDia.LineFile
import com.kamelong.aodia.AOdiaFragmentCustom

class SearchFragment : AOdiaFragmentCustom() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun getLineFile(): LineFile?{
        return null
    }
    override fun getName(): String {
        return "時刻表検索"
    }

}
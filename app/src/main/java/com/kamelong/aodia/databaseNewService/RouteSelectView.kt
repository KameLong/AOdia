package com.kamelong.aodia.databaseNewService

import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

/**
 * Created by kame on 2017/12/01.
 */
class RouteSelectView(context: Context?) : LinearLayout(context) {
    val spinner=SearchableSpinner(context)
    val changeDirectButton=Button(context)
    init{
        orientation= HORIZONTAL
        gravity=Gravity.CENTER_VERTICAL
        addView(spinner)
        addView(changeDirectButton)
    }
}
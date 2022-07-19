package com.kamelong.aodia.TimeTable

import android.app.Dialog
import android.widget.Button
import android.widget.LinearLayout
import com.kamelong.OuDia.Train
import com.kamelong.aodia.MainActivity

class OpenLineFileSelector(activity:MainActivity,listener:OnLineFileOpenSelect):Dialog(activity){
    init{
        val layout=LinearLayout(activity)
        layout.orientation=LinearLayout.VERTICAL
        setContentView(layout)

        val openNormal=Button(context)
        openNormal.setText("新しい路線として開く")
        openNormal.setOnClickListener { listener.openAsNewLine();this.dismiss() }
        val appendDown=Button(context)
        appendDown.setText("下り方向に挿入する")
        appendDown.setOnClickListener { listener.openAsIncludeLine(Train.DOWN);this.dismiss() }
        val appendUp=Button(context)
        appendUp.setText("上り方向に挿入する")
        appendUp.setOnClickListener { listener.openAsIncludeLine(Train.UP);this.dismiss() }

        layout.addView(openNormal)
        layout.addView(appendDown)
        layout.addView(appendUp)
    }

}
interface OnLineFileOpenSelect{
    fun openAsNewLine();
    fun openAsIncludeLine(direction:Int);
}

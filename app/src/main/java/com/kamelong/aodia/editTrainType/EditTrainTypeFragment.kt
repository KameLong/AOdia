package com.kamelong.aodia.editTrainType

import android.app.Fragment
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.kamelong.aodia.AOdiaActivity
import com.kamelong.aodia.AOdiaFragment
import com.kamelong.aodia.AOdiaFragmentInterface
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.editStation.CopyPasteInsertAddDeleteDialog

class EditTrainTypeFragment :Fragment(),AOdiaFragmentInterface{
    override var fragment=this as Fragment
    override lateinit var diaFile: AOdiaDiaFile
    override lateinit var aodiaActivity: AOdiaActivity
    lateinit var fragmentContainer: View
    lateinit var typeListLinear:LinearLayout
    var fileIndex=0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        super.onCreateView(inflater, container, savedInstanceState)
        try {
            aodiaActivity = getActivity() as AOdiaActivity
            fragment = this
            val bundle = arguments
            fileIndex = bundle.getInt("fileNum")
            diaFile = aodiaActivity.diaFiles[fileIndex]
        } catch (e: Exception) {
            e.printStackTrace()
            //activity.killFragment(this)
        }
        fragmentContainer = inflater.inflate(R.layout.edit_traintype_fragment, container, false)
        fragmentContainer.setFocusableInTouchMode(true)
        fragmentContainer.requestFocus();
        fragmentContainer.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                println("key")

                return if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    val frameLayout = fragmentContainer.findViewById<FrameLayout>(R.id.frameLayout)
                    true
                } else false
            }
        })
        return fragmentContainer
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        typeListLinear = fragmentContainer.findViewById(R.id.typeListLinear)
        for (i in 0 until diaFile.trainTypeNum) {
            typeListLinear.addView(EditTrainTypeView(activity,diaFile.getTrainType(i)))

        }

        fragmentContainer.setOnLongClickListener {
            true
        }
    }

}
package com.kamelong.aodia

import android.app.Fragment
import android.view.View
import android.widget.TextView

import com.kamelong.aodia.diadata.AOdiaDiaFile

/**
 * AOdia内のタブとして使われるFragment
 * 画面分割を可能とする
 */

abstract class AOdiaFragment : Fragment(), AOdiaFragmentInterface {

    protected var fragmentContainer: View? = null
    override lateinit var diaFile: AOdiaDiaFile
    override var aodiaActivity: AOdiaActivity
        get() =getActivity() as AOdiaActivity
        set(value) {}
    override var fragment: Fragment
        get() = this
        set(fragment) {

        }
    override fun onStart() {
        super.onStart()
        (getActivity().findViewById<View>(R.id.titleView) as TextView).text = fragmentName()


    }
    fun getAOdiaActivity():AOdiaActivity{
        return aodiaActivity
    }

    override fun onStop() {

        super.onStop()
    }

    protected open fun findViewById(id: Int): View {
        return fragmentContainer!!.findViewById(id)
    }

    override fun fragmentName(): String {
        return ""
    }

    override fun fragmentHash(): String {
        return ""
    }

}

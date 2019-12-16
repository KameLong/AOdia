package com.kamelong.aodia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.kamelong.OuDia.LineFile
import com.kamelong.tool.SDlog

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
class PayFragment : AOdiaFragmentCustom() {
    private lateinit var fragmentContainer: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        try {
        } catch (e: Exception) {
            SDlog.toast("Error(CommentFragment-onCreateView-E1)")
            SDlog.log(e)
        }
        fragmentContainer = inflater.inflate(R.layout.pay_fragment, container, false)
        return fragmentContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val payment=(context as MainActivity).payment
        if(payment.buyCheck("010")){
            fragmentContainer.findViewById<Button>(R.id.button5).visibility=View.GONE
            fragmentContainer.findViewById<Button>(R.id.button6).visibility=View.VISIBLE
        }else{
            fragmentContainer.findViewById<Button>(R.id.button5).setOnClickListener {payment.buy("010")  }

        }
        fragmentContainer.findViewById<Button>(R.id.button2).setOnClickListener {
            if(payment.buyCheck("011")){
                payment.use()
            }
            payment.buy("011")

        }

    }

    override fun getName(): String {
        return "開発者に寄付をする"
    }

    override fun getLineFile(): LineFile {
        return lineFile!!
    }
}

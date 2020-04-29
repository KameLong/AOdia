package com.kamelong.aodia

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.kamelong.OuDia.LineFile
import java.util.*

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
class Pay2Fragment : AOdiaFragmentCustom() {
    val FRAGMENT_NAME = "Pay2Fragment"

    private lateinit var fragmentContainer: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        fragmentContainer = inflater.inflate(R.layout.kifu, container, false)
        return fragmentContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val payment=(context as MainActivity).payment

        fragmentContainer.findViewById<Button>(R.id.button5).setOnClickListener {
            if(payment.buyCheck("011")){
                payment.use()
            }
            payment.buy("011")

        }
        fragmentContainer.findViewById<Button>(R.id.button6).setOnClickListener {
            if(payment.buyCheck("013")){
                payment.use()
            }
            payment.buy("013")

        }
        fragmentContainer.findViewById<Button>(R.id.button7).setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            val month = calendar[Calendar.YEAR].toString() + "" + calendar[Calendar.MONTH]
            val pref = PreferenceManager.getDefaultSharedPreferences(activity)
            pref.edit().putBoolean(month, true).apply()

            aOdia.killFragment(this)

        }

    }

    override fun getHash(): String {
        return FRAGMENT_NAME
    }

    override fun getName(): String {
        return "開発者に寄付をする"
    }

    override fun getLineFile(): LineFile {
        return lineFile!!
    }
}

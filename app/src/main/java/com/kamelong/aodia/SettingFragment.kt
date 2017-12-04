package com.kamelong.aodia

import android.app.Fragment
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import com.kamelong.OuDia2nd.DiaFile

import com.kamelong.aodia.diadata.AOdiaDiaFile

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
/**
 * PreferenceFragmentを継承したクラス
 * 個々で定義したPreferenceのリソースを設定します
 */
class SettingFragment : android.preference.PreferenceFragment(), AOdiaFragmentInterface {
    private var payment: Payment? = null
    override var fragment: Fragment
        get() = this
        set(fragment) {

        }
    override var diaFile: AOdiaDiaFile
        get() = DiaFile(getActivity())
        set(aOdiaDiaFile) {

        }

    override var aodiaActivity: AOdiaActivity
        get() = super.getActivity() as AOdiaActivity
        set(aOdiaActivity) {

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        payment = (getActivity() as AOdiaActivity).payment

        addPreferencesFromResource(R.xml.activity_settings)

        try {
            findPreference("001").onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                if (payment!!.buyCheck("001")) {
                    (preference as CheckBoxPreference).isChecked = true
                    preference.isEnabled = false
                } else {
                    payment!!.buy("001")
                    if (payment!!.buyCheck("001")) {
                        (preference as CheckBoxPreference).isChecked = true
                        preference.isEnabled = false
                    }
                }
                false
            }

        } catch (e: Exception) {
            SdLog.log(e)
        }

    }

    override fun onResume() {
        super.onResume()
        findPreference("001").isEnabled = !payment!!.buyCheck("001")
        (findPreference("001") as CheckBoxPreference).isChecked = payment!!.buyCheck("001")
    }

    override fun onStop() {
        try {
            (getActivity() as AOdiaActivity).onCloseSetting()
            //Intent intent = new Intent();
            //intent.setClass(getActivity(), getActivity().getClass());
            //getActivity().startActivity(intent);
        } catch (e: Exception) {
            SdLog.log(e)
        }

        super.onStop()
    }

    override fun fragmentName(): String {
        return "設定"
    }

    override fun fragmentHash(): String {
        return "setting"
    }
}
package com.kamelong.aodia;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;

import com.kamelong.aodia.diadata.AOdiaDiaFile;
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
public class SettingFragment extends android.preference.PreferenceFragment implements AOdiaFragmentInterface{
    private Payment payment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        payment=((AOdiaActivity)getActivity()).getPayment();

        addPreferencesFromResource(R.xml.activity_settings);

        try {
            findPreference("001").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o) {

                            if(payment.buyCheck("001")){
                                ((CheckBoxPreference) preference).setChecked(true);
                                ((CheckBoxPreference) preference).setEnabled(false);
                            }else{
                                payment.buy("001");
                                if(payment.buyCheck("001")){
                                    ((CheckBoxPreference) preference).setChecked(true);
                                    ((CheckBoxPreference) preference).setEnabled(false);
                                }
                            }
                            return false;
                        }

                    });

        }catch(Exception e){
            SdLog.log(e);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        (findPreference("001")).setEnabled(!payment.buyCheck("001"));
        ((CheckBoxPreference) findPreference("001")).setChecked(payment.buyCheck("001"));
    }
    @Override
    public void onStop(){
        try {
            ((AOdiaActivity)getActivity()).onCloseSetting();
            //Intent intent = new Intent();
            //intent.setClass(getActivity(), getActivity().getClass());
            //getActivity().startActivity(intent);
        }catch(Exception e){
            SdLog.log(e);
        }
        super.onStop();
    }

    @Override
    public String fragmentName() {
        return "設定";
    }

    @Override
    public String fragmentHash() {
        return "setting";
    }
    @Override
    public Fragment getFragment(){
        return this;
    }
    @Override
    public AOdiaDiaFile getDiaFile(){
        return null;
    }
}
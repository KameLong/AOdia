package com.fc2.web.kamelong.aodia;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
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
public class PreferenceFragment extends android.preference.PreferenceFragment {
    Payment payment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        payment=new Payment(getActivity());

        addPreferencesFromResource(R.xml.activity_settings);
        ((CheckBoxPreference) findPreference("item001")).setChecked(false);
        if(true){
            return;
        }
        try {
            findPreference("item001").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o) {
                            if(((CheckBoxPreference)preference).isChecked()){
                                ((CheckBoxPreference) preference).setChecked(false);
                            }else{
                                if(payment.buyCheck("item001")){
                                    ((CheckBoxPreference) preference).setChecked(true);
                                    ((CheckBoxPreference) preference).setEnabled(false);
                                }else{
                                    payment.buy("item001");
                                    if(payment.buyCheck("item001")){
                                        ((CheckBoxPreference) preference).setChecked(true);
                                        ((CheckBoxPreference) preference).setEnabled(false);
                                    }
                                }
                            }
                            return false;
                        }

                    });
            findPreference("paymentClear").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o) {
                            payment.use();
                            ((CheckBoxPreference) findPreference("item001")).setChecked(false);
                            ((CheckBoxPreference) findPreference("item001")).setEnabled(true);
                            return false;
                        }

                    });

            findPreference("paymentPass").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o) {
                            if (((CheckBoxPreference) preference).isChecked()) {
                                ((CheckBoxPreference) preference).setChecked(false);
                                ((CheckBoxPreference) findPreference("paymentClear")).setEnabled(true);
                            } else {
                                ((CheckBoxPreference) preference).setChecked(true);
                                ((CheckBoxPreference) findPreference("item001")).setChecked(true);
                                ((CheckBoxPreference) findPreference("item001")).setEnabled(false);
                                ((CheckBoxPreference) findPreference("paymentClear")).setEnabled(false);
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
        //(findPreference("item001")).setEnabled(!((CheckBoxPreference) findPreference("item001")).isChecked());
    }
    @Override
    public void onStop(){
        try {
            ((MainActivity)getActivity()).setting();
            //Intent intent = new Intent();
            //intent.setClass(getActivity(), getActivity().getClass());
            //getActivity().startActivity(intent);
        }catch(Exception e){
            SdLog.log(e);
        }
        super.onStop();
    }
}
package com.kamelong.aodia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.DiagramFragment.DiagramDefaultView;
import com.kamelong.aodia.TimeTable.TimeTableDefaultView;
import com.kamelong.aodia.detabase.AOdiaDetabase;
import com.kamelong.tool.SDlog;

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
public class SettingFragment extends PreferenceFragmentCompat implements AOdiaFragment {
    public static final String FRAGMENT_NAME="SettingFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ((TextView) getActivity().findViewById(R.id.titleView)).setText(getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Payment payment=((MainActivity)getContext()).payment;

        addPreferencesFromResource(R.xml.activity_settings);
        findPreference("textsize").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try{


                    TimeTableDefaultView.setTextSize(Integer.parseInt((String) newValue));
                    DiagramDefaultView.setTextSize(Integer.parseInt((String) newValue));
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    pref.edit().putString("textsize",(String) newValue).apply();
                }catch (NumberFormatException e){
                    SDlog.toast("数字を入力してください");
                }

                return false;
            }
        });
        findPreference("timetableStationWidth").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try{

                    TimeTableDefaultView.setStationWidth(Integer.parseInt((String) newValue));
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    pref.edit().putString("timetableStationWidth",(String) newValue).apply();
                }catch (NumberFormatException e){
                    SDlog.toast("数字を入力してください");
                }
                return false;
            }
        });
        findPreference("diagramStationWidth").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try{
                    DiagramDefaultView.setStationWidth(Integer.parseInt((String) newValue));
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    pref.edit().putString("diagramStationWidth",(String) newValue).apply();

                }catch (NumberFormatException e){
                    SDlog.toast("数字を入力してください");
                }
                return false;
            }
        });

        final CheckBoxPreference resetDatabase=findPreference("resetDatabase");
        resetDatabase.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try{
                    getActivity().deleteDatabase(AOdiaDetabase.DETABASE_NAME);
                    ((MainActivity)getActivity()).getAOdia().database=new AOdiaDetabase(getContext());
                    SDlog.toast("内部データの削除を行いました");
                }catch(Exception  e){
                    SDlog.toast("内部データの削除に失敗しました");
                }
                resetDatabase.setChecked(false);
                return false;
            }
        });
        final CheckBoxPreference sendLog=findPreference("send_log");
        sendLog.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("send_log",false));
        final CheckBoxPreference useBeta=findPreference("useOld");
        useBeta.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                useBeta.setChecked(false);
                Intent intent = new Intent(getContext(), com.kamelong2.aodia.AOdiaActivity.class);
                startActivity(intent);
                return false;
            }
        });


//        final CheckBoxPreference a=findPreference("aaa");
//        a.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
////                    if (payment.buyCheck("010")) {
////                        ((CheckBoxPreference) preference).setChecked(true);
////                        ((CheckBoxPreference) preference).setEnabled(false);
////                    } else {
////                        payment.buy("010");
////                        if (payment.buyCheck("010")) {
////                            ((CheckBoxPreference) preference).setChecked(true);
////                            ((CheckBoxPreference) preference).setEnabled(false);
////                        }
////                    }
//                payment.querySkuDetails("010");
//                return false;
//            }
//        });
//        findPreference("011").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//
//                    @Override
//                    public boolean onPreferenceClick(Preference preference) {
////                        payment.buy("011");
////                        if (payment.buyCheck("011")) {
//////                            ((CheckBoxPreference) preference).setChecked(true);
//////                            ((CheckBoxPreference) preference).setEnabled(false);
////                        }
//                        return false;
//                    }
//
//        });

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        try {
        } catch (Exception e) {
            SDlog.log(e);
        }
        super.onStop();
    }

    @NonNull
    @Override
    public String getName() {
        return "設定";
    }

    @Override
    public String getHash() {
        return FRAGMENT_NAME;
    }

    @Override
    public LineFile getLineFile() {
        return null;
    }
}
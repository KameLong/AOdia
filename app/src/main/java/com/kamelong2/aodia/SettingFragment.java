package com.kamelong2.aodia;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.kamelong.aodia.R;

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
public class SettingFragment extends PreferenceFragmentCompat {
    Payment payment;
        @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        payment=((AOdiaActivity)getActivity()).payment;

        addPreferencesFromResource(R.xml.old_activity_settings);
        final CheckBoxPreference useBeta=findPreference("useBeta");
        useBeta.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                useBeta.setChecked(false);
                Intent intent = new Intent(getContext(), com.kamelong.aodia.MainActivity.class);
                startActivity(intent);
                return false;
            }
        });


    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStop(){
        try {
            ((AOdiaActivity)getActivity()).onCloseSetting();
        }catch(Exception e){
            SDlog.log(e);
        }
        super.onStop();
    }
}
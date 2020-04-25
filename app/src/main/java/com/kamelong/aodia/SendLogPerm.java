package com.kamelong.aodia;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class SendLogPerm extends Dialog {
    public SendLogPerm(Context context, final SharedPreferences pref){
        super(context);
        setContentView(R.layout.sendlog);
        Button accept=findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.edit().putBoolean("send_log",true).putBoolean("send_log_action",true).apply();
                System.out.println(pref.getBoolean("send_log",false));

                dismiss();
            }
        });
        Button decline=findViewById(R.id.decrine);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                pref.edit().putBoolean("send_log_action",true).apply();
                dismiss();
            }
        });
    }
}

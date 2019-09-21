package com.kamelong.aodia.AOdiaIO;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kamelong.aodia.AOdiaData.LineFile;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * OuDiaデータベースにファイルを保存する
 * まだ実装していない
 */
public class FileSaveDatabase extends ScrollView {
    private static final String preferenceName = "OuDiaDataBase";
    private LineFile lineFile;

    public FileSaveDatabase(Context context) {
        this(context, null);
    }

    public FileSaveDatabase(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setLineFile(LineFile lineFile) {
        final SharedPreferences preferences = getContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        this.lineFile = lineFile;
        ((EditText) findViewById(R.id.mailAddress)).setText(preferences.getString("email", ""));
        ((EditText) findViewById(R.id.userName)).setText(preferences.getString("name", ""));
        findViewById(R.id.textView7).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.lisenceFrame).setVisibility(VISIBLE);
//                ((WebView)findViewById(R.id.lisenceView)).loadUrl();
            }
        });

        ((TextView) findViewById(R.id.textView8)).setText(lineFile.name);
        findViewById(R.id.submitButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((CheckBox) findViewById(R.id.checkbox)).isChecked()) {
                    SDlog.toast("利用規約に同意してください");
                    return;
                }
                String mail = ((EditText) findViewById(R.id.mailAddress)).getText().toString();
                if (!mail.contains("@") || mail.split("@")[0].length() == 0 || mail.split("@")[1].length() == 0 || mail.split("@", -1).length != 2) {
                    SDlog.toast("メールアドレスを正しく入力してください");
                    return;
                }
                preferences.edit().putString("email", mail).apply();
                String[] keywords = ((EditText) findViewById(R.id.keywords)).getText().toString().split(" ");
                String name = ((EditText) findViewById(R.id.userName)).getText().toString();
                if (name.length() == 0) {
                    SDlog.toast("ニックネームを正しく入力してください");
                    return;
                }
                preferences.edit().putString("name", name).apply();
                int year = 0;
                try {
                    year = Integer.parseInt(((EditText) findViewById(R.id.year)).getText().toString());
                } catch (Exception e) {
                    SDlog.toast("年代を正しく入力してください");
                    return;
                }
                if (year < 100 || year > 9999) {
                    SDlog.toast("年代を正しく入力してください");
                    return;
                }


            }
        });

    }

}

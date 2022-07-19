package com.kamelong.aodia.AOdiaIO;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * OuDiaデータベースで取得した路線情報を表示するためのAdapterです
 */
public class DatabaseListAdapter extends BaseAdapter {
    /**
     * 路線ごとのJSONリスト
     */
    ArrayList<JSONObject> jsonArray = new ArrayList<>();
    LayoutInflater layoutInflater = null;
    Context context = null;

    /**
     * 取得したJSONオブジェクトを用いて路線を表示する
     */
    public DatabaseListAdapter(Context context, JSONArray json) throws JSONException {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < json.length(); i++) {
            jsonArray.add(json.getJSONObject(i));
        }
    }

    @Override
    public int getCount() {
        return jsonArray.size();
    }

    @Override
    public Object getItem(int i) {
        jsonArray.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.fileselector_database_list, parent, false);

        try {
            ((TextView) convertView.findViewById(R.id.lineName)).setText(jsonArray.get(position).getString("lineName"));
            ((TextView) convertView.findViewById(R.id.user)).setText(jsonArray.get(position).getString("userName"));
            ((TextView) convertView.findViewById(R.id.type)).setText(jsonArray.get(position).getString("diaYear") + "年");
            Spinner keywordSpinner = convertView.findViewById(R.id.keyword);
            Spinner stationSpinner = convertView.findViewById(R.id.stationName);
            ArrayList<String> keywordList = new ArrayList<>();
            for (int i = 0; i < jsonArray.get(position).getJSONArray("keyword").length(); i++) {
                keywordList.add(jsonArray.get(position).getJSONArray("keyword").getString(i));
            }
            ArrayList<String> stationNameList = new ArrayList<>();
            for (int i = 0; i < jsonArray.get(position).getJSONArray("stationName").length(); i++) {
                stationNameList.add(jsonArray.get(position).getJSONArray("stationName").getString(i));
            }
            ArrayAdapter<String> keywordAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, keywordList);
            keywordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            keywordSpinner.setAdapter(keywordAdapter);
            ArrayAdapter<String> stationAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, stationNameList);
            stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stationSpinner.setAdapter(stationAdapter);
            convertView.findViewById(R.id.download).setOnClickListener(view -> {
                try {
                    final String url = "https://kamelong.com/OuDiaDataBase/files/" + jsonArray.get(position).getString("url");
                    final Handler handler = view.getHandler();
                    new Thread(() -> {
                        try {
                            URL downloadURL = new URL(url);
                            HttpURLConnection download = null;
                            download = (HttpURLConnection) downloadURL.openConnection();
                            download.connect();
                            final DataInputStream DATA_INPUT = new DataInputStream(download.getInputStream());
                            // 書き込み用ストリーム
                            try {
                                final FileOutputStream FILE_OUTPUT = new FileOutputStream(context.getExternalFilesDir(null).getPath() + "/" + jsonArray.get(position).getString("lineName").replace("/", "-") + ".oud");
                                final DataOutputStream DATA_OUT = new DataOutputStream(FILE_OUTPUT);

                                // 読み込みデータ単位
                                final byte[] BUFFER = new byte[4096];
                                // 読み込んだデータを一時的に格納しておく変数
                                int readByte = 0;

                                // ファイルを読み込む
                                while ((readByte = DATA_INPUT.read(BUFFER)) != -1) {
                                    DATA_OUT.write(BUFFER, 0, readByte);
                                }
                                // 各ストリームを閉じる
                                DATA_INPUT.close();
                                FILE_OUTPUT.close();
                                DATA_INPUT.close();
                                download.getInputStream().close();
                            } catch (Exception e) {
                                SDlog.log(e);
                                SDlog.toast("原因不明のエラーが発生しました");
                                return;
                            }

                            handler.post(() -> {
                                try {
                                    ((MainActivity) context).getAOdia().openFile(new File(context.getExternalFilesDir(null).getPath() + "/" + jsonArray.get(position).getString("lineName").replace("/", "-") + ".oud"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    System.out.println(url);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}

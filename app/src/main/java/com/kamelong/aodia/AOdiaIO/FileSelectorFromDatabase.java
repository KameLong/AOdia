package com.kamelong.aodia.AOdiaIO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * OuDiaデータベースからファイルを開くためのView
 */
public class FileSelectorFromDatabase extends LinearLayout {
    public FileSelectorFromDatabase(Context context) {
        this(context, null);
    }

    public FileSelectorFromDatabase(final Context context, AttributeSet attr) {
        super(context, attr);
        LayoutInflater.from(context).inflate(R.layout.fileselector_database, this);
        final Button openButton = findViewById(R.id.openButton);
        final Button closeButton = findViewById(R.id.closeButton);
        //LinearLayout(R.id.title)をタッチしても検索部分を開閉できるようにする
        findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (openButton.getVisibility() == View.VISIBLE) {
                    openButton.callOnClick();
                } else {

                    closeButton.callOnClick();
                }
            }
        });
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openButton.setVisibility(View.GONE);
                closeButton.setVisibility(View.VISIBLE);
                findViewById(R.id.search).setVisibility(View.VISIBLE);
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openButton.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.GONE);
                findViewById(R.id.search).setVisibility(View.GONE);

            }
        });
        Button startSearch = findViewById(R.id.startSearch);
        startSearch.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                final String stationName = ((EditText) findViewById(R.id.stationInput)).getText().toString();
                final String keyword = ((EditText) findViewById(R.id.keywordInput)).getText().toString();
                final boolean andSearch = ((CheckBox) findViewById(R.id.andCheck)).isChecked();
                final String startYear = ((EditText) findViewById(R.id.startYear)).getText().toString();
                final String endYear = ((EditText) findViewById(R.id.endYear)).getText().toString();
                final Handler handler = getHandler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String url = "https://kamelong.com/OuDiaDataBase/api/apiv1.php";
                            url += "?stationName=" + stationName;
                            url += "&keyword=" + keyword;
                            url += "&startYear=" + startYear;
                            url += "&endYear=" + endYear;
                            if (andSearch) {
                                url += "&andSearch=" + "1";
                            }
                            System.out.println(url);


                            URL con = new URL(url);
                            HttpsURLConnection connection = (HttpsURLConnection) con.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("Accept", "application/json");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.connect();
                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                // 通信に成功した
                                // テキストを取得する
                                final InputStream in = connection.getInputStream();
                                String encoding = connection.getContentEncoding();
                                if (null == encoding) {
                                    encoding = "UTF-8";
                                }
                                final InputStreamReader inReader = new InputStreamReader(in, encoding);
                                final BufferedReader bufReader = new BufferedReader(inReader);
                                String line = bufReader.readLine();
                                final JSONObject json = new JSONObject(line);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (!json.has("lineData")) {
                                                json.put("lineData", new JSONArray());
                                            }
                                            openData(json.getJSONArray("lineData"));
                                            ((TextView) findViewById(R.id.statesText)).setText("検索結果は" + json.getJSONArray("lineData").length() + "件です");
                                        } catch (JSONException e) {
                                            //
                                            openData(new JSONArray());
                                            ((TextView) findViewById(R.id.statesText)).setText("検索結果は" + 0 + "件です");
                                            e.printStackTrace();
                                        }
                                        findViewById(R.id.closeButton).callOnClick();
                                        return;
                                    }
                                });
                                bufReader.close();
                                inReader.close();
                                in.close();
                            } else {
                                SDlog.toast("検索エラー" + connection.getResponseCode());
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

    }

    private void openData(JSONArray json) {
        ListView listView = findViewById(R.id.databaseList);
        try {
            DatabaseListAdapter adapter = new DatabaseListAdapter(getContext(), json);
            listView.setAdapter(adapter);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
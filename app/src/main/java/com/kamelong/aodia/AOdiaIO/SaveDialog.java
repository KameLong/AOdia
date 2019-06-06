package com.kamelong.aodia.AOdiaIO;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SDlog;

import java.io.File;

public class SaveDialog extends Dialog{
    DiaFile diaFile;
    String directoryName;
    public SaveDialog(Context context, final DiaFile diaFile) {
        super(context);
        this.diaFile = diaFile;
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.save_dialog);

        final EditText saveFileName=findViewById(R.id.saveFileName);
        directoryName=diaFile.filePath.substring(0,diaFile.filePath.lastIndexOf("/")+1);
        final EditText saveFileDirectory= ((EditText)findViewById(R.id.saveFileDirectory));
        saveFileDirectory.setText(directoryName);
        findViewById(R.id.homeDirectory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFileDirectory.setText(getContext().getExternalFilesDir(null).getAbsolutePath());
            }
        });


        saveFileName.setText(diaFile.filePath.substring(diaFile.filePath.lastIndexOf("/")+1,diaFile.filePath.lastIndexOf("."))+".oud2");

        final LinearLayout saveAlart=findViewById(R.id.saveAlart);

        Button keepButton=findViewById(R.id.saveKeep);
        keepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName=getContext().getFilesDir().getPath()+"/keep.oud2";
                File file=new File(fileName);
                if(file.exists()){
                    findViewById(R.id.textView19).setVisibility(View.GONE);
                    saveAlart.setVisibility(View.VISIBLE);

                }else{
                    try {
                        diaFile.saveToFile(fileName,true);
                        SaveDialog.this.dismiss();
                    }catch (Exception e){
                        Toast.makeText(getContext(),"不明なエラーが発生したため、保存を中止します。作者に連絡をお願いいたします。\n詳細:\n"+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
        Button saveButton=findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName=saveFileDirectory.getEditableText().toString()+"/"+saveFileName.getEditableText().toString();
                File file=new File(fileName);
                if(file.exists()){
                    findViewById(R.id.textView14).setVisibility(View.GONE);
                    saveAlart.setVisibility(View.VISIBLE);

                }else{
                    try {
                        diaFile.saveToFile(fileName,false);
                        SaveDialog.this.dismiss();
                    }catch (Exception e){
                        Toast.makeText(getContext(),"不明なエラーが発生したため、保存を中止します。作者に連絡をお願いいたします。\n詳細:\n"+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        Button savePverrideButton=findViewById(R.id.saveOverrideButton);
        savePverrideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName=directoryName+saveFileName.getEditableText().toString();
                if(findViewById(R.id.textView19).getVisibility()==View.GONE){
                    fileName=getContext().getFilesDir().getPath()+"/keep.oud2";
                    try {
                        diaFile.saveToFile(fileName,true);
                        SaveDialog.this.dismiss();
                        SDlog.toast("KEEPに保存しました。「ファイルを開く」→「履歴」タブよりKEEPに保存したファイルを読み込めるようになります。");
                    }catch (Exception e){
                        Toast.makeText(getContext(),"不明なエラーが発生したため、保存を中止します。作者に連絡をお願いいたします。\n詳細:\n"+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    try {
                        diaFile.saveToFile(fileName,false);
                        SaveDialog.this.dismiss();
                    }catch (Exception e){
                        Toast.makeText(getContext(),"このフォルダにはファイルを保存できません。他のフォルダを選んでください。", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("001",false)||(System.currentTimeMillis()<1557154800)){
            findViewById(R.id.saveFileDirectory).setEnabled(true);
            findViewById(R.id.saveFileName).setEnabled(true);
            findViewById(R.id.saveButton).setEnabled(true);
        }else{
            findViewById(R.id.saveFileDirectory).setEnabled(false);
            findViewById(R.id.saveFileName).setEnabled(false);
            findViewById(R.id.saveButton).setEnabled(false);
            findViewById(R.id.layout1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SDlog.toast("有料オプションを購入した場合、任意のファイル名に保存することが可能となります。購入方法は「設定」をご覧ください");
                }
            });
            findViewById(R.id.saveFileName).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SDlog.toast("有料オプションを購入した場合、任意のファイル名に保存することが可能となります。購入方法は「設定」をご覧ください");
                }
            });
            findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SDlog.toast("有料オプションを購入した場合、任意のファイル名に保存することが可能となります。購入方法は「設定」をご覧ください");
                }
            });
        }

    }
}

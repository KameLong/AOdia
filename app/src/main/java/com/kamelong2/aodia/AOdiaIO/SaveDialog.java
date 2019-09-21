package com.kamelong2.aodia.AOdiaIO;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kamelong2.OuDia.DiaFile;
import com.kamelong.aodia.R;

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
        setContentView(R.layout.old_save_dialog);

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

        Button saveButton=findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName=saveFileDirectory.getEditableText().toString()+"/"+saveFileName.getEditableText().toString();
                File file=new File(fileName);
                if(file.exists()){
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
                    try {
                        diaFile.saveToFile(fileName,false);
                        SaveDialog.this.dismiss();
                    }catch (Exception e){
                        Toast.makeText(getContext(),"このフォルダにはファイルを保存できません。他のフォルダを選んでください。", Toast.LENGTH_LONG).show();
                    }
            }
        });
            findViewById(R.id.saveFileDirectory).setEnabled(true);
            findViewById(R.id.saveFileName).setEnabled(true);
            findViewById(R.id.saveButton).setEnabled(true);

    }
}

package com.kamelong.aodia.AOdiaIO;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.aodia.R;

import java.io.File;

public class SaveDialog extends Dialog{
    DiaFile diaFile;
    public SaveDialog(Context context, final DiaFile diaFile) {
        super(context);
        this.diaFile = diaFile;
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.save_dialog);

    final EditText saveFileName=findViewById(R.id.saveFileName);
        final String directoryName=diaFile.filePath.substring(0,diaFile.filePath.lastIndexOf("/")+1);
        Log.d("saveDialog",directoryName);
        saveFileName.setText(diaFile.filePath.substring(diaFile.filePath.lastIndexOf("/")+1,diaFile.filePath.lastIndexOf("."))+".oud2");

        final LinearLayout saveAlart=findViewById(R.id.saveAlart);

        Button saveButton=findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName=directoryName+saveFileName.getEditableText().toString();
                File file=new File(fileName);
                if(file.exists()){
                    saveAlart.setVisibility(View.VISIBLE);

                }else{
                    diaFile.saveToFile(fileName);
                    SaveDialog.this.dismiss();
                }
            }
        });
        Button savePverrideButton=findViewById(R.id.saveOverrideButton);
        savePverrideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName=directoryName+saveFileName.getEditableText().toString();
                    diaFile.saveToFile(fileName);
                    SaveDialog.this.dismiss();
            }
        });

    }
}

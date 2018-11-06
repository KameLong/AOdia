package com.kamelong.aodia.menu;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;

public class EditLineDialog extends Dialog {
    DiaFile diaFile;
    int diagramIndex=0;
    AOdiaActivity activity;
    public EditLineDialog (AOdiaActivity activity, DiaFile diafile){
        super(activity);
        this.diaFile=diafile;
        this.activity=activity;
        this.diagramIndex=diagramIndex;

    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_line_dialog);
        final EditText lineName=findViewById(R.id.lineName);
        lineName.setText(diaFile.name);
        final Button changeLineName=findViewById(R.id.changeLineName);
        changeLineName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaFile.name=lineName.getEditableText().toString();
                EditLineDialog.this.dismiss();
            }
        });
        Button addNewDia=findViewById(R.id.addNewDia);
        addNewDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaFile.addNewDiagram();
                new DiagramEditDialog(activity,diaFile, diaFile.getDiaNum()-1).show();
                EditLineDialog.this.dismiss();
            }
        });

    }


}

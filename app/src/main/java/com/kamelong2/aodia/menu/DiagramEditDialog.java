package com.kamelong2.aodia.menu;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.kamelong2.OuDia.DiaFile;
import com.kamelong2.aodia.AOdiaActivity;
import com.kamelong.aodia.R;

public class DiagramEditDialog extends Dialog {
    DiaFile diaFile;
    int diagramIndex=0;
    AOdiaActivity activity;
    public DiagramEditDialog(AOdiaActivity activity, DiaFile diafile, int diagramIndex){
        super(activity);
        this.activity=activity;
        this.diaFile=diafile;
        this.diagramIndex=diagramIndex;

    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.old_edit_dia);
        final EditText diagramName=findViewById(R.id.diagramNameEdit);
        diagramName.setText(diaFile.diagram.get(diagramIndex).name);
        Button diagramNameChange=findViewById(R.id.diaNameChangeSubmit);
        diagramNameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaFile.diagram.get(diagramIndex).name=diagramName.getEditableText().toString();
                DiagramEditDialog.this.dismiss();
                activity.menuFragment.createMenu();
            }
        });
        Button diagramCopy=findViewById(R.id.diagramCopySubmit);
        diagramCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaFile.copyDiagram(diagramIndex,diagramName.getEditableText().toString());
                DiagramEditDialog.this.dismiss();
            }
        });
        Button diagramDelete=findViewById(R.id.deleteButton);
        diagramDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaFile.deleteDiagram(diagramIndex);
                DiagramEditDialog.this.dismiss();
            }
        });

    }


}

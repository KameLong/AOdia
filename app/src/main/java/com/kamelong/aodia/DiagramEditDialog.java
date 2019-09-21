package com.kamelong.aodia;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.kamelong.aodia.AOdiaData.Diagram;
import com.kamelong.aodia.AOdiaData.LineFile;

public class DiagramEditDialog extends Dialog {
    public DiagramEditDialog(final Context context, final LineFile lineFile, final Diagram diagram){
        super(context);
        setContentView(R.layout.diagram_edit);
        final EditText editName=findViewById(R.id.editDiaName);
        editName.setText(diagram.name);
        findViewById(R.id.copyDiagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineFile.diagram.add(diagram.clone(lineFile));
                lineFile.diagram.get(lineFile.getDiagramNum()-1).name=diagram.name+"(コピー)";
            }
        });
        findViewById(R.id.deleteDiagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineFile.diagram.remove(diagram);
                if(lineFile.getDiagramNum()==0){
                    lineFile.diagram.add(new Diagram(lineFile));
                }
                ((MainActivity)context).getAOdia().killFragment(lineFile);
                DiagramEditDialog.this.dismiss();
            }
        });
        findViewById(R.id.upButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index=lineFile.diagram.indexOf(diagram);
                if(index>0){
                    lineFile.diagram.remove(diagram);
                    lineFile.diagram.add(index-1,diagram);
                }
                ((MainActivity) (context)).getAOdia().killFragment(lineFile);
            }
        });
        findViewById(R.id.OkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagram.name=editName.getEditableText().toString();
                DiagramEditDialog.this.dismiss();

            }
        });
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiagramEditDialog.this.dismiss();
            }
        });

    }
}

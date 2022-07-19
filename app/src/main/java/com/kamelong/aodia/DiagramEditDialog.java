package com.kamelong.aodia;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.LineFile;

public class DiagramEditDialog extends Dialog {
    public DiagramEditDialog(final Context context, final LineFile lineFile, final Diagram diagram){
        super(context);
        setContentView(R.layout.diagram_edit);
        final EditText editName=findViewById(R.id.editDiaName);
        editName.setText(diagram.name);
        findViewById(R.id.copyDiagram).setOnClickListener(v -> {
            lineFile.diagram.add(diagram.clone(lineFile));
            lineFile.diagram.get(lineFile.getDiagramNum()-1).name=diagram.name+"(コピー)";
        });
        findViewById(R.id.deleteDiagram).setOnClickListener(v -> {
            lineFile.diagram.remove(diagram);
            if(lineFile.getDiagramNum()==0){
                lineFile.diagram.add(new Diagram(lineFile));
            }
            ((MainActivity)context).getAOdia().killFragment(lineFile);
            DiagramEditDialog.this.dismiss();
        });
        findViewById(R.id.upButton).setOnClickListener(v -> {
            int index=lineFile.diagram.indexOf(diagram);
            if(index>0){
                lineFile.diagram.remove(diagram);
                lineFile.diagram.add(index-1,diagram);
            }
            ((MainActivity) (context)).getAOdia().killFragment(lineFile);
        });
        findViewById(R.id.OkButton).setOnClickListener(v -> {
            diagram.name=editName.getEditableText().toString();
            DiagramEditDialog.this.dismiss();

        });
        findViewById(R.id.cancelButton).setOnClickListener(v -> DiagramEditDialog.this.dismiss());

    }
}

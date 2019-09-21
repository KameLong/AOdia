package com.kamelong.aodia.EditTrain;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;

import android.view.View;
import android.widget.EditText;

import com.kamelong.aodia.R;


public class TrainPasteDialog extends Dialog {
    TrainPasteDialogInterface trainPasteDialogInterface;

    public TrainPasteDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.trainedit_paste_dialog);
        setTitle("列車貼り付けオプション");
        ((EditText)findViewById(R.id.secondEdit)).setText("0");
        ((EditText)findViewById(R.id.minitesEdit)).setText("0");
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trainPasteDialogInterface!=null) {
                    trainPasteDialogInterface.onCancelClicked();
                }
            }
        });
        findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trainPasteDialogInterface!=null) {
                    int time=Integer.parseInt(((EditText)findViewById(R.id.minitesEdit)).getEditableText().toString());
                    time*=60;
                    time+=Integer.parseInt(((EditText)findViewById(R.id.secondEdit)).getEditableText().toString());


                    trainPasteDialogInterface.onOkClicked(time);
                }
            }
        });
    }
    public void setTrainPasteDialogInterface(TrainPasteDialogInterface trainPasteDialogInterface){
        this.trainPasteDialogInterface=trainPasteDialogInterface;
    }
}

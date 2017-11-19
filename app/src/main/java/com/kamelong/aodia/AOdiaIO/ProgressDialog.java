package com.kamelong.aodia.AOdiaIO;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.kamelong.aodia.R;

public class ProgressDialog extends DialogFragment {
    View contentView;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("ファイル処理中");
        contentView=LayoutInflater.from(getActivity()).inflate(R.layout.progress_dialog, null);
        builder.setView(contentView);
        // Create the AlertDialog object and return it
        return builder.create();
    }
    public void setProgress(int progress,int max){
        ProgressBar progressBar=contentView.findViewById(R.id.progressBar);
        progressBar.setMax(max);
        progressBar.setProgress(progress);

    }
}

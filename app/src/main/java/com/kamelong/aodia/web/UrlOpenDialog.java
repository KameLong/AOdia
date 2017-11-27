package com.kamelong.aodia.web;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.kamelong.aodia.AOdiaActivity;

import java.io.File;

/**
 * Created by kame on 2017/08/06.
 */

public class UrlOpenDialog {
    private AlertDialog dialog=null;
    public UrlOpenDialog(final @NonNull Context context,final String url,final String originURL) {
        final AlertDialog.Builder builder = new AlertDialog.Builder( context);

        builder.setTitle("ファイルダウンロード方法選択");
        builder.setPositiveButton("直接開く", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                dialog=null;
            }
        });
        builder.setNeutralButton("chromeからダウンロードする", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                dialog=null;
                Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse(url));
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("ファイル公開ページをChromeで開く", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                dialog=null;
                Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse(originURL));
                context.startActivity(intent);

            }
        });
        dialog=builder.show();

    }
}

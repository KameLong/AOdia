package com.kamelong.aodia;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.kamelong.OuDia.LineFile;
import com.kamelong.tool.SDlog;

public class LineFileEditDialog extends Dialog {
    public LineFileEditDialog(Context context, final LineFile lineFile){
        super(context);
        setContentView(R.layout.linefile_edit);
        final EditText editName=findViewById(R.id.editLineName);
        editName.setText(lineFile.name);
        String startTime=String.format("%02d",lineFile.diagramStartTime/3600)+":"+String.format("%02d",(lineFile.diagramStartTime/60)%60);
        ((EditText)findViewById(R.id.startTime)).setText(startTime);
        ((EditText)findViewById(R.id.moveSecond1)).setText(""+lineFile.secondShift[0]);
        ((EditText)findViewById(R.id.moveSecond2)).setText(""+lineFile.secondShift[1]);
        findViewById(R.id.OkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineFile.name=editName.getEditableText().toString();
                try{
                    String startTime=((EditText)findViewById(R.id.startTime)).getText().toString();
                    lineFile.diagramStartTime=Integer.parseInt(startTime.split(":")[0])*3600+Integer.parseInt(startTime.split(":")[1])*60;
                }catch (Exception e){
                    e.printStackTrace();
                    SDlog.toast("ダイヤ起点時刻の入力が正しくありません。0:00形式で入力してください");
                    return;
                }
                try{
                    String second1=((EditText)findViewById(R.id.moveSecond1)).getText().toString();
                    if(Integer.parseInt(second1)<0){
                        throw new Exception();
                    }
                    lineFile.secondShift[0]=Integer.parseInt(second1);
                }catch (Exception e){
                    e.printStackTrace();
                    SDlog.toast("秒移動量1の入力が正しくありません。0以上の整数を入力してください");
                    return;
                }
                try{
                    String second2=((EditText)findViewById(R.id.moveSecond2)).getText().toString();
                    if(Integer.parseInt(second2)<0){
                        throw new Exception();
                    }
                    lineFile.secondShift[1]=Integer.parseInt(second2);
                }catch (Exception e){
                    e.printStackTrace();
                    SDlog.toast("秒移動量2の入力が正しくありません。0以上の整数を入力してください");
                    return;
                }

                LineFileEditDialog.this.dismiss();

            }
        });
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineFileEditDialog.this.dismiss();
            }
        });

    }
}

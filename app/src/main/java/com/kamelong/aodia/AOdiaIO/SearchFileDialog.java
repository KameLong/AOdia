package com.kamelong.aodia.AOdiaIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.kamelong.tool.SDlog;

public class SearchFileDialog implements OnItemClickListener
{
    private Context					m_parent;				// 呼び出し元
    private OnFileSelectListener	m_listener;			// 結果受取先
    private AlertDialog				m_dlg;					// ダイアログ
    private FileInfoArrayAdapter m_fileinfoarrayadapter; // ファイル情報配列アダプタ

    // コンストラクタ
    public SearchFileDialog( Context context, OnFileSelectListener listener ){
        m_parent = context;
        m_listener =listener;
    }

    // ダイアログの作成と表示
    public void show(String searchName,ArrayList<String> filePathList){
        // タイトル
        String strTitle = searchName+"駅を検索";
        // リストビュー
        ListView listview = new ListView( m_parent );
        listview.setScrollingCacheEnabled( false );
        listview.setOnItemClickListener( this );
        // ファイルリスト
        List<FileInfo> listFileInfo = new ArrayList<>();
        for( String filePath : filePathList ){
            File file=new File(filePath);
            if(!file.exists()){
                continue;
            }
            System.out.println(filePath);
            if(file.getName().endsWith(".oud")||file.getName().endsWith(".oud2")) {
                String[] stationName=loadStartEndStation(file);
                listFileInfo.add(new FileInfo(stationName[0]+"～"+stationName[1]+"\n"+file.getName(),file));
            }
        }
        Collections.sort( listFileInfo );

        m_fileinfoarrayadapter = new FileInfoArrayAdapter( m_parent, listFileInfo );
        listview.setAdapter( m_fileinfoarrayadapter );

        Builder builder = new AlertDialog.Builder( m_parent );
        builder.setTitle( strTitle );
        builder.setNeutralButton("キャンセル", null );
        builder.setView( listview );
        m_dlg = builder.show();
    }

    // ListView内の項目をクリックしたときの処理
    public void onItemClick(	AdapterView<?> l,
                                View v,
                                int position,
                                long id )
    {
        if( null != m_dlg )
        {
            m_dlg.dismiss();
            m_dlg = null;
        }

        FileInfo fileinfo = m_fileinfoarrayadapter.getItem( position );

            m_listener.onFileSelect( fileinfo.getFile() );

    }
    private String[] loadStartEndStation(File file) {

        try {
            String[] result=new String[2];
            result[0]="";
            result[1]="";
            FileInputStream is = new FileInputStream(file);
            if (file.getPath().endsWith(".oud")||file.getPath().endsWith(".oud2")) {
                InputStreamReader filereader = new InputStreamReader(is, "SJIS");
                BufferedReader br = new BufferedReader(filereader);
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals("Eki.")) {
                        while (!line.equals(".")) {
                            if (line.split("=", -1)[0].equals("Ekimei")) {
                                if(result[0].length()==0){
                                    result[0]=line.split("=",-1)[1];
                                }else{
                                    result[1]=line.split("=",-1)[1];

                                }
                            }
                            line = br.readLine();
                        }
                    }
                    if(line.equals("Dia.")){
                        break;
                    }
                }
            }
            return result;
        } catch (Exception e) {
            SDlog.log(e);
            return new String[]{"", ""};
        }
    }

    // 選択したファイルの情報を取り出すためのリスナーインターフェース
    public interface OnFileSelectListener
    {
        // ファイルが選択されたときに呼び出される関数
        public void onFileSelect( File file );
        public void onFileListSelect(File[] file);
    }
}

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
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.detabase.DBHelper;

public class FileSelectionDialog implements OnItemClickListener
{
    private Context					m_parent;				// 呼び出し元
    private OnFileSelectListener	m_listener;			// 結果受取先
    private AlertDialog				m_dlg;					// ダイアログ
    private FileInfoArrayAdapter m_fileinfoarrayadapter; // ファイル情報配列アダプタ

    private File fileDirectory;

    // コンストラクタ
    public FileSelectionDialog( Context context, OnFileSelectListener listener ){
        m_parent = context;
        m_listener =listener;
    }

    // ダイアログの作成と表示
    public void show( File fileDirectory ){
        this.fileDirectory=fileDirectory;
        // タイトル
        String strTitle = fileDirectory.getAbsolutePath();
        // リストビュー
        ListView listview = new ListView( m_parent );
        listview.setScrollingCacheEnabled( false );
        listview.setOnItemClickListener( this );
        // ファイルリスト
        File[] aFile = fileDirectory.listFiles();
        List<FileInfo> listFileInfo = new ArrayList<>();
        if( null != aFile ){
            for( File fileTemp : aFile ){
                if(fileTemp.getName().endsWith(".oud")||fileTemp.getName().endsWith(".oud2")) {
                    String[] stationName=loadStartEndStation(fileTemp);
                    listFileInfo.add(new FileInfo(stationName[0]+"～"+stationName[1]+"\n"+fileTemp.getName(), fileTemp));
                }
                if(fileTemp.isDirectory()) {
                    listFileInfo.add(new FileInfo(fileTemp.getName(), fileTemp));
                }
                if(fileTemp.getName().endsWith(".ZIP")){
//                    listFileInfo.add(new FileInfo(fileTemp.getName(), fileTemp));

                }

            }
            Collections.sort( listFileInfo );
        }
        // 親フォルダに戻るパスの追加
        if( null != fileDirectory.getParent() )
        {
            listFileInfo.add( 0, new FileInfo( "..", new File( fileDirectory.getParent() ) ) );
        }
        m_fileinfoarrayadapter = new FileInfoArrayAdapter( m_parent, listFileInfo );
        listview.setAdapter( m_fileinfoarrayadapter );

        Builder builder = new AlertDialog.Builder( m_parent );
        builder.setTitle( strTitle );
        builder.setNeutralButton("キャンセル", null );
        builder.setPositiveButton("前回の復元", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
            }
        });
        builder.setNegativeButton("履歴", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showHistory();
            }
        });
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

        if(fileinfo.getFile().isDirectory() ) {
            show( fileinfo.getFile() );
        }
        else{
            // ファイルが選ばれた：リスナーのハンドラを呼び出す
            m_listener.onFileSelect( fileinfo.getFile() );
        }


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
            SdLog.log(e);
            return new String[]{"", ""};
        }
    }
    private void showHistory(){
        // タイトル
        String strTitle = "履歴";
        // リストビュー
        ListView listview = new ListView( m_parent );
        listview.setScrollingCacheEnabled( false );
        listview.setOnItemClickListener( this );
        // ファイルリスト
        DBHelper db=new DBHelper(m_parent);
        String[] fileHistory=db.getHistory();
        File[] aFile = new File[fileHistory.length];
        for(int i=0;i<fileHistory.length;i++){
            aFile[i]=new File(fileHistory[i]);
        }
        List<FileInfo> listFileInfo = new ArrayList<>();
        if( null != aFile ){
            for( File fileTemp : aFile ){
                if(fileTemp.getName().endsWith(".oud")) {
                    String[] stationName=loadStartEndStation(fileTemp);
                    listFileInfo.add(new FileInfo(stationName[0]+"～"+stationName[1]+"\n"+fileTemp.getName(), fileTemp));
                }
                if(fileTemp.isDirectory()) {
                    String[] stationName=loadStartEndStation(fileTemp);
                    listFileInfo.add(new FileInfo(fileTemp.getName(), fileTemp));
                }

            }
        }
        m_fileinfoarrayadapter = new FileInfoArrayAdapter( m_parent, listFileInfo );
        listview.setAdapter( m_fileinfoarrayadapter );

        Builder builder = new AlertDialog.Builder( m_parent );
        builder.setTitle( strTitle );
        builder.setNeutralButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                show(fileDirectory);
            }
        });
        builder.setView( listview );
        m_dlg = builder.show();

    }

    // 選択したファイルの情報を取り出すためのリスナーインターフェース
    public interface OnFileSelectListener
    {
        // ファイルが選択されたときに呼び出される関数
        public void onFileSelect( File file );
        public void onFileListSelect(File[] file);
    }
}

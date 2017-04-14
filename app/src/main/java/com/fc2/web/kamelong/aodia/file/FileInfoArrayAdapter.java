package com.fc2.web.kamelong.aodia.file;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileInfoArrayAdapter extends ArrayAdapter<FileInfo>
{
    private List<FileInfo>	m_listFileInfo; // ファイル情報リスト

    // コンストラクタ
    public FileInfoArrayAdapter(	Context context,
                                    List<FileInfo> objects )
    {
        super( context, -1, objects );

        m_listFileInfo = objects;
    }

    // m_listFileInfoの一要素の取得
    @Override
    public FileInfo getItem( int position )
    {
        return m_listFileInfo.get( position );
    }

    // 一要素のビューの生成
    @Override
    public View getView(	int position,
                            View convertView,
                            ViewGroup parent )
    {
        // レイアウトの生成
        if( null == convertView )
        {
            Context context = getContext();
            // レイアウト
            LinearLayout layout = new LinearLayout( context );
            layout.setPadding( 10, 10, 10, 10 );
            layout.setBackgroundColor( Color.WHITE );
            convertView = layout;
            // テキスト
            TextView textview = new TextView( context );
            textview.setTag( "text" );
            textview.setTextColor( Color.BLACK );
            textview.setPadding( 10, 10, 10, 10 );
            layout.addView( textview );
        }

        // 値の指定
        FileInfo fileinfo = m_listFileInfo.get( position );
        TextView textview = (TextView)convertView.findViewWithTag( "text" );
        if( fileinfo.getFile().isDirectory() )
        { // ディレクトリの場合は、名前の後ろに「/」を付ける
            textview.setText( fileinfo.getName() + "/" );
        }
        else
        {
            textview.setText( fileinfo.getName() );
        }

        return convertView;
    }
}
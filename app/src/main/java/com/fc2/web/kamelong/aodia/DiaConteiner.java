package com.fc2.web.kamelong.aodia;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.fc2.web.kamelong.aodia.diagram.DiagramFragment;
import com.fc2.web.kamelong.aodia.diagram.DiagramSetting;
import com.fc2.web.kamelong.aodia.oudia.DiaFile;
import com.fc2.web.kamelong.aodia.oudia.OuDiaDiaFile;
import com.fc2.web.kamelong.aodia.timeTable.TimeTableFragment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kame on 2017/01/21.
 */
/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 * あと、これは強制というわけではないですが、このソースコードを利用したときは、
 * 作者に一言メールなりで連絡して欲しいなと思ってます。
 * こちらが全く知らないところで使われていたりするのは、ちょっと気分悪いですよね。
 * まあ、強制はできないので、皆さんの良識におまかせします。
 */
public class DiaConteiner {
    public DiaFile diaFile;
    public ArrayList<Fragment[]> dataFragment;
    public ArrayList<DiagramSetting> diagramsetting;

    public Fragment commentFragment;
    public Fragment stationTimeTableFragment;
    public Context context;
    /**
     * 現在起動しているActivity内での、
     * この路線のインデックス
     */
    public int indexNumber;

    public DiaConteiner(Context c,DiaFile dia){
        context=c;
        diaFile=dia;

        for(int i=0;i<diaFile.getDiaNum();i++){
            Fragment[] fragments=new Fragment[3];
            fragments[0]=new TimeTableFragment();
            Bundle args0 = new Bundle();
            args0.putInt("fileNum",indexNumber);
            args0.putInt("diaN", i);
            args0.putInt("direct",0);
            fragments[0].setArguments(args0);
            fragments[1]=new TimeTableFragment();
            Bundle args1 = new Bundle();
            args1.putInt("fileNum",indexNumber);
            args1.putInt("diaN", i);
            args1.putInt("direct",1);
            fragments[1].setArguments(args0);
            fragments[2]=new DiagramFragment();
            Bundle args2 = new Bundle();
            args2.putInt("fileNum",indexNumber);
            args2.putInt("diaN", i);
            fragments[2].setArguments(args0);
            dataFragment.add(fragments);
        }
        commentFragment=new CommentFragment();
    }
    public void setMenu(){

    }
}

package com.kamelong.aodia;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
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
 */

public class CommentFragment extends AOdiaFragment {
    private int fileNum=0;
    public CommentFragment(){
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout main=new LinearLayout(getAOdiaActivity());

        try {

            Bundle bundle = getArguments();
            fileNum=bundle.getInt("fileNum");
            diaFile=getAOdiaActivity().diaFiles.get(fileNum);
        }catch(Exception e){
            Toast.makeText(getAOdiaActivity(), "Error(CommentFragment-onCreateView-E1)", Toast.LENGTH_SHORT).show();
            SdLog.log(e);
        }
        try {

            main.setOrientation(LinearLayout.VERTICAL);
            ScrollView scrollView=new ScrollView(getAOdiaActivity());
            TextView text = new TextView(getAOdiaActivity());
            text.setTextColor(Color.BLACK);
            scrollView.addView(text);
            LinearLayout.LayoutParams mlp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            text.setText(diaFile.comment);
           mlp.setMargins(10, 10, 10, 10);
            main.addView(scrollView,mlp);
        }catch(Exception e){
            Toast.makeText(getAOdiaActivity(), "Error(CommentFragment-onCreateView-E2)", Toast.LENGTH_SHORT).show();
            SdLog.log(e);
        }
        return main;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
//    @Override
    public String fragmentName(){
        try {
            return "コメント";
        }catch(Exception e){
            SdLog.log(e);
            Toast.makeText(getAOdiaActivity(), "Error(CommentFragment-fragmentName-E1)", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
//    @Override
    public String fragmentHash() {
        try {
            return "comment-" + diaFile.filePath;
        }catch(Exception e){
            SdLog.log(e);
            Toast.makeText(getAOdiaActivity(), "Error(CommentFragment-fragmentHash-E1)", Toast.LENGTH_SHORT).show();
            return "";
        }
    }


}

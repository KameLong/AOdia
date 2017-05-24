package com.fc2.web.kamelong.aodia;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class CommentFragment extends KLFragment {
    private int fileNum=0;
    public CommentFragment(){
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout main=new LinearLayout(getActivity());
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
        try {

            Bundle bundle = getArguments();
            fileNum=bundle.getInt("fileNum");
        }catch(Exception e){
            SdLog.log(e);
        }
        try {

            main.setOrientation(LinearLayout.VERTICAL);
            TextView title = new TextView(getActivity());

            TextView text = new TextView(getActivity());
            title.setText("コメント");
            title.setBackgroundColor(Color.rgb(200, 200, 0));
            title.setTextSize(30);

            title.setTextColor(Color.BLACK);
            text.setText(((MainActivity) getActivity()).diaFiles.get(fileNum).getComment());
            text.setTextColor(Color.BLACK);

            main.addView(title);
            main.addView(text);
        }catch(Exception e){
            SdLog.log(e);
        }
        return main;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    }

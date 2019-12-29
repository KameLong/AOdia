package com.kamelong.aodia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.kamelong.OuDia.LineFile;
import com.kamelong.tool.SDlog;
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

public class CommentFragment extends AOdiaFragmentCustom {
    public static final String FRAGMENT_NAME="CommentFragment";

    private LineFile lineFile;
    private int fileIndex = 0;
    private View fragmentContainer;

    public CommentFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {

            Bundle bundle = getArguments();
            fileIndex = bundle.getInt(AOdia.FILE_INDEX);
            lineFile = getAOdia().getLineFile(fileIndex);
        }
        catch (Exception e) {
            SDlog.toast("Error(CommentFragment-onCreateView-E1)");
            SDlog.log(e);
        }

        fragmentContainer = inflater.inflate(R.layout.comment_framgent, container, false);
        return fragmentContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(lineFile==null){
            getAOdia().killFragment(this);
        }
        try {
            ((EditText) fragmentContainer.findViewById(R.id.commentText)).setText(lineFile.comment.replace("\\n", "\n"));
            fragmentContainer.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String comment = ((EditText) fragmentContainer.findViewById(R.id.commentText)).getText().toString().replace("\n", "\\n");
                        lineFile.comment = comment;
                        ((MainActivity) getActivity()).getAOdia().killFragment(CommentFragment.this);
                    } catch (Exception e) {
                        SDlog.toast("コメントの変更ができませんでした");
                        SDlog.log(e);
                    }
                }
            });

        } catch (Exception e) {
            SDlog.log(e);
        }
    }


    @NonNull
    @Override
    public String getName() {
        try {
            String line = lineFile.name;
            if (line.length() > 10) {
                line = line.substring(0, 10);
            }
            return line + "\n" + "コメント";
        } catch (Exception e) {
            return "コメント";
        }
    }

    @Override
    public String getHash() {
        return FRAGMENT_NAME;
    }


    @Override
    public LineFile getLineFile() {
        return lineFile;
    }
}

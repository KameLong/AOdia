package com.kamelong.tool;

import com.kamelong.OuDia.LineFile;

public class LineFileException extends Exception{
    public LineFile errorFile;
    public LineFileException(String title,LineFile file){
        super(title);
        errorFile=file;
    }

}

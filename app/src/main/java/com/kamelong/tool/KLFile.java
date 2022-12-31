package com.kamelong.tool;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public interface KLFile {

    public String getPath();
    public FileInputStream getFileInputStream() throws FileNotFoundException;

}

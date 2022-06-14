package com.nju.ds.common;

import java.io.File;

public class FileUtils {
    public static void deleteFiles(File file){
        if(file.isDirectory()){
            File[] childs=file.listFiles();
            for (File f:childs) deleteFiles(f);
        }
        file.delete();
    }
}

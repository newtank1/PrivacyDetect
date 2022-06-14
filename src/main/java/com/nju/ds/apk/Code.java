package com.nju.ds.apk;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
public class Code {
    public String rootPath;
    public Smali[] smaliCode;

    public Code(String rootPath){
        this.rootPath=rootPath;
        Queue<File> fileList=new LinkedList<>() ;
        fileList.offer(new File(rootPath));
        ArrayList<Smali> smaliFiles=new ArrayList<>();
        while (!fileList.isEmpty()){
            File head=fileList.poll();
            if(head==null) break;
            if(head.isDirectory()&&(!head.getAbsolutePath().contains("smali\\android"))){
                for(File f:head.listFiles()) fileList.offer(f);
            }
            else if(head.getName().endsWith(".smali")){
                smaliFiles.add(new Smali(head.getAbsolutePath()));
            }
        }
        smaliCode=new Smali[smaliFiles.size()];
        smaliFiles.toArray(smaliCode);
    }
}

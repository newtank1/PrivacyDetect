package com.nju.ds.apk;

import java.io.*;
import java.util.ArrayList;

public class Manifest {
    public String path;

    public Manifest(String path){
        this.path=path;
    }
    public File getFile(){
        return new File(path);
    }
    public ArrayList<String> getContent(){
        ArrayList<String> strings=new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line;
            while ((line=reader.readLine())!=null){
                strings.add(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return strings;
    }
}

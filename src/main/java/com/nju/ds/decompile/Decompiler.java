package com.nju.ds.decompile;

import com.nju.ds.apk.Apk;
import com.nju.ds.apk.Code;
import com.nju.ds.apk.Manifest;
import com.nju.ds.apk.Source;
import com.nju.ds.common.Config;
import com.nju.ds.common.FileUtils;

import java.io.*;

public class Decompiler {
    public Source decompile(Apk apk){
        String name=apk.name;
        String path=apk.path;
        String cmd = "java -jar "+ Config.APKTOOL_PATH + " d -f " + path + " -o " + Config.CACHE_PATH+name;
        File cache=new File(Config.CACHE_PATH+name);
        if(cache.exists()&&cache.isDirectory()) return decompileFinish(name);
        System.out.println("Decompiling");
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            handleError(process.getErrorStream());
            handleInput(process.getInputStream());
            process.waitFor();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Decompile completed");
        return decompileFinish(name);
    }

    public Source decompileFinish(String name){
        String path=Config.CACHE_PATH+name;
        for(File f:new File(path).listFiles()){
            String fp=f.getAbsolutePath();
            if(!fp.contains("smali")&&!fp.contains(Config.ANDROIDMANIFEST))
                FileUtils.deleteFiles(f);
        }
        Manifest manifest=new Manifest(path+"\\"+Config.ANDROIDMANIFEST);
        Code code=new Code(path);
        return new Source(manifest,code,path,name);
    }

    public void handleError(InputStream errorStream){
        InputStreamReader inputStreamReader=new InputStreamReader(errorStream);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                char[] buffer=new char[1024];
                int len=0;
                try {
                    while ((len = inputStreamReader.read(buffer,0,1024))!=-1) {
                        //System.err.println(buffer);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void handleInput(InputStream inputStream){
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String line;
                    while ((line=reader.readLine())!=null){
                        //System.out.println(line);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}

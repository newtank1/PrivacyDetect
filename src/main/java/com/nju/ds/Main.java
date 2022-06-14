package com.nju.ds;

import com.nju.ds.analysis.Analysisor;
import com.nju.ds.apk.Apk;
import com.nju.ds.apk.Source;
import com.nju.ds.common.Config;
import com.nju.ds.decompile.Decompiler;

public class Main {
    public static void main(String[] args){
        String apkName="\\"+args[0];//填入安装包名称
        Apk apk=new Apk(Config.ROOT_DIR+apkName,apkName);
        Source source=new Decompiler().decompile(apk);
        Analysisor analysisor=new Analysisor(source,true,true,Config.ROOT_DIR+"\\"+args[1]);//填入policy名称
        analysisor.analyse();
    }
}

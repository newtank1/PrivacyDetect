package com.nju.ds.policy;

import java.util.HashMap;
import java.util.Objects;

public class PolicyAction {
    public Stoken stoken;
    public String action;
    public String word;
    public static HashMap<String,String[]> table=new HashMap<>();
    static {
        table.put("读取", new String[]{"read","get"});
        table.put("获取",new String[]{"read","get"});
        table.put("转换", new String[]{"process","write"});
        table.put("收集",new String[]{"read","get"});
        table.put("保存",new String[]{"write"});
        table.put("查阅",new String[]{"read","get"});
        table.put("使用",new String[]{"read","get","use"});
        table.put("提供",new String[]{"read","get"});
        table.put("接入",new String[]{"read","get","use"});
        table.put("更改",new String[]{"process","write"});
        table.put("添加",new String[]{"process","write"});
        table.put("修改",new String[]{"process","write"});
        table.put("删除",new String[]{"write","delete","process"});
        table.put("存储",new String[]{"read","write","get"});
        table.put("录制",new String[]{"record"});
        table.put("访问",new String[]{"read","get"});
        table.put("定位",new String[]{"read","get"});
    }
    public PolicyAction(String word) {
        this.word=word;
        for(Stoken s:Stoken.values()){
            for(String w:s.words){
                if(word.contains(w)){
                    stoken=s;
                    action=word.substring(0,2);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyAction that = (PolicyAction) o;
        return stoken == that.stoken && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stoken, action);
    }

    public String[] translate(){
        String[] a=table.get(action);
        if (a != null) {
            return a;
        }
        return new String[]{"read","write"};
    }
}

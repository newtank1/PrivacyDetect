package com.nju.ds.code;

import com.nju.ds.common.Config;
import com.nju.ds.permission.Activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Function {
    public String name;
    public String clazz;
    public int paramCount;
    public int regCount;
    public String params;
    public String retType;
    public boolean isStatic;

    public Set<Activity> activities=new HashSet<>();

    public double credit;
    public Set<Function> doCalls =new HashSet<>();
    public Set<Function> beCalledBy =new HashSet<>();
    public boolean isVisited;
    public PrivacyWord privacyWord=new PrivacyWord();

    public Function(String name, String clazz, String params, String retType, boolean isStatic) {
        this.name = name;
        this.clazz = clazz;
        this.params=params;
        this.retType = retType;
        this.isStatic = isStatic;
    }

    public String toString(){
        return clazz+"."+name+"("+params+")";
    }

    public String getActivitiesString(){
        StringBuilder stringBuilder=new StringBuilder("activities: ");
        for(Activity activity:activities){
            if(!activity.isNormal())
            stringBuilder.append(activity.toString()).append(", ");
        }
        return stringBuilder.toString();
    }

    public boolean haveSpecialActivity(){
        if(activities.size()==0) return false;
        for(Activity activity:activities){
            if(!activity.isNormal()) return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return name.equals(function.name) && clazz.equals(function.clazz) && params.equals(function.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, clazz, params);
    }

    public void makeCallRelation(Function function,Boolean isCalled){
        if(this.equals(function)) return;
        if(isCalled) beCalledBy.add(function);
        else doCalls.add(function);
    }

    public boolean usesPrivacy(){
        return privacyWord.usesPrivacy();
    }

    public boolean isEncrypted(){
        String[] encryptTable= Config.ENCRYPT_TABLE;
        for(String word:encryptTable){
            if(name.contains(word)&&clazz.contains(word)){
                return (!name.contains("decrypt")&&!clazz.contains("decrypt"));
            }
        }
        return false;
    }
}

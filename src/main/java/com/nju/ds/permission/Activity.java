package com.nju.ds.permission;

import com.nju.ds.policy.Stoken;

import java.util.Locale;
import java.util.Objects;

public class Activity {
    public static final String ACTIVITY_REGEX="<activity (.*?) android:name=\"(.*?)\" (.*?)>";
    public static final String[] targetTable={"phone","contact","file" ,"calendar", "photo","image","picture", "video","audio", "profile", "location","web","bluetooth","sensor","sms","storage"};
    public static final String[] actionTable={"get","use","search","move","preview","upload","verify","edit","read","write","share","delete","scan","send","process","publish","record"};
    public String action;
    public String target;
    public String name;

    public Activity(String name) {
        this.name = name;
        String n=name.toLowerCase(Locale.ROOT);
        for(String t:targetTable){
            if(n.contains(t)) target=t;
        }
        for (String a:actionTable){
            if(n.contains(a)) action=a;
        }
    }

    public boolean isNormal(){
        return action==null||target==null;
    }

    public String toString(){
        if(action==null||target==null) return "normal";
        return action+" "+target;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        if((this.action==null||this.target==null)&&(activity.action==null||activity.target==null)) return true;
        return Objects.equals(action, activity.action) && Objects.equals(target, activity.target);
    }

    @Override
    public int hashCode() {
        if(action==null||target==null) return Objects.hash(null,null);
        return Objects.hash(action, target);
    }

    public Activity transform(){
        Activity a=new Activity(this.toString());
        if("picture".equals(target)||"image".equals(target)||"video".equals(target)||"storage".equals(target)) a.target="file";
        if("search".equals(action)||"preview".equals(action)||"verify".equals(action)||"scan".equals(action)||"get".equals(action)) a.action="read";
        if("move".equals(action)||"edit".equals(action)||"delete".equals(action)||"process".equals(action)) a.action="write";
        if("share".equals(action)||("send".equals(action)&&!"sms".equals(target))||"publish".equals(action)) a.action="upload";
        return a;
    }

    public Stoken toStoken(){
        if(isNormal()) return null;
        switch (target){
            case "phone":return Stoken.PHONE;
            case "contact":return Stoken.CONTACT;
            case "calendar":return Stoken.CALENDAR;
            case "audio":return Stoken.MICROPHONE;
            case "sms":return Stoken.SMS;
            case "sensor":return Stoken.SENSOR;
            case "bluetooth":return Stoken.NEARBY_DEVICES;
            case "location":return Stoken.LOCATION;
            case "camera":
            case "photo":
                return Stoken.CAMERA;
            default:return Stoken.STORAGE;
        }
    }
}

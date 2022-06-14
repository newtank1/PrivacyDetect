package com.nju.ds.analysis;

import com.nju.ds.apk.Manifest;
import com.nju.ds.permission.Activity;
import com.nju.ds.permission.Permission;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManifestScanner {
    public Manifest manifest;
    public Pattern permissionPattern=Pattern.compile(Permission.PERMISSION_REGEX);
    public Pattern activityPattern=Pattern.compile(Activity.ACTIVITY_REGEX);
    public static Set<Activity> activities=new HashSet<>();
    public ManifestScanner(Manifest manifest){
        this.manifest=manifest;
    }

    public Set<Permission> scanPermission(){
        ArrayList<String> manifestContent= manifest.getContent();
        HashSet<Permission> permissions=new HashSet<>();
        File file=new File("manifest.txt");
        try {
            if (!file.exists()) file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
            writer.append("Permissions:\n");
            for (String s : manifestContent) {
                Matcher matcher = permissionPattern.matcher(s);
                if (matcher.find()) {
                    Permission permission = new Permission(matcher.group(1));
                    if (!"NORMAL".equals(permission.type)) {
                        permissions.add(permission);
                        writer.append(permission.toString()+"\n");
                    }
                }
            }
            writer.flush();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return permissions;
    }

    public Set<Activity> scanActivity(){
        ArrayList<String> manifestContent= manifest.getContent();
        HashSet<Activity> activities=new HashSet<>();
        File file=new File("manifest.txt");
        try {
            if(!file.exists()) file.createNewFile();
            BufferedWriter writer=new BufferedWriter(new FileWriter(file,true));
            writer.write("Activities:\n");
            for (String s : manifestContent) {

                Matcher matcher = activityPattern.matcher(s);
                if (matcher.find()) {
                    Activity activity = new Activity(matcher.group(2));
                    writer.append(activity.name+"\n");
                    activities.add(activity);
                }
            }
            writer.flush();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        ManifestScanner.activities=activities;
        return activities;
    }

    public Set<Activity> filterActivity(Set<Activity> old,Set<Permission> permissions){
        Set<Activity> neu=new HashSet<>();
        Set<Activity> transformedOld=new HashSet<>(old);
        transformedOld.forEach((a)->{if("picture".equals(a.target)||"image".equals(a.target)||"video".equals(a.target)||"storage".equals(a.target)) a.target="file";
        if("search".equals(a.action)||"preview".equals(a.action)||"verify".equals(a.action)||"scan".equals(a.action)||"get".equals(a.action)) a.action="read";
        if("move".equals(a.action)||"edit".equals(a.action)||"delete".equals(a.action)||"process".equals(a.action)) a.action="write";
        if("share".equals(a.action)||("send".equals(a.action)&&!"sms".equals(a.target))||"publish".equals(a.action)) a.action="upload";});
        if(permissions.contains(new Permission("READ_PHONE_STATE"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("phone")) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("ACCESS_BACKGROUND_LOCATION"))
        ||permissions.contains(new Permission("ACCESS_COARSE_LOCATION"))
        ||permissions.contains(new Permission("ACCESS_FINE_LOCATION"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("location")) neu.add(activity);
            }
        }if(permissions.contains(new Permission("BODY_SENSORS"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("sensor")) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("CAMERA"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("camera")||activity.target.equals("photo")) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("GET_ACCOUNTS"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("account")&&(activity.action.equals("read")||activity.action.equals("write"))) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("READ_ACCOUNTS"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("contact")&&(activity.action.equals("read"))) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("WRITE_ACCOUNTS"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("contact")&&(activity.action.equals("write"))) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("READ_CALENDAR"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("calendar")&&(activity.action.equals("read"))) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("WRITE_CALENDAR"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("calendar")&&(activity.action.equals("write"))) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("READ_SMS"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("sms")&&activity.action.equals("read")) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("SEND_SMS"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("sms")&&(activity.action.equals("send"))) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("RECORD_AUDIO"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("audio")&&(activity.action.equals("record"))) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("READ_EXTERNAL_STORAGE"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("file")&&(activity.action.equals("read"))) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("WRITE_EXTERNAL_STORAGE"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("file") &&activity.action.equals("write")) neu.add(activity);
            }
        }
        if(permissions.contains(new Permission("INTERNET"))){
            for(Activity activity:transformedOld){
                if(activity.isNormal()) continue;
                if(activity.target.equals("upload")) neu.add(activity);
            }
        }
        return neu;
    }
}

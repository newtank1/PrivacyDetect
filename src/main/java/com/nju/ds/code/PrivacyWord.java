package com.nju.ds.code;

import com.nju.ds.permission.Activity;

import java.util.Objects;
import java.util.Set;

public class PrivacyWord {
    public boolean usesRecorder;
    public boolean usesLocation;
    public boolean usesCamera;
    public boolean usesSensor;
    public boolean visitImages;
    public boolean visitVideos;
    public boolean visitSms;
    public boolean upload;
    public boolean readStorage;
    public boolean writeStorage;
    public boolean isEncrypted;

    public void merge(PrivacyWord privacyWord){
        usesRecorder=usesRecorder||privacyWord.usesRecorder;
        usesLocation=usesLocation||privacyWord.usesLocation;
        usesCamera=usesCamera||privacyWord.usesCamera;
        usesSensor=usesSensor||privacyWord.usesSensor;
        visitImages=visitImages||privacyWord.visitImages;
        visitVideos=visitVideos||privacyWord.visitVideos;
        visitSms=visitSms||privacyWord.visitSms;
        upload=upload||privacyWord.upload;
        readStorage=readStorage||privacyWord.readStorage;
        writeStorage=writeStorage||privacyWord.writeStorage;
    }

    public boolean usesPrivacy(){
        return usesRecorder||usesLocation||usesCamera||usesSensor||visitVideos||visitImages||visitSms||upload||readStorage||writeStorage;
    }

    public String toString(){
        StringBuilder stringBuilder=new StringBuilder("uses ");
        if(usesRecorder) stringBuilder.append("recorder ");
        if(usesLocation) stringBuilder.append("location ");
        if(usesCamera) stringBuilder.append("camera ");
        if(usesSensor) stringBuilder.append("sensor ");
        if(visitImages) stringBuilder.append("image ");
        if(visitVideos) stringBuilder.append("video ");
        if(visitSms) stringBuilder.append("sms ");
        if(upload) stringBuilder.append("upload ");
        if(readStorage) stringBuilder.append("reading storage ");
        if(writeStorage) stringBuilder.append("writing storage ");
        if(isEncrypted) stringBuilder.append("encrypted");
        return stringBuilder.toString();
    }

    public boolean differ(PrivacyWord p){
        if(p.usesRecorder&&!usesRecorder) return true;
        if(p.usesLocation&&!usesLocation) return true;
        if(p.usesCamera&&!usesCamera) return true;
        if(p.usesSensor&&!usesSensor) return true;
        if(p.visitImages&&!visitImages) return true;
        if(p.visitVideos&&!visitVideos) return true;
        if(p.visitSms&&!visitSms) return true;
        if(p.upload&&!upload) return true;
        if(p.readStorage&&!readStorage) return true;
        if(p.writeStorage&&!writeStorage) return true;
        return false;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivacyWord that = (PrivacyWord) o;
        return usesRecorder == that.usesRecorder && usesLocation == that.usesLocation && usesCamera == that.usesCamera && usesSensor==that.usesSensor && visitImages == that.visitImages && visitVideos == that.visitVideos && visitSms == that.visitSms && upload == that.upload &&readStorage==that.readStorage && writeStorage==that.writeStorage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(usesRecorder, usesLocation, usesCamera, usesSensor, visitImages, visitVideos, visitSms, upload, readStorage);
    }

    public double makeCredit(){
        double c=0;
        if(usesRecorder) c+=1;
        if(usesCamera) c+=1;
        if(usesLocation) c+=2;
        if(usesSensor) c+=1;
        if(visitVideos) c+=4;
        if(visitImages) c+=4;
        if(visitSms) c+=2;
        if(readStorage) c+=4;
        if(writeStorage) c+=4;
        if(upload) c*=2;
        return c;
    }

    public void toActivity(Set<Activity> activities){
        if(usesRecorder) activities.add(new Activity("record video"));
        if(usesCamera) activities.add(new Activity("get photo"));
        if(usesLocation) activities.add(new Activity("get location"));
        if(usesSensor) activities.add(new Activity("use sensor"));
        if(visitImages) activities.add(new Activity("get image"));
        if(visitVideos) activities.add(new Activity("get video"));
        if(visitSms) activities.add(new Activity("get sms"));
        if(readStorage) activities.add(new Activity("read file"));
        if(writeStorage) activities.add(new Activity("write file"));
        if(upload){
            if(visitImages) activities.add(new Activity("upload image"));
            if(visitVideos) activities.add(new Activity("upload video"));
            if(visitSms) activities.add(new Activity("upload sms"));
            if(readStorage) activities.add(new Activity("upload file"));
        }
    }
}

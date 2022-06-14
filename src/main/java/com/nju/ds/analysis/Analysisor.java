package com.nju.ds.analysis;

import com.nju.ds.apk.Source;
import com.nju.ds.permission.Activity;
import com.nju.ds.permission.Permission;
import com.nju.ds.policy.PolicyAction;
import com.nju.ds.policy.Stoken;

import java.util.Set;

public class Analysisor {
    public ManifestScanner manifestScanner;
    public CodeScanner codeScanner;
    public PolicyScanner policyScanner;

    public Source source;

    public Analysisor(Source source,boolean scanActivity,boolean scanPrivacy,String policyFile){
        this.source=source;
        manifestScanner =new ManifestScanner(source.manifest);
        codeScanner =new CodeScanner(source.code,scanActivity,scanPrivacy);
        policyScanner=new PolicyScanner(policyFile);
    }

    public void analyse(){
        System.out.println("Scanning manifest...");
        Set<Activity> activities=scanActivity();
        Set<Permission> permissions=scanPermission();
        System.out.println("Scanning completed, see manifest.txt");
        System.out.println("Scanning code...");
        activities.addAll(scanFunction());

        Set<Activity> newActivities=filterActivity(activities,permissions);
        System.out.println("Scanning completed, see code.txt");
        System.out.println("Scanning policy...");
        Set<PolicyAction> policyActions=policyScanner.scan();
        System.out.println("Scanning completed, see policy.txt");
        System.out.print("从代码中获取的个人信息操作:");
        for (Activity activity:newActivities){
            System.out.print(activity+", ");
        }
        System.out.println();
        System.out.print("从政策中获取的个人信息操作:");
        for (PolicyAction policyAction:policyActions){
            System.out.print(policyAction.word+", ");
        }
        System.out.println();
        check(newActivities,policyActions);
    }

    public Set<Permission> scanPermission(){
        return manifestScanner.scanPermission();
    }

    public Set<Activity> scanFunction() {
        return codeScanner.scan();
    }

    public Set<Activity> scanActivity(){
        return manifestScanner.scanActivity();
    }

    public Set<Activity> filterActivity(Set<Activity> activities,Set<Permission> permissions){
        return manifestScanner.filterActivity(activities,permissions);
    }

    public void check(Set<Activity> activities,Set<PolicyAction> policyActions){
        for(Activity activity: activities){
            Stoken stoken=activity.toStoken();
            for(PolicyAction p:policyActions){
                if(p.stoken.equals(stoken)){
                    String[] a=p.translate();
                    for(String s:a) {
                        if (activity.action.equals(s)) {
                            System.out.println(activity + "符合");
                            break;
                        }
                    }
                }
            }
        }
    }
}

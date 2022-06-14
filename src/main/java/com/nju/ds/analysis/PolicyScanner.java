package com.nju.ds.analysis;

import com.nju.ds.policy.AutomaticClassification;
import com.nju.ds.policy.PolicyAction;
import com.nju.ds.policy.Segment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class PolicyScanner {
    public String fileName;

    public PolicyScanner(String fileName) {
        this.fileName = fileName;
    }

    public Set<PolicyAction> scan(){
        File file=new File("policy.txt");
        try{
            file.createNewFile();
            BufferedWriter writer=new BufferedWriter(new FileWriter(file,true));
            AutomaticClassification.scan(fileName,writer);
            return Segment.scan(fileName,writer);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}

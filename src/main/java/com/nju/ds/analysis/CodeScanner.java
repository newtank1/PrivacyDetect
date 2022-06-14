package com.nju.ds.analysis;

import com.nju.ds.apk.Code;
import com.nju.ds.apk.Smali;
import com.nju.ds.code.Call;
import com.nju.ds.code.Function;
import com.nju.ds.code.Invoker;
import com.nju.ds.common.Config;
import com.nju.ds.permission.Activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeScanner {
    public Code code;
    public static final Pattern functionPattern = Pattern.compile(Config.FUNCTION_REGEX);
    public static final Pattern AndroidInvokePattern=Pattern.compile(Config.ANDROID_INVOKE_REGEX);
    public static final Pattern invokePattern=Pattern.compile(Config.INVOKE_REGEX);
    public static final Pattern constStringPattern=Pattern.compile(Config.CONST_STRING_REGEX);
    public static final Pattern constClassPattern=Pattern.compile(Config.CONST_CLASS_REGEX);
    public boolean scanActivity;
    public boolean scanPrivacy;
    public Set<Activity> activities=new HashSet<>();

    public CodeScanner(Code code, boolean scanActivity, boolean scanPrivacy) {
        this.code = code;
        this.scanActivity = scanActivity;
        this.scanPrivacy = scanPrivacy;
    }

    public Set<Activity> scan(){
        HashMap<String,Function> functions=new HashMap<>();
        File file=new File("code.txt");
        try {
            file.createNewFile();
            BufferedWriter writer=new BufferedWriter(new FileWriter(file,true));
            writer.append("API detection: ");
            writer.newLine();
            for (Smali smali : code.smaliCode) {
                scanClass(functions, smali, scanActivity, scanPrivacy,writer);
            }
            writer.append("Call relation: ");
            writer.newLine();
            Set<Function> rootFunctions=scanCallGraph(new HashSet<>(functions.values()),writer);
            writer.append("Root functions:");
            writer.newLine();
            for (Function f : rootFunctions) {
                StringBuilder stringBuilder = new StringBuilder("root caller: ");
                stringBuilder.append(f);
                if (scanPrivacy) {
                    if (f.usesPrivacy())
                        stringBuilder.append(f.privacyWord);
                }
                if (scanActivity) {
                    if (!f.activities.isEmpty())
                        stringBuilder.append(f.getActivitiesString());
                }
                f.privacyWord.toActivity(activities);
                stringBuilder.append("\n");
                writer.append(stringBuilder);
            }
            writer.flush();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return activities;
    }

    public void scanClass(Map<String,Function> functions, Smali smali,boolean scanActivity,boolean scanPrivacy,BufferedWriter writer)throws IOException{

        ArrayList<String> strings = smali.getContent();

        String[] clazzLine = strings.get(0).split(" ");
        String clazz = clazzLine[clazzLine.length - 1];
        clazz=clazz.substring(1,clazz.length()-1);

        boolean inFunction = false;
        boolean inVisitingStorage=false;
        boolean inHttpConnect=false;
        String createClass=null;
        Function function = null;
        int lineNum=0;
        for (String line : strings) {
            if (line.startsWith(".method")) {
                lineNum = 0;
                inFunction = true;
                String[] declaration = line.split(" ");
                Matcher functionMatcher = functionPattern.matcher(declaration[declaration.length - 1]);
                if (functionMatcher.find()) {
                    if ((function = functions.get(clazz + "." + functionMatcher.group(1) + "(" + functionMatcher.group(2) + ")")) == null) {
                        function = new Function(functionMatcher.group(1), clazz, functionMatcher.group(2), functionMatcher.group(3), line.contains("static"));
                        functions.put(function.toString(), function);
                    }
                } else {
                    throw new RuntimeException("illegal code");
                }
            } else if (line.startsWith(".end method")) {
                if (scanPrivacy) {
                    if ((function.name.equals("onShowFileChooser") || function.name.equals("openFileChooser"))) {
                        if (!function.privacyWord.upload) {
                            function.privacyWord.upload = true;
                            writer.append("Detected app uploading to Internet in ").append(function.toString()).append("\n");
                        }
                    } else if (function.isEncrypted()) {
                        if (!function.privacyWord.isEncrypted) {
                            function.privacyWord.isEncrypted = true;
                            writer.append("Detected encryption in ").append(function.toString()).append("\n");
                        }
                    }
                    if (scanActivity && function.usesPrivacy() && !function.haveSpecialActivity()) {
                        Activity a=new Activity(function.toString());
                        function.activities.add(a);
                        if (function.haveSpecialActivity()) {
                            activities.addAll(function.activities);
                            writer.append("Detected ").append(String.valueOf(a)).append(" activity in ").append(String.valueOf(function)).append("\n");
                        }
                    }
                }
                inFunction = false;
                inVisitingStorage = false;
                inHttpConnect = false;
                createClass = null;
            }
            if (inFunction) {
                lineNum++;
                if (scanPrivacy) {
                    Matcher constStringInstruction = constStringPattern.matcher(line);
                    if (constStringInstruction.find()) {
                        String constString = constStringInstruction.group(1);
                        if (constString.contains("content://sms")) {
                            if (!function.privacyWord.visitSms) {
                                function.privacyWord.visitSms = true;
                                writer.append("Detected app visiting sms in ").append(String.valueOf(function)).append("\n");
                            }
                        } else if (constString.equals("android.media.action.IMAGE_CAPTURE")) {
                            if (!function.privacyWord.usesCamera) {
                                function.privacyWord.usesCamera = true;
                                writer.append("Detected app starting a camera in ").append(String.valueOf(function)).append("\n");
                            }
                        } else if (constString.contains("GET_CONTENT") || constString.contains("PICK")) {
                            inVisitingStorage = true;
                        } else if (inVisitingStorage) {
                            if (constStringInstruction.group(1).startsWith("image/*")) {
                                if (!function.privacyWord.visitImages) {
                                    function.privacyWord.visitImages = true;
                                    writer.append("Detected app visiting images in ").append(String.valueOf(function)).append("\n");
                                    inVisitingStorage = false;
                                }
                            } else if (constStringInstruction.group(1).startsWith("video/*")) {
                                if (!function.privacyWord.visitVideos) {
                                    function.privacyWord.visitVideos = true;
                                    writer.append("Detected app visiting videos in ").append(String.valueOf(function)).append("\n");
                                    inVisitingStorage = false;
                                }
                            }
                        }
                    }
                }
                Matcher androidInvoke = AndroidInvokePattern.matcher(line);
                if (androidInvoke.find()) {
                    String androidClazz = androidInvoke.group(2);
                    String androidMethod = androidInvoke.group(3);
                    if (scanActivity
                            && (androidClazz.equals("android/content/ComponentName") || androidClazz.equals("android/content/Intent"))
                            && androidMethod.equals("<init>")) {
                        if (createClass != null) {
                            Activity activity = new Activity(createClass);
                            if (ManifestScanner.activities.contains(activity) && !function.activities.contains(activity)) {
                                function.activities.add(activity);
                                activities.add(activity);
                                writer.append("Detected ").append(String.valueOf(activity)).append(" activity : ").append(createClass).append(" in ").append(String.valueOf(function)).append("\n");
                            }
                        }
                    }
                    if (scanPrivacy) {
                        if (androidClazz.equals("android/media/MediaRecorder") && androidMethod.equals("start")) {
                            if (!function.privacyWord.usesRecorder) {
                                function.privacyWord.usesRecorder = true;
                                writer.append("Detected app starting a recorder in ").append(String.valueOf(function)).append("\n");
                            }
                        } else if (androidClazz.equals("android/content/ContentResolver") && androidMethod.equals("query")) {
                            if (!function.privacyWord.readStorage) {
                                function.privacyWord.readStorage = true;
                                writer.append("Detected app reading external storage in ").append(String.valueOf(function)).append("\n");
                            }
                        } else if (androidClazz.equals("android/content/ContentResolver") && (androidMethod.equals("insert") || androidMethod.equals("delete") || androidMethod.equals("update"))) {
                            if (!function.privacyWord.writeStorage) {
                                function.privacyWord.writeStorage = true;
                                writer.append("Detected app writing external storage in ").append(String.valueOf(function)).append("\n");
                            }
                        } else if (androidClazz.equals("android/location/LocationManager") && (androidMethod.equals("getLastKnownLocation"))) {
                            if (!function.privacyWord.usesLocation) {
                                function.privacyWord.usesLocation = true;
                                writer.append("Detected app tracking location in ").append(String.valueOf(function)).append("\n");
                            }
                        } else if (androidClazz.equals("android/hardware/SensorManager") && androidMethod.equals("getDefaultSensor")) {
                            if (!function.privacyWord.usesSensor) {
                                function.privacyWord.usesSensor = true;
                                writer.append("Detected app starting a sensor in ").append(String.valueOf(function)).append("\n");
                            }
                        }
                    }
                } else {
                    Matcher invoke = invokePattern.matcher(line);
                    if (invoke.find()) {
                        String invokeClazz = invoke.group(2);
                        String invokeMethod = invoke.group(3);
                        if (!invokeClazz.startsWith("java/")) {
                            Function callee = functions.get(invokeClazz + "." + invokeMethod + "(" + invoke.group(4) + ")");
                            if (callee == null) {
                                callee = new Function(invokeMethod, invokeClazz, invoke.group(4), invoke.group(5), line.contains("invoke-static"));
                                functions.put(callee.toString(), callee);
                            }
                            callee.beCalledBy.add(function);
                            function.doCalls.add(callee);
                        } else if (scanPrivacy) {
                            if (invokeClazz.equals("java/net/HttpURLConnection") && invokeMethod.equals("getOutputStream")) {
                                inHttpConnect = true;
                            } else if (inHttpConnect && invokeClazz.equals("java/io/DataOutputStream") && invokeMethod.equals("write")) {
                                if (!function.privacyWord.upload) {
                                    function.privacyWord.upload = true;
                                    writer.append("Detected app uploading to Internet in ").append(String.valueOf(function)).append("\n");
                                }
                            }
                        }
                    } else {
                        if (scanActivity) {
                            Matcher cClazz = constClassPattern.matcher(line);
                            if (cClazz.find()) {
                                createClass = cClazz.group(1).replace("/", ".");
                            }
                        }
                    }
                }
            }
        }
        writer.flush();
    }

    public Set<Function> scanCallGraph(Set<Function> functions,BufferedWriter writer)throws IOException{
        class Node{
            final Function function;
            final ScanDirection direction;
            public Node(Function function, ScanDirection direction) {
                this.function = function;
                this.direction = direction;
            }

        }
        Queue<Node> queue=new LinkedList<>();
        for(Function function:functions){
            if(scanActivity&&function.haveSpecialActivity()){
                queue.add(new Node(function,ScanDirection.START));
            }
            if(scanPrivacy&&(function.usesPrivacy()||function.privacyWord.isEncrypted)){
                queue.add(new Node(function,ScanDirection.START));
            }
        }
        Set<Function> rootFunctions=new HashSet<>();
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            Function f = node.function;
            f.credit = f.privacyWord.makeCredit() / (f.beCalledBy.size() + 1);
            if (node.direction == ScanDirection.START || node.direction == ScanDirection.BACK) {
                for (Function caller : f.beCalledBy) {
                    if (scanActivity && !caller.activities.equals(f.activities)) {
                        caller.activities.addAll(f.activities);
                        queue.add(new Node(caller, ScanDirection.BACK));
                    }
                    if (scanPrivacy && (caller.privacyWord.differ(f.privacyWord) || (f.privacyWord.isEncrypted && !caller.privacyWord.isEncrypted))) {
                        caller.privacyWord.merge(f.privacyWord);
                        if (f.privacyWord.isEncrypted) caller.privacyWord.isEncrypted = true;
                        if (scanActivity && !caller.haveSpecialActivity()) {
                            caller.activities.add(new Activity(caller.toString()));
                        }
                        queue.add(new Node(caller, ScanDirection.BACK));
                    }
                    if (caller.beCalledBy.size() == 0 && (caller.usesPrivacy() || caller.haveSpecialActivity())) {
                        rootFunctions.add(caller);
                    }
                    writer.append(String.valueOf(caller)).append("->").append(String.valueOf(f)).append("\n");
                }
            }
        }
        for (Function f : rootFunctions) {
            StringBuilder stringBuilder = new StringBuilder("root caller: ");
            stringBuilder.append(f);
            if (scanPrivacy) {
                if (f.usesPrivacy())
                    stringBuilder.append(f.privacyWord);
            }
            if (scanActivity) {
                if (!f.activities.isEmpty())
                    stringBuilder.append(f.getActivitiesString());
            }
            f.privacyWord.toActivity(activities);
            stringBuilder.append("\n");
            writer.append(stringBuilder);
        }
        writer.flush();
        return rootFunctions;
    }

    enum ScanDirection{
        FORWARD,BACK,START;
    }
}

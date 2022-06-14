package com.nju.ds.policy;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;

import java.io.*;
import java.util.*;

/**
 * 利用HanLP获取隐私政策中的个人信息
 * 首先通过关键词筛选对应句子
 * 去掉停用词
 * 分词获取句子中的动词
 * 若动词包含“收集、获取”等，则声明使用了该个人信息
 * 最后摘要包含该信息的的所有句子，增强准确率
 */
public class Segment {

  public static HashSet<PolicyAction> policyActions =new HashSet<>();

  public static BufferedWriter writer;
  public static Set<PolicyAction> scan(String filename,BufferedWriter writer) throws IOException {
    Segment.writer=writer;
    for (Stoken stoken : Stoken.values()) {
      writer.append(stoken.toString());
      writer.newLine();
      String use=String.valueOf(UseOrNot(stoken, filename));
      writer.append("是否使用:").append(use);
      writer.newLine();
      writer.newLine();
      writer.flush();
    }
      writer.flush();
    return policyActions;
  }

  public static boolean UseOrNot(Stoken stoken,String filename)throws IOException {

    boolean isUse = false;

    //得到含有标签的句子
    SearchSentence s = new SearchSentence();
    s.getInfo(filename);
    StringBuilder str = new StringBuilder();

    //分词
    for (String sentence : Objects.requireNonNull(transformation(stoken.toString()))) {
      str.append(sentence);
      writer.append("包含该信息的句子： ").append(sentence).append("\n");  //原始句子
      writer.append("去除停用词后： ").append(String.valueOf(NotionalTokenizer.segment(sentence))).append("\n");//去除停用词
      List<Term> terms = NotionalTokenizer.segment(sentence);//标准分词
      writer.append("提取动词后： ");
      writer.newLine();
      Term verb = null;
      for (Term t : terms) {
        if (t.nature == Nature.vn) {
          isUse = true;
          writer.append(String.valueOf(t)).append(" ");
          for (Stoken stokenv : Stoken.values()) {
            for (String w : stokenv.words) {
              if (t.word.contains(w))
                policyActions.add(new PolicyAction(t.word));
            }
          }
        } else if (t.nature == Nature.v) {
          isUse = true;
          verb = t;
          writer.append(String.valueOf(t)).append("  ");
        } else if ((t.nature == Nature.n || t.nature == Nature.nz) && verb != null) {
          for (Stoken stokenv : Stoken.values()) {
            for (String w : stokenv.words) {
              if (t.word.contains(w))
                policyActions.add(new PolicyAction(verb.word + t.word));
            }
          }
        }
      }
    }
    writer.newLine();
    writer.newLine();
    // 摘要
    List<String> sentenceList = HanLP.extractSummary(str.toString(), 3);
    writer.append("摘要： ").append(String.valueOf(sentenceList));
    writer.flush();
    return isUse;
  }

  public static ArrayList<String> transformation(String token){
    switch(token){
      case "PHONE":
        return SearchSentence.PHONE;
      case "LOCATION":
        return SearchSentence.LOCATION;
      case "SENSOR":
        return SearchSentence.SENSOR;
      case "ACTIVITY_RECOGNITION":
        return SearchSentence.ACTIVITY_RECOGNITION;
      case "CAMERA":
        return SearchSentence.CAMERA;
      case "CONTACT":
        return SearchSentence.CONTACT;
      case "CALENDAR":
        return SearchSentence.CALENDAR;
      case "SMS":
        return SearchSentence.SMS;
      case "MICROPHONE":
        return SearchSentence.MICROPHONE;
      case "STORAGE":
        return SearchSentence.STORAGE;
      case "NEARBY_DEVICES":
        return SearchSentence.NEARBY_DEVICES;
      default:
        return null;
    }
  }


  public static class SearchSentence{

    public static ArrayList<String> PHONE = new ArrayList<>();
    public static ArrayList<String> LOCATION = new ArrayList<>();
    public static ArrayList<String> SENSOR = new ArrayList<>();
    public static ArrayList<String> ACTIVITY_RECOGNITION = new ArrayList<>();
    public static ArrayList<String> CAMERA = new ArrayList<>();
    public static ArrayList<String> CONTACT = new ArrayList<>();
    public static ArrayList<String> CALENDAR = new ArrayList<>();
    public static ArrayList<String> SMS = new ArrayList<>();
    public static ArrayList<String> MICROPHONE= new ArrayList<>();
    public static ArrayList<String> STORAGE = new ArrayList<>();
    public static ArrayList<String> NEARBY_DEVICES = new ArrayList<>();


    public void getInfo(String filepath) {
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
        String data = "";
        while ((data = br.readLine()) != null) {
          if (involve(data, Stoken.PHONE.words)) PHONE.add(data);
          if (involve(data, Stoken.LOCATION.words)) LOCATION.add(data);
          if (involve(data, Stoken.SENSOR.words)) SENSOR.add(data);
          if (involve(data, Stoken.ACTIVITY_RECOGNITION.words)) ACTIVITY_RECOGNITION.add(data);
          if (involve(data, Stoken.CAMERA.words)) CAMERA.add(data);
          if (involve(data, Stoken.CONTACT.words)) CONTACT.add(data);
          if (involve(data, Stoken.CALENDAR.words)) CALENDAR.add(data);
          if (involve(data, Stoken.SMS.words)) SMS.add(data);
          if (involve(data, Stoken.MICROPHONE.words)) MICROPHONE.add(data);
          if (involve(data, Stoken.STORAGE.words)) STORAGE.add(data);
          if (involve(data, Stoken.NEARBY_DEVICES.words)) NEARBY_DEVICES.add(data);
        }
      }catch (IOException e){
        e.printStackTrace();
      }
    }

    public boolean involve(String str, String[] arr){
        for(String s : arr){
            if(str.contains(s)) return true;
        }
        return false;
    }
  }
}

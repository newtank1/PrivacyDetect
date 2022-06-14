package com.nju.ds.policy;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *参考国家互联网信息办公室、工业和信息化部、公安部、国家市场监督管理总局近日联合印发《常见类型移动互联网应用程序必要个人信息范围规定》
 *《规定》明确了39种常见类型APP的必要个人信息范围
 */

public class AutomaticClassification {

  public static Set<String> TrackInformation = new HashSet<>();
  public static Set<String> FinancialAccountInformation = new HashSet<>();
  public static Set<String> MedicalAndHealthInformation = new HashSet<>();
  public static Set<String> SpecificIdentityInformation = new HashSet<>();
  public static Set<String> ReligiousBeliefInformation = new HashSet<>();
  public static Set<String> BiometricInformation = new HashSet<>();
  public static Set<String> MinorInformation = new HashSet<>();
  public static BufferedWriter writer;
  public static void scan(String fileName,BufferedWriter writer)throws IOException {
    AutomaticClassification a = new AutomaticClassification();
    a.Classification(fileName);
    AutomaticClassification.writer=writer;
    writer.append("行踪轨迹信息: ").append(TrackInformation.toString());
    writer.newLine();
    writer.append("金融账户信息: ").append(FinancialAccountInformation.toString());
    writer.newLine();
    writer.append("医疗健康信息: ").append(MedicalAndHealthInformation.toString());
    writer.newLine();
    writer.append("特定身份信息: ").append(SpecificIdentityInformation.toString());
    writer.newLine();
    writer.append("宗教信仰信息: ").append(ReligiousBeliefInformation.toString());
    writer.newLine();
    writer.append("未满14周岁未成年人的个人信息: ").append(MinorInformation.toString());
    writer.newLine();
    writer.flush();
  }

  public void Classification(String fileName) {
    // 将个人信息关键词插入到用户词典
    Initialize();
    try{
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
      String data = "";
      while ((data = br.readLine()) != null) {
        StandardTokenizer.SEGMENT.enablePartOfSpeechTagging(true);  // 支持隐马词性标注
        List<Term> termList = HanLP.segment(data);
        for (Term term : termList) {
          Add(term);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void Initialize() {
    String nature1 = "行踪轨迹信息 1024";
    String nature2 = "金融账户信息 1024";
    String nature3 = "医疗健康信息 1024";
    String nature4 = "特定身份信息 1024";
    String nature5 = "宗教信仰信息 1024";
    String nature6 = "生物识别信息 1024";
    String nature7 = "未满14周岁未成年人的个人信息 1024";
    // 初始化个人词典
    CustomDictionary.insert("位置", nature1);
    CustomDictionary.insert("地址", nature4);
    CustomDictionary.insert("出发地", nature1);
    CustomDictionary.insert("到达地", nature1);
    CustomDictionary.insert("出发时间", nature1);
    CustomDictionary.insert("车牌号", nature1);
    CustomDictionary.insert("场次", nature1);
    CustomDictionary.insert("座位号", nature1);
    CustomDictionary.insert("支付账号", nature2);
    CustomDictionary.insert("支付金额", nature2);
    CustomDictionary.insert("支付时间", nature2);
    CustomDictionary.insert("支付渠道", nature2);
    CustomDictionary.insert("资金账户", nature2);
    CustomDictionary.insert("银行卡号码", nature2);
    CustomDictionary.insert("电话号码", nature2);
    CustomDictionary.insert("病历", nature3);
    CustomDictionary.insert("病情", nature3);
    CustomDictionary.insert("医院", nature3);
    CustomDictionary.insert("姓名", nature4);
    CustomDictionary.insert("电话号码", nature4);
    CustomDictionary.insert("地址", nature4);
    CustomDictionary.insert("年龄", nature4);
    CustomDictionary.insert("婚姻状况", nature4);
    CustomDictionary.insert("座位号", nature4);
    CustomDictionary.insert("证件号码", nature4);
    CustomDictionary.insert("联系人", nature4);
    CustomDictionary.insert("电话号码", nature4);
    CustomDictionary.insert("简历", nature4);
    CustomDictionary.insert("通信联系人账号", nature4);
    CustomDictionary.insert("证件号码", nature4);
    CustomDictionary.insert("联系方式", nature4);
    CustomDictionary.insert("身份证号码", nature4);
    CustomDictionary.insert("宗教信仰", nature5);
    CustomDictionary.insert("指纹", nature6);
    CustomDictionary.insert("人脸识别", nature6);
    CustomDictionary.insert("性别", nature6);
    CustomDictionary.insert("面部信息", nature7);
    CustomDictionary.insert("孩子", nature7);
    CustomDictionary.insert("儿童", nature7);
  }

  public void Add(Term term){
    if(term.nature == Nature.create("行踪轨迹信息")){
        TrackInformation.add(term.word);
    }
    if(term.nature == Nature.create("金融账户信息")){
      FinancialAccountInformation.add(term.word);
    }
    if(term.nature == Nature.create("医疗健康信息")){
      MedicalAndHealthInformation.add(term.word);
    }
    if(term.nature == Nature.create("特定身份信息")){
      SpecificIdentityInformation.add(term.word);
    }
    if(term.nature == Nature.create("宗教信仰信息")){
      ReligiousBeliefInformation.add(term.word);
    }
    if(term.nature == Nature.create("生物识别信息")){
      BiometricInformation.add(term.word);
    }
    if(term.nature == Nature.create("未满14周岁未成年人的个人信息")){
      MinorInformation.add(term.word);
    }
  }
}

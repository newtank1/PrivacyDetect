package com.nju.ds.policy;

/**
 * 含有app收集信息标签的枚举类
 */

public enum Stoken {

  PHONE(new String[]{"电话", "手机","通话","拨号"}),
  LOCATION(new String[]{"位置", "定位","区域"}),
  SENSOR(new String[]{"传感器","速度","轨迹","重力","磁力","方向","步数"}),
  ACTIVITY_RECOGNITION(new String[]{}),
  CAMERA(new String[]{"录像", "摄像", "相机","拍摄","录制"}),
  CONTACT(new String[]{"联系人","通讯录"}),
  CALENDAR(new String[]{"日历", "日程", "待办"}),
  SMS(new String[]{"邮件", "短信","电子信息","彩信"}),
  MICROPHONE(new String[]{"人声","语音"}),
  STORAGE(new String[]{"存储","内存"}),
  NEARBY_DEVICES(new String[]{"附近设备"});

  public String[] words ;

  Stoken( String[] words){
    this.words = words ;
  }
}

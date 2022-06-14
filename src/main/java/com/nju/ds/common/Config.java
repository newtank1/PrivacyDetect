package com.nju.ds.common;

public class Config {

    public static final String ROOT_DIR=System.getProperty("user.dir");

    public static final String APKTOOL_PATH=ROOT_DIR+"\\lib\\apktool_2.6.0.jar";

    public static final String CACHE_PATH=ROOT_DIR+"\\cache\\";

    public static final String ANDROIDMANIFEST="AndroidManifest.xml";

    public static final String ANDROID_INVOKE_REGEX ="invoke-.*?\\{(.*?)\\}, L(android/.*?);->(.*?)\\((.*?)\\)(.*)";

    public static final String INVOKE_REGEX ="invoke-.*?\\{(.*?)\\}, L((?!android).*?);->(.*?)\\((.*?)\\)(.*)";

    public static final String FUNCTION_REGEX="(.*?)\\((.*?)\\)(.*)";

    public static final String CONST_STRING_REGEX="const-string(?:/jumbo)? (?:.*?), \"(.*?)\"";

    public static final String CONST_CLASS_REGEX="const-class (?:.*?), L(.*?);";

    public static final String[] ENCRYPT_TABLE={"Encrypt","encrypt","RSA","Rsa","MD5","md5","Base64","BASE64","base64","DES","Cipher","cipher","SHA","HMAC"};
}

package com.nju.ds.apk;

public class Source {
    public Manifest manifest;
    public Code code;
    public String path;
    public String name;


    public Source(Manifest manifest, Code code, String path, String name) {
        this.manifest = manifest;
        this.code = code;
        this.path = path;
        this.name = name;
    }
}

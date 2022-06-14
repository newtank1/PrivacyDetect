package com.nju.ds.code;

public class Invoker {
    public String clazz;
    public String name;
    public int lineNum;

    public Invoker(String clazz, String name, int lineNum) {
        this.clazz = clazz;
        this.name = name;
        this.lineNum = lineNum;
    }

    @Override
    public String toString() {
        return "Invoker{" +
                "clazz='" + clazz + '\'' +
                ", name='" + name + '\'' +
                ", lineNum=" + lineNum +
                '}';
    }
}

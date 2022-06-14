package com.nju.ds.code;

import java.util.Objects;

public class Call {
    public Function caller;
    public Function callee;

    public Call(Function caller, Function callee) {
        this.caller = caller;
        this.callee = callee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Call call = (Call) o;
        return caller.equals(call.caller) && callee.equals(call.callee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caller, callee);
    }

    @Override
    public String toString() {
        return "Call{" +
                "caller=" + caller +
                ", callee=" + callee +
                '}';
    }
}

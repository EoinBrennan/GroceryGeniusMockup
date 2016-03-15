package com.team5.grocerygeniusmockup.Model;

/**
 * Created by user on 15/03/2016.
 */

public class NumStore {
    private int num;

    public NumStore(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void add(int a) {
        num += a;
    }

    public void take(int b) {
        num -= b;
    }
}

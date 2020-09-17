package com.example.zckj.math_tool;

/**
 * Created by ZCKJ on 2019/4/7.
 */

public class Item {
    private int type;
    private double num;
    private String oper;

    public Item(int type, double num, String oper){
        this.type = type;
        this.num = num;
        this.oper = oper;
    }

    public Item(){
        type = -1;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setNum(double num){
        this.num = num;
    }

    public void setOper(String oper){
        this.oper = oper;
    }

    public int getType(){
        return type;
    }

    public double getNum(){
        return num;
    }

    public String getOper(){
        return oper;
    }

}

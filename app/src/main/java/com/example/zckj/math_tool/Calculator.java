package com.example.zckj.math_tool;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by ZCKJ on 2019/4/7.
 */

public class Calculator {
    public static double calculate(String expression){
        int length = expression.length();
        Queue<Item> firstExp = new LinkedList<Item>();
        firstExp.clear();
        int flag = -1;
        String tmp = "";
        for(int i=0; i<length; i++){
            if(expression.charAt(i)!=' ' && expression.charAt(i)!='\n'){
                if((expression.charAt(i)>='0' && expression.charAt(i)<='9') || expression.charAt(i)=='.'){
                    if(flag==1 || flag==2){
                        Item item = new Item();
                        item.setType(getOrder(tmp));
                        item.setOper(tmp);
                        firstExp.offer(item);
                        flag = 0;
                        tmp = "";
                    }
                    if(flag == -1){
                        flag = 0;
                    }
                }else{
                    if(expression.charAt(i)>='a' && expression.charAt(i)<='z'){
                        if(flag == 0){
                            Item item = new Item();
                            item.setType(2);
                            item.setNum(Double.parseDouble(tmp));
                            firstExp.offer(item);
                            flag = 2;
                            tmp = "";
                        }else if(flag == 1){
                            Item item = new Item();
                            item.setType(getOrder(tmp));
                            item.setOper(tmp);
                            firstExp.offer(item);
                            flag = 2;
                            tmp = "";
                        }else if(flag==2 && getOrder(tmp)!=-1){
                            Item item = new Item();
                            item.setType(getOrder(tmp));
                            item.setOper(tmp);
                            firstExp.offer(item);
                            tmp = "";
                        }
                        if(flag == -1){
                            flag = 2;
                        }
                    }else{
                        if(flag == 0){
                            Item item = new Item();
                            item.setType(2);
                            item.setNum(Double.parseDouble(tmp));
                            firstExp.offer(item);
                            flag = 1;
                            tmp = "";
                        }else if(flag == 2){
                            Item item = new Item();
                            item.setType(getOrder(tmp));
                            item.setOper(tmp);
                            firstExp.offer(item);
                            flag = 1;
                            tmp = "";
                        }else if(flag == 1){
                            Item item = new Item();
                            item.setType(getOrder(tmp));
                            item.setOper(tmp);
                            firstExp.offer(item);
                            tmp = "";
                        }
                        if(flag == -1){
                            flag = 1;
                        }
                    }
                }
                tmp += expression.charAt(i);
            }
        }
        if(flag == 0){
            Item item = new Item();
            item.setType(2);
            item.setNum(Double.parseDouble(tmp));
            firstExp.offer(item);
        }else{
            Item item = new Item();
            item.setType(1);
            item.setOper(tmp);
            firstExp.offer(item);
        }
        tmp = "";

        boolean frontHasNum = false;
        Stack<Item> operStack = new Stack<Item>();
        operStack.clear();
        Queue<Item> secondExp = new LinkedList<Item>();
        secondExp.clear();
        Item firstItem = null;
        while((firstItem=firstExp.poll()) != null){
            if(firstItem.getType() == 2){
                frontHasNum = true;
                secondExp.offer(firstItem);
            }else{
                if(firstItem.getOper().equals("(")){
                    frontHasNum = false;
                    operStack.push(firstItem);
                }else if(firstItem.getOper().equals(")")){
                    frontHasNum = true;
                    Item t = null;
                    while(!operStack.peek().getOper().equals("(")){
                        if((t=operStack.pop()) == null){
                            break;
                        }
                        secondExp.offer(t);
                    }
                    operStack.pop();
                }else{
                    Item t = null;
                    while((!operStack.isEmpty() && getPriority(firstItem.getOper())<getPriority(operStack.peek().getOper())) || (!operStack.isEmpty() && getPriority(firstItem.getOper())==getPriority(operStack.peek().getOper()) && getCombination(operStack.peek().getOper())==1)){
                        if((t=operStack.pop()) == null){
                            break;
                        }
                        secondExp.offer(t);
                    }
                    if(!frontHasNum){
                        frontHasNum = true;
                        firstItem.setType(0);
                    }
                    operStack.push(firstItem);
                    /*if(firstItem.getOper().equals("<") || firstItem.getOper().equals(">")){
                        frontHasNum = false;
                    }*/
                    if(frontNoNum(firstItem.getOper())){
                        frontHasNum = false;
                    }
                }
            }
        }
        while(!operStack.isEmpty()){
            firstItem=operStack.pop();
            secondExp.offer(firstItem);
        }

        Stack<Item> startCalculate = new Stack<Item>();
        startCalculate.clear();
        Item startItem = null;
        while((startItem=secondExp.poll()) != null){
            if(startItem.getType() == 2){
                startCalculate.push(startItem);
            }else{
                if(startItem.getType() == 0){
                    Item a = null;
                    a = startCalculate.pop();
                    a.setNum(getResult0(startItem.getOper(), a.getNum()));
                    startCalculate.push(a);
                }else if(startItem.getType() == 1){
                    Item a1=null, a2=null;
                    a2 = startCalculate.pop();
                    a1 = startCalculate.pop();
                    Item a = new Item();
                    a.setType(2);
                    a.setNum(getResult1(startItem.getOper(), a1.getNum(), a2.getNum()));
                    startCalculate.push(a);
                }
            }
        }

        Item result = null;
        result = startCalculate.pop();
        return result.getNum();
    }

    public static int getPriority(String a){
        if(a.equals("&") || a.equals("|"))
            return 0;
        if(a.equals("<") || a.equals(">") || a.equals("equ"))
            return 1;
        if(a.equals("max") || a.equals("min"))
            return 2;
        if(a.equals("+") || a.equals("-"))
            return 3;
        if(a.equals("*") || a.equals("/") || a.equals("%"))
            return 4;
        if(a.equals("^") || a.equals("A") || a.equals("C") || a.equals("gcd"))
            return 5;
        if(a.equals("sin") || a.equals("cos") || a.equals("tan") || a.equals("ln") || a.equals("exp") || a.equals("arcsin") || a.equals("arccos") || a.equals("arctan") || a.equals("fac") || a.equals("log") || a.equals("abs") || a.equals("fix"))
            return 6;
        return -1;
    }

    public static int getCombination(String a){	//1表示左结合，0表示右结合
        if(a.equals("+") || a.equals("-") || a.equals("*") || a.equals("/") || a.equals("^") || a.equals("A") || a.equals("C") || a.equals("%") || a.equals("<") || a.equals(">") || a.equals("&") || a.equals("|") || a.equals("equ") || a.equals("max") || a.equals("min") || a.equals("gcd"))
            return 1;
        if(a.equals("sin") || a.equals("cos") || a.equals("tan") || a.equals("ln") || a.equals("exp") || a.equals("arcsin") || a.equals("arccos") || a.equals("arctan") || a.equals("fac") || a.equals("log") || a.equals("abs") || a.equals("fix"))
            return 0;
        return -1;
    }

    public static int getOrder(String a){			//0表示单目，1表示双目
        if(a.equals("+") || a.equals("-") || a.equals("*") || a.equals("/") || a.equals("^") || a.equals("A") || a.equals("C") || a.equals("log") || a.equals("%") || a.equals("<") || a.equals(">") || a.equals("&") || a.equals("|") || a.equals("equ") || a.equals("max") || a.equals("min") || a.equals("gcd"))
            return 1;
        if(a.equals("sin") || a.equals("cos") || a.equals("tan") || a.equals("ln") || a.equals("exp") || a.equals("arcsin") || a.equals("arccos") || a.equals("arctan") || a.equals("fac") || a.equals("abs") || a.equals("fix"))
            return 0;
        return -1;
    }

    public static boolean frontNoNum(String oper){         //出现这个运算符是否触发“前面无数字”状态
        if(oper.equals("<") || oper.equals(">") || oper.equals("max") || oper.equals("min") || oper.equals("equ") || oper.equals("&") || oper.equals("|"))
            return true;
        return false;
    }

    public static double getResult0(String oper, double a1){
        if(oper.equals("+"))
            return a1;
        if(oper.equals("-"))
            return -a1;
        if(oper.equals("sin"))
            return Math.sin(a1);
        if(oper.equals("cos"))
            return Math.cos(a1);
        if(oper.equals("tan"))
            return Math.tan(a1);
        if(oper.equals("ln"))
            return Math.log(a1);
        if(oper.equals("exp"))
            return Math.exp(a1);
        if(oper.equals("arcsin"))
            return Math.asin(a1);
        if(oper.equals("arccos"))
            return Math.acos(a1);
        if(oper.equals("arctan"))
            return Math.atan(a1);
        if(oper.equals("fac"))
            return AdditionMath.fac(a1);
        if(oper.equals("abs"))
            return Math.abs(a1);
        if(oper.equals("fix")) {
            if(a1 >= 0)
                return a1-a1%1;
            else
                return a1-a1%1-1;
        }
        return 0;
    }
    public static double getResult1(String oper, double a1, double a2){
        if(oper.equals("+"))
            return a1+a2;
        if(oper.equals("-"))
            return a1-a2;
        if(oper.equals("*"))
            return a1*a2;
        if(oper.equals("/"))
            return a1/a2;
        if(oper.equals("^"))
            return Math.pow(a1,a2);
        if(oper.equals("A"))
            return AdditionMath.arr(a1, a2);
        if(oper.equals("C"))
            return AdditionMath.com(a1, a2);
        if(oper.equals("log"))
            return Math.log(a2)/Math.log(a1);
        if(oper.equals("%"))
            return a1%a2;
        if(oper.equals("<")) {
            if(a1 < a2)
                return 1;
        }
        if(oper.equals(">")) {
            if(a1 > a2)
                return 1;
        }
        if(oper.equals("&")) {
            if(a1!=0 && a2!=0)
                return 1;
        }
        if(oper.equals("|")) {
            if(a1!=0 || a2!=0)
                return 1;
        }
        if(oper.equals("equ")){
            if(a1 == a2)
                return 1;
        }
        if(oper.equals("max")){
            if(a1 >= a2)
                return a1;
            else
                return a2;
        }
        if(oper.equals("min")){
            if(a1 <= a2)
                return a1;
            else
                return a2;
        }
        if(oper.equals("gcd"))
            return AdditionMath.gcd(a1, a2);
        return 0;
    }

}

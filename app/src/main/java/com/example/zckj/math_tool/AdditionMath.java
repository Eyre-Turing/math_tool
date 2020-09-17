package com.example.zckj.math_tool;

/**
 * Created by ZCKJ on 2019/4/14.
 */

public class AdditionMath {
    public static double fac(double a){
        double result = 1;
        for(int i=2; i<=a; i++){
            result*=i;
        }
        return result;
    }
    public static double arr(double n, double r){
        double result;
        result = fac(n)/fac(n-r);
        return result;
    }
    public static double com(double n, double r){
        double result;
        result = arr(n, r)/arr(r, r);
        return result;
    }
    public static double gcd(double a1, double a2){
        double a = Math.abs(a1);
        double b = Math.abs(a2);
        a = a-a%1;
        b = b-b%1;
        if(a==0 && b==0)
            return 0;
        if(a == 0)
            return b;
        if(b == 0)
            return a;
        double c;
        do{
            c = a%b;
            a = b;
            b = c;
        }while(c != 0);
        return a;
    }
}

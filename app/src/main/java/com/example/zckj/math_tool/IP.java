package com.example.zckj.math_tool;

import android.annotation.SuppressLint;

import java.util.Vector;

/**
 * Created by ZCKJ on 2019/11/10.
 */

public class IP extends LP {

    private String lpstr;
    private Vector<Integer> ivar;

    public IP(String lpstr, Vector<Integer> ivar) throws Exception {
        super(lpstr);
        this.lpstr = lpstr;
        this.ivar = ivar;
        // TODO Auto-generated constructor stub
    }

    public boolean isTargetGetMin() {	//返回目标是否为求极小值
        if(lpstr.replace(" ", "").replace("\n", "").split(":")[0].equals("min")) {
            return true;
        } else {
            return false;
        }
    }

    public double subs(Matrix val) throws Exception {	//把变量值代入目标函数求值（都会化成求极大值的标准形式后再计算）
        Matrix target = getTarget();
        Matrix v = target.multiply(val.lineAt(0, target.getSize().getCol()));
        if(v == null) {
            throw new Exception("integer programme subs bug");
        }
        return v.get(0, 0);
    }

    public Matrix isolve() {
        Matrix result = solve();	//先计算无整数限制时的解
        if(result.isEmpty()) {	//无最优解直接返回
            return result;
        }
        int index = -1;	//第一个不是整数的有整数限制的变量的下标
        for(int i : ivar) {	//检查每一个有整数限制的变量是否是整数
            if(Math.abs(result.get(i-1, 0)%1) > Matrix.ALMOST_NULL) {	//不是整数
                index = i;
                break;
            }
        }
        if(index != -1) {	//说明有变量要求是整数，但是该变量计算出的结果不是整数
            try{
                double curVal = result.get(index-1, 0);	//当前操作的变量值
                double leftV = curVal-curVal%1;	//整数下确界
                double rightV = leftV+1;		//整数上确界
                String conditionV = ";";			//限制条件符号左边
                for(int i=1; i<index; i++) {
                    conditionV += "0,";
                }
                conditionV += "1";

                //分成两枝
                IP lIP = new IP(lpstr+conditionV+"<"+leftV, ivar);
                IP rIP = new IP(lpstr+conditionV+">"+rightV, ivar);

                Matrix ls = lIP.isolve();
                Matrix rs = rIP.isolve();

                if(ls.isEmpty()) {
                    if(rs.isEmpty()) {
                        result = new Matrix(0);
                    } else {
                        result = rs;
                    }
                } else {
                    if(rs.isEmpty()) {
                        result = ls;
                    } else {	//两枝都有解
                        double lt = lIP.subs(ls);
                        double rt = rIP.subs(rs);
                        result = (lt>rt)?ls:rs;
                    }
                }
            } catch (Exception e) {
                //System.out.println(e.toString());
            }
        }
        return result;
    }

    public void print() {
        super.print();
        System.out.println();
        System.out.println("integer:");
        System.out.println("[");
        for(int i : ivar) {
            System.out.print("\t"+i);
        }
        System.out.println();
        System.out.println("]");
    }

    /*
     * text format:
     * linear programme expression
     * int:int var indexs, each var index split by ','
     */
    @SuppressLint("NewApi")
    public static IP initIP(String text) throws Exception {
        Vector<Integer> ivar = new Vector<Integer>();
        String []t = text.split("int:");
        if(t.length == 2) {	//有整数变量限制
            String []t1 = t[1].replace(" ", "").replace("\n", "").replace(";", "").split(",");
            for(String i : t1) {
                if(ivar.indexOf(Integer.parseInt(i)) == -1) {
                    ivar.add(Integer.parseInt(i));
                }
            }
        }
        int c = text.split(";")[0].split(",").length;
        for(int i : ivar) {
            if(i > c) {
                throw new Exception("integer var index too big");
            }
        }
        ivar.sort(null);
        return new IP(t[0], ivar);
    }

}

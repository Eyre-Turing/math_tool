package com.example.zckj.math_tool;

import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.os.Handler;

import java.util.Vector;

/**
 * Created by ZCKJ on 2019/4/7.
 */

public class ImageViewTouch implements View.OnTouchListener {
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;
    private float rx;
    private float ry;
    private float rx2;
    private float ry2;
    private float d;
    private float d2;
    private int w;
    private int h;
    private String expression;
    private Lagrange lagrange;
    private CheckBox lagCheckBox;
    private EditText lagEdit;
    private boolean initAble = true;
    private boolean canChange = false;
    private String mode = "";
    private Handler handler = null;
    public float []ns = null;       //minx, maxx, miny, maxy

    public void setNs(float []ns){
        this.ns = ns;
    }

    public void setSize(int w, int h){
        this.w = w;
        this.h = h;
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    public void setExpression(String expression){
        this.expression = expression;
    }

    public void setLagrange(Lagrange lagrange) {
        this.lagrange = lagrange;
    }

    public String getExpression() {
        return expression;
    }

    public String getLagExp(String m) {
        String result = "";
        if(m.equals("y=")) {
            result = lagrange.getLagrangerStr();
            if (result.equals("")) {
                result = lagrange.getLagrangerStr_();
            }
        } else if(m.equals("x=")) {
            result = lagrange.getLagrangerStr_();
            if (result.equals("")) {
                result = lagrange.getLagrangerStr();
            }
        }
        return result;
    }

    public void setLagCheckBox(CheckBox lagCheckBox) {
        this.lagCheckBox = lagCheckBox;
    }

    public void setLagEdit(EditText lagEdit) {
        this.lagEdit = lagEdit;
    }

    public void setMode(String mode){
        this.mode = mode;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //view.requestFocus();
        int add = 1;
        if(motionEvent.getPointerCount() == 1){
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    canChange = true;
                    if(mode.equals("lagrange") && lagCheckBox.isChecked()) {
                        marker((ImageView) view, startX, startY);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(canChange) {
                        stopX = motionEvent.getX();
                        stopY = motionEvent.getY();
                        if(mode.equals("function")){
                            add = 10;
                        }else if(mode.equals("equation")){
                            add = 100;
                        } else if(mode.equals("lagrange")) {
                            if(lagCheckBox.isChecked()) {
                                //ImagePainter.breakPaint();
                                //marker((ImageView) view, startX, startY, stopX, stopY);
                                startX = motionEvent.getX();
                                startY = motionEvent.getY();
                                break;
                            } else {
                                add = 10;
                                moveFun(startX, startY, stopX, stopY);
                                ImagePainter.breakPaint();
                                try {
                                    String format = "y=";
                                    String t = lagEdit.getText().toString().replace(" ", "").replace("\n", "");
                                    if(t.length()>=2 && t.substring(0, 2).equals("x=")) {
                                        format = "x=";
                                    }
                                    final String lagExp = getLagExp(format);
                                    ImagePainter.paintPoint((ImageView) view, expression + ";" + lagExp, lagrange.getPoints(), ns[0], ns[1], ns[2], ns[3], add, handler, false);
                                } catch (Exception e) {

                                }
                                startX = motionEvent.getX();
                                startY = motionEvent.getY();
                                break;
                            }
                        }
                        ImagePainter.breakPaint();
                        moveFun((ImageView) view, startX, startY, stopX, stopY, add);
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    startX = 0;
                    startY = 0;
                    stopX = 0;
                    stopY = 0;
                    if(mode.equals("function")){
                        add = 1;
                    }else if(mode.equals("equation")){
                        add = 10;
                    } else if(mode.equals("lagrange")) {
                        if(lagCheckBox.isChecked()) {
                            canChange = false;
                            break;
                        } else {
                            add = 1;
                            ImagePainter.breakPaint();
                            try {
                                String format = "y=";
                                String t = lagEdit.getText().toString().replace(" ", "").replace("\n", "");
                                if(t.length()>=2 && t.substring(0, 2).equals("x=")) {
                                    format = "x=";
                                }
                                final String lagExp = getLagExp(format);
                                ImagePainter.paintPoint((ImageView) view, expression + ";" + lagExp, lagrange.getPoints(), ns[0], ns[1], ns[2], ns[3], add, handler, false);
                            } catch (Exception e) {

                            }
                            canChange = false;
                            break;
                        }
                    }
                    ImagePainter.breakPaint();
                    moveFun((ImageView)view, 0, 0, 0, 0, add);
                    canChange = false;
                    break;
                default:
                    break;
            }
            return true;
        }else if(motionEvent.getPointerCount() == 2){
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_POINTER_DOWN:
                    rx = motionEvent.getX(0)-motionEvent.getX(1);
                    ry = motionEvent.getY(0)-motionEvent.getY(1);
                    d = (float)Math.sqrt(rx*rx+ry*ry);
                    canChange = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(canChange) {
                        rx2 = motionEvent.getX(0) - motionEvent.getX(1);
                        ry2 = motionEvent.getY(0) - motionEvent.getY(1);
                        d2 = (float) Math.sqrt(rx2 * rx2 + ry2 * ry2);
                        if(mode.equals("function")){
                            add = 10;
                        }else if(mode.equals("equation")){
                            add = 100;
                        } else if(mode.equals("lagrange")) {
                            if(lagCheckBox.isChecked()) {
                                rx = motionEvent.getX(0) - motionEvent.getX(1);
                                ry = motionEvent.getY(0) - motionEvent.getY(1);
                                d = (float) Math.sqrt(rx * rx + ry * ry);
                                break;
                            } else {
                                add = 10;
                                reSize(d-d2);
                                ImagePainter.breakPaint();
                                try {
                                    String format = "y=";
                                    String t = lagEdit.getText().toString().replace(" ", "").replace("\n", "");
                                    if(t.length()>=2 && t.substring(0, 2).equals("x=")) {
                                        format = "x=";
                                    }
                                    final String lagExp = getLagExp(format);
                                    ImagePainter.paintPoint((ImageView) view, expression + ";" + lagExp, lagrange.getPoints(), ns[0], ns[1], ns[2], ns[3], add, handler, false);
                                } catch (Exception e) {

                                }
                                rx = motionEvent.getX(0) - motionEvent.getX(1);
                                ry = motionEvent.getY(0) - motionEvent.getY(1);
                                d = (float) Math.sqrt(rx * rx + ry * ry);
                                break;
                            }
                        }
                        ImagePainter.breakPaint();
                        reSize((ImageView) view, d - d2, add);
                        rx = motionEvent.getX(0) - motionEvent.getX(1);
                        ry = motionEvent.getY(0) - motionEvent.getY(1);
                        d = (float) Math.sqrt(rx * rx + ry * ry);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    rx = 0;
                    ry = 0;
                    rx2 = 0;
                    ry2 = 0;
                    d = 0;
                    d2 = 0;
                    if(mode.equals("function")){
                        add = 1;
                    }else if(mode.equals("equation")){
                        add = 10;
                    } else if(mode.equals("lagrange")) {
                        if(lagCheckBox.isChecked()) {
                            canChange = false;
                            break;
                        } else {
                            add = 1;
                            ImagePainter.breakPaint();
                            try {
                                String format = "y=";
                                String t = lagEdit.getText().toString().replace(" ", "").replace("\n", "");
                                if(t.length()>=2 && t.substring(0, 2).equals("x=")) {
                                    format = "x=";
                                }
                                final String lagExp = getLagExp(format);
                                ImagePainter.paintPoint((ImageView) view, expression + ";" + lagExp, lagrange.getPoints(), ns[0], ns[1], ns[2], ns[3], add, handler, false);
                            } catch (Exception e) {

                            }
                            canChange = false;
                            break;
                        }
                    }
                    ImagePainter.breakPaint();
                    reSize((ImageView)view, 0, add);
                    canChange = false;
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    public void moveFun(ImageView imageView, float startX, float startY, float stopX, float stopY, int add){
        float moveX = (startX-stopX)*(ns[1]-ns[0])/w;
        float moveY = (stopY-startY)*(ns[3]-ns[2])/h;
        ns[0]+=moveX;
        ns[1]+=moveX;
        ns[2]+=moveY;
        ns[3]+=moveY;
        try {
            if(mode.equals("function")) {
                ImagePainter.paintFunction(imageView, expression, ns[0], ns[1], ns[2], ns[3], add, handler, false);
            }else if(mode.equals("equation")){
                ImagePainter.paintEquation(imageView, expression, ns[0], ns[1], ns[2], ns[3], add, handler, false);
            } else if(mode.equals("lagrange")) {
                String format = "y=";
                String t = lagEdit.getText().toString().replace(" ", "").replace("\n", "");
                if(t.length()>=2 && t.substring(0, 2).equals("x=")) {
                    format = "x=";
                }
                ImagePainter.paintFunction(imageView, expression+";"+getLagExp(format), ns[0], ns[1], ns[2], ns[3], add, handler, false);
            }
        }catch (Exception e){

        }
    }

    public void moveFun(float startX, float startY, float stopX, float stopY) {
        float moveX = (startX-stopX)*(ns[1]-ns[0])/w;
        float moveY = (stopY-startY)*(ns[3]-ns[2])/h;
        ns[0]+=moveX;
        ns[1]+=moveX;
        ns[2]+=moveY;
        ns[3]+=moveY;
    }

    public void initSize() {
        if(initAble && ns[1]-ns[0] > 100) {
            float x = h / (2 * (ns[1] - ns[0])) - h/2 +1;
            float xs = x * (ns[1] - ns[0]) / h;
            float ys = x * (ns[3] - ns[2]) / h;
            if (ns[0] - xs < ns[1] + xs - 1 && ns[2] - ys < ns[3] + ys - 1) {
                ns[0] -= xs;
                ns[1] += xs;
                ns[2] -= ys;
                ns[3] += ys;
            }
            initAble = false;
        }
    }

    public void reSize(ImageView imageView, float x, int add){
        //float xs = x*(ns[1]-ns[0])/w;
        float xs = x*(ns[1]-ns[0])/h;
        //float ys = xs*h/w;
        float ys = x*(ns[3]-ns[2])/h;
        if(ns[0]-xs<ns[1]+xs-1 && ns[2]-ys<ns[3]+ys-1){ //minx, maxx, miny, maxy
            ns[0]-=xs;
            ns[1]+=xs;
            ns[2]-=ys;
            ns[3]+=ys;
            try {
                if(mode.equals("function")) {
                    ImagePainter.paintFunction(imageView, expression, ns[0], ns[1], ns[2], ns[3], add, handler, false);
                }else if(mode.equals("equation")){
                    ImagePainter.paintEquation(imageView, expression, ns[0], ns[1], ns[2], ns[3], add, handler, false);
                } else if(mode.equals("lagrange")) {
                    String format = "y=";
                    String t = lagEdit.getText().toString().replace(" ", "").replace("\n", "");
                    if(t.length()>=2 && t.substring(0, 2).equals("x=")) {
                        format = "x=";
                    }
                    ImagePainter.paintFunction(imageView, expression+";"+getLagExp(format), ns[0], ns[1], ns[2], ns[3], add, handler, false);
                }
            }catch (Exception e){

            }
        }
    }

    public void reSize(float x) {
        float xs = x*(ns[1]-ns[0])/h;
        float ys = x*(ns[3]-ns[2])/h;
        if(ns[0]-xs<ns[1]+xs-1 && ns[2]-ys<ns[3]+ys-1) { //minx, maxx, miny, maxy
            ns[0]-=xs;
            ns[1]+=xs;
            ns[2]-=ys;
            ns[3]+=ys;
        }
    }

    public void marker(ImageView imageView, float x, float y) {     //添加或移除（点击空白处添加，点击点移除）
        float pro = (ns[1] - ns[0]) / w;
        Vector<Point> point = lagrange.getPoints();
        int delIndex = -1;
        for(int i=0; i<point.size(); i++) {
            Point p = point.get(i);
            float x_ = (float)(p.getX()-ns[0])/pro;
            float y_ = (float)(1 - (p.getY() - ns[2]) / (ns[3] - ns[2])) * h;
            if(Math.sqrt((x-x_)*(x-x_)+(y-y_)*(y-y_)) < 100) {  //认为选中了那个点
                delIndex = i;
                break;
            }
        }
        if(delIndex == -1) {
            //minx, maxx, miny, maxy

            float setx = pro * x + ns[0];
            //y = (1 - (sety - miny) / (maxy - miny)) * h
            float sety = (1 - y / h) * (ns[3] - ns[2]) + ns[2];
            lagrange.addPoint(setx, sety);
            try {
                String format = "y=";
                String t = lagEdit.getText().toString().replace(" ", "").replace("\n", "");
                if(t.length()>=2 && t.substring(0, 2).equals("x=")) {
                    format = "x=";
                }
                final String lagExp = getLagExp(format);
                ImagePainter.paintPoint(imageView, expression + ";" + lagExp, lagrange.getPoints(), ns[0], ns[1], ns[2], ns[3], 10, handler, false);
                lagEdit.post(new Runnable() {
                    @Override
                    public void run() {
                        lagEdit.setText(lagExp);
                    }
                });
            } catch (Exception e) {

            }
        } else {
            lagrange.remove(delIndex);
            try {
                String format = "y=";
                String t = lagEdit.getText().toString().replace(" ", "").replace("\n", "");
                if(t.length()>=2 && t.substring(0, 2).equals("x=")) {
                    format = "x=";
                }
                final String lagExp = getLagExp(format);
                ImagePainter.paintPoint(imageView, expression + ";" + lagExp, lagrange.getPoints(), ns[0], ns[1], ns[2], ns[3], 10, handler, false);
                lagEdit.post(new Runnable() {
                    @Override
                    public void run() {
                        lagEdit.setText(lagExp);
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    public void marker(ImageView imageView, float startX, float startY, float stopX, float stopY) {     //移动

    }

}

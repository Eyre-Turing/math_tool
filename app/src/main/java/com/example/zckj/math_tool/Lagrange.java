package com.example.zckj.math_tool;

import java.util.Vector;

/**
 * Created by ZCKJ on 2019/9/8.
 */

public class Lagrange {
    private Vector<Point> point;

    public Lagrange() {
        point = new Vector<Point>();
    }

    public void addPoint(Point p) {
        point.add(p);
    }

    public void addPoint(double x, double y) {
        Point p = new Point();
        p.setLocation(x, y);
        addPoint(p);
    }

    public Point get(int index) {
        int s = point.size();
        if(index >= 0 && index < s) {
            return point.get(index);
        } else {
            return null;
        }
    }

    public Vector<Point> getPoints() {
        return point;
    }

    public void set(int index, Point p) {
        int s = point.size();
        if(index >= 0 && index < s) {
            point.set(index, p);
        }
    }

    public void set(int index, double x, double y) {
        Point p = new Point();
        p.setLocation(x, y);
        set(index, p);
    }

    public void remove(int index) {
        int s = point.size();
        if(index >= 0 && index < s) {
            point.remove(index);
        }
    }

    public Matrix getLineFunctionSolve(Matrix A, Matrix B) {
        return A.getInvProcess(B);
    }

    public String getLagrangerStr() {		//y=a0+a1*x+a2*x^2+...
        String result = "";
        int N = point.size();
        if(N < 2) {
            return result;
        }
        Matrix A = new Matrix(N);
        Matrix Y = new Matrix(N, 1);
        for(int i=0; i<N; i++) {
            for(int c=0; c<N; c++) {
                A.set(i, c, Math.pow(point.get(i).getX(), c));
            }
            Y.set(i, 0, point.get(i).getY());
        }
        Matrix X = getLineFunctionSolve(A, Y);
        boolean catAdd = false;
        if(A.multiply(X).equals(Y)) {
            result += "y=";
            for(int i=0; i<N; i++) {
                if(Math.abs(X.get(i, 0)) > Matrix.ALMOST_NULL) {
                    if(catAdd && (X.get(i, 0) >= 0)) {
                        result += "+";
                    }
                    result += ImagePainter.float2string((float)X.get(i, 0));
                    if(i != 0) {
                        result += "*x^"+i;
                    }
                    catAdd = true;
                }
            }
        }
        return result;
    }

    public String getLagrangerStr_() {		//x=a0+a1*y+a2*y^2+...
        String result = "";
        int N = point.size();
        if(N < 2) {
            return result;
        }
        Matrix A = new Matrix(N);
        Matrix Y = new Matrix(N, 1);
        for(int i=0; i<N; i++) {
            for(int c=0; c<N; c++) {
                A.set(i, c, Math.pow(point.get(i).getY(), c));
            }
            Y.set(i, 0, point.get(i).getX());
        }
        Matrix X = getLineFunctionSolve(A, Y);
        boolean catAdd = false;
        if(A.multiply(X).equals(Y)) {
            result += "x=";
            for(int i=0; i<N; i++) {
                if(Math.abs(X.get(i, 0)) > Matrix.ALMOST_NULL) {
                    if(catAdd && (X.get(i, 0) >= 0)) {
                        result += "+";
                    }
                    result += ImagePainter.float2string((float)X.get(i, 0));
                    if(i != 0) {
                        result += "*y^"+i;
                    }
                    catAdd = true;
                }
            }
        }
        return result;
    }

}

class Point{
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this(0, 0);
    }

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
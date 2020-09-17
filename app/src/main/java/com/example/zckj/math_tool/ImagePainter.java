package com.example.zckj.math_tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.widget.ImageView;
import android.os.Handler;

import java.math.BigDecimal;
import java.util.Vector;

/**
 * Created by ZCKJ on 2019/4/9.
 */

public class ImagePainter {
    public static int w;
    public static int h;
    public static boolean canPaint = false;

    public static void setSize(int _w, int _h){
        w = _w;
        h = _h;
    }

    public static void breakPaint(){
        canPaint = false;
    }

    public static void paintPoint(final ImageView paintView, String expression, final Vector<Point> point, final float minx, final float maxx, final float miny, final float maxy, final int add, final Handler handler, final boolean printError) throws Exception{
        final Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Paint paint = new Paint();
        final Canvas canvas = new Canvas(bitmap);
        final String []exp = replace(expression, false);
        canPaint = true;

        new Thread(){
            public void run(){
                //paintFun(canvas, paint, exp, minx, maxx, miny, maxy, add, handler, printError);
                paintFunPlus(canvas, paint, exp, minx, maxx, miny, maxy, add, handler, printError);

                float pro = (maxx-minx)/w;
                paint.setStrokeWidth(20);
                paint.setColor(Color.RED);
                for(Point p: point) {
                    if(!canPaint) {
                        break;
                    }
                    float setx = (float)p.getX();
                    float sety = (float)p.getY();
                    float x = (setx-minx)/pro;
                    //y = (1 - (sety - miny) / (maxy - miny)) * h
                    float y = (1 - (sety - miny) / (maxy - miny)) * h;
                    canvas.drawPoint(x, y, paint);
                }
                paintView.post(new Runnable() {
                    @Override
                    public void run() {
                        paintView.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }

    public static void paintFunction(final ImageView paintView, String expression, final float minx, final float maxx, final float miny, final float maxy, final int add, final Handler handler, final boolean printError) throws Exception{
        final Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Paint paint = new Paint();
        final Canvas canvas = new Canvas(bitmap);
        final String []exp = replace(expression, false);
        canPaint = true;

        new Thread(){
            public void run(){
                //paintFun(canvas, paint, exp, minx, maxx, miny, maxy, add, handler, printError);
                paintFunPlus(canvas, paint, exp, minx, maxx, miny, maxy, add, handler, printError);
                paintView.post(new Runnable() {
                    @Override
                    public void run() {
                        paintView.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }

    public static void paintEquation(final ImageView paintView, String expression, final float minx, final float maxx, final float miny, final float maxy, final int add, final Handler handler, final boolean printError) throws Exception{
        final Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Paint paint = new Paint();
        final Canvas canvas = new Canvas(bitmap);
        final String []exp = replace(expression, true);
        canPaint = true;

        new Thread(){
            public void run(){
                paintEqu(canvas, paint, exp, minx, maxx, miny, maxy, add, handler, printError);
                paintView.post(new Runnable() {
                    @Override
                    public void run() {
                        paintView.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }

    public static void paintFunPlus(Canvas canvas, Paint paint, String []exp, float minx, float maxx, float miny, float maxy, int add, Handler handler, boolean printError){
        int x = 0;
        float setx = 0;
        float y = 0;
        float yb = 0;
        float ym = 0;
        //float pro = (maxx-minx)/w;
        float pro = (maxx-minx)/w;  /////////////////////////
        paint.setStrokeWidth(1);
        paint.setColor(Color.YELLOW);
        paintNet(canvas, paint, minx, maxx, miny, maxy);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        //canvas.drawLine(0,(1-(-miny/(maxy-miny)))*h,w,(1-(-miny/(maxy-miny)))*h,paint);
        canvas.drawLine(0,(1-(-miny/(maxy-miny)))*h,w,(1-(-miny/(maxy-miny)))*h,paint); ///////////////////
        //canvas.drawLine(-minx/(maxx-minx)*w,0,-minx/(maxx-minx)*w,h,paint);
        canvas.drawLine(-minx/(maxx-minx)*w,0,-minx/(maxx-minx)*w,h,paint); /////////////////////////
        if(exp == null) {
            return ;
        }
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        boolean end = false;
        for(int i=0; i<exp.length; i++){
            if(!canPaint || end){
                break;
            }
            if(exp[i].replace(" ", "").replace("\n", "").equals("")) {
                continue;
            }
            String []exp_ = exp[i].split(",");
            boolean changeTarget = false;
            if(exp_[0].length() > 2) {
                if (exp_[0].substring(0, 2).equals("y=")) {
                    exp_[0] = exp_[0].substring(2);
                    changeTarget = false;
                } else if (exp_[0].substring(0, 2).equals("x=")) {
                    exp_[0] = exp_[0].substring(2);
                    changeTarget = true;
                }
            }
            boolean startDraw = false;      //当前开始画
            boolean preStartDraw = false;   //上一次开始画
            int endNum = changeTarget?h:w;
            for(x=0; x<=endNum; x+=add){
                preStartDraw = startDraw;
                if(!canPaint){
                    end = true;
                    break;
                }
                //if(x != 0){
                if(preStartDraw){
                    yb = y;
                }
                try {
                    if(!changeTarget) {     //以y=...的形式
                        setx = pro * x + minx;
                        /*y = f(exp_[0], setx);
                        ym = f(exp_[0], pro * (2 * x - add) / 2 + minx);*/
                        if (exp_.length == 1 || (exp_.length == 2 && f(exp_[1], setx) != 0)) {
                            if (x != 0 && (exp_.length == 1 || (exp_.length == 2 && f(exp_[1], setx) != 0))) {
                                y = f(exp_[0], setx);
                                ym = f(exp_[0], pro * (2 * x - add) / 2 + minx);
                                startDraw = true;
                            }
                            if (preStartDraw && startDraw && (yb - ym) * (ym - y) >= 0) {
                                //canvas.drawLine(x - add, (1 - (yb - miny) / (maxy - miny)) * h, x, (1 - (y - miny) / (maxy - miny)) * h, paint);
                                //float t = (1-(-miny/(maxy-miny)))*(h-w);    ///////////////////////////
                                //canvas.drawLine(x - add, (1 - (yb - miny) / (maxy - miny)) * w+t, x, (1 - (y - miny) / (maxy - miny)) * w+t, paint);    ///////////////
                                canvas.drawLine(x - add, (1 - (yb - miny) / (maxy - miny)) * h, x, (1 - (y - miny) / (maxy - miny)) * h, paint);
                            }
                        } else {
                            startDraw = false;
                        }
                    }else{          //以x=...的形式
                        setx = pro * x + miny;
                        /*y = fc(exp_[0], setx);
                        ym = fc(exp_[0], pro * (2 * x - add) / 2 + miny);*/
                        if (exp_.length == 1 || (exp_.length == 2 && fc(exp_[1], setx) != 0)) {
                            if (x != 0 && (exp_.length == 1 || (exp_.length == 2 && fc(exp_[1], setx) != 0))) {
                                y = fc(exp_[0], setx);
                                ym = fc(exp_[0], pro * (2 * x - add) / 2 + miny);
                                startDraw = true;
                            }
                            if (preStartDraw && startDraw && (yb - ym) * (ym - y) >= 0) {
                                //canvas.drawLine(x - add, (1 - (yb - miny) / (maxy - miny)) * h, x, (1 - (y - miny) / (maxy - miny)) * h, paint);
                                //canvas.drawLine((yb-minx)/(maxx-minx)*w, h-(x-add), (y-minx)/(maxx-minx)*w, h-x, paint);
                                canvas.drawLine((yb-minx)/(maxx-minx)*w, h-(x-add), (y-minx)/(maxx-minx)*w, h-x, paint);    /////////////////
                            }
                        } else {
                            startDraw = false;
                        }
                    }
                }catch (Exception e){
                    canPaint = false;
                    if(printError) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        //msg.obj = e.toString();
                        if(exp_.length == 1) {
                            msg.obj = exp_[0];
                        } else if(exp_.length == 2) {
                            msg.obj = exp_[0]+", "+exp_[1];
                        }
                        handler.sendMessage(msg);
                    }
                }
            }
        }
    }

    public static void paintFun(Canvas canvas, Paint paint, String []exp, float minx, float maxx, float miny, float maxy, int add, Handler handler, boolean printError){
        int x = 0;
        float setx = 0;
        float y = 0;
        float yb = 0;
        float ym = 0;
        float pro = (maxx-minx)/w;
        paint.setStrokeWidth(1);
        paint.setColor(Color.YELLOW);
        paintNet(canvas, paint, minx, maxx, miny, maxy);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        canvas.drawLine(0,(1-(-miny/(maxy-miny)))*h,w,(1-(-miny/(maxy-miny)))*h,paint);
        canvas.drawLine(-minx/(maxx-minx)*w,0,-minx/(maxx-minx)*w,h,paint);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        boolean end = false;
        for(int i=0; i<exp.length; i++){
            if(!canPaint || end){
                break;
            }
            String []exp_ = exp[i].split(",");
            boolean startDraw = false;
            for(x=0; x<=w; x+=add){
                if(!canPaint){
                    end = true;
                    break;
                }
                if(x != 0){
                    yb = y;
                }
                setx = pro*x+minx;
                /*y = f(exp[i], setx);
                ym = f(exp[i], pro*(2*x-add)/2+minx);*/
                try {
                    y = f(exp_[0], setx);
                    ym = f(exp_[0], pro * (2 * x - add) / 2 + minx);
                    if (exp_.length == 1 || (exp_.length == 2 && f(exp_[1], setx) != 0)) {
                        if (x != 0 && (exp_.length == 1 || (exp_.length == 2 && f(exp_[1], setx) != 0))) {
                            startDraw = true;
                        }
                    /*y = f(exp_[0], setx);
                    ym = f(exp_[0], pro * (2 * x - add) / 2 + minx);*/
                        if (startDraw && (yb - ym) * (ym - y) >= 0) {
                            canvas.drawLine(x - add, (1 - (yb - miny) / (maxy - miny)) * h, x, (1 - (y - miny) / (maxy - miny)) * h, paint);
                        }
                    } else {
                        startDraw = false;
                    }
                }catch (Exception e){
                    canPaint = false;
                    if(printError) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = e.toString();
                        handler.sendMessage(msg);
                    }
                }
            }
        }
    }

    public static void paintEqu(Canvas canvas, Paint paint, String []exp, float minx, float maxx, float miny, float maxy, int add, Handler handler, boolean printError){
        int x = 0;
        float setx = 0;
        float sety = 0;
        float y = 0;
        float e = 0;
        float e1 = 0;
        float e2 = 0;
        int k = -1;
        float pro = (maxx-minx)/w;
        float proy = (maxy-miny)/h;
        paint.setStrokeWidth(1);
        paint.setColor(Color.YELLOW);
        paintNet(canvas, paint, minx, maxx, miny, maxy);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        canvas.drawLine(0,(1-(-miny/(maxy-miny)))*h,w,(1-(-miny/(maxy-miny)))*h,paint);
        canvas.drawLine(-minx/(maxx-minx)*w,0,-minx/(maxx-minx)*w,h,paint);
        if(exp == null) {
            return ;
        }
        paint.setStrokeWidth(add);
        paint.setColor(Color.BLACK);
        boolean end = false;
        for(int i=0; i+1<exp.length; i+=2){
            if(!canPaint || end){
                break;
            }
            if(exp[i].replace(" ", "").replace("\n", "").equals("")) {
                continue;
            }
            for(x=0; x<w; x+=add){
                if(!canPaint || end){
                    end = true;
                    break;
                }
                k = -1;
                for(y=0; y<h; y+=add,k++){
                    if(!canPaint){
                        end = true;
                        break;
                    }
                    setx = pro*x+minx;
                    sety = proy*y+miny;
                    try {
                        e2 = f(exp[i], setx, sety) - f(exp[i + 1], setx, sety);
                        if (Math.abs(e) <= Math.abs(e1) && Math.abs(e) <= Math.abs(e2) && e1 * e2 <= 0 && k > 0) {
                            canvas.drawPoint(x, h - y + add, paint);
                        }
                        e1 = e;
                        e = e2;
                    }catch (Exception exc){
                        canPaint = false;
                        if(printError) {
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = exc.toString();
                            handler.sendMessage(msg);
                        }
                    }
                }
            }
            for(y=0; y<h; y+=add){
                if(!canPaint || end){
                    end = true;
                    break;
                }
                k = -1;
                for(x=0; x<w; x+=add,k++){
                    if(!canPaint){
                        end = true;
                        break;
                    }
                    setx = pro*x+minx;
                    sety = proy*y+miny;
                    try {
                        e2 = f(exp[i], setx, sety) - f(exp[i + 1], setx, sety);
                        if (Math.abs(e) <= Math.abs(e1) && Math.abs(e) <= Math.abs(e2) && e1 * e2 <= 0 && k > 0) {
                            canvas.drawPoint(x - add, h - y, paint);
                        }
                        e1 = e;
                        e = e2;
                    }catch (Exception exc){
                        canPaint = false;
                        if(printError) {
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = exc.toString();
                            handler.sendMessage(msg);
                        }
                    }
                }
            }
        }
    }

    public static String float2string(float x)
    {
        String result = String.valueOf(x);
        if(result.indexOf("E") != -1) {
            //BigDecimal b = new BigDecimal(x);
            //result = b.toString();
            Float f = new Float(x);
            BigDecimal b = new BigDecimal(f.toString());
            result = b.toPlainString();
        }
        return result;
    }

    public static float f(String exp, float x) throws Exception{
        String e = exp;
        String []_e = new String[3];
        int length = 1;
        int a = -1;
        do{
            a = e.indexOf("x", a+1);
            if(a != -1){
                if(e.length()==a+length || !((e.charAt(a+length)>='a' && e.charAt(a+length)<='z') || (e.charAt(a+length)>='0' && e.charAt(a+length)<='9') || e.charAt(a+length)=='.' || e.charAt(a+length)=='(') || a==0 || !((e.charAt(a-1)>='a' && e.charAt(a-1)<='z') || (e.charAt(a-1)>='0' && e.charAt(a-1)<='9') || e.charAt(a-1)=='.' || e.charAt(a-1)==')')){
                    if(a == 0){
                        _e[0] = "";
                        _e[2] = e.substring(1);
                    }else{
                        if(a == e.length()-1){
                            _e[0] = e.substring(0, e.length()-1);
                            _e[2] = "";
                        }else{
                            _e[0] = e.substring(0, a);
                            _e[2] = e.substring(a+1, e.length());
                        }
                    }
                    //_e[1] = "("+String.valueOf(x)+")";
                    _e[1] = "("+float2string(x)+")";
                    e = _e[0]+_e[1]+_e[2];
                }
            }
        }while(a != -1);
        return (float)Calculator.calculate(e);
    }

    public static float fc(String exp, float x) throws Exception{
        String e = exp;
        String []_e = new String[3];
        int length = 1;
        int a = -1;
        do{
            a = e.indexOf("y", a+1);
            if(a != -1){
                if(e.length()==a+length || !((e.charAt(a+length)>='a' && e.charAt(a+length)<='z') || (e.charAt(a+length)>='0' && e.charAt(a+length)<='9') || e.charAt(a+length)=='.' || e.charAt(a+length)=='(') || a==0 || !((e.charAt(a-1)>='a' && e.charAt(a-1)<='z') || (e.charAt(a-1)>='0' && e.charAt(a-1)<='9') || e.charAt(a-1)=='.' || e.charAt(a-1)==')')){
                    if(a == 0){
                        _e[0] = "";
                        _e[2] = e.substring(1);
                    }else{
                        if(a == e.length()-1){
                            _e[0] = e.substring(0, e.length()-1);
                            _e[2] = "";
                        }else{
                            _e[0] = e.substring(0, a);
                            _e[2] = e.substring(a+1, e.length());
                        }
                    }
                    //_e[1] = "("+String.valueOf(x)+")";
                    _e[1] = "("+float2string(x)+")";
                    e = _e[0]+_e[1]+_e[2];
                }
            }
        }while(a != -1);
        return (float)Calculator.calculate(e);
    }

    public static float f(String exp, float x, float y) throws Exception{
        String e = exp;
        String []_e = new String[3];
        int length = 1;
        int a = -1;
        do{
            a = e.indexOf("x", a+1);
            if(a != -1){
                if(e.length()==a+length || !((e.charAt(a+length)>='a' && e.charAt(a+length)<='z') || (e.charAt(a+length)>='0' && e.charAt(a+length)<='9') || e.charAt(a+length)=='.' || e.charAt(a+length)=='(') || a==0 || !((e.charAt(a-1)>='a' && e.charAt(a-1)<='z') || (e.charAt(a-1)>='0' && e.charAt(a-1)<='9') || e.charAt(a-1)=='.' || e.charAt(a-1)==')')){
                    if(a == 0){
                        _e[0] = "";
                        _e[2] = e.substring(1);
                    }else{
                        if(a == e.length()-1){
                            _e[0] = e.substring(0, e.length()-1);
                            _e[2] = "";
                        }else{
                            _e[0] = e.substring(0, a);
                            _e[2] = e.substring(a+1, e.length());
                        }
                    }
                    //_e[1] = "("+String.valueOf(x)+")";
                    _e[1] = "("+float2string(x)+")";
                    e = _e[0]+_e[1]+_e[2];
                }
            }
        }while(a != -1);
        a = -1;
        do{
            a = e.indexOf("y", a+1);
            if(a != -1){
                if(e.length()==a+length || !((e.charAt(a+length)>='a' && e.charAt(a+length)<='z') || (e.charAt(a+length)>='0' && e.charAt(a+length)<='9') || e.charAt(a+length)=='.' || e.charAt(a+length)=='(') || a==0 || !((e.charAt(a-1)>='a' && e.charAt(a-1)<='z') || (e.charAt(a-1)>='0' && e.charAt(a-1)<='9') || e.charAt(a-1)=='.' || e.charAt(a-1)==')')){
                    if(a == 0){
                        _e[0] = "";
                        _e[2] = e.substring(1);
                    }else{
                        if(a == e.length()-1){
                            _e[0] = e.substring(0, e.length()-1);
                            _e[2] = "";
                        }else{
                            _e[0] = e.substring(0, a);
                            _e[2] = e.substring(a+1, e.length());
                        }
                    }
                    //_e[1] = "("+String.valueOf(y)+")";
                    _e[1] = "("+float2string(y)+")";
                    e = _e[0]+_e[1]+_e[2];
                }
            }
        }while(a != -1);
        return (float)Calculator.calculate(e);
    }

    public static void paintNet(Canvas canvas, Paint paint, float minx, float maxx, float miny, float maxy){
        float e=(maxx-minx)/10;
        int k;
        if(Math.abs(minx)>1000 || Math.abs(maxx)>1000)
            paint.setTextSize(20);
        else {
            if (Math.abs(minx) > 100 || Math.abs(maxx) > 100)
                paint.setTextSize(30);
            else paint.setTextSize(35);
        }
        for(k=(int)(minx/e);k*e<=maxx;k++){//画与y轴平行的线
            paint.setColor(Color.YELLOW);
            //canvas.drawLine((k*e-minx)/(maxx-minx)*w,0,(k*e-minx)/(maxx-minx)*w,h,paint);
            canvas.drawLine((k*e-minx)/(maxx-minx)*w,0,(k*e-minx)/(maxx-minx)*w,h,paint);//////////////////////////////////////
            paint.setColor(Color.BLUE);

            //canvas.drawText(String.valueOf((int)(k*e*100)/100.0).toString(),(k*e-minx)/(maxx-minx)*w,40,paint);
            canvas.drawText(String.valueOf((int)(k*e*100)/100.0).toString(),(k*e-minx)/(maxx-minx)*w,40,paint);///////////////////////////////////////
            //canvas.drawText(String.valueOf((int)(k*e*100)/100.0).toString(),(k*e-minx)/(maxx-minx)*w,h-40,paint);
            canvas.drawText(String.valueOf((int)(k*e*100)/100.0).toString(),(k*e-minx)/(maxx-minx)*w,h-40,paint);//////////////////////////////////////////
        }
        for(k=(int)(miny/e);k*e<=maxy;k++){//画与x轴平行的线
            paint.setColor(Color.YELLOW);
            //canvas.drawLine(0,(1-((k*e-miny)/(maxy-miny)))*h,w,(1-((k*e-miny)/(maxy-miny)))*h,paint);
            canvas.drawLine(0,(1-((k*e-miny)/(maxy-miny)))*h,w,(1-((k*e-miny)/(maxy-miny)))*h,paint);////////////////////////////////////////
            paint.setColor(Color.BLUE);
            //canvas.drawText(String.valueOf((int)(k*e*100)/100.0).toString(),0,(1-((k*e-miny)/(maxy-miny)))*h,paint);
            canvas.drawText(String.valueOf((int)(k*e*100)/100.0).toString(),0,(1-((k*e-miny)/(maxy-miny)))*h,paint);/////////////////////////////////////////
            //canvas.drawText(String.valueOf((int)(k*e*100)/100.0).toString(),w-100,(1-((k*e-miny)/(maxy-miny)))*h,paint);
            canvas.drawText(String.valueOf((int)(k*e*100)/100.0).toString(),w-100,(1-((k*e-miny)/(maxy-miny)))*h,paint);////////////////////////////////////
        }
    }

    public static String[] replace(String exp, boolean isEqu) throws Exception{
        String[] result = null;
        if(exp.replace(" ", "").replace("\n", "").equals("")) {
            return result;
        }
        String []e = exp.split("#");
        if(e.length == 1){
            /*int a = -1;
            while((a=e[0].indexOf(" ")) != -1 || (a=e[0].indexOf("\n")) != -1){
                e[0] = e[0].substring(0, a)+e[0].substring(a+1);
            }*/
            e[0] = e[0].replace(" ", "").replace("\n", "");
            if(isEqu){
                result = e[0].split("[;=]");
                if(result.length%2 != 0){
                    throw new Exception("equation must have \"=\"");
                }
            }else {
                result = e[0].split(";");
            }
        }else if(e.length == 2) {
            /*int a = -1;
            while((a=e[0].indexOf(" ")) != -1 || (a=e[0].indexOf("\n")) != -1){
                e[0] = e[0].substring(0, a)+e[0].substring(a+1);
            }
            a = -1;
            while((a=e[1].indexOf(" ")) != -1 || (a=e[1].indexOf("\n")) != -1){
                e[1] = e[1].substring(0, a)+e[1].substring(a+1);
            }*/
            e[0] = e[0].replace(" ", "").replace("\n", "");
            e[1] = e[1].replace(" ", "").replace("\n", "");
            String []replaceE = e[0].split(";");
            for(int i=0; i<replaceE.length; i++){
                String []replaceS = replaceE[i].split("=");
                if(replaceS.length != 2){
                    throw new Exception("symbol replace must have \"=\"");
                }
                int length = replaceS[0].length();
                int length2 = replaceS[1].length();
                //int position = -(length2+2);
                int position = -1;
                boolean replaceEd = false;
                do{
                    if(position >= e[1].length()){
                        break;
                    }
                    if(replaceEd) {     //之前是替换了的
                        position = e[1].indexOf(replaceS[0], position + (length2 + 2));
                        replaceEd = false;
                    } else {
                        position = e[1].indexOf(replaceS[0], position + 1);
                    }
                    if(position != -1){
                        if(position == 0){
                            if(position+length == e[1].length()){
                                e[1] = e[1].substring(0, position)+"("+replaceS[1]+")"+e[1].substring(position+length);
                                replaceEd = true;
                            }else{
                                if(!((e[1].charAt(position+length)>='a' && e[1].charAt(position+length)<='z') || (e[1].charAt(position+length)>='0' && e[1].charAt(position+length)<='9') || e[1].charAt(position+length)=='.' || e[1].charAt(position+length)=='(')){
                                    e[1] = e[1].substring(0, position)+"("+replaceS[1]+")"+e[1].substring(position+length);
                                    replaceEd = true;
                                }
                                /*if(e[1].substring(position+length, position+length+1).equals("+") || e[1].substring(position+length, position+length+1).equals("-") || e[1].substring(position+length, position+length+1).equals("*") || e[1].substring(position+length, position+length+1).equals("/") || e[1].substring(position+length, position+length+1).equals("^") || e[1].substring(position+length, position+length+1).equals(")") || e[1].substring(position+length, position+length+1).equals(";")){
                                    e[1] = e[1].substring(0, position)+"("+replaceS[1]+")"+e[1].substring(position+length);
                                }*/
                            }
                        }else{
                            if(position+length == e[1].length()){
                                if(!((e[1].charAt(position-1)>='a' && e[1].charAt(position-1)<='z') || (e[1].charAt(position-1)>='0' && e[1].charAt(position-1)<='9') || e[1].charAt(position-1)=='.' || e[1].charAt(position-1)==')')){
                                    e[1] = e[1].substring(0, position)+"("+replaceS[1]+")"+e[1].substring(position+length);
                                    replaceEd = true;
                                }
                                /*if(e[1].substring(position-1, position).equals("+") || e[1].substring(position-1, position).equals("-") || e[1].substring(position-1, position).equals("*") || e[1].substring(position-1, position).equals("/") || e[1].substring(position-1, position).equals("^") || e[1].substring(position-1, position).equals("(") || e[1].substring(position-1, position).equals(";")){
                                    e[1] = e[1].substring(0, position)+"("+replaceS[1]+")"+e[1].substring(position+length);
                                }*/
                            }else{
                                if(!((e[1].charAt(position-1)>='a' && e[1].charAt(position-1)<='z') || (e[1].charAt(position-1)>='0' && e[1].charAt(position-1)<='9') || e[1].charAt(position-1)=='.' || e[1].charAt(position-1)==')')){
                                    if(!((e[1].charAt(position+length)>='a' && e[1].charAt(position+length)<='z') || (e[1].charAt(position+length)>='0' && e[1].charAt(position+length)<='9') || e[1].charAt(position+length)=='.' || e[1].charAt(position+length)=='(')){
                                        e[1] = e[1].substring(0, position)+"("+replaceS[1]+")"+e[1].substring(position+length);
                                        replaceEd = true;
                                    }
                                }
                                /*if(e[1].substring(position-1, position).equals("+") || e[1].substring(position-1, position).equals("-") || e[1].substring(position-1, position).equals("*") || e[1].substring(position-1, position).equals("/") || e[1].substring(position-1, position).equals("^") || e[1].substring(position-1, position).equals("(") || e[1].substring(position-1, position).equals(";")){
                                    if(e[1].substring(position+length, position+length+1).equals("+") || e[1].substring(position+length, position+length+1).equals("-") || e[1].substring(position+length, position+length+1).equals("*") || e[1].substring(position+length, position+length+1).equals("/") || e[1].substring(position+length, position+length+1).equals("^") || e[1].substring(position+length, position+length+1).equals(")") || e[1].substring(position+length, position+length+1).equals(";")){
                                        e[1] = e[1].substring(0, position)+"("+replaceS[1]+")"+e[1].substring(position+length);
                                    }
                                }*/
                            }
                        }
                    }
                }while(position != -1);
            }
            if(isEqu){
                result = e[1].split("[;=]");
                if(result.length%2 != 0){
                    throw new Exception("equation must have \"=\"");
                }
            }else {
                result = e[1].split(";");
            }
        }
        return result;
    }

}

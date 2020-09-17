package com.example.zckj.math_tool;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;


public class MainActivity extends Activity {
    private Button calButton;
    private Button funButton;
    private Button equButton;
    private Button calBackButton;
    private Button funBackButton;
    private Button equBackButton;
    private Button lagrangeButton;
    private Button lagBackButton;
    private Button lpButton;
    private Button lpBackButton;
    private Button lpSolveButton;
    private EditText lpProText;
    private EditText lpSolveText;
    private EditText calculateExpressionEdit;
    private Button calculateButton;
    private TextView calculateResult;
    private ImageView functionView = null;
    private EditText funExpText;
    private Button drawFunButton;
    private ImageView equationView = null;
    private EditText equExpText;
    private Button drawEquButton;
    private ImageView lagrangeView = null;
    private Button lagStartButton;
    private CheckBox lagCheckBox;
    private Lagrange lagrange;
    private EditText lagEdit;
    private ImageViewTouch imageViewTouch;
    private Handler handler = new Handler();
    public float []ns = {0, 0, 0, 0};

    private File calculationFile;
    private File lpFile;
    private File functionFile;
    private File equationFile;
    private long mExitTime = 0;
    private boolean currentBackExitStatus;

    Handler showHandle = new Handler();
    Handler handlerMsg = new Handler(){
        public void handleMessage(Message msg){
            final String str = msg.obj.toString();
            showHandle.post(new Runnable() {
                @Override
                public void run() {
                    showAlert(str);
                }
            });
        }
    };

    @Override
    public void onBackPressed() {
        if(currentBackExitStatus && System.currentTimeMillis() - mExitTime < 2000) {
            this.finish();   //关闭本活动页面
        }
        else{
            if(currentBackExitStatus) {
                Toast.makeText(this, "再按一次退出！", Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();   //这里赋值最关键，别忘记
            }else{
                ImagePainter.breakPaint();
                imageViewTouch.setMode("");
                imageViewTouch.setExpression("");
                toMenu();
            }
        }
    }

    private Runnable getLagrangeViewSize = new Runnable() {
        @Override
        public void run() {
            if(lagrangeView.getWidth()==0 || lagrangeView.getHeight()==0) {
                handler.postDelayed(getLagrangeViewSize, 1000);
            } else {
                if(ns[0]==0 && ns[1]==0 && ns[2]==0 && ns[3]==0) {
                    ns[0] = -lagrangeView.getWidth();
                    ns[1] = lagrangeView.getWidth();
                    ns[2] = -lagrangeView.getHeight();
                    ns[3] = lagrangeView.getHeight();
                }
                lagrangeView.setOnTouchListener(imageViewTouch);
                imageViewTouch.setNs(ns);
                imageViewTouch.setSize(lagrangeView.getWidth(), lagrangeView.getHeight());

                imageViewTouch.initSize();

                imageViewTouch.setMode("lagrange");
                ImagePainter.setSize(lagrangeView.getWidth(), lagrangeView.getHeight());
                handler.removeCallbacks(getLagrangeViewSize);
            }
        }
    };

    private Runnable getFunctionViewSize = new Runnable() {
        @Override
        public void run() {
            if(functionView.getWidth()==0 || functionView.getHeight()==0){
                handler.postDelayed(getFunctionViewSize, 1000);
            }else{
                if(ns[0]==0 && ns[1]==0 && ns[2]==0 && ns[3]==0){
                    ns[0] = -functionView.getWidth();
                    ns[1] = functionView.getWidth();
                    ns[2] = -functionView.getHeight();
                    ns[3] = functionView.getHeight();
                }
                functionView.setOnTouchListener(imageViewTouch);
                imageViewTouch.setNs(ns);
                imageViewTouch.setSize(functionView.getWidth(), functionView.getHeight());

                imageViewTouch.initSize();

                imageViewTouch.setMode("function");
                ImagePainter.setSize(functionView.getWidth(), functionView.getHeight());
                handler.removeCallbacks(getFunctionViewSize);
            }
        }
    };

    private Runnable getEquationViewSize = new Runnable() {
        @Override
        public void run() {
            if(equationView.getWidth()==0 || equationView.getHeight()==0){
                handler.postDelayed(getEquationViewSize, 1000);
            }else{
                if(ns[0]==0 && ns[1]==0 && ns[2]==0 && ns[3]==0){
                    ns[0] = -equationView.getWidth();
                    ns[1] = equationView.getWidth();
                    ns[2] = -equationView.getHeight();
                    ns[3] = equationView.getHeight();
                }
                equationView.setOnTouchListener(imageViewTouch);
                imageViewTouch.setNs(ns);
                imageViewTouch.setSize(equationView.getWidth(), equationView.getHeight());

                imageViewTouch.initSize();

                imageViewTouch.setMode("equation");
                ImagePainter.setSize(equationView.getWidth(), equationView.getHeight());
                handler.removeCallbacks(getEquationViewSize);
            }
        }
    };

    private void requestStore(){
        if(!PermissionsUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    init();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {

                }
            },new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }else{
            init();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestStore();
    }

    private void init(){
        String folderPath = Environment.getExternalStorageDirectory() + File.separator;
        String path = folderPath+"zckj_math_tool";
        File saveDir = new File(path);
        if(!saveDir.exists()){
            saveDir.mkdirs();
        }
        calculationFile = new File(path+"/calculation.txt");
        lpFile = new File(path+"/lp.txt");
        functionFile = new File(path+"/function.txt");
        equationFile = new File(path+"/equation.txt");
        imageViewTouch = new ImageViewTouch();
        imageViewTouch.setHandler(handlerMsg);
        toMenu();
    }

    private void showAlert(String message){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String simpleReplace(String src) throws Exception {
        String [] e = src.replace(" ", "").replace("\n", "").split("#");
        if(e.length > 2) {
            throw new Exception("inlegal replace");
        }
        String result = "";
        if(e.length == 1) {
            result = e[0];
        } else if(e.length == 2) {
            String condition = e[0];
            String [] cs = condition.split(";");
            for(int i=0; i<cs.length; i++) {
                String curC = cs[i];
                String [] replaceS = curC.split("=");
                if(replaceS.length != 2) {
                    throw new Exception("replace format error");
                }
                int length = replaceS[0].length();
                int length2 = replaceS[1].length();
                int position = -1;
                boolean replaceEd = false;
                do {
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
            result = e[1];
        }
        return result;
    }

    private void toLP() {
        setContentView(R.layout.activity_lp);
        lpBackButton = (Button) findViewById(R.id.lpBackButton);
        lpBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCal();
            }
        });
        lpSolveButton = (Button) findViewById(R.id.lpSolvebutton);
        lpProText = (EditText) findViewById(R.id.lpProText);
        lpSolveText = (EditText) findViewById(R.id.lpSolveText);
        try {
            if(lpFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(lpFile);
                byte[] bytes = new byte[(int)lpFile.length()];
                fileInputStream.read(bytes);
                String expression = new String(bytes);
                lpProText.setText(expression);
                fileInputStream.close();
            }
        }catch (Exception e){

        }
        lpSolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expression = lpProText.getText().toString();
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(lpFile);
                    fileOutputStream.write(expression.getBytes());
                    fileOutputStream.close();
                }catch (Exception e){

                }
                try {
                    //double result = Calculator.calculate(expression);
                    //calculateResult.setText(String.valueOf(result));
                    //LP lp = new LP(simpleReplace(expression));
                    String rexp = simpleReplace(expression);
                    IP ip = IP.initIP(rexp);
                    int tSize = rexp.split(";")[0].split(",").length;
                    //Matrix result = lp.solve();
                    Matrix result = ip.isolve();
                    if(result.isEmpty()) {
                        lpSolveText.setText("无最优解");
                    } else {
                        String str = result.toString();
                        int index = -1;
                        for(int i=0; i<tSize; i++) {
                            index = str.indexOf("\n", index+1);
                            if(index == -1) {
                                break;
                            }
                        }
                        if(index != -1) {
                            str = "原变量：\n"+str.substring(0, index)+"\n松弛变量："+str.substring(index);
                        }
                        str += "此时目标函数有";
                        double tar = ip.subs(result);
                        if(ip.isTargetGetMin()) {
                            tar *= -1;
                            str += "极小值：";
                        } else {
                            str += "极大值：";
                        }
                        str += Matrix.double2String(tar);
                        lpSolveText.setText(str);
                    }
                }catch (Exception e) {
                    showAlert(e.toString());
                }
            }
        });
    }

    private void toCal(){
        setContentView(R.layout.activity_calculate);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        calBackButton = (Button)findViewById(R.id.calBackButton);
        calculateExpressionEdit = (EditText)findViewById(R.id.calculateExpressionEdit);
        calculateButton = (Button)findViewById(R.id.calculateButton);
        calculateResult = (TextView)findViewById(R.id.calculateResult);
        lpButton = (Button) findViewById(R.id.lpButton);
        calBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMenu();
            }
        });
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expression = calculateExpressionEdit.getText().toString();
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(calculationFile);
                    fileOutputStream.write(expression.getBytes());
                    fileOutputStream.close();
                }catch (Exception e){

                }
                try {
                    double result = Calculator.calculate(simpleReplace(expression));
                    calculateResult.setText(String.valueOf(result));
                }catch (Exception e) {
                    showAlert(e.toString());
                }
            }
        });
        lpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLP();
            }
        });
        try {
            if(calculationFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(calculationFile);
                byte[] bytes = new byte[(int)calculationFile.length()];
                fileInputStream.read(bytes);
                String expression = new String(bytes);
                calculateExpressionEdit.setText(expression);
                fileInputStream.close();
            }
        }catch (Exception e){

        }
        currentBackExitStatus = false;
    }

    private void toFun(){
        setContentView(R.layout.activity_function);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        funBackButton = (Button)findViewById(R.id.funBackButton);
        functionView = (ImageView)findViewById(R.id.functionView);
        funExpText = (EditText)findViewById(R.id.funExpText);
        drawFunButton = (Button)findViewById(R.id.drawFunButton);
        lagrangeButton = (Button)findViewById(R.id.lagrangeButton);
        imageViewTouch.setExpression("");
        funBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePainter.breakPaint();
                imageViewTouch.setMode("");
                toMenu();
            }
        });
        drawFunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(functionView.getWidth()!=0 && functionView.getHeight()!=0){
                    String expression = funExpText.getText().toString();
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(functionFile);
                        fileOutputStream.write(expression.getBytes());
                        fileOutputStream.close();
                    }catch (Exception e){

                    }
                    try {
                        ImagePainter.breakPaint();
                        ImagePainter.paintFunction(functionView, expression, ns[0], ns[1], ns[2], ns[3], 1, handlerMsg, true);
                        imageViewTouch.setExpression(expression);
                    }catch (Exception e){
                        showAlert(e.toString());
                    }
                }
            }
        });
        handler.post(getFunctionViewSize);
        try {
            if(functionFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(functionFile);
                byte[] bytes = new byte[(int)functionFile.length()];
                fileInputStream.read(bytes);
                String expression = new String(bytes);
                funExpText.setText(expression);
                fileInputStream.close();
            }
        }catch (Exception e){

        }
        lagrangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePainter.breakPaint();
                toLagrange();
            }
        });
        currentBackExitStatus = false;
    }

    private void toEqu(){
        setContentView(R.layout.activity_equation);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        equBackButton = (Button)findViewById(R.id.equBackButton);
        equationView = (ImageView)findViewById(R.id.equationView);
        equExpText = (EditText)findViewById(R.id.equExpText);
        drawEquButton = (Button)findViewById(R.id.drawEquButton);
        imageViewTouch.setExpression("");
        equBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePainter.breakPaint();
                imageViewTouch.setMode("");
                toMenu();
            }
        });
        drawEquButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(equationView.getWidth()!=0 && equationView.getHeight()!=0){
                    String expression = equExpText.getText().toString();
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(equationFile);
                        fileOutputStream.write(expression.getBytes());
                        fileOutputStream.close();
                    }catch (Exception e){

                    }
                    try {
                        ImagePainter.breakPaint();
                        ImagePainter.paintEquation(equationView, expression, ns[0], ns[1], ns[2], ns[3], 10, handlerMsg, true);
                        imageViewTouch.setExpression(expression);
                    }catch (Exception e){
                        showAlert(e.toString());
                    }
                }
            }
        });
        handler.post(getEquationViewSize);
        try {
            if(equationFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(equationFile);
                byte[] bytes = new byte[(int)equationFile.length()];
                fileInputStream.read(bytes);
                String expression = new String(bytes);
                equExpText.setText(expression);
                fileInputStream.close();
            }
        }catch (Exception e){

        }
        currentBackExitStatus = false;
    }

    private void toMenu(){
        setContentView(R.layout.activity_main);
        calButton = (Button)findViewById(R.id.calButton);
        funButton = (Button)findViewById(R.id.funButton);
        equButton = (Button)findViewById(R.id.equButton);
        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCal();
            }
        });
        funButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toFun();
            }
        });
        equButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toEqu();
            }
        });
        currentBackExitStatus = true;
    }

    private void toLagrange() {
        setContentView(R.layout.activity_lagrange);
        lagStartButton = (Button)findViewById(R.id.lagStartButton);
        lagrangeView = (ImageView)findViewById(R.id.lagrangeView);
        lagBackButton = (Button)findViewById(R.id.lagBackButton);
        lagCheckBox = (CheckBox) findViewById(R.id.markerCheckBox);
        lagCheckBox.setVisibility(CheckBox.INVISIBLE);
        lagEdit = (EditText) findViewById(R.id.lagText);
        lagBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePainter.breakPaint();
                imageViewTouch.setMode("");
                toFun();
            }
        });
        lagStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "算法启动，请标记点", Toast.LENGTH_SHORT).show();
                if(lagrangeView.getWidth()!=0 && lagrangeView.getHeight()!=0) {
                    try {
                        lagrange = new Lagrange();
                        imageViewTouch.setLagrange(lagrange);
                        imageViewTouch.setLagCheckBox(lagCheckBox);
                        imageViewTouch.setLagEdit(lagEdit);
                        ImagePainter.breakPaint();
                        ImagePainter.paintFunction(lagrangeView, imageViewTouch.getExpression(), ns[0], ns[1], ns[2], ns[3], 1, handlerMsg, true);
                        lagCheckBox.setVisibility(CheckBox.VISIBLE);
                    } catch (Exception e) {
                        showAlert(e.toString());
                    }
                }
            }
        });
        handler.post(getLagrangeViewSize);
    }

}

package com.example.zckj.math_tool;

import java.util.Vector;

/**
 * Created by ZCKJ on 2019/9/22.
 */

public class LP {
    private Matrix target;
    private Matrix st;

    public LP(String lpstr) throws Exception {
        input(lpstr);
    }

    public LP(Matrix srctar, Matrix srcst, Matrix type) throws Exception {
        input(srctar, srcst, type);
    }

    public Matrix getTarget() {
        return new Matrix(target);
    }

    public Matrix getSt() {
        return new Matrix(st);
    }

    public Vector<Size> useVar() {
        int len = 0;
        Vector<Size> result = new Vector<Size>();
        Size size = st.getSize();
        int sc = size.getCol();
        int sr = size.getRow();
        for(int i=0; i<sc-1; i++) {
            int status = 0;
            int row = -1;
            for(int j=0; j<sr; j++) {
                if(st.get(j, i) > 0) {
                    if(status == 0) {
                        int rsize = result.size();
                        boolean ok = true;
                        for(int k=0; k<rsize; k++) {
                            if(result.get(k).getRow() == j) {
                                ok = false;
                            }
                        }
                        if(ok) {
                            status = 1;
                            row = j;
                        } else {
                            status = -1;
                            break;
                        }
                    } else if(status == 1) {
                        status = -1;
                        break;
                    }
                } else if(st.get(j, i) < 0) {
                    status = -1;
                    break;
                }
            }
            if(status == 1) {
                result.add(new Size(row, i));
                len++;
                if(len >= sr) {
                    break;
                }
            }
        }
        return result;
    }

    public Matrix solve() {
        Matrix table = new Matrix(st);
        Vector<Size> base = useVar();
        Vector<Integer> baseV = new Vector<Integer>();
        baseV.setSize(st.getSize().getRow());
        int preLen = st.getSize().getRow()-base.size();
        if(preLen > 0) {
            int begL = target.getSize().getCol();
            Matrix preTarget = new Matrix(1, begL+preLen);
            Matrix b = table.listAt(begL, begL+1);
            int sn = table.getSize().getRow();
            table.resize(sn, begL+preLen);
            table.lineSpell(b);
            Vector<Boolean> addV = new Vector<Boolean>();
            for(int i=0; i<sn; i++) {
                addV.add(true);
            }
            for(int i=0; i<base.size(); i++) {
                addV.set(base.get(i).getRow(), false);
                baseV.set(base.get(i).getRow(), base.get(i).getCol());
            }
            for(int i=begL, j=0; i<begL+preLen; i++) {
                table.listMultiply(i, 0);
                preTarget.set(0, i, -1);
                for(; j<sn && !addV.get(j); j++);
                addV.set(j, false);
                table.set(j, i, 1);
                baseV.set(j, i);
            }
            SimplexTable preSimplexTable = new SimplexTable(preTarget, table, baseV);
            if(preSimplexTable.solve().isEmpty()) {
                return new Matrix(0);
            }
            table = preSimplexTable.getMainTable();
            baseV = preSimplexTable.getBaseV();
            int baseSize = baseV.size();
            int baseMaxIndex = begL-1;
            for(int i=0; i<baseSize; i++) {
                if(baseV.get(i) > baseMaxIndex) {
                    return new Matrix(0);
                }
            }
        } else {
            //Matrix b = table.listAt(table.getSize().getCol()-1, table.getSize().getCol());
            int bsize = base.size();
            for(int i=0; i<bsize; i++) {
                baseV.set(base.get(i).getRow(), base.get(i).getCol());
            }
        }
        SimplexTable simplexTable = new SimplexTable(target, table, baseV);
        return simplexTable.solve().standar();
    }

    /*
     * format:
     * target(include max or min); condition(include type). as:
     * max: a, b, c;
     * d, e, f > g;
     * h, i, j = e;
     */
    public void input(String lpstr) throws Exception {
        lpstr = lpstr.replace(" ", "").replace("\n", "").replace(">=", ">").replace("<=", "<");
        String []strlist = lpstr.split(";");
        if(strlist.length < 2) {
            throw new Exception("too less information");
        }
        String tar = strlist[0];
        String tart = tar.substring(0, 4);
        tar = tar.substring(4);
        Matrix srctar;
        if(tart.equals("max:")) {
            String []varlist = tar.split(",");
            srctar = new Matrix(1, varlist.length);
            for(int i=0; i<varlist.length; i++) {
                srctar.set(0, i, Calculator.calculate(varlist[i]));
            }
        } else if(tart.equals("min:")) {
            String []varlist = tar.split(",");
            srctar = new Matrix(1, varlist.length);
            for(int i=0; i<varlist.length; i++) {
                srctar.set(0, i, -Calculator.calculate(varlist[i]));
            }
        } else {
            throw new Exception("target format error");
        }
        Matrix type = new Matrix();
        Matrix srcst = new Matrix();
        Matrix bm = new Matrix();
        int curLine = 0;
        for(int i=1; i<strlist.length; i++) {
            String curSt = strlist[i];
            if(curSt.indexOf(">") != -1) {
                type.resize(curLine+1, 1);
                type.set(curLine, 0, 1);
                String []sp = curSt.split(">");
                if(sp.length != 2) {
                    throw new Exception("condition format error");
                }
                String []vl = sp[0].split(",");
                String b = sp[1];
                int col = srcst.getSize().getCol();
                if(vl.length > col) {
                    col = vl.length;
                }
                srcst.resize(curLine+1, col);
                for(int j=0; j<vl.length; j++) {
                    srcst.set(curLine, j, Calculator.calculate(vl[j]));
                }
                bm.resize(curLine+1, 1);
                bm.set(curLine, 0, Calculator.calculate(b));
                curLine++;
            } else if(curSt.indexOf("<") != -1) {
                type.resize(curLine+1, 1);
                type.set(curLine, 0, -1);
                String []sp = curSt.split("<");
                if(sp.length != 2) {
                    throw new Exception("condition format error");
                }
                String []vl = sp[0].split(",");
                String b = sp[1];
                int col = srcst.getSize().getCol();
                if(vl.length > col) {
                    col = vl.length;
                }
                srcst.resize(curLine+1, col);
                for(int j=0; j<vl.length; j++) {
                    srcst.set(curLine, j, Calculator.calculate(vl[j]));
                }
                bm.resize(curLine+1, 1);
                bm.set(curLine, 0, Calculator.calculate(b));
                curLine++;
            } else if(curSt.indexOf("=") != -1) {
                type.resize(curLine+1, 1);
                type.set(curLine, 0, 0);
                String []sp = curSt.split("=");
                if(sp.length != 2) {
                    throw new Exception("condition format error");
                }
                String []vl = sp[0].split(",");
                String b = sp[1];
                int col = srcst.getSize().getCol();
                if(vl.length > col) {
                    col = vl.length;
                }
                srcst.resize(curLine+1, col);
                for(int j=0; j<vl.length; j++) {
                    srcst.set(curLine, j, Calculator.calculate(vl[j]));
                }
                bm.resize(curLine+1, 1);
                bm.set(curLine, 0, Calculator.calculate(b));
                curLine++;
            }
        }
        srcst.lineSpell(bm);
        input(srctar, srcst, type);
    }

    /*
     * srctar is a 1*var matrix
     * srcst is a st*(var+1) matrix, +1 for b
     * type is a st*1 matrix, value==0 is '=', value>0 is '>', value<0 is '<'
     */
    public void input(Matrix srctar, Matrix srcst, Matrix type) throws Exception {
        if(srctar.isEmpty() || srcst.isEmpty() || type.isEmpty()) {
            throw new Exception("matrix empty");
        }
        if(srcst.getSize().getCol()-1!=srctar.getSize().getCol() || srcst.getSize().getRow()!=type.getSize().getRow()) {
            throw new Exception("number of variable has error");
        }
        if(srctar.getSize().getRow()!=1 || type.getSize().getCol()!=1) {
            throw new Exception("format error");
        }
        target = new Matrix(srctar);
        st = new Matrix(srcst);
        int sn = type.getSize().getRow();
        int vn = srctar.getSize().getCol();
        for(int i=0; i<sn; i++) {
            if(type.get(i, 0) > 0) {		//>
                Matrix tmp = st.listAt(vn, vn+1);
                vn++;
                st.lineSpell(tmp);
                st.listMultiply(vn-1, 0);
                target.resize(1, vn);
                if(st.get(i, vn) >= 0) {
                    st.set(i, vn-1, -1);
                } else {
                    st.lineMultiply(i, -1);
                    st.set(i, vn-1, 1);
                }
            } else if(type.get(i, 0) < 0) {	//<
                Matrix tmp = st.listAt(vn, vn+1);
                vn++;
                st.lineSpell(tmp);
                st.listMultiply(vn-1, 0);
                target.resize(1, vn);
                if(st.get(i, vn) >= 0) {
                    st.set(i, vn-1, 1);
                } else {
                    st.lineMultiply(i, -1);
                    st.set(i, vn-1, -1);
                }
            } else {						//=
                if(st.get(i, vn) < 0) {
                    st.lineMultiply(i, -1);
                }
            }
        }
    }

    public void print() {
        System.out.println("target:");
        target.print();
        System.out.println();
        System.out.println("condition:");
        st.print();
    }
}

class SimplexTable {
    private Matrix C;
    private Matrix mainTable;
    private Matrix initX;
    private boolean hadAddValue;
    private Vector<Integer> baseV;

    public SimplexTable(Matrix target, Matrix initMainTable, Vector<Integer> base) {
        C = new Matrix(target);
        mainTable = new Matrix(initMainTable);
        hadAddValue = false;
        baseV = new Vector<Integer>(base);
        int len = baseV.size();
        int bindex = mainTable.getSize().getCol()-1;
        initX = new Matrix(bindex, 1);
        for(int i=0; i<len; i++) {
            //initX.set(baseV.get(i), 0, mainTable.get(i, bindex));   //it will be some bug, because it's unsure that the var's a is 1
            //initX.set(baseV.get(i), 0, mainTable.get(i, bindex)/mainTable.get(i, baseV.get(i)));
            mainTable.lineMultiply(i, 1/mainTable.get(i, baseV.get(i)));
            initX.set(baseV.get(i), 0, mainTable.get(i, bindex));
        }
    }

    public SimplexTable(SimplexTable simplexTable) {
        C = new Matrix(simplexTable.C);
        mainTable = new Matrix(simplexTable.mainTable);
        initX = new Matrix(simplexTable.initX);
        hadAddValue = simplexTable.hadAddValue;
        baseV = new Vector<Integer>(simplexTable.baseV);
    }

    public Matrix calculateValue() {
        int len = C.getSize().getCol();
        int sn = mainTable.getSize().getRow();
        Matrix value = new Matrix(1, len+1);
        for(int i=0; i<len; i++) {
            double v = C.get(0, i);
            for(int j=0; j<sn; j++) {
                v -= C.get(0, baseV.get(j))*mainTable.get(j, i);
            }
            value.set(0, i, v);
        }
        return value;
    }

    public Matrix solve() {
        if(!hadAddValue) {
            Matrix value = calculateValue();
            mainTable.listSpell(value);
            hadAddValue = true;
        }
        double maxValue = 0;
        int maxValueIndex = -1;
        int sc = C.getSize().getCol();
        int msr = mainTable.getSize().getRow();
        for(int i=0; i<sc; i++) {
            if(maxValueIndex==-1 || mainTable.get(msr-1, i)>maxValue) {
                maxValue = mainTable.get(msr-1, i);
                maxValueIndex = i;
            }
        }
        if(maxValue > 0) {
            double minValue = 0;
            int minValueIndex = -1;
            int msc = mainTable.getSize().getCol();
            for(int i=0; i<msr-1; i++) {
                if(mainTable.get(i, maxValueIndex) > 0) {
                    if(minValueIndex==-1 || mainTable.get(i, msc-1)/mainTable.get(i, maxValueIndex)<minValue) {
                        minValue = mainTable.get(i, msc-1)/mainTable.get(i, maxValueIndex);
                        minValueIndex = i;
                    } else if(mainTable.get(i, msc-1)/mainTable.get(i, maxValueIndex) == minValue) {
                        if(baseV.get(i) > baseV.get(minValueIndex)) {
                            minValue = mainTable.get(i, msc-1)/mainTable.get(i, maxValueIndex);
                            minValueIndex = i;
                        }
                    }
                }
            }
            if(minValueIndex == -1) {
                initX.resize(0);
                return initX;
            } else {
                double a = 1/mainTable.get(minValueIndex, maxValueIndex);
                mainTable.lineTransformations(minValueIndex, maxValueIndex);
                mainTable.lineMultiply(minValueIndex, a);
                baseV.set(minValueIndex, maxValueIndex);
                return solve();
            }
        } else {
            initX.listMultiply(0, 0);
            int len = baseV.size();
            int bindex = mainTable.getSize().getCol()-1;
            for(int i=0; i<len; i++) {
                initX.set(baseV.get(i), 0, mainTable.get(i, bindex));
            }
            return initX;
        }
    }

    public Matrix getInitX() {
        return new Matrix(initX);
    }

    public Matrix getMainTable() {
        Matrix matrix;
        if(hadAddValue) {
            matrix = mainTable.lineAt(0, mainTable.getSize().getRow()-1);
        } else {
            matrix = new Matrix(mainTable);
        }
        int i;
        int csc = C.getSize().getCol();
        for(i=0; i<csc && C.get(0, i)==0; i++);
        int msc = matrix.getSize().getCol();
        Matrix b = matrix.listAt(msc-1, msc);
        matrix = matrix.listAt(0, i);
        matrix.lineSpell(b);
        return matrix;
    }

    public Vector<Integer> getBaseV() {
        return new Vector<Integer>(baseV);
    }

    public void print() {
        System.out.println("target: (format C1, C2, ..., Cn, for one line)");
        C.print();
        System.out.println();
        if(hadAddValue) {
            System.out.println("mainTable: (format x1, x2, ..., xn, b, for front line, value for the last line)");
        } else {
            System.out.println("mainTable: (format x1, x2, ..., xn, for each line");
        }
        mainTable.print();
        System.out.println();
        System.out.println("X: (format x1, x2, ..., xn, for one list)");
        initX.print();
    }
}

package com.example.zckj.math_tool;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by ZCKJ on 2019/9/8.
 */

public class Matrix {
    final public static double ALMOST_NULL = 1e-10;
    final public static int DIGIT = 10;
    private Vector<Vector<Double>> A;

    public Matrix(int row, int col) {
        A = new Vector<Vector<Double>>();
        for(int i=0; i<row; i++) {
            A.add(new Vector<Double>());
            for(int j=0; j<col; j++) {
                A.get(i).add(0.0);
            }
        }
    }

    public Matrix(int size) {
        this(size, size);
    }

    public Matrix() {
        this(0);
    }

    public Matrix(Size size) {
        this(size.getRow(), size.getCol());
    }

    public Matrix(double []array, int row, int col) {
        this(row, col);
        if(row>0 && col>0) {
            for(int i=0; i<row; i++) {
                for(int j=0; j<col; j++) {
                    A.get(i).set(j, array[i*col+j]);
                }
            }
        }
    }

    public Matrix(double []array, int size) {
        this(array, size, size);
    }

    public Matrix(double []array, Size size) {
        this(array, size.getRow(), size.getCol());
    }

    public Size getSize() {
        Size size = new Size();
        size.setRow(A.size());
        if(size.getRow() != 0) {
            size.setCol(A.get(0).size());
        } else {
            size.setCol(0);
        }
        return size;
    }

    public double get(int r, int c) {
        Size size = getSize();
        if(r>=0 && r<size.getRow() && c>=0 && c<size.getCol()) {
            return A.get(r).get(c).doubleValue();
        } else {
            return 0.0;
        }
    }

    public Matrix lineAt(int begin, int end) {
        Matrix matrix = new Matrix();
        if(end-begin>0 && begin>=0 && end<=A.size()) {
            for(int i=begin; i<end; i++) {
                matrix.A.add(new Vector<Double>(A.get(i)));
            }
        }
        return matrix;
    }

    public Matrix listAt(int begin, int end) {
        Matrix matrix = new Matrix();
        if(end-begin>0 && begin>=0 && end<=getSize().getCol()) {
            int len = A.size();
            for(int r=0; r<len; r++) {
                Vector<Double> tmp = new Vector<Double>();
                for(int c=begin; c<end; c++) {
                    tmp.add(A.get(r).get(c));
                }
                matrix.A.add(new Vector<Double>(tmp));
            }
        }
        return matrix;
    }

    public Matrix at(int br, int bc, int er, int ec) {
        Matrix matrix = lineAt(br, er+1);
        matrix = matrix.listAt(bc, ec+1);
        return matrix;
    }

    public Matrix at(Size begin, Size end) {
        return at(begin.getRow(), begin.getCol(), end.getRow(), end.getCol());
    }

    public boolean set(int r, int c, double value) {
        Size size = getSize();
        if(r>=0 && r<size.getRow() && c>=0 && c<size.getCol()) {
            A.get(r).set(c, value);
            return true;
        } else {
            return false;
        }
    }

    public Matrix(Matrix matrix) {
        this(matrix.getSize());
        Size size = matrix.getSize();
        for(int i=0; i<size.getRow(); i++) {
            for(int j=0; j<size.getCol(); j++) {
                set(i, j, matrix.get(i, j));
            }
        }
    }

    public boolean isTri() {
        boolean result = true;
        Size size = getSize();
        for(int c=0; c<size.getCol() && result; c++) {
            for(int r=c+1; r<size.getRow() && result; r++) {
                if(get(r, c) != 0) {
                    result = false;
                }
            }
        }
        return result;
    }

    public boolean isTri_() {
        boolean result = true;
        Size size = getSize();
        for(int r=0; r<size.getRow() && result; r++) {
            for(int c=r+1; c<size.getCol() && result; c++) {
                if(get(r, c) != 0) {
                    result = false;
                }
            }
        }
        return result;
    }

    public Matrix standar() {
        for(int i=0; i<getSize().getRow(); i++) {
            for(int j=0; j<getSize().getCol(); j++) {
                if(ALMOST_NULL != 0) {
                    double num = A.get(i).get(j);
                    double r = num%ALMOST_NULL;
                    if(r > 0) {
                        if(r >= ALMOST_NULL/2) {
                            A.get(i).set(j, num-r+ALMOST_NULL);
                        } else {
                            A.get(i).set(j, num-r);
                        }
                    } else if(r < 0) {
                        if(-r >= ALMOST_NULL/2) {
                            A.get(i).set(j, num-r-ALMOST_NULL);
                        } else {
                            A.get(i).set(j, num-r);
                        }
                    }
                }
            }
        }
        return this;
    }

    public void lineTransformation(int targetRow, int otherRow, double a) {
        Size size = getSize();
        if(targetRow>=0 && targetRow<size.getRow() && otherRow>=0 && otherRow<size.getRow()) {
            for(int c=0; c<size.getCol(); c++) {
                A.get(otherRow).set(c, A.get(otherRow).get(c)-A.get(targetRow).get(c)*a);
                if(Math.abs(A.get(otherRow).get(c)) < ALMOST_NULL) {
                    A.get(otherRow).set(c, 0.0);
                }
            }
        }
    }

    public double lineTransformation(int targetRow, int otherRow) {
        Size size = getSize();
        if(targetRow>=0 && targetRow<size.getRow() && otherRow>=0 && otherRow<size.getRow()) {
            double a = get(otherRow, targetRow)/get(targetRow, targetRow);
            lineTransformation(targetRow, otherRow, a);
            return a;
        } else {
            return 0;
        }
    }

    public void lineTransformations(int mainRow, int mainCol) {
        double a;
        Size size = getSize();
        if(mainRow>=0 && mainRow<size.getRow() && mainCol>=0 && mainCol<size.getCol()) {
            int row = size.getRow();
            for(int r=0; r<row; r++) {
                if(r != mainRow) {
                    a = get(r, mainCol)/get(mainRow, mainCol);
                    lineTransformation(mainRow, r, a);
                }
            }
        }
    }

    public void lineTransformations(Size point) {
        lineTransformations(point.getRow(), point.getCol());
    }

    public void lineMultiply(int row, double a) {
        Size size = getSize();
        if(row>=0 && row<size.getRow()) {
            int col = size.getCol();
            for(int c=0; c<col; c++) {
                set(row, c, get(row, c)*a);
            }
        }
    }

    public void listMultiply(int col, double a) {
        Size size = getSize();
        if(col>=0 && col<size.getCol()) {
            int row = size.getRow();
            for(int r=0; r<row; r++) {
                set(r, col, get(r, col)*a);
            }
        }
    }

    public boolean lineSpell(Matrix matrix) {
        Size size = getSize();
        Size size1 = matrix.getSize();
        if(size.getRow() == 0) {
            A.setSize(size1.getRow());
            size.setRow(size1.getRow());
        }
        if(size.getRow() == size1.getRow()) {
            int row = size.getRow();
            for(int r=0; r<row; r++) {
                A.get(r).addAll(new Vector<Double>(matrix.A.get(r)));
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean listSpell(Matrix matrix) {
        Size size = getSize();
        Size size1 = matrix.getSize();
        if(size.getCol()==0 || size.getCol()==size1.getCol()) {
            Matrix a = new Matrix(matrix);
            A.addAll(a.A);
            return true;
        } else {
            return false;
        }
    }

    public Matrix getTransposition() {
        Size size = getSize();
        int row = size.getRow();
        int col = size.getCol();
        Matrix matrix = new Matrix(col, row);
        for(int r=0; r<col; r++) {
            for(int c=0; c<row; c++) {
                matrix.set(r, c, get(c, r));
            }
        }
        return matrix;
    }

    public double getDeterminant(int curCol, double symbol) {
        Size size = getSize();
        if(size.getRow() == size.getCol()) {
            if(isTri()) {
                double result = 1;
                for(int i=0; i<size.getRow(); i++) {
                    result *= get(i, i);
                }
                return result*symbol;
            } else {
                Matrix m = new Matrix(this);
                int targetRow = -1;
                for(int r=curCol; r<size.getRow(); r++) {
                    if(m.get(r, curCol) != 0) {
                        targetRow = r;
                        break;
                    }
                }
                if(targetRow == -1) {
                    return 0;
                } else {
                    if(targetRow != curCol) {
                        symbol *= -1;
                        Collections.swap(m.A, targetRow, curCol);
                        targetRow = curCol;
                    }
                    for(int r=targetRow+1; r<size.getRow(); r++) {
                        m.lineTransformation(targetRow, r);
                    }
                    return m.getDeterminant(curCol+1, symbol);
                }
            }
        } else {
            return 0;
        }
    }

    public double getDeterminant(int curCol) {
        return getDeterminant(curCol, 1);
    }

    public double getDeterminant() {
        return getDeterminant(0);
    }

    public Matrix getInvProcess(Matrix E_, int curCol) {
        if(E_ == null) {
            return null;
        }
        Matrix E = new Matrix(E_);
        Size size = getSize();
        if(isTri() && isTri_()) {
            for(int i=0; i<size.getRow(); i++) {
                if(get(i, i) != -1) {
                    for(int c=0; c<size.getCol(); c++) {
                        E.set(i, c, E.get(i, c)/get(i, i));
                    }
                }
            }
            return E;
        } else {
            Matrix m = new Matrix(this);
            int targetRow = -1;
            for(int r=curCol; r<size.getRow(); r++) {
                if(m.get(r, curCol) != 0) {
                    targetRow = r;
                    break;
                }
            }
            if(targetRow == -1) {
                return null;
            } else {
                if(targetRow != curCol) {
                    Collections.swap(m.A, targetRow, curCol);
                    Collections.swap(E.A, targetRow, curCol);
                    targetRow = curCol;
                }
                for(int i=0; i<size.getRow(); i++) {
                    if(i != targetRow) {
                        E.lineTransformation(targetRow, i, m.lineTransformation(targetRow, i));
                    }
                }
                return m.getInvProcess(E, curCol+1);
            }
        }
    }

    public Matrix getInvProcess(Matrix E) {
        return getInvProcess(E, 0);
    }

    public Matrix getInvertor() {
        Size size = getSize();
        if(getDeterminant() != 0) {
            Matrix E = new Matrix(size);
            for(int i=0; i<size.getRow(); i++) {
                E.set(i, i, 1.0);
            }
            return getInvProcess(E);
        } else {
            return null;
        }
    }

    public void resize(int row, int col) {
        if(row==0 || col==0) {
            A.setSize(0);
        } else {
            Size size = getSize();
            int sr = size.getRow();
            int sc = size.getCol();
            for(int i=0; i<row; i++) {
                if(i >= sr) {
                    A.add(new Vector<Double>());
                }
                for(int j=0; j<col; j++) {
                    if(i>=sr || j>=sc) {
                        A.get(i).add(0.0);
                    }
                }
                if(i < sr) {
                    for(int j=col; j<sc; j++) {
                        A.get(i).remove(col);
                    }
                }
            }
            for(int i=row; i<sr; i++) {
                A.remove(row);
            }
        }
    }

    public void resize(Size size) {
        resize(size.getRow(), size.getCol());
    }

    public void resize(int size) {
        resize(size, size);
    }

    public Matrix multiply(Matrix matrix) {
        if(matrix == null) {
            return null;
        }
        Size size1 = getSize();
        Size size2 = matrix.getSize();
        double sum;
        if(size1.getCol() == size2.getRow()) {
            Size size = new Size(size1.getRow(), size2.getCol());
            Matrix result = new Matrix(size);
            for(int r=0; r<size.getRow(); r++) {
                for(int c=0; c<size.getCol(); c++) {
                    sum = 0;
                    for(int k=0; k<size1.getCol(); k++) {
                        sum += get(r, k)*matrix.get(k, c);
                    }
                    if(Math.abs(sum) < ALMOST_NULL) {
                        sum = 0;
                    }
                    result.set(r, c, sum);
                }
            }
            return result;
        } else {
            return null;
        }
    }

    public Matrix multiply(double n) {
        Size size = getSize();
        Matrix m = new Matrix(this);
        for(int r=0; r<size.getRow(); r++) {
            for(int c=0; c<size.getCol(); c++) {
                m.set(r, c, get(r, c)*n);
            }
        }
        return m;
    }

    public Matrix divide(double n) {
        return multiply(1/n);
    }

    public boolean equals(Matrix matrix) {
        if(matrix == null) {
            return false;
        }
        boolean result = false;
        Size size;
        if((size = getSize()).equals(matrix.getSize())) {
            result = true;
            for(int r=0; r<size.getRow() && result; r++) {
                for(int c=0; c<size.getCol() && result; c++) {
                    if(Math.abs(get(r, c)-matrix.get(r, c)) > ALMOST_NULL) {
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    public void print() {
        Size size = getSize();
        System.out.println("[");
        if(size.getRow() != 0 && size.getCol() != 0) {
            for(int r=0; r<size.getRow(); r++) {
                for(int c=0; c<size.getCol(); c++) {
                    System.out.print("\t"+get(r, c));
                }
                System.out.println();
            }
        }
        System.out.println("]");
    }

    public boolean isEmpty() {
        if(getSize().getRow() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String double2String(double x) {
        Double d = new Double(x);
        BigDecimal b = new BigDecimal(d.toString()).setScale(DIGIT, BigDecimal.ROUND_DOWN);
        String s = b.toPlainString().replaceAll("0+?$", "").replaceAll("[.]$", "");
        return s;
    }

    public String toString() {
        String result = "";
        Size size = getSize();
        if(size.getRow() != 0 && size.getCol() != 0) {
            for(int r=0; r<size.getRow(); r++) {
                for(int c=0; c<size.getCol(); c++) {
                    //result += get(r, c)+"\t";
                    String s = double2String(get(r, c));
                    result += s+"\t";
                }
                result += "\n";
            }
        }
        return result;
    }
}

class Size {
    private int row;
    private int col;

    public Size(int r, int c) {
        row = r;
        col = c;
    }

    public Size(int s) {
        this(s, s);
    }

    public Size() {
        this(0, 0);
    }

    public Size(Size s) {
        if(s != null) {
            row = s.row;
            col = s.col;
        }
    }

    public void setSize(int r, int c) {
        row = r;
        col = c;
    }

    public void setRow(int r) {
        row = r;
    }

    public void setCol(int c) {
        col = c;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean equals(Size size) {
        if(size == null) {
            return false;
        }
        if(row == size.row && col == size.col) {
            return true;
        } else {
            return false;
        }
    }

}

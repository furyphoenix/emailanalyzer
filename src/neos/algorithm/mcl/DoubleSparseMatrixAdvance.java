package neos.algorithm.mcl;

//~--- non-JDK imports --------------------------------------------------------

import JSci.GlobalSettings;

import JSci.maths.DimensionException;
import JSci.maths.Mapping;
import JSci.maths.matrices.AbstractComplexMatrix;
import JSci.maths.matrices.AbstractDoubleMatrix;
import JSci.maths.matrices.AbstractIntegerMatrix;
import JSci.maths.matrices.ComplexMatrix;
import JSci.maths.matrices.DoubleMatrix;
import JSci.maths.matrices.DoubleSparseSquareMatrix;
import JSci.maths.matrices.DoubleSquareMatrix;
import JSci.maths.matrices.IntegerMatrix;
import JSci.maths.matrices.Matrix;
import JSci.maths.matrices.MatrixDimensionException;
import JSci.maths.vectors.AbstractDoubleVector;
import JSci.maths.vectors.DoubleSparseVector;
import JSci.maths.vectors.DoubleVector;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public final class DoubleSparseMatrixAdvance extends AbstractDoubleMatrix {

    /**
     * Amount by which to increase the capacity.
     */
    private int capacityIncrement = 1;

    /**
     * Sparse indexing data.
     * Contains the column positions of each element,
     * e.g. <code>colPos[n]</code> is the column position
     * of the <code>n</code>th element.
     */
    private int colPos[];

    /**
     * Matrix elements.
     */
    private double elements[];

    /**
     * Sparse indexing data.
     * Contains the indices of the start of each row,
     * e.g. <code>rows[i]</code> is the index
     * where the <code>i</code>th row starts.
     */
    private int rows[];

    /**
     * Constructs a matrix from an array.
     * @param array an assigned value
     */
    public DoubleSparseMatrixAdvance(final double array[][]) {
        super(array.length, array[0].length);
        rows = new int[numRows + 1];

        int n = 0;

        for (int i = 0; i < numRows; i++) {
            if (array[i].length != array.length) {
                throw new MatrixDimensionException("Array is not square.");
            }

            for (int j = 0; j < numCols; j++) {
                if (Math.abs(array[i][j]) > GlobalSettings.ZERO_TOL) {
                    n++;
                }
            }
        }

        elements = new double[n];
        colPos   = new int[n];
        n        = 0;

        for (int i = 0; i < numRows; i++) {
            rows[i] = n;

            for (int j = 0; j < numCols; j++) {
                if (Math.abs(array[i][j]) > GlobalSettings.ZERO_TOL) {
                    elements[n] = array[i][j];
                    colPos[n]   = j;
                    n++;
                }
            }
        }

        rows[numRows] = n;
    }

    /**
     * Constructs an empty matrix.
     * @param rowCount the number of rows
     * @param colCount the number of columns
     */
    public DoubleSparseMatrixAdvance(final int rowCount, final int colCount) {
        super(rowCount, colCount);
        elements = new double[0];
        colPos   = new int[0];
        rows     = new int[numRows + 1];
    }

    public DoubleSparseMatrixAdvance(final int rowCount, final int colCount, int capacityIncrement) {
        this(rowCount, colCount);
        this.capacityIncrement = capacityIncrement;
    }

    public Hashtable<Integer, ArrayList<Integer>> getRowPosByColTab() {
        Hashtable<Integer, ArrayList<Integer>> rowPosByColTab = new Hashtable<Integer, ArrayList<Integer>>();
        int                                    offset         = 0;

        for (int rowId = 0; rowId < rows.length - 1; rowId++) {
            for (int i = offset; i < offset + rows[rowId + 1] - rows[rowId]; i++) {
                int                pos = colPos[i];
                ArrayList<Integer> lst = rowPosByColTab.get(pos);

                if (lst == null) {
                    lst = new ArrayList<Integer>();
                }

                lst.add(rowId);
                rowPosByColTab.put(pos, lst);
            }

            offset += (rows[rowId + 1] - rows[rowId]);
        }

        return rowPosByColTab;
    }

    public double getMinValue() {
        double v = elements[0];

        for (int i = 1; i < elements.length; i++) {
            v = (elements[i] < v)
                ? elements[i]
                : v;
        }

        return v;
    }

    public double getMaxValue() {
        double v = elements[0];

        for (int i = 1; i < elements.length; i++) {
            v = (elements[i] > v)
                ? elements[i]
                : v;
        }

        return v;
    }

    public double[] getRowElement(int row) {
        if (row >= this.numRows) {
            return null;
        }

        int beginIdx = rows[row];
        int endIdx   = rows[row + 1];
        int cnt      = endIdx - beginIdx;

        if (cnt == 0) {
            return null;
        }

        double[] elems = new double[cnt];

        for (int i = beginIdx; i < endIdx; i++) {
            elems[i - beginIdx] = elements[i];
        }

        return elems;
    }

    public int[] getRowElementPos(int row) {
        if (row >= this.numRows) {
            return null;
        }

        int beginIdx = rows[row];
        int endIdx   = rows[row + 1];
        int cnt      = endIdx - beginIdx;

        if (cnt == 0) {
            return null;
        }

        int[] elems = new int[cnt];

        for (int i = beginIdx; i < endIdx; i++) {
            elems[i - beginIdx] = colPos[i];
        }

        return elems;
    }

    public double getMinValueInRow(int rowNum) {
        if (rowNum > this.numRows - 1) {
            throw new IllegalArgumentException();
        }

        int beginIdx = rows[rowNum];
        int endIdx;

        if (rowNum == this.numRows - 1) {
            endIdx = elements.length;
        } else {
            endIdx = rows[rowNum + 1];
        }

        double v = elements[beginIdx];

        for (int i = beginIdx + 1; i < endIdx; i++) {
            v = (elements[i] < v)
                ? elements[i]
                : v;
        }

        return v;
    }

    public double getMaxValueInRow(int rowNum) {
        if (rowNum > this.numRows - 1) {
            throw new IllegalArgumentException();
        }

        int beginIdx = rows[rowNum];
        int endIdx;

        if (rowNum == this.numRows - 1) {
            endIdx = elements.length;
        } else {
            endIdx = rows[rowNum + 1];
        }

        double v = elements[beginIdx];

        for (int i = beginIdx + 1; i < endIdx; i++) {
            v = (elements[i] > v)
                ? elements[i]
                : v;
        }

        return v;
    }

    public Enumeration<SparseMatrixEntry> getMatrixValues() {
        return new MatrixEntryEnum();
    }

    /**
     * Compares two double sparse matrices for equality.
     * @param m a double matrix
     */
    public boolean equals(AbstractDoubleMatrix m, double tol) {
        if ((numRows == m.rows()) && (numCols == m.columns())) {
            if (m instanceof DoubleSparseMatrixAdvance) {
                return this.equals((DoubleSparseMatrixAdvance) m);
            } else {
                double sumSqr = 0;

                for (int i = 0; i < numRows; i++) {
                    for (int j = 0; j < numCols; j++) {
                        double delta = getElement(i, j) - m.getElement(i, j);

                        sumSqr += delta * delta;
                    }
                }

                return (sumSqr <= tol * tol);
            }
        } else {
            return false;
        }
    }

    public final boolean equals(DoubleSparseMatrixAdvance m) {
        return equals(m, GlobalSettings.ZERO_TOL);
    }

    public boolean equals(DoubleSparseMatrixAdvance m, double tol) {
        if ((numRows == m.rows()) && (numCols == m.columns())) {
            for (int i = 1; i < rows.length; i++) {
                if (rows[i] != m.rows[i]) {
                    return false;
                }
            }

            double sumSqr = 0.0;

            for (int i = 0; i < rows[numRows]; i++) {
                if (colPos[i] != m.colPos[i]) {
                    return false;
                }

                double delta = elements[i] - m.elements[i];

                sumSqr += delta * delta;
            }

            return (sumSqr <= tol * tol);
        } else {
            return false;
        }
    }

    /**
     * Returns a string representing this matrix.
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer(numRows * numCols);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                buf.append(getElement(i, j));
                buf.append(' ');
            }

            buf.append('\n');
        }

        return buf.toString();
    }

    /**
     * Converts this matrix to an integer matrix.
     * @return an integer matrix
     */
    public AbstractIntegerMatrix toIntegerMatrix() {
        final int ans[][] = new int[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                ans[i][j] = Math.round((float) getElement(i, j));
            }
        }

        return new IntegerMatrix(ans);
    }

    /**
     * Converts this matrix to a complex matrix.
     * @return a complex matrix
     */
    public AbstractComplexMatrix toComplexMatrix() {
        final double arrayRe[][] = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                arrayRe[i][j] = getElement(i, j);
            }
        }

        return new ComplexMatrix(arrayRe, new double[numRows][numCols]);
    }

    /**
     * Returns an element of the matrix.
     * @param i row index of the element
     * @param j column index of the element
     * @exception MatrixDimensionException If attempting to access an invalid element.
     */
    public double getElement(final int i, final int j) {
        if ((i >= 0) && (i < numRows) && (j >= 0) && (j < numCols)) {
            int p = rows[i];

            while ((p < rows[i + 1]) && (colPos[p] < j)) {
                p++;
            }

            if ((p < rows[i + 1]) && (colPos[p] == j)) {
                return elements[p];
            } else {
                return 0.0;
            }
        } else {
            throw new MatrixDimensionException(getInvalidElementMsg(i, j));
        }
    }

    /**
     * Sets the value of an element of the matrix.
     * @param i row index of the element
     * @param j column index of the element
     * @param x a number
     * @exception MatrixDimensionException If attempting to access an invalid element.
     */
    public void setElement(final int i, final int j, final double x) {
        if ((i >= 0) && (i < numRows) && (j >= 0) && (j < numCols)) {
            int p = rows[i];

            while ((p < rows[i + 1]) && (colPos[p] < j)) {
                p++;
            }

            if ((p < rows[i + 1]) && (colPos[p] == j)) {
                if (Math.abs(x) <= GlobalSettings.ZERO_TOL) {

                    // shrink if zero
                    System.arraycopy(elements, p + 1, elements, p, rows[numRows] - p - 1);
                    System.arraycopy(colPos, p + 1, colPos, p, rows[numRows] - p - 1);

                    for (int k = i + 1; k < rows.length; k++) {
                        rows[k]--;
                    }
                } else {

                    // overwrite
                    elements[p] = x;
                }
            } else if (Math.abs(x) > GlobalSettings.ZERO_TOL) {

                // expand
                if (rows[numRows] == elements.length) {

                    // increase capacity
                    final double oldMatrix[] = elements;
                    final int    oldColPos[] = colPos;

                    elements = new double[oldMatrix.length + capacityIncrement];
                    colPos   = new int[oldColPos.length + capacityIncrement];
                    System.arraycopy(oldMatrix, 0, elements, 0, p);
                    System.arraycopy(oldColPos, 0, colPos, 0, p);
                    System.arraycopy(oldMatrix, p, elements, p + 1, oldMatrix.length - p);
                    System.arraycopy(oldColPos, p, colPos, p + 1, oldColPos.length - p);
                } else {
                    System.arraycopy(elements, p, elements, p + 1, rows[numRows] - p);
                    System.arraycopy(colPos, p, colPos, p + 1, rows[numRows] - p);
                }

                elements[p] = x;
                colPos[p]   = j;

                for (int k = i + 1; k < rows.length; k++) {
                    rows[k]++;
                }
            }
        } else {
            throw new MatrixDimensionException(getInvalidElementMsg(i, j));
        }
    }

    /**
     * Returns the number of non-zero elements.
     */
    public int elementCount() {
        return rows[numRows];
    }

    /**
     * Returns the l<sup><img border=0 alt="infinity" src="doc-files/infinity.gif"></sup>-norm.
     */
    public double infNorm() {
        double result = 0.0, tmpResult;

        for (int i = 0; i < numRows; i++) {
            tmpResult = 0.0;

            for (int j = rows[i]; j < rows[i + 1]; j++) {
                tmpResult += Math.abs(elements[j]);
            }

            if (tmpResult > result) {
                result = tmpResult;
            }
        }

        return result;
    }

    /**
     * Returns the Frobenius (l<sup>2</sup>) norm.
     */
    public double frobeniusNorm() {
        double result = 0.0;

        for (int i = 0; i < rows[numRows]; i++) {
            result += elements[i] * elements[i];
        }

        return Math.sqrt(result);
    }

//  ============
//   OPERATIONS
//  ============
//   ADDITION

    /**
     * Returns the addition of this matrix and another.
     * @param m a double matrix
     * @exception MatrixDimensionException If the matrices are different sizes.
     */
    public AbstractDoubleMatrix add(final AbstractDoubleMatrix m) {
        if (m instanceof DoubleSparseMatrixAdvance) {
            return add((DoubleSparseMatrixAdvance) m);
        }

        if (m instanceof DoubleMatrix) {
            return add((DoubleMatrix) m);
        }

        if ((numRows == m.rows()) && (numCols == m.columns())) {
            final double array[][] = new double[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                for (int j = rows[i]; j < rows[i + 1]; j++) {
                    array[i][colPos[j]] = elements[j];
                }

                array[i][0] += m.getElement(i, 0);

                for (int j = 1; j < numCols; j++) {
                    array[i][j] += m.getElement(i, j);
                }
            }

            return new DoubleMatrix(array);
        } else {
            throw new MatrixDimensionException("Matrices are different sizes.");
        }
    }

    public DoubleMatrix add(final DoubleMatrix m) {
        if ((numRows == m.rows()) && (numCols == m.columns())) {
            final double array[][] = new double[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                for (int j = rows[i]; j < rows[i + 1]; j++) {
                    array[i][colPos[j]] = elements[j];
                }

                array[i][0] += m.getElement(i, 0);

                for (int j = 1; j < numCols; j++) {
                    array[i][j] += m.getElement(i, j);
                }
            }

            return new DoubleMatrix(array);
        } else {
            throw new MatrixDimensionException("Matrices are different sizes.");
        }
    }

    /**
     * Returns the addition of this matrix and another.
     * @param m a double sparse matrix
     * @exception MatrixDimensionException If the matrices are different sizes.
     */
    public DoubleSparseMatrixAdvance add(final DoubleSparseMatrixAdvance m) {
        if ((numRows == m.rows()) && (numCols == m.columns())) {
            DoubleSparseMatrixAdvance ans = new DoubleSparseMatrixAdvance(numRows, numCols);

            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    ans.setElement(i, j, getElement(i, j) + m.getElement(i, j));
                }
            }

            return ans;
        } else {
            throw new MatrixDimensionException("Matrices are different sizes.");
        }
    }

//  SUBTRACTION

    /**
     * Returns the subtraction of this matrix and another.
     * @param m a double matrix
     * @exception MatrixDimensionException If the matrices are different sizes.
     */
    public AbstractDoubleMatrix subtract(final AbstractDoubleMatrix m) {
        if (m instanceof DoubleSparseMatrixAdvance) {
            return subtract((DoubleSparseMatrixAdvance) m);
        }

        if (m instanceof DoubleMatrix) {
            return subtract((DoubleMatrix) m);
        }

        if ((numRows == m.rows()) && (numCols == m.columns())) {
            final double array[][] = new double[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                for (int j = rows[i]; j < rows[i + 1]; j++) {
                    array[i][colPos[j]] = elements[j];
                }

                array[i][0] -= m.getElement(i, 0);

                for (int j = 1; j < numCols; j++) {
                    array[i][j] -= m.getElement(i, j);
                }
            }

            return new DoubleMatrix(array);
        } else {
            throw new MatrixDimensionException("Matrices are different sizes.");
        }
    }

    public DoubleMatrix subtract(final DoubleMatrix m) {
        if ((numRows == m.rows()) && (numCols == m.columns())) {
            final double array[][] = new double[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                for (int j = rows[i]; j < rows[i + 1]; j++) {
                    array[i][colPos[j]] = elements[j];
                }

                array[i][0] -= m.getElement(i, 0);

                for (int j = 1; j < numCols; j++) {
                    array[i][j] -= m.getElement(i, j);
                }
            }

            return new DoubleMatrix(array);
        } else {
            throw new MatrixDimensionException("Matrices are different sizes.");
        }
    }

    /**
     * Returns the addition of this matrix and another.
     * @param m a double sparse matrix
     * @exception MatrixDimensionException If the matrices are different sizes.
     */
    public DoubleSparseMatrixAdvance subtract(final DoubleSparseMatrixAdvance m) {
        if ((numRows == m.rows()) && (numCols == m.columns())) {
            DoubleSparseMatrixAdvance ans = new DoubleSparseMatrixAdvance(numRows, numCols);

            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    ans.setElement(i, j, getElement(i, j) - m.getElement(i, j));
                }
            }

            return ans;
        } else {
            throw new MatrixDimensionException("Matrices are different sizes.");
        }
    }

//  SCALAR MULTIPLICATION

    /**
     * Returns the multiplication of this matrix by a scalar.
     * @param x a double
     * @return a double sparse matrix
     */
    public AbstractDoubleMatrix scalarMultiply(final double x) {
        final DoubleSparseMatrixAdvance ans = new DoubleSparseMatrixAdvance(numRows, numCols);

        ans.elements = new double[elements.length];
        ans.colPos   = new int[colPos.length];
        System.arraycopy(colPos, 0, ans.colPos, 0, colPos.length);
        System.arraycopy(rows, 0, ans.rows, 0, rows.length);

        for (int i = 0; i < rows[numRows]; i++) {
            ans.elements[i] = x * elements[i];
        }

        return ans;
    }

    public AbstractDoubleMatrix scalarDivide(final double x) {
        final DoubleSparseMatrixAdvance ans = new DoubleSparseMatrixAdvance(numRows, numCols);

        ans.elements = new double[elements.length];
        ans.colPos   = new int[colPos.length];
        System.arraycopy(colPos, 0, ans.colPos, 0, colPos.length);
        System.arraycopy(rows, 0, ans.rows, 0, rows.length);

        for (int i = 0; i < rows[numRows]; i++) {
            ans.elements[i] = elements[i] / x;
        }

        return ans;
    }

//  SCALAR PRODUCT

    /**
     * Returns the scalar product of this matrix and another.
     * @param m a double matrix.
     * @exception MatrixDimensionException If the matrices are different sizes.
     */
    public double scalarProduct(final AbstractDoubleMatrix m) {
        if (m instanceof DoubleMatrix) {
            return scalarProduct((DoubleMatrix) m);
        }

        if ((numRows == m.rows()) && (numCols == m.columns())) {
            double ans = 0.0;

            for (int i = 0; i < numRows; i++) {
                for (int j = rows[i]; j < rows[i + 1]; j++) {
                    ans += elements[j] * m.getElement(i, colPos[j]);
                }
            }

            return ans;
        } else {
            throw new MatrixDimensionException("Matrices are different sizes.");
        }
    }

    public double scalarProduct(final DoubleMatrix m) {
        if ((numRows == m.rows()) && (numCols == m.columns())) {
            double ans = 0.0;

            for (int i = 0; i < numRows; i++) {
                for (int j = rows[i]; j < rows[i + 1]; j++) {
                    ans += elements[j] * m.getElement(i, colPos[j]);
                }
            }

            return ans;
        } else {
            throw new MatrixDimensionException("Matrices are different sizes.");
        }
    }

//  MATRIX MULTIPLICATION

    /**
     * Returns the multiplication of a vector by this matrix.
     * @param v a double vector
     * @exception DimensionException If the matrix and vector are incompatible.
     */
    public AbstractDoubleVector multiply(final AbstractDoubleVector v) {
        if (v instanceof DoubleSparseVector) {
            return multiply((DoubleSparseVector) v);
        }

        if (numCols == v.dimension()) {
            final double array[] = new double[numRows];

            for (int i = 0; i < numRows; i++) {
                for (int j = rows[i]; j < rows[i + 1]; j++) {
                    array[i] += elements[j] * v.getComponent(colPos[j]);
                }
            }

            return new DoubleVector(array);
        } else {
            throw new DimensionException("Matrix and vector are incompatible.");
        }
    }

    /**
     * Returns the multiplication of a sparse vector by this matrix.
     * @param v a double vector
     * @exception DimensionException If the matrix and vector are incompatible.
     */
    public AbstractDoubleVector multiply(final DoubleSparseVector v) {
        if (numCols == v.dimension()) {
            DoubleSparseVector res = new DoubleSparseVector(numRows);

            for (int i = 0; i < numRows; i++) {
                double tmp = 0.0;

                for (int j = rows[i]; j < rows[i + 1]; j++) {
                    tmp += elements[j] * v.getComponent(colPos[j]);
                }

                res.setComponent(i, tmp);
            }

            return res;
        } else {
            throw new DimensionException("Matrix and vector are incompatible.");
        }
    }

    /**
     * Returns the multiplication of this matrix and another.
     * @param m a double matrix
     * @exception MatrixDimensionException If the matrices are incompatible.
     */
    public AbstractDoubleMatrix multiply(final AbstractDoubleMatrix m) {
        if (m instanceof DoubleSparseMatrixAdvance) {
            return multiply((DoubleSparseMatrixAdvance) m);
        }

        if (m instanceof DoubleMatrix) {
            return multiply((DoubleMatrix) m);
        }

        if (numCols == m.rows()) {
            final double array[][] = new double[numRows][m.columns()];

            for (int j = 0; j < numRows; j++) {
                for (int k = 0; k < m.columns(); k++) {
                    for (int n = rows[j]; n < rows[j + 1]; n++) {
                        array[j][k] += elements[n] * m.getElement(colPos[n], k);
                    }
                }
            }

            if (numRows == m.columns()) {
                return new DoubleSquareMatrix(array);
            } else {
                return new DoubleMatrix(array);
            }
        } else {
            throw new MatrixDimensionException("Incompatible matrices.");
        }
    }

    public AbstractDoubleMatrix multiply(final DoubleMatrix m) {
        if (numCols == m.rows()) {
            final double array[][] = new double[numRows][m.columns()];

            for (int j = 0; j < numRows; j++) {
                for (int k = 0; k < m.columns(); k++) {
                    for (int n = rows[j]; n < rows[j + 1]; n++) {
                        array[j][k] += elements[n] * m.getElement(colPos[n], k);
                    }
                }
            }

            if (numRows == m.columns()) {
                return new DoubleSquareMatrix(array);
            } else {
                return new DoubleMatrix(array);
            }
        } else {
            throw new MatrixDimensionException("Incompatible matrices.");
        }
    }

    /**
     * Returns the multiplication of this matrix and another.
     * @param m a double sparse matrix
     * @exception MatrixDimensionException If the matrices are incompatible.
     */
    public AbstractDoubleMatrix multiply(final DoubleSparseMatrixAdvance m) {
        if (numCols == m.rows()) {
            AbstractDoubleMatrix ans;

            if (numRows == m.columns()) {
                ans = new DoubleSparseSquareMatrix(numRows);
            } else {
                ans = new DoubleSparseMatrixAdvance(numRows, m.columns());
            }

            for (int j = 0; j < ans.rows(); j++) {
                for (int k = 0; k < ans.columns(); k++) {
                    double tmp = 0.0;

                    for (int n = rows[j]; n < rows[j + 1]; n++) {
                        tmp += elements[n] * m.getElement(colPos[n], k);
                    }

                    ans.setElement(j, k, tmp);
                }
            }

            return ans;
        } else {
            throw new MatrixDimensionException("Incompatible matrices.");
        }
    }

//  TRANSPOSE

    /**
     * Returns the transpose of this matrix.
     * @return a double sparse matrix
     */
    public Matrix transpose() {
        final DoubleSparseMatrixAdvance ans = new DoubleSparseMatrixAdvance(numCols, numRows);

        for (int i = 0; i < numRows; i++) {
            ans.setElement(0, i, getElement(i, 0));

            for (int j = 1; j < numCols; j++) {
                ans.setElement(j, i, getElement(i, j));
            }
        }

        return ans;
    }

//  MAP ELEMENTS

    /**
     * Applies a function on all the matrix elements.
     * @param f a user-defined function
     * @return a double sparse matrix
     */
    public AbstractDoubleMatrix mapElements(final Mapping f) {
        final DoubleSparseMatrixAdvance ans = new DoubleSparseMatrixAdvance(numRows, numCols);

        ans.elements = new double[elements.length];
        ans.colPos   = new int[colPos.length];
        System.arraycopy(colPos, 0, ans.colPos, 0, colPos.length);
        System.arraycopy(rows, 0, ans.rows, 0, rows.length);

        for (int i = 0; i < rows[numRows]; i++) {
            ans.elements[i] = f.map(elements[i]);
        }

        return ans;
    }

    public static void main(String[] args) {
        double[][]                m      = {
            { 1, 0, 0 }, { 0, 0, 0 }, { 0, 0, 5 }
        };
        DoubleSparseMatrixAdvance matrix = new DoubleSparseMatrixAdvance(m);

        for (int i = 0; i < matrix.numRows; i++) {
            int[] rowElems = matrix.getRowElementPos(i);

            if (rowElems == null) {
                System.out.println("null");

                continue;
            }

            for (int j = 0; j < rowElems.length; j++) {
                System.out.print(rowElems[j] + " ");
            }

            System.out.print("\r\n");
        }

        System.out.println(matrix.rows);
        System.out.println(matrix.colPos.toString());

        Hashtable<Integer, ArrayList<Integer>> tab  = matrix.getRowPosByColTab();
        Enumeration<Integer>                   cols = tab.keys();

        while (cols.hasMoreElements()) {
            Integer currCol = cols.nextElement();

            System.out.print(currCol.intValue() + ":");

            ArrayList<Integer> currList = tab.get(currCol);

            for (Integer row : currList) {
                System.out.print(row.intValue() + ",");
            }

            System.out.print("\r\n");
        }

        Enumeration<SparseMatrixEntry> entrys = matrix.getMatrixValues();

        while (entrys.hasMoreElements()) {
            System.out.println(entrys.nextElement().toString());
        }
    }

    private class MatrixEntryEnum implements Enumeration<SparseMatrixEntry> {
        private int idx = 0;

        // private int offset=0;
        private int rowIdx = 0;

        @Override
        public boolean hasMoreElements() {

            // TODO Auto-generated method stub
            if (idx < elements.length) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public SparseMatrixEntry nextElement() {

            // TODO Auto-generated method stub
            while (idx == rows[rowIdx + 1]) {
                rowIdx++;
            }

            SparseMatrixEntry entry = new SparseMatrixEntry(rowIdx, colPos[idx], elements[idx]);

            idx++;

            return entry;
        }
    }
}

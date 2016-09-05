package neos.algorithm.mcl;

public class SparseMatrixEntry {
    public int    col;
    public int    row;
    public double value;

    public SparseMatrixEntry(int row, int col, double value) {
        this.row   = row;
        this.col   = col;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[" + row + "," + col + "," + value + "]";
    }
}

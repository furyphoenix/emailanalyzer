package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.BitSet;

public abstract class Distance {
    private double[][] distanceMatrix;

    public Distance(BitSet[] items) {
        int itemNo = items.length;

        this.distanceMatrix = new double[itemNo][itemNo];

        for (int i = 0; i < itemNo; i++) {
            for (int j = 0; j < itemNo; j++) {
                double tmp55_52 = getDistance(items[i], items[j]);

                this.distanceMatrix[j][i] = tmp55_52;
                this.distanceMatrix[i][j] = tmp55_52;
            }
        }
    }

    public void setDistance(double[][] distance) {
        int rows    = distance.length;
        int columns = distance[0].length;

        if (rows != columns) {
            System.out.println("The supplied Distance Matrix must be n x n. You gave: " + rows + " x " + columns);
            System.exit(-3);
        }

        this.distanceMatrix = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.distanceMatrix[i][j] = distance[i][j];
            }
        }
    }

    public abstract double getDistance(BitSet paramBitSet1, BitSet paramBitSet2);

    public double[][] getMatrix() {
        return this.distanceMatrix;
    }

    public String toString() {
        StringBuffer sb  = new StringBuffer();
        int          len = this.distanceMatrix.length;

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                sb.append(this.distanceMatrix[i][j]);
                sb.append("  ");
            }

            sb.append("\n");
        }

        return sb.toString();
    }
}

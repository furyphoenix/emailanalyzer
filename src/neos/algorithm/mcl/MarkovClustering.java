package neos.algorithm.mcl;

//~--- JDK imports ------------------------------------------------------------

import java.util.Iterator;
import java.util.Set;

public class MarkovClustering {

    /**
     * test the MCL algorithm with the matrix loaded from the file in the
     * argument. If no argument is given, the default file for the matrix T(G3 +
     * I) from van Dongen (2000), page 50.
     *
     * @param args
     */
    private MCLMornitor mor;

    /*
     * private static void print(SparseMatrix a, String label) {
     *   System.out.println(label + ":");
     *   // use transpose to compare with van Dongen's thesis
     *   System.out.println(a.transpose().toStringDense());
     * }
     */
    public MarkovClustering() {

        // nothing yet
    }

    /*public static void main(String[] args) {
        String file = "m.txt";

        if (args.length > 1) {
            file = args[0];
        }

        double[][]   a  = MatrixLoader.loadDense(file);
        SparseMatrix aa = new SparseMatrix(a);

        // we use the transpose because our sparse matrices are row-major
        // aa = aa.transpose();
        double           maxResidual = 0.001;
        double           gammaExp    = 2.0;
        double           loopGain    = 0.;
        double           zeroMax     = 0.001;
        MarkovClustering mcl         = new MarkovClustering();

        aa = mcl.run(aa, maxResidual, gammaExp, loopGain, zeroMax);
        aa = aa.transpose();

        for (int i = 0; i < aa.size(); i++) {
            SparseVector sv     = aa.get(i);
            Set<Integer> indexs = sv.keySet();

            if (indexs.size() > 0) {
                Iterator<Integer> iter = indexs.iterator();

                while (iter.hasNext()) {
                    System.out.print((iter.next().intValue() + 1) + " ");
                }

                System.out.print("\r\n");
            }
        }

        // print(aa, "result");
    }*/

    public void setMornitor(MCLMornitor mor) {
        this.mor = mor;
    }

    /**
     * run the MCL process.
     *
     * @param a matrix
     * @param maxResidual maximum difference between row elements and row square
     *        sum (measure of idempotence)
     * @param pGamma inflation exponent for Gamma operator
     * @param loopGain values for cycles
     * @param maxZero maximum value considered zero for pruning operations
     * @return the resulting matrix
     */
    public SparseMatrix run(SparseMatrix a, double maxResidual, double pGamma, double loopGain, double maxZero) {

        /*
         * Vectors.setFormat(8, 3);
         * print(a, "original matrix");
         */

        // add cycles
        addLoops(a, loopGain);

        if (loopGain > 0) {

            // print(a, "... with added loops");
            // make stochastic
            a.normaliseRows();
        }

        // print(a, "normalised");

        double residual = 1.;
        int    i        = 0;

        // main iteration
        while (residual > maxResidual) {
            i++;
            a = expand(a);

            // print(a, "iteration " + i + ", after expansion");
            residual = inflate(a, pGamma, maxZero);

            // print(a, "iteration " + i + ", after inflation");
            //System.out.println("residual energy = " + residual);

            if (mor != null) {
                mor.showInfo(i, residual);
            }
        }

        return a;
    }

    /**
     * inflate stochastic matrix by Hadamard (elementwise) exponentiation,
     * pruning and normalisation :
     * <p>
     * result = Gamma ( m, p ) = normalise ( prune ( m .^ p ) ).
     * <p>
     * By convention, normalisation is done along rows (SparseMatrix has
     * row-major representation)
     *
     * @param m matrix (mutable)
     * @param p exponent as a double
     * @param zeromax below which elements are pruned from the sparse matrix
     * @return residuum value, m is modified.
     */
    public double inflate(SparseMatrix m, double p, double zeromax) {
        double res = 0.;

        // matlab: m = m .^ p
        m.hadamardPower(p);

        // matlab: m(find(m < threshold)) = 0
        m.prune(zeromax);

        // matlab [for cols]: dinv = diag(1./sum(m)); m = m * dinv; return
        // sum(m)
        SparseVector rowsums = m.normalise(1.);

        // check if done: if the maximum element
        for (int i : rowsums.keySet()) {
            SparseVector row   = m.get(i);
            double       max   = row.max();
            double       sumsq = row.sum(2.);

            res = Math.max(res, max - sumsq);
        }

        return res;
    }

    /**
     * expand stochastic quadratic matrix by sqaring it with itself: result = m 
     * m. Here normalisation is rowwise.
     *
     * @param matrix
     * @return new matrix (pointer != argument)
     */
    public SparseMatrix expand(SparseMatrix m) {
        m = m.times(m);

        return m;
    }

    /**
     * add loops with specific energy, which corresponds to adding loopGain to
     * the diagonal elements.
     *
     * @param a
     * @param loopGain
     */
    private void addLoops(SparseMatrix a, double loopGain) {
        if (loopGain <= 0) {
            return;
        }

        for (int i = 0; i < a.size(); i++) {
            a.add(i, i, loopGain);
        }
    }
}

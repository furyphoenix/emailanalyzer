package neos.algorithm.lofia;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

/**
 * Main class - executing LoFIA (Longest Frequent Itemset mining) algorithm
 * which searches for longest frequent patterns.
 * <p></p>
 * Source code for LoFIA is developed based on
 * Coenen, F. (2003), The LUCS-KDD FP-growth Association Rule Mining Algorithm,
 * <p>
 * "http://www.cxc.liv.ac.uk/~frans/KDD/Software/FPgrowth/fpGrowth.html",
 * <p>
 * Department of Computer Science, The University of Liverpool, UK.
 * @author Le Minh Nghia, NTU-Singapore
 *
 */
public class LoFIAApp {

    /**
     * List of command parameters in args
     * -F[data filename]
     * -S[support threshold/frequency]
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // Create instance of class FPTree
        // String [] params = {"-Frr3204", "-S80"};
        // Create new FPtree with initial longest pattern (expected) = 0
        FPtree newFPtree = new FPtree(args, 0);

        // Read data to be mined from file
        newFPtree.inputDataSet();

        // Reorder and prune input data according to frequency of single
        // attributes
        newFPtree.idInputDataOrdering();
        newFPtree.recastInputDataAndPruneUnsupportedAtts();
        newFPtree.setNumOneItemSets();

        // newFPtree.outputDataArray();

        // Build initial FP-tree
        double time1 = (double) System.currentTimeMillis();

        newFPtree.createFPtree();
        newFPtree.outputDuration(time1, (double) System.currentTimeMillis());
        newFPtree.outputFPtreeStorage();

        // newFPtree.outputFPtree();
        // newFPtree.outputItemPrefixSubtree();

        // Mine longest frequent pattern
        time1 = (double) System.currentTimeMillis();
        newFPtree.startLoFIAMining();
        System.out.println("Longest frequent patterns: ");

        for (int i = 0; i < newFPtree.longestPattern.size(); i++) {
            System.out.println(newFPtree.longestPattern.elementAt(i).toString());
        }

        newFPtree.outputDuration(time1, (double) System.currentTimeMillis());
    }
}

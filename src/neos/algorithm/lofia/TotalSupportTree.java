package neos.algorithm.lofia;

/* ------------------------------------------------------------------------- */
/*                                                                           */
/*                                                                           */
/*                                Frans Coenen                               */
/*                                                                           */
/*                               10 January 2003                             */
/*  (Revised 23/1/3003, 8/2/2003, 18/3/2003, 3/3/2003, 7/4/2004, 19/1/2005,  */
/*                                3/2/2006)                                  */
/*                                                                           */
/*                       Department of Computer Science                      */
/*                         The University of Liverpool                       */
/*                                                                           */
/* ------------------------------------------------------------------------- */

/* Structure:

AssocRuleMining
      |
      +-- TotalSupportTree       */

/* Java packages */

/**
 * Methods concerned with the generation, processing and manipulation of
 * T-tree data storage structures used to hold the total support counts
 * for large itemsets.
 * <p></p>
 * Source code for LoFIA is developed based on
 * Coenen, F. (2003), The LUCS-KDD FP-growth Association Rule Mining Algorithm,
 * <p>
 * "http://www.cxc.liv.ac.uk/~frans/KDD/Software/FPgrowth/fpGrowth.html",
 * <p>
 * Department of Computer Science, The University of Liverpool, UK.
 * @author Le Minh Nghia, NTU-Singapore
 */
public class TotalSupportTree extends AssocRuleMining {

    /** Time to generate T-tree. */
    protected String duration = null;

    // Diagnostics

    /**
     * The number of frequent sets (nodes in t-tree with above minimum
     * support) generated so far. 
     */
    protected int numFrequentsets = 0;

    /** The number of updates required to generate the T-tree. */
    protected long numUpdates = 0l;

    // Data structures

    /** The reference to start of t-tree. */
    protected TtreeNode[] startTtreeRef;

    /** Processes command line arguments. */
    public TotalSupportTree(String[] args) {
        super(args);
    }

    /**
     * Commences process of adding an itemset (with its support value)
     * to a T-tree when using a T-tree either as a storage mechanism,
     * or when adding to an existing T-tree.
     * @param itemSet The given itemset. Listed in numeric order
     * (not reverse numeric order!).
     * @param support The support value associated with the given itemset.
     */
    public void addToTtree(short[] itemSet, int support) {

        // Determine index of last elemnt in itemSet.
        int endIndex = itemSet.length - 1;

        // Add itemSet to T-tree.
        startTtreeRef = addToTtree(startTtreeRef, numOneItemSets + 1, endIndex, itemSet, support);
    }

    /**
     * Inserts a node into a T-tree.
     * <P> Recursive procedure.
     * @param linkRef The reference to the current array in Ttree.
     * @param size the size of the current array in T-tree.
     * @param endIndex the index of the last element/attribute in
     * the itemset, which is also used as a level counter.
     * @param itemSet the given itemset.
     * @param support the support value associated with the given itemset.
     * @return the reference to the revised sub-branch of t-tree. 
     */
    protected TtreeNode[] addToTtree(TtreeNode[] linkRef, int size, int endIndex, short[] itemSet, int support) {

        // If no array describing current level in the T-tree or T-tree
        // sub-branch create one with "null" nodes.
        if (linkRef == null) {
            linkRef = new TtreeNode[size];

            for (int index = 1; index < linkRef.length; index++) {
                linkRef[index] = null;
            }
        }

        // If null node at index of array describing current level in T-tree
        // (T-tree sub-branch) create a T-tree node describing the current
        // itemset sofar.
        int currentAttribute = itemSet[endIndex];

        if (linkRef[currentAttribute] == null) {
            linkRef[currentAttribute] = new TtreeNode();
        }

        // If at right level add support
        if (endIndex == 0) {
            linkRef[currentAttribute].support = linkRef[currentAttribute].support + support;

            return (linkRef);
        }

        // Otherwise proceed down branch and return
        linkRef[currentAttribute].childRef = addToTtree(linkRef[currentAttribute].childRef, currentAttribute,
                endIndex - 1, itemSet, support);

        // Return
        return (linkRef);
    }

    /**
     * Commences process for finding the support value for the given
     * item set in the T-tree (which is know to exist in the T-tree).
     * <P> Used when generating Association Rules (ARs). Note that
     * itemsets are stored in reverse order in the T-tree therefore
     * the given itemset must be processed in reverse.
     * @param itemSet the given itemset.
     * @return returns the support value (0 if not found). 
     */
    protected int getSupportForItemSetInTtree(short[] itemSet) {
        int endInd = itemSet.length - 1;

        // Last element of itemset in Ttree (Note: Ttree itemsets stored in
        // reverse)
        if (startTtreeRef[itemSet[endInd]] != null) {

            // If "current index" is 0, then this is the last element (i.e the
            // input is a 1 itemset)  and therefore item set found
            if (endInd == 0) {
                return (startTtreeRef[itemSet[0]].support);

                // Otherwise continue down branch
            } else {
                TtreeNode[] tempRef = startTtreeRef[itemSet[endInd]].childRef;

                if (tempRef != null) {
                    return (getSupForIsetInTtree2(itemSet, endInd - 1, tempRef));

                    // No further branch therefore rerurn 0
                } else {
                    return (0);
                }
            }
        }

        // Item set not in Ttree thererfore return 0
        else {
            return (0);
        }
    }

    /**
     * Returns the support value for the given itemset if
     * found in the T-tree and 0 otherwise.
     * <P> Operates recursively.
     * @param itemSet the given itemset.
     * @param index the current index in the given itemset.
     * @param linRef the reference to the current T-tree level.
     * @return returns the support value (0 if not found). 
     */
    private int getSupForIsetInTtree2(short[] itemSet, int index, TtreeNode[] linkRef) {

        // Element at "index" in item set exists in Ttree
        if (linkRef[itemSet[index]] != null) {

            // If "current index" is 0, then this is the last element of the
            // item set and therefore item set found
            if (index == 0) {
                return (linkRef[itemSet[0]].support);

                // Otherwise continue provided there is a child branch to follow
            } else if (linkRef[itemSet[index]].childRef != null) {
                return (getSupForIsetInTtree2(itemSet, index - 1, linkRef[itemSet[index]].childRef));
            } else {
                return (0);
            }
        }

        // Item set not in Ttree therefore return 0
        else {
            return (0);
        }
    }

    /* ----------------------------------------------------------------------- */
    /*  */
    /* ASSOCIATION RULE (AR) GENERATION */
    /*  */
    /* ----------------------------------------------------------------------- */
    /* GENERATE ASSOCIATION RULES */

    /**
     * Initiates process of generating Association Rules (ARs) from a
     * T-tree. 
     */
    public void generateARs() {

        // Command line interface output
        System.out.println("GENERATE ARs:\n-------------");

        // Set rule data structure to null
        startRulelist = null;

        // Generate
        generateARs2();
    }

    /**
     * Loops through top level of T-tree as part of the AR generation
     * process. 
     */
    protected void generateARs2() {

        // Loop
        for (int index = 1; index <= numOneItemSets; index++) {
            if (startTtreeRef[index] != null) {
                if (startTtreeRef[index].support >= minSupport) {
                    short[] itemSetSoFar = new short[1];

                    itemSetSoFar[0] = (short) index;
                    generateARs(itemSetSoFar, index, startTtreeRef[index].childRef);
                }
            }
        }
    }

    /* GENERATE ASSOCIATION RULES */

    /**
     * Continues process of generating association rules from a T-tree
     * by recursively looping through T-tree level by level.
     * @param itemSetSofar the label for a T-tree node as generated sofar.
     * @param size the length/size of the current array lavel in the T-tree.
     * @param linkRef the reference to the current array level in the T-tree.
     */
    protected void generateARs(short[] itemSetSofar, int size, TtreeNode[] linkRef) {

        // If no more nodes return
        if (linkRef == null) {
            return;
        }

        // Otherwise process
        for (int index = 1; index < size; index++) {
            if (linkRef[index] != null) {
                if (linkRef[index].support >= minSupport) {

                    // Temp itemset
                    short[] tempItemSet = realloc2(itemSetSofar, (short) index);

                    // Generate ARs for current large itemset
                    generateARsFromItemset(tempItemSet, linkRef[index].support);

                    // Continue generation process
                    generateARs(tempItemSet, index, linkRef[index].childRef);
                }
            }
        }
    }

    /* GENERATE ASSOCIATION RULES */

    /**
     * Generates all association rules for a given large item set
     * found in a T-tree structure.
     * <P> Called from <TT>generateARs</TT> method.
     * @param itemSet the given large itemset.
     * @param support the associated support value for the
     * given large itemset.
     */
    private void generateARsFromItemset(short[] itemSet, double support) {

        // Determine combinations
        short[][] combinations = combinations(itemSet);

        // Loop through combinations
        for (int index = 0; index < combinations.length; index++) {

            // Find complement of combination in given itemSet
            short[] complement = complement(combinations[index], itemSet);

            // If complement is not empty generate rule
            if (complement != null) {
                double confidenceForAR = getConfidence(combinations[index], support);

                if (confidenceForAR >= confidence) {
                    insertRuleintoRulelist(combinations[index], complement, confidenceForAR);
                }
            }
        }
    }

    /* ----------------------------------------------------------------------- */
    /*  */
    /* GET METHODS */
    /*  */
    /* ----------------------------------------------------------------------- */
    /* GET CONFIDENCE */

    /**
     * Calculates and returns the confidence for an AR given the
     * antecedent item set and the support for the total item set.
     * @param antecedent the antecedent (LHS) of the AR.
     * @param support the support for the large itemset from which
     * the AR is generated.
     * @return the associated confidence value (as a precentage)
     * correct to two decimal places.
     */
    protected double getConfidence(short[] antecedent, double support) {

        // Get support for antecedent
        double supportForAntecedent = (double) getSupportForItemSetInTtree(antecedent);

        // Return confidence
        double confidenceForAR = ((double) support / supportForAntecedent) * 10000;
        int    tempConf        = (int) confidenceForAR;

        confidenceForAR = (double) tempConf / 100;

        return (confidenceForAR);
    }

    /* ----------------------------------------------------------------------- */
    /*  */
    /* UTILITY METHODS */
    /*  */
    /* ----------------------------------------------------------------------- */
    /* SET NUMBER ONE ITEM SETS */

    /**
     * Sets the number of one item sets field
     * (<TT>numOneItemSets</TT> to the number of supported one item sets.
     */
    public void setNumOneItemSets() {
        numOneItemSets = getNumSupOneItemSets();
    }

    /* ----------------------------------------------------------------------- */
    /*  */
    /* OUTPUT METHODS */
    /*  */
    /* ----------------------------------------------------------------------- */
    /* ---------------- */
    /* OUTPUT T-TRRE */
    /* ---------------- */

    /**
     * Commences process of outputting T-tree structure contents
     * to screen. 
     */
    public void outputTtree() {
        int number = 1;

        // Loop
        if (startTtreeRef == null) {
            System.out.println("No tree is built...");

            return;
        }

        for (short index = 1; index < startTtreeRef.length; index++) {
            if (startTtreeRef[index] != null) {
                String itemSetSofar = new Short(reconvertItem(index)).toString();

                System.out.print("[" + number + "] {" + itemSetSofar);
                System.out.println("} = " + startTtreeRef[index].support);
                outputTtree(new Integer(number).toString(), itemSetSofar, startTtreeRef[index].childRef);
                number++;
            }
        }
    }

    /**
     * Continue process of outputting T-tree. .
     * <P> Operates in a recursive manner.
     * @param number the ID number of a particular node.
     * @param itemSetSofar the label for a T-tree node as generated so far.
     * @param linkRef the reference to the current array level in the
     * T-tree.
     */
    private void outputTtree(String number, String itemSetSofar, TtreeNode[] linkRef) {

        // Set output local variables.
        int num = 1;

        number       = number + ".";
        itemSetSofar = itemSetSofar + " ";

        // Check for empty branch/sub-branch.
        if (linkRef == null) {
            return;
        }

        // Loop through current level of branch/sub-branch.
        for (short index = 1; index < linkRef.length; index++) {
            if (linkRef[index] != null) {
                String newItemSet = itemSetSofar + (reconvertItem(index));

                System.out.print("[" + number + num + "] {" + newItemSet);
                System.out.println("} = " + linkRef[index].support);
                outputTtree(number + num, newItemSet, linkRef[index].childRef);
                num++;
            }
        }
    }

    /* ----------------------- */
    /* OUTPUT FREQUENT SETS */
    /* ----------------------- */

    /**
     * Commences the process of outputting the frequent sets
     * contained in the T-tree. 
     */
    public void outputFrequentSets() {
        int number = 1;

        System.out.println("FREQUENT (LARGE) ITEM SETS:\n" + "---------------------------");
        System.out.println("Format: [N] {I} = S, where N is a sequential "
                           + "number, I is the item set and S the support.");

        // Loop
        for (short index = 1; index <= numOneItemSets; index++) {
            if (startTtreeRef[index] != null) {
                if (startTtreeRef[index].support >= minSupport) {
                    String itemSetSofar = new Short(reconvertItem(index)).toString();

                    System.out.println("[" + number + "] {" + itemSetSofar + "} = " + startTtreeRef[index].support);
                    number = outputFrequentSets(number + 1, itemSetSofar, index, startTtreeRef[index].childRef);
                }
            }
        }

        // End
        System.out.println("\n");
    }

    /**
     * Outputs T-tree frequent sets. <P> Operates in a recursive manner.
     * @param number the number of frequent sets so far.
     * @param itemSetSofar the label for a T-treenode as generated sofar.
     * @param size the length/size of the current array level in the T-tree.
     * @param linkRef the reference to the current array level in the T-tree.
     * @return the incremented (possibly) number the number of frequent
     * sets so far. 
     */
    private int outputFrequentSets(int number, String itemSetSofar, int size, TtreeNode[] linkRef) {

        // No more nodes
        if (linkRef == null) {
            return (number);
        }

        // Otherwise process
        itemSetSofar = itemSetSofar + " ";

        for (short index = 1; index < size; index++) {
            if (linkRef[index] != null) {
                if (linkRef[index].support >= minSupport) {
                    String newItemSet = itemSetSofar + (reconvertItem(index));

                    System.out.println("[" + number + "] {" + newItemSet + "} = " + linkRef[index].support);
                    number = outputFrequentSets(number + 1, newItemSet, index, linkRef[index].childRef);
                }
            }
        }

        // Return
        return (number);
    }

    /* ------------------------------ */
    /* OUTPUT NUMBER FREQUENT SETS */
    /* ------------------------------ */

    /**
     * Commences the process of counting and outputting number
     * of supported nodes in the T-tree.<P> A supported set is
     * assumed to be a non null node in the T-tree. 
     */
    public void outputNumFreqSets() {

        // If empty tree (i.e. no supported sets) do nothing
        if (startTtreeRef == null) {
            System.out.println("Number of frequent " + "sets = 0");

            // Otherwise count and output
        } else {
            System.out.println("Number of frequent sets = " + countNumFreqSets());
        }
    }

    /* COUNT NUMBER OF FRQUENT SETS */

    /**
     * Commences process of counting the number of frequent
     * (large/supported sets contained in the T-tree.
     */
    protected int countNumFreqSets() {

        // If empty tree return 0
        if (startTtreeRef == null) {
            return (0);
        }

        // Otherwise loop through T-tree starting with top level
        int num = 0;

        for (int index = 1; index <= numOneItemSets; index++) {

            // Check for null valued top level Ttree node.
            if (startTtreeRef[index] != null) {
                if (startTtreeRef[index].support >= minSupport) {
                    num = countNumFreqSets(index, startTtreeRef[index].childRef, num + 1);
                }
            }
        }

        // Return
        return (num);
    }

    /**
     * Counts the number of supported nodes in a sub branch of the T-tree.
     * @param size the length/size of the current array level in the T-tree.
     * @param linkRef the reference to the current array level in the T-tree.
     * @param num the number of frequent sets sofar. 
     */
    protected int countNumFreqSets(int size, TtreeNode[] linkRef, int num) {
        if (linkRef == null) {
            return (num);
        }

        for (int index = 1; index < size; index++) {
            if (linkRef[index] != null) {
                if (linkRef[index].support >= minSupport) {
                    num = countNumFreqSets(index, linkRef[index].childRef, num + 1);
                }
            }
        }

        // Return
        return (num);
    }

    /* --------------------------- */
    /* OUTPUT T-TREE STATISTICS */
    /* --------------------------- */

    /**
     * Commences the process of outputting T-tree statistics
     * (for diagnostic purposes): (a) Storage,
     * (b) Number of nodes on P-tree, (c) number of partial support
     * increments (updates) and (d) generation time.
     */
    public void outputTtreeStats() {
        System.out.println("T-TREE STATISTICS\n-----------------");
        System.out.println(calculateStorage() + " (Bytes) storage");
        System.out.println(TtreeNode.getNumberOfNodes() + " nodes");
        System.out.println(countNumFreqSets() + " frequent sets");
        System.out.println(numUpdates + " support value increments");
        System.out.println(duration);
    }

    /* --------------------------- */
    /* OUTPUT NUMBER OF UPDATES */
    /* --------------------------- */

    /*
     *  Commences the process of determining and outputting the
     * storage requirements (in bytes) for the T-tree
     */

    /**
     * Outputs the number of update and number of nodes created
     * during the generation of the T-tree (the later is not the
     * same as the number of supported nodes).
     */
    public void outputNumUpdates() {
        System.out.println("Number of Nodes created = " + TtreeNode.getNumberOfNodes());
        System.out.println("Number of Updates       = " + numUpdates);
    }

    /* ----------------- */
    /* OUTPUT STORAGE */
    /* ----------------- */

    /**
     * Commences the process of determining and outputting the storage
     * requirements (in bytes) for the T-tree. <P> Example: Given ---
     * <PRE>
     *           {1,2,3}
     *           {1,2,3}
     *                   {1,2,3}
     *           {1,2,3}
     *                   {1,2,3}
     * </PRE>
     * This will produce a T-tree as shown below:
     * <PRE>
     * +---+---+---+---+
     * | 0 | 1 | 2 | 3 |
     * +---+---+---+---+
     *     |   |   |
     *     |   |   +-----------+
     *     |   |               |
     *     |   +---+         +---+---+---+
     *     |       |         | 0 | 1 | 2 |
     *   ( 5 )   +---+---+   +---+---+---+
     *   (nul)   | 0 | 1 |         |   |
     *           +---+---+         |   +----+
     *                 |           |        |
     *                 |           |      +---+---+
     *               ( 5 )         |      | 0 + 1 |
     *               (nul)       ( 5 )    +---+---+
     *                           (nul)          |
     *                                          |
     *                                        ( 5 )
     *                                        (nul)
     * </PRE>
     * 0 elements require 4 bytes of storage, null nodes (not shown above) 4 bytes
     * of storage, others 12 bytes of storage.
     */
    public void outputStorage() {

        // If empty tree (i.e. no supported sets) do nothing
        if (startTtreeRef == null) {
            return;
        }

        /* Otherwise calculate storage */
        System.out.println("T-tree Storage          = " + calculateStorage() + " (Bytes)");
    }

    /* CALCULATE STORAGE */

    /**
     * Commences process of calculating storage requirements
     * for T-tree. 
     */
    protected int calculateStorage() {

        // If emtpy tree (i.e. no supported sets) return 0
        if (startTtreeRef == null) {
            return (0);
        }

        /* Step through top level */
        int storage = 4;    // For element 0

        for (int index = 1; index <= numOneItemSets; index++) {
            if (startTtreeRef[index] != null) {
                storage = storage + 12 + calculateStorage(0, startTtreeRef[index].childRef);
            } else {
                storage = storage + 4;
            }
        }

        // Return
        return (storage);
    }

    /**
     * Calculate storage requirements for a sub-branch of the T-tree.
     * @param localStorage the storage as calculated sofar
     * (set to 0 at start).
     * @param linkRef the reference to the current sub-branch of the T-tree.
     */
    private int calculateStorage(int localStorage, TtreeNode[] linkRef) {
        if (linkRef == null) {
            return (0);
        }

        for (int index = 1; index < linkRef.length; index++) {
            if (linkRef[index] != null) {
                localStorage = localStorage + 12 + calculateStorage(0, linkRef[index].childRef);
            } else {
                localStorage = localStorage + 4;
            }
        }

        /* Return */
        return (localStorage + 4);    // For element 0
    }
}

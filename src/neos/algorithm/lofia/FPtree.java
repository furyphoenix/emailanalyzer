package neos.algorithm.lofia;

//~--- JDK imports ------------------------------------------------------------

import java.util.Vector;

/**
 * Implementation of FP-tree structure
 * <p></p>
 * Source code for LoFIA is developed based on
 * Coenen, F. (2003), The LUCS-KDD FP-growth Association Rule Mining Algorithm,
 * <p>
 * "http://www.cxc.liv.ac.uk/~frans/KDD/Software/FPgrowth/fpGrowth.html",
 * <p>
 * Department of Computer Science, The University of Liverpool, UK.
 * @author Le Minh Nghia, NTU-Singapore
 */
public class FPtree extends TotalSupportTree {

    /**
     * Start reference for supportedSets linked list (temporary storage
     * only).
     */
    private static FPgrowthSupportedSets startTempSets = null;

    /**
     * List of longest frequent pattern found so far
     */
    public Vector<FPItemSet> longestPattern = new Vector<FPItemSet>();

    /**
     * Current longest length of frequent patterns
     */
    public int maxOrder = 0;

    /** Start reference for FP-tree. */
    protected FPtreeNode rootNode = null;

    /**
     * Temporary storage for an index into an array of FP-tree nodes. </P>
     * Used when reassigning child reference arrays. 
     */
    private int tempIndex = 0;

    /** Start reference for header table. */
    protected FPgrowthHeaderTable[] headerTable;

    /** Number of nodes created. */
    private int numberOfNodes;

    /**
     * Constructor to process command line argument.
     * @param args the command line arguments. 
     */
    public FPtree(String[] args) {
        super(args);

        // Initialize root node
        rootNode = new FPtreeNode();

        // Create header table
        headerTable = new FPgrowthHeaderTable[numOneItemSets + 1];

        // Populate header table
        for (int index = 1; index < headerTable.length; index++) {
            headerTable[index] = new FPgrowthHeaderTable((short) index);
        }
    }

    /**
     * Constructor
     * @param args Command line arguments
     * @param maxLength Expected length of longest frequent patterns
     */
    public FPtree(String[] args, int maxLength) {
        super(args);

        // Initialize root node
        rootNode = new FPtreeNode();

        // Create header table
        headerTable = new FPgrowthHeaderTable[numOneItemSets + 1];

        // Populate header table
        for (int index = 1; index < headerTable.length; index++) {
            headerTable[index] = new FPgrowthHeaderTable((short) index);
        }

        this.maxOrder = maxLength;
    }

    /** Top level method to commence the construction of the FP-Tree. */
    public void createFPtree() {

        // System.out.println("GENERATING FP-TREE\n------------------");
        // Create header table
        headerTable = new FPgrowthHeaderTable[numOneItemSets + 1];

        // Populate header table
        for (int index = 1; index < headerTable.length; index++) {
            headerTable[index] = new FPgrowthHeaderTable((short) index);
        }

        // Process datatable, loop through data table (stored in data array)
        // For each entry add the entry to the FP-tree.
        for (int index = 0; index < dataArray.length; index++) {

            // Non null record (if initial data set has been reordered and
            // pruned some records may be empty
            if (dataArray[index] != null) {
                addToFPtree(rootNode, 0, dataArray[index], 1, headerTable);
            }
        }
    }

    /**
     * Searches through current list of child refs looking
     * for given item set.
     * <P> If reference for current itemset found increments support
     * count and proceed down branch, otherwise adds to current level.
     * @param ref the current location in the FP-tree
     * (<TT>rootNode</TT> at start).
     * @param place the current index in the given itemset.
     * @param itemSet the given itemset.
     * @param support the associated support value for the given itemset.
     * @param headerRef the link to the appropriate place in the header table. 
     */
    private void addToFPtree(FPtreeNode ref, int place, short[] itemSet, int support, FPgrowthHeaderTable[] headerRef) {
        if (place < itemSet.length) {
            if (!addToFPtree1(ref, place, itemSet, support, headerRef)) {
                addToFPtree2(ref, place, itemSet, support, headerRef);
            }
        }
    }

    /**
     * Searches through existing branch and if itemset
     * found updates the support count and returns true,
     * otherwise return false.
     * @param ref the current FP-tree node reference.
     * @param place the current index in the given itemset.
     * @param itemSet the given itemset.
     * @param support the associated support value for the given itemset.
     * @param headerRef the link to the appropriate place in the header table.
     * @return true if given itemset exists in FP-tree, and false otherwise. 
     */
    private boolean addToFPtree1(FPtreeNode ref, int place, short[] itemSet, int support,
                                 FPgrowthHeaderTable[] headerRef) {

        // Loop
        if (ref.childRefs != null) {
            for (int index = 0; index < ref.childRefs.length; index++) {

                // If item is already in list of child refs
                // increment count and proceed down branch.
                if (itemSet[place] == ref.childRefs[index].node.itemName) {
                    ref.childRefs[index].node.itemCount = ref.childRefs[index].node.itemCount + support;
                    numUpdates++;
                    addToFPtree(ref.childRefs[index], place + 1, itemSet, support, headerRef);

                    return (true);
                }

                // Child refs ordered lexicographically so break when passed
                // point where item should be
                if (itemSet[place] < ref.childRefs[index].node.itemName) {
                    return (false);
                }
            }
        }

        // Default
        return (false);
    }

    /**
     * Adds new node to FP-tree.
     * <P> Adds first attribute in itemSet and then rest of sequence.
     * @param ref the current FP-tree node reference.
     * @param place the current index in the given itemset.
     * @param itemSet the given itemset.
     * @param support the associated support value for the given itemset.
     * @param headerRef the link to the appropriate place in the header table.
     */
    private void addToFPtree2(FPtreeNode ref, int place, short[] itemSet, int support,
                              FPgrowthHeaderTable[] headerRef) {

        // Create new Item Prefix Subtree Node
        FPgrowthItemPrefixSubtreeNode newPrefixNode = new FPgrowthItemPrefixSubtreeNode(itemSet[place], support,
                                                          ref.node);

        // Create new FP tree node incorporating new Item Prefix Subtree Node
        FPtreeNode newFPtreeNode = new FPtreeNode(newPrefixNode);

        // Add link from header table
        addRefToFPgrowthHeaderTable(itemSet[place], newPrefixNode, headerRef);

        // Add into FP tree
        ref.childRefs = reallocFPtreeChildRefs(ref.childRefs, newFPtreeNode);

        // Proceed down branch with rest of itemSet
        addRestOfitemSet(ref.childRefs[tempIndex], newPrefixNode, place + 1, itemSet, support, headerRef);
    }

    /**
     * Continues adding attributes in current itemset to FP-tree.
     * @param ref the current FP-tree node reference.
     * @param backRef the backwards link to the previous node.
     * @param place the current index in the given itemset.
     * @param itemSet the given itemset.
     * @param support the associated support value for the given itemset.
     * @param headerRef the link to the appropriate place in the header table.
     */
    private void addRestOfitemSet(FPtreeNode ref, FPgrowthItemPrefixSubtreeNode backRef, int place, short[] itemSet,
                                  int support, FPgrowthHeaderTable[] headerRef) {

        // Process if more items in item set.
        if (place < itemSet.length) {

            // Create new Item Prefix Subtree Node
            FPgrowthItemPrefixSubtreeNode newPrefixNode = new FPgrowthItemPrefixSubtreeNode(itemSet[place], support,
                                                              backRef);

            // Create new FP tree node incorporating new Item Prefix Subtree
            // Node
            FPtreeNode newFPtreeNode = new FPtreeNode(newPrefixNode);

            // Add link from header table
            addRefToFPgrowthHeaderTable(itemSet[place], newPrefixNode, headerRef);
            ref.childRefs = reallocFPtreeChildRefs(ref.childRefs, newFPtreeNode);

            // Add into FP tree
            addRestOfitemSet(ref.childRefs[tempIndex], newPrefixNode, place + 1, itemSet, support, headerRef);
        }
    }

    /**
     * Adds reference to new FP-tree node to header table moving old reference
     * so that it becomes a link from the new FP-tree node.
     * @param columnNumber the given attribute.
     * @param newNode the newly created FP-tree node.
     * @param headerRef the reference to the header table (array).
     */
    private void addRefToFPgrowthHeaderTable(short columnNumber, FPgrowthItemPrefixSubtreeNode newNode,
            FPgrowthHeaderTable[] headerRef) {
        FPgrowthItemPrefixSubtreeNode tempRef;

        // Loop through header table
        for (int index = 1; index < headerRef.length; index++) {

            // Found right attribute in table?
            if (columnNumber == headerRef[index].itemName) {
                tempRef                   = headerRef[index].nodeLink;
                headerRef[index].nodeLink = newNode;
                newNode.nodeLink          = tempRef;

                break;
            }
        }
    }

    /* ---------------------------------------------------------- */
    /*  */
    /* FP-TREE MINING */
    /*  */
    /* ---------------------------------------------------------- */

    /*
     *  Methodology:
     *
     * 1) Step through header table from end to start (least common single
     * attribute to most common single attribute). For each item.
     * a) Count support by following node links and add to linked list of
     *  supported sets.
     * b) Determine the "ancestor trails" connected to the nodes linked to the
     *  current item in the header table.
     * c) Treat the list of ancestor itemSets as a new set of input data and
     *  create a new header table based on the accumulated supported counts of
     *  the single items in the ancestor itemSets
     * d) Prune the ancestor itemSets so as to remove unsupported items.
     * e) Repeat (1) with local header table and list of pruned ancestor itemSets
     *  as input
     */

    /* START MINING */

    /**
     * Top level "LoFIA method" - use to generate longest
     * frequent patterns
     */
    public void startLoFIAMining() {
        System.out.println("LoFIA Mining ");
        startMining(headerTable, null);
        System.out.println("Finishing mining T-tree");
    }

    /**
     * LoFIA algorithm - Commences process of mining the FP tree.
     * <P> Commence with the bottom of the header table and work upwards.
     * Working upwards from the bottom of the header table if there is a link to an FP tree node :
     * <OL>
     * <LI> Count the support.
     * <LI> Build up itemSet sofar.
     * <LI> Add to supported sets.
     * <LI> Build a new FP tree: (i) create a new local root,
     * (ii) create a new local header table and (iii) populate with ancestors.
     * <LI> If new local FP tree is not empty repeat mining operation.
     * </OL>
     *  Otherwise end.
     *  @param tableRef the reference to the current location in the header table
     *  (commencing with the last item).
     *  @param itemSetSofar the label fot the current item sets as generated to
     *  date (null at start).
     */
    private void startMining(FPgrowthHeaderTable[] tableRef, short[] itemSetSofar) {
        int                    headerTableEnd   = tableRef.length - 1;
        FPgrowthColumnCounts[] countArray       = null;
        FPgrowthHeaderTable[]  localHeaderTable = null;
        FPtreeNode             localRoot;
        int                    support;
        short[]                newCodeSofar;

        // Loop through header table from end to start, item by item
        for (int index = headerTableEnd; index >= 1; index--) {

            // Check for null link
            if (tableRef[index].nodeLink != null) {

                // Pruning Criteria 1
                boolean process = true;

                if (itemSetSofar != null) {
                    if (index < this.maxOrder - itemSetSofar.length) {
                        process = false;
                    }
                }

                if (process) {

                    // process trail of links from header table element
                    startMining(tableRef[index].nodeLink, tableRef[index].itemName, itemSetSofar);
                }
            }
        }
    }

    /**
     * LoFIA algorithm - Commence process of mining FP tree with respect to a
     * single element in the header table.
     * @param nodeLink the firsty link from the header
     * table pointing to an FP-tree node.
     * @param itemName the label associated with the element
     * of interest in the header table.
     * @param itemSetSofar the item set represented by the current FP-tree.
     */
    protected void startMining(FPgrowthItemPrefixSubtreeNode nodeLink, short itemName, short[] itemSetSofar) {

        // Count support for current item in header table and store a
        // T-tree data structure
        int     support      = genSupHeadTabItem(nodeLink);
        short[] newCodeSofar = realloc2(itemSetSofar, itemName);

        /* Pruning Criteria 2 - ONLY RECORD LONGEST PATTERN */
        if (newCodeSofar.length > this.maxOrder) {
            System.out.println("Number current found items: " + longestPattern.size());
            this.longestPattern.clear();
            this.maxOrder = newCodeSofar.length;
            System.out.println("- Processing at order: " + newCodeSofar.length);
        }

        if (newCodeSofar.length == this.maxOrder) {
            short[] temp = new short[newCodeSofar.length];

            for (int i = 0; i < temp.length; i++) {
                temp[i] = reconversionArray[newCodeSofar[i]];
            }

            FPItemSet item = new FPItemSet(temp, support);

            longestPattern.addElement(item);
        }

        // Collect ancestor itemSets and store in linked list structure
        startTempSets = null;
        generateAncestorCodes(nodeLink);

        // Process Ancestor itemSets
        if (startTempSets != null) {

            // Count singles in linked list
            FPgrowthColumnCounts[] countArray = countFPgrowthSingles();

            // Create and populate local header table
            FPgrowthHeaderTable[] localHeaderTable = createLocalHeaderTable(countArray);

            if (localHeaderTable != null) {

                // Prune ancestor itemSets
                pruneAncestorCodes(countArray);

                // Create new local root for local FP tree
                FPtreeNode localRoot = generateLocalFPtree(localHeaderTable);
                int        depthTree = depthFPtree(localRoot);

                /*
                 *  Code to print conditional-tree
                 * System.out.print("Code: ");
                 * for (int i = 0; i < newCodeSofar.length; i++)
                 *   System.out.print(newCodeSofar[i] + " ");
                 * System.out.println("Depth of tree: " + depthTree);
                 * outputFPtreeNode(localRoot);
                 */

                // Mine new FP tree

                /*
                 * System.out.println("Local Header Table: " +
                 *           localHeaderTable.length +
                 *           ", New Code: " + newCodeSofar.length);
                 */

                /*
                 *  Only proceed on branches with expected length
                 * exceeding the current longest pattern found
                 */
                if (depthTree + newCodeSofar.length >= maxOrder) {

                    // System.out.println("Do the mining...");
                    startMining(localHeaderTable, newCodeSofar);
                }
            }
        }
    }

    /**
     * Get depth of the tree (from root to leaves)
     * @param node
     * @return
     */
    private int depthFPtree(FPtreeNode node) {
        return depthFPtree2(node.childRefs, 0);
    }

    private int depthFPtree2(FPtreeNode ref[], int level) {
        if (ref == null) {
            return level;
        }

        int res = -1;

        for (int index = 0; index < ref.length; index++) {
            int temp = depthFPtree2(ref[index].childRefs, level + 1);

            if (temp > res) {
                res = temp;
            }
        }

        return res;
    }

    /**
     * Counts support for single attributes in header table by following
     * node links.
     * @param nodeLink the start link from the header table.
     * @return the support value for the item set indicated
     * by the header table.
     */
    private int genSupHeadTabItem(FPgrowthItemPrefixSubtreeNode nodeLink) {
        int counter = 0;

        // Loop
        while (nodeLink != null) {
            counter = counter + nodeLink.itemCount;
            numUpdates++;
            nodeLink = nodeLink.nodeLink;
        }

        // Return
        return (counter);
    }

    /**
     * Generates ancestor itemSets are made up of the parent nodes of a given
     * node. This method collects such itemSets and stores them in a linked list
     * pointed at by startTempSets.
     * @param ref the reference to the current node in the prefix tree containing
     * itemsets together with support values.
     */
    private void generateAncestorCodes(FPgrowthItemPrefixSubtreeNode ref) {
        short[] ancestorCode = null;
        int     support;

        // Loop
        while (ref != null) {
            support      = ref.itemCount;
            ancestorCode = getAncestorCode(ref.parentRef);

            // Add to linked list with current support
            if (ancestorCode != null) {
                startTempSets = new FPgrowthSupportedSets(ancestorCode, support, startTempSets);
            }

            // Next ref
            ref = ref.nodeLink;
        }
    }

    /**
     * Generate the ancestor itemSet from a given node.
     * @param ref the reference to the current node in the
     * prefix tree containing itemsets together with support values.
     */
    private short[] getAncestorCode(FPgrowthItemPrefixSubtreeNode ref) {
        short[] itemSet = null;

        if (ref == null) {
            return (null);
        }

        // Else process
        while (ref != null) {
            itemSet = realloc2(itemSet, ref.itemName);
            ref     = ref.parentRef;
        }

        // Return
        return (itemSet);
    }

    /**
     * Removes elements in ancestor itemSets (pointed at by
     * <TT>startTempSets</TT>) which are not supported by referring to count
     * array (which contains all the current supported 1 itemsets).
     * @param countArray the array of <TT>FPgrowthColumnCounts</TT> structures
     * describing the single item sets (in terms of labels and associated
     * support), contained in a linked list of <TT>FPgrowthSupportedSets</TT>
     * which in turn describe the ancestor nodes in an FP-tree that preceed the
     * nodes identified by following a trail of links from a particular item in
     * the header table.
     */
    private void pruneAncestorCodes(FPgrowthColumnCounts[] countArray) {
        FPgrowthSupportedSets ref = startTempSets;

        // Loop through linked list of ancestor paths

        while (ref != null) {
            for (int index = 0; index < ref.itemSet.length; index++) {
                if (countArray[ref.itemSet[index]].support < minSupport) {
                    ref.itemSet = removeElementN(ref.itemSet, index);
                }
            }

            ref = ref.nodeLink;
        }
    }

    /**
     * Counts frequent 1 item sets in ancestor itemSets linked list and place
     * into an array.
     * @return array of <TT>FPgrowthColumnCounts</TT> structures describing the
     * single item sets (in terms of labels and associated support), contained in
     * a linked list of <TT>FPgrowthSupportedSets</TT> which in turn describe the
     * ancestor nodes in an FP-tree that preceed the nodes identified by following
     * a trail of links from a particular item in the header table.
     */
    private FPgrowthColumnCounts[] countFPgrowthSingles() {
        int                   index,
                              place    = 0;
        FPgrowthSupportedSets nodeLink = startTempSets;    // Start of linked list

        // Dimension array, assume all attributes present, then it will
        // be possible to index in to the array.
        FPgrowthColumnCounts[] countArray = new FPgrowthColumnCounts[numOneItemSets + 1];

        // Initialise array
        for (index = 1; index < numOneItemSets + 1; index++) {
            countArray[index] = new FPgrowthColumnCounts(index);
        }

        // Loop through linked list of ancestor itemSets
        while (nodeLink != null) {

            // Loop through itemSet
            for (index = 0; index < nodeLink.itemSet.length; index++) {
                place                     = nodeLink.itemSet[index];
                countArray[place].support = countArray[place].support + nodeLink.support;
                numUpdates++;
            }

            nodeLink = nodeLink.nodeLink;
        }

        // Return
        return (countArray);
    }

    /* CREATE LOCAL HEADER TABLE */

    /**
     * Creates a local header table comprising those item that are supported
     * in the count array.
     * @param countArray the support for the 1 item sets.
     * @return a FPgrowth header table. 
     */
    private FPgrowthHeaderTable[] createLocalHeaderTable(FPgrowthColumnCounts[] countArray) {
        int                   index;
        FPgrowthHeaderTable[] localHeaderTable;

        localHeaderTable = localHeadTabUnordered(countArray);

        // Order according single item support
        // orderLocalHeaderTable(localHeaderTable,countArray);
        // Return
        return (localHeaderTable);
    }

    /**
     * Create a new local header table, but unordered.
     * @param countArray the csupport for the 1 item sets.
     * @return a FPgrpwth header table.
     */
    private FPgrowthHeaderTable[] localHeadTabUnordered(FPgrowthColumnCounts[] countArray) {
        int counter = 1;

        // Loop through array and count supported one item sets
        for (int index = 1; index < countArray.length; index++) {
            if (countArray[index].support >= minSupport) {
                counter++;
            }
        }

        // Build new Header Table array containing only supported items
        if (counter == 1) {
            return (null);
        }

        FPgrowthHeaderTable[] localHeaderTable = new FPgrowthHeaderTable[counter];

        // Populate header table
        int place = 1;

        for (int index = 1; index < countArray.length; index++) {
            if (countArray[index].support >= minSupport) {
                localHeaderTable[place] = new FPgrowthHeaderTable((short) countArray[index].columnNum);
                place++;
            }
        }

        // Return
        return (localHeaderTable);
    }

    /**
     * Orders local header table (currently unused).
     * @param localHeaderTable the FPgrpwth header table to be ordered.
     * @param countArray the support for the 1 item sets. 
     */
    private void orderLocalHeaderTable(FPgrowthHeaderTable[] localHeaderTable, FPgrowthColumnCounts[] countArray) {
        boolean             isOrdered;
        FPgrowthHeaderTable temp;
        int                 index, place1, place2;

        // loop through table
        do {
            index     = 1;
            isOrdered = true;

            while (index < (localHeaderTable.length - 1)) {
                place1 = localHeaderTable[index].itemName;
                place2 = localHeaderTable[index + 1].itemName;

                if (countArray[place1].support > countArray[place2].support) {
                    isOrdered = false;

                    // Swap
                    temp                        = localHeaderTable[index];
                    localHeaderTable[index]     = localHeaderTable[index + 1];
                    localHeaderTable[index + 1] = temp;
                }

                // increment index
                index++;
            }
        } while (isOrdered == false);
    }

    /**
     * Generates a local FP tree
     * @param tableRef reference to start of header table containing links to
     * an FP-tree produced during the FP-tree generation process.
     * @return reference to the start of the generated FP-tree
     */
    private FPtreeNode generateLocalFPtree(FPgrowthHeaderTable[] tableRef) {
        FPgrowthSupportedSets ref       = startTempSets;
        FPtreeNode            localRoot = new FPtreeNode();

        // Loop
        while (ref != null) {

            // Add to conditional FP tree
            if (ref.itemSet != null) {
                addToFPtree(localRoot, 0, ref.itemSet, ref.support, tableRef);
            }

            ref = ref.nodeLink;
        }

        // Return
        return (localRoot);
    }

    /**
     * Resizes the given array of FP-tree nodes so that its length is
     * increased by one element and new element inserted.
     * @param oldArray the given array of FP-tree nodes.
     * @param newNode the given node to be added to the FP-tree
     * @return The revised array of FP-tree nodes.
     */
    private FPtreeNode[] reallocFPtreeChildRefs(FPtreeNode[] oldArray, FPtreeNode newNode) {

        // No old array
        if (oldArray == null) {
            FPtreeNode[] newArray = { newNode };

            tempIndex = 0;

            return (newArray);
        }

        // Otherwise create new array with length one greater than old array
        int          oldArrayLength = oldArray.length;
        FPtreeNode[] newArray       = new FPtreeNode[oldArrayLength + 1];

        // Insert new node in correct lexicographic order.
        for (int index1 = 0; index1 < oldArrayLength; index1++) {
            if (newNode.node.itemName < oldArray[index1].node.itemName) {
                newArray[index1] = newNode;

                for (int index2 = index1; index2 < oldArrayLength; index2++) {
                    newArray[index2 + 1] = oldArray[index2];
                }

                tempIndex = index1;

                return (newArray);
            }

            newArray[index1] = oldArray[index1];
        }

        // Default
        newArray[oldArrayLength] = newNode;
        tempIndex                = oldArrayLength;

        return (newArray);
    }

    /**
     * Commences process of outputting the prefix sub tree to the screen,
     * starting at header table. 
     */
    public void outputItemPrefixSubtree() {
        int flag;

        System.out.println("PREFIX SUBTREE FROM HEADER TABLE");

        for (int index = 1; index < headerTable.length; index++) {
            System.out.println("Header = " + reconvertItem(headerTable[index].itemName));
            flag = outputItemPrefixTree(headerTable[index].nodeLink);

            if (flag != 1) {
                System.out.println();
            }
        }

        System.out.println();
    }

    /**
     * Commences process of outputting a local prefix sub tree to the screen.
     * @param tableRef the reference to the local header table.
     */
    private void outputItemPrefixSubtree(FPgrowthHeaderTable[] tableRef) {
        int flag;

        System.out.println("PREFIX SUBTREE FROM LOCAL HEADER TABLE");

        for (int index = 1; index < tableRef.length; index++) {
            System.out.println("Header = " + reconvertItem(tableRef[index].itemName));
            flag = outputItemPrefixTree(tableRef[index].nodeLink);

            if (flag != 1) {
                System.out.println();
            }
        }

        System.out.println();
    }

    /**
     * Outputs the given prefix sub tree.
     * @param ref the reference to the given branch.
     * @return a counter representing the current "node number" (used in output). 
     */
    private int outputItemPrefixTree(FPgrowthItemPrefixSubtreeNode ref) {
        int counter = 1;

        // Loop
        while (ref != null) {
            System.out.print("(" + counter + ") " + (reconvertItem(ref.itemName)) + ":" + ref.itemCount + " ");
            counter++;
            ref = ref.nodeLink;
        }

        return (counter);
    }

    /** Commences process of outputting FP-tree to screen. */
    public void outputFPtree() {
        System.out.println("FP TREE");
        outputFPtreeNode1();
        System.out.println();
    }

    /**
     * Commences process of outputting a given branch of an FP-tree to the
     * screen.
     * @param ref the reference to the given FP-tree branch.
     */
    private void outputFPtreeNode(FPtreeNode ref) {
        System.out.println("LOCAL FP TREE");
        outputFPtreeNode2(ref.childRefs, "");
        System.out.println();
    }

    /** Continues process of outputting FP-tree to screen. */
    private void outputFPtreeNode1() {
        outputFPtreeNode2(rootNode.childRefs, "");
    }

    /**
     * Outputs a given level in an FP-tree to the screen.
     * @param ref the reference to the given FP-tree level.
     * @param nodeID the root string for the node ID.
     */
    private void outputFPtreeNode2(FPtreeNode ref[], String nodeID) {
        if (ref == null) {
            return;
        }

        // Otherwise process

        for (int index = 0; index < ref.length; index++) {
            System.out.print("(" + nodeID + (index + 1) + ") ");
            outputItemPrefixSubtreeNode(ref[index].node);
            outputFPtreeNode2(ref[index].childRefs, nodeID + (index + 1) + ".");
        }
    }

    /**
     * Outputs the given prefix sub tree node.
     * @param ref the reference to the given node.
     */
    public void outputItemPrefixSubtreeNode(FPgrowthItemPrefixSubtreeNode ref) {
        System.out.print((reconvertItem(ref.itemName)) + ":" + ref.itemCount);

        if (ref.nodeLink != null) {
            System.out.println(" (ref to " + (reconvertItem(ref.nodeLink.itemName)) + ":" + ref.nodeLink.itemCount
                               + ")");
        } else {
            System.out.println(" (ref to null)");
        }
    }

    /**
     * Commence the process of outputting the ancestor trail from the header
     * table 
     */
    private void outputAncesterTrail() {
        int flag;

        System.out.println("ANCESTOR TRAIL FROM HEADER TABLE");

        for (int index = 1; index < headerTable.length; index++) {
            System.out.println("Header = " + (reconvertItem(headerTable[index].itemName)));
            outputAncestorTrail1(headerTable[index].nodeLink);
        }

        System.out.println();
    }

    /**
     * Commence the process of outputting the ancestor trail from a local
     * header table.
     * @param tableRef the reference to the local header table.
     */
    private void outputAncesterTrail(FPgrowthHeaderTable[] tableRef) {
        int flag;

        System.out.println("ANCESTOR TRAIL FROM LOCAL HEADER TABLE");

        for (int index = 1; index < tableRef.length; index++) {
            System.out.println("Header = " + (reconvertItem(tableRef[index].itemName)));
            outputAncestorTrail1(tableRef[index].nodeLink);
        }

        System.out.println();
    }

    /**
     * Outputs the ancestor trail given a prefix sub tree.
     * @param ref the reference to the given branch. 
     */
    private void outputAncestorTrail1(FPgrowthItemPrefixSubtreeNode ref) {
        while (ref != null) {
            System.out.print("\t");
            outputAncestorTrail2(ref);
            ref = ref.nodeLink;
            System.out.println();
        }
    }

    /**
     * Outputs the given ancestor trail node in prefix sub tree.
     * @param ref the reference to the given node. 
     */
    private void outputAncestorTrail2(FPgrowthItemPrefixSubtreeNode ref) {
        while (ref != null) {
            System.out.print("(" + (reconvertItem(ref.itemName)) + ":" + ref.itemCount + ") ");
            ref = ref.parentRef;
        }
    }

    /**
     * Commence process of determining and outputting FP-tree storage, number
     * of updates and number of nodes. 
     */
    public void outputFPtreeStorage() {
        int storage = 8;    // 8 Bytes for root node

        numberOfNodes = 1;    // For root node
        storage       = calculateStorage(rootNode.childRefs, storage);

        // Add header table.
        storage = storage + (headerTable.length * 6);
        System.out.println("FP tree storage = " + storage + " (bytes)");
        System.out.println("FP tree updates = " + numUpdates);
        System.out.println("FP tree nodes   = " + numberOfNodes);
    }

    /**
     * Determines storage requirements for FP-tree.
     * @param ref the reference to the current portion of the P-tree under
     * consideration.
     * @param storage the storage requirements so far.
     * @return the storage in Bytes required for the given FP=tree node.
     */
    private int calculateStorage(FPtreeNode[] ref, int storage) {
        if (ref == null) {
            return (storage);
        }

        // Process, each node has 14+8 bytes of storage, 8 for FP tree
        // links (child and sibling), 14 for prefix tree links (parentRef,
        // nodeRef, Support, ID
        for (int index = 0; index < ref.length; index++) {
            storage = storage + 14 + 8;
            numberOfNodes++;
            storage = calculateStorage(ref[index].childRefs, storage);
        }

        // Return
        return (storage);
    }

    /**
     * Output local array count structure (diagnostic use only).
     * @param countArray the array of <TT>FPgrowthColumnCounts</TT> structures
     * describing the single item sets (in terms of labels and associated
     * support), contained in a linked list of <TT>FPgrowthSupportedSets</TT>
     * which in turn describe the ancestor nodes in an FP-tree that precede the
     * nodes identified by following a trail of links from a particular item in
     * the header table.  
     */
    private void outputColumnCount(FPgrowthColumnCounts[] countArray) {
        for (int index = 1; index < countArray.length; index++) {
            System.out.print("Col " + countArray[index].columnNum + " : ");

            if (countArray[index].support == 0) {
                System.out.println("Unsupported");
            } else {
                System.out.println(countArray[index].support);
            }
        }

        System.out.println();
    }

    /** Structure in which to store counts. */
    private class FPgrowthColumnCounts {

        /** The associated support value. */
        private int support = 0;

        /** The column/attribute ID number. */
        private short columnNum;

        /**
         * One argument constructor.
         *       @param column the column/attribute ID number.
         */
        private FPgrowthColumnCounts(int column) {
            columnNum = (short) column;
        }

        /**
         * Two argument constructor.
         * @param column the column/attribute ID number.
         * @param sup the associatec support value. 
         */
        private FPgrowthColumnCounts(int column, int sup) {
            columnNum = (short) column;
            support   = sup;
        }
    }


    /**
     * Header table.
     * <P> Array of these structures used to link into FP-tree.
     * All FP-tree nodes with the same identifier are linked
     * together starting from a node in a header table
     * (made up of <TT>HeaderTasble</TT> structures).
     * It is this "cross" linking that gives the FP-tree
     * its most significant advantage. 
     */
    protected class FPgrowthHeaderTable {

        /** The forward link to the next node in the link list of nodes. */
        protected FPgrowthItemPrefixSubtreeNode nodeLink = null;

        /** The 1-itemset (attribute) identifier. */
        protected short itemName;

        /**
         *  Constructors
         *      @param columnNum
         */
        protected FPgrowthHeaderTable(short columnNum) {
            itemName = columnNum;
        }
    }


    /**
     * Prefix subtree structure.
     * <P> A set enumeration tree in which to store itemsets
     * together with support values. 
     */
    private class FPgrowthItemPrefixSubtreeNode {

        /**
         * The forward link to the next node in a linked list of nodes
         * with same attribute identifier starting with an element in
         * the header table     (array). 
         */
        private FPgrowthItemPrefixSubtreeNode nodeLink = null;

        /** The backward link to the parent node in FP tree. */
        private FPgrowthItemPrefixSubtreeNode parentRef = null;

        /** The support count. */
        private int itemCount;

        /** The attribute identifier. */
        private short itemName;

        /** Default constructor. */
        private FPgrowthItemPrefixSubtreeNode() {}

        /**
         * Three argument constructor.
         * @param name the itemset identifier.
         * @param support the support value for the itemset.
         * @param backRef the backward link to the parent node. 
         */
        private FPgrowthItemPrefixSubtreeNode(short name, int support, FPgrowthItemPrefixSubtreeNode backRef) {
            itemName  = name;
            itemCount = support;
            parentRef = backRef;
        }
    }


    /**
     * Structure in which to store ancestor itemSets,
     * i.e. nodes in an FP-tree that precede the nodes
     * identified by following a trail of links from a
     * particular item in the header table. 
     */
    private class FPgrowthSupportedSets {

        /** The itemSet label. */
        private short[] itemSet = null;

        /** The reference to the next node in a linked list. */
        private FPgrowthSupportedSets nodeLink = null;

        /** The associated support value for the given itemset. */
        private int support;

        /**
         * Three argument constructor.
         * @param newitemSet the given itemSet label.
         * @param newSupport the associated support value for the given itemset.
         * @param newNodeLink the reference to the next node in a linked list. 
         */
        private FPgrowthSupportedSets(short[] newitemSet, int newSupport, FPgrowthSupportedSets newNodeLink) {
            itemSet  = newitemSet;
            support  = newSupport;
            nodeLink = newNodeLink;
        }
    }


    /**
     * FP-tree node structure comprising a
     * <TT>FPgrowthItemPrefixSubtreeNode</TT> in
     *  which to store counts and a reference to a
     *  child branch. 
     */
    protected class FPtreeNode {

        /**
         * The reference to the child branch (levels in FP-tree branches a
         * restored as a arrays of <TT>FPtreeNode</TT> structures.
         */
        private FPtreeNode[] childRefs = null;

        /**
         * The FP tree node.
         */
        private FPgrowthItemPrefixSubtreeNode node = null;

        /** Default constructor. */
        protected FPtreeNode() {}

        /**
         * Single argument constructor.
         * @param newNode The reference to a new node to be included in
         * the FP-tree.
         */
        protected FPtreeNode(FPgrowthItemPrefixSubtreeNode newNode) {
            node = newNode;
        }
    }
}

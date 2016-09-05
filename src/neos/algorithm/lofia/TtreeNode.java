package neos.algorithm.lofia;

/* ------------------------------------------------------------------------- */
/*                                                                           */
/*                           TOTAL SUPPORT TREE BODE                         */
/*                                                                           */
/*                                Frans Coenen                               */
/*                                                                           */
/*                            Wednesday 2 July 2003                          */
/*                                                                           */
/*                       Department of Computer Science                      */
/*                         The University of Liverpool                       */
/*                                                                           */
/* ------------------------------------------------------------------------- */

/**
 * Methods concerned with Ttree node structure. Arrays of
 * these structures are used to store nodes at the same level in any
 * sub-branch of the T-tree.
 * @author Frans Coenen
 * @version 2 July 2003
 */
public class TtreeNode {

    // Diagnostics

    /** The number of nodes in the T-tree. */
    public static int numberOfNodes = 0;

    /** A reference variable to the child (if any) of the node. */
    public TtreeNode[] childRef = null;

    /* ------ FIELDS ------ */

    /** The support associate with the itemset represented by the node. */
    public int support = 0;

    /* ------ CONSTRUCTORS ------ */

    /** Default constructor */
    public TtreeNode() {
        numberOfNodes++;
    }

    /**
     * One argument constructor.
     * @param sup the support value to be included in the structure. 
     */
    public TtreeNode(int sup) {
        support = sup;
        numberOfNodes++;
    }

    /* ------ METHODS ------ */
    public static int getNumberOfNodes() {
        return (numberOfNodes);
    }
}

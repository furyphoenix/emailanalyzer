package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

public class Afisa {
    private static boolean           DEBUG = false;
    public final BitSet[]            BitVectors;
    private double                   NumberOfItemsets;
    private Cluster[]                clusters;
    private Proximity                cp;
    private double[][]               distance;
    public HashMap<Integer, Integer> nameHash;
    private final double[][]         originalDistance;
    private boolean                  overlp;
    private int                      size;
    private int                      supportCount;

    public static enum DistanceMethod { D1, XOR }

    public static enum ProximityMethod { Single, Average, Complete }

    
    public Afisa(BitSet[] BitVectors, double[][] diss, ProximityMethod method, int supportCount, boolean shallOverlap) {
        this.overlp = shallOverlap;

        if ((BitVectors == null) || (diss == null) || (method == null)) {
            System.err.println(
                "None of the arguments of public Afisa(BitSet[] BitVectors, double[][] diss, String method, int supportCount) can be null");
            System.exit(-1);
        }

        if (supportCount < 0) {
            System.err.println("supportCount can be less than 0");
        } else if (supportCount == 0) {
            System.out.println("All items qualifiy");
            System.out.println("Number of frequent itemsets is: " + (Math.pow(2.0D, BitVectors.length) - 1.0D));
            System.exit(0);
        }

        this.BitVectors = BitVectors;

        Vector<Integer> qualifiedItems = new Vector<Integer>();

        this.NumberOfItemsets = 0.0D;

        int realSize = 0;

        for (int i = 0; i < BitVectors.length; i++) {
            if (BitVectors[i].cardinality() >= supportCount) {
                realSize++;
                qualifiedItems.add(new Integer(i));
            }
        }

        this.nameHash = new HashMap<Integer, Integer>(2 * realSize + 3);

        if (realSize != diss.length) {
            double[][]            newDiss = new double[realSize][realSize];
            ListIterator<Integer> iterOut = qualifiedItems.listIterator();

            for (int i = 0; i < realSize; i++) {
                int                   outer  = ((Integer) (Integer) iterOut.next()).intValue();
                ListIterator<Integer> iterIn = qualifiedItems.listIterator();

                for (int j = 0; j < realSize; j++) {
                    int inner = ((Integer) (Integer) iterIn.next()).intValue();

                    newDiss[i][j] = diss[outer][inner];
                }
            }

            if (qualifiedItems.size() > 0) {
                int                   index = 0;
                ListIterator<Integer> iter  = qualifiedItems.listIterator();

                while (iter.hasNext()) {
                    int oldIndex = ((Integer) (Integer) iter.next()).intValue();

                    this.nameHash.put(new Integer(index), new Integer(oldIndex));
                    index++;
                }
            }

            this.originalDistance = newDiss;
        } else {
            this.originalDistance = diss;
        }

        this.supportCount = supportCount;
        this.size         = realSize;
        this.clusters     = new Cluster[this.size];

        ListIterator<Integer> iter = qualifiedItems.listIterator();

        for (int i = 0; i < this.size; i++) {
            this.clusters[i] = new Cluster(i, BitVectors[((Integer) (Integer) iter.next()).intValue()]);
        }

        switch (method) {
        case Single :
            this.cp = new SingleLink();

            break;

        case Average :
            this.cp = new Average();

            break;

        case Complete :
            this.cp = new CompleteLink();

            break;

        default :
            this.cp = new CompleteLink();
        }
    }

    public Map<Integer, List<Cluster>> run() {
    	Map<Integer, List<Cluster>> supClustersMap=new Hashtable<Integer, List<Cluster>> ();
    	
        int i = 0;
        int j = 0;

        this.distance = new double[this.size][this.size];

        for (int m = 0; m < this.size; m++) {
            for (int n = 0; n < this.size; n++) {
                this.distance[m][n] = this.originalDistance[m][n];
            }
        }

        while (this.size > 1) {
            double minVal = 1.7976931348623157E+308D;

            for (int k = 0; k < this.size; k++) {
                for (int l = k + 1; l < this.size; l++) {
                    if (this.distance[k][l] < minVal) {
                        i      = k;
                        j      = l;
                        minVal = this.distance[i][j];
                    }
                }
            }

            Cluster c1 = this.clusters[i];
            Cluster c2 = this.clusters[j];

            if (DEBUG) {
                System.out.println(c1 + " ::: " + c2 + " ::: " + minVal);
            }

            Cluster merge = new Cluster();

            merge.addCluster(c1);
            merge.addCluster(c2);
            merge.addItems(c1);
            merge.addItems(c2);
            merge.setDistance(minVal);
            merge.setSupportVector(c1.andVector(c2.getSupportVector()));
            merge.setSupportCount(Cluster.calculateSupport(merge.getSupportVector()));

            if (merge.getSupportCount() < this.supportCount) {
                if (this.overlp) {
                    overlap(c1, c2, merge);
                }

                System.out.print("Support Count==>" + c1.getSupportCount() + " ::: ");
                this.NumberOfItemsets += Math.pow(2.0D, c1.getItems().size()) - 1.0D;
                System.out.println(c1.toString(this));
                
                int support=c1.getSupportCount();
                List<Cluster> clusterList;
                if(supClustersMap.containsKey(support)){
                	clusterList=supClustersMap.get(support);
                }else{
                	clusterList=new ArrayList<Cluster> ();
                	supClustersMap.put(support, clusterList);
                }
                clusterList.add(c1);
                
                System.out.print("Support Count==>" + c2.getSupportCount() + " ::: ");
                this.NumberOfItemsets += Math.pow(2.0D, c2.getItems().size()) - 1.0D;
                System.out.println(c2.toString(this));
                
                support=c2.getSupportCount();
                if(supClustersMap.containsKey(support)){
                	clusterList=supClustersMap.get(support);
                }else{
                	clusterList=new ArrayList<Cluster> ();
                	supClustersMap.put(support, clusterList);
                }
                clusterList.add(c2);
                
                merge = null;
            }

            modifyDistanceandCluster(i, j, merge);
            this.size -= 1;

            if (merge == null) {
                this.size -= 1;
            }
        }

        for (int k = 0; k < this.clusters.length; k++) {
            System.out.print("Support Count==>" + this.clusters[k].getSupportCount() + " ::: ");
            System.out.println(this.clusters[k].toString(this));
            
            int support=this.clusters[k].getSupportCount();
            List<Cluster> clusterList;
            if(supClustersMap.containsKey(support)){
            	clusterList=supClustersMap.get(support);
            }else{
            	clusterList=new ArrayList<Cluster> ();
            	supClustersMap.put(support, clusterList);
            }
            clusterList.add(this.clusters[k]);
            
            this.NumberOfItemsets += Math.pow(2.0D, this.clusters[k].getItems().size()) - 1.0D;
        }
        
        return supClustersMap;
    }

    public void modifyDistanceandCluster(int i, int j, Cluster merged) {
        HashMap<Integer, Integer> hp = new HashMap<Integer, Integer>(3 * this.size);
        double[][]                distanceNew;
        Cluster[]                 clusterNew;

        // double[][] distanceNew;
        if (merged != null) {
            clusterNew  = new Cluster[this.size - 1];
            distanceNew = new double[this.size - 1][this.size - 1];
        } else {
            clusterNew  = new Cluster[this.size - 2];
            distanceNew = new double[this.size - 2][this.size - 2];
        }

        int m = 0;

        if (merged != null) {
            clusterNew[m] = merged;

            for (int k = 0; k < this.size; k++) {
                if ((((k == i)
                      ? 1
                      : 0) | ((k == j)
                              ? 1
                              : 0)) == 0) {
                    m++;
                    clusterNew[m] = this.clusters[k];
                    hp.put(new Integer(m), new Integer(k));
                }
            }
        } else {
            for (int k = 0; k < this.size; k++) {
                if ((((k == i)
                      ? 1
                      : 0) | ((k == j)
                              ? 1
                              : 0)) == 0) {
                    clusterNew[m] = this.clusters[k];
                    hp.put(new Integer(m), new Integer(k));
                    m++;
                }
            }
        }

        if (merged != null) {
            for (int k = 0; k < this.size - 1; k++) {
                for (int n = k + 1; n < this.size - 1; n++) {
                    if (k == 0) {
                        distanceNew[k][n] = this.cp.proximity(clusterNew[k], clusterNew[n], this.originalDistance);
                    } else {
                        distanceNew[k][n] =
                            this
                            .distance[((Integer) (Integer) hp.get(new Integer(k))).intValue()][((Integer) (Integer) hp.get(new Integer(n))).intValue()];
                    }
                }
            }
        } else {
            for (int k = 0; k < this.size - 2; k++) {
                for (int n = k + 1; n < this.size - 2; n++) {
                    distanceNew[k][n] =
                        this
                        .distance[((Integer) (Integer) hp.get(new Integer(k))).intValue()][((Integer) (Integer) hp.get(new Integer(n))).intValue()];
                }
            }
        }

        this.clusters = clusterNew;
        this.distance = distanceNew;
    }

    public double getNumberOfItemsets() {
        return this.NumberOfItemsets;
    }

    public void overlap(Cluster a, Cluster b, Cluster merge) {
        Vector<Integer>       origAItems = (Vector<Integer>) a.getItems().clone();
        Vector<Integer>       origBItems = (Vector<Integer>) b.getItems().clone();
        ListIterator<Integer> itA        = origAItems.listIterator();
        ListIterator<Integer> itB        = origBItems.listIterator();
        int                   overlapped = 0;

        while (itB.hasNext()) {
            Integer mockItem = (Integer) (Integer) itB.next();
            Integer realItem = (Integer) (Integer) this.nameHash.get(mockItem);

            if (realItem == null) {
                realItem = mockItem;
            }

            BitSet aSupportVector = a.andVector(this.BitVectors[realItem.intValue()]);
            int    cardinality    = aSupportVector.cardinality();

            if (cardinality < this.supportCount) {
                continue;
            }

            if (!a.getItems().contains(mockItem)) {
                overlapped++;
                a.addItem(mockItem);
                a.setSupportCount(cardinality);
                a.setSupportVector(aSupportVector);
                System.out.println("Added " + (realItem.intValue() + 1) + " to " + a.toString(this));
            }
        }

        while (itA.hasNext()) {
            Integer mockItem = (Integer) (Integer) itA.next();
            Integer realItem = (Integer) (Integer) this.nameHash.get(mockItem);

            if (realItem == null) {
                realItem = mockItem;
            }

            BitSet bSupportVector = b.andVector(this.BitVectors[realItem.intValue()]);
            int    cardinality    = bSupportVector.cardinality();

            if (cardinality < this.supportCount) {
                continue;
            }

            if (!b.getItems().contains(mockItem)) {
                overlapped++;
                b.addItem(mockItem);
                b.setSupportCount(cardinality);
                b.setSupportVector(bSupportVector);
                System.out.println("Added " + (realItem.intValue() + 1) + " to " + b.toString(this));
            }
        }

        this.NumberOfItemsets -= Math.pow(2.0D, overlapped) - 1.0D;
    }
}

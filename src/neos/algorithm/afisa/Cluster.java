package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.BitSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

public class Cluster {
    private Vector<Cluster> clusters;
    private double distance;
    private Vector<Integer> items;
    private int    supportCount;
    private BitSet supportVector;

    public Cluster() {
        this.clusters = new Vector<Cluster>();
        this.items    = new Vector<Integer>();
    }

    public Cluster(int itemNumber, BitSet v) {
        this.clusters = new Vector<Cluster>();
        addCluster(this);
        this.items = new Vector<Integer>();
        addItem(new Integer(itemNumber));
        setDistance(0.0D);
        setSupportCount(calculateSupport(v));
        setSupportVector(v);
    }

    public void setSupportCount(int s) {
        this.supportCount = s;
    }

    public int getSupportCount() {
        return this.supportCount;
    }

    public void setDistance(double d) {
        this.distance = d;
    }

    public double getDistance() {
        return this.distance;
    }

    public BitSet andVector(BitSet v) {
        BitSet bs = (BitSet) this.supportVector.clone();

        bs.and(v);

        return bs;
    }

    public static int calculateSupport(BitSet v) {
        return v.cardinality();
    }

    public Vector<Cluster> getClusters() {
        return this.clusters;
    }

    public void addCluster(Cluster c) {
        this.clusters.add(c);
    }

    public Vector<Integer> getItems() {
        return this.items;
    }

    public void addItem(Integer i) {
        this.items.add(i);
    }

    public void addItems(Cluster c) {
        Vector<Integer>       ts = c.getItems();
        ListIterator<Integer> it = ts.listIterator();

        while (it.hasNext()) {
            addItem((Integer) (Integer) it.next());
        }
    }

    public void setSupportVector(BitSet v) {
        this.supportVector = v;
    }

    public BitSet getSupportVector() {
        return this.supportVector;
    }

    public String toString(Afisa ag) {
        Iterator<Integer>     it  = this.items.iterator();
        StringBuffer sbf = new StringBuffer();

        while (it.hasNext()) {
            Integer oldValue = (Integer) (Integer) it.next();
            Integer newValue = (Integer) (Integer) ag.nameHash.get(oldValue);

            if (newValue == null) {
                sbf.append(oldValue.intValue() + 1);
            } else {
                sbf.append(newValue.intValue() + 1);
            }

            sbf.append(",");
        }

        return sbf.toString();
    }
}

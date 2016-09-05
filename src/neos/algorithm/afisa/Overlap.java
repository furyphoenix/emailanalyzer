package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.BitSet;

public class Overlap extends Distance {
    public Overlap(BitSet[] items) {
        super(items);
    }

    public double getDistance(BitSet a, BitSet b) {
        BitSet aANDb = (BitSet) a.clone();

        aANDb.and(b);

        double dist = aANDb.cardinality() / Math.min(a.cardinality(), b.cardinality());

        return 1.0D - dist;
    }
}

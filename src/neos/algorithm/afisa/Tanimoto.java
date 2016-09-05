package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.BitSet;

public class Tanimoto extends Distance {
    public Tanimoto(BitSet[] items) {
        super(items);
    }

    public double getDistance(BitSet a, BitSet b) {
        BitSet aANDb = (BitSet) a.clone();
        BitSet aORb  = (BitSet) a.clone();

        aANDb.and(b);
        aORb.or(b);

        double dist = aANDb.cardinality() / aORb.cardinality();

        return 1.0D - dist;
    }
}

package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.BitSet;

public class D1 extends Distance {
    public D1(BitSet[] items) {
        super(items);
    }

    public double getDistance(BitSet a, BitSet b) {
        BitSet a_Clone = (BitSet) a.clone();
        BitSet b_Clone = (BitSet) b.clone();

        a_Clone.xor(b);
        b_Clone.or(a);

        double value = a_Clone.cardinality() / b_Clone.cardinality();

        return value;
    }
}

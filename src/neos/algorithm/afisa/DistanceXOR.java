package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.BitSet;

public class DistanceXOR extends Distance {
    public DistanceXOR(BitSet[] items) {
        super(items);
    }

    public double getDistance(BitSet a, BitSet b) {
        BitSet result = (BitSet) a.clone();

        result.xor(b);

        return result.cardinality();
    }
}

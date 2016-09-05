package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.BitSet;

public class Asym_Diss extends Distance {
    public Asym_Diss(BitSet[] items) {
        super(items);
    }

    public double getDistance(BitSet a, BitSet b) {
        BitSet result        = (BitSet) a.clone();
        double cardinality_a = result.cardinality();

        result.and(b);

        double cardinality_a_AND_b = result.cardinality();
        double value               = (cardinality_a - cardinality_a_AND_b) / cardinality_a;

        return value;
    }
}

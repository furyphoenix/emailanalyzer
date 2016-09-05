package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.ListIterator;
import java.util.Vector;

public class SingleLink implements Proximity {
    public double proximity(Cluster c1, Cluster c2, double[][] originalDistance) {
        double       minVal = 1.7976931348623157E+308D;
        Vector<Integer>       set1   = c1.getItems();
        Vector<Integer>       set2   = c2.getItems();
        ListIterator<Integer> it1    = set1.listIterator();

        while (it1.hasNext()) {
            int          i   = ((Integer) (Integer) it1.next()).intValue();
            ListIterator<Integer> it2 = set2.listIterator();

            while (it2.hasNext()) {
                int j = ((Integer) (Integer) it2.next()).intValue();

                if (originalDistance[i][j] < minVal) {
                    minVal = originalDistance[i][j];
                }
            }
        }

        return minVal;
    }
}

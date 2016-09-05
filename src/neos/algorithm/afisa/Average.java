package neos.algorithm.afisa;

//~--- JDK imports ------------------------------------------------------------

import java.util.ListIterator;
import java.util.Vector;

public class Average implements Proximity {
    public double proximity(Cluster c1, Cluster c2, double[][] originalDistance) {
        double       average  = 0.0D;
        Vector<Integer>       set1     = c1.getItems();
        Vector<Integer>       set2     = c2.getItems();
        double       set1Size = set1.size();
        double       set2Size = set2.size();
        ListIterator<Integer> it1      = set1.listIterator();

        while (it1.hasNext()) {
            int          i   = ((Integer) (Integer) it1.next()).intValue();
            ListIterator<Integer> it2 = set2.listIterator();

            while (it2.hasNext()) {
                int j = ((Integer) (Integer) it2.next()).intValue();

                average += originalDistance[i][j];
            }
        }

        return average / (set1Size * set2Size);
    }
}

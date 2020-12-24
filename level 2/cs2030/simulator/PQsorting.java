package cs2030.simulator;
import java.util.Comparator;
public class PQsorting implements Comparator<Customer> {
    /** Comparing the Customer objects in the PriorityQueue. Customers will be sorted by timing of
     * their activity in ascending order.
     */
    public int compare(Customer a, Customer b) {
        /**
         *Calculating he arrival time of customer a.
         * @param time1 is the time of customer a.
         */
        double time1 = a.getTime();
        /**
         *Calculating the arrival time of customer b.
         * @param time2 is the time of customer b.
         */
        double time2 = b.getTime();
        /** If the timing of the activity of the 2 customers is the same.
         */
        /** The Customer with the lower ID will have the higher priority.
         */
        if (time1 == time2) {
            int id1 = a.getId();
            int id2 = b.getId();
            return id1 - id2;
        } else if (time1 > time2) {
            return 1;
        } else {
            return -1;
        }
    }
}


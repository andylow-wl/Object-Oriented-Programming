package cs2030.simulator;

public class Customer {
    private final double time;
    private final int id;
    public final String activity;
    private final int server;
    private final double servingDuration;

    /** Creating a Customer Object.
     */
    public Customer(double time, int id, String activity, int server, double servingDuration) {
        this.id = id;
        this.time = time;
        this.activity = activity;
        this.server = server;
        this.servingDuration = servingDuration;
    }

    /**Get ID of customer.
     */
    public int getId() {
        return this.id;
    }

    /** Get Time of customer.
     */
    public double getTime() {
        return this.time;
    }

    /**Get Activity of customer.
     */
    public String getActivity() {
        return this.activity;
    }

    /** Get ID of the server.
     */
    public int getServerID() {
        return this.server;
    }

    public double getServingDuration(){
        return this.servingDuration;
    }



    /**
     * Converting it the string.
     * If server is equal to 0, it means that no one is
     * serving that customer
     */

    public String toString() {
        if (this.server == 0) {
            return String.format("%.03f",this.time) + " " + Integer.toString(this.id) +
                    " " + activity;
        }

        return String.format("%.03f",this.time) + " " + Integer.toString(this.id) + " " + activity +
                Integer.toString(this.server);
    }
}
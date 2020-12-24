import java.util.Scanner;
import cs2030.simulator.Simulator;
import cs2030.simulator.PQsorting;
import cs2030.simulator.Customer;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args) {


        /**
         * Scanning of all the inputs.
         */
        Scanner sc = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("0.000");
        int seed = sc.nextInt();
        int noOfServers = sc.nextInt();
        int maxQueue = sc.nextInt();
        int customers = sc.nextInt();
        double arrivalRate = sc.nextDouble();
        double serviceRate = sc.nextDouble();
        double restingRate= sc.nextDouble();
        double probability =sc.nextDouble();

        /**
         *
         * @param waitingTime         Waiting time of each customer
         * @param servingCust         No of customer served by each server
         * @param waitingQueue        Stores the id of customers waiting for each server
         * @param servingDuration     Stores the service rate of each customer
         * @param restingTime         Stores the resting duration of each server
         * @param arrivalTime         Stores the arrival time of each customer
         * @param waitingPeople       Stores the no of customer waiting for each server
         * @param restingServer       Stores the no of resting sever -1 (for list)
         */

        double[] waitingTime = new double[customers + 1];
        double[] servingCust = new double[noOfServers + 1];
        int[][] waitingQueue = new int[maxQueue + 1][noOfServers + 1];
        double[][] servingDuration = new double[customers + 1][noOfServers + 1];
        double [] restingTime= new double[noOfServers+1];
        double[] arrivalTime = new double[customers+1];
        int[] waitingPeople = new int[noOfServers+1];
        int[]restingServer= new int[noOfServers+1];

        /**
         *
         * @param served        No of customers served
         * @param leaves        No of customer who leaves
         * @param totalWait     Total waiting time for waiting customers
         * @param servingNo     No of customers served - 1 (for List)
         * @param rest          No of servers who rest
         * @param timing        Timing of each customer arrival time
         */

        int served = 0;
        int leaves = 0;
        double totalWait = 0;
        int servingNo = 0;
        int rest = 0 ;
        double timing = 0;

        /**
         * Creates a priorityqueue to sort the customer object.
         */

        Comparator<Customer> comparator = new PQsorting();
        PriorityQueue<Customer> pq = new PriorityQueue<Customer>(comparator);

        /**
         * Using Simulator Class to return a list of randomly generated
         * arrival time and service rate.
         *
         * @param list  stores the list of randomly generated arrivalTime
         * @param list1 stores the list of randomly generated serviceRate
         * @param list2 stores the list of randomly resting duration
         * @param list3 stores the list of randomly generated probability
         */

        ArrayList<Double> list = Simulator.getArrivalTime(seed, arrivalRate, serviceRate, restingRate,customers);
        ArrayList<Double> list1 = Simulator.getServiceTime(seed, arrivalRate, serviceRate, restingRate,customers);
        ArrayList<Double> list2 = Simulator.getResting(seed, arrivalRate, serviceRate, restingRate, customers);
        ArrayList<Double> list3 = Simulator.getProbability(seed, arrivalRate, serviceRate, restingRate,customers);

        /**
         * Assigning each customer with their own arrival time
         */

        for (int i = 1; i < customers + 1; i++) {
            if (i == 1) {
                pq.add(new Customer(timing, i, "arrives", 0, 0));
            } else {
                timing += list.get(i - 2).doubleValue();
                arrivalTime[i]=timing;
                pq.add(new Customer(timing, i, "arrives", 0, 0));
            }
        }

        while (!pq.isEmpty()) {
            /**
             * While PriorityQueue is not empty, keep on
             * printing the customer object.
             */

            int id = pq.peek().getId();
            double time = pq.peek().getTime();
            String activity = pq.peek().getActivity();
            int server = pq.peek().getServerID();

            /**
             *
             * @param check   returns true if customer can be served
             * @param check2  returns true if customer can wait
             *
             */

            boolean check = false;
            boolean check2 = false;

            /**
             * Checks if customer can be served
             * If cannot , check if customer can wait
             * If both checks fail, customer will leave
             */

            if (activity == "arrives") {
                for (int i = 1; i < noOfServers + 1; i++) {
                    if ((servingCust[i] == 0 && time >= restingTime[i]) && waitingPeople[i] == 0) {
                        pq.add(new Customer(time, id, "served by server ", i, 0));
                        servingCust[i]++;
                        check = true;
                        break;
                    }
                }

                if (!check) {
                    for (int i = 1; i < noOfServers + 1; i++) {
                        for (int a = 1; a < maxQueue + 1; a++) {
                            if (waitingQueue[a][i] == 0) {
                                pq.add(new Customer(time, id, "waits to be served by server ", i, 0));
                                waitingQueue[a][i] = id;
                                check2 = true;
                                waitingTime[id] = time;
                                waitingPeople[i]++;
                                break;
                            }
                        }
                        if (check2) {
                            break;
                        }
                    }
                }

                if (!check && !check2) {
                    pq.add(new Customer(time, id, "leaves", 0, 0));
                    leaves++;
                }
            }

            /**
             * If Customer is served, get the service rate
             * from list1 and assign it to the Customer Object.
             * If Customer is a waiting customer,
             * push all the waiting customers of that sever
             * forward.
             *
             */

            if (activity.contains("served by server") && !activity.contains("waits")) {
                double serving = list1.get(servingNo).doubleValue();

                servingNo++;
                servingDuration[id][server] = serving;
                pq.add(new Customer(time + servingDuration[id][server], id, "done serving by server ", server,
                        servingDuration[id][server]));

                if(id == waitingQueue[1][server]){
                    for (int i = 1; i < maxQueue; i++) {
                        waitingQueue[i][server] = waitingQueue[i + 1][server];
                    }
                    waitingQueue[maxQueue][server] = 0;
                    waitingPeople[server]--;
                }
            }

            /**
             * If that customer is waiting
             * and server is not serving anyone,
             * that customer will be served.
             */

            if(activity.contains("waits to be served")){
                if(servingCust[server]==0) {
                    pq.add(new Customer(restingTime[server], id, "served by server ", server, 0));
                    servingCust[server]++;
                    totalWait += (restingTime[server] - waitingTime[id]);

                }
            }

            /**
             * After a customer is served,
             * check for probability.
             *
             * If probability is lesser that the one stated,
             * server will rest and get the resting duration from list2.
             * If there is a waiting customer, assign that waiting customer
             * being served after the server finished resting.
             *
             * If probability is larger than the stated one,
             * if there is a waiting customer, serve that
             * custoemr immediately.
             *
             */


            if (activity.contains("done serving by server ")) {
                double prob = list3.get(served).doubleValue();
                served++;
                if (prob < probability) {
                    double restingDuration = list2.get(rest).doubleValue();
                    rest++;
                    restingTime[server] = restingDuration + time;
                    restingServer[server]++;

                    if (waitingQueue[1][server] != 0) {
                        pq.add(new Customer(restingTime[server], waitingQueue[1][server],
                                "served by server ", server, servingDuration[waitingQueue[1][server]][server]));
                        totalWait += (restingTime[server] - waitingTime[waitingQueue[1][server]]);

                    } else {
                        servingCust[server]--;
                    }
                } else {
                    if (waitingQueue[1][server] != 0) {
                        pq.add(new Customer(time, waitingQueue[1][server], "served by server ", server,
                                servingDuration[waitingQueue[1][server]][server]));
                        totalWait += (time - waitingTime[waitingQueue[1][server]]);

                    } else {
                        servingCust[server]--;
                    }
                }
            }


            System.out.println(pq.poll());
        }

        if (served == 0) {
            System.out.println("[" + df.format(0)+ " "
                    + Integer.toString(served) + " " + Integer.toString(leaves) + "]");

        } else {
            System.out.println("[" + String.format("%.03f", totalWait / served) + " "
                    + Integer.toString(served) + " " + Integer.toString(leaves) + "]");
        }
    }
}































































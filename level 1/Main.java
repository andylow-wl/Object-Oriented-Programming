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

        Scanner sc = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("0.000");
        int seed = sc.nextInt();
        int noOfServers = sc.nextInt();
        int maxQueue = sc.nextInt();
        int customers = sc.nextInt();
        double arrivalRate = sc.nextDouble();
        double serviceRate = sc.nextDouble();

        double[] waitingTime = new double[customers + 1];
        double[] servingCust = new double[noOfServers + 1];
        int[][] waitingQueue = new int[maxQueue + 1][noOfServers + 1];
        double[][] servingDuration = new double[customers + 1][noOfServers + 1];

        int served = 0;
        int leaves = 0;
        double totalWait = 0;
        int servingNo = 0;

        Comparator<Customer> comparator = new PQsorting();
        PriorityQueue<Customer> pq = new PriorityQueue<Customer>(comparator);
        double timing = 0;


        ArrayList<Double> list = Simulator.getArrivalTime(seed, arrivalRate, serviceRate, customers);
        ArrayList<Double> list1 = Simulator.getServiceTime(seed, arrivalRate, serviceRate, customers);

        for (int i = 1; i < customers + 1; i++) {
            if (i == 1) {
                pq.add(new Customer(timing, i, "arrives", 0, 0));
            } else {
                timing += list.get(i - 2).doubleValue();
                pq.add(new Customer(timing, i, "arrives", 0, 0));
            }
        }

        while (!pq.isEmpty()) {
            int id = pq.peek().getId();
            double time = pq.peek().getTime();
            String activity = pq.peek().getActivity();
            int server = pq.peek().getServerID();

            boolean check = false;
            boolean check2 = false;

            if (activity == "arrives") {
                for (int i = 1; i < noOfServers + 1; i++) {
                    if (servingCust[i] == 0) {
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

            if (activity.contains("served by server") && !activity.contains("waits")) {
                double serving = list1.get(servingNo).doubleValue();
                servingNo++;
                servingDuration[id][server] = serving;
                pq.add(new Customer(time + servingDuration[id][server], id, "done serving by server ", server,
                        servingDuration[id][server]));
            }

            if (activity.contains("done serving by server ")) {
                served++;
                if (waitingQueue[1][server] != 0) {
                    pq.add(new Customer(time, waitingQueue[1][server], "served by server ", server,
                            servingDuration[waitingQueue[1][server]][server]));
                    totalWait += (time - waitingTime[waitingQueue[1][server]]);
                    for (int i = 1; i < maxQueue; i++) {
                        waitingQueue[i][server] = waitingQueue[i + 1][server];
                    }
                    waitingQueue[maxQueue][server] = 0;

                } else {
                    servingCust[server]--;
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



































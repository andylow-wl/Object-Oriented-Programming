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
        int noOfSelfServers=sc.nextInt();
        int maxQueue = sc.nextInt();
        int customers = sc.nextInt();
        double arrivalRate = sc.nextDouble();
        double serviceRate = sc.nextDouble();
        double restingRate= sc.nextDouble();
        double probability =sc.nextDouble();
        double greedyRate= sc.nextDouble();

        double[] waitingTime = new double[customers + 1];
        double[] servingCust = new double[noOfServers + 2 +noOfSelfServers];
        int[][] waitingQueue = new int[maxQueue + 1][noOfServers + 1+noOfSelfServers];
        double[][] servingDuration = new double[customers + 1][noOfServers + 1+noOfSelfServers];
        double [] restingTime= new double[noOfServers+1+noOfSelfServers];
        double[] arrivalTime = new double[customers+1];
        int[] waitingPeople = new int[noOfServers+1+noOfSelfServers];
        int[]restingServer= new int[noOfServers+1+noOfSelfServers];
        int[] greedyCust =new int[customers+1];

        int served = 0;
        int leaves = 0;
        double totalWait = 0;
        int servingNo = 0;
        int dontRest = 0 ;
        int manualServed=0;


        Comparator<Customer> comparator = new PQsorting();
        PriorityQueue<Customer> pq = new PriorityQueue<Customer>(comparator);
        double timing = 0;


        ArrayList<Double> list = Simulator.getArrivalTime(seed, arrivalRate, serviceRate, restingRate,customers);
        ArrayList<Double> list1 = Simulator.getServiceTime(seed, arrivalRate, serviceRate, restingRate,customers);
        ArrayList<Double> list2 = Simulator.getResting(seed, arrivalRate, serviceRate, restingRate,customers);
        ArrayList<Double> list3 = Simulator.getProbability(seed, arrivalRate, serviceRate, restingRate,customers);
        ArrayList<Double> list4 = Simulator.getGreedyRate(seed,arrivalRate,serviceRate,restingRate,customers);

        for (int i = 1; i < customers + 1; i++) {
            double greedyProb = list4.get(i-1).doubleValue();
            if (greedyProb < greedyRate) {
                greedyCust[i]++;
                if (i == 1) {
                    pq.add(new Customer(timing, i, "(greedy)","arrives", 0, 0));
                } else {
                    timing += list.get(i - 2).doubleValue();
                    arrivalTime[i] = timing;
                    pq.add(new Customer(timing, i, "(greedy)","arrives", 0, 0));
                }
            } else {
                if (i == 1) {
                    pq.add(new Customer(timing, i,"", "arrives", 0, 0));
                } else {
                    timing += list.get(i - 2).doubleValue();
                    arrivalTime[i] = timing;
                    pq.add(new Customer(timing, i,"", "arrives", 0, 0));
                }
            }
        }

        while (!pq.isEmpty()) {
            int id = pq.peek().getId();
            double time = pq.peek().getTime();
            String activity = pq.peek().getActivity();
            int server = pq.peek().getServerID();
            String greedy = pq.peek().getGreedy();

            boolean check = false;
            boolean check1 = false;
            boolean check2 = false;

            if (activity == "arrives") {
                for (int i = 1; i < noOfServers + 1; i++) {
                    if ((servingCust[i] == 0 && time >= restingTime[i]) && waitingPeople[i] == 0) {
                        pq.add(new Customer(time, id, greedy, "served by server ", i, 0));
                        servingCust[i]++;
                        check = true;
                        break;
                    }
                }

                if (!check) {
                    for (int i = 1 + noOfServers; i < noOfSelfServers + noOfServers + 1; i++) {
                        if (servingCust[i] == 0 && waitingPeople[i] == 0) {
                            pq.add(new Customer(time, id, greedy, "served by self-check ", i, 0));
                            servingCust[i]++;
                            check1 = true;
                            break;
                        }

                    }
                }

                if (noOfSelfServers > 0 && greedy.equals("")) {
                    if (!check && !check1) {
                        for (int i = 1; i < noOfServers + noOfSelfServers; i++) {
                            for (int a = 1; a < maxQueue + 1; a++) {
                                if (waitingQueue[a][i] == 0) {
                                    if (i <= noOfServers) {
                                        pq.add(new Customer(time, id, greedy, "waits to be served by server ", i, 0));
                                    } else {
                                        pq.add(new Customer(time, id, greedy, "waits to be served by self-check ", i, 0));
                                    }
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
                } else if (noOfSelfServers > 0 && greedy.equals("(greedy)")) {
                    if (!check && !check1) {
                        int minQueue = maxQueue;
                        int minSelfQueue=maxQueue;
                        int minServer = 0;
                        int waitingSelfServers = 0;
                        for (int a = noOfServers+1; a < noOfServers + noOfSelfServers + 1; a++) {
                            waitingSelfServers += waitingPeople[a];
                        //    System.out.println(waitingPeople[a] +" waiting for self server "+ a + " at " + time);
                        }

                        for (int i = 1; i < noOfServers + 1; i++) {
                            if (waitingPeople[i] < minQueue) {
                                minQueue = waitingPeople[i];
                                minServer = i;
                           }
                        //    System.out.println(waitingPeople[i] +" waiting for server "+ i + " at " + time);

                        }
                        if (minQueue <= waitingSelfServers) {
                            for (int a = 1; a < maxQueue + 1; a++) {
                                if (minQueue == maxQueue) {
                                    break;
                                } else {
                                    if (waitingQueue[a][minServer] == 0) {
                                        pq.add(new Customer(time, id, greedy, "waits to be served by server ", minServer, 0));
                                        waitingQueue[a][minServer] = id;
                                        check2 = true;
                                        waitingTime[id] = time;
                                        waitingPeople[minServer]++;
                                        break;
                                    }
                                }
                            }
                        } else {
                            for(int b = noOfServers+1; b< noOfServers+noOfSelfServers+1; b++){
                                if(waitingPeople[b]<minSelfQueue){
                                    minSelfQueue=waitingPeople[b];
                               }
                            }
                            minServer=noOfServers+1;
                            for (int a = 1; a < maxQueue + 1; a++) {
                                if (minSelfQueue==maxQueue) {
                                    break;
                                } else {
                                    if (waitingQueue[a][minServer] == 0) {
                                        pq.add(new Customer(time, id, greedy, "waits to be served by self-check ", minServer, 0));
                                        waitingQueue[a][minServer] = id;
                                        check2 = true;
                                        waitingTime[id] = time;
                                        waitingPeople[minServer]++;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                } else if (noOfSelfServers == 0 && greedy == "(greedy)") {
                    if (!check) {
                        int minQueue = maxQueue;
                        int minServer = 0;
                        for (int i = 1; i < noOfServers + noOfSelfServers + 1; i++) {
                            if (waitingPeople[i] < minQueue) {
                                minQueue = waitingPeople[i];
                                minServer = i;
                            }
                        }
                        for (int a = 1; a < maxQueue + 1; a++) {
                            if(minQueue==maxQueue){
                                break;
                            }else {
                                if (waitingQueue[a][minServer] == 0) {
                                    pq.add(new Customer(time, id, greedy, "waits to be served by server ", minServer, 0));
                                    waitingQueue[a][minServer] = id;
                                    check2 = true;
                                    waitingTime[id] = time;
                                    waitingPeople[minServer]++;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (!check) {
                        for (int i = 1; i < noOfServers + 1; i++) {
                            for (int a = 1; a < maxQueue + 1; a++) {
                                if (waitingQueue[a][i] == 0) {
                                    pq.add(new Customer(time, id, greedy, "waits to be served by server ", i, 0));
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
                }

                if (noOfSelfServers > 0) {
                    if ((!check && !check1) && !check2) {
                        pq.add(new Customer(time, id, greedy,"leaves", 0, 0));
                        leaves++;
                    }
                } else {
                    if (!check && !check2) {
                        pq.add(new Customer(time, id,greedy, "leaves", 0, 0));
                        leaves++;
                    }

                }
            }

            if (activity.contains("served by server ") && !activity.contains("waits")) {
                double serving = list1.get(servingNo).doubleValue();
                servingNo++;
                servingDuration[id][server] = serving;
                pq.add(new Customer(time + servingDuration[id][server], id, greedy,"done serving by server ", server,
                        servingDuration[id][server]));

                if(id == waitingQueue[1][server]){
                    for (int i = 1; i < maxQueue; i++) {
                    waitingQueue[i][server] = waitingQueue[i + 1][server];
                    }
                    waitingQueue[maxQueue][server] = 0;
                    waitingPeople[server]--;
                }
            }

            if (activity.contains("served by self-check ") && !activity.contains("waits")) {
                double serving = list1.get(servingNo).doubleValue();
                servingNo++;
                servingDuration[id][server] = serving;
                pq.add(new Customer(time + servingDuration[id][server], id,greedy, "done serving by self-check ", server,
                        servingDuration[id][server]));


                }



            if(activity.contains("waits to be served by server")){
                if(servingCust[server]==0) {
                    pq.add(new Customer(restingTime[server], id, greedy,"served by server ", server, 0));
                    servingCust[server]++;
                    totalWait += (restingTime[server] - waitingTime[id]);
                }
            }

            if (activity.contains("done serving by server ")) {
                double prob = list3.get(manualServed).doubleValue();
                manualServed++;
                served++;

                if (prob < probability) {
                    double restingDuration = list2.get(dontRest).doubleValue();
                    dontRest++;
                    restingTime[server] = restingDuration + time;
                    restingServer[server]++;

                    if (waitingQueue[1][server] != 0) {
                        if(greedyCust[waitingQueue[1][server]]==1){
                            pq.add(new Customer(restingTime[server], waitingQueue[1][server],"(greedy)",
                                    "served by server ", server, servingDuration[waitingQueue[1][server]][server]));
                        }else{
                            pq.add(new Customer(restingTime[server], waitingQueue[1][server],"",
                                    "served by server ", server, servingDuration[waitingQueue[1][server]][server]));
                        }
                        totalWait += (restingTime[server] - waitingTime[waitingQueue[1][server]]);
                    } else {
                        servingCust[server]--;
                    }
                } else {
                    if (waitingQueue[1][server] != 0) {
                        if (greedyCust[waitingQueue[1][server]] == 1) {
                            pq.add(new Customer(time, waitingQueue[1][server], "(greedy)", "served by server "
                                    , server, servingDuration[waitingQueue[1][server]][server]));
                        } else {
                            pq.add(new Customer(time, waitingQueue[1][server], "", "served by server ", server,
                                    servingDuration[waitingQueue[1][server]][server]));
                        }
                        totalWait += (time - waitingTime[waitingQueue[1][server]]);


                    }else {
                        servingCust[server]--;
                    }
                }
            }

            if (activity.contains("done serving by self-check ")) {
                if(waitingQueue[1][server] !=0 && servingCust[server+1]>0) {
                    if (greedyCust[waitingQueue[1][server]] == 1) {
                        pq.add(new Customer(time, waitingQueue[1][server], "(greedy)",
                                "served by self-check ", server, servingDuration[waitingQueue[1][server]][server]));
                    } else {
                        pq.add(new Customer(time, waitingQueue[1][server], "", "served by self-check ", server,
                                servingDuration[waitingQueue[1][server]][server]));
                    }
                    totalWait += (time - waitingTime[waitingQueue[1][server]]);
                    for (int i = 1; i < maxQueue; i++) {
                        waitingQueue[i][server] = waitingQueue[i + 1][server];
                    }
                    waitingQueue[maxQueue][server] = 0;
                    waitingPeople[server]--;
                }else if(waitingQueue[1][server-1] != 0 && server != noOfServers+1) {
                    if (greedyCust[waitingQueue[1][server-1]] == 1) {
                        pq.add(new Customer(time, waitingQueue[1][server - 1], "(greedy)",
                                "served by self-check ", server, servingDuration[waitingQueue[1][server - 1]][server]));
                    }else {
                        pq.add(new Customer(time, waitingQueue[1][server - 1], "", "served by self-check ", server,
                                servingDuration[waitingQueue[1][server - 1]][server]));
                    }
                    totalWait += (time - waitingTime[waitingQueue[1][server - 1]]);
                    for (int i = 1; i < maxQueue; i++) {
                        waitingQueue[i][server - 1] = waitingQueue[i + 1][server - 1];
                        }
                        waitingQueue[maxQueue][server - 1] = 0;
                        waitingPeople[server - 1]--;
                    } else {
                        servingCust[server]--;
                    }
                served++;
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































































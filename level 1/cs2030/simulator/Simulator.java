package cs2030.simulator;
import java.util. ArrayList;

public class Simulator {


    public static ArrayList<Double> getServiceTime(int seed, double arrivalRate, double serviceRate, int customers) {
        ArrayList<Double> list = new ArrayList<>();
        RandomGenerator RG = new RandomGenerator(seed, arrivalRate, serviceRate, 0);
        for (int i = 0; i < customers; i++) {
            double timing = RG.genServiceTime();
            list.add((Double)timing);
        }
        return list;
    }

    public static  ArrayList<Double> getArrivalTime(int seed, double arrivalRate, double serviceRate,int customers) {
        ArrayList<Double> list1 = new ArrayList<>();
        RandomGenerator RG = new RandomGenerator(seed, arrivalRate, serviceRate, 0);
        for (int i = 0; i < customers; i++) {
            double timing = RG.	genInterArrivalTime();
            list1.add((Double)timing);
        }
        return list1;
    }
}
package cs2030.simulator;
import java.util. ArrayList;

public class Simulator {


    public static ArrayList<Double> getServiceTime(int seed, double arrivalRate, double serviceRate, double restingRate,
                                                   int customers) {
        ArrayList<Double> list = new ArrayList<>();
        RandomGenerator RG = new RandomGenerator(seed, arrivalRate, serviceRate, restingRate);
        for (int i = 0; i < customers; i++) {
            double timing = RG.genServiceTime();
            list.add((Double) timing);
        }
        return list;
    }

    public static ArrayList<Double> getArrivalTime(int seed, double arrivalRate, double serviceRate, double restingRate,
                                                   int customers) {
        ArrayList<Double> list1 = new ArrayList<>();
        RandomGenerator RG = new RandomGenerator(seed, arrivalRate, serviceRate, restingRate);
        for (int i = 0; i < customers; i++) {
            double timing = RG.genInterArrivalTime();
            list1.add((Double) timing);
        }
        return list1;
    }

    public static ArrayList<Double> getProbability(int seed, double arrivalRate, double serviceRate, double restingRate,
                                                   int customers) {
        ArrayList<Double> list2 = new ArrayList<>();
        RandomGenerator RG = new RandomGenerator(seed, arrivalRate, serviceRate, restingRate);
        for (int i = 0; i < customers; i++) {
            double timing = RG.genRandomRest();
            list2.add((Double) timing);
        }
        return list2;
    }

    public static ArrayList<Double> getResting (int seed, double arrivalRate, double serviceRate, double restingRate,
                                                int customers) {
        ArrayList<Double> list3 = new ArrayList<>();
        RandomGenerator RG = new RandomGenerator(seed, arrivalRate, serviceRate, restingRate);
        for (int i = 0; i < customers; i++) {
            double timing = RG.genRestPeriod();
            list3.add((Double) timing);
        }
        return list3;
    }

    public static ArrayList<Double> getGreedyRate(int seed, double arrivalRate, double serviceRate, double restingRate,
                                              int customers) {
        ArrayList<Double> list4 = new ArrayList<>();
        RandomGenerator RG = new RandomGenerator(seed, arrivalRate, serviceRate, restingRate);
        for (int i = 0; i < customers; i++) {
            double timing = RG.genCustomerType();
            list4.add((Double) timing);
        }
        return list4;
    }
}







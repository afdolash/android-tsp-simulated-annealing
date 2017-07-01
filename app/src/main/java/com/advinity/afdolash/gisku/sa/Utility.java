package com.advinity.afdolash.gisku.sa;

import java.util.Random;

/**
 * Created by Afdolash on 7/1/2017.
 */

public class Utility {
    /**
     * Computes and returns the Euclidean distance between two cities
     * @param city1 the first city
     * @param city2 the second city
     * @return distance the dist between city1 and city2
     */
    public static double distance(City city1, City city2){
        double xDistance = Math.abs(city1.getX() - city2.getX());
        double yDistance = Math.abs(city1.getY() - city2.getY());
        double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

        return distance;
    }

    /**
     * Calculates the acceptance probability
     * @param currentDistance the total distance of the current tour
     * @param newDistance the total distance of the new tour
     * @param temperature the current temperature
     * @return value the probability of whether to accept the new tour
     */
    public static double acceptanceProbability(double currentDistance, double newDistance, double temperature) {
        // If the new solution is better, accept it
        if (newDistance < currentDistance) {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((currentDistance - newDistance) / temperature);
    }

    /**
     * this method returns a random number n such that
     * 0.0 <= n <= 1.0
     * @return random such that 0.0 <= random <= 1.0
     */
    public static double randomDouble()
    {
        Random r = new Random();
        return r.nextInt(1000) / 1000.0;
    }

    /**
     * returns a random int value within a given range
     * min inclusive .. max not inclusive
     * @param min the minimum value of the required range (int)
     * @param max the maximum value of the required range (int)
     * @return rand a random int value between min and max [min,max)
     */
    public static int randomInt(int min , int max) {
        Random r = new Random();
        double d = min + r.nextDouble() * (max - min);
        return (int)d;
    }
}
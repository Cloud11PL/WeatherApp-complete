package sample;

import java.util.ArrayList;

/**
 * Class containing methods for calculating parameters of the data.
 */
public class UICalculations {
    /**
     * Method calculates the standard deviation based on Y Axis data
     * @param oyData ArrayList that consists of Y Axis data - temperature
     * @return Standard Deviation
     */
    public double getStDeviation(ArrayList<Double> oyData){

        double sum = 0;
        for(double e : oyData){
            sum += e;
        }
        double mean = sum/oyData.size();
        double temp = 0;

        for (int i = 0; i < oyData.size(); i++)
        {
            double val = oyData.get(i);
            double squrDiffToMean = Math.pow(val - mean, 2);
            temp += squrDiffToMean;
        }
        double meanOfDiffs = temp / (double) (oyData.size());
        return Math.sqrt(meanOfDiffs);
    }
}

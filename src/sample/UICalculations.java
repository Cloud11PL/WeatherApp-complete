package sample;

import java.util.ArrayList;
import java.util.Optional;

public class UICalculations {
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

    public Optional<Double> getMinValue(ArrayList<Double> oyData){
        return oyData.stream().min(Double::compareTo);
    }
}

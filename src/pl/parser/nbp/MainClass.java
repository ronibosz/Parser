/**
 * @author Bartosz Roszko
 * @date 22-09-2016
 * 
 * Program parses data from http://nbp.pl/kursy/xml/ and prints mean of bid rates and standard deviation of ask rates.
 * Input format: "currency, dateFrom, dateTo"
 * e.i. EUR, 2015-05-13, 2015-05-19
 */

package pl.parser.nbp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainClass {
	
	public static void main(String[] args){
		
		Rates rates = new Rates(args[0], args[1], args[2]);

		List<Double> bidRatesList = new ArrayList<>();
		List<Double> askRatesList = new ArrayList<>();
		
		bidRatesList = rates.getBidRates();
		askRatesList = rates.getAskRates();
		
		Double bidMean = getMean(bidRatesList);
		Double askStandardDeviation = getStandardDeviation(askRatesList);
		
		DecimalFormat decimalFormat = new DecimalFormat("#.####");
		
		System.out.println(decimalFormat.format(bidMean));
		System.out.println(decimalFormat.format(askStandardDeviation));
	}
	
	public static Double getMean(List<Double> valuesList){
		
		Double sum = 0d;
		
		for(int i=0; i<valuesList.size(); i++ )
			sum += valuesList.get(i);
		
		Double mean = sum/valuesList.size();		
		
		return mean;
	}
	
	public static Double getStandardDeviation(List<Double> valuesList){
		
		Double mean = getMean(valuesList);
		
		Double sumPow = 0d;
		
		for(int i=0; i<valuesList.size(); i++ )
			sumPow += Math.pow(valuesList.get(i) - mean, 2);
		
		Double standardDeviation = Math.sqrt(sumPow/valuesList.size());
		
		return standardDeviation;
	}
}

package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;

public class DataPoint {

	public Double averagePrecision=0.0;
	public Double averageRecall=0.0;
	public Double elapsedTime=0.0;
	public Double totalWorkers=0.0;
	public Double totalAnswers=0.0;
	public Double faultsLocated=0.0;
	public Double falsePositives=0.0;
	public Double falseNegatives=0.0;
	public Double truePositives=0.0;
	public Double trueNegatives=0.0;
	public Double numberOfOutcomes=0.0;
	public String subcrowdName;
	
	public Double averageTruePositiveLinesCount;
	public Double averageFalsePositiveLinesCount;
	public Double averageNearPositiveLinesCount;
	public Double averageFalseNegativeLinesCount;
	public Double averageTrueNegativeLinesCount;
	
	// All lines selected under each category <line number, number of times it was selected> 
	public HashMap<String, Integer> truePositiveLineMap;
	public HashMap<String, Integer> nearPositiveLineMap;
	public HashMap<String, Integer> falsePositiveLineMap;
	public HashMap<String, Integer> falseNegativeLineMap;
	public HashMap<String, Integer> trueNegativeLineMap;
	
	private static String[] header = { "average Precision", "average Recall", "elapsed Time", "total Workers", 
			"total Answers", "faults Located", "true Positives", "true Negatives", 
			 "false Positives", "false Negatives", "number of Outcomes Simulated"} ;
	
	public DataPoint(){}
	
	public DataPoint(String subcrowdName){
		this.subcrowdName = subcrowdName;
	}

	public HashMap<String, Outcome> fileNameOutcomeMap = new HashMap<String, Outcome>();

	public void computeAverages(){

		ArrayList<Double> precisionValues = new ArrayList<Double>();
		ArrayList<Double> recallValues = new ArrayList<Double>();

		for(String key: fileNameOutcomeMap.keySet()){
			Outcome outcome = fileNameOutcomeMap.get(key);
			precisionValues.add(outcome.precision);
			recallValues.add(outcome.recall);
			
			if(outcome.precision!=0){
				faultsLocated++;
			}
			
			falsePositives = falsePositives + outcome.falsePositives;
			falseNegatives = falseNegatives + outcome.falseNegatives;
			truePositives = truePositives + outcome.truePositives;
			trueNegatives = trueNegatives + outcome.trueNegatives;
			
		
			this.truePositiveLineMap = addLines(outcome.truePositiveLines);
			this.nearPositiveLineMap = addLines(outcome.nearTruePositiveFaultyLines);
			this.falsePositiveLineMap = addLines(outcome.falsePositiveLines);
			this.falseNegativeLineMap = addLines(outcome.falseNegativeLines);
			this.trueNegativeLineMap = addLines(outcome.trueNegativeLines);
		}
		numberOfOutcomes = new Double(fileNameOutcomeMap.size());
		averagePrecision = average(precisionValues);
		averageRecall = average(recallValues);
		
		
	}

	private HashMap<String, Integer> addLines(
			HashMap<String, Integer> truePositiveLines) {
		// TODO Auto-generated method stub
		return null;
	}

	private Double average(ArrayList<Double> values){

		Double total = 0.0;
		for(int i=0; i<values.size();i++){
			total = total + values.get(i);
		}
		return total/values.size();
	}
	
	/**
	 * 
	 * @param suffix necessary to identify the type of predictor that produced this datapoint
	 * @return
	 */
	public static String getHeader(String suffix){
		
		StringBuffer titles=new StringBuffer();
		for(String label: header){
			titles.append(label);
			titles.append(suffix);
			titles.append(",");
		}
		return titles.toString();
	}
	
	public String toString(){
		return this.averagePrecision+","+this.averageRecall+","+this.elapsedTime+","+this.totalWorkers+","+
				this.totalAnswers+","+this.faultsLocated+","+this.truePositives+","+this.trueNegatives+","+
				this.falsePositives+","+this.falseNegatives+","+this.numberOfOutcomes;
	}

	/** Selects the intersection of lines from two DataPoints and recalculates all metrics 
	 * @return a new data point representing the intersection the two data points
	 * */
	public static DataPoint combineConsensusDataPoint(
			DataPoint acrossQuestionsDataPoint,
			DataPoint withinQuestionDataPoint) {
		// TODO Auto-generated method stub
		return null;
	}


}


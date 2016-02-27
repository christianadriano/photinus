package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

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
	
	public Integer truePositiveLinesCount;
	public Integer falsePositiveLinesCount;
	public Integer nearPositiveLinesCount;
	public Integer falseNegativeLinesCount;
	public Integer trueNegativeLinesCount;
	
	public Double LinesPrecision;
	public Double LinesRecall;
	
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

		ArrayList<Double> precisionValueList = new ArrayList<Double>();
		ArrayList<Double> recallValueList = new ArrayList<Double>();

		for(String key: fileNameOutcomeMap.keySet()){
			Outcome outcome = fileNameOutcomeMap.get(key);
			precisionValueList.add(outcome.precision);
			recallValueList.add(outcome.recall);
			
			if(outcome.precision!=0){
				faultsLocated++;
			}
			
			falsePositives = falsePositives + outcome.falsePositives;
			falseNegatives = falseNegatives + outcome.falseNegatives;
			truePositives = truePositives + outcome.truePositives;
			trueNegatives = trueNegatives + outcome.trueNegatives;
			
			this.truePositiveLineMap = addLines(this.truePositiveLineMap, outcome.truePositiveLines);
			this.nearPositiveLineMap = addLines(this.nearPositiveLineMap, outcome.nearTruePositiveFaultyLines);
			this.falsePositiveLineMap = addLines(this.falsePositiveLineMap, outcome.falsePositiveLines);
			this.falseNegativeLineMap = addLines(this.falseNegativeLineMap, outcome.falseNegativeLines);
			this.trueNegativeLineMap = addLines(this.trueNegativeLineMap, outcome.trueNegativeLines);
			
			
		}
		this.truePositiveLinesCount = this.truePositiveLineMap.size();
		this.trueNegativeLinesCount = this.trueNegativeLineMap.size();
		this.falsePositiveLinesCount = this.falsePositiveLineMap.size();
		this.nearPositiveLinesCount = this.nearPositiveLineMap.size();
		this.falseNegativeLinesCount = this.falseNegativeLineMap.size();
		
		this.LinesPrecision = new Integer(this.truePositiveLinesCount / (this.truePositiveLinesCount + this.falsePositiveLinesCount)).doubleValue();
		this.LinesRecall =  new Integer (this.truePositiveLinesCount / this.truePositiveLinesCount + this.falseNegativeLinesCount)).doubleValue();
		
		numberOfOutcomes = new Double(fileNameOutcomeMap.size());
		averagePrecision = average(precisionValueList);
		averageRecall = average(recallValueList);
			
	}

	private HashMap<String, Integer> addLines(HashMap<String, Integer> destinationMap,
			HashMap<String, Integer> sourceMap) {
		
		for(Entry<String, Integer> entity : sourceMap.entrySet() ){
			String key = entity.getKey();
			if(destinationMap.containsKey(key)){
				  Integer  destinationCount = destinationMap.get(key);
				  Integer sourceCount = sourceMap.get(key);
				  destinationCount = destinationCount + sourceCount;
				  destinationMap.put(key, destinationCount);
			}
			else{
				destinationMap.put(key, entity.getValue());
			}	
		}
		return destinationMap;
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
	public static DataPoint combineConsensusDataPoints(DataPoint dataPoint_A,DataPoint dataPoint_W) {
		
		DataPoint combinedDataPoint =  new DataPoint("Combined");
		
		combinedDataPoint.falseNegativeLineMap = intersectionMap(dataPoint_A.falseNegativeLineMap,dataPoint_W.falseNegativeLineMap);
		combinedDataPoint.falsePositiveLineMap = intersectionMap(dataPoint_A.falsePositiveLineMap,dataPoint_W.falsePositiveLineMap);
		combinedDataPoint.truePositiveLineMap = intersectionMap(dataPoint_A.truePositiveLineMap,dataPoint_W.truePositiveLineMap);
		combinedDataPoint.nearPositiveLineMap = intersectionMap(dataPoint_A.nearPositiveLineMap,dataPoint_W.nearPositiveLineMap);
		combinedDataPoint.trueNegativeLineMap = intersectionMap(dataPoint_A.trueNegativeLineMap,dataPoint_W.trueNegativeLineMap);
		
		return combinedDataPoint;
	}


	private static HashMap<String,Integer> intersectionMap(HashMap<String,Integer> map_A, HashMap<String,Integer> map_B){
		
		HashMap<String,Integer> intersectionMap = new HashMap<String,Integer>();
		
		for(Entry<String,Integer> entry : map_A.entrySet()){
			String key = entry.getKey();
			if(map_B.containsKey(key)){
				Integer valueA = map_A.get(key);
				Integer valueB = map_B.get(key);
				intersectionMap.put(key, valueA+valueB);
			}
		}
		return intersectionMap;	
	}
	
}


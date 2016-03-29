package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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
	public Integer numberOfOutcomes=0;
	public String subcrowdName;
	public String consensusType; //across-questions, within-question, combined

	public Integer truePositiveLinesCount;
	public Integer falsePositiveLinesCount;
	public Integer nearPositiveLinesCount;
	public Integer falseNegativeLinesCount;

	public Double LinesPrecision;
	public Double LinesRecall;
	
	// All lines selected under each category <line number, number of times it was selected> 
	public HashMap<String, Integer> truePositiveLineMap=new HashMap<String, Integer>();
	public HashMap<String, Integer> nearPositiveLineMap=new HashMap<String, Integer>();
	public HashMap<String, Integer> falsePositiveLineMap=new HashMap<String, Integer>();
	public HashMap<String, Integer> falseNegativeLineMap=new HashMap<String, Integer>();

	public Integer correct_YES=0;
	public Integer correct_NO=0;
	public Integer total_YES_NO=0;
	
	private static String[] header = { 
		"consensus type", 
		"average Precision", 
		"average Recall",
		"elapsed Time", 
		"total Workers", 
		"total Answers", 
		"faults Located", 
		"true Positives", 
		"false Positives", 
		"false Negatives",		
		"true Negatives", 
		"#true positive line count", "true positive lines",
		"#near positive line count", "near positive lines",
		"#false positive line count", "false positive lines",
		"#false negative line count", "false negative lines",
		"line precision", "line recall"} ;


	public DataPoint(){}

	public DataPoint(String subcrowdName, String consensusType){
		this.subcrowdName = subcrowdName;
		this.consensusType = consensusType;
	}

	public HashMap<String, Outcome> fileNameOutcomeMap = new HashMap<String, Outcome>();

	public void computeAverages(){

		ArrayList<Double> precisionValueList = new ArrayList<Double>();
		ArrayList<Double> recallValueList = new ArrayList<Double>();

		ArrayList<Double> precision_LineValueList = new ArrayList<Double>();
		ArrayList<Double> recall_LineValueList = new ArrayList<Double>();

		for(String key: fileNameOutcomeMap.keySet()){
			Outcome outcome = fileNameOutcomeMap.get(key);
			precisionValueList.add(outcome.precision);
			recallValueList.add(outcome.recall);
			
			if(this.totalWorkers < outcome.differentWorkersAmongHITs)
				this.totalWorkers =  new Integer(outcome.differentWorkersAmongHITs).doubleValue();
			
			if(this.totalAnswers < outcome.totalAnswersObtained)
				this.totalAnswers =  new Integer(outcome.totalAnswersObtained).doubleValue();

			if(outcome.faultLocated){
				this.faultsLocated++;
			}
			
			if(fileNameOutcomeMap.size()>8){
				System.out.println("fileNameOutcomeMap>8");
			}
			
			falsePositives = falsePositives + outcome.falsePositives;
			falseNegatives = falseNegatives + outcome.falseNegatives;
			truePositives = truePositives + outcome.truePositives;
			trueNegatives = trueNegatives + outcome.trueNegatives;

			this.truePositiveLineMap = addLines(this.truePositiveLineMap, outcome.truePositiveLines, key);
			this.nearPositiveLineMap = addLines(this.nearPositiveLineMap, outcome.nearPositiveLines, key);
			this.falsePositiveLineMap = addLines(this.falsePositiveLineMap, outcome.falsePositiveLines, key);
			this.falseNegativeLineMap = addLines(this.falseNegativeLineMap, outcome.falseNegativeLines, key);

			Double precisionLine = 0.0;
			Double recallLine = 0.0;
			if(outcome.truePositiveLines.size()>0){
				precisionLine = new Integer(outcome.truePositiveLines.size() /(outcome.truePositiveLines.size() + outcome.falsePositiveLines.size())).doubleValue();
				recallLine = new Integer(outcome.truePositiveLines.size() /(outcome.truePositiveLines.size() + outcome.falseNegativeLines.size())).doubleValue();
			}

			precision_LineValueList.add(precisionLine);
			recall_LineValueList.add(recallLine);
			
			this.correct_YES = this.correct_YES + outcome.correct_YES_Answers;
			this.correct_NO = this.correct_NO + outcome.correct_NO_Answers;
			this.total_YES_NO = this.total_YES_NO + outcome.total_YESNO_Answers;
		}
		
		this.truePositiveLinesCount = this.truePositiveLineMap.size();
		this.falsePositiveLinesCount = this.falsePositiveLineMap.size();
		this.nearPositiveLinesCount = this.nearPositiveLineMap.size();
		this.falseNegativeLinesCount = this.falseNegativeLineMap.size();

		this.LinesPrecision = average(precision_LineValueList);
		this.LinesRecall =  average(recall_LineValueList);
		this.numberOfOutcomes =  fileNameOutcomeMap.size();
		this.averagePrecision = average(precisionValueList);
		this.averageRecall = average(recallValueList);
	}

	private HashMap<String, Integer> addLines(HashMap<String, Integer> destinationMap,
			HashMap<String, Integer> sourceMap, String fileName) {

		for(Entry<String, Integer> entity : sourceMap.entrySet() ){
			String key = entity.getKey();
			if(destinationMap.containsKey(key)){
				Integer  destinationCount = destinationMap.get(key);
				Integer sourceCount = sourceMap.get(key);
				destinationCount = destinationCount + sourceCount;
				destinationMap.put(fileName+"-"+key, destinationCount);
			}
			else{
				destinationMap.put(fileName+"-"+key, entity.getValue());
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
		return  this.consensusType+","+
				this.averagePrecision+","+this.averageRecall+","+this.elapsedTime+","+this.totalWorkers+","+
				this.totalAnswers+","+this.faultsLocated+","+this.truePositives+","+
				this.falsePositives+","+this.falseNegatives+","+this.trueNegatives+","+
				this.truePositiveLinesCount+","+linesToString(this.truePositiveLineMap)+","+
				this.nearPositiveLinesCount+","+linesToString(this.nearPositiveLineMap)+","+
				this.falsePositiveLinesCount+","+linesToString(this.falsePositiveLineMap)+","+
				this.falseNegativeLinesCount+","+linesToString(this.falseNegativeLineMap)+","+
				this.LinesPrecision + "," + this.LinesRecall;
	}

	private String linesToString(HashMap<String,Integer> map){
		if(map==null)
			return "";
		else{
			Iterator<String> iter = map.keySet().iterator();
			String result="";
			while(iter.hasNext()){
				result = result+";"+iter.next();
			}
			return result;
		}
	}

	/** Selects the intersection of lines from two DataPoints and recalculates all metrics 
	 * @return a new data point representing the intersection the two data points
	 * */
	public static DataPoint combineConsensusDataPoints(DataPoint dataPoint_A,DataPoint dataPoint_W) {

		DataPoint combinedDataPoint =  new DataPoint(dataPoint_A.subcrowdName,"Combined");

		combinedDataPoint.falseNegativeLineMap = intersectionMap(dataPoint_A.falseNegativeLineMap,dataPoint_W.falseNegativeLineMap);
		combinedDataPoint.falsePositiveLineMap = intersectionMap(dataPoint_A.falsePositiveLineMap,dataPoint_W.falsePositiveLineMap);
		combinedDataPoint.truePositiveLineMap = intersectionMap(dataPoint_A.truePositiveLineMap,dataPoint_W.truePositiveLineMap);
		combinedDataPoint.nearPositiveLineMap = intersectionMap(dataPoint_A.nearPositiveLineMap,dataPoint_W.nearPositiveLineMap);

		//Do the same for each Java method	
		for(Entry<String,Outcome> entry : dataPoint_A.fileNameOutcomeMap.entrySet()){
			String key = entry.getKey();
			Outcome outcome_A = entry.getValue();
			Outcome outcome_B =  dataPoint_W.fileNameOutcomeMap.get(key);

			HashMap<String,Integer> truePositiveLines = intersectionMap(outcome_A.truePositiveLines, outcome_B.truePositiveLines);
			HashMap<String,Integer> nearPositiveLines = intersectionMap(outcome_A.nearPositiveLines, outcome_B.nearPositiveLines);
			HashMap<String,Integer> falsePositiveLines = intersectionMap(outcome_A.falsePositiveLines, outcome_B.falsePositiveLines);
			HashMap<String, Integer> falseNegativeLines = intersectionMap(outcome_A.falseNegativeLines, outcome_B.falseNegativeLines);

			Outcome combinedOutcome = new Outcome(
					new FilterCombination(),
					outcome_A.fileName,
					"Combined consensus",
					(outcome_A.faultLocated && outcome_B.faultLocated), 
					0,
					outcome_A.maxWorkerPerQuestion>outcome_B.maxWorkerPerQuestion ?outcome_B.maxWorkerPerQuestion: outcome_A.maxWorkerPerQuestion,
					outcome_A.totalAnswersObtained>outcome_B.totalAnswersObtained ?outcome_B.totalAnswersObtained: outcome_A.totalAnswersObtained,
					0,
					outcome_A.truePositives < outcome_B.truePositives ? outcome_A.truePositives : outcome_B.truePositives,
					outcome_A.trueNegatives < outcome_B.trueNegatives ? outcome_A.trueNegatives : outcome_B.trueNegatives,
					outcome_A.falsePositives < outcome_B.falsePositives ? outcome_A.falsePositives : outcome_B.falsePositives,
					outcome_A.falseNegatives < outcome_B.falseNegatives ? outcome_A.falseNegatives : outcome_B.falseNegatives,
					outcome_A.differentWorkersPerHIT>outcome_B.differentWorkersPerHIT ?outcome_B.differentWorkersPerHIT: outcome_A.differentWorkersPerHIT,
					outcome_A.differentWorkersAmongHITs>outcome_B.differentWorkersAmongHITs ?outcome_B.differentWorkersAmongHITs: outcome_A.differentWorkersAmongHITs,
					truePositiveLines,
					nearPositiveLines,
					falsePositiveLines,
					falseNegativeLines);


			combinedDataPoint.fileNameOutcomeMap.put(combinedOutcome.fileName, combinedOutcome);
		}	
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

	public Integer getTotalLinesToInspect() {
		return this.falsePositiveLinesCount+this.nearPositiveLinesCount+this.truePositiveLinesCount;
	}



}


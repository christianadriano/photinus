package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.report.predictive.FilterCombination;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;;
/**
 *  Outcome of a crowd consensus 
 * 
 * @author adrianoc
 * @see Predictor, WithinQuestionConsensus, AcrossQuestionsConsensus
 */
public class Outcome {

	public FilterCombination filter;

	public String fileName;

	public String predictorType;

	public Boolean faultLocated;

	public Integer signalStrength;

	/** Maximum different workers per question for this HIT */
	public Integer maxWorkerPerQuestion;

	/** All the YES, NO, IDK for all different questions in the same HIT */
	public Integer totalAnswersObtained;

	/** Minimal number of YES's (has different definitions for WithinQuestionConsensus and AcrossQuestionsConsensus  */
	public Integer threshold; 

	public Integer truePositives;

	public Integer trueNegatives;

	public Integer falsePositives;	

	public Integer falseNegatives;


	//Lines counts per consensus outcome
	public HashMap<String,Integer> allLines;
	public HashMap<String,Integer> trueNegativeLines;
	public HashMap<String,Integer> falseNegativeLines;
	public HashMap<String, Integer> truePositiveLines;
	public HashMap<String,Integer> falsePositiveLines;

	public HashMap<String, Integer> nearTruePositiveFaultyLines; //Only the true positive lines that are not actually faulty
	public HashMap<String, Integer> truePositiveFaultyLines; //Only the true positive lines that are actually faulty

	public Double precision;
	public Double recall;

	/** Total workers that contributed to one HIT after applying the combined filter */
	public Integer differentWorkersPerHIT;

	/** Total workers that remained after applying the combined filter */
	public Integer differentWorkersAmongHITs;

	public Outcome(FilterCombination filter, String fileName, String predictorType, Boolean faultLocated,
			Integer signalStrength, Integer maxWorkerPerQuestion, Integer totalAnswers, Integer threshold,
			Integer truePositives, Integer trueNegatives,
			Integer falsePositives, Integer falseNegatives,Integer differentWorkersPerHIT, Integer differentWorkersAmongHITs,
			HashMap<String,Integer> truePosiveFaultyLines, HashMap<String,Integer> truePosiveNearFaultyLines, 
			HashMap<String,Integer> falsePositiveLines  ) {
		super();
		this.filter = filter;
		this.fileName = new String(fileName.replace("HIT0", "J").replace("_","."));
		this.predictorType = predictorType;
		this.faultLocated = faultLocated;
		this.signalStrength = signalStrength;
		this.maxWorkerPerQuestion = maxWorkerPerQuestion;
		this.totalAnswersObtained = totalAnswers;
		this.threshold = threshold; 
		this.truePositives = truePositives;
		this.trueNegatives = trueNegatives;
		this.falsePositives = falsePositives;
		this.falseNegatives = falseNegatives;
		this.differentWorkersPerHIT = differentWorkersPerHIT;
		this.differentWorkersAmongHITs = differentWorkersAmongHITs;
		this.precision = this.computePrecision(this.truePositives, this.falsePositives);
		this.recall = this.computeRecall(this.truePositives, this.falseNegatives);
		this.falsePositiveLines =   (HashMap<String, Integer>) ((falsePositiveLines!=null) ? falsePositiveLines.clone() : new HashMap<String,QuestionLinesMap>());
		this.nearTruePositiveFaultyLines =  (HashMap<String, Integer>) ((truePosiveNearFaultyLines!=null) ? truePosiveNearFaultyLines.clone() : new HashMap<String,QuestionLinesMap>()); 
		this.truePositiveFaultyLines =   (HashMap<String, Integer>) ((truePosiveFaultyLines!=null) ? truePosiveFaultyLines.clone() : new HashMap<String,QuestionLinesMap>()); 
	}

	private Double computePrecision(int tp, int fp){
		Double tpD = new Double(tp);
		Double fpD =  new Double(fp);
		if((tpD+fpD) ==0) 
			return 0.0;
		else 
			return (tpD/(tpD+fpD))*100;
	}

	private Double computeRecall(int tp, int fn){
		Double tpD = new Double(tp);
		Double fnD =  new Double(fn);
		if((tpD+fnD) ==0) 
			return 0.0;
		else 
			return (tpD/(tpD+fnD))*100;
	}

	public static String getHeader(){

		String header =  "HIT:Consensus:Fault located?:"
				+ "True Positive Faulty Lines:" + "#True Positive Faulty Lines:"
				+ "Near Positive Faulty Lines:" + "#Near Positive Faulty Lines:"
				+ "False Positive Lines:"+ "#False Positive Lines:"
				+ "Signal strength:#Maximum workers per question:#Total answers obtained: #YES needed :"
				+"True positives:True negatives:False positives:False negatives:Different workers in HIT:"
				+"Different Workers among all HITs";
		return header;
	}

	public String toString(){
		
		String output = fileName +":"+ predictorType +":"+ faultLocated + 
				":"+ this.linesToString(this.truePositiveFaultyLines) + ":"+ this.truePositiveFaultyLines.size()+
				":"+ this.linesToString(this.nearTruePositiveFaultyLines) + ":"+ this.nearTruePositiveFaultyLines.size()+
				":"+ this.linesToString(this.falsePositiveLines) + ":"+ this.falsePositiveLines.size()+

				":"+ signalStrength +":"+ maxWorkerPerQuestion +":"+ totalAnswersObtained+
				":"+threshold +":"+	truePositives +":"+ trueNegatives +":"+ falsePositives +":"+ falseNegatives +":"+ differentWorkersPerHIT +
				":"+differentWorkersAmongHITs;
		return output;	
	}

	public String linesToString(HashMap<String,Integer> map){
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
}

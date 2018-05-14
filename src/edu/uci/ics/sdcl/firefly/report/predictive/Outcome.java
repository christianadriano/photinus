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

	public Double signalStrength;

	/** Maximum different workers per question for this HIT */
	public Double maxWorkerPerQuestion;

	/** All the YES, NO, IDK for all different questions in the same HIT */
	public Double totalAnswersObtained;

	/** Minimal number of YES's (has different definitions for WithinQuestionConsensus and AcrossQuestionsConsensus  */
	public Double threshold; 

	public Double truePositives;

	public Double trueNegatives;

	public Double falsePositives;	

	public Double falseNegatives;
	
	public Double correct_YES_Answers;

	public Double correct_NO_Answers;
	
	public Double total_YES_Answers;
	
	public Double total_NO_Answers;
	
	public Double 	total_IDK_Answers;
	
	public Double total_YESNO_Answers;
	
	public Double average_Total_Correct_Answers;
	
	public Double average_correctYES_Answers;
	
	public Double average_correctNO_Answers;
	

	//Lines counts per consensus outcome
  	public HashMap<String,Integer> falseNegativeLinesMap;
	public HashMap<String, Integer> truePositiveLinesMap;
	public HashMap<String, Integer> trueNegativeLinesMap;
	public HashMap<String,Integer> falsePositiveLinesMap;
	public HashMap<String, Integer> nearPositiveLinesMap; //Only the true positive lines that are not actually faulty

	public Double precision;
	public Double recall;

	/** Total workers that contributed to one HIT after applying the combined filter */
	public Double differentWorkersPerHIT;

	/** Total workers that remained after applying the combined filter */
	public Double differentWorkersAmongHITs;

	/** Maps which flagged lines came from which questions */
	public HashMap<String, HashMap<String, Integer>> questionMap;

	private int questionBelowMinimumAnswers;

	public Outcome(){}
	
	public Outcome(FilterCombination filter, String fileName, String predictorType, Boolean faultLocated,
			Double signalStrength, Double maxWorkerPerQuestion, Double totalAnswers, Double threshold,
			Double truePositives, Double trueNegatives,
			Double falsePositives, Double falseNegatives,Double differentWorkersPerHIT, Double differentWorkersAmongHITs,
			HashMap<String,Integer> truePositiveLines, HashMap<String,Integer> nearPositiveLines, 
			HashMap<String,Integer> falsePositiveLines, HashMap<String, Integer> trueNegativeLines,
			int questionsBelowMinimumAnswers,
			Double correctYES, Double correctNO, Double totalYES, Double totalNO,Double totalIDK) {
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
		this.falsePositiveLinesMap =   (HashMap<String, Integer>) ((falsePositiveLines!=null) ? falsePositiveLines.clone() : new HashMap<String,QuestionLinesMap>());
		this.nearPositiveLinesMap =  (HashMap<String, Integer>) ((nearPositiveLines!=null) ? nearPositiveLines.clone() : new HashMap<String,QuestionLinesMap>()); 
		this.truePositiveLinesMap =   (HashMap<String, Integer>) ((truePositiveLines!=null) ? truePositiveLines.clone() : new HashMap<String,QuestionLinesMap>()); 
		this.falseNegativeLinesMap =   new HashMap<String,Integer>(); //I am not computing the false negative lines.
		this.trueNegativeLinesMap = (HashMap<String, Integer>) ((trueNegativeLinesMap!=null) ? trueNegativeLinesMap.clone() : new HashMap<String,QuestionLinesMap>()); 
		this.questionBelowMinimumAnswers =   questionsBelowMinimumAnswers; 
		this.correct_YES_Answers = correctYES;
		this.correct_NO_Answers = correctNO;
		this.total_YES_Answers = totalYES;
		this.total_NO_Answers =  totalNO;
		this.total_IDK_Answers = totalIDK;
		this.total_YESNO_Answers = totalYES+ totalNO;
		this.average_correctYES_Answers = this.correct_YES_Answers.doubleValue() / this.total_YES_Answers.doubleValue();
		this.average_correctNO_Answers = this.correct_NO_Answers.doubleValue() / this.total_NO_Answers.doubleValue();
		this.average_Total_Correct_Answers = (this.correct_YES_Answers.doubleValue()+this.correct_NO_Answers.doubleValue())/this.total_YESNO_Answers.doubleValue();
	}


	private Double computePrecision(double tp, double fp){
		Double tpD = new Double(tp);
		Double fpD =  new Double(fp);
		if((tpD+fpD) ==0) 
			return 0.0;
		else 
			return (tpD/(tpD+fpD));
	}

	private Double computeRecall(double tp, double fn){
		Double tpD = new Double(tp);
		Double fnD =  new Double(fn);
		if((tpD+fnD) ==0) 
			return 0.0;
		else 
			return (tpD/(tpD+fnD));
	}

	public static String getHeader(){

		String header =  "HIT,Consensus,Fault located?,"
				+ "True Positive Lines," + "#True Positive Lines,"
				+ "Near Positive Lines," + "#Near Positive Lines,"
				+ "False Positive Lines,"+ "#False Positive Lines,"
				+ "Signal strength,#Maximum workers per question,#Total answers obtained, #YES needed ,"
				+"True positives,True negatives,False positives,False negatives,Different workers in HIT,"
				+"Different Workers among all HITs,Precision,Recall";
		return header;
	}
	
	public static String getHeaderAllLineTypes(){

		String header =  "HIT,Consensus,Fault located?,"
				+ "True Positive Lines," + "#True Positive Lines,"
				+ "Near Positive Lines," + "#Near Positive Lines,"
				+ "False Positive Lines,"+ "#False Positive Lines,"
				+ "Questions Below Minimum Answers,"
				+ "Signal strength,#Maximum workers per question,#Total answers obtained, #YES needed ,"
				+"True positives,True negatives,False positives,False negatives,Different workers in HIT,"
				+"Different Workers among all HITs,Precision,Recall";
		return header;
	}

	public static String getHeaderCorrectAnswers(){

		String header =  "HIT,Consensus,Fault located?,"
				+ "True Positive Lines," + "#True Positive Lines,"
				+ "Near Positive Lines," + "#Near Positive Lines,"
				+ "False Positive Lines,"+ "#False Positive Lines,"
				+ "Signal strength,#Maximum workers per question,#Total answers obtained, #YES needed ,"
				+"True positives,True negatives,False positives,False negatives,Different workers in HIT,"
				+"Different Workers among all HITs,Precision,Recall,"
				+"#Correct YES,#Correct NO, Total YES, Total NO, Total IDK, Total YES NO,Average correct YES, Average correct NO, Average total correct answers";
		
		return header;
	}

	
	public String toString(){
		
		String output = fileName +","+ predictorType +","+ faultLocated + 
				","+ this.linesToString(this.truePositiveLinesMap) + ","+ this.truePositiveLinesMap.size()+
				","+ this.linesToString(this.nearPositiveLinesMap) + ","+ this.nearPositiveLinesMap.size()+
				","+ this.linesToString(this.falsePositiveLinesMap) + ","+ this.falsePositiveLinesMap.size()+

				","+ signalStrength +","+ maxWorkerPerQuestion +","+ totalAnswersObtained+
				","+threshold +","+	truePositives +","+ trueNegatives +","+ falsePositives +","+ falseNegatives +","+ differentWorkersPerHIT +
				","+differentWorkersAmongHITs+","+this.precision+","+this.recall;
		return output;	
	}
	
	
	public String toStringAllLineTypes(){
		
		String output = fileName +","+ predictorType +","+ faultLocated + 
				","+ this.linesToString(this.truePositiveLinesMap) + ","+ this.truePositiveLinesMap.size()+
				","+ this.linesToString(this.nearPositiveLinesMap) + ","+ this.nearPositiveLinesMap.size()+
				","+ this.linesToString(this.falsePositiveLinesMap) + ","+ this.falsePositiveLinesMap.size()+
				","+ this.questionBelowMinimumAnswers+

				","+ signalStrength +","+ maxWorkerPerQuestion +","+ totalAnswersObtained+
				","+threshold +","+	truePositives +","+ trueNegatives +","+ falsePositives +","+ falseNegatives +","+ differentWorkersPerHIT +
				","+differentWorkersAmongHITs+","+this.precision+","+this.recall;
		return output;	
	}
	
	
	
	public String toStringCorrectAnswers(){
		
		String output = fileName +","+ predictorType +","+ faultLocated + 
				","+ this.linesToString(this.truePositiveLinesMap) + ","+ this.truePositiveLinesMap.size()+
				","+ this.linesToString(this.nearPositiveLinesMap) + ","+ this.nearPositiveLinesMap.size()+
				","+ this.linesToString(this.falsePositiveLinesMap) + ","+ this.falsePositiveLinesMap.size()+

				","+ signalStrength +","+ maxWorkerPerQuestion +","+ totalAnswersObtained+
				","+threshold +","+	truePositives +","+ trueNegatives +","+ falsePositives +","+ falseNegatives +","+ differentWorkersPerHIT +
				","+differentWorkersAmongHITs+","+this.precision+","+this.recall+
				","+this.correct_YES_Answers+","+this.correct_NO_Answers+
				","+this.total_YES_Answers+","+this.total_NO_Answers+","+this.total_IDK_Answers+","+this.total_YESNO_Answers+
				","+this.average_correctYES_Answers+","+this.average_correctNO_Answers+","+this.average_Total_Correct_Answers;
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

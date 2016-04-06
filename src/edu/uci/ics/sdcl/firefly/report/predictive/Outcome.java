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
	
	public Integer correct_YES_Answers;

	public Integer correct_NO_Answers;
	
	public Integer total_YES_Answers;
	
	public Integer total_NO_Answers;
	
	public Integer 	total_IDK_Answers;
	
	public Integer total_YESNO_Answers;
	
	public Double average_Total_Correct_Answers;
	
	public Double average_correctYES_Answers;
	
	public Double average_correctNO_Answers;
	

	//Lines counts per consensus outcome
  	public HashMap<String,Integer> falseNegativeLines;
	public HashMap<String, Integer> truePositiveLines;
	public HashMap<String,Integer> falsePositiveLines;

	public HashMap<String, Integer> nearPositiveLines; //Only the true positive lines that are not actually faulty

	public Double precision;
	public Double recall;

	/** Total workers that contributed to one HIT after applying the combined filter */
	public Integer differentWorkersPerHIT;

	/** Total workers that remained after applying the combined filter */
	public Integer differentWorkersAmongHITs;

	/** Maps which flagged lines came from which questions */
	public HashMap<String, HashMap<String, Integer>> questionMap;

	public Outcome(){}
	
		
	public Outcome(FilterCombination filter, String fileName, String predictorType, Boolean faultLocated,
			Integer signalStrength, Integer maxWorkerPerQuestion, Integer totalAnswers, Integer threshold,
			Integer truePositives, Integer trueNegatives,
			Integer falsePositives, Integer falseNegatives,Integer differentWorkersPerHIT, Integer differentWorkersAmongHITs,
			HashMap<String,Integer> truePositiveLines, HashMap<String,Integer> nearPositiveLines, 
			HashMap<String,Integer> falsePositiveLines, HashMap<String, Integer> falseNegativeLines,
			Integer correctYES, Integer correctNO, Integer totalYES, Integer totalNO,Integer totalIDK) {
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
		this.nearPositiveLines =  (HashMap<String, Integer>) ((nearPositiveLines!=null) ? nearPositiveLines.clone() : new HashMap<String,QuestionLinesMap>()); 
		this.truePositiveLines =   (HashMap<String, Integer>) ((truePositiveLines!=null) ? truePositiveLines.clone() : new HashMap<String,QuestionLinesMap>()); 
		this.falseNegativeLines =   (HashMap<String, Integer>) ((falseNegativeLines!=null) ? falseNegativeLines.clone() : new HashMap<String,QuestionLinesMap>());
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

	private Double computePrecision(int tp, int fp){
		Double tpD = new Double(tp);
		Double fpD =  new Double(fp);
		if((tpD+fpD) ==0) 
			return 0.0;
		else 
			return (tpD/(tpD+fpD));
	}

	private Double computeRecall(int tp, int fn){
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
				","+ this.linesToString(this.truePositiveLines) + ","+ this.truePositiveLines.size()+
				","+ this.linesToString(this.nearPositiveLines) + ","+ this.nearPositiveLines.size()+
				","+ this.linesToString(this.falsePositiveLines) + ","+ this.falsePositiveLines.size()+

				","+ signalStrength +","+ maxWorkerPerQuestion +","+ totalAnswersObtained+
				","+threshold +","+	truePositives +","+ trueNegatives +","+ falsePositives +","+ falseNegatives +","+ differentWorkersPerHIT +
				","+differentWorkersAmongHITs+","+this.precision+","+this.recall;
		return output;	
	}
	
	
	public String toStringCorrectAnswers(){
		
		String output = fileName +","+ predictorType +","+ faultLocated + 
				","+ this.linesToString(this.truePositiveLines) + ","+ this.truePositiveLines.size()+
				","+ this.linesToString(this.nearPositiveLines) + ","+ this.nearPositiveLines.size()+
				","+ this.linesToString(this.falsePositiveLines) + ","+ this.falsePositiveLines.size()+

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

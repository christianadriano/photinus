package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;

public abstract class Consensus {

	public abstract String getName();
	
	public abstract void setCalibration(int calilbration);
	
	public abstract int getCalibration();
	
	public abstract void setData(AnswerData data);
	
	public abstract Integer computeThreshold(AnswerData data);
	
	public abstract Integer computeSignalStrength(AnswerData data);
	
	public abstract Integer computeNumberOfWorkers(AnswerData data);

	public abstract Integer getMinimumNumberYESAnswersThatLocatedFault();

	public abstract Integer getTruePositives();
	
	public abstract Integer getTrueNegatives();
	
	public abstract Integer getFalsePositives();
	
	public abstract Integer getFalseNegatives();

	public abstract HashMap<String, Integer> getTruePositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping);
		 
	public abstract HashMap<String, Integer> getTrueNegativeLines(
			HashMap<String, QuestionLinesMap> lineMapping);

	public abstract HashMap<String, Integer> getFalsePositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping);

	public abstract HashMap<String, Integer> getFalseNegativeLines(
			HashMap<String, QuestionLinesMap> lineMapping);
	
	public abstract HashMap<String, Integer> getNearPositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping);

	/**
	 *  Lines considered correctly positive cannot be considered again as False positives 
	 * 
	 * @param correctPositiveMap any lines that should be considered as correctly flagged as positive (e.g., True or Near positive lines)
	 * @param falsePositiveMap
	 * @return
	 */
	public static HashMap<String,Integer> removeFalsePositiveDuplications(HashMap<String, Integer> correctPositiveMap,
			HashMap<String, Integer> falsePositiveMap ){
		if(correctPositiveMap!=null && falsePositiveMap!=null){
			HashMap<String, Integer> revisedFalsePositiveMap = new HashMap<String,Integer>();
			for (Map.Entry<String, Integer> entry : falsePositiveMap.entrySet()) {
				if(!correctPositiveMap.containsKey(entry.getKey())){
					revisedFalsePositiveMap.put(entry.getKey(),entry.getValue());
				}
			}
			return revisedFalsePositiveMap;
		}
		else
			return falsePositiveMap;
	}

	public abstract HashMap<String, HashMap<String, Integer>> getNearPositiveLinesQuestions(
			 HashMap<String, QuestionLinesMap> lineMapping);
	
}

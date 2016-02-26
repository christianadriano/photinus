package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;

public abstract class Consensus {

	public abstract String getName();
	
	public abstract void setCalibration(int calilbration);
	
	public abstract int getCalibration();
	
	public abstract void setData(AnswerData data);
	
	public abstract Boolean computeSignal(AnswerData data);
	
	public abstract Integer computeSignalStrength(AnswerData data);
	
	public abstract Integer computeNumberOfWorkers(AnswerData data);

	public abstract Integer getThreshold();

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

	public abstract HashMap<String, Integer> getTruePositiveFaultyLines(
			HashMap<String, QuestionLinesMap> lineMapping);

	public abstract HashMap<String, Integer> getNearPositiveFaultyLines(
			HashMap<String, QuestionLinesMap> lineMapping);

	/**
	 *  Lines considered Near positive cannot be considered again False positives 
	 * 
	 * @param nearPositiveMap
	 * @param falsePositiveMap
	 * @return
	 */
	public static HashMap<String,Integer> removeFalsePositiveDuplications(HashMap<String, Integer> nearPositiveMap,
			HashMap<String, Integer> falsePositiveMap ){
		if(nearPositiveMap!=null && falsePositiveMap!=null){
			HashMap<String, Integer> revisedFalsePositiveMap = new HashMap<String,Integer>();
			for (Map.Entry<String, Integer> entry : falsePositiveMap.entrySet()) {
				if(!nearPositiveMap.containsKey(entry.getKey())){
					revisedFalsePositiveMap.put(entry.getKey(),entry.getValue());
				}
			}
			return revisedFalsePositiveMap;
		}
		else
			return falsePositiveMap;
	}
}

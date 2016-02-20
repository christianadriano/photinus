package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.HashMap;

import edu.uci.isc.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;

public abstract class Consensus {

	public abstract String getName();
	
	public abstract Boolean computeSignal(AnswerData data);
	
	public abstract Double computeSignalStrength(AnswerData data);
	
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

	
}

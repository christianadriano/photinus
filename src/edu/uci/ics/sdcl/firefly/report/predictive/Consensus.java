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

	public abstract HashMap<String, Integer> getTruePositiveLineCount(
			HashMap<String, QuestionLinesMap> lineMapping);
		 
	public abstract HashMap<String, Integer> getTrueNegativeLineCount(
			HashMap<String, QuestionLinesMap> lineMapping);

	public abstract HashMap<String, Integer> getFalsePositiveLineCount(
			HashMap<String, QuestionLinesMap> lineMapping);

	public abstract HashMap<String, Integer> getFalseNegativeLineCount(
			HashMap<String, QuestionLinesMap> lineMapping);

	public abstract HashMap<String, Integer> getTruePositiveFaultyLineCount(
			HashMap<String, QuestionLinesMap> lineMapping);

	public abstract HashMap<String, Integer> getTruePositiveNearFaultyLineCount(
			HashMap<String, QuestionLinesMap> lineMapping);

	
}

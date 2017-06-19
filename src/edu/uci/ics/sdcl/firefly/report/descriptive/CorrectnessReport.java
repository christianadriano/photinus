package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds a part of the DescriptiveReport.
 * This is the ORANGE part of the tables.
 * 
 * @author igMoreira
 *
 */
public abstract class CorrectnessReport {

	/**
	 * Holds the content of the ORANGE part of the report
	 */
	protected Map<String, List<String>> correctnessContent = new LinkedHashMap<String, List<String>>();
	
	/**
	 * CONSTRUCTOR
	 */
	public CorrectnessReport() {
		this.correctnessContent = new LinkedHashMap<String, List<String>>();
	}
	
	public Map<String, List<String>> getContent() {
		return correctnessContent;
	}
	
	public abstract String getType();


	/**
	 * Filters the content and returns the HashMap containing
	 * the Headers and the contents of the table.
	 * @param filter 
	 * @return: The map containing the columns and values of the table
	 */
	public abstract Map<String, List<String>> generateReport(HeaderReport headerReport, AnswerReport answerReport, FilterCombination filter);
}

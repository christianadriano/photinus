package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * This class holds a part of the DescriptiveReport.
 * This is the YELLOW part of the tables.
 * 
 * @author igMoreira
 *
 */
public abstract class CountReport {

	/**
	 * Holds the content of the ORANGE part of the report
	 */
	protected Map<String, List<String>> countContent = new LinkedHashMap<String, List<String>>();
	
	/**
	 * CONSTRUCTOR
	 */
	public CountReport() {
	}
	
	public Map<String, List<String>> getContent()
	{
		return this.countContent;
	}
	
	/**
	 * Filters the content and returns the HashMap containing
	 * the Headers and the contents of the table.
	 * @param filter 
	 * @return: The map containing the columns and values of the table
	 */
	public abstract Map<String, List<String>> generateReport(HeaderReport headerReport, AnswerReport answerReport, FilterCombination filter);
	
	public abstract String getType();
	
}

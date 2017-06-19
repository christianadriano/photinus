package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.predictive.FilterCombination;

/**
 * Keeps data relative to a sub-group of workers and their microtasks
 * 
 * @author adrianoc
 *
 */
public class SubCrowd {
	
	/** Total answers provided by this sub-crowd */
	public Double totalAnswers;
	
	/** Filter that produced the sub-crowd */
	public FilterCombination filterCombination;
	
	/** Total workers in the sub-crowd*/
	public Double totalWorkers;
	
	/** The maximum number of answers that all questions answered by this sub-crowd has */
	public Double maxCommonAnswers;

	/** All microtasks taken by this sub-crowd */
	public HashMap<String, Microtask> microtaskMap;
	
	/** Name of the sub-crowd */
	public String name;
	
	/** Keep all filters that must be applied as an UNION */
	ArrayList<FilterCombination> OR_FilterList = new ArrayList<FilterCombination>();
	
	
	//------------------------------------------------------------------------
	
	/** Results for all Java methods and averages computed with Across-questions consensus*/
	public DataPoint acrossQuestionsDataPoint; 

	/** Results for all Java methods and averages computed with Within-questions consensus */
	public DataPoint withinQuestionDataPoint; 
	
	/** Results from combining across and within question Data Points */
	public DataPoint combinedConsensusDataPoint;
	
	
	/** Add elements to the OR filter */
	public void addOR_Filter(FilterCombination combination) {
		this.OR_FilterList.add(combination);
	}
}

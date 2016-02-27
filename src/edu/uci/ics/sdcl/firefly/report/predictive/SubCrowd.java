package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;


/**
 * Keeps data relative to a sub-group of workers and their microtasks
 * 
 * @author adrianoc
 *
 */
public class SubCrowd {
	
	/** Total answers provided by this sub-crowd */
	public Integer totalAnswers;
	
	/** Filter that produced the sub-crowd */
	public Filter filter;
	
	/** Total workers in the sub-crowd*/
	public Integer totalWorkers;
	
	/** The maximum number of answers that all questions answered by this sub-crowd has */
	public Integer maxCommonAnswers;

	/** All microtasks taken by this sub-crowd */
	public HashMap<String, Microtask> microtaskMap;
	
	/** Name of the sub-crowd */
	public String name;
	
	
}

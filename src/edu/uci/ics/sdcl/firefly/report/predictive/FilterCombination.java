package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;

/** 
 * Class responsible to keep the multiple filter combinations and the respective outcome of
 * computing predictors with the data produced by a concrete filter combination.
 * 
 * @author adrianoc
 *
 */
public class FilterCombination {

	public static final String ANSWER_DURATION = "ANSWER DURATION RANGE";
	public static final String SESSION_DURATION = "SESSION DURATION RANGE";
	public static final String CONFIDENCE_LEVEL = "CONFIDENCE LEVEL RANGE";
	public static final String DIFFICULTY_LEVEL = "DIFFICULTY LEVEL RANGE";
	public static final String WORKER_SCORE = "WORKER SCORE RANGE";
	public static final String WORKER_SCORE_EXCLUSION = "WORKER SCORES EXCLUDED"; //List of professions to be considered
	public static final String EXPLANATION_SIZE = "EXPLANATION SIZE RANGE";
	public static final String WORKER_IDK = "WORKER %IDK RANGE";
	public static final String WORKER_PROFESSION = "WORKER PROFESSION"; //List of professions to be considered.
	public static final String WORKER_YEARS_OF_EXEPERIENCE = "WORKER YEARS OF EXPERIENCE"; //List of professions to be considered.
	public static final String FIRST_ANSWER_DURATION = "FIRST_ANSWER_DURATION";
	public static final String SECOND_THIRD_ANSWER_DURATION = "SECOND_THIRD_ANSWER_DURATION";
	public static final String CONFIDENCE_DIFFICULTY_PAIRS = "CONFIDENCE_DIFFICULTY_PAIRS";
	public static final String EXCLUDED_QUESTIONS = "EXCLUDED_QUESTIONS";
	public static final String FIRST_HOURS = "FIRST_HOURS";
	public static final String MAX_ANSWERS = "MAX_ANSWERS";

	public HashMap<String,Range> combinationMap = new HashMap<String,Range>();


	//-----------------------------------------------------------------------------------------------------
	//FOR REPORT GENERATION
	
	public static String[] headerList = { 
		
		FIRST_ANSWER_DURATION,
		SECOND_THIRD_ANSWER_DURATION,
		CONFIDENCE_LEVEL,
		DIFFICULTY_LEVEL,
		EXPLANATION_SIZE,
		WORKER_SCORE_EXCLUSION,
		WORKER_SCORE,
		WORKER_IDK,
		WORKER_PROFESSION,
		WORKER_YEARS_OF_EXEPERIENCE,
		CONFIDENCE_DIFFICULTY_PAIRS,
		EXCLUDED_QUESTIONS,
		FIRST_HOURS,
		MAX_ANSWERS
	};

	public static String getFilterHeaders(){
		String result="";
		for(String name:headerList){
			if(result.length()==0) 
				result = name+",";
			else
				result = result + name+",";
		}
		return result;
	}

	public String toString(String[] headerList){
		String result="";
		for(String name : headerList){
			Range range = combinationMap.get(name);
			//System.out.println(name);
			if(result.length()==0)				
				result = range.toString();
			else
				result = result+","+range.toString();
		}
		return result;
	}

	public void addFilterParam(String filterName, int max, int min){
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(filterName, new Range(min,max));
	}

	public Range getFilterParam(String filterName){
		return this.combinationMap.get(filterName);
	}

	/** Instantiates a filter with the combined configurations */
	public Filter getFilter(){

		Filter filter = new Filter();
		
		for(String filterName: this.combinationMap.keySet()){

			int min=-1;
			int max=-1;

			if(this.combinationMap.get(filterName).min!=null){
				min = this.combinationMap.get(filterName).min.intValue();
				max = this.combinationMap.get(filterName).max.intValue();
			}

			if(filterName.compareTo(FilterCombination.FIRST_ANSWER_DURATION)==0){
				Range range = this.combinationMap.get(filterName);
				filter.setFirstAnswerDurationCriteria(range.minD.doubleValue(), range.maxD.doubleValue());
			}
			else
				if(filterName.compareTo(FilterCombination.SECOND_THIRD_ANSWER_DURATION)==0){
					Range range = this.combinationMap.get(filterName);
					filter.setSecondThirdAnswerDurationCriteria(range.minD.doubleValue(), range.maxD.doubleValue());
				}
				else
				if(filterName.compareTo(FilterCombination.SESSION_DURATION)==0){
					filter.setSessionDurationCriteria(new Double(min), new Double(max));
				}
				else
					if(filterName.compareTo(FilterCombination.CONFIDENCE_LEVEL)==0){
						filter.setConfidenceCriteria(min, max);
					}
					else
						if(filterName.compareTo(FilterCombination.DIFFICULTY_LEVEL)==0){
							filter.setDifficultyCriteria(min, max);
						}
						else 
							if(filterName.compareTo(FilterCombination.EXPLANATION_SIZE)==0){
								filter.setExplanationSizeCriteria(min, max);
							}
							else 
								if(filterName.compareTo(FilterCombination.WORKER_SCORE)==0){
									filter.setWorkerScoreCriteria(min, max);
								}
								else
									if(filterName.compareTo(FilterCombination.WORKER_SCORE_EXCLUSION)==0){
										filter.setWorkerScoreExclusionList(this.combinationMap.get(filterName).list);
									}
									else
										if(filterName.compareTo(FilterCombination.WORKER_IDK)==0){
											filter.setIDKPercentageCriteria(new Double(min), new Double(max));
										}
										else
											if(filterName.compareTo(FilterCombination.WORKER_PROFESSION)==0){
												filter.setWorkerProfessionMap(this.combinationMap.get(filterName).professionExclusionList);
											}
											else
												if(filterName.compareTo(FilterCombination.WORKER_YEARS_OF_EXEPERIENCE)==0){
													Range range = this.combinationMap.get(filterName);
													filter.setYearsOfExperience(range.minD, range.maxD);
												}
												else
													if(filterName.compareTo(FilterCombination.CONFIDENCE_DIFFICULTY_PAIRS)==0){
														Range range = this.combinationMap.get(filterName);
														filter.setConfidenceDifficultyPairList(range.confidenceDifficultyPairMap);
													}
													else
														if(filterName.compareTo(FilterCombination.EXCLUDED_QUESTIONS)==0){
															Range range = this.combinationMap.get(filterName);
															filter.setQuestionToExcludeMap(range.questionsToExcludeMap);
														}
														else
															if(filterName.compareTo(FilterCombination.FIRST_HOURS)==0){
																Range range = this.combinationMap.get(filterName);
																filter.setStartEndDates(range.startEndDates);
															}
															else
																if(filterName.compareTo(FilterCombination.MAX_ANSWERS)==0){
																	Range range = this.combinationMap.get(filterName);
																	filter.setMaxAnswers(range.max.intValue());
																}
		}
		return filter;	
	}
	
	public void addFilterParam(String workerScoreExclusion,
			int[] workerScoreExclusionList) {
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(workerScoreExclusion, new Range(workerScoreExclusionList));
	}

	public void addFilterParam(String workerProfession, String[] workerProfessionList) {
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(workerProfession, new Range(workerProfessionList));
	}

	public void addFilterParam(String workerYearsOfExeperienceProfession,
			double maxYearsOfExperience, double minYearsOfExperience) {
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(workerYearsOfExeperienceProfession, new Range(minYearsOfExperience,maxYearsOfExperience));		
	}

	public void addFilterParam(String confidenceDifficultyPairs,
			HashMap<String,Tuple> confidenceDifficultyPairMap) {
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(confidenceDifficultyPairs, new Range(confidenceDifficultyPairMap));	
		
	}

	public void addFilterParam(String questionExclusion,
			TreeMap<String,String> questionToExcludeMap) {
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(questionExclusion, new Range(questionToExcludeMap));	
		
	}

	public void addFilterParam(String firstHours, Date maxDate, Date minDate) {
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(firstHours, new Range(maxDate, minDate));	
		
	}
	

}

package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Generates filter combinations that will later be used to select microtasks
 */
public class FilterGenerator {

	public static FilterCombination generateFilterCombination(CombinedFilterRange range){

		FilterCombination combination = new FilterCombination();

		combination.addFilterParam(FilterCombination.FIRST_ANSWER_DURATION, range.getMaxFirstAnswerDuration(), range.getMinFirstAnswerDuration());
		combination.addFilterParam(FilterCombination.SECOND_THIRD_ANSWER_DURATION, range.getMaxSecondThirdAnswerDuration(), range.getMinSecondThirdAnswerDuration());
		combination.addFilterParam(FilterCombination.CONFIDENCE_DIFFICULTY_PAIRS,range.getConfidenceDifficultyPairList());
		combination.addFilterParam(FilterCombination.CONFIDENCE_LEVEL, range.getMaxConfidence(), range.getMinConfidence());
		combination.addFilterParam(FilterCombination.DIFFICULTY_LEVEL, range.getMaxDifficulty(),range.getMinDifficulty());
		combination.addFilterParam(FilterCombination.EXPLANATION_SIZE, range.getMaxExplanationSize(), range.getMinExplanationSize());
		combination.addFilterParam(FilterCombination.WORKER_SCORE_EXCLUSION, range.getWorkerScoreExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_SCORE, range.getMaxWorkerScore(), range.getMinWorkerScore());
		combination.addFilterParam(FilterCombination.WORKER_IDK, range.getMaxWorkerIDKPercentage(),range.getMinWorkerIDKPercentage());
		combination.addFilterParam(FilterCombination.WORKER_PROFESSION, range.getProfessionExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_YEARS_OF_EXEPERIENCE, range.getMaxYearsOfExperience(), range.getMinWorkerYearsOfExperience());
		combination.addFilterParam(FilterCombination.EXCLUDED_QUESTIONS, range.getQuestionsToExcludeMap());
		combination.addFilterParam(FilterCombination.FIRST_HOURS, range.getMaxDate(),range.getMinDate());
		combination.addFilterParam(FilterCombination.MAX_ANSWERS, range.getMaxAnswers(), 0);

		return combination;
	}


	/** Filter answers by answer duration */
	public static ArrayList<FilterCombination> generateAnswerFilterCombinationList(){

		HashMap<String, CombinedFilterRange> map;
		CombinedFilterRange range;

		map = AttributeRangeGenerator.setupNoFilters();
		range = map.get(AttributeRangeGenerator.NO_FILTERS);	
		
	//	map = AttributeRangeGenerator.setupCombineScoreProfession();
		//range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_NON_STUDENT);	

		//range.setMaxFirstAnswerDuration(10800.00);
		//range.setMinFirstAnswerDuration(0.0);
		
		ArrayList<FilterCombination> filterList = new ArrayList<FilterCombination>();

		for(int minConfidence : range.getConfidenceList()){
			for(int maxDifficulty : range.getDifficulytList()){
				for(int minExplanationSize : range.getExplanationSizeList()){
					for(int minWorkerScore : range.getWorkerScoreList()){
						for(int maxAnswers: range.getMaxAnswerList()){
							FilterCombination combination = new FilterCombination();

							combination.addFilterParam(FilterCombination.FIRST_ANSWER_DURATION, range.getMaxFirstAnswerDuration(), range.getMinFirstAnswerDuration());
							combination.addFilterParam(FilterCombination.SECOND_THIRD_ANSWER_DURATION, range.getMaxSecondThirdAnswerDuration(), range.getMinSecondThirdAnswerDuration());
							combination.addFilterParam(FilterCombination.CONFIDENCE_DIFFICULTY_PAIRS,range.getConfidenceDifficultyPairList());
							combination.addFilterParam(FilterCombination.CONFIDENCE_LEVEL, range.getMaxConfidence(), minConfidence);
							combination.addFilterParam(FilterCombination.DIFFICULTY_LEVEL, maxDifficulty,range.getMinDifficulty());
							combination.addFilterParam(FilterCombination.EXPLANATION_SIZE, range.getMaxExplanationSize(), minExplanationSize);
							combination.addFilterParam(FilterCombination.WORKER_SCORE_EXCLUSION, range.getWorkerScoreExclusionList());
							combination.addFilterParam(FilterCombination.WORKER_SCORE, range.getMaxWorkerScore(), minWorkerScore);
							combination.addFilterParam(FilterCombination.WORKER_IDK, range.getMaxWorkerIDKPercentage(),range.getMinWorkerIDKPercentage());
							combination.addFilterParam(FilterCombination.WORKER_PROFESSION, range.getProfessionExclusionList());
							combination.addFilterParam(FilterCombination.WORKER_YEARS_OF_EXEPERIENCE, range.getMaxYearsOfExperience(), range.getMinWorkerYearsOfExperience());
							combination.addFilterParam(FilterCombination.EXCLUDED_QUESTIONS, range.getQuestionsToExcludeMap());
							combination.addFilterParam(FilterCombination.FIRST_HOURS, range.getMaxDate(),range.getMinDate());
							combination.addFilterParam(FilterCombination.MAX_ANSWERS, maxAnswers, 0);
							filterList.add(combination);
						}
					}
				}
			}
		}
		return filterList;
	}


}

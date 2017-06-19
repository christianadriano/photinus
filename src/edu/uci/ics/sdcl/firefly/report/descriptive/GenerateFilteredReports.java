package edu.uci.ics.sdcl.firefly.report.descriptive;


import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.report.predictive.CombinedFilterRange;
import edu.uci.ics.sdcl.firefly.report.predictive.FilterCombination;
import edu.uci.ics.sdcl.firefly.report.predictive.Tuple;


/**
 * 
 * Filters data for the following selections:
 * 
 * All Non-students
 * 100% score Non-Students
 * 100% score workers
 * Conditional question <= 3 LOC 
 * > 80% score Non-Students
 * All workers
 * All but short answers (below 1st quartile of explanation size)
 * > 80% score workers
 * Top confident answers (level of confidence 5)
 * All but fast answers (below 1st quartile of duration)
 */
public class GenerateFilteredReports {


	public static void main(String[] args){
		GenerateFilteredReports report = new GenerateFilteredReports();
		report.generate_AllWorkers();
	}

	public void generate_AllNonStudents(){

		CombinedFilterRange range = new CombinedFilterRange();
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();

		FilterCombination combination  = new FilterCombination();
		combination.addFilterParam(FilterCombination.WORKER_PROFESSION, range.getProfessionExclusionList());

		FilterCombination filter = combination.getFilter();

		ReportApplication reportApplication = new ReportApplication(filter, new ExcelExporter());
		reportApplication.runAnswerOptionReport();
	}

	public void generate_100ScoreWorkers(){

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName("WORKER_SCORE_100");
		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {3,4});
		range.setWorkerScoreList(new int[]{5});
		range.setUndefinedWithDefault();

		FilterCombination combination  = new FilterCombination();
		combination.addFilterParam(FilterCombination.WORKER_SCORE_EXCLUSION, range.getWorkerScoreExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_SCORE, range.getMaxWorkerScore(), 3);

		FilterCombination filter = combination.getFilter();

		ReportApplication reportApplication = new ReportApplication(filter, new ExcelExporter());
		reportApplication.runAnswerOptionReport();
	}

	public void generate_100ScoreWorkers_AllNonStudents(){

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName("WORKER_SCORE_100_ALL_NON_STUDENTS");
		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {3,4});
		range.setWorkerScoreList(new int[]{5});
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();

		FilterCombination combination  = new FilterCombination();
		combination.addFilterParam(FilterCombination.WORKER_SCORE_EXCLUSION, range.getWorkerScoreExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_SCORE, range.getMaxWorkerScore(), 3);
		combination.addFilterParam(FilterCombination.WORKER_PROFESSION, range.getProfessionExclusionList());

		FilterCombination filter = combination.getFilter();

		ReportApplication reportApplication = new ReportApplication(filter, new ExcelExporter());
		reportApplication.runAnswerOptionReport();
	}

	public void generate_Above80ScoreWorkers(){

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName("WORKER_SCORE_ABOVE_80");
		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {3});
		range.setWorkerScoreList(new int[]{4});
		range.setUndefinedWithDefault();

		FilterCombination combination  = new FilterCombination();
		combination.addFilterParam(FilterCombination.WORKER_SCORE_EXCLUSION, range.getWorkerScoreExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_SCORE, range.getMaxWorkerScore(), 5);

		FilterCombination filter = combination.getFilter();

		ReportApplication reportApplication = new ReportApplication(filter, new ExcelExporter());
		reportApplication.runAnswerOptionReport();
	}


	public void generate_above80ScoreWorkers_AllNonStudents(){

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName("ABOVE_80_SCORE_ALL_NON_STUDENTS");
		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {3});
		range.setWorkerScoreList(new int[]{4});
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();

		FilterCombination combination  = new FilterCombination();
		combination.addFilterParam(FilterCombination.WORKER_SCORE_EXCLUSION, range.getWorkerScoreExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_SCORE, range.getMaxWorkerScore(), 4);
		combination.addFilterParam(FilterCombination.WORKER_PROFESSION, range.getProfessionExclusionList());

		FilterCombination filter = combination.getFilter();

		ReportApplication reportApplication = new ReportApplication(filter, new ExcelExporter());
		reportApplication.runAnswerOptionReport();
	}

	public void generate_removedFastAnswers(){

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName("ANSWER_DURATION_MIN_q1_q1");  //Remove first QUARTILE for first and secod-third answers

		range.setMaxFirstAnswerDuration(3600);//1hour 
		range.setMinFirstAnswerDuration(167.4);

		range.setMaxSecondThirdAnswerDuration(3600);//1hour
		range.setMinSecondThirdAnswerDuration(69.9);

		range.setUndefinedWithDefault();

		FilterCombination combination  = new FilterCombination();
		combination.addFilterParam(FilterCombination.FIRST_ANSWER_DURATION, range.getMaxFirstAnswerDuration(), range.getMinFirstAnswerDuration());
		combination.addFilterParam(FilterCombination.SECOND_THIRD_ANSWER_DURATION, range.getMaxSecondThirdAnswerDuration(), range.getMinSecondThirdAnswerDuration());
		FilterCombination filter = combination.getFilter();

		ReportApplication reportApplication = new ReportApplication(filter, new ExcelExporter());
		reportApplication.runAnswerOptionReport();
	}

	public void generate_RemoveAnswersWithShortExplanations(){

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName("EXPLANATION_2_3_4_QT_57_2383") ;  // REMOVED 1Quartile          
		range.setMaxExplanationSize(2383);
		range.setUndefinedWithDefault();

		FilterCombination combination  = new FilterCombination();
		combination.addFilterParam(FilterCombination.EXPLANATION_SIZE, range.getMaxExplanationSize(),57);
		FilterCombination filter = combination.getFilter();

		ReportApplication reportApplication = new ReportApplication(filter, new ExcelExporter());
		reportApplication.runAnswerOptionReport();
	}


	public void generate_AllWorkers(){
		CombinedFilterRange range = new CombinedFilterRange();
		range.setUndefinedWithDefault();
		FilterCombination combination  = new FilterCombination();
		FilterCombination filter = combination.getFilter();

		ReportApplication reportApplication = new ReportApplication(filter, new ExcelExporter());
		reportApplication.runAnswerOptionReport();
	}

	public void generate_ConfidenceDifficulty_UP_3_PERCENT(){

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName("CONFIDENCE_DIFFICULTY_UP_3_PERCENT");  

		HashMap<String, Tuple>  map = Tuple.generateAllCombinations(5, 5);
		map.remove(new Tuple(5,1).toString());
		map.remove(new Tuple(5,2).toString());
		map.remove(new Tuple(5,3).toString());
		map.remove(new Tuple(4,2).toString());
		map.remove(new Tuple(4,3).toString());
		map.remove(new Tuple(4,4).toString());
		map.remove(new Tuple(3,3).toString());
		map.remove(new Tuple(3,4).toString());
		//map.remove(new Tuple(0,4).toString()); don't need to consider IDK answers
		//map.remove(new Tuple(0,5).toString());

		range.setConfidenceDifficultyPairMap(map);

		range.setUndefinedWithDefault();
		
		FilterCombination combination  = new FilterCombination();
		combination.addFilterParam(FilterCombination.CONFIDENCE_DIFFICULTY_PAIRS,range.getConfidenceDifficultyPairList());
		FilterCombination filter = combination.getFilter();

		ReportApplication reportApplication = new ReportApplication(filter, new ExcelExporter());
		reportApplication.runAnswerOptionReport();
	}

}

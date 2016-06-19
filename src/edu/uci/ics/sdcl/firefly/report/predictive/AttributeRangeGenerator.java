package edu.uci.ics.sdcl.firefly.report.predictive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

/** 
 * Generate different range values for worker, answer, or question attributes.
 * 
 * @author adrianoc
 *
 */
public class AttributeRangeGenerator {

	public static String NO_FILTERS = "ALL_WORKERS";
	public static String WORKER_SCORE_100 ="100_SCORE_WORKERS";
	static String WORKER_SCORE_80 ="80_SCORE_WORKERS";
	static String WORKER_SCORE_60 ="60% score worker only";
	public static String WORKER_SCORE_100_80 ="100_80_SCORE_WORKERS";
	static String WORKER_SCORE_80_60 ="80% and 60% score workers";

	static String WORKER_PROFESSIONAL_PROGRAMMER ="Professional Developer Only";
	static String WORKER_GRADUATE_STUDENT ="Graduate Student Only";
	static String WORKER_UNDERGRADUATE_STUDENT ="Undergraduate Student Only";
	static String WORKER_HOBBYIST ="Hobbyist Only";
	static String WORKER_PROFESSIONAL_PROGRAMMER_HOBBYIST= "Professional Programmers and Hobbyists";
	static String WORKER_PROFESSIONAL_PROGRAMMER_OTHER= "Professional Programmers and Others";
	static String WORKER_PROFESSIONAL_PROGRAMMER_GRADUATE= "Professional Programmers and Graduates";
	static String WORKER_PROFESSIONAL_PROGRAMMER_UNDERGRADUATE= "Professional Programmers and Undergraduates";
	static String WORKER_HOBBYIST_OTHER= "Hobbyists and Others";
	static String WORKER_HOBBYIST_GRADUATE= "Hobbyists and Graduates";
	static String WORKER_HOBBYIST_UNDERGRADUATE= "Hobbyists and Undergraduates";
	static String WORKER_GRADUATE_UNDERGRADUATE_OTHER= "Graduates Undergraduates Others";

	static String WORKER_OTHER ="Other Only";
	public static String WORKER_NON_STUDENT ="WORKER_NON_STUDENT";
	static String WORKER_STUDENT ="Student Only";

	static final String WORKER_SCORE_NON_STUDENT = "Scores and Non-Students";
	static final String WORKER_SCORE_STUDENT = "Scores and Students";
	static final String WORKER_SCORE_80_NON_STUDENT = "Score 80% and Non-Students";
	static final String WORKER_SCORE80_STUDENT = "Score 80% and Students";
	static final String WORKER_SCORE60_NON_STUDENT = "Score 60% and Non-Students";
	static final String WORKER_SCORE60_STUDENT = "Score 60% and Students";
	static final String WORKER_SCORE60_80_NON_STUDENT = "Score 60% to 80% and Non-Students";
	static final String WORKER_SCORE60_80_STUDENT = "Score 60% to 80% and Students";
	public static final String WORKER_SCORE_100_NON_STUDENT = "WORKER_SCORE_100_NON_STUDENT";
	public static final String WORKER_SCORE_100_80_NON_STUDENT = "WORKER_SCORE_100_80_NON_STUDENT";

	static final String WORKER_PERCENT_IDK_1_34 = "WORKER_PERCENT_IDK_1_34";
	static final String WORKER_PERCENT_IDK_35_67 = "WORKER_PERCENT_IDK_35_67";
	static final String WORKER_PERCENT_IDK_68_100 = "WORKER_PERCENT_IDK_68_100";
	static final String WORKER_PERCENT_IDK_0 = "WORKER_PERCENT_IDK_0";

	static final String WORKER_YEARS_OF_EXPERIENCE_0_1 = "WORKER_YEARS_OF_EXPERIENCE_0_1";
	static final String WORKER_YEARS_OF_EXPERIENCE_1_5 = "WORKER_YEARS_OF_EXPERIENCE_1_5";
	static final String WORKER_YEARS_OF_EXPERIENCE_5_10 = "WORKER_YEARS_OF_EXPERIENCE_5_10";
	static final String WORKER_YEARS_OF_EXPERIENCE_10_15 = "WORKER_YEARS_OF_EXPERIENCE_10_15";
	static final String WORKER_YEARS_OF_EXPERIENCE_15_50 = "WORKER_YEARS_OF_EXPERIENCE_15_50";

	static final String SESSION_DURATION_0_2 = "SESSION_DURATION_0_2";
	static final String SESSION_DURATION_2_4 = "SESSION_DURATION_2_4";
	static final String SESSION_DURATION_4_8 = "SESSION_DURATION_4_8";

	//First answer 1st Qu.= 160.93s //Second Third answers  1st Qu.:  69.387s
	//FILTER both first and second-third answers 
	static final String ANSWER_DURATION_MIN_0_0  = "ANSWER_DURATION_MIN_0_0"; 
	static final String ANSWER_DURATION_MIN_30_15 = "ANSWER_DURATION_MIN_30_15"; 
	static final String ANSWER_DURATION_MIN_60_30 = "ANSWER_DURATION_MIN_60_30";
	static final String ANSWER_DURATION_MIN_120_60 = "ANSWER_DURATION_MIN_120_60";
	public static final String ANSWER_DURATION_MIN_q1_q1 = "ANSWER_DURATION_MIN_q1_q1";
	static final String ANSWER_DURATION_MIN_q1_q3 = "ANSWER_DURATION_MIN_q1_q3";

	//Second Third answers  1st Qu.:  69.387s
	static final String SECOND_THIRD_ANSWER_DURATION_MIN_15s = "SECOND_THIRD_ANSWER_DURATION_MIN_15s"; 
	static final String SECOND_THIRD_ANSWER_DURATION_MIN_30s = "SECOND_THIRD_ANSWER_DURATION_MIN_30s";
	static final String SECOND_THIRD_ANSWER_DURATION_MIN_60s = "SECOND_THIRD_ANSWER_DURATION_MIN_60s";

	public static final String CONFIDENCE_DIFFICULTY_UP_3_PERCENT = "CONFIDENCE_DIFFICULTY_UP_3PERCENT";
	static final String CONFIDENCE_DIFFICULTY_HIGH_CONFIDENCE = "CONFIDENCE_DIFFICULTY_HIGH_CONFIDENCE";
	static final String CONFIDENCE_DIFFICULTY_LOW_DIFFICULTY = "CONFIDENCE_DIFFICULTY_LOW_DIFFICULTY";

	static final String EXPLANATION_1QT_0_53 = "EXPLANATION_1QT_0_53";
	static final String EXPLANATION_2QT_53_99 = "EXPLANATION_2QT_53_99";
	static final String EXPLANATION_3QT_99_171 = "EXPLANATION_3QT_99_171";
	static final String EXPLANATION_4_QT_171_2383 = "EXPLANATION_4_QT_171_2383";
	public static final String EXPLANATION_2_3_4_QT_57_2383 = "EXPLANATION_2_3_4_QT_57_2383";

	static final String COMBINED_DURATION_CONFIDENCE_EXPLANATIONSIZE_1QT_3PERCENT_1QT = "COMBINED_DURATION_CONFIDENCE_EXPLANATIONSIZE_1QT_3PERCENT_1QT";
	public static final String CONDITIONAL_CLAUSE_ABOVE_3LINES = "CONDITIONAL_CLAUSE_ABOVE_3LINES";

	static final String FIRST_6_HOURS= "FIRST_6_HOURS";
	static final String FIRST_9_HOURS= "FIRST_9_HOURS";
	static final String FIRST_12_HOURS= "FIRST_12_HOURS";
	static final String FIRST_15_HOURS= "FIRST_15_HOURS";
	static final String FIRST_18_HOURS= "FIRST_18_HOURS";
	static final String FIRST_24_HOURS= "FIRST_24_HOURS";
	static final String FIRST_36_HOURS= "FIRST_36_HOURS";
	static final String FIRST_48_HOURS= "FIRST_48_HOURS";

	static final String MAX_ANSWERS = "MAX_ANSWERS";

	static final String WORKER_SCORE_100_80_DIFFICULTY_ALL = "WORKER_SCORE_100_80_DIFFICULTY_ALL";
	static final String WORKER_SCORE_100_DIFFICULTY_ALL = "WORKER_SCORE_100_DIFFICULTY_ALL";

	static final String WORKER_SCORE_100_DIFFICULTY_5_4_3 = "WORKER_SCORE_100_DIFFICULTY_5_4_3";
	static final String WORKER_SCORE_100_DIFFICULTY_1_2_3_4 = "WORKER_SCORE_100_DIFFICULTY_1_2_3_4";
	static final String WORKER_SCORE_100_DIFFICULTY_1_2_3 = "WORKER_SCORE_100_DIFFICULTY_1_2_3";

	static final String WORKER_SCORE_60_80_DIFFICULTY_1_2 = "WORKER_SCORE_60_80_DIFFICULTY_1_2";
	static final String WORKER_SCORE_60_DIFFICULTY_1_2 = "WORKER_SCORE_60_DIFFICULTY_1_2";
	static final String WORKER_SCORE_100_80_DIFFICULTY_5_4_3 = "WORKER_SCORE_100_80_DIFFICULTY_5_4_3";
	static final String WORKER_SCORE_100_80_DIFFICULTY_5_4 = "WORKER_SCORE_100_80_DIFFICULTY_5_4";
	static final String WORKER_SCORE_60_DIFFICULTY_1_2_3 = "WORKER_SCORE_60_DIFFICULTY_1_2_3";
	static final String WORKER_SCORE_60_DIFFICULTY_1_2_3_4 = "WORKER_SCORE_60_DIFFICULTY_1_2_3_4";

	static final String WORKER_SCORE_60_DIFFICULTY_5_4 = "WORKER_SCORE_60_DIFFICULTY_5_4";
	static final String WORKER_SCORE_80_DIFFICULTY_5_4 = "WORKER_SCORE_80_DIFFICULTY_5_4";
	static final String WORKER_SCORE_100_DIFFICULTY_5_4 = "WORKER_SCORE_100_DIFFICULTY_5_4";

	static final String WORKER_SCORE_80_DIFFICULTY_1_2 = "WORKER_SCORE_80_DIFFICULTY_1_2";
	static final String WORKER_SCORE_100_DIFFICULTY_1_2 = "WORKER_SCORE_100_DIFFICULTY_1_2";

	static final String WORKER_SCORE_80_DIFFICULTY_1_2_3_4 = "WORKER_SCORE_80_DIFFICULTY_1_2_3_4";
	static final String WORKER_SCORE_80_DIFFICULTY_1_2_3 = "WORKER_SCORE_80_DIFFICULTY_1_2_3";

	//Profession difficulty filters
	static final String UNDERGRAD_DIFFICULTY_1_2_3 = "UNDERGRAD_DIFFICULTY_1_2_3";
	static final String GRAD_STUDENTS_DIFFICULTY_1_2_3_4 = "GRAD_STUDENTS_DIFFICULTY_1_2_3_4";
	static final String NON_STUDENTS_DIFFICULTY_ALL = "NON_STUDENTS_DIFFICULTY_ALL";
	static final String NON_STUDENTS_DIFFICULTY_1 = "NON_STUDENTS_DIFFICULTY_1";
	static final String NON_STUDENTS_DIFFICULTY_2 = "NON_STUDENTS_DIFFICULTY_2";
	static final String NON_STUDENTS_DIFFICULTY_3 = "NON_STUDENTS_DIFFICULTY_3";
	static final String STUDENTS_DIFFICULTY_1_2_3 = "STUDENTS_DIFFICULTY_1_2_3";
	static final String STUDENTS_DIFFICULTY_1_2 = "STUDENTS_DIFFICULTY_1_2";

	//Profession and Years of Experience
	static final String UNDERGRAD_YoE_QT_2_UP = "UNDERGRAD_YoE_QT_2_UP";
	static final String GRAD_YoE_QT_2_UP = "GRAD_YoE_QT_2_UP";
	static final String PROFESSIONALS_YoE_QT_2_UP = "PROFESSIONALS_YoE_QT_2_UP";
	static final String HOBBYIST_YoE_QT_2_UP = "HOBBYIST_YoE_QT_2_UP";
	static final String OTHER_YoE_QT_2_UP = "OTHER_YoE_QT_2_UP";
	static final String STUDENTS_YoE_QT_2_UP = "STUDENTS_YoE_QT_2_UP";
	static final String NON_STUDENTS_YoE_QT_2_UP = "NON_STUDENTS_YoE_QT_2_UP";
	static final String NON_STUDENTS_YoE_ALL = "NON_STUDENTS_YoE_ALL";

	
	

	/**
	 * These are filters that selected subcrowds who DID locate all 8 faults.
	 */
	public static HashMap<String, CombinedFilterRange> getSubCrowdFilters(){

		HashMap<String, CombinedFilterRange> originalMap;
		CombinedFilterRange range;
		HashMap<String, CombinedFilterRange> selectedRangeMap = new HashMap<String, CombinedFilterRange>();

		originalMap = AttributeRangeGenerator.setupCombineScoreProfession();
		range = originalMap.get(AttributeRangeGenerator.WORKER_SCORE_100_NON_STUDENT);		
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_SCORE_100_NON_STUDENT, range);

		originalMap = AttributeRangeGenerator.setupProfessionRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_NON_STUDENT);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_NON_STUDENT, range);


		originalMap = AttributeRangeGenerator.setupCombineScoreProfession();
		range = originalMap.get(AttributeRangeGenerator.WORKER_SCORE_100_80_NON_STUDENT);
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_SCORE_100_80_NON_STUDENT, range);

		originalMap = AttributeRangeGenerator.setupScoreRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_SCORE_100);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_SCORE_100, range);

		originalMap = AttributeRangeGenerator.setupScoreRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_SCORE_100_80);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_SCORE_100_80, range);

		originalMap = AttributeRangeGenerator.setupAnswerDurations();
		range = originalMap.get(AttributeRangeGenerator.ANSWER_DURATION_MIN_60_30);
		selectedRangeMap.put(AttributeRangeGenerator.ANSWER_DURATION_MIN_60_30, range);

		originalMap = AttributeRangeGenerator.setupConfidenceDifficulty();
		range = originalMap.get(AttributeRangeGenerator.CONFIDENCE_DIFFICULTY_UP_3_PERCENT);
		selectedRangeMap.put(AttributeRangeGenerator.CONFIDENCE_DIFFICULTY_UP_3_PERCENT, range);

		originalMap =  AttributeRangeGenerator.setupExplanationSize();
		range = originalMap.get(AttributeRangeGenerator.EXPLANATION_2_3_4_QT_57_2383);
		selectedRangeMap.put(AttributeRangeGenerator.EXPLANATION_2_3_4_QT_57_2383, range);

		originalMap =  AttributeRangeGenerator.setupQuestionsToExclude();
		range = originalMap.get(AttributeRangeGenerator.CONDITIONAL_CLAUSE_ABOVE_3LINES);
		selectedRangeMap.put(AttributeRangeGenerator.CONDITIONAL_CLAUSE_ABOVE_3LINES, range);

		originalMap =  AttributeRangeGenerator.setupNoFilters();
		range = originalMap.get(AttributeRangeGenerator.NO_FILTERS);
		selectedRangeMap.put(AttributeRangeGenerator.NO_FILTERS, range);

		//run the first 6h, 9h, 12h, 24h, 48h using the filtered logs. //Or make a filter for that
		//originalMap =  AttributeRangeGenerator.setupDateInterval();
		//range = originalMap.get(AttributeRangeGenerator.FIRST_9_HOURS);

		return selectedRangeMap;
	}

	/**
	 * These are filters that selected subcrowds who DID NOT locate all 8 faults.
	 */
	public static HashMap<String, CombinedFilterRange> getFailed_SubCrowdFilters(){

		HashMap<String, CombinedFilterRange> originalMap;
		CombinedFilterRange range;
		HashMap<String, CombinedFilterRange> selectedRangeMap = new HashMap<String, CombinedFilterRange>();

		originalMap = AttributeRangeGenerator.setupProfessionRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_PROFESSIONAL_PROGRAMMER_HOBBYIST);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_PROFESSIONAL_PROGRAMMER_HOBBYIST, range);

		originalMap = AttributeRangeGenerator.setupProfessionRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_PROFESSIONAL_PROGRAMMER_OTHER);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_PROFESSIONAL_PROGRAMMER_OTHER, range);

		originalMap = AttributeRangeGenerator.setupProfessionRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_PROFESSIONAL_PROGRAMMER_GRADUATE);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_PROFESSIONAL_PROGRAMMER_GRADUATE, range);

		originalMap = AttributeRangeGenerator.setupProfessionRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_PROFESSIONAL_PROGRAMMER_UNDERGRADUATE);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_PROFESSIONAL_PROGRAMMER_UNDERGRADUATE, range);

		originalMap = AttributeRangeGenerator.setupProfessionRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_HOBBYIST_OTHER);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_HOBBYIST_OTHER, range);

		originalMap = AttributeRangeGenerator.setupProfessionRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_HOBBYIST_GRADUATE);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_HOBBYIST_GRADUATE, range);

		originalMap = AttributeRangeGenerator.setupProfessionRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_HOBBYIST_UNDERGRADUATE);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_HOBBYIST_UNDERGRADUATE, range);

		originalMap = AttributeRangeGenerator.setupProfessionRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_GRADUATE_UNDERGRADUATE_OTHER);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_GRADUATE_UNDERGRADUATE_OTHER, range);

		originalMap = AttributeRangeGenerator.setupScoreRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_SCORE_60);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_SCORE_60, range);

		originalMap = AttributeRangeGenerator.setupScoreRangeFilters();
		range = originalMap.get(AttributeRangeGenerator.WORKER_SCORE_80);	
		selectedRangeMap.put(AttributeRangeGenerator.WORKER_SCORE_80, range);


		return selectedRangeMap;
	}


	static HashMap<String,CombinedFilterRange> setupNoFilters(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(NO_FILTERS);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;
	}

	static HashMap<String,CombinedFilterRange> setupScoreRangeFilters(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		//WorkerScore
		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100);
		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {3,4});
		range.setWorkerScoreList(new int[]{5});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_80);
		range.setMaxWorkerScore(4);
		range.setWorkerScoreExclusionList(new int[] {3});
		range.setWorkerScoreList(new int[]{4});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_60);
		range.setMaxWorkerScore(3);
		range.setWorkerScoreList(new int[]{3});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_80);
		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {3});
		range.setWorkerScoreList(new int[]{4});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_80_60);
		range.setMaxWorkerScore(4);
		range.setWorkerScoreExclusionList(new int[] {5});
		range.setWorkerScoreList(new int[]{3,4});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;

	}

	static HashMap<String,CombinedFilterRange> setupProfessionRangeFilters(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(WORKER_PROFESSIONAL_PROGRAMMER);
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_GRADUATE_STUDENT);
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Undergraduate_Student","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_UNDERGRADUATE_STUDENT);
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Graduate_Student","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_HOBBYIST);
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Graduate_Student","Undergraduate_Student","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_OTHER);
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Graduate_Student","Undergraduate_Student","Hobbyist"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_NON_STUDENT); 
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_STUDENT);
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_PROFESSIONAL_PROGRAMMER_HOBBYIST);
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_PROFESSIONAL_PROGRAMMER_OTHER);
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student","Hobbyist"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_PROFESSIONAL_PROGRAMMER_GRADUATE);
		range.setProfessionExclusionList(new String[] {"Undergraduate_Student","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_PROFESSIONAL_PROGRAMMER_UNDERGRADUATE);
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_HOBBYIST_OTHER);
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_HOBBYIST_GRADUATE);
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Undergraduate_Student","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_HOBBYIST_UNDERGRADUATE);
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Graduate_Student","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_GRADUATE_UNDERGRADUATE_OTHER);
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);	

		return rangeMap;
	}

	static HashMap<String,CombinedFilterRange> setupIDKRangeFilters(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(WORKER_PERCENT_IDK_1_34);
		range.setMaxWorkerIDKPercentage(34);
		range.setMinWorkerIDKPercentage(1);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_PERCENT_IDK_35_67);
		range.setMaxWorkerIDKPercentage(67);
		range.setMinWorkerIDKPercentage(35);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_PERCENT_IDK_68_100);
		range.setMaxWorkerIDKPercentage(100);
		range.setMinWorkerIDKPercentage(68);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_PERCENT_IDK_0);
		range.setMaxWorkerIDKPercentage(0);
		range.setMinWorkerIDKPercentage(0);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;
	}

	static HashMap<String,CombinedFilterRange> setupYearsExperienceRangeFilters(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(WORKER_YEARS_OF_EXPERIENCE_0_1);
		range.setMaxWorkerYearsOfExperience(1.0);
		range.setMinWorkerYearsOfExperience(0.0);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_YEARS_OF_EXPERIENCE_1_5);
		range.setMaxWorkerYearsOfExperience(5.0);
		range.setMinWorkerYearsOfExperience(1.1);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_YEARS_OF_EXPERIENCE_5_10);
		range.setMaxWorkerYearsOfExperience(10.0);
		range.setMinWorkerYearsOfExperience(5.1);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_YEARS_OF_EXPERIENCE_10_15);
		range.setMaxWorkerYearsOfExperience(15.0);
		range.setMinWorkerYearsOfExperience(10.1);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_YEARS_OF_EXPERIENCE_15_50);
		range.setMaxWorkerYearsOfExperience(50.0);
		range.setMinWorkerYearsOfExperience(15.1);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;
	} 


	static HashMap<String,CombinedFilterRange> setupCombineScoreProfession(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		//----------------------------------
		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_NON_STUDENT);

		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {});
		range.setWorkerScoreList(new int[]{3,4,5});
		range.setMinWorkerScore(3);

		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_80_NON_STUDENT);

		range.setMaxWorkerScore(4);
		range.setWorkerScoreExclusionList(new int[] {3,5});
		range.setWorkerScoreList(new int[]{4});
		range.setMinWorkerScore(4);

		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);


		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_NON_STUDENT);

		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {3,4});
		range.setWorkerScoreList(new int[]{5});
		range.setMinWorkerScore(5);

		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);


		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_80_NON_STUDENT);

		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {3});
		range.setWorkerScoreList(new int[]{4});
		range.setMinWorkerScore(4);

		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);


		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE60_NON_STUDENT);

		range.setMaxWorkerScore(3);
		range.setWorkerScoreExclusionList(new int[] {4,5});
		range.setWorkerScoreList(new int[]{3});
		range.setMinWorkerScore(3);

		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE60_80_NON_STUDENT);

		range.setMaxWorkerScore(4);
		range.setWorkerScoreExclusionList(new int[] {5});
		range.setWorkerScoreList(new int[]{3});
		range.setMinWorkerScore(3);

		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE80_STUDENT);

		range.setMaxWorkerScore(4);
		range.setWorkerScoreExclusionList(new int[] {3,5});
		range.setWorkerScoreList(new int[]{4});
		range.setMinWorkerScore(4);

		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);


		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE60_STUDENT);

		range.setMaxWorkerScore(3);
		range.setWorkerScoreExclusionList(new int[] {4,5});
		range.setWorkerScoreList(new int[]{3});
		range.setMinWorkerScore(3);

		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);


		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE60_80_STUDENT);

		range.setMaxWorkerScore(4);
		range.setWorkerScoreExclusionList(new int[] {5});
		range.setWorkerScoreList(new int[]{3});
		range.setMinWorkerScore(3);

		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//-----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_STUDENT);
		range.setMaxWorkerScore(5);
		range.setWorkerScoreExclusionList(new int[] {});
		range.setWorkerScoreList(new int[]{3,4,5});
		range.setMinWorkerScore(3);

		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist","Other"});
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;
	}


	static HashMap<String,CombinedFilterRange> setupAnswerDurations(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		//----------------------------------
		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(ANSWER_DURATION_MIN_0_0); //NO FILTER

		range.setMaxFirstAnswerDuration(600);//1hour 3rd quartile
		range.setMinFirstAnswerDuration(0);
		range.setMaxSecondThirdAnswerDuration(300);//1hour 3rd quartile
		range.setMinSecondThirdAnswerDuration(0);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(ANSWER_DURATION_MIN_30_15); 

		range.setMaxFirstAnswerDuration(3600);//1hour
		range.setMinFirstAnswerDuration(30);
		range.setMaxSecondThirdAnswerDuration(3600);//1hour
		range.setMinSecondThirdAnswerDuration(15);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(ANSWER_DURATION_MIN_60_30);

		range.setMaxFirstAnswerDuration(3600);//1hour
		range.setMinFirstAnswerDuration(60);

		range.setMaxSecondThirdAnswerDuration(3600);//1hour
		range.setMinSecondThirdAnswerDuration(30);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(ANSWER_DURATION_MIN_120_60);

		range.setMaxFirstAnswerDuration(3600);//1hour
		range.setMinFirstAnswerDuration(120);

		range.setMaxSecondThirdAnswerDuration(3600);//1hour
		range.setMinSecondThirdAnswerDuration(60);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(ANSWER_DURATION_MIN_q1_q1);  //Remove first QUARTILE

		range.setMaxFirstAnswerDuration(3600);//1hour 
		range.setMinFirstAnswerDuration(167.4);


		range.setMaxSecondThirdAnswerDuration(3600);//1hour
		range.setMinSecondThirdAnswerDuration(69.9);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);
		//----------------------------------

		//REMOVED FIRST AND LAST QUARTILES
		range = new CombinedFilterRange();
		range.setRangeName(ANSWER_DURATION_MIN_q1_q3);

		range.setMaxFirstAnswerDuration(683.90);
		range.setMinFirstAnswerDuration(167.4);

		range.setMaxSecondThirdAnswerDuration(228.700); 
		range.setMinSecondThirdAnswerDuration(69.9);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;
	}

	static HashMap<String,CombinedFilterRange> setupConfidenceDifficulty(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		//----------------------------------
		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(CONFIDENCE_DIFFICULTY_UP_3_PERCENT);  

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

		//System.out.println("Size of exclusion map:" + map.size());
		range.setConfidenceDifficultyPairMap(map);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(CONFIDENCE_DIFFICULTY_HIGH_CONFIDENCE); 
		map = Tuple.generateAllCombinations(5, 5);

		map.remove(new Tuple(5,1).toString());
		map.remove(new Tuple(5,2).toString());
		map.remove(new Tuple(5,3).toString());
		map.remove(new Tuple(5,4).toString());
		map.remove(new Tuple(5,5).toString());

		range.setConfidenceDifficultyPairMap(map);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(CONFIDENCE_DIFFICULTY_LOW_DIFFICULTY);   

		map = Tuple.generateAllCombinations(5, 5);
		map.remove(new Tuple(0,1).toString());
		map.remove(new Tuple(1,1).toString());
		map.remove(new Tuple(2,1).toString());
		map.remove(new Tuple(3,1).toString());
		map.remove(new Tuple(4,1).toString());
		map.remove(new Tuple(5,1).toString());


		range.setConfidenceDifficultyPairMap(map);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;
	}

	static HashMap<String,CombinedFilterRange> setupExplanationSize(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		//----------------------------------
		CombinedFilterRange range = new CombinedFilterRange();
		//		range.setRangeName(EXPLANATION_1QT_0_53) ;  //  1st Qu.:  53.0
		//
		//		int[] explanationSizeList_1 = {0};
		//		range.setMaxExplanationSize(53);
		//		range.setExplanationSizeList(explanationSizeList_1);
		//		range.setUndefinedWithDefault();
		//		rangeMap.put(range.getRangeName(),range);
		//
		//		//----------------------------------
		//		range = new CombinedFilterRange();
		//		range.setRangeName(EXPLANATION_2QT_53_99) ;  // Median :  99.0       
		//
		//		int[] explanationSizeList_2 = {53};
		//		range.setMaxExplanationSize(99);
		//		range.setExplanationSizeList(explanationSizeList_2);
		//		range.setUndefinedWithDefault();
		//		rangeMap.put(range.getRangeName(),range);
		//
		//		//----------------------------------
		//
		//		//----------------------------------
		//		range = new CombinedFilterRange();
		//		range.setRangeName(EXPLANATION_3QT_99_171) ;  // 3rd Qu.: 171.0 
		//		int[] explanationSizeList_3 = {99};
		//		range.setMaxExplanationSize(171);
		//		range.setExplanationSizeList(explanationSizeList_3);
		//		range.setUndefinedWithDefault();
		//		rangeMap.put(range.getRangeName(),range);
		//
		//		//----------------------------------
		//		range = new CombinedFilterRange();
		//		range.setRangeName(EXPLANATION_4_QT_171_2383) ;  //  Max 2383           
		//
		//		int[] explanationSizeList_4 = {171};
		//
		//		range.setMaxExplanationSize(2383);
		//
		//		range.setExplanationSizeList(explanationSizeList_4);
		//		range.setUndefinedWithDefault();
		//		rangeMap.put(range.getRangeName(),range);


		//----------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(EXPLANATION_2_3_4_QT_57_2383) ;  // REMOVED 1Quartile          

		int[] explanationSizeList = {57};

		range.setMaxExplanationSize(2383);
		range.setMinExplanationSize(57);

		range.setExplanationSizeList(explanationSizeList);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;
	}

	static HashMap<String,CombinedFilterRange> setupCombinedDurationConfidenceDifficultyExplanationSize(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		//----------------------------------
		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(COMBINED_DURATION_CONFIDENCE_EXPLANATIONSIZE_1QT_3PERCENT_1QT) ;  //  Exclude 1q and consider only 3% confidence, difficulty cells.


		//Duration
		range.setMaxFirstAnswerDuration(3600);//1hour 
		range.setMinFirstAnswerDuration(167.4);

		range.setMaxSecondThirdAnswerDuration(3600);//1hour
		range.setMinSecondThirdAnswerDuration(69.9);


		//Confidence,Difficulty
		HashMap<String, Tuple>  map = Tuple.generateAllCombinations(5, 5);
		map.remove(new Tuple(5,1).toString());
		map.remove(new Tuple(5,2).toString());
		map.remove(new Tuple(5,3).toString());
		map.remove(new Tuple(4,2).toString());
		map.remove(new Tuple(4,3).toString());
		map.remove(new Tuple(4,4).toString());
		map.remove(new Tuple(3,3).toString());
		map.remove(new Tuple(3,4).toString());
		map.remove(new Tuple(0,4).toString());
		map.remove(new Tuple(0,5).toString());

		range.setConfidenceDifficultyPairMap(map);

		//ExplanationSize
		int[] explanationSizeList_1 = {53}; //Minimal
		range.setExplanationSizeList(explanationSizeList_1);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;
	}


	static HashMap<String,CombinedFilterRange> setupQuestionsToExclude(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		//----------------------------------
		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(CONDITIONAL_CLAUSE_ABOVE_3LINES) ;  // REMOVED QUESTIONS COVERING MORE THAN THREE LOCS       

		int[] excludeQuestions = {7,26,40,44,48,55,62,66,82,84,88,92,98,115,124};//Conditional clause questions with more than 3 Lines of code

		TreeMap<String, String> excludedQuestionMap;

		excludedQuestionMap = new TreeMap<String,String>();
		for(int excluded : excludeQuestions){
			String questionID = new Integer(excluded).toString();
			excludedQuestionMap.put(questionID, questionID);
		}	
		range.setQuestionsToExcludeMap(excludedQuestionMap);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);
		return rangeMap;
	}

	static HashMap<String,CombinedFilterRange> setupDateInterval(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		CombinedFilterRange range = createDateRange(6,FIRST_6_HOURS);
		rangeMap.put(range.getRangeName(), range);
		//----------------------------------
		range = createDateRange(9,FIRST_9_HOURS);
		rangeMap.put(range.getRangeName(), range);
		//----------------------------------
		range = createDateRange(12,FIRST_12_HOURS);
		rangeMap.put(range.getRangeName(), range);
		//----------------------------------
		range = createDateRange(15,FIRST_15_HOURS);
		rangeMap.put(range.getRangeName(), range);
		//----------------------------------
		range = createDateRange(18,FIRST_18_HOURS);
		rangeMap.put(range.getRangeName(), range);
		//----------------------------------
		range = createDateRange(24,FIRST_24_HOURS);
		rangeMap.put(range.getRangeName(), range);
		//----------------------------------
		range = createDateRange(36,FIRST_36_HOURS);
		rangeMap.put(range.getRangeName(), range);
		//----------------------------------
		range = createDateRange(48,FIRST_48_HOURS);
		rangeMap.put(range.getRangeName(), range);

		return rangeMap;

	}

	static CombinedFilterRange createDateRange(long hours, String label){
		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(label) ; 

		String startDateStr = "Tue 2015 Jul 07 08:19:56";
		DateFormat format = new SimpleDateFormat("EEE yyyy MMM dd HH:mm:ss", Locale.ENGLISH);
		try {
			Date startDate = format.parse(startDateStr);
			long startTime = startDate.getTime();
			long endTime = startTime + hours*3600*1000;
			Date endDate = (Date) startDate.clone();
			endDate.setTime(endTime);
			range.setMaxDate(endDate);
			range.setMinDate(startDate);
			range.setUndefinedWithDefault();
			return range;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	//--------------------------------------------------------------------------------------

	private static HashMap<String, Tuple>  map543; //Difficulty 5, 4, 3
	private static HashMap<String, Tuple>  map54;
	private static HashMap<String, Tuple> map1;
	private static HashMap<String, Tuple> map2;
	private static HashMap<String, Tuple> map3;
	private static HashMap<String, Tuple> map12;
	private static HashMap<String, Tuple> map123;
	private static HashMap<String, Tuple> map1234;

	static void initializeDifficultyMaps(){
		//Confidence,Difficulty
		map543 = new HashMap<String, Tuple>();
		map543.put(new Tuple(0,1).toString(), new Tuple(0,1));
		map543.put(new Tuple(1,1).toString(), new Tuple(1,1));
		map543.put(new Tuple(2,1).toString(), new Tuple(2,1));
		map543.put(new Tuple(3,1).toString(), new Tuple(3,1));
		map543.put(new Tuple(4,1).toString(), new Tuple(4,1));
		map543.put(new Tuple(5,1).toString(), new Tuple(5,2));
		map543.put(new Tuple(0,2).toString(), new Tuple(0,2));
		map543.put(new Tuple(1,2).toString(), new Tuple(1,2));
		map543.put(new Tuple(2,2).toString(), new Tuple(2,2));
		map543.put(new Tuple(3,2).toString(), new Tuple(3,2));
		map543.put(new Tuple(4,2).toString(), new Tuple(4,2));
		map543.put(new Tuple(5,2).toString(), new Tuple(5,2));

		//Confidence,Difficulty
		map12 = new HashMap<String, Tuple>();
		map12.put(new Tuple(0,3).toString(), new Tuple(0,3));
		map12.put(new Tuple(1,3).toString(), new Tuple(1,3));
		map12.put(new Tuple(2,3).toString(), new Tuple(2,3));
		map12.put(new Tuple(3,3).toString(), new Tuple(3,3));
		map12.put(new Tuple(4,3).toString(), new Tuple(4,3));
		map12.put(new Tuple(5,3).toString(), new Tuple(5,3));
		map12.put(new Tuple(0,4).toString(), new Tuple(0,4));
		map12.put(new Tuple(1,4).toString(), new Tuple(1,4));
		map12.put(new Tuple(2,4).toString(), new Tuple(2,4));
		map12.put(new Tuple(3,4).toString(), new Tuple(3,4));
		map12.put(new Tuple(4,4).toString(), new Tuple(4,4));
		map12.put(new Tuple(5,4).toString(), new Tuple(5,4));
		map12.put(new Tuple(0,5).toString(), new Tuple(0,5));
		map12.put(new Tuple(1,5).toString(), new Tuple(1,5));
		map12.put(new Tuple(2,5).toString(), new Tuple(2,5));
		map12.put(new Tuple(3,5).toString(), new Tuple(3,5));
		map12.put(new Tuple(4,5).toString(), new Tuple(4,5));
		map12.put(new Tuple(5,5).toString(), new Tuple(5,5));

		//Confidence,Difficulty
		map1 = new HashMap<String, Tuple>();
		map1.put(new Tuple(0,2).toString(), new Tuple(0,2));
		map1.put(new Tuple(1,2).toString(), new Tuple(1,2));
		map1.put(new Tuple(2,2).toString(), new Tuple(2,2));
		map1.put(new Tuple(3,2).toString(), new Tuple(3,2));
		map1.put(new Tuple(4,2).toString(), new Tuple(4,2));
		map1.put(new Tuple(5,2).toString(), new Tuple(5,2));
		map1.put(new Tuple(0,3).toString(), new Tuple(0,3));
		map1.put(new Tuple(1,3).toString(), new Tuple(1,3));
		map1.put(new Tuple(2,3).toString(), new Tuple(2,3));
		map1.put(new Tuple(3,3).toString(), new Tuple(3,3));
		map1.put(new Tuple(4,3).toString(), new Tuple(4,3));
		map1.put(new Tuple(5,3).toString(), new Tuple(5,3));
		map1.put(new Tuple(0,4).toString(), new Tuple(0,4));
		map1.put(new Tuple(1,4).toString(), new Tuple(1,4));
		map1.put(new Tuple(2,4).toString(), new Tuple(2,4));
		map1.put(new Tuple(3,4).toString(), new Tuple(3,4));
		map1.put(new Tuple(4,4).toString(), new Tuple(4,4));
		map1.put(new Tuple(5,4).toString(), new Tuple(5,4));
		map1.put(new Tuple(0,5).toString(), new Tuple(0,5));
		map1.put(new Tuple(1,5).toString(), new Tuple(1,5));
		map1.put(new Tuple(2,5).toString(), new Tuple(2,5));
		map1.put(new Tuple(3,5).toString(), new Tuple(3,5));
		map1.put(new Tuple(4,5).toString(), new Tuple(4,5));
		map1.put(new Tuple(5,5).toString(), new Tuple(5,5));

		//Confidence,Difficulty
		map2 = new HashMap<String, Tuple>();
		map2.put(new Tuple(0,1).toString(), new Tuple(0,1));
		map2.put(new Tuple(1,1).toString(), new Tuple(1,1));
		map2.put(new Tuple(2,1).toString(), new Tuple(2,1));
		map2.put(new Tuple(3,1).toString(), new Tuple(3,1));
		map2.put(new Tuple(4,1).toString(), new Tuple(4,1));
		map2.put(new Tuple(5,1).toString(), new Tuple(5,1));
		map2.put(new Tuple(0,3).toString(), new Tuple(0,3));
		map2.put(new Tuple(1,3).toString(), new Tuple(1,3));
		map2.put(new Tuple(2,3).toString(), new Tuple(2,3));
		map2.put(new Tuple(3,3).toString(), new Tuple(3,3));
		map2.put(new Tuple(4,3).toString(), new Tuple(4,3));
		map2.put(new Tuple(5,3).toString(), new Tuple(5,3));
		map2.put(new Tuple(0,4).toString(), new Tuple(0,4));
		map2.put(new Tuple(1,4).toString(), new Tuple(1,4));
		map2.put(new Tuple(2,4).toString(), new Tuple(2,4));
		map2.put(new Tuple(3,4).toString(), new Tuple(3,4));
		map2.put(new Tuple(4,4).toString(), new Tuple(4,4));
		map2.put(new Tuple(5,4).toString(), new Tuple(5,4));
		map2.put(new Tuple(0,5).toString(), new Tuple(0,5));
		map2.put(new Tuple(1,5).toString(), new Tuple(1,5));
		map2.put(new Tuple(2,5).toString(), new Tuple(2,5));
		map2.put(new Tuple(3,5).toString(), new Tuple(3,5));
		map2.put(new Tuple(4,5).toString(), new Tuple(4,5));
		map2.put(new Tuple(5,5).toString(), new Tuple(5,5));

		//Confidence,Difficulty
		map3 = new HashMap<String, Tuple>();
		map3.put(new Tuple(0,1).toString(), new Tuple(0,1));
		map3.put(new Tuple(1,1).toString(), new Tuple(1,1));
		map3.put(new Tuple(2,1).toString(), new Tuple(2,1));
		map3.put(new Tuple(3,1).toString(), new Tuple(3,1));
		map3.put(new Tuple(4,1).toString(), new Tuple(4,1));
		map3.put(new Tuple(5,1).toString(), new Tuple(5,1));
		map3.put(new Tuple(0,2).toString(), new Tuple(0,2));
		map3.put(new Tuple(1,2).toString(), new Tuple(1,2));
		map3.put(new Tuple(2,2).toString(), new Tuple(2,2));
		map3.put(new Tuple(3,2).toString(), new Tuple(3,2));
		map3.put(new Tuple(4,2).toString(), new Tuple(4,2));
		map3.put(new Tuple(5,2).toString(), new Tuple(5,2));
		map3.put(new Tuple(0,4).toString(), new Tuple(0,4));
		map3.put(new Tuple(1,4).toString(), new Tuple(1,4));
		map3.put(new Tuple(2,4).toString(), new Tuple(2,4));
		map3.put(new Tuple(3,4).toString(), new Tuple(3,4));
		map3.put(new Tuple(4,4).toString(), new Tuple(4,4));
		map3.put(new Tuple(5,4).toString(), new Tuple(5,4));
		map3.put(new Tuple(0,5).toString(), new Tuple(0,5));
		map3.put(new Tuple(1,5).toString(), new Tuple(1,5));
		map3.put(new Tuple(2,5).toString(), new Tuple(2,5));
		map3.put(new Tuple(3,5).toString(), new Tuple(3,5));
		map3.put(new Tuple(4,5).toString(), new Tuple(4,5));
		map3.put(new Tuple(5,5).toString(), new Tuple(5,5));

		//Confidence,Difficulty
		map123 = new HashMap<String, Tuple>();
		map123.put(new Tuple(0,4).toString(), new Tuple(0,4));
		map123.put(new Tuple(1,4).toString(), new Tuple(1,4));
		map123.put(new Tuple(2,4).toString(), new Tuple(2,4));
		map123.put(new Tuple(3,4).toString(), new Tuple(3,4));
		map123.put(new Tuple(4,4).toString(), new Tuple(4,4));
		map123.put(new Tuple(5,4).toString(), new Tuple(5,4));
		map123.put(new Tuple(0,5).toString(), new Tuple(0,5));
		map123.put(new Tuple(1,5).toString(), new Tuple(1,5));
		map123.put(new Tuple(2,5).toString(), new Tuple(2,5));
		map123.put(new Tuple(3,5).toString(), new Tuple(3,5));
		map123.put(new Tuple(4,5).toString(), new Tuple(4,5));
		map123.put(new Tuple(5,5).toString(), new Tuple(5,5));

		//Confidence,Difficulty
		map1234 = new HashMap<String, Tuple>();
		map1234.put(new Tuple(0,5).toString(), new Tuple(0,5));
		map1234.put(new Tuple(1,5).toString(), new Tuple(1,5));
		map1234.put(new Tuple(2,5).toString(), new Tuple(2,5));
		map1234.put(new Tuple(3,5).toString(), new Tuple(3,5));
		map1234.put(new Tuple(4,5).toString(), new Tuple(4,5));
		map1234.put(new Tuple(5,5).toString(), new Tuple(5,5));

		//Confidence,Difficulty
		map54= new HashMap<String, Tuple>();
		map54.put(new Tuple(0,3).toString(), new Tuple(0,3));
		map54.put(new Tuple(1,3).toString(), new Tuple(1,3));
		map54.put(new Tuple(2,3).toString(), new Tuple(2,3));
		map54.put(new Tuple(3,3).toString(), new Tuple(3,3));
		map54.put(new Tuple(4,3).toString(), new Tuple(4,3));
		map54.put(new Tuple(5,3).toString(), new Tuple(5,3));
		map54.put(new Tuple(0,1).toString(), new Tuple(0,1));
		map54.put(new Tuple(1,1).toString(), new Tuple(1,1));
		map54.put(new Tuple(2,1).toString(), new Tuple(2,1));
		map54.put(new Tuple(3,1).toString(), new Tuple(3,1));
		map54.put(new Tuple(4,1).toString(), new Tuple(4,1));
		map54.put(new Tuple(5,1).toString(), new Tuple(5,2));
		map54.put(new Tuple(0,2).toString(), new Tuple(0,2));
		map54.put(new Tuple(1,2).toString(), new Tuple(1,2));
		map54.put(new Tuple(2,2).toString(), new Tuple(2,2));
		map54.put(new Tuple(3,2).toString(), new Tuple(3,2));
		map54.put(new Tuple(4,2).toString(), new Tuple(4,2));
		map54.put(new Tuple(5,2).toString(), new Tuple(5,2));
	}


	static HashMap<String,CombinedFilterRange> getMostDifficultySkill(){
		initializeDifficultyMaps();
		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();


		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_80_DIFFICULTY_ALL);
		range.setMaxWorkerScore(5);
		range.setMinWorkerScore(4);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------------------------------------

		range.setRangeName(WORKER_SCORE_100_DIFFICULTY_ALL);
		range.setMaxWorkerScore(5);
		range.setMinWorkerScore(5);

		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(),range);

		//----------------------------------------------------------------

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_DIFFICULTY_5_4_3);
		range.setMaxWorkerScore(5);
		range.setMinWorkerScore(5);
		range.setUndefinedWithDefault(); 

		range.setConfidenceDifficultyPairMap(map543);
		rangeMap.put(range.getRangeName(),range);

		//---------------------------------------------

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_60_80_DIFFICULTY_1_2); 
		range.setMaxWorkerScore(4);
		range.setMinWorkerScore(4);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map12);
		rangeMap.put(range.getRangeName(),range);

		//---------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_80_DIFFICULTY_1_2_3); 
		range.setMaxWorkerScore(4);
		range.setMinWorkerScore(4);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map123);
		rangeMap.put(range.getRangeName(),range);

		//---------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_80_DIFFICULTY_1_2_3_4); 
		range.setMaxWorkerScore(4);
		range.setMinWorkerScore(4);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map1234);
		rangeMap.put(range.getRangeName(),range);

		//---------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_60_DIFFICULTY_1_2); 
		range.setMaxWorkerScore(3);
		range.setMinWorkerScore(3);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map12);
		rangeMap.put(range.getRangeName(),range);

		//-----------------------------------------------

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_80_DIFFICULTY_5_4_3);
		range.setMaxWorkerScore(5);
		range.setMinWorkerScore(4);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map543);
		rangeMap.put(range.getRangeName(),range);

		//-----------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_DIFFICULTY_1_2_3_4); 
		range.setMaxWorkerScore(5);
		range.setMinWorkerScore(5);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map1234);
		rangeMap.put(range.getRangeName(),range);

		//-----------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_DIFFICULTY_1_2_3); 
		range.setMaxWorkerScore(5);
		range.setMinWorkerScore(5);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map123);
		rangeMap.put(range.getRangeName(),range);
		//-----------------------------------------------

		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_80_DIFFICULTY_5_4);
		range.setMaxWorkerScore(5);
		range.setMinWorkerScore(4);
		range.setUndefinedWithDefault();




		range.setConfidenceDifficultyPairMap(map54);
		rangeMap.put(range.getRangeName(),range);
		//---------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_60_DIFFICULTY_1_2_3); 
		range.setMaxWorkerScore(3);
		range.setMinWorkerScore(3);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map123);
		rangeMap.put(range.getRangeName(),range);
		//-----------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_60_DIFFICULTY_1_2_3_4); 
		range.setMaxWorkerScore(3);
		range.setMinWorkerScore(3);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map1234);
		rangeMap.put(range.getRangeName(),range);
		//---------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_60_DIFFICULTY_5_4); 
		range.setMaxWorkerScore(3);
		range.setMinWorkerScore(3);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map54);
		rangeMap.put(range.getRangeName(),range);

		//---------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_80_DIFFICULTY_5_4); 
		range.setMaxWorkerScore(4);
		range.setMinWorkerScore(4);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map54);
		rangeMap.put(range.getRangeName(),range);

		//---------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_80_DIFFICULTY_1_2); 
		range.setMaxWorkerScore(4);
		range.setMinWorkerScore(43);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map12);
		rangeMap.put(range.getRangeName(),range);
		//-----------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(WORKER_SCORE_100_DIFFICULTY_1_2); 
		range.setMaxWorkerScore(4);
		range.setMinWorkerScore(4);
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map12);
		rangeMap.put(range.getRangeName(),range);

		return rangeMap;
	}


	static HashMap<String,CombinedFilterRange> getMostDifficultyProfession(){

		initializeDifficultyMaps();
		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(UNDERGRAD_DIFFICULTY_1_2_3); 
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist","Other","Graduate_Student"});
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map123);
		rangeMap.put(range.getRangeName(),range);

		//------------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(GRAD_STUDENTS_DIFFICULTY_1_2_3_4); 
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist","Other","Undergraduate_Student"});
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map1234);
		rangeMap.put(range.getRangeName(),range);

		//------------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(NON_STUDENTS_DIFFICULTY_ALL); 
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();

		rangeMap.put(range.getRangeName(),range);

		//------------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(NON_STUDENTS_DIFFICULTY_1); 
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		range.setConfidenceDifficultyPairMap(map1);
		rangeMap.put(range.getRangeName(),range);

		//------------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(NON_STUDENTS_DIFFICULTY_2); 
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		range.setConfidenceDifficultyPairMap(map2);
		rangeMap.put(range.getRangeName(),range);

		//------------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(NON_STUDENTS_DIFFICULTY_3); 
		range.setProfessionExclusionList(new String[] {"Graduate_Student","Undergraduate_Student"});
		range.setUndefinedWithDefault();
		range.setConfidenceDifficultyPairMap(map3);
		rangeMap.put(range.getRangeName(),range);


		//------------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(STUDENTS_DIFFICULTY_1_2_3); 
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist","Other"});
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map123);
		rangeMap.put(range.getRangeName(),range);

		//------------------------------------------------
		range = new CombinedFilterRange();
		range.setRangeName(STUDENTS_DIFFICULTY_1_2); 
		range.setProfessionExclusionList(new String[] {"Professional_Developer","Hobbyist","Other"});
		range.setUndefinedWithDefault();

		range.setConfidenceDifficultyPairMap(map12);
		rangeMap.put(range.getRangeName(),range);



		return rangeMap;
	}

	//HIGH DIFFICULTY 60
	//HIG CONFIDENCE 60

	//HIGH DIFFICULTY 80
	//HIG CONFIDENCE 80

	//HIGH DIFFICULTY 100
	//HIG CONFIDENCE 100


	static HashMap<String,CombinedFilterRange> setupMaxAnswers(int max){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		CombinedFilterRange range = new CombinedFilterRange();
		range.setRangeName(MAX_ANSWERS);
		range.setMaxAnswers(max);

		int maxList[] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};

		range.setMaxAnswerList(maxList);
		range.setUndefinedWithDefault();
		rangeMap.put(range.getRangeName(), range);
		return rangeMap;
	}

}

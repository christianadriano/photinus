package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * Generate two types of filters
 * 
 * Type-1 
 * Motivation: inspect the accuracy of pairs Profession, YoE
 * See methods generate
 * Generate filters that select answers by workers' Years of Profession (YoE) and
 * worker profession (PROFESSIONAL_DEVELOPER, HOBBYIST, GRADUATE_STUDENT, UNDERGRADUATE_STUDENT, OTHERS)
 * 
 * Type-2
 * Motivation: verify if certain subcrowds located all faults
 * UNION of one or more subcrowds (see method composeSubcrowds)
 * 
 *  
 * @author adrianoc
 */
public class FilterCombination_Profession_YoE {

	public static String[] STUDENT_LIST = {"Graduate_Student","Undergraduate_Student"};
	public static String[] NON_STUDENT_LIST = {"Professional_Developer","Hobbyist","Other"};
	public static String[] ALL_PROFESSIONS_LIST = {"Professional_Developer","Hobbyist","Other","Graduate_Student","Undergraduate_Student"};


	//First position is the First quartile, Median, Third quartile.
	public static Double[] Professional_Developer_YoE_Quartiles = {4.0,6.0,12.0}; //Data obtained from the current distribution of the data
	public static Double[] Graduate_Student_YoE_Quartiles = {1.0,2.0,5.0}; 
	public static Double[] Undergraduate_Student_YoE_Quartiles = {1.0,2.0,3.0};

	public static Double[] Hobbyist_YoE_Quartiles = {1.0,2.0,6.0};
	public static Double[] Other_YoE_Quartiles = {0.0,1.0,4.75};
	public static Double[] Students_YoE_Quartiles = {1.0,2.0,4.0};
	public static Double[] NonStudents_YoE_Quartiles = {1.0,3.0,9.0};

	public static HashMap<String,HashMap<Integer,Double>> professionsYoEQuartileMap = new HashMap<String,HashMap<Integer,Double>>();

	//--------------------------------------------------------------------

	public static double getQuartileLevel(String profession, int level){
		return professionsYoEQuartileMap.get(profession).get(level);
	}

	public static void initializeProfessionsYoEQuartileMap(){

		//Professional_Developer
		HashMap<Integer,Double> quartileValueMap;;
		quartileValueMap = addQuartileValues(Professional_Developer_YoE_Quartiles);
		professionsYoEQuartileMap.put("Professional_Developer", quartileValueMap);

		//Hobbyist
		quartileValueMap = addQuartileValues(Hobbyist_YoE_Quartiles);
		professionsYoEQuartileMap.put("Hobbyist", quartileValueMap);

		//Graduate_Student
		quartileValueMap = addQuartileValues(Graduate_Student_YoE_Quartiles);
		professionsYoEQuartileMap.put("Graduate_Student", quartileValueMap);

		//Undergraduate_Student
		quartileValueMap = addQuartileValues(Undergraduate_Student_YoE_Quartiles);
		professionsYoEQuartileMap.put("Undergraduate_Student", quartileValueMap);

		//Other
		quartileValueMap = addQuartileValues(Other_YoE_Quartiles);
		professionsYoEQuartileMap.put("Other", quartileValueMap);
	}

	private static HashMap<Integer,Double> addQuartileValues(Double[] list){

		HashMap<Integer,Double> map =  new HashMap<Integer,Double>();

		for(int i=0; i<3;i++){
			Double value =  list[i];
			map.put(i+1, value);
		}
		return map;
	}

	//-----------------------------------------------------------------------------------
	// TYPE-1 FILTER

	public static HashMap<String,CombinedFilterRange> generateFilter(){
		initializeProfessionsYoEQuartileMap();
		HashMap<String,CombinedFilterRange> filterMap = generateProfessionYoEFilters();
		return filterMap;
	}

	private static HashMap<String,CombinedFilterRange> generateProfessionYoEFilters(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		for(int i =0;i<ALL_PROFESSIONS_LIST.length;i++){
			String profession = ALL_PROFESSIONS_LIST[i];
			for(int quartile=1;quartile<=5;quartile++){
				CombinedFilterRange range = getRangeProfessions(profession);
				range.setRangeName(getName(profession,quartile));
				
				if(quartile==1){
					range.setMinWorkerYearsOfExperience(0);
					range.setMaxWorkerYearsOfExperience(professionsYoEQuartileMap.get(profession).get(1));
				}
				else
				if(quartile==2){
					range.setMinWorkerYearsOfExperience(professionsYoEQuartileMap.get(profession).get(1));
					range.setMaxWorkerYearsOfExperience(professionsYoEQuartileMap.get(profession).get(2));
				}
				else
				if(quartile==3){
					range.setMinWorkerYearsOfExperience(professionsYoEQuartileMap.get(profession).get(2));
					range.setMaxWorkerYearsOfExperience(professionsYoEQuartileMap.get(profession).get(3));
				}
				else
				if(quartile==4){
					range.setMinWorkerYearsOfExperience(professionsYoEQuartileMap.get(profession).get(3));
					range.setMaxWorkerYearsOfExperience(100.0); //No limit to the maximum years of experience
				}
				else
				if(quartile==5){//Consider all quartiles, no filtering
					range.setMinWorkerYearsOfExperience(0.0);
					range.setMaxWorkerYearsOfExperience(100.0); //No limit to the maximum years of experience
				}

				rangeMap.put(range.getRangeName(),range);
			}
		}
		return rangeMap;
	}	

	private static String getName(String profession, int quartile){
		return  profession+"-quartile-"+quartile;
	}


	/**  
	 * @param profession the profession name that has to be kept, hence it will not be in the list
	 * @return the list of all professions but the one provided as parameter
	 */
	private static CombinedFilterRange getRangeProfessions(String profession){

		String[] professionExclusionList = removeProfession(ALL_PROFESSIONS_LIST,profession);	
		CombinedFilterRange range = new CombinedFilterRange();

		range.setProfessionExclusionList(professionExclusionList); 
		range.setUndefinedWithDefault();

		return range;
	}


	private static String[] removeProfession(String[] list, String profession){

		int length = list.length-1;
		String[] resultList = new String[length];

		int j=0;
		for(int i=0; i<list.length; i++){
			if(!list[i].matches(profession)){
				resultList[j] = list[i];
				j++;
			}
		}
		return resultList;
	}

	private static FilterCombination generateCombination (CombinedFilterRange range){
		FilterCombination combination = new FilterCombination();
		combination.addFilterParam(FilterCombination.FIRST_ANSWER_DURATION, range.getMaxFirstAnswerDuration(), range.getMinFirstAnswerDuration());
		combination.addFilterParam(FilterCombination.SECOND_THIRD_ANSWER_DURATION, range.getMaxSecondThirdAnswerDuration(), range.getMinSecondThirdAnswerDuration());
		combination.addFilterParam(FilterCombination.CONFIDENCE_DIFFICULTY_PAIRS,range.getConfidenceDifficultyPairList());
		combination.addFilterParam(FilterCombination.CONFIDENCE_LEVEL, range.getMaxConfidence(), range.getMinConfidence());
		combination.addFilterParam(FilterCombination.DIFFICULTY_LEVEL,range.getMaxDifficulty(),range.getMinDifficulty());
		combination.addFilterParam(FilterCombination.EXPLANATION_SIZE, range.getMaxExplanationSize(), range.getMinExplanationSize());
		combination.addFilterParam(FilterCombination.WORKER_SCORE_EXCLUSION, range.getWorkerScoreExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_SCORE, range.getMaxWorkerScore(), range.getMinWorkerScore());
		combination.addFilterParam(FilterCombination.WORKER_IDK, range.getMaxWorkerIDKPercentage(),range.getMinWorkerIDKPercentage());
		combination.addFilterParam(FilterCombination.WORKER_PROFESSION, range.getProfessionExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_YEARS_OF_EXEPERIENCE, range.getMaxYearsOfExperience(), range.getMinWorkerYearsOfExperience());
		combination.addFilterParam(FilterCombination.EXCLUDED_QUESTIONS, range.getQuestionsToExcludeMap());
		combination.addFilterParam(FilterCombination.FIRST_HOURS, range.getMaxDate(),range.getMinDate());
		combination.addFilterParam(FilterCombination.MAX_ANSWERS, 20, 0);
		return combination;
	}

	//-----------------------------------------------------------------------------------
	// TYPE -2 FILTER
	public static ArrayList<SubCrowd> composeSubCrowds(){
		initializeProfessionsYoEQuartileMap();
		ArrayList<SubCrowd>  subCrowdList = new ArrayList<SubCrowd>();
		//Generate ranges for filtering
		HashMap<String, CombinedFilterRange> map = AttributeRangeGenerator.getMostExperiencedByProfession();
		CombinedFilterRange range;
		FilterCombination combination;
		SubCrowd crowd;

		//------------------------------------------------
		crowd = new SubCrowd();
		crowd.name = "Removed Undergrad_YoE_1stQT and Grad_YoE_1stQT";

		range = map.get(AttributeRangeGenerator.UNDERGRADUATE_STUDENT_YoE_QT_2_UP);			
		combination = generateCombination(range);	
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.GRADUATE_STUDENT_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.NON_STUDENTS_YoE_ALL);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		subCrowdList.add(crowd);

		//------------------------------------------------	
		crowd = new SubCrowd();
		crowd.name = "Removed All_YoE_1stQT";

		range = map.get(AttributeRangeGenerator.UNDERGRADUATE_STUDENT_YoE_QT_2_UP);			
		combination = generateCombination(range);	
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.GRADUATE_STUDENT_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.PROFESSIONAL_DEVELOPERS_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.HOBBYIST_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.OTHER_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		subCrowdList.add(crowd);

		//------------------------------------------------
		crowd = new SubCrowd();
		crowd.name = "Removed ALL but professionals above YoE 1stQT";

		range = map.get(AttributeRangeGenerator.PROFESSIONAL_DEVELOPERS_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		subCrowdList.add(crowd);

		//------------------------------------------------
		crowd = new SubCrowd();
		crowd.name = "Only non-students above YoE 1stQT";

		range = map.get(AttributeRangeGenerator.PROFESSIONAL_DEVELOPERS_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.HOBBYIST_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.OTHER_YoE_QT_2_UP);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		subCrowdList.add(crowd);

		return subCrowdList;

	}



}

package edu.uci.ics.sdcl.firefly.report.predictive;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Microtask;

/**
 * 
 * Generate two types of filters
 * 
 * Type-1 
 * See methods generate
 * Generate filters that select answers by workers' score and
 * questions difficulty. Workers' score consist in the grade 
 * they got in the qualification test. The difficulty of question is
 * the level chosen by each worker. 
 * 
 * Type-2
 * UNION of one or more subcrowds (see method composeSubcrowds)
 * 
 *  
 * @author adrianoc
 *
 */
public class FilterCombination_Profession_DifficultyMatrix {

	public static String WORKER_SCORE_100_DIFFICULTY_5 =  "WORKER_SCORE_100_DIFFICULTY_5";
	public static String WORKER_SCORE_100_DIFFICULTY_4 =  "WORKER_SCORE_100_DIFFICULTY_4";
	public static String WORKER_SCORE_100_DIFFICULTY_3 =  "WORKER_SCORE_100_DIFFICULTY_3";
	public static String WORKER_SCORE_100_DIFFICULTY_2 =  "WORKER_SCORE_100_DIFFICULTY_2";
	public static String WORKER_SCORE_100_DIFFICULTY_1 =  "WORKER_SCORE_100_DIFFICULTY_1";

	public static String WORKER_SCORE_80_DIFFICULTY_5 =  "WORKER_SCORE_100_DIFFICULTY_5";
	public static String WORKER_SCORE_80_DIFFICULTY_4 =  "WORKER_SCORE_100_DIFFICULTY_4";
	public static String WORKER_SCORE_80_DIFFICULTY_3 =  "WORKER_SCORE_100_DIFFICULTY_3";
	public static String WORKER_SCORE_80_DIFFICULTY_2 =  "WORKER_SCORE_100_DIFFICULTY_2";
	public static String WORKER_SCORE_80_DIFFICULTY_1 =  "WORKER_SCORE_100_DIFFICULTY_1";

	public static String WORKER_SCORE_60_DIFFICULTY_5 =  "WORKER_SCORE_60_DIFFICULTY_5";
	public static String WORKER_SCORE_60_DIFFICULTY_4 =  "WORKER_SCORE_60_DIFFICULTY_4";
	public static String WORKER_SCORE_60_DIFFICULTY_3 =  "WORKER_SCORE_60_DIFFICULTY_3";
	public static String WORKER_SCORE_60_DIFFICULTY_2 =  "WORKER_SCORE_60_DIFFICULTY_2";
	public static String WORKER_SCORE_60_DIFFICULTY_1 =  "WORKER_SCORE_60_DIFFICULTY_1";

	
	public static String[] STUDENT_LIST = {"Graduate_Student","Undergraduate_Student"};
	public static String[] NON_STUDENT_LIST = {"Professional_Developer","Hobbyist","Other"};
	public static String[] ALL_PROFESSIONS_LIST = {"Professional_Developer","Hobbyist","Other","Graduate_Student","Undergraduate_Student"};
	
	
	/** Produces a map with all pairs that must be excluded. The only level that
	 * should remain is the one provided as parameter
	 * 
	 * @param difficultyObj only difficulty level that should remain
	 * @return
	 */
	public static HashMap<String, Tuple> getExclusionTupleMap(Integer difficultyObj){

		int[] difficultyList = {1,2,3,4,5};
		int[] exclusionList = removeDifficulty(difficultyList,difficultyObj.intValue());

		HashMap<String, Tuple>  map = new HashMap<String, Tuple>();
		for(int i=0;i<exclusionList.length;i++){
			int difficulty = exclusionList[i];
			map.put(new Tuple(0,difficulty).toString(), new Tuple(0,difficulty));
			map.put(new Tuple(1,difficulty).toString(), new Tuple(1,difficulty));
			map.put(new Tuple(2,difficulty).toString(), new Tuple(2,difficulty));
			map.put(new Tuple(3,difficulty).toString(), new Tuple(3,difficulty));
			map.put(new Tuple(4,difficulty).toString(), new Tuple(4,difficulty));
			map.put(new Tuple(5,difficulty).toString(), new Tuple(5,difficulty));
		}
		return map;
	}

	
	
	public static CombinedFilterRange getRangeProfessions(String profession){

		String[] professionList = {"Professional_Developer","Hobbyist","Other","Graduate_Student","Undergraduate_Student"};
		String[] professionExclusionList = removeProfession(professionList,profession);	
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

	private static int[] removeDifficulty(int[] list, int difficulty){

		int length = list.length-1;
		int[] resultList = new int[length];

		int j=0;
		for(int i=0; i<list.length; i++){
			if(list[i]!=difficulty){
				resultList[j] = list[i];
				j++;
			}
		}
		return resultList;
	}

	private static String getName(String profession, int difficulty){
		return  profession+"-DIFFICULTY-"+difficulty;
	}
	
	public static HashMap<String,CombinedFilterRange> generateFilter(){
		 HashMap<String,CombinedFilterRange> filterMap = generateProfessionFilters();
		 filterMap.putAll(generateProfesssionSETfilter(STUDENT_LIST,"non_students"));
		 filterMap.putAll(generateProfesssionSETfilter(NON_STUDENT_LIST,"students"));
		 return filterMap;
	}

	private static HashMap<String,CombinedFilterRange> generateProfessionFilters(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();
		
		for(int i =0;i<ALL_PROFESSIONS_LIST.length;i++){
			String profession = ALL_PROFESSIONS_LIST[i];
			for(int difficulty=1;difficulty<=5;difficulty++){
				CombinedFilterRange range = getRangeProfessions(profession);
				range.setRangeName(getName(profession,difficulty));
				range.setConfidenceDifficultyPairMap(getExclusionTupleMap(difficulty));
				rangeMap.put(range.getRangeName(),range);
			}
		}
		return rangeMap;
	}	

	
	private static HashMap<String,CombinedFilterRange> generateProfesssionSETfilter(String[] exclusionList,String name){
		
		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();
				
			for(int difficulty=1;difficulty<=5;difficulty++){
				
				CombinedFilterRange range = new CombinedFilterRange();
				range.setProfessionExclusionList(exclusionList); 
				range.setUndefinedWithDefault();
				range.setRangeName(getName(name,difficulty));
				range.setConfidenceDifficultyPairMap(getExclusionTupleMap(difficulty));
				rangeMap.put(range.getRangeName(),range);
			}
		
		return rangeMap;
	}

	

	
	public static String tupleMapToString(HashMap<String, Tuple> tupleMap){

		String result="";

		Iterator<String> iter = tupleMap.keySet().iterator();
		while(iter.hasNext()){
			result = result + ";" + iter.next();
		}
		return result;
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

	public static ArrayList<SubCrowd> composeSubCrowds(){

		ArrayList<SubCrowd>  subCrowdList = new ArrayList<SubCrowd>();
		HashMap<String, CombinedFilterRange> map = AttributeRangeGenerator.getMostDifficultyProfession();
		CombinedFilterRange range;
		FilterCombination combination;

		//------------------------------------------------
		//
		SubCrowd crowd = new SubCrowd();
		crowd.name = "Removed Undergrad_diff45 and Grad_diff5";

		range = map.get(AttributeRangeGenerator.UNDERGRAD_DIFFICULTY_1_2_3);			
		combination = generateCombination(range);	
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.GRAD_STUDENTS_DIFFICULTY_1_2_3_4);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.NON_STUDENTS_DIFFICULTY_ALL);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);

		//------------------------------------------------
		//Removed students_45
		crowd = new SubCrowd();
		crowd.name = "Removed students_45";

		range = map.get(AttributeRangeGenerator.STUDENTS_DIFFICULTY_1_2_3);			
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.NON_STUDENTS_DIFFICULTY_ALL);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);

		//------------------------------------------------
		//Removed students_345
		crowd = new SubCrowd();
		crowd.name = "Removed students_345";

		range = map.get(AttributeRangeGenerator.STUDENTS_DIFFICULTY_1_2);			
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.NON_STUDENTS_DIFFICULTY_ALL);
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);
		
		return subCrowdList;

	}

	/** Used for testing */
	public static void main(String args[]){

		HashMap<String,CombinedFilterRange> map = FilterCombination_Profession_DifficultyMatrix.generateFilter();
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			CombinedFilterRange range = map.get(key);
			String name = range.getRangeName();
			String[] professionList = range.getProfessionExclusionList();
			HashMap<String, Tuple> tupleMap = range.getConfidenceDifficultyPairList();

			System.out.println("key:"+name+"  excluded: "+professionList[0]+","+professionList[1] + ", confidence:difficulty =" + tupleMapToString(tupleMap));
		}
	}


}

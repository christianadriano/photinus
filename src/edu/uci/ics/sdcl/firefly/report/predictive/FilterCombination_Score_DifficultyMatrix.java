package edu.uci.ics.sdcl.firefly.report.predictive;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * Generate filters that select answers by workers' score and
 * questions difficulty. Workers' score consist in the grade 
 * they got in the qualification test. The difficulty of question is
 * the level chosen by each worker. 
 *  
 * @author adrianoc
 *
 */
public class FilterCombination_Score_DifficultyMatrix {

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

	/** Produces a map with all pairs that must be excluded. The only level that
	 * should remain is the one provided as parameter
	 * 
	 * @param difficultyObj only difficulty level that should remain
	 * @return
	 */
	public static HashMap<String, Tuple> getExclusionTupleMap(Integer difficultyObj){

		int[] difficultyList = {1,2,3,4,5};
		int[] exclusionList = remove(difficultyList,difficultyObj.intValue());

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

	public static CombinedFilterRange getRangeScore(Integer score){

		int[] scoreList = {3,4,5};
		int[] exclusionList = remove(scoreList,score.intValue());	
		CombinedFilterRange range = new CombinedFilterRange();

		range.setMaxWorkerScore(score.intValue());
		range.setMinWorkerScore(score.intValue());
		//range.setWorkerScoreExclusionList(exclusionList);
		range.setWorkerScoreList(new int[]{score.intValue()});
		range.setUndefinedWithDefault();

		return range;
	}


	private static int[] remove(int[] list, int score){

		int length = list.length-1;
		int[] resultList = new int[length];

		int j=0;
		for(int i=0; i<list.length; i++){
			if(list[i]!=score){
				resultList[j] = list[i];
				j++;
			}
		}
		return resultList;
	}


	public static String getName(int score, int difficulty){

		String name = "WORKER_SCORE_";


		Double percentScore = new Double(score/5.0) * 100;
		String percentScoreStr = new DecimalFormat("#").format(percentScore);

		name = name+percentScoreStr+"_DIFFICULTY_"+difficulty;

		return name;
	}

	public static HashMap<String,CombinedFilterRange> generate(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		for(int score =3;score<=5;score++){
			for(int difficulty=1;difficulty<=5;difficulty++){
				CombinedFilterRange range = getRangeScore(score);
				range.setRangeName(getName(score,difficulty));
				range.setConfidenceDifficultyPairMap(getExclusionTupleMap(difficulty));
				rangeMap.put(range.getRangeName(),range);
			}
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

	public static void main(String args[]){

		HashMap<String,CombinedFilterRange> map = FilterCombination_Score_DifficultyMatrix.generate();
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			CombinedFilterRange range = map.get(key);
			String name = range.getRangeName();
			int[] scoreList = range.getWorkerScoreList();
			int[] excludedList = range.getWorkerScoreExclusionList();
			HashMap<String, Tuple> tupleMap = range.getConfidenceDifficultyPairList();

			System.out.println("key:"+name+" score:"+scoreList[0] + " excluded: "+excludedList[0]+","+excludedList[1] + ", confidence:difficulty =" + tupleMapToString(tupleMap));
		}
	}

}

package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.FilterCombination;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapValidator;

/**
 * Applies an OR filter returning the resulting set.
 * 
 * @author adrianoc
 *
 */
public class OR_Filter {

	/** Used this method to obtain data from disjoint sets, for instance, skill 80 and difficulty 1 OR skill 100, difficulty 5 */
	public static HashMap<String, Microtask> apply(HashMap<String, Microtask> microtaskMap, ArrayList<FilterCombination> filterList){

		if(filterList.size()<2)
			return null;

		FilterCombination filter1 = filterList.get(0).getFilter();
		FilterCombination filter2 = filterList.get(1).getFilter();
		
		ArrayList<HashMap<String, Microtask>> mergeMapList = new ArrayList<HashMap<String, Microtask>>(); //Maps that will be merged.
		mergeMapList.add((HashMap<String, Microtask>) filter1.apply(microtaskMap));
		mergeMapList.add((HashMap<String, Microtask>) filter2.apply(microtaskMap));
		HashMap<String, Microtask> map12 = MicrotaskMapUtil.mergeMapList(microtaskMap, mergeMapList);

		if(filterList.size()==2){
		//	printFilterOutcomes(mergeMapList,map12);
			return map12;
		}
		else{//There are more filters to apply and OR
			mergeMapList = new 	ArrayList<HashMap<String, Microtask>>();
			mergeMapList.add(map12);

			for(int i=2;i<filterList.size();i++){
				FilterCombination combination = filterList.get(i);
				FilterCombination filter = combination.getFilter();
				HashMap<String, Microtask> map = (HashMap<String, Microtask>) filter.apply(microtaskMap);
				mergeMapList.add(map);
			}
			HashMap<String, Microtask> resultMap = MicrotaskMapUtil.mergeMapList(microtaskMap, mergeMapList);
			//printFilterOutcomes(mergeMapList,resultMap);
			
			//For testing to guarantee that Maps are distinct
			MicrotaskMapValidator validator =  new MicrotaskMapValidator();
			if(validator.haveDuplicates(mergeMapList))
				return null;
		    else 
				return resultMap;
		}
	}


	/** Just prints the number of answers of each map in the console */
	private static void printFilterOutcomes(ArrayList<HashMap<String, Microtask>> mergeMapList,
											HashMap<String, Microtask> finalMerged ){

		System.out.println("Answers,");
		int i=0;
		for(HashMap<String, Microtask> map: mergeMapList){
			i++;
			System.out.println("map_"+i+": "+MicrotaskMapUtil.countAnswers(map));
		}
		System.out.println(", union:"+ MicrotaskMapUtil.countAnswers(finalMerged));
	}


}

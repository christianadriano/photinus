package edu.uci.ics.sdcl.firefly.report.predictive.montecarlo;


import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;
import edu.uci.ics.sdcl.firefly.report.predictive.AttributeRangeGenerator;
import edu.uci.ics.sdcl.firefly.report.predictive.CombinedFilterRange;
import edu.uci.ics.sdcl.firefly.report.predictive.FilterCombination;
import edu.uci.ics.sdcl.firefly.report.predictive.FilterGenerator;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;

/**
 * The simulation has the following steps:
 * 1- Generate sub-crowd filters
 * 2- Obtain the microtasks for each of these sub-crowds
 * 3- Obtain the maximum common number of answers for each sub-crowd
 * 4- Cut the answers from each sub-crowd to this maximum
 * 5- For each sub-crowd generate random samples from 1 to the maximum common answers minus 1
 * 6- For each sample compute Precision, Recall, TP, TN, FP, FN, elapsed time, #workers, #answers
 * 7- For all the samples compute the average of all these values.
 * 8- Write results to a file
 * 
 * @author adrianoc
 *
 */
public class SimulationController {

	private int numberOfSamples=10000;

	/**
	 * 1- Generate sub-crowd filters
	 * 
	 * @return list of empty SubCrowd objects only populated the respective filter type
	 */
	public ArrayList<SubCrowd> generateSubCrowdFilters(){

		HashMap<String, CombinedFilterRange> rangeMap = AttributeRangeGenerator.getSubCrowdFilters();
		ArrayList<SubCrowd> subCrowdList =  new ArrayList<SubCrowd>();

		for(CombinedFilterRange range: rangeMap.values()){

			FilterCombination combination = FilterGenerator.generateFilterCombination(range);
			Filter filter = combination.getFilter();
			SubCrowd crowd =  new SubCrowd();
			crowd.name = range.getRangeName();
			crowd.filter = filter;
			subCrowdList.add(crowd);
		}

		return subCrowdList;
	}
	
	

	private ArrayList<SubCrowd> generateJavaMethodFakeSubCrowds() {

		ArrayList<SubCrowd> JavaMethodSubcrowdList =  new ArrayList<SubCrowd>();
		for(String fileName:MonteCarloSimulator.fileNameList){
			SubCrowd crowd =  new SubCrowd();
			crowd.name = fileName;
			JavaMethodSubcrowdList.add(crowd);			
		}
		return JavaMethodSubcrowdList;
	}
	
	/**
	 * 2- Obtain the microtasks for each of these sub-crowds
	 * @param filterList
	 * @return
	 */
	public ArrayList<SubCrowd> generateJavaMethodSubCrowdMicrotasks(ArrayList<SubCrowd> subCrowdList){

		FileSessionDTO dto = new FileSessionDTO();
		HashMap<String, Microtask> originalMicrotaskMap = (HashMap<String, Microtask>) dto.getMicrotasks();

		for(int i=0; i<subCrowdList.size();i++){

			SubCrowd crowd = subCrowdList.get(i);
			HashMap<String, Microtask> map = (HashMap<String, Microtask>) 
					Filter.selectMicrotaskFromFileName(originalMicrotaskMap, crowd.name);
			crowd.microtaskMap = map;
			crowd.totalWorkers = MicrotaskMapUtil.countWorkers(map, null);
			crowd.totalAnswers = MicrotaskMapUtil.getMaxAnswersPerQuestion(map);
			subCrowdList.set(i, crowd);
		}

		return subCrowdList;
	}

	public ArrayList<SubCrowd> generateSubCrowdMicrotasks(ArrayList<SubCrowd> subCrowdList){
		
		
		FileSessionDTO dto = new FileSessionDTO();
		HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) dto.getMicrotasks();

		for(int i=0; i<subCrowdList.size();i++){

			SubCrowd crowd = subCrowdList.get(i);
			HashMap<String, Microtask> map = (HashMap<String, Microtask>) crowd.filter.apply(microtaskMap);
			crowd.microtaskMap = map;
			crowd.totalWorkers = MicrotaskMapUtil.countWorkers(map, null);
			crowd.totalAnswers = MicrotaskMapUtil.getMaxAnswersPerQuestion(map);
			subCrowdList.set(i, crowd);
		}

		return subCrowdList;
		
	}
	
	/**
	 * 3- Obtain the maximum common number of answers for each sub-crowd
	 * 
	 */
	public ArrayList<SubCrowd> setMaximumCommonAnswers(ArrayList<SubCrowd> subCrowdList){

		for(int i=0; i<subCrowdList.size();i++){

			SubCrowd crowd = subCrowdList.get(i);
			HashMap<String, Microtask> map = crowd.microtaskMap;
			crowd.maxCommonAnswers = MicrotaskMapUtil.getMaxCommonAnswersPerQuestion(map);
			subCrowdList.set(i, crowd);
		}
		return subCrowdList;
	}

	/**
	 * 4- Cut the answers from each sub-crowd to the maximum of answers per question
	 */
	public ArrayList<SubCrowd> cutAnswerListsToMaximum(ArrayList<SubCrowd> subCrowdList){

		for(int i=0; i<subCrowdList.size();i++){

			SubCrowd crowd = subCrowdList.get(i);
			HashMap<String, Microtask> map = crowd.microtaskMap;
			crowd.microtaskMap = MicrotaskMapUtil.cutMapToMaximumAnswers(map,crowd.maxCommonAnswers);
			crowd.totalAnswers = MicrotaskMapUtil.countAnswers(crowd.microtaskMap).intValue();
			crowd.totalWorkers = MicrotaskMapUtil.countWorkers(crowd.microtaskMap, null);
			subCrowdList.set(i, crowd);
		}
		return subCrowdList;
	}	

	/**
	 * 5- For each sub-crowd generate random samples from 1 to the maximum common answers minus 1
	 * 6- For each sample compute Precision, Recall, TP, TN, FP, FN, elapsed time, #workers, #answers
	 * 7- For all the samples compute the average of all these values.
	 * 8- Write results to a file
	 */
	private void runSimulations(ArrayList<SubCrowd> subCrowdList){

		for(SubCrowd crowd: subCrowdList){

			MonteCarloSimulator simulator = new MonteCarloSimulator(crowd.name);
			simulator.generateSimulations(crowd.maxCommonAnswers, this.numberOfSamples,	crowd.microtaskMap, crowd.name);
		}
	}

	/** Simulations for all subcrowds */
	public void run(){

		ArrayList<SubCrowd> subCrowdList =  this.generateSubCrowdFilters();
		subCrowdList = this.generateSubCrowdMicrotasks(subCrowdList);
		subCrowdList = this.setMaximumCommonAnswers(subCrowdList);
		subCrowdList = this.cutAnswerListsToMaximum(subCrowdList);
		this.runSimulations(subCrowdList);
	}

	/** Simulations for all Java methods */
	public void run_JavaMethodSimulations(){

		ArrayList<SubCrowd> subCrowdList =  this.generateJavaMethodFakeSubCrowds();
		subCrowdList = this.generateJavaMethodSubCrowdMicrotasks(subCrowdList);
		subCrowdList = this.setMaximumCommonAnswers(subCrowdList);
		subCrowdList = this.cutAnswerListsToMaximum(subCrowdList);
		this.runSimulations(subCrowdList);
	}


	/** Entry point method */
	public static void main(String args[]){
		SimulationController controller = new SimulationController();
		//controller.run();
		controller.run_JavaMethodSimulations();
	}

}

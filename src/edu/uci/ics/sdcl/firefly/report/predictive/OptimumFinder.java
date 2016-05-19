package edu.uci.ics.sdcl.firefly.report.predictive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap; 


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask; 
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileConsentDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;
import edu.uci.ics.sdcl.firefly.util.ElapsedTimeUtil;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;
import edu.uci.ics.sdcl.firefly.util.mturk.AnswerCounter;

/**
 *  Uses the predictor StrengthSignal to search for the best result among all different filtered datasets.
 * 
 *  Uses a goal function that basically looks for the fewer amount of YES's which still enable to select only bug covering questions.
 * 
 * @author adrianoc
 *
 */
public class OptimumFinder {

	public String[] fileNameList  = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7",
			"HIT05_35","HIT06_51","HIT07_33","HIT08_54"};
	
	ArrayList<HashMap<FilterCombination,AnswerData>> dataList;

	ArrayList<Consensus> predictorList;

	ArrayList<Outcome> filterOutcomeList = new ArrayList<Outcome>();

	ArrayList<String> hitFileNameList = new 	ArrayList<String>();
	
	HashMap<String, QuestionLinesMap> lineMapping;
	
	HashMap<String,String>  bugCoveringMap;
	

	public OptimumFinder(){}
	
	public void initialize(){
		
		//Obtain bug covering question list
		PropertyManager manager = PropertyManager.initializeSingleton();
		this.bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		for(String questionID:listOfBugPointingQuestions){
			bugCoveringMap.put(questionID,questionID);
		}
		
		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		this.lineMapping =  loader.loadList();
	}
			
	public void setData(ArrayList<HashMap<FilterCombination,AnswerData>> dataList){
		this.dataList = dataList;
	}

	public void addPredictor(Consensus pred){
		if(predictorList==null)
			this.predictorList = new ArrayList<Consensus>();
		this.predictorList.add(pred);
	}

	public void run(){

	

		for(HashMap<FilterCombination,AnswerData> map : dataList){ //one for each file (8)
			for(FilterCombination filter : map.keySet()){ //one time
				AnswerData answerData = map.get(filter);

				for(Consensus predictor: predictorList){ //one time

					predictor.computeThreshold(answerData);
					HashMap<String, Integer> truePositiveLines = predictor.getTruePositiveLines(lineMapping);
					HashMap<String, Integer> nearPositiveLines = predictor.getNearPositiveLines(lineMapping);
					HashMap<String, Integer> falsePositiveLines = predictor.getFalsePositiveLines(lineMapping);
					HashMap<String, Integer> falseNegativeLines = predictor.getFalseNegativeLines(lineMapping);;
					falsePositiveLines = Consensus.removeFalsePositiveDuplications(nearPositiveLines,falsePositiveLines);
					falsePositiveLines = Consensus.removeFalsePositiveDuplications(truePositiveLines,falsePositiveLines);
					Boolean faultLocated = truePositiveLines!=null && truePositiveLines.size()>0;
					
					Outcome outcome = new Outcome(filter,
							answerData.getHitFileName(),
							predictor.getName(),
							faultLocated,
							predictor.computeSignalStrength(answerData),
							predictor.computeNumberOfWorkers(answerData),
							answerData.getTotalAnswers(),
							predictor.getThreshold(),
							predictor.getTruePositives(),
							predictor.getTrueNegatives(),
							predictor.getFalsePositives(),
							predictor.getFalseNegatives(),
							answerData.getWorkerCount(),
							answerData.getDifferentWorkersAmongHITs(),
							truePositiveLines,
							nearPositiveLines,
							falsePositiveLines,
							falseNegativeLines,
							AnswerData.countCorrectYES(answerData.answerMap, answerData.bugCoveringMap),
							AnswerData.countCorrectNO(answerData.answerMap, answerData.bugCoveringMap),
							AnswerData.count(answerData.answerMap, Answer.YES),
							AnswerData.count(answerData.answerMap, Answer.NO),
							AnswerData.count(answerData.answerMap, Answer.I_DONT_KNOW)
							);
					
					filterOutcomeList.add(outcome);					
				}
			}
		}
	}


	public void printResults(String suffix){	

		String destination = "C://firefly//optimumFinder_"+suffix+".csv";
		BufferedWriter log;
		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(getHeader()+"\n");
			for(Outcome outcome: this.filterOutcomeList){
				String line= outcome.filter.toString(FilterCombination.headerList)+","+outcome.toString();
				log.write(line+"\n");
			}
			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}




	private String getHeader(){

		return FilterCombination.getFilterHeaders() + Outcome.getHeader();
	}


	private static HashMap<String, ArrayList<String>>  extractAnswersForFileName(
			HashMap<String, Microtask> microtaskMap,String fileName){

		int answerCount = 0;
		HashMap<String, ArrayList<String>> resultMap = new HashMap<String, ArrayList<String>>();

		for(Microtask task:microtaskMap.values() ){
			//System.out.println("fileName: "+fileName+":"+task.getFileName());
			if(task.getFileName().compareTo(fileName)==0){
				resultMap.put(task.getID().toString(),task.getAnswerOptions());
				answerCount = answerCount+task.getAnswerOptions().size();
			}
		}
		//System.out.println(fileName+" has "+answerCount+" answers");
		return resultMap;
	}

	private static Integer countWorkers(
			HashMap<String, Microtask> filteredMicrotaskMap, String fileName) {

		//FileConsentDTO dto = new FileConsentDTO();
		//HashMap<String,Worker> workerObjectMap = dto.getWorkers();
		HashMap<String,String> workerMap = new HashMap<String, String>();
		for(Microtask task: filteredMicrotaskMap.values()){
			if(fileName==null || task.getFileName().compareTo(fileName)==0){
				for(Answer answer:task.getAnswerList()){
					String workerID = answer.getWorkerId();
					//Worker workerObj = workerObjectMap.get(workerID);
					//System.out.println(workerID+","+workerObj.getSurveyAnswer("Experience")+","+workerObj.getGrade());
					workerMap.put(workerID, workerID);
				}
			}
		}
		return workerMap.size();
	}

	/** Used this method to obtain data from disjoints sets, for instance, skill 80 and difficulty 1 + skill 100, difficulty 5 */
	public static HashMap<String, Microtask> apply_OR_filter(HashMap<String, Microtask> microtaskMap, ArrayList<FilterCombination> filterList){

		FilterCombination combination1 = filterList.get(0);
		FilterCombination combination2 = filterList.get(1);
		Filter filter1 = combination1.getFilter();
		Filter filter2 = combination2.getFilter();
		
		HashMap<String, Microtask> map1 = (HashMap<String, Microtask>) filter1.apply(microtaskMap);
		FileSessionDTO sessionDTO = new FileSessionDTO();
		microtaskMap = (HashMap<String, Microtask>) sessionDTO.getMicrotasks();
		HashMap<String, Microtask> map2 = (HashMap<String, Microtask>) filter2.apply(microtaskMap);
		
		HashMap<String, Microtask> map12 = MicrotaskMapUtil.mergeMaps(microtaskMap, map1, map2); //Elapsed time: 150.0, number of answers: 521, number of workers: 139

		
		if(filterList.size()==3){
			FilterCombination combination3 = filterList.get(2);
			Filter filter3 = combination3.getFilter();
			microtaskMap = (HashMap<String, Microtask>) sessionDTO.getMicrotasks();
			HashMap<String, Microtask> map3 = (HashMap<String, Microtask>) filter3.apply(microtaskMap);
			HashMap<String, Microtask> map123 = MicrotaskMapUtil.mergeMaps(microtaskMap, map12, map3);
			System.out.println("Answers sizes, map1:"+MicrotaskMapUtil.countAnswers(map1)+", map2:"+MicrotaskMapUtil.countAnswers(map2)+
					", map3:"+ MicrotaskMapUtil.countAnswers(map3)+", union:"+ MicrotaskMapUtil.countAnswers(map123));
			return map123;
		}
		else{
			System.out.println("Answers, map1:"+MicrotaskMapUtil.countAnswers(map1)+", map2:"+MicrotaskMapUtil.countAnswers(map2)+
					", union:"+ MicrotaskMapUtil.countAnswers(map12));
			return	map12;			
		}
	}

	/** 
	 * Computes the outcomes of aggregation mechanisms (consensus) by using data that was filter by using OR clauses,
	 * i.e., joined different groups. (e.g., score larger than 100 and profession = hobbyist OR score=60 and profession=student),
	 */
	public ArrayList<HashMap<FilterCombination,AnswerData>> applyORFilters(){

		ArrayList<HashMap<FilterCombination,AnswerData>> processingList = new 	ArrayList<HashMap<FilterCombination,AnswerData>> ();

		ArrayList<FilterCombination> filterList = FilterGenerator.generateSkillDifficultyFilterCombinationList();


		//Apply filter and extract data by fileName
		//System.out.println("FilterList size: "+ filterList.size());
		//for(FilterCombination combination :  filterList){
		FilterCombination combination =  filterList.get(0);//MAKE THIS MORE ELEGANT
		FileSessionDTO sessionDTO = new FileSessionDTO();
		HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) sessionDTO.getMicrotasks();

		//Filter filter = combination.getFilter();
		//HashMap<String, Microtask> filteredMicrotaskMap = (HashMap<String, Microtask>) filter.apply(microtaskMap);

		HashMap<String, Microtask> filteredMicrotaskMap = (HashMap<String, Microtask>) apply_OR_filter(microtaskMap, filterList);

		Integer totalDifferentWorkersAmongHITs = countWorkers(filteredMicrotaskMap, null);

		System.out.println("Elapsed time: "+ElapsedTimeUtil.getElapseTime(filteredMicrotaskMap)+
				", number of answers: "+AnswerCounter.countAnswers(filteredMicrotaskMap) + 
				", number of workers: "+totalDifferentWorkersAmongHITs);


		for(String fileName: fileNameList){
			HashMap<String, ArrayList<String>> answerMap = extractAnswersForFileName(filteredMicrotaskMap,fileName);
			Integer workerCountPerHIT = countWorkers(filteredMicrotaskMap,fileName);
			AnswerData data = new AnswerData(fileName,answerMap,bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);
			HashMap<FilterCombination, AnswerData> map = new HashMap<FilterCombination, AnswerData>();
			map.put(combination, data);
			processingList.add(map);
		}
		//}
		return processingList;

	}

	/** 
	 * Computes the outcomes of aggregation mechanisms (consensus) by using data that was obtained by using AND filters
	 * on the data attributes (e.g., score larger than 100 and profession = hobbyist),
	 * i.e., joined different groups.
	 */
	public ArrayList<HashMap<FilterCombination,AnswerData>> applyANDFilters(){
		
		ArrayList<HashMap<FilterCombination,AnswerData>> processingList = new 	ArrayList<HashMap<FilterCombination,AnswerData>> ();

		//Produce the list of filters
		ArrayList<FilterCombination> filterList = FilterGenerator.generateAnswerFilterCombinationList();

		//Apply filter and extract data by fileName
		//System.out.println("FilterList size: "+ filterList.size());
		for(FilterCombination combination :  filterList){
			FileSessionDTO sessionDTO = new FileSessionDTO();
			HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) sessionDTO.getMicrotasks();

			Filter filter = combination.getFilter();
			HashMap<String, Microtask> filteredMicrotaskMap = (HashMap<String, Microtask>) filter.apply(microtaskMap);

			Integer totalDifferentWorkersAmongHITs = countWorkers(filteredMicrotaskMap, null);

			System.out.println("Elapsed time: "+ElapsedTimeUtil.getElapseTime(filteredMicrotaskMap)+
					", number of answers: "+AnswerCounter.countAnswers(filteredMicrotaskMap) + 
					", number of workers: "+totalDifferentWorkersAmongHITs);


			for(String fileName: this.fileNameList){
				HashMap<String, ArrayList<String>> answerMap = extractAnswersForFileName(filteredMicrotaskMap,fileName);
				Integer workerCountPerHIT = countWorkers(filteredMicrotaskMap,fileName);
				AnswerData data = new AnswerData(fileName,answerMap,this.bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);
				HashMap<FilterCombination, AnswerData> map = new HashMap<FilterCombination, AnswerData>();
				map.put(combination, data);
				processingList.add(map);
			}
		}
		return processingList;	
	}


	public void addAggregationMechanisms(){
		//finder.addPredictor(new AcrossQuestionsConsensus(1));
		//finder.addPredictor(new AcrossQuestionsConsensus(2));
		//finder.addPredictor(new AcrossQuestionsConsensus(3));
		
		//finder.addPredictor(new WithinQuestionConsensus(WithinQuestionConsensus.Balance_YES_NO_Consensus,null,-1));
		//finder.addPredictor(new WithinQuestionConsensus(WithinQuestionConsensus.Balance_YES_NO_Consensus,null,0));
		//finder.addPredictor(new WithinQuestionConsensus(WithinQuestionConsensus.Balance_YES_NO_Consensus,null,1));

		for(int minimumYes=1;minimumYes<21;minimumYes++){
			addPredictor(new WithinQuestionConsensus(WithinQuestionConsensus.Absolute_YES_Consensus,minimumYes,0));
		}
	}
	
	
	/** 
	 * Orchestrates the execution 
	 * There are two filter types, one applied AND condiditions, while the other applies an OR.
	 * */
	public static void main(String[] args){

		OptimumFinder finder =  new OptimumFinder();
		finder.initialize();
		finder.setData(finder.applyANDFilters());
		finder.addAggregationMechanisms();
		finder.run();
		finder.printResults("calibration");
	}
	
	
}

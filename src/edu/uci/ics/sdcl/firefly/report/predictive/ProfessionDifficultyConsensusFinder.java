package edu.uci.ics.sdcl.firefly.report.predictive; 

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapValidator;

/**
 * This class generates two sets of data.
 * 
 * Set-1: matrix of professions x difficulty type, where each cell is the accuracy of answers
 * Set-2: consensus for subcrowds grouped by profession and difficulty 
 * 
 * The main method has the two entry points for these analyzes.
 * 
 * @author adrianoc
 *
 */
public class ProfessionDifficultyConsensusFinder {

	public  String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7", "HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

	private HashMap<String,String> bugCoveringMap;

	private HashMap<String, QuestionLinesMap> lineMapping;


	public ProfessionDifficultyConsensusFinder(){
		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		this.lineMapping = loader.loadList();

		bugCoveringMap = BugCoveringMap.initialize();
	}

	/**
	 * 1- Generate sub-crowd filters
	 * 
	 * @return list of empty SubCrowd objects only populated the respective filter type
	 */
	public ArrayList<SubCrowd> generateProfessionDifficultyFilters(){

		//HashMap<String, CombinedFilterRange> rangeMap;
		//CombinedFilterRange range;

		//rangeMap = AttributeRangeGenerator.setupNoFilters();
		//range = rangeMap.get(AttributeRangeGenerator.NO_FILTERS);	

		HashMap<String, CombinedFilterRange> rangeMap = FilterCombination_Profession_DifficultyMatrix.generateFilter();
		ArrayList<SubCrowd> subCrowdList =  new ArrayList<SubCrowd>();

		for(CombinedFilterRange range: rangeMap.values()){

			//	Iterator<CombinedFilterRange> iter = rangeMap.values().iterator();
			//	CombinedFilterRange range = (CombinedFilterRange) iter.next();
			FilterCombination combination = FilterGenerator.generateFilterCombination(range);
			Filter filter = combination.getFilter();
			//System.out.println(combination.getFilterHeaders());
			//System.out.println(combination.toString(combination.headerList));

			SubCrowd crowd =  new SubCrowd();
			crowd.name = range.getRangeName();
			crowd.filter = filter;
			subCrowdList.add(crowd);
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


	public ArrayList<SubCrowd> generateSubCrowdMicrotasks_ORFilters(
			ArrayList<SubCrowd> subCrowdList) {
		FileSessionDTO dto = new FileSessionDTO();
		HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) dto.getMicrotasks();


		for(int i=0; i<subCrowdList.size();i++){

			SubCrowd crowd = subCrowdList.get(i);
			HashMap<String, Microtask> map = (HashMap<String, Microtask>) OR_Filter.apply(microtaskMap, crowd.OR_FilterList);
			crowd.microtaskMap = map;
			crowd.totalWorkers = MicrotaskMapUtil.countWorkers(map, null);
			crowd.totalAnswers = MicrotaskMapUtil.countAnswers(map).intValue();
			subCrowdList.set(i, crowd);
		}

		return subCrowdList; 
	}


	private Outcome computeDataPoint(AnswerData answerData, Consensus predictor, HashMap<String, QuestionLinesMap> lineMapping) {

		predictor.computeThreshold(answerData);
		HashMap<String, Integer> truePositiveLines = predictor.getTruePositiveLines(lineMapping);
		HashMap<String, Integer> nearPositiveLines = predictor.getNearPositiveLines(lineMapping);
		HashMap<String, Integer> falsePositiveLines = predictor.getFalsePositiveLines(lineMapping);
		falsePositiveLines = Consensus.removeFalsePositiveDuplications(nearPositiveLines,falsePositiveLines);
		Boolean faultLocated = truePositiveLines!=null && truePositiveLines.size()>0;
		int questionsBelowMinimumAnswers = predictor.getQuestionsBelowMinimalAnswers();


		Outcome outcome = new Outcome(null,
				answerData.getHitFileName(),
				predictor.getName(),
				faultLocated,
				predictor.computeSignalStrength(answerData),
				predictor.computeNumberOfWorkers(answerData),
				answerData.getTotalAnswers(),
				predictor.getMinimumNumberYESAnswersThatLocatedFault(),
				predictor.getTruePositives(),
				predictor.getTrueNegatives(),
				predictor.getFalsePositives(),
				predictor.getFalseNegatives(),
				answerData.getWorkerCount(),
				answerData.getDifferentWorkersAmongHITs(),
				truePositiveLines,
				nearPositiveLines,
				falsePositiveLines,
				questionsBelowMinimumAnswers,
				AnswerData.countCorrectYES(answerData.answerMap, answerData.bugCoveringMap),
				AnswerData.countCorrectNO(answerData.answerMap, answerData.bugCoveringMap),
				AnswerData.count(answerData.answerMap, Answer.YES),
				AnswerData.count(answerData.answerMap, Answer.NO),
				AnswerData.count(answerData.answerMap, Answer.I_DONT_KNOW)
				);

		return outcome;
	}

	public ArrayList<SubCrowd> run(ArrayList<SubCrowd> subCrowdList){

		for(SubCrowd subcrowd: subCrowdList){
			Integer totalDifferentWorkersAmongHITs = MicrotaskMapUtil.countWorkers(subcrowd.microtaskMap, null);

			DataPoint acrossQuestionsDataPoint = new DataPoint(subcrowd.name,"Across-Questions Consensus");

			for(String fileName: this.fileNameList){//each fileName is a Java method
				HashMap<String, ArrayList<String>> answerMap = MicrotaskMapUtil.extractAnswersForFileName(subcrowd.microtaskMap,fileName);
				if(answerMap!=null && answerMap.size()>0){

					Integer workerCountPerHIT = MicrotaskMapUtil.countWorkers(subcrowd.microtaskMap,fileName);
					AnswerData data = new AnswerData(fileName,answerMap,this.bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);
					Consensus consensus = new AcrossQuestionsConsensus(2);
					Outcome outcome = computeDataPoint(data,consensus,this.lineMapping);
					acrossQuestionsDataPoint.fileNameOutcomeMap.put(outcome.fileName, outcome);

				}
			}

			acrossQuestionsDataPoint.computeAverages();
			acrossQuestionsDataPoint.elapsedTime = MicrotaskMapUtil.computeElapsedTimeForAnswerLevels(subcrowd.microtaskMap);

			subcrowd.acrossQuestionsDataPoint = acrossQuestionsDataPoint;
		}

		return subCrowdList;
	}


	public void printJavaOutcomes(SubCrowd subcrowd){

		String destination = "C://firefly//Profession_Difficulty_Analysis//Outcomes//"+ subcrowd.name+"_outcomes.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header
			log.write(Outcome.getHeaderCorrectAnswers()+"\n");

			for(Entry<String, Outcome> entry: subcrowd.acrossQuestionsDataPoint.fileNameOutcomeMap.entrySet()){
				String fileName = entry.getKey();
				Outcome outcome = subcrowd.acrossQuestionsDataPoint.fileNameOutcomeMap.get(fileName);
				log.write(outcome.toStringCorrectAnswers()+"\n");
			}

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}


	public void printSubCrowdAverages(SubCrowd subcrowd){

		String destination = "C://firefly//Profession_Difficulty_Analysis//Averages//"+ subcrowd.name+"_averages.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header
			log.write(DataPoint.getHeaderCorrectAnswers("")+"\n");
			log.write(subcrowd.acrossQuestionsDataPoint.toStringCorrectAnswers()+"\n"); 
			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}


	public void printSingleFileSubCrowdAverages(ArrayList<SubCrowd> subcrowdList){

		String destination = "C://firefly//Profession_Difficulty_Analysis//Averages//AllSubcrowds_averages.csv";
		BufferedWriter log;


		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header
			log.write("profession,difficulty,"+DataPoint.getHeaderCorrectAnswers("")+"\n");

			for(SubCrowd subcrowd:subcrowdList){

				String[] nameList=subcrowd.name.split("-");
				String profession = nameList[0];
				String difficulty = nameList[2];
				log.write(profession+","+difficulty+","+subcrowd.acrossQuestionsDataPoint.toStringCorrectAnswers()+"\n"); 
			}
			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}

	}


	public static void test(){
		ProfessionYoEConsensusFinder finder = new ProfessionYoEConsensusFinder();
		ArrayList<SubCrowd> list = finder.generateProfessionYoEFilters();
		list = finder.generateSubCrowdMicrotasks(list);
		SubCrowd crowd = list.get(0);
		FileSessionDTO dto = new FileSessionDTO();
		HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) dto.getMicrotasks();
		HashMap<String, ArrayList<String>> answerMap = MicrotaskMapUtil.extractAnswersForFileName(crowd.microtaskMap,"HIT04_7");
	}


	public static void main(String args[]){
		ProfessionYoEConsensusFinder finder = new ProfessionYoEConsensusFinder();
		//ArrayList<SubCrowd> subCrowdList = finder.generateProfessionDifficultyFilters(); //Pairs of profession/difficulty to evaluate accuracy
		//subCrowdList = finder.generateSubCrowdMicrotasks(subCrowdList);

		ArrayList<SubCrowd> subCrowdList = FilterCombination_Profession_DifficultyMatrix.composeSubCrowds();
		subCrowdList =finder.generateSubCrowdMicrotasks_ORFilters(subCrowdList);
		subCrowdList = finder.run(subCrowdList);

		for(SubCrowd subcrowd: subCrowdList){
			finder.printJavaOutcomes(subcrowd);
			finder.printSubCrowdAverages(subcrowd);
		}
		//finder.printSingleFileSubCrowdAverages(subCrowdList);

		//test();
	}


}


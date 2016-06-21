package edu.uci.ics.sdcl.firefly.report.predictive; 

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;

/**
 * This class generates two sets of data.
 * 
 * Set-1: matrix of professions x Years of Experience (YoE), where each cell is the accuracy of answers
 * Set-2: consensus for subcrowds grouped by profession and YoE 
 * 
 * The main method has the two entry points for these analyzes.
 * 
 * @author adrianoc
 *
 */
public class ProfessionYoEConsensusFinder {

	public  String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7", "HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

	private HashMap<String,String> bugCoveringMap;

	private HashMap<String, QuestionLinesMap> lineMapping;


	public ProfessionYoEConsensusFinder(){
		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		this.lineMapping = loader.loadList();

		bugCoveringMap = BugCoveringMap.initialize();
	}

	/**
	 * 1- Generate sub-crowd filters
	 * 
	 * @return list of empty SubCrowd objects only populated the respective filter type
	 */
	public ArrayList<SubCrowd> generateProfessionYoEFilters(){

		HashMap<String, CombinedFilterRange> rangeMap = FilterCombination_Profession_YoE.generateFilter();
		ArrayList<SubCrowd> subCrowdList =  new ArrayList<SubCrowd>();

		for(CombinedFilterRange range: rangeMap.values()){

			FilterCombination combination = FilterGenerator.generateFilterCombination(range);
			Filter filter = combination.getFilter();
			System.out.println("Filter: "+ range.getRangeName() + " minYoE : "+ filter.getMinYoE());
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
			crowd.maxCommonAnswers = MicrotaskMapUtil.getMaxAnswersPerQuestion(map);
			crowd.totalAnswers = MicrotaskMapUtil.countAnswers(map);
			System.out.println("totalWorkers: "+ crowd.totalWorkers+", total answers: "+crowd.totalAnswers);
			
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
		falsePositiveLines = Consensus.removeFalsePositiveDuplications(truePositiveLines,falsePositiveLines);
		Boolean faultLocated = truePositiveLines!=null && truePositiveLines.size()>0;
		int questionsBelowMinimumAnswers = predictor.getQuestionsBelowMinimalAnswers();

		Outcome outcome = new Outcome(
				null, //FilterCombination filter
				answerData.getHitFileName(), //fileName
				predictor.getName(), //predictorType
				faultLocated, //faultLocated
				predictor.computeSignalStrength(answerData), //signalStrength
				predictor.computeNumberOfWorkers(answerData), //maxWorkerPerQuestion
				answerData.getTotalAnswers(), //totalAnswers
				predictor.getMinimumNumberYESAnswersThatLocatedFault(), //threshold
				predictor.getTruePositives(), //truePositives
				predictor.getTrueNegatives(), //trueNegatives
				predictor.getFalsePositives(), //falsePositives
				predictor.getFalseNegatives(), //falseNegatives
				answerData.getWorkerCount(), //differentWorkersPerHIT
				answerData.getDifferentWorkersAmongHITs(), //differentWorkersAmongHITs
				truePositiveLines, //truePositiveLines
				nearPositiveLines, //nearPositiveLines
				falsePositiveLines, //falsePositiveLines
				questionsBelowMinimumAnswers, //questionsBelowMinimumAnswers
				AnswerData.countCorrectYES(answerData.answerMap, answerData.bugCoveringMap),  //correctYES
				AnswerData.countCorrectNO(answerData.answerMap, answerData.bugCoveringMap), //correctNO
				AnswerData.count(answerData.answerMap, Answer.YES), //totalYES
				AnswerData.count(answerData.answerMap, Answer.NO), //totalNO
				AnswerData.count(answerData.answerMap, Answer.I_DONT_KNOW) //totalIDK
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

		String destination = "C://firefly//Profession_YoE_Analysis//Outcomes//"+ subcrowd.name+"_outcomes.csv";
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

		String destination = "C://firefly//Profession_YoE_Analysis//Averages//"+ subcrowd.name+"_averages.csv";
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


	public void printProfessionYoE_pairs(ArrayList<SubCrowd> subcrowdList){

		String destination = "C://firefly//Profession_YoE_Analysis//Averages//AllProfessionYoE_pairs.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header
			log.write("profession,YoE,"+DataPoint.getHeaderCorrectAnswers("")+"\n");

			for(SubCrowd subcrowd:subcrowdList){

				String[] nameList=subcrowd.name.split("-");
				String profession = nameList[0];
				String YoE = nameList[2];
				log.write(profession+","+YoE+","+subcrowd.acrossQuestionsDataPoint.toStringCorrectAnswers()+"\n"); 
			}
			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}


	public static void smokeTest(){
		ProfessionYoEConsensusFinder finder = new ProfessionYoEConsensusFinder();
		ArrayList<SubCrowd> list = finder.generateProfessionYoEFilters();
		list = finder.generateSubCrowdMicrotasks(list);
		SubCrowd crowd = list.get(0);
		FileSessionDTO dto = new FileSessionDTO();
		HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) dto.getMicrotasks();
		HashMap<String, ArrayList<String>> answerMap = MicrotaskMapUtil.extractAnswersForFileName(crowd.microtaskMap,"HIT04_7");
	}

	
	public void computeAccuracyMatrix(){
		ArrayList<SubCrowd> subCrowdList = generateProfessionYoEFilters(); //Pairs of profession/YoEto evaluate accuracy
		subCrowdList = generateSubCrowdMicrotasks(subCrowdList);
		subCrowdList = run(subCrowdList);
		printProfessionYoE_pairs(subCrowdList);
	}
	
	public void computeSubcrowdsOutcomes(){
		ArrayList<SubCrowd> subCrowdList = FilterCombination_Profession_YoE.composeSubCrowds();
		subCrowdList = generateSubCrowdMicrotasks_ORFilters(subCrowdList);
		subCrowdList = run(subCrowdList);

		for(SubCrowd subcrowd: subCrowdList){
			printJavaOutcomes(subcrowd);
			printSubCrowdAverages(subcrowd);
		}
	}

	public static void main(String args[]){
		ProfessionYoEConsensusFinder finder = new ProfessionYoEConsensusFinder();
		
		//Print accuracy matrix to inspect how well each profession and YoE answered the questions
		finder.computeAccuracyMatrix();
		
		//Print the list of subcrowds to investigate which located all faults
		//finder.computeSubcrowdsOutcomes();
		
		//smokeTest();
	}


}


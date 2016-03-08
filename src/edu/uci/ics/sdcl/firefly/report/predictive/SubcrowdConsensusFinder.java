package edu.uci.ics.sdcl.firefly.report.predictive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;

public class SubcrowdConsensusFinder {

	public  String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7", "HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

	private HashMap<String,String> bugCoveringMap;

	private HashMap<String, QuestionLinesMap> lineMapping;


	public SubcrowdConsensusFinder(){
		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		this.lineMapping = loader.loadList();

		bugCoveringMap = BugCoveringMap.initialize();

	}

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

	private Outcome computeDataPoint(AnswerData answerData, Consensus predictor, HashMap<String, QuestionLinesMap> lineMapping) {
	
		Boolean signal = predictor.computeSignal(answerData);
		HashMap<String, Integer> truePositiveLines = predictor.getTruePositiveLines(lineMapping);
		HashMap<String, Integer> nearPositiveLines = predictor.getNearPositiveLines(lineMapping);
		HashMap<String, Integer> falsePositiveLines = predictor.getFalsePositiveLines(lineMapping);
		HashMap<String, Integer> falseNegativeLines = predictor.getFalseNegativeLines(lineMapping);
		falsePositiveLines = Consensus.removeFalsePositiveDuplications(nearPositiveLines,falsePositiveLines);

		Outcome outcome = new Outcome(null,
				answerData.getHitFileName(),
				predictor.getName(),
				predictor.getTruePositives()>0,
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
				falseNegativeLines
				);

		return outcome;
	}

	public ArrayList<SubCrowd> run(){

		ArrayList<SubCrowd> subCrowdList =  this.generateSubCrowdFilters();
		subCrowdList = this.generateSubCrowdMicrotasks(subCrowdList);

		for(SubCrowd subcrowd: subCrowdList){
			Integer totalDifferentWorkersAmongHITs = MicrotaskMapUtil.countWorkers(subcrowd.microtaskMap, null);

			DataPoint acrossQuestionsDataPoint = new DataPoint(subcrowd.name,"Across-Questions Consensus");
			DataPoint withinQuestionDataPoint = new DataPoint(subcrowd.name,"Within-Question Consensus");

			for(String fileName: this.fileNameList){//each fileName is a Java method
				HashMap<String, ArrayList<String>> answerMap = MicrotaskMapUtil.extractAnswersForFileName(subcrowd.microtaskMap,fileName);
				if(answerMap!=null && answerMap.size()>0){

					Integer workerCountPerHIT = MicrotaskMapUtil.countWorkers(subcrowd.microtaskMap,fileName);
					AnswerData data = new AnswerData(fileName,answerMap,this.bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);
					Consensus consensus = new AcrossQuestionsConsensus(2);
					Outcome outcome = computeDataPoint(data,consensus,this.lineMapping);
					acrossQuestionsDataPoint.fileNameOutcomeMap.put(outcome.fileName, outcome);

					consensus = new WithinQuestionConsensus();
					outcome = computeDataPoint(data,consensus,lineMapping);
					withinQuestionDataPoint.fileNameOutcomeMap.put(outcome.fileName, outcome);
				}
			}

			acrossQuestionsDataPoint.computeAverages();
			acrossQuestionsDataPoint.elapsedTime = MicrotaskMapUtil.computeElapsedTimeForAnswerLevels(subcrowd.microtaskMap);
			withinQuestionDataPoint.computeAverages();
			withinQuestionDataPoint.elapsedTime = MicrotaskMapUtil.computeElapsedTimeForAnswerLevels(subcrowd.microtaskMap);

			subcrowd.acrossQuestionsDataPoint = acrossQuestionsDataPoint;
			subcrowd.withinQuestionDataPoint = withinQuestionDataPoint;
			subcrowd.combinedConsensusDataPoint = DataPoint.combineConsensusDataPoints(acrossQuestionsDataPoint,withinQuestionDataPoint);
			subcrowd.combinedConsensusDataPoint.computeAverages();
			subcrowd.combinedConsensusDataPoint.elapsedTime = acrossQuestionsDataPoint.elapsedTime > withinQuestionDataPoint.elapsedTime ? acrossQuestionsDataPoint.elapsedTime : withinQuestionDataPoint.elapsedTime;
		}

		return subCrowdList;
	}


	public void printJavaOutcomes(SubCrowd subcrowd){

		String destination = "C://firefly//CombinedFilters_withLinesToInspect//SubcrowdOutcomes//"+ subcrowd.name+"_outcomes.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(Outcome.getHeader()+"\n");

			for(Entry<String, Outcome> entry: subcrowd.acrossQuestionsDataPoint.fileNameOutcomeMap.entrySet()){
				String fileName = entry.getKey();
				Outcome outcome = subcrowd.acrossQuestionsDataPoint.fileNameOutcomeMap.get(fileName);
				log.write(outcome.toString()+"\n");
				outcome = subcrowd.withinQuestionDataPoint.fileNameOutcomeMap.get(fileName);
				log.write(outcome.toString()+"\n");
				outcome = subcrowd.combinedConsensusDataPoint.fileNameOutcomeMap.get(fileName);
				log.write(outcome.toString()+"\n");
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

		String destination = "C://firefly//CombinedFilters_withLinesToInspect//SubcrowdAverages//"+ subcrowd.name+"_averages.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(DataPoint.getHeader("")+"\n");
			log.write(subcrowd.acrossQuestionsDataPoint.toString()+"\n"); 
			log.write(subcrowd.withinQuestionDataPoint.toString()+"\n"); 
			log.write(subcrowd.combinedConsensusDataPoint.toString()+"\n");

			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}

	public static void main(String args[]){
		SubcrowdConsensusFinder finder = new SubcrowdConsensusFinder();
		ArrayList<SubCrowd> list = finder.run();
		for(SubCrowd subcrowd: list){
			finder.printJavaOutcomes(subcrowd);
			finder.printSubCrowdAverages(subcrowd);
		}
	}


}

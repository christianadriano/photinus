package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;

public class SubcrowdConsensusFinder {

	public static String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7", "HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

	private HashMap<String,String> bugCoveringMap;

	private String outputFolder = "";

	private HashMap<String, QuestionLinesMap> lineMapping;


	public SubcrowdConsensusFinder(String outputFolder){
		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		this.lineMapping = loader.loadList();

		this.outputFolder = outputFolder;
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
		HashMap<String, Integer> truePositiveLines = predictor.getTruePositiveFaultyLines(lineMapping);
		HashMap<String, Integer> nearPositiveLines = predictor.getNearPositiveFaultyLines(lineMapping);
		HashMap<String, Integer> falsePositiveLines = predictor.getFalsePositiveLines(lineMapping);
		falsePositiveLines = Consensus.removeFalsePositiveDuplications(nearPositiveLines,falsePositiveLines);

		Outcome outcome = new Outcome(null,
				answerData.getHitFileName(),
				predictor.getName(),
				signal,
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
				falsePositiveLines);

		return outcome;
	}

	public void run(){

		ArrayList<SubCrowd> subCrowdList =  this.generateSubCrowdFilters();
		subCrowdList = this.generateSubCrowdMicrotasks(subCrowdList);

		for(SubCrowd subcrowd: subCrowdList){
			Integer totalDifferentWorkersAmongHITs = MicrotaskMapUtil.countWorkers(subcrowd.microtaskMap, null);

			DataPoint positiveVDataPoint = new DataPoint();
			DataPoint majorityVDataPoint = new DataPoint();

			for(String fileName: this.fileNameList){//each fileName is a Java method
				HashMap<String, ArrayList<String>> answerMap = MicrotaskMapUtil.extractAnswersForFileName(subcrowd.microtaskMap,fileName);
				if(answerMap!=null && answerMap.size()>0){

					Integer workerCountPerHIT = MicrotaskMapUtil.countWorkers(subcrowd.microtaskMap,fileName);
					AnswerData data = new AnswerData(fileName,answerMap,this.bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);
					Consensus consensus = new AcrossQuestionsConsensus();
					Outcome outcome = computeDataPoint(data,consensus,this.lineMapping);
					positiveVDataPoint.fileNameOutcomeMap.put(fileName, outcome);

					consensus = new WithinQuestionConsensus();
					outcome = computeDataPoint(data,consensus,lineMapping);
					majorityVDataPoint.fileNameOutcomeMap.put(fileName, outcome);
				}
			}
		}

	}

	//Print Method Data



}

package edu.uci.ics.sdcl.firefly.report.predictive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;
import edu.uci.ics.sdcl.firefly.util.Coordinate;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;
import edu.uci.ics.sdcl.firefly.util.RoundDouble;

/**
 * Obtains the elapsed time for each level of answers.
 * For instance, how long time has passed to obtain 1 answer for all questions, 2, 3, 4, etc.
 * 
 * @author adrianoc
 *
 */
public class CrowdSpeedAnalysis {


	/**  list of precision values calculated for each sample */
	private ArrayList<DataPoint> outcomes_PositiveVoting = new ArrayList<DataPoint>();

	/**  list of precision values calculated for each sample */
	private ArrayList<DataPoint> outcomes_MajorityVoting = new ArrayList<DataPoint>();

	/**  list of precision values calculated for each sample */
	private ArrayList<DataPoint> outcomes_ThresholdVoting  = new ArrayList<DataPoint>();

	private String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7",
			"HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

	private HashMap<String,String> bugCoveringMap;

	//-------------------------------------------------------------------------------------

	public CrowdSpeedAnalysis(String[] newFileNameList){
		if(fileNameList!=null) {
			this.fileNameList = newFileNameList;
			this.loadInitialize(fileNameList[0]); //load only the question for one bugID
		}
		else
			this.loadInitialize(null); //Load all questions
	}

	private void loadInitialize(String bugID) {
		//Obtain bug covering question list
		PropertyManager manager = PropertyManager.initializeSingleton();
		bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		Coordinate range = new Coordinate(0,128);
		
		if(bugID!=null) {
			HashMap<String,Coordinate> bugIDQuestionMap = manager.questionRangeMap;
			range = bugIDQuestionMap.get(bugID);	
		}
		
		for(String questionID:listOfBugPointingQuestions){
			Integer questionID_double = new Integer(questionID);
			if(questionID_double>=range.start && questionID_double<=range.end)
				bugCoveringMap.put(questionID,questionID);
		}
	}

	private Answer getFirstAnswer(HashMap<String, Microtask> map){

		Answer currentAnswer=null;

		for(Microtask microtask:map.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			for(Answer answer:answerList){
				if(currentAnswer==null)
					currentAnswer=answer;
				else{
					Date answerDate = answer.getTimeStampDate(); 
					if(answerDate.compareTo(currentAnswer.getTimeStampDate())<0)
						currentAnswer = answer;
				}
			}	
		}
		return currentAnswer;
	}

	private Answer getLastAnswer(HashMap<String,Microtask> map){

		Answer currentAnswer=null;

		for(Microtask microtask:map.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			for(Answer answer:answerList){
				if(currentAnswer==null)
					currentAnswer=answer;
				else{
					Date answerDate = answer.getTimeStampDate(); 
					if(answerDate.compareTo(currentAnswer.getTimeStampDate())>0)
						currentAnswer = answer;
				}
			}	
		}
		return currentAnswer;
	}

	private double computeElapsedTime_Hours(Date startDate, Date endDate){
		double millisec = endDate.getTime() - startDate.getTime();
		return millisec /(3600 *1000);
	}



	private HashMap<String, Microtask> filterMicrotaskMap(int maxAnswers, HashMap<String, Microtask> map){

		HashMap<String, Microtask> newMap = new HashMap<String, Microtask>();

		for(Microtask microtask:map.values()){
			Vector<Answer> answerList = microtask.getAnswerList();
			Vector<Answer> newAnswerList = new Vector<Answer>();
			for(int i=0;i<maxAnswers;i++){
				if(answerList.size()>i){
					Answer answer = answerList.get(i);
					newAnswerList.add(answer);
				}
			}
			Microtask newMicrotask = microtask.getSimpleVersion();
			newMicrotask.setAnswerList(newAnswerList);
			newMap.put(newMicrotask.getID().toString(), newMicrotask);
		}
		return newMap;
	}

	private Outcome computeDataPoint(AnswerData answerData, Consensus predictor,HashMap<String, QuestionLinesMap> lineMapping) {
		predictor.computeThreshold(answerData);
		HashMap<String, Integer> truePositiveLines = predictor.getTruePositiveLines(lineMapping);
		HashMap<String, Integer> nearPositiveLines = predictor.getNearPositiveLines(lineMapping);
		HashMap<String, Integer> falsePositiveLines = predictor.getFalsePositiveLines(lineMapping);
		HashMap<String, Integer> trueNegativeLines = predictor.getTrueNegativeLines(lineMapping);
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
				trueNegativeLines,
				questionsBelowMinimumAnswers,
				AnswerData.countCorrectYES(answerData.answerMap, answerData.bugCoveringMap),
				AnswerData.countCorrectNO(answerData.answerMap, answerData.bugCoveringMap),
				AnswerData.count(answerData.answerMap, Answer.YES),
				AnswerData.count(answerData.answerMap, Answer.NO),
				AnswerData.count(answerData.answerMap, Answer.I_DONT_KNOW));

		return outcome;
	}

	//-------------------------------------------------------------------------------------------------------
	private Double countWorkers(
			HashMap<String, Microtask> filteredMicrotaskMap, String fileName) {

		HashMap<String,String> workerMap = new HashMap<String, String>();
		for(Microtask task: filteredMicrotaskMap.values()){
			if(fileName==null || task.getFileName().compareTo(fileName)==0){
				for(Answer answer:task.getAnswerList()){
					String workerID = answer.getWorkerId();
					workerMap.put(workerID, workerID);
				}
			}
		}
		return new Double(workerMap.size());
	}

	private Integer countAnswers(HashMap<String, Microtask> map){

		int count=0;
		for(Microtask microtask: map.values()){
			count = count + microtask.getNumberOfAnswers();
		}
		return count;
	}

	private HashMap<String, ArrayList<String>>  extractAnswersForFileName(
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
	//----------------------------------------------------------------------------------------------------------


	public void computeVoting(HashMap<String, Microtask> microtaskMap, Double elapsedTime){

		Double totalDifferentWorkersAmongHITs = countWorkers(microtaskMap, null);

		DataPoint positiveVDataPoint = new DataPoint();
		DataPoint majorityVDataPoint = new DataPoint();
		DataPoint thresholdVDatapoint = new DataPoint();

		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		HashMap<String, QuestionLinesMap> lineMapping =  loader.loadList();

		for(String fileName: fileNameList){//each fileName is a Java method
			HashMap<String, ArrayList<String>> answerMap = extractAnswersForFileName(microtaskMap,fileName);
			Double workerCountPerHIT = countWorkers(microtaskMap,fileName);
			AnswerData data = new AnswerData(fileName,answerMap,bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);

			Consensus predictor = new AcrossQuestionsConsensus(2,true);
			Outcome outcome = computeDataPoint(data,predictor,lineMapping);
			positiveVDataPoint.fileNameOutcomeMap.put(fileName, outcome);

			predictor = new WithinQuestionConsensus(WithinQuestionConsensus.Balance_YES_NO_Consensus,null,-2,true);//Initial values used was 0
			outcome = computeDataPoint(data,predictor,lineMapping);
			majorityVDataPoint.fileNameOutcomeMap.put(fileName, outcome);

			predictor = new WithinQuestionConsensus(WithinQuestionConsensus.Absolute_YES_Consensus,8.0,0,true); //Initial value used was 5
			outcome = computeDataPoint(data,predictor,lineMapping);
			thresholdVDatapoint.fileNameOutcomeMap.put(fileName, outcome);		
		}

		positiveVDataPoint.maxAnswersHIT = MicrotaskMapUtil.countAnswers(microtaskMap).doubleValue();
		majorityVDataPoint.maxAnswersHIT = MicrotaskMapUtil.countAnswers(microtaskMap).doubleValue();
		thresholdVDatapoint.maxAnswersHIT = MicrotaskMapUtil.countAnswers(microtaskMap).doubleValue();

		positiveVDataPoint.computeAverages();//Compute the average precision and recall for all Java methods
		majorityVDataPoint.computeAverages();
		thresholdVDatapoint.computeAverages();

		positiveVDataPoint.elapsedTime = elapsedTime;
		majorityVDataPoint.elapsedTime = elapsedTime;
		thresholdVDatapoint.elapsedTime = elapsedTime;

		positiveVDataPoint.totalWorkers = new Double(totalDifferentWorkersAmongHITs);
		majorityVDataPoint.totalWorkers = new Double(totalDifferentWorkersAmongHITs);
		thresholdVDatapoint.totalWorkers = new Double(totalDifferentWorkersAmongHITs);

		this.outcomes_PositiveVoting.add(positiveVDataPoint);
		this.outcomes_MajorityVoting.add(majorityVDataPoint);
		this.outcomes_ThresholdVoting.add(thresholdVDatapoint);
	}

	public void computeElapsedTimeForAnswerLevels(HashMap<String, Microtask> map){

		if(map==null){
			FileSessionDTO dto = new FileSessionDTO();
			map = (HashMap<String, Microtask>) dto.getMicrotasks();
		}
		for(int i=1;i<=20;i++){
			HashMap<String, Microtask> newMap = this.filterMicrotaskMap(i, map);
			Answer firstAnswer = this.getFirstAnswer(newMap);
			Answer lastAnswer = this.getLastAnswer(newMap);
			Double elapsedTime = this.computeElapsedTime_Hours(firstAnswer.getTimeStampDate(),lastAnswer.getTimeStampDate());
			this.computeVoting(newMap,RoundDouble.round(elapsedTime, 1));
		}
	}

	//----------------------------------------------------------------------

	private String getAllAggregationMethodsHeader(){
		return "Answer level,Average Precision_PV,Average Recall_PV,Average Precision_MV,Average Recall_MV,"
				+ "Average Precision_Threshold,Average Recall_Threshold, Total Workers, Total Answers, "
				+ " Hours taken, Faults Located_PV, Faults Located_MV, Faults Located_Threshold"
				+ " Lines to Inspect_PV, Lines to Inspect_MV, Lines to Inspect_Threshold ";

	}

	public void printDataPointList_ToFile(String outputFileName){
		
		PropertyManager manager = PropertyManager.initializeSingleton();

		String destination =  manager.speedAnalysisPath + "/" + outputFileName;
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(getAllAggregationMethodsHeader()+"\n");

			for(int i=0; i<this.outcomes_MajorityVoting.size();i++){
				DataPoint datapointPV = this.outcomes_PositiveVoting.get(i);
				DataPoint datapointMV = this.outcomes_MajorityVoting.get(i);
				DataPoint datapointThreshold = this.outcomes_ThresholdVoting.get(i);

				String line= new Integer(i+1).toString() +","+
						datapointPV.averagePrecision.toString()+","+
						datapointPV.averageRecall.toString()+","+
						datapointMV.averagePrecision.toString()+","+
						datapointMV.averageRecall.toString()+","+
						datapointThreshold.averagePrecision.toString()+","+
						datapointThreshold.averageRecall.toString()+","+

						datapointMV.totalWorkers.toString()+","+
						datapointMV.maxAnswersHIT.toString()+","+
						datapointMV.elapsedTime.toString()+","+

						datapointPV.faultsLocated.toString()+","+
						datapointMV.faultsLocated.toString()+","+
						datapointThreshold.faultsLocated.toString()+","+

						datapointPV.getTotalLinesToInspect().toString()+","+
						datapointMV.getTotalLinesToInspect().toString()+","+
						datapointThreshold.getTotalLinesToInspect().toString();

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
	//----------------------------------------------------------------------

	private String getHeader(){
		return "Answer level,Average Precision,Average Recall,"
				+ "True Positives, True Negatives, False Positives, False Negatives,"
				+ "Total Workers, Total Answers, "
				+ "Hours taken, Faults Located, "
				+ "True Positive Lines, Near Positive Lines, False Positive Lines, False Negative Lines, True Negative Lines"
				+ "Total lines to inspect";
	}

	public void printDataPointsToFile(String fileName){
		PropertyManager manager = PropertyManager.initializeSingleton();
		String destination =  manager.speedAnalysisPath +fileName;
		BufferedWriter log;

		ArrayList<DataPoint> dataPointList = this.outcomes_ThresholdVoting;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(getHeader()+"\n");

			for(int i=0; i<dataPointList.size();i++){
				DataPoint datapoint = dataPointList.get(i);

				Integer linesToInspect = datapoint.getTotalLinesToInspect();

				String line= new Integer(i+1).toString() +","+
						datapoint.averagePrecision.toString()+","+
						datapoint.averageRecall.toString()+","+
						datapoint.truePositives.toString()+","+
						datapoint.trueNegatives.toString()+","+
						datapoint.falsePositives.toString()+","+
						datapoint.falseNegatives.toString()+","+
						datapoint.totalWorkers.toString()+","+
						datapoint.maxAnswersHIT.toString()+","+
						datapoint.elapsedTime.toString()+","+
						datapoint.faultsLocated.toString()+","+
						datapoint.truePositiveLinesCount.toString()+","+
						datapoint.nearPositiveLinesCount.toString()+","+
						datapoint.falsePositiveLinesCount.toString()+","+
						datapoint.falseNegativeLinesCount.toString()+","+
						datapoint.trueNegativeLinesCount.toString()+","+
						linesToInspect.toString();
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



	//-------------------------------------------------------------------
	//Work with filtered map, instead of all filters

	public HashMap<String, Microtask> getFilteredMap(){
		FileSessionDTO dto = new FileSessionDTO();
		HashMap<String,Microtask> map =  (HashMap<String, Microtask>) dto.getMicrotasks();

		ArrayList<FilterCombination> filterList = FilterGenerator.generateAnswerFilterCombinationList();

		FilterCombination combination = filterList.get(0);
		Filter filter = combination.getFilter();

		HashMap<String,Microtask> filteredMap =  (HashMap<String, Microtask>) filter.apply(map);

		return filteredMap;
	}


	public static void runPerJavaMethod() {
		String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7", "HIT05_35","HIT06_51","HIT07_33","HIT08_54"};
		for(String fileName:fileNameList) {
			String[] newFileNameList = {fileName};
			CrowdSpeedAnalysis analysis =  new CrowdSpeedAnalysis(newFileNameList);
			analysis.computeElapsedTimeForAnswerLevels(analysis.getFilteredMap());
			analysis.printDataPointList_ToFile("speedAnalysis_AllAggregationMethods_"+fileName+".csv");
		}
	}

	public static void runConsolidated() {
		CrowdSpeedAnalysis analysis =  new CrowdSpeedAnalysis(null);
		analysis.computeElapsedTimeForAnswerLevels(analysis.getFilteredMap());
		analysis.printDataPointsToFile("speedAnalysis_Threshold_predicted_KNN.csv");
		//analysis.printDataPointList_ToFile();
	}

	//----------------------------------------------------------------------

	public static void main(String args[]){
		//runConsolidated();
		runPerJavaMethod();
	}
}

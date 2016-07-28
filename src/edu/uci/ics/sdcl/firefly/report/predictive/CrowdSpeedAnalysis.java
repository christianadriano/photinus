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

	private String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7",
			"HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

	private HashMap<String,String> bugCoveringMap;

	public CrowdSpeedAnalysis(){

		//Obtain bug covering question list
		PropertyManager manager = PropertyManager.initializeSingleton();
		bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		for(String questionID:listOfBugPointingQuestions){
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
				AnswerData.count(answerData.answerMap, Answer.I_DONT_KNOW));

		return outcome;
	}

	//-------------------------------------------------------------------------------------------------------
	private Integer countWorkers(
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
		return workerMap.size();
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

		Integer totalDifferentWorkersAmongHITs = countWorkers(microtaskMap, null);

		DataPoint positiveVDataPoint = new DataPoint();
		DataPoint majorityVDataPoint = new DataPoint();

		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		HashMap<String, QuestionLinesMap> lineMapping =  loader.loadList();

		
		for(String fileName: fileNameList){//each fileName is a Java method
			HashMap<String, ArrayList<String>> answerMap = extractAnswersForFileName(microtaskMap,fileName);
			Integer workerCountPerHIT = countWorkers(microtaskMap,fileName);
			AnswerData data = new AnswerData(fileName,answerMap,bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);
			Consensus predictor = new AcrossQuestionsConsensus(2);
			Outcome outcome = computeDataPoint(data,predictor,lineMapping);
			positiveVDataPoint.fileNameOutcomeMap.put(fileName, outcome);

			predictor = new WithinQuestionConsensus();
			outcome = computeDataPoint(data,predictor,lineMapping);
			majorityVDataPoint.fileNameOutcomeMap.put(fileName, outcome);
		}
		
		positiveVDataPoint.maxAnswersHIT = MicrotaskMapUtil.countAnswers(microtaskMap).doubleValue();
		majorityVDataPoint.maxAnswersHIT = positiveVDataPoint.maxAnswersHIT;
		
		positiveVDataPoint.computeAverages();//Compute the average precision and recall for all Java methods
		majorityVDataPoint.computeAverages();

		positiveVDataPoint.elapsedTime = elapsedTime;
		majorityVDataPoint.elapsedTime = elapsedTime;

		positiveVDataPoint.totalWorkers = new Double(totalDifferentWorkersAmongHITs);
		majorityVDataPoint.totalWorkers = new Double(totalDifferentWorkersAmongHITs);
		
		this.outcomes_PositiveVoting.add(positiveVDataPoint);
		this.outcomes_MajorityVoting.add(majorityVDataPoint);
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

	private String getOldHeader(){
		return "Answer level,Average Precision_PV,Average Recall_PV,Average Precision_MV,Average Recall_MV, Total Workers, Total Answers, "
				+ " Hours taken, Faults Located_PV, Faults Located_MV, False Positives";
	}

	public void printOldDataPointsToFile(){

		String destination = "C://firefly//SpeedAnalysis//speedAnalysis_all.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(getHeader()+"\n");

			for(int i=0; i<this.outcomes_MajorityVoting.size();i++){
				DataPoint datapointPV = this.outcomes_PositiveVoting.get(i);
				DataPoint datapointMV = this.outcomes_MajorityVoting.get(i);

				String line= new Integer(i+1).toString() +","+
						datapointPV.averagePrecision.toString()+","+
						datapointPV.averageRecall.toString()+","+
						datapointMV.averagePrecision.toString()+","+
						datapointMV.averageRecall.toString()+","+
						datapointMV.totalWorkers.toString()+","+
						datapointMV.maxAnswersHIT.toString()+","+
						datapointMV.elapsedTime.toString()+","+
						datapointPV.faultsLocated.toString()+","+
						datapointMV.faultsLocated.toString()+","+
						datapointPV.falsePositives.toString();
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
		return "Answer level,Average Precision,Average Recall,Total Workers, Total Answers, "
				+ "Hours taken, Faults Located, True Positive Lines, Near Positive Lines, False Positive Lines,"
				+ "Total lines to inspect";
	}

	public void printDataPointsToFile(){

		String destination = "C://firefly//SpeedAnalysis//speedAnalysis_all.csv";
		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(getHeader()+"\n");

			for(int i=0; i<this.outcomes_PositiveVoting.size();i++){
				DataPoint datapointPV = this.outcomes_PositiveVoting.get(i);

				Integer linesToInspect = datapointPV.getTotalLinesToInspect();
				
				String line= new Integer(i+1).toString() +","+
						datapointPV.averagePrecision.toString()+","+
						datapointPV.averageRecall.toString()+","+
						datapointPV.totalWorkers.toString()+","+
						datapointPV.maxAnswersHIT.toString()+","+
						datapointPV.elapsedTime.toString()+","+
						datapointPV.faultsLocated.toString()+","+
						datapointPV.truePositiveLinesCount.toString()+","+
						datapointPV.falsePositiveLinesCount.toString()+","+
						datapointPV.nearPositiveLinesCount.toString()+","+
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

	
	
	/**
	 * 
	 * @param map
	 * @param minAnswers the minimal number of answers for which all questions
	 */
	public void simulateMonteCarlo(HashMap<String, Microtask> map, int maxAnswers){
		
	}
	
	//----------------------------------------------------------------------

	public static void main(String args[]){
		CrowdSpeedAnalysis analysis =  new CrowdSpeedAnalysis();
		analysis.computeElapsedTimeForAnswerLevels(analysis.getFilteredMap());
		analysis.printDataPointsToFile();
	}

}

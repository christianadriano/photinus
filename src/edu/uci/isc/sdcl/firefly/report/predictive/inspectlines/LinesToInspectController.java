package edu.uci.isc.sdcl.firefly.report.predictive.inspectlines;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.predictive.AnswerData;
import edu.uci.ics.sdcl.firefly.report.predictive.FilterCombination;
import edu.uci.ics.sdcl.firefly.report.predictive.Outcome;
import edu.uci.ics.sdcl.firefly.report.predictive.AcrossQuestionsConsensus;
import edu.uci.ics.sdcl.firefly.report.predictive.Consensus;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

/** Compute the lines to inspect under various crowd consensus configurations
 * 
 * @author adrianoc
 *
 */
public class LinesToInspectController {


	String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7",
			"HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

	HashMap<String, ArrayList<OutcomeInspect>> outcomeMap;

	private OutcomeInspect computeDataPoint(AnswerData answerData, Consensus predictor, HashMap<String, QuestionLinesMap> linesMapping) {

		OutcomeInspect outcome = new OutcomeInspect(null,
				answerData.getHitFileName(),
				predictor.getName(),
				predictor.computeSignal(answerData),
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
				predictor.getTruePositiveFaultyLineCount(linesMapping),
				predictor.getTruePositiveNearFaultyLineCount(linesMapping),
				predictor.getFalsePositiveLineCount(linesMapping));
		return outcome;
	}

	public HashMap<String, Microtask> getCutAnswers(int maximum){


		HashMap<String, Microtask> cutMicrotaskMap = new HashMap<String, Microtask>();
		FileSessionDTO sessionDTO = new FileSessionDTO();

		HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) sessionDTO.getMicrotasks();

		Iterator<String> iter = microtaskMap.keySet().iterator();
		while(iter.hasNext()){
			String id = iter.next();
			Microtask microtask = microtaskMap.get(id);
			Vector<Answer> answerlist = microtask.getAnswerList();
			if(answerlist.size()<maximum)
				System.err.println("AnswerList < "+maximum+", size:"+answerlist.size()+", id:"+id);

			microtask.setAnswerList(new Vector<Answer>(answerlist.subList(0, maximum)));
			cutMicrotaskMap.put(id, microtask);

		}
		return cutMicrotaskMap;
	}



	public void runAcrossQuestions(int calibration){

		this.outcomeMap = this.initializeOutcomeMap();


		PropertyManager manager = PropertyManager.initializeSingleton();
		HashMap<String,String> bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		for(String questionID:listOfBugPointingQuestions){
			bugCoveringMap.put(questionID,questionID);
		}

		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		HashMap<String, QuestionLinesMap> linesMapping =  loader.loadList();

		for(String fileName: fileNameList){

			for(int i=1; i<=20;i++){
				HashMap<String, Microtask> cutMicrotaskMap = this.getCutAnswers(i);
				Integer totalDifferentWorkersAmongHITs = countWorkers(cutMicrotaskMap, null);
				HashMap<String, ArrayList<String>> answerMap = extractAnswersForFileName(cutMicrotaskMap,fileName);
				Integer workerCountPerHIT = countWorkers(cutMicrotaskMap,fileName);
				AnswerData data = new AnswerData(fileName,answerMap,bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);

				AcrossQuestionsConsensus predictor = new AcrossQuestionsConsensus();
				predictor.setCalibrationLevel(calibration);
				OutcomeInspect outcome = this.computeDataPoint(data,predictor,linesMapping);
				ArrayList<OutcomeInspect> list = outcomeMap.get(fileName);
				list.add(outcome);
				outcomeMap.put(fileName,list);
			}
			printOutcomeMap(fileName, fileName+"_AQ_"+calibration);			
		}

	}
	public void printOutcomeMap(String fileName, String outputFileName){	

		String destination = "C://firefly//InspectLinesAnalysis//"+ outputFileName+".txt";
		BufferedWriter log;
		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header
			ArrayList<OutcomeInspect> list= this.outcomeMap.get(fileName);
			log.write(list.get(0).getHeader()+"\n");
			for(OutcomeInspect outcome: list){
				String line= outcome.toString();
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

	private HashMap<String, ArrayList<OutcomeInspect>> initializeOutcomeMap(){
		HashMap<String, ArrayList<OutcomeInspect>> outcomeMap =  new  HashMap<String, ArrayList<OutcomeInspect>>();
		for(String fileName: fileNameList){
			ArrayList<OutcomeInspect> list =  new ArrayList<OutcomeInspect>();
			outcomeMap.put(fileName, list);
		}
		return outcomeMap;
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

	public static void main(String args[]){

		LinesToInspectController controller = new LinesToInspectController();
		//controller.runAcrossQuestions(1);
		//controller.runAcrossQuestions(2);
		controller.runAcrossQuestions(3);


	}

}

package edu.uci.ics.sdcl.firefly.report.predictive.inspectlines;

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
import edu.uci.ics.sdcl.firefly.report.predictive.WithinQuestionConsensus;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

/** Compute the lines to inspect under various crowd consensus configurations
 * 
 * @author adrianoc
 *
 */
public class LinesToInspectController {


	String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7",
			"HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

	HashMap<String, ArrayList<Outcome>> outcomeMap;
	
	HashMap<String, Microtask> cutMicrotaskMap;

	private Outcome computeDataPoint(AnswerData answerData, Consensus predictor, HashMap<String, QuestionLinesMap> lineMapping) {

		Boolean signal = predictor.computeSignal(answerData);
		HashMap<String, Integer> truePositiveLines = predictor.getTruePositiveLines(lineMapping);
		HashMap<String, Integer> nearPositiveLines = predictor.getNearPositiveLines(lineMapping);
		HashMap<String, Integer> falsePositiveLines = predictor.getFalsePositiveLines(lineMapping);
		HashMap<String, Integer> falseNegativeLines = predictor.getFalseNegativeLines(lineMapping);
		falsePositiveLines = Consensus.removeFalsePositiveDuplications(nearPositiveLines,falsePositiveLines);
		falsePositiveLines = Consensus.removeFalsePositiveDuplications(truePositiveLines,falsePositiveLines);
		HashMap<String, HashMap<String, Integer>> questionMap = predictor.getNearPositiveLinesQuestions(lineMapping);


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
				falsePositiveLines,
				falseNegativeLines);

		outcome.questionMap=questionMap;

		return outcome;
	}


	public HashMap<String, Microtask> getCutAnswers(int maximum){

		HashMap<String, Microtask> cutMicrotaskMap = new HashMap<String, Microtask>();
		FileSessionDTO sessionDTO = new FileSessionDTO();
		HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) sessionDTO.getMicrotasks();

		for(Map.Entry<String,Microtask> entry : microtaskMap.entrySet()){
			String id = entry.getKey();
			Microtask microtask = microtaskMap.get(id);
			Vector<Answer> answerlist = microtask.getAnswerList();
			if(answerlist.size()<maximum)
				System.err.println("AnswerList < "+maximum+", size:"+answerlist.size()+", id:"+id);

			microtask.setAnswerList(new Vector<Answer>(answerlist.subList(0, maximum)));
			microtask.setLOC_CoveredByQuestion(QuestionLinesMapLoader.questionLineCount.get(id));
			cutMicrotaskMap.put(id, microtask);

		}
		return cutMicrotaskMap;
	}



	public void printOutcomeMap(  String fileName, String outputFileName){	

		String destination = "C://firefly//InspectLinesAnalysis//"+ outputFileName+".csv";
		BufferedWriter log;
		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header
			ArrayList<Outcome> list= this.outcomeMap.get(fileName);
			log.write("#answers,"+list.get(0).getHeader()+"\n");
			for(int i=0;i<list.size();i++){
				Outcome outcome = list.get(i);
				String line= outcome.toString();
				log.write(i+1+","+line+"\n");
			}
			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}

	public void printLinesFlaggedInQuestion(String fileName, String outputFileName){

		String destination = "C://firefly//InspectLinesAnalysis//LinesFlaggedInQuestion//"+ outputFileName+"_LinesFlagged.csv";
		BufferedWriter log;
		
		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header
			ArrayList<Outcome> list= this.outcomeMap.get(fileName);
			log.write("fileName, #answers, question, lines,questionType,LOC covered by question"+ "\n");
			for(int i=0;i<list.size();i++){
				Outcome outcome = list.get(i);
				Iterator<String> iter = outcome.questionMap.keySet().iterator();
				int answerLevel = i+1;
				while(iter.hasNext()){
					String key = iter.next();
					String questionType = this.cutMicrotaskMap.get(key).getQuestionType();
					Integer locCoveredByQuestion = this.cutMicrotaskMap.get(key).getLOC_CoveredByQuestion();
					HashMap<String,Integer> lineMap = outcome.questionMap.get(key);
					String lines = outcome.linesToString(lineMap);
					log.write(fileName+","+answerLevel+","+key+","+lines+","+questionType+","+locCoveredByQuestion+"\n");
				}
			}
			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}

	}

	private HashMap<String, ArrayList<Outcome>> initializeOutcomeMap(){
		HashMap<String, ArrayList<Outcome>> outcomeMap =  new  HashMap<String, ArrayList<Outcome>>();
		for(String fileName: fileNameList){
			ArrayList<Outcome> list =  new ArrayList<Outcome>();
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


	//-------------------------------------------------------
	/** Entry point of all computations
	 * 
	 * @param consensus
	 */
	public void run(Consensus consensus){

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
				this.cutMicrotaskMap = this.getCutAnswers(i);
				Integer totalDifferentWorkersAmongHITs = countWorkers(cutMicrotaskMap, null);
				HashMap<String, ArrayList<String>> answerMap = extractAnswersForFileName(cutMicrotaskMap,fileName);
				Integer workerCountPerHIT = countWorkers(cutMicrotaskMap,fileName);
				AnswerData data = new AnswerData(fileName,answerMap,bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);

				consensus.setData(data);
				Outcome outcome = this.computeDataPoint(data,consensus,linesMapping);
				ArrayList<Outcome> list = outcomeMap.get(fileName);
				list.add(outcome);
				outcomeMap.put(fileName,list);
			}
			printOutcomeMap(fileName, fileName+"_"+consensus.getName());	
			printLinesFlaggedInQuestion (fileName, fileName+"_"+consensus.getName());
		}

	}

	public static void main(String args[]){

		LinesToInspectController controller = new LinesToInspectController();

		//Compute across-questions consensus
		AcrossQuestionsConsensus acrossQuestionsConsensus = new AcrossQuestionsConsensus(2);

		//Compute within-question consensus
		WithinQuestionConsensus withinQuestionConsensus = new WithinQuestionConsensus(WithinQuestionConsensus.Balance_YES_NO_Consensus,0,0);
		

		controller.run((Consensus)acrossQuestionsConsensus);
	}

}

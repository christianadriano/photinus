package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;
import edu.uci.ics.sdcl.firefly.util.ReadWriteFile;
import edu.uci.ics.sdcl.firefly.util.WorkerUtil;

/**
 * Lists worker score and profession by Java method.
 * The idea is to check any Java method received more skillful workers, which might explain
 * some differences in precision of answers for some Java methods. 
 *
 * @author adrianoc
 *
 */
public class WorkerScoreProfessionByJavaMethod {

	public static void main(String args[]){
		WorkerScoreProfessionByJavaMethod object = new WorkerScoreProfessionByJavaMethod();
		object.run();
		object.printAll();
		object.printScoresPerJavaMethod();
		
	}
	
	private String path = "c:/firefly/BetweenJavaMethods/";
	
	private void printAll() {
		
		String fileName = "allAnswers.csv";
		ReadWriteFile.writeBackToBuffer(lineList, path, fileName);
	}

	private ArrayList<String> lineList;
	private ArrayList<OutcomeLine> outcomeLineList;
	
	public WorkerScoreProfessionByJavaMethod(){
		lineList =  new ArrayList<String>();
		outcomeLineList = new ArrayList<OutcomeLine>();
		lineList.add(OutcomeLine.header);
	}
	
	public class OutcomeLine{
		public static final String header = "Java Method, MicrotaskID, IsBugCovering, Answer Option, WorkerID, Worker Score, Worker Profession, Confidence, Difficulty, Duration, Explanation" ;
		
		public String javaMethod;
		public String microtaskID;
		public String isBugCovering;
		public String answerOption;
		public String workerScore;
		public String workerProfession;
		public String answerConfidence;
		public String answerDifficulty;
		public String answerExplanation;
		public String workerID;
		public String answerDuration;
		
		private String token=",";

		
		
		public String toString(){
			return javaMethod+token+microtaskID+token+isBugCovering+token+answerOption+token+
					workerID+token+workerScore+token+workerProfession+token+answerConfidence+token+
					answerDifficulty+token+answerDuration+token+answerExplanation;
		}
		
	}
	
	
	
	private void run() {
	
			
		HashMap<String, String> bugCoveringMap = BugCoveringMap.initialize();	
		HashMap<String, Microtask> microtaskMap = MicrotaskMapUtil.initialize();
		
		for (Map.Entry<String, Microtask> entry : microtaskMap.entrySet()) {
			String id= entry.getKey();
			Microtask task =  entry.getValue();
			Vector<Answer> answerList = task.getAnswerList();
			for(Answer answer:answerList){
				OutcomeLine line = new OutcomeLine();
				line.microtaskID =id;
				line.answerConfidence = new Integer(answer.getConfidenceOption()).toString();
				line.answerDifficulty = new Integer(answer.getDifficulty()).toString();
				line.answerOption = answer.getShortOption();
				line.answerExplanation = answer.getExplanation().replace(",", ";");
				line.workerID = answer.getWorkerId();
				line.workerProfession = WorkerUtil.getWorkerProfession(answer.getWorkerId());
				line.workerScore = WorkerUtil.getWorkerScore(answer.getWorkerId());
				line.isBugCovering = bugCoveringMap.containsKey(id)?"yes":"no";
				line.javaMethod = task.getFileName();
				line.answerDuration = answer.getElapsedTime();
				lineList.add(line.toString());
				outcomeLineList.add(line);
			}
		}
	}
	
	private void printScoresPerJavaMethod(){
		
		StringBuffer buffer;
		HashMap<String, StringBuffer> map = new HashMap<String, StringBuffer> ();
		for(OutcomeLine line : outcomeLineList){
			if(!map.containsKey(line.javaMethod)){
				 buffer = new StringBuffer();
			}
			else{
				buffer = map.get(line.javaMethod);
			}
			buffer.append(line.workerScore);
			buffer.append(line.token);
			 map.put(line.javaMethod, buffer);
		}
		
		ArrayList<String> linesToPrint =  new ArrayList<String> ();
		for(Map.Entry<String,StringBuffer> entity : map.entrySet()){
			String lineStr = entity.getKey()+","+entity.getValue();
			linesToPrint.add(lineStr);
		}
		
		String fileName = "workerScoreByJavaMethod.csv";
		ReadWriteFile.writeBackToBuffer(linesToPrint, path, fileName);
	}
	
		
	
}

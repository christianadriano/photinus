package edu.uci.ics.sdcl.firefly.report.voting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;

public class AnswerPerQuestionAnalysis {

	static String samsungPath = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String dellPath = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String currentPath = samsungPath;

	LogData data;

	HashMap<String,ArrayList<Answer>> filteredMicrotaskMap;
	HashMap<String, Integer> yesAnswerMap;
	HashMap<String, Integer> probYesAnswerMap;
	HashMap<String,Result> TP_FP_FN_Map;
	HashMap<String, Microtask> scoreFilterCutMicrotaskMap; //Remove all microtasks from workers with Score below and cut to the first N answers for each microtask

	public AnswerPerQuestionAnalysis(LogData data){
		this.data = data;
		this.initializeDataStrutures();
	}

	public void initializeDataStrutures(){
		this.filteredMicrotaskMap = new 	HashMap<String,ArrayList<Answer>>();
		this.yesAnswerMap = new HashMap<String, Integer>();
		this.probYesAnswerMap = new HashMap<String,Integer>();
		this.TP_FP_FN_Map = new HashMap<String,Result>();
	}

	private static AnswerPerQuestionAnalysis initializeLogs(){
		LogData data = new LogData(false, 0);

		String path = currentPath;
		path = path + "\\RawDataLogs\\";

		data.processLogProduction1(path);
		data.processLogProduction2(path);

		System.out.println("Logs loaded! Totals are:");
		System.out.println("Consents: "+data.getConsents());
		System.out.println("SkillTests: "+data.getSkillTests());
		System.out.println("Surveys: "+data.getSurveys());
		System.out.println("Sessions Opened: "+data.getOpenedSessions());
		System.out.println("Sessions Closed: "+data.getClosedSessions());
		System.out.println("Answers in Map: "+data.getNumberOfMicrotasks());
		System.out.println("Microtasks in List: "+data.microtaskList.size());
		System.out.println("Workers in Map: "+data.workerMap.size());

		AnswerPerQuestionAnalysis analysis = new AnswerPerQuestionAnalysis(data);
		return analysis;
	}


	//----------------------------------------------------------------------------------------------------------

	/**Discard workers below certain Score workers, then cut.
	 * 
	 * @param cutlevel initially 10, but could be larger.
	 * @param minimumDuration
	 * @param maxDuration
	 * @param minimumGrade
	 * @param maxGrade
	 * @param numberOfICanTell
	 * @param lowerNumberOfICantTell
	 * @return
	 */
	private void filterCutMicrotasks(int cutLevel, int score){

		System.out.println("Cut:");
		this.scoreFilterCutMicrotaskMap = new HashMap<String,Microtask>();

		Iterator<String> iter=data.microtaskMap.keySet().iterator();
		int  totalAnswers=0;
		while(iter.hasNext()){
			String taskID = iter.next();
			Microtask task = data.microtaskMap.get(taskID);
			Microtask newTask = task.getSimpleVersion();
			newTask.setAnswerList(new Vector<Answer>());//Remove all answers
			int validMicrotaskAnswers=0;
			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);					
				String workerId = answer.getWorkerId();
				Worker worker = data.workerMap.get(workerId);
				if(worker!=null && worker.getGrade()>score){
					if(newTask.getAnswerList().size()<cutLevel){//Add only answers if we didn't have reached the cut level
						newTask.addAnswer(answer);
						validMicrotaskAnswers++;
					}
				}
				//else discard answer from worker
			}
			this.scoreFilterCutMicrotaskMap.put(taskID, newTask);
			//System.out.println(taskID+":"+validMicrotaskAnswers);
			totalAnswers = totalAnswers + validMicrotaskAnswers;
		}
		System.out.println("TotalAnswers:"+totalAnswers);
		System.out.println("----------------------------");
	}

	private void printQuestionAnswers(){
		Iterator<String> iter= this.scoreFilterCutMicrotaskMap.keySet().iterator(); 

		System.out.println("Task ID | Total Answers | Yes");
		while(iter.hasNext()){
			String taskID = iter.next();
			Microtask task = this.scoreFilterCutMicrotaskMap.get(taskID);
			Vector<Answer> answerList = task.getAnswerList();
			Integer yesCount=0;
			Integer answerListSize=0;
			if(answerList!=null && answerList.size()>0){
				answerListSize = answerList.size();
				yesCount = this.countAnswerOption(answerList, Answer.YES);
			}
			System.out.println(taskID+"|"+answerListSize+"|"+yesCount);
		}

	}

	private int countAnswerOption(Vector<Answer> answerList, String optionType){
		int count=0;
		for(Answer answer:answerList){
			String option = answer.getOption();
			if(option.matches(optionType))
				count++;
		}
		return count;
	}

	
	//------------------------------------------------------------------------------------------------
	
	public static void main(String[] args){

		AnswerPerQuestionAnalysis analysis = initializeLogs();

		String path =  currentPath;
		path = path +"\\DataAnalysis\\BaseDataInTime\\cutFilter\\";
		
		analysis.filterCutMicrotasks(-1, 1);
		analysis.printQuestionAnswers();
		
	}

}

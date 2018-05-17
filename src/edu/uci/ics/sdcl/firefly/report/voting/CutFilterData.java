package edu.uci.ics.sdcl.firefly.report.voting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;

/** 
 * Produces a data set for spreadsheets run analyzes.
 * Data set is the id of the question and all answers for that question
 * 
 * @author Christian Adriano
 *
 */
public class CutFilterData {

	private static String samsungPath = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	private static String dellPath = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	private static String currentPath = samsungPath;
	
	private HashMap<String, Microtask> cutMicrotaskMap; //Remove all microtasks above a cut level
	
	private HashMap<String, String> discardedWorkerMap;
	private HashMap<String, String> activeWorkerMap; //Workers whose answer was included in the analysis.
	
	private LogData data;
	
	public CutFilterData(LogData data){
		this.data = data;
	}

	private static CutFilterData initializeLogs(){
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

		CutFilterData cutFilterData = new CutFilterData(data);
		return cutFilterData;
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
	private HashMap<String, Microtask> cutMicrotaskMap(int cutLevel){
		
		//System.out.println("Cut:");
		this.cutMicrotaskMap = new HashMap<String,Microtask>();
		
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
				if(newTask.getAnswerList().size()<cutLevel){//Add only answers if we didn't have reached the cut level
						newTask.addAnswer(answer);
						validMicrotaskAnswers++;
				}
				//else discard answer 
			}
			this.cutMicrotaskMap.put(taskID, newTask);
			System.out.println(taskID+":"+validMicrotaskAnswers);
			totalAnswers = totalAnswers + validMicrotaskAnswers;
		}
		System.out.println("TotalAnswers:"+totalAnswers);
		System.out.println("----------------------------");
		return this.cutMicrotaskMap;
	}
	
	
	//----------------------------------------------------------------------------------------------------------
/**
 * 
 * @param microtaskMap the set of microtasks that will be filtered.
 * @param minimumDuration
 * @param maxDuration
 * @param minimumGrade
 * @param maxGrade
 * @param numberOfICanTell
 * @param lowerNumberOfICantTell
 * @return
 */
	private HashMap<String,String> writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(HashMap<String, Microtask> microtaskMap, 
			Double minimumDuration, Double maxDuration, Integer minimumGrade, Integer maxGrade, Integer numberOfICanTell, 
			Integer lowerNumberOfICantTell){
		
		HashMap<String, String> contentMap = new HashMap<String, String>();

		Iterator<String> iter = microtaskMap.keySet().iterator();
		
		discardedWorkerMap = new HashMap<String, String>();//just to analyze who are the workers in terms of skill test score
		activeWorkerMap = new HashMap<String, String>();
		int answerCount=0;
		int validAnswers=0;
		System.out.println("Worker ID| option | duration");
		while(iter.hasNext()){
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = microtaskMap.get(id);
			buffer.append(task.getID().toString());
			buffer.append("|");
			int validMicrotaskAnswers=0;
			Vector<Answer> answerList = task.getAnswerList();
			
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);					
				String workerId = answer.getWorkerId();
				Integer count = data.workerICantTellMap.get(workerId);
				Worker worker = data.workerMap.get(workerId);
				Integer grade = worker.getGrade();	
				Double duration = new Double(answer.getElapsedTime());
				if(count!=null && count.intValue()<numberOfICanTell && count.intValue()>lowerNumberOfICantTell && 
						grade!=null && grade>=minimumGrade && grade<=maxGrade && 
						duration>=minimumDuration&& duration<=maxDuration){
					System.out.println(workerId+"|"+answer.getOption()+"|"+duration.toString());
					
					answerCount++;
					validMicrotaskAnswers++;
					activeWorkerMap.put(workerId, workerId);
					buffer.append(answer.getOption());
					buffer.append("|");
				}
				else{
					//System.out.println("Worker "+ workerId+" discarded, I Cannot Tell count= "+count+", grade="+grade+", duration="+duration);
					this.discardedWorkerMap.put(workerId,workerId);
				}
			}

			if(validMicrotaskAnswers>10)
				validAnswers = validAnswers+10;//because we would ignore anything above 10 answers for one same question.
			else
				validAnswers = validAnswers+validMicrotaskAnswers;
			contentMap.put(task.getID().toString(), buffer.toString());
		}
		//System.out.println("duration|validAnswers|activeWorkers");
		String duration = new Double(minimumDuration/1000).toString();
		duration = duration.substring(0, duration.length()-2);
		System.out.println(duration+"|"+numberOfICanTell+"|"+validAnswers+"|"+this.activeWorkerMap.size());
		//
		System.out.println(validAnswers);//this.activeWorkerMap.size());// ", active workers: "+);
		return contentMap;
	}
	
	//-----------------------------------------------------------------
	private ArrayList<String> mapToSortedList(HashMap<String, String> map){
		
		ArrayList<String> sortedList = new ArrayList<String>();
				
		for(int i=0;i<215;i++){
			String index = new Integer(i).toString();
			String content = map.get(index);
			sortedList.add(content);
		}
		
		return sortedList;		
	}

	//------------------------------------------------------------------------------------------------

	private void printToFile(String fileNamePath, ArrayList<String> contentList){
		try{
			File file = new File(fileNamePath);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(String content: contentList){
				writer.write(content);
				writer.newLine();
			}
			writer.close();
		}
		catch(Exception e){
			System.err.println(e.toString());
		}
	}

	public void printActiveWorkerMap(){
		System.out.println("Active WorkerMap");
		Iterator<String> iter = this.activeWorkerMap.keySet().iterator();
		System.out.println("worker | score | number of answers ");
		while(iter.hasNext()){
			String workerID = iter.next();
			Worker worker = this.data.workerMap.get(workerID);
			//discovers grade score
			Integer score = worker.getGrade();
			//discover number of answers
			String sessionID = worker.getSessionId();
			WorkerSession session = this.data.sessionMap.get(sessionID);
			Vector<Microtask> taskList = session.getMicrotaskList();
			int count =0;
			if(taskList!=null)
				 count = taskList.size();
			System.out.println(workerID+"|"+score+"|"+count);
		}
	}
	
	
	//-----------------------------------------------------------------------------------
	public static void main(String[] args){

		CutFilterData cutData = initializeLogs();

		String path =  currentPath;
		path = path +"\\1.DataAnalysis(CutFirst)\\data\\filter13\\";

		Double maxDuration = new Double(Double.MAX_VALUE);
		Integer[] durationList = {10};//,15,20,30,45,60,120}; //Minimal duration to be considered
		Integer[] scoreList = {3};//,4};//,4};  //Minimal Score to be considered		
		Integer[] idkList = {2};//,4,6,8,10}; //I Can't Tell answer count that would eliminate workers
		Integer lowerCut_idk = -1;  //Worker that has an equal amount below will be cut out of the set.
		Integer maxScore=5; //Worker has to have grade below that.
		int i=0;
		
		HashMap<String, Microtask> cutMap = cutData.cutMicrotaskMap(205);//just cut the 10 first answers
		
		while(i<durationList.length){
			int j=0;
			String durationStr = durationList[i].toString();
			Double duration = new Double(durationList[i].doubleValue()*1000);
			while(j<scoreList.length){
				String scoreStr = scoreList[j].toString();
				int k=0;
				while(k<idkList.length){
					String idkStr = idkList[k].toString();
					String fileName = "Cut_Filter_"+durationStr+"_"+idkStr+".txt"; //"s_test-"+scoreStr+"_"+maxScore+"
					ArrayList<String> sortedList = cutData.mapToSortedList(cutData.writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(cutMap, duration, maxDuration, scoreList[j],maxScore,idkList[k],lowerCut_idk));
					cutData.printToFile(path+fileName, sortedList);
					k++;
				}
				j++;
			}
			i++;
		}	
		System.out.println("files written, look at: "+path);
		
		cutData.printActiveWorkerMap();

	}
	
	
}

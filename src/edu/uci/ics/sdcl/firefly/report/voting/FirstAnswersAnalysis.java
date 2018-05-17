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
import edu.uci.ics.sdcl.firefly.report.voting.LogAnalysis.Counter;

public class FirstAnswersAnalysis {

	LogData data;

	HashMap<Integer,Microtask> filteredMicrotaskMap; 
	HashMap<String, String> discardedWorkerMap;
	HashMap<String, String> activeWorkerMap;

	static String samsungPath = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String dellPath = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String currentPath = samsungPath;

	public FirstAnswersAnalysis(LogData data){
		this.data = data;
	}

	private void initializeMap(){		

		filteredMicrotaskMap = new HashMap<Integer,Microtask>();		

		for(int i=0;i<215;i++){
			Integer id = new Integer(i);
			Microtask originalMicrotask = this.data.microtaskMap.get(id.toString());
			Microtask simpleTask = originalMicrotask.getSimpleVersion();
			simpleTask.setAnswerList(new Vector<Answer>());
			filteredMicrotaskMap.put(id, simpleTask);
		}
	}

	/**
	 * Remove all answers above a threshold
	 * @param maximumAnswers the number of answers that will be considered for each worker
	 */
	public void filterWorkerAnswers(int maximumAnswers){	
		initializeMap();
		Iterator<String> workerIdIter = data.workerMap.keySet().iterator();
		while(workerIdIter.hasNext()){
			String workerId = workerIdIter.next();
			Worker worker = data.workerMap.get(workerId);
			String sessionId = worker.getSessionId();
			if(sessionId!=null){
				WorkerSession session = data.sessionMap.get(sessionId);
				Vector<Microtask> microtaskVector = session.getMicrotaskList();
				int i=0;
				while(i<microtaskVector.size() && i<maximumAnswers){
					Microtask workerMicrotask = microtaskVector.elementAt(i);
					addToFilteredMicrotasks(workerMicrotask, workerId);
					i++;
				}
			}
		}
	}

	/** Add the worker's microtask to the list */
	public void addToFilteredMicrotasks(Microtask workerMicrotask, String workerId){

		Microtask filteredMicrotask = this.filteredMicrotaskMap.get(workerMicrotask.getID());

		if(filteredMicrotask==null)
			this.filteredMicrotaskMap.put(workerMicrotask.getID(), workerMicrotask);
		else{
			Answer workerAnswer = workerMicrotask.getAnswerByUserId(workerId);
			filteredMicrotask.addAnswer(workerAnswer);
			this.filteredMicrotaskMap.put(filteredMicrotask.getID(), filteredMicrotask);
		}
	}

	/**
	 * 
	 * @param answerOption if true, collect answer option, otherwise, answer elapsed Time. 
	 * @param minimumDuration
	 * @param maxDuration
	 * @param minimumGrade
	 * @param maxGrade
	 * @param maxNumberOfICanTell
	 * @param lowerNumberOfICantTell
	 * @return
	 */
	private ArrayList<String> writeAnswers_Filtered_by_DURATION_GRADE_IDK(boolean answerOption, Double minimumDuration, Double maxDuration,
			Integer minimumGrade, Integer maxGrade, Integer maxNumberOfICanTell, Integer lowerNumberOfICantTell){
		ArrayList<String> contentList = new ArrayList<String>();

		//System.out.println("Size of microtask Map: "+ data.microtaskMap.size());

		Iterator<Integer> iter=this.filteredMicrotaskMap.keySet().iterator();
		discardedWorkerMap = new HashMap<String, String>();//just to analyze who are the workers in terms of skill test score
		activeWorkerMap = new HashMap<String, String>();
		int answerCount=0;
		int validAnswers=0;
		while(iter.hasNext()){
			StringBuffer buffer = new StringBuffer();//new line
			Integer id = iter.next();
			Microtask task = this.filteredMicrotaskMap.get(id);
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
				if(count!=null && count.intValue()<maxNumberOfICanTell && count.intValue()>=lowerNumberOfICantTell && 
						grade!=null && grade>=minimumGrade && grade<=maxGrade && 
						duration>=minimumDuration&& duration<=maxDuration){
					answerCount++;
					validMicrotaskAnswers++;
					activeWorkerMap.put(workerId, workerId);
					if(answerOption)
						buffer.append(answer.getOption());
					else
						buffer.append(answer.getElapsedTime());
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
			contentList.add(buffer.toString());
		}
		System.out.println("valid answers=" + validAnswers+ ", active workers: "+this.activeWorkerMap.size());
		//System.out.println(validAnswers);//this.activeWorkerMap.size());// ", active workers: "+);
		return contentList;
	}

	public void printActiveWorkerMap(){
		System.out.println("Active WorkerMap");
		Iterator<String> iter = this.activeWorkerMap.keySet().iterator();
		while(iter.hasNext()){
			System.out.println(iter.next());
		}
	}

	private static FirstAnswersAnalysis initializeLogs(){
		boolean rejectNoisyWorkers = false;
		double minimalDuration = 0;
		LogData data = new LogData(false, 0);	

		String path = currentPath;
		path = path + "\\RawDataLogs\\";

		data.processLogProduction1(path);
		data.processLogProduction2(path);

		data.printSummary();

		FirstAnswersAnalysis firstData = new FirstAnswersAnalysis(data);
		return firstData;
	}

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


	//--------------------------------------------------------------------------

	public static void main(String[] args){

		FirstAnswersAnalysis firstData = initializeLogs();

		String path =  currentPath;
		path = path +"\\DataAnalysis\\BaseDataInTime\\firstAnswers\\";

		Double minDuration = new Double(10000.00);
		Double maxDuration = new Double(Double.MAX_VALUE); //for duration average I cut at 1h, but for answer option not.
		int minScore=3;
		int maxScore=4;
		int eliminationICT=2; //2,4,6,8,10}; //I Can't Tell answer count that would eliminate workers
		int minICT=0;
		boolean answerOption = true; //collects elapsed time

		//for(int i=1;i<=10;i++){

			String fileName = "first_"+10+"_answers_option.txt";

			firstData.filterWorkerAnswers(10);
			firstData.printToFile(path+fileName, 
					firstData.writeAnswers_Filtered_by_DURATION_GRADE_IDK(answerOption, minDuration, maxDuration, minScore, maxScore, eliminationICT, minICT));

			
			
			System.out.println("files written, look at: "+path+fileName);
		//}
	}

}

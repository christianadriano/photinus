package edu.uci.ics.sdcl.firefly.report.voting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.QuestionType;
import edu.uci.ics.sdcl.firefly.QuestionTypeFactory;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.report.voting.LogAnalysis.Counter;

/** 
 * Generates the data in a  CSV
 * @author adrianoc
 *
 */
public class CSVData {

	private NumberFormat formatter = new DecimalFormat("#0.00"); 

	static String samsungPath = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String dellPath = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String currentPath = samsungPath;
	
	LogData data;

	/** Used to filter answers by question type */
	QuestionTypeFactory questionTypeFactory;

	public HashMap<String,Counter> counterMap;
	public HashMap<String, Result> bugReportResultMap;
	public HashMap<String, String> discardedWorkerMap;
	public HashMap<String, String> activeWorkerMap; //Workers whose answer was included in the analysis.
	
	public HashMap<String, Microtask> scoreFilterCutMicrotaskMap; //Remove all microtasks from workers with Score below and cut to the first N answers for each microtask
	
	//Sessions that are considered spurious
	public	HashMap<String,String> batch1RejectMap = new HashMap<String,String>();

	public	HashMap<String,String> batch2RejectMap = new HashMap<String,String>();

	public CSVData(LogData data){
		this.data = data;
		counterMap = new HashMap<String,Counter>(); 
		bugReportResultMap = new HashMap<String, Result>();
	}

	private static CSVData initializeLogs(){
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

		CSVData csvData = new CSVData(data);
		return csvData;
	}


	private ArrayList<String> writeMicrotaskAnswers_ZeroOnes(){

		ArrayList<String> contentList = new ArrayList<String>();

		//System.out.println("Size of list: "+ data.microtaskList.size());

		for(Microtask microtask: data.microtaskList){
			String yes="0";
			String no="0";
			String iDontKnow="0";

			StringBuffer buffer = new StringBuffer();
			buffer.append(microtask.getID().toString());
			buffer.append("|");
			Answer answer = microtask.getAnswerList().get(0);

			if(answer.getOption().trim().matches(Answer.YES))
				yes="1";
			else
				if(answer.getOption().trim().matches(Answer.NO))
					no="1";
				else
					if(answer.getOption().matches(Answer.I_DONT_KNOW))
						iDontKnow="1";
					else
						System.out.println("Microtask:"+microtask.getID().toString()+"Answer with no option:"+answer.getOption());

			buffer.append(yes);
			buffer.append("|");
			buffer.append(no);
			buffer.append("|");
			buffer.append(iDontKnow);

			contentList.add(buffer.toString());

		}
		//System.out.println("contentList size="+ contentList.size());
		return contentList;
	}

	private ArrayList<String> writeMicrotaskAnswers_Labels(){

		ArrayList<String> contentList = new ArrayList<String>();

		//System.out.println("Size of list: "+ data.microtaskList.size());

		for(Microtask microtask: data.microtaskList){
			String yes="0";
			String no="0";
			String ICantTell="0";

			StringBuffer buffer = new StringBuffer();
			buffer.append(microtask.getID().toString());
			buffer.append("|");
			Answer answer = microtask.getAnswerList().get(0);			
			buffer.append(answer.getOption());

			contentList.add(buffer.toString());

		}
		System.out.println("contentList size="+ contentList.size());
		return contentList;
	}	


	private ArrayList<String> writeAnswersInTime_Labels(){
		ArrayList<String> contentList = new ArrayList<String>();

		System.out.println("Size of list: "+ data.microtaskMap.size());

		Iterator<String> iter=data.microtaskMap.keySet().iterator();

		while(iter.hasNext()){
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);
			buffer.append(task.getID().toString());
			buffer.append("|");

			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);
				buffer.append(answer.getOption());
				if((i+1)<answerList.size()) //only appends if it is not the last position
					buffer.append("|");
			}
			contentList.add(buffer.toString());
		}

		System.out.println("contentList size="+ contentList.size());
		return contentList;

	}

	private ArrayList<String> writeAnswersInTime_Duration(){
		ArrayList<String> contentList = new ArrayList<String>();

		System.out.println("Size of list: "+ data.microtaskMap.size());

		Iterator<String> iter=data.microtaskMap.keySet().iterator();

		while(iter.hasNext()){
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);
			buffer.append(task.getID().toString());
			buffer.append("|");

			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);
				buffer.append(answer.getElapsedTime());
				if((i+1)<answerList.size()) //only appends if it is not the last position
					buffer.append("|");
			}
			contentList.add(buffer.toString());
		}

		System.out.println("contentList size="+ contentList.size());
		return contentList;

	}


	//--------------------------------------------------------------------------------


	private ArrayList<String> writeAnswerLabels_Filtered_by_Combined_WorkerICantTell_WorkerGrade(Integer numberOfICanTell, Integer minimumGrade){
		ArrayList<String> contentList = new ArrayList<String>();

		System.out.println("Size of microtask Map: "+ data.microtaskMap.size());

		Iterator<String> iter=data.microtaskMap.keySet().iterator();
		discardedWorkerMap = new HashMap<String, String>();//just to analyze who are the workers in terms of skill test score
		int answerCount=0;
		while(iter.hasNext()){
			//	boolean bufferHasAtLeastOneAnswer=false;
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);
			buffer.append(task.getID().toString());
			buffer.append("|");

			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);
				String workerId = answer.getWorkerId();
				Integer count = data.workerICantTellMap.get(workerId);
				Worker worker = data.workerMap.get(workerId);
				Integer grade = worker.getGrade();	
				if(count!=null && count.intValue()<numberOfICanTell && grade!=null && grade>=minimumGrade){
					answerCount++;
					buffer.append(answer.getOption());
					if((i+1)<answerList.size()) //only appends if it is not the last position
						buffer.append("|");
				}
				else{
					System.out.println("Worker "+ workerId+" discarded, I Cannot Tell count= "+count+", grade="+grade);
					this.discardedWorkerMap.put(workerId,workerId);
				}
			}
			contentList.add(buffer.toString());
		}
		System.out.println("Number of answers="+ answerCount);
		return contentList;
	}




	//--------------------------------------------------------------------------------

	private ArrayList<String> writeAnswerLabels_Filtered_by_WorkerICantTell(Integer numberOfICanTell){
		ArrayList<String> contentList = new ArrayList<String>();

		System.out.println("Size of list: "+ data.microtaskMap.size());

		Iterator<String> iter=data.microtaskMap.keySet().iterator();
		discardedWorkerMap = new HashMap<String, String>();//just to analyze who are the workers in terms of skill test score

		while(iter.hasNext()){
			boolean bufferHasAtLeastOneAnswer=false;
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);
			buffer.append(task.getID().toString());
			buffer.append("|");

			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);
				String workerId = answer.getWorkerId();
				Integer count = data.workerICantTellMap.get(workerId);
				if(count!=null && count.intValue()<numberOfICanTell){
					bufferHasAtLeastOneAnswer=true;
					buffer.append(answer.getOption());
					if((i+1)<answerList.size()) //only appends if it is not the last position
						buffer.append("|");
				}
				else{
					System.out.println("Worker "+ workerId+" discarded, I Cannot Tell count= "+count);
					this.discardedWorkerMap.put(workerId,workerId);
				}

			}
			//Check if buffer has at least one answer, otherwise, ignore it
			//	if(bufferHasAtLeastOneAnswer)
			contentList.add(buffer.toString());
		}
		System.out.println("contentList size="+ contentList.size());
		return contentList;
	}


	private ArrayList<String> writeAnswerDuration_Filtered_by_WorkerICantTell(Integer numberOfICanTell){
		ArrayList<String> contentList = new ArrayList<String>();
		System.out.println("Size of list: "+ data.microtaskMap.size());

		Iterator<String> iter=data.microtaskMap.keySet().iterator();

		while(iter.hasNext()){
			boolean bufferHasAtLeastOneAnswer=false;
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);
			buffer.append(task.getID().toString());
			buffer.append("|");

			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);
				String workerId = answer.getWorkerId();
				Integer count = data.workerICantTellMap.get(workerId);
				if(count!=null && count.intValue()<numberOfICanTell){
					bufferHasAtLeastOneAnswer=true;
					buffer.append(answer.getElapsedTime());
					if((i+1)<answerList.size()) //only appends if it is not the last position
						buffer.append("|");
				}
				else{


					System.out.println("Worker "+ workerId+" discarded, I Cannot Tell count= "+count);
				}
			}
			//Check if buffer has at least one answer, otherwise, ignore it
			if(bufferHasAtLeastOneAnswer)
				contentList.add(buffer.toString());
		}
		return contentList;
	}	



	//----------------------------------------------------------------------------------------------

	private ArrayList<String> writeAnswerLabels_Filtered_by_SkillTest(Integer minimumGrade){
		ArrayList<String> contentList = new ArrayList<String>();

		System.out.println("Size of list: "+ data.microtaskMap.size());

		Iterator<String> iter=data.microtaskMap.keySet().iterator();
		int answerCount=0;
		while(iter.hasNext()){
			boolean bufferHasAtLeastOneAnswer=false;
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);
			buffer.append(task.getID().toString());
			buffer.append("|");

			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);
				String workerId = answer.getWorkerId();
				Worker worker = data.workerMap.get(workerId);
				Integer grade = worker.getGrade();
				if(grade>=minimumGrade){
					bufferHasAtLeastOneAnswer=true;
					answerCount++;
					buffer.append(answer.getOption());
					if((i+1)<answerList.size()) //only appends if it is not the last position
						buffer.append("|");
				}
			}
			contentList.add(buffer.toString());
		}

		System.out.println("Number of Answsers="+ answerCount);
		return contentList;

	}

	private ArrayList<String> writeAnswerDurations_Filtered_by_SkillTestGrade(Integer minimumGrade){
		ArrayList<String> contentList = new ArrayList<String>();
		System.out.println("Size of list: "+ data.microtaskMap.size());

		Iterator<String> iter=data.microtaskMap.keySet().iterator();

		HashMap<String, String> selectedWorkers = new HashMap<String,String>();

		while(iter.hasNext()){
			boolean bufferHasAtLeastOneAnswer=false;
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);
			buffer.append(task.getID().toString());
			buffer.append("|");


			Vector<Answer> answerList = task.getAnswerList();
			for(int i=0;i<answerList.size();i++){
				Answer answer = answerList.get(i);
				String workerId = answer.getWorkerId();
				Worker worker = data.workerMap.get(workerId);
				Integer grade = worker.getGrade();
				if(grade>=minimumGrade){
					bufferHasAtLeastOneAnswer=true;
					selectedWorkers.put(workerId, workerId);

					buffer.append(answer.getElapsedTime());
					if((i+1)<answerList.size()) //only appends if it is not the last position
						buffer.append("|");

				}
				//else
				//System.out.println("Worker "+ workerId+" discarded, actual grade= "+grade);
			}
			//Check if buffer has at least one answer, otherwise, ignore it
			if(bufferHasAtLeastOneAnswer)
				contentList.add(buffer.toString());
		}

		//Print the worker selected list just for checking.
		/*System.out.println("---------------------------------------");
		System.out.println("Worker selected list just for checking");
		Iterator<String> iter2 = selectedWorkers.keySet().iterator();
		while(iter2.hasNext()){
			System.out.println(iter2.next());
		}*/

		return contentList;
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

	//----------------------------------------------------------------------------------
	private void printDiscardedWorkerGrades() {
		//initializeRejectMaps();
		Iterator<String> iter = this.discardedWorkerMap.keySet().iterator();
		//System.out.println("Total original workers: "+data.workerMap.size());
		//System.out.println("Total discarded workers "+ this.discardedWorkerMap.size());
		System.out.println("Active workers: "+this.activeWorkerMap.size());
		while(iter.hasNext()){
			String workerId= iter.next();
			Worker worker = data.workerMap.get(workerId);
			Integer score = worker.getGrade();
			String session = worker.getSessionId();
			session = session.substring(2, session.length());

			String printSession=" ";
			if(this.batch1RejectMap.containsKey(session) || this.batch2RejectMap.containsKey(session))
				printSession = session;
			//System.out.println(workerId+"|"+score+"|"+printSession);

		}
	}

	private void initializeRejectMaps(){
		//Initialize Maps
		for(String id:data.batch1Reject){
			this.batch1RejectMap.put(id,id);
		}

		for(String id:data.batch2Reject){
			this.batch2RejectMap.put(id,id);
		}
		int total = this.batch1RejectMap.size()+this.batch2RejectMap.size();
		System.out.println("Number of rejected: "+total);

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
			System.out.println(taskID+":"+validMicrotaskAnswers);
			totalAnswers = totalAnswers + validMicrotaskAnswers;
		}
		System.out.println("TotalAnswers:"+totalAnswers);
		System.out.println("----------------------------");
	}
	
	
	
	
	//----------------------------------------------------------------------------------------------------------
/**
 * 
 * @param filterCut means that must use the microtasks from scoreFilterCutMicrotaskMap which were filtered and cut.
 * @param minimumDuration
 * @param maxDuration
 * @param minimumGrade
 * @param maxGrade
 * @param numberOfICanTell
 * @param lowerNumberOfICantTell
 * @return
 */
	ArrayList<String> writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(boolean filterCut, Double minimumDuration, Double maxDuration, Integer minimumGrade, Integer maxGrade, Integer numberOfICanTell, Integer lowerNumberOfICantTell){
		ArrayList<String> contentList = new ArrayList<String>();

		//System.out.println("Size of microtask Map: "+ data.microtaskMap.size());
		Iterator<String> iter;
		if(filterCut)
			iter = this.scoreFilterCutMicrotaskMap.keySet().iterator();
		else
			iter = data.microtaskMap.keySet().iterator();
		
		discardedWorkerMap = new HashMap<String, String>();//just to analyze who are the workers in terms of skill test score
		activeWorkerMap = new HashMap<String, String>();
		int answerCount=0;
		int validAnswers=0;
		while(iter.hasNext()){
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);
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
			contentList.add(buffer.toString());
		}
		System.out.println("valid answers=" + validAnswers+ ", active workers: "+this.activeWorkerMap.size());
		//System.out.println(validAnswers);//this.activeWorkerMap.size());// ", active workers: "+);
		return contentList;
	}
	//-----------------------------------------------------------------------------------------------------------------


	private ArrayList<String> writeAnswerLabels_Filtered_by_QUESTIONTYPE_DURATION_GRADE_IDK(String questionTypeStr,Double minimumDuration, Integer minimumGrade, Integer numberOfICanTell){

		ArrayList<String> contentList = new ArrayList<String>();

		QuestionTypeFactory questionTypeFactory = new QuestionTypeFactory();
		questionTypeFactory.generateQuestionTypes();

		//System.out.println("Size of microtask Map: "+ data.microtaskMap.size());

		Iterator<String> iter=data.microtaskMap.keySet().iterator();
		discardedWorkerMap = new HashMap<String, String>();//just to analyze who are the workers in terms of skill test score
		activeWorkerMap = new HashMap<String, String>();
		
		int bugPointingAnswers=0;
		int NOT_bugPointingAnswers=0;
		
		int answerCount=0;
		int validAnswers=0;
		while(iter.hasNext()){			
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);

			StringBuffer buffer = new StringBuffer();//new line
			buffer.append(task.getID().toString());
			buffer.append("|");
			if(isValidTaskType(task.getID(), questionTypeStr,questionTypeFactory.getQuestionTypeMap())){
				int validMicrotaskAnswers=0;
				Vector<Answer> answerList = task.getAnswerList();
				for(int i=0;i<answerList.size();i++){
					Answer answer = answerList.get(i);					
					String workerId = answer.getWorkerId();
					Integer count = data.workerICantTellMap.get(workerId);
					Worker worker = data.workerMap.get(workerId);
					Integer grade = worker.getGrade();	
					Double duration = new Double(answer.getElapsedTime());
					if(count!=null && count.intValue()<numberOfICanTell && grade!=null && grade>=minimumGrade && duration>=minimumDuration){
						answerCount++;
						validMicrotaskAnswers++;
						activeWorkerMap.put(workerId, workerId);
						buffer.append(answer.getOption());
						buffer.append("|");
					}
					else{
						//System.out.println("Discarded task "+task.getID()+", worker "+ workerId+" discarded, I Cannot Tell count= "+count+", grade="+grade+", duration="+duration);
						this.discardedWorkerMap.put(workerId,workerId);
					}
				}

				int incrementOfAnswers = 0;
				
				if(validMicrotaskAnswers>10){
					incrementOfAnswers = 10;//because we would ignore anything above 10 answers for one same question.
				}
				else{
					incrementOfAnswers = validMicrotaskAnswers;
				}
				
				validAnswers = validAnswers + incrementOfAnswers;
		
				if(this.data.yesMap.containsKey(id))
					bugPointingAnswers = bugPointingAnswers + incrementOfAnswers;
				else
					NOT_bugPointingAnswers = NOT_bugPointingAnswers + incrementOfAnswers;
			}
			else{
				//System.out.println("Task "+ id +" not of type: "+questionTypeStr);
			}
			contentList.add(buffer.toString());
		}
		System.out.println("valid answers=" + validAnswers+ ", active workers: "+this.activeWorkerMap.size()+", bugPointing answers: "+bugPointingAnswers+", not bugPointing: "+NOT_bugPointingAnswers);
		//System.out.println(validAnswers);//this.activeWorkerMap.size());// ", active workers: "+);
		return contentList;
	}


	
	private boolean isValidTaskType(Integer id, String desiredQuestionTypeStr, HashMap<String,HashMap<Integer,QuestionType>> questionTypeMap){

		HashMap<Integer,QuestionType> idQuestionTypeMap = questionTypeMap.get(desiredQuestionTypeStr);
		if(idQuestionTypeMap!=null){
			if(idQuestionTypeMap.containsKey(id))
				return true;
			else 
				return false;
		}
		else 
			return false;
	}
	
	public void printActiveWorkerMap(){
		System.out.println("Active WorkerMap");
		Iterator<String> iter = this.activeWorkerMap.keySet().iterator();
		while(iter.hasNext()){
			System.out.println(iter.next());
		}
	}
	
	
		
	//-----------------------------------------------------------------------------------
	public static void main(String[] args){

		CSVData csvData = initializeLogs();

		String path =  currentPath;
		path = path +"\\DataAnalysis\\BaseDataInTime\\cutFilter\\";

		/*csvData.printToFile(path+"all.txt", csvData.writeMicrotaskAnswers_ZeroOnes());

		csvData.printToFile(path+"allAnswer.txt", csvData.writeMicrotaskAnswers_Labels());

		csvData.printToFile(path+"allAnswersInTime.txt", csvData.writeAnswersInTime_Labels());

		csvData.printToFile(path+"allAnswersInTime_Duration.txt", csvData.writeAnswersInTime_Duration());
		 */
		//csvData.printToFile(path+"allAnswerDurations-SkillTest-Grade4.txt", csvData.writeAnswerDurations_Filtered_by_SkillTestGrade(4));
		//csvData.printToFile(path+"x2.txt", csvData.writeAnswerLabels_Filtered_by_SkillTest(3));

		//csvData.printToFile(path+"allAnswerDurations-ICantTell-2.txt", csvData.writeAnswerDuration_Filtered_by_WorkerICantTell(2));
		//csvData.printToFile(path+"allAnswerLabels-ICantTell-2.txt", csvData.writeAnswerLabels_Filtered_by_WorkerICantTell(2));

		//csvData.printToFile(path+"allAnswerLabels-SkillTest-IDK10_Grade3.txt", csvData.writeAnswerLabels_Filtered_by_Combined_WorkerICantTell_WorkerGrade(10,3));

		String questionTypeStr = QuestionType.METHOD_PARAMETERS 	; //CONDITIONAL_BODY CONDITIONAL_STATEMENT; LOOP_BODY; LOOP_STATEMENT;METHOD_BODY;METHOD_DECLARATION;METHOD_INVOCATION;METHOD_PARAMETERS;

		Double maxDuration = new Double(Double.MAX_VALUE);
		Integer[] durationList = {0};//,15,20,30,45,60,120}; //Minimal duration to be considered
		Integer[] scoreList = {2};//,4};  //Minimal Score to be considered		
		Integer[] idkList = {11};// 2,4,6,8,10}; //I Can't Tell answer count that would eliminate workers
		Integer lowerCut_idk = -1;  //Worker that has an equal amount below will be cut out of the set.
		Integer maxScore=5; //Worker has to have grade below that.
		int i=0;
		
		csvData.filterCutMicrotasks(0,1);
		
		while(i<durationList.length){
			int j=0;
			String durationStr = durationList[i].toString();
			Double duration = new Double(durationList[i].doubleValue()*1000);
			while(j<scoreList.length){
				String scoreStr = scoreList[j].toString();
				int k=0;
				while(k<idkList.length){
					String idkStr = idkList[k].toString();
					String fileName = "noCut_noFilter_";///+durationStr+"s_test-"+scoreStr+"_"+maxScore+"_idk-"+idkStr+"_"+lowerCut_idk.toString()+".txt";
					//System.out.print("fileName:"+fileName+"> ");
					csvData.printToFile(path+fileName, csvData.writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(true, duration, maxDuration, scoreList[j],maxScore,idkList[k],lowerCut_idk));
					//csvData.printToFile(path+fileName, csvData.writeAnswerLabels_Filtered_by_QUESTIONTYPE_DURATION_GRADE_IDK(questionTypeStr, duration, scoreList[j],idkList[k]));
					//csvData.writeAnswerLabels_Filtered_by_QUESTIONTYPE_DURATION_GRADE_IDK(questionTypeStr, duration, scoreList[j],idkList[k]);
					k++;
				}
				j++;
			}
			i++;
		}
		
		
		System.out.println("files written, look at: "+path);
		//csvData.printActiveWorkerMap();
	}





}

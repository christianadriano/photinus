package edu.uci.ics.sdcl.firefly.report.voting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.QuestionType;
import edu.uci.ics.sdcl.firefly.Worker;

public class MajorityVoting {

	LogData data;

	HashMap<String,ArrayList<Answer>> filteredMicrotaskMap; 
	HashMap<String, String> discardedWorkerMap;
	HashMap<String, String> activeWorkerMap;
	
	int validAnswers;
	int activeWorkers;
	
	HashMap<String, Integer> positiveAnswerMap; //Holds the difference between (Yes+Prob Yes) - (No + Prob Not)
	HashMap<String,Result> TP_FP_FN_Map;
	ArrayList<String> precisionRecallList;

	static String samsungPath = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String dellPath = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	static String currentPath = dellPath;

	public HashMap<String, Microtask> scoreFilterCutMicrotaskMap; //Remove all microtasks from workers with Score below and cut to the first N answers for each microtask
	
	public MajorityVoting(LogData data){
		this.data = data;
		this.precisionRecallList = new ArrayList<String>();
		initializeDataStrutures();
	}
	
	private static MajorityVoting initializeLogs(){
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

		MajorityVoting majorityData = new MajorityVoting(data);
		return majorityData;
	}
	
	public void initializeDataStrutures(){
		this.filteredMicrotaskMap = new 	HashMap<String,ArrayList<Answer>>();
		this.positiveAnswerMap = new HashMap<String, Integer>();
		this.TP_FP_FN_Map = new HashMap<String,Result>();
	}
	
	/**
	 * 
	 * @param filterCut  means that must use the microtasks from scoreFilterCutMicrotaskMap which were filtered and cut.
	 * @param minimumDuration
	 * @param maxDuration
	 * @param minimumGrade
	 * @param maxGrade
	 * @param numberOfICanTell
	 * @param lowerNumberOfICantTell
	 */
	private void writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(boolean filterCut, Double minimumDuration, Double maxDuration, Integer minimumGrade, Integer maxGrade, Integer numberOfICanTell, Integer lowerNumberOfICantTell){
		ArrayList<String> contentList = new ArrayList<String>();

		//System.out.println("Size of microtask Map: "+ data.microtaskMap.size());
		
		Iterator<String> iter;
		if(filterCut)
			iter = this.scoreFilterCutMicrotaskMap.keySet().iterator();
		else
			iter = this.filteredMicrotaskMap.keySet().iterator();

		discardedWorkerMap = new HashMap<String, String>();//just to analyze who are the workers in terms of skill test score
		activeWorkerMap = new HashMap<String, String>();
		int answerCount=0;
		this.validAnswers=0;
		while(iter.hasNext()){
			StringBuffer buffer = new StringBuffer();//new line
			String id = iter.next();
			Microtask task = data.microtaskMap.get(id);
			ArrayList<Answer> filteredAnswers = new ArrayList<Answer>();
			this.filteredMicrotaskMap.put(task.getID().toString(),null);

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
					filteredAnswers.add(answer);
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
			this.filteredMicrotaskMap.put(task.getID().toString(),filteredAnswers);
		}
		this.activeWorkers = this.activeWorkerMap.size();
		//System.out.println("valid answers=" + validAnswers+ ", active workers: "+this.activeWorkerMap.size());
		//System.out.println(validAnswers);//this.activeWorkerMap.size());// ", active workers: "+);
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
		
	
	
	/** 
	 * Computes the sum of all Yes and Probably Yes per concrete question
	 */
	public void computePositiveAnswers(){ 
		
	 Iterator<String> iter = this.filteredMicrotaskMap.keySet().iterator();

		while(iter.hasNext()){
			String microtaskId = iter.next();
			ArrayList<Answer> answerList = this.filteredMicrotaskMap.get(microtaskId);
			int yesCount=0;
			int noCount=0;
				
			for(Answer answer: answerList ){
				String option = answer.getOption();
				if(option.matches(Answer.YES)){
					yesCount++;
				}
				else
					if(option.matches(Answer.NO)){
						noCount++;
					}
			}
			
			Integer balance = yesCount - noCount;
			this.positiveAnswerMap.put(microtaskId, balance);
			
				
		}
		
	}
	
	private void computeTPFPFN_Map(){
		Iterator<String> iter = this.positiveAnswerMap.keySet().iterator();
		while(iter.hasNext()){
			String microtaskId = iter.next();
			Integer balance = this.positiveAnswerMap.get(microtaskId);
			Result result= new Result();
			if(this.data.yesMap.containsKey(microtaskId)){//Bug Pointing
				if(balance>0)
					result.truePositives="1";
				else
					if(balance<0)
						result.falseNegatives="1";
			}
			else{ //Not Bug Pointing. If balance is zero, all data is zero.
				if(balance>0)
					result.falsePositives="1";
				else
					if(balance<0)
						result.trueNegatives="1";		
			}
			this.TP_FP_FN_Map.put(microtaskId, result);
		}
	}
	
	private void computePrecisionRecall(Double minimumDuration,  Integer minimumGrade, Integer maxNumberOfICanTell){
		
		Double totalTP = 0.0;
		Double totalFP = 0.0;
		Double totalFN = 0.0;
		
		Iterator<String> iter = this.TP_FP_FN_Map.keySet().iterator();
		while(iter.hasNext()){
			String microtaskId = iter.next();
			Result result = this.TP_FP_FN_Map.get(microtaskId);
			totalTP = totalTP +  new Double(result.truePositives);
			totalFP = totalFP + new Double(result.falsePositives);
			totalFN = totalFN + new Double(result.falseNegatives);
		}
		
		
		Double precision = totalTP / (totalTP + totalFP);//*100.0;
		Double recall = totalTP / (totalTP + totalFN);//*100.0;
		
		String duration;
		String score;
		String ICT;
		
		if(minimumDuration!=null){
			minimumDuration = minimumDuration/1000.0;
			duration = minimumDuration.toString();
		}
		else
			duration = "disabled";
		
		if(minimumGrade!=null)
			score = minimumGrade.toString();
		else
			score = "disabled";

		if(maxNumberOfICanTell!=null)
			ICT = maxNumberOfICanTell.toString();
		else
			ICT = "disabled";

		
		String content =  duration+"|"+score+"|"+ICT+"|"+this.validAnswers+"|"+this.activeWorkers+"|"+precision.toString()+"|"+recall.toString();
		this.precisionRecallList.add(content);
		
	}
	
	private void printPrecisionRecallList(){
		for(String content: this.precisionRecallList){
			content = content.replace('.', ',');
			System.out.println(content);//replaceAll(".", ",")); //because my Excel is Portuguese.
		}
	}
	
	//-----------------------------------------------------------------------------------
		public static void main(String[] args){

			MajorityVoting majorityData = initializeLogs();

			String path =  currentPath;
			path = path +"\\DataAnalysis\\BaseDataInTime\\combined12\\";

			Double maxDuration = new Double(Double.MAX_VALUE);
			Integer[] durationList = {0};//10,15,20,30,45,60,120}; //Minimal duration to be considered
			Integer[] scoreList = {2};  //Minimal Score to be considered		
			Integer[] idkList = {11};//2,4,6,8,10}; //I Can't Tell answer count that would eliminate workers
			Integer lowerCut_idk = -1;  //Worker that has an equal amount below will be cut out of the set.
			Integer maxScore=5; //Worker has to have grade below that.
			int i=0;
			majorityData.filterCutMicrotasks(10, 1);
			while(i<durationList.length){
				int j=0;
				String durationStr = durationList[i].toString();
				Double duration = new Double(durationList[i].doubleValue()*1000);
				while(j<scoreList.length){
					String scoreStr = scoreList[j].toString();
					int k=0;
					while(k<idkList.length){
						String idkStr = idkList[k].toString();
						String fileName = durationStr+"s_test-"+scoreStr+"_"+maxScore+"_idk-"+idkStr+"_"+lowerCut_idk.toString()+".txt";
						//System.out.print("fileName:"+fileName+"> ");
						majorityData.initializeDataStrutures();
						majorityData.writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(true,duration, maxDuration, scoreList[j],maxScore,idkList[k],lowerCut_idk);
						majorityData.computePositiveAnswers();
						majorityData.computeTPFPFN_Map();
						majorityData.computePrecisionRecall(duration, scoreList[j], idkList[k]);
						
						k++;
					}
					j++;
				}
				i++;
			}
			
			majorityData.printPrecisionRecallList();
			//System.out.println("files written, look at: "+path);
			//csvData.printActiveWorkerMap();
		}

	
	
}

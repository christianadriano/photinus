package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import sun.security.util.PendingException;
import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.predictive.Tuple;
import edu.uci.ics.sdcl.firefly.util.MicrotaskMapUtil;

/**
 * Responsible for filter a collection of Sessions.
 * 
 * @author igMoreira
 *
 */
public class Filter  {

	private int[] explanationSize = new int[2];
	private int[] confidence = new int[2];
	private int[] difficulty = new int[2];
	private double[] answerDuration = new double[2];
	private int[] workerScore = new int[2];
	private double[] sessionDuration = new double[2];
	private double[] workerIDKPercentage = new double[2];
	private double[] yearsOfExperience = new double[2];

	private double[] FirstAnswerDuration = new double[2];
	private double[] SecondThirdAnswerDuration = new double[2];
	private Date[] stardEndDates = new Date[2];
	private int maxAnswers = -1;





	/** map of confidence, difficulty pairs */
	private HashMap<String, Tuple> confidenceDifficultyPairMap = new HashMap<String, Tuple>();

	/** map of professions to be accepted */
	private HashMap<String, String> workerProfessionMap = new HashMap<String, String> (); //Hobbyist, Professional Programmer, Undergrad, Graduate, Others

	/** this is a list of scores that must be excluded */
	private HashMap<String, Integer> workerScoreExclusionMap = new HashMap<String,Integer>();

	/** list of questionIDs to be ignored */
	private TreeMap<String, String> questionToExcludeMap = new TreeMap<String,String>();

	private  Map<String, Integer> sessionDurationMap = new HashMap<String, Integer>();
	private  Map<String, Double> workerIDKMap = new HashMap<String, Double>();

	public int[] getExplanationSize() {
		return explanationSize;
	}

	public int[] getConfidence() {
		return confidence;
	}

	public int[] getDifficulty() {
		return difficulty;
	}

	public double[] getAnswerDuration() {
		return answerDuration;
	}

	public int[] getWorkerScore() {
		return workerScore;
	}

	public double[] getSessionDuration() {
		return sessionDuration;
	}

	public HashMap<String, String>  getWorkerProfessionMap(){
		return workerProfessionMap;
	}

	public Filter() {
		Arrays.fill(explanationSize, -1);
		Arrays.fill(confidence, -1);
		Arrays.fill(difficulty, -1);
		Arrays.fill(answerDuration, -1);
		Arrays.fill(workerScore, -1);
		Arrays.fill(sessionDuration, -1);
		Arrays.fill(workerIDKPercentage, -1);
		Arrays.fill(yearsOfExperience,-1.0);
		Arrays.fill(FirstAnswerDuration, -1.0);
		Arrays.fill(SecondThirdAnswerDuration, -1.0);
		Arrays.fill(stardEndDates, null);
	}

	public void setExplanationSizeCriteria(int minimum, int maximum)
	{
		if((minimum > maximum) && (maximum != -1))
		{
			System.out.println("The minimum value of the filter cannot be greater than the maximum");
		}
		else{
			this.explanationSize[0] = minimum;
			this.explanationSize[1] = maximum;
		}
	}

	public void setConfidenceCriteria(int minimum, int maximum)
	{
		if((minimum > maximum) && (maximum != -1))
		{
			System.out.println("The minimum value of the filter cannot be greater than the maximum");
		}
		else{
			this.confidence[0] = minimum;
			this.confidence[1] = maximum;
		}
	}

	public void setDifficultyCriteria(int minimum, int maximum)
	{
		if((minimum > maximum) && (maximum != -1))
		{
			System.out.println("The minimum value of the filter cannot be greater than the maximum");
		}
		else{
			this.difficulty[0] = minimum;
			this.difficulty[1] = maximum;
		}
	}

	public void setAnswerDurationCriteria(double minimum, double maximum)
	{
		if((minimum > maximum) && (maximum != -1))
		{
			System.out.println("The minimum value of the filter cannot be greater than the maximum");
		}
		else{
			this.answerDuration[0] = minimum;
			this.answerDuration[1] = maximum;
		}
	}

	public void setSessionDurationCriteria(double minimum, double maximum)
	{
		if((minimum > maximum) && (maximum != -1))
		{
			System.out.println("The minimum value of the filter cannot be greater than the maximum");
		}
		else{
			this.sessionDuration[0] = minimum;
			this.sessionDuration[1] = maximum;
		}
	}

	public void setWorkerScoreCriteria(int minimum, int maximum)
	{
		if((minimum > maximum) && (maximum != -1))
		{
			System.out.println("The minimum value of the filter cannot be greater than the maximum");
		}
		else{
			this.workerScore[0] 	= minimum;
			this.workerScore[1] = maximum;
		}
	}

	public void setIDKPercentageCriteria(double minimum, double maximum)
	{
		if((minimum > maximum) && (maximum != -1))
		{
			System.out.println("The minimum value of the filter cannot be greater than the maximum");
		}
		else{
			this.workerIDKPercentage[0] = minimum;
			this.workerIDKPercentage[1] = maximum;
		}
	}

	public void setWorkerScoreExclusionList(int [] exclusionList)
	{
		for(int score : exclusionList){
			this.workerScoreExclusionMap.put(new Integer(score).toString(), score);
		}
	}

	public void setWorkerProfessionMap(String[] list) 
	{
		for(String profession : list){			
			this.workerProfessionMap.put(profession, profession);
		}
	}

	public void setYearsOfExperience(double minimum, double maximum)
	{
		if((minimum > maximum) && (maximum != -1.0))
		{
			System.out.println("The minimum value of the filter cannot be greater than the maximum");
		}
		else{
			this.yearsOfExperience[0] 	= minimum;
			this.yearsOfExperience[1] = maximum;
		}
	}

	private Map<String, Microtask>  excludeQuestions(Map<String, Microtask>  aux){
		for(String questionID : this.questionToExcludeMap.keySet()){
			if(aux.containsKey(questionID)){
				aux.remove(questionID);
				//System.out.println("key removed: "+questionID);
			}
		}
		return aux;
	}
	
	
	private Map<String, Microtask>  excludeAnswers(Map<String, Microtask>  aux){
		
		HashMap<String, Microtask> newMap = new HashMap<String, Microtask>();
		
		for(Microtask task : aux.values()){
			Vector<Answer> answerList = task.getAnswerList();
			Vector<Answer> newList = new Vector<Answer>();
			for(int i=0;i<this.maxAnswers;i++){
				newList.add(answerList.get(i));
				task.setAnswerList(newList);
				newMap.put(task.getID().toString(), task);
			}
		}
		return newMap;
	}

	/**
	 * Applies the filter on the desired content.
	 * @param content Desired content to be filtered
	 * @return The filtered content
	 */
	public Map<String, Microtask> apply(HashMap<String, Microtask> microtaskMap)
	{
		Map<String, Microtask> content = MicrotaskMapUtil.cloneMap(microtaskMap);

		//Remove questions that should be excluded
		if(this.questionToExcludeMap !=null && this.questionToExcludeMap.size()>0)
			content = excludeQuestions(content);

		Map<String, Microtask> aux = new LinkedHashMap<String, Microtask>();
		aux.putAll(content);
		List<Integer> removeIndex = new ArrayList<Integer>();	
		FileConsentDTO workerDTO = new FileConsentDTO();
		HashMap<String,Worker> workerMap = workerDTO.getWorkers();

		//Remove questions that should be excluded
		if(this.questionToExcludeMap !=null && this.questionToExcludeMap.size()>0)
			aux = excludeQuestions(aux);

		//Remove answers that should be excluded
		if(this.maxAnswers !=-1 )
			aux = excludeAnswers(aux);
		
		int removedCount = 0;
		
		for (String questionID : aux.keySet()) {

			Vector<Answer> answerList = aux.get(questionID).getAnswerList();
			
			for (int i = 0; i < answerList.size(); i++) {				
				Answer answer = answerList.get(i);
				String workerID = answer.getWorkerId();
				Worker worker = workerMap.get(workerID);
				if(worker==null){ //Does not accept answers from workers who are not in the ConsentLog.
					System.out.print("Worker not in ConsentLog: "+ workerID);				
				}else{
					if((explanationSize[0] != -1) || (explanationSize[1] != -1))
					{
						if( (explanationSize[0] != -1) && (answer.getExplanation().length() < explanationSize[0]))
						{
							removeIndex.add(i);
							continue;
						}
						if( (explanationSize[1] != -1) && (answer.getExplanation().length() > explanationSize[1]))
						{
							removeIndex.add(i);
							continue;
						}
					}
					if((confidence[0] != -1) || (confidence[1] != -1))
					{
						if( (confidence[0] != -1) && (answer.getConfidenceOption() < confidence[0]))
						{
							removeIndex.add(i);
							continue;
						}
						if( (confidence[1] != -1) && (answer.getConfidenceOption() > confidence[1]))
						{
							removeIndex.add(i);
							continue;
						}
					}
					if((difficulty[0] != -1) || (difficulty[1] != -1))
					{
						if( (difficulty[0] != -1) && (answer.getDifficulty() < difficulty[0]))
						{
							removeIndex.add(i);
							continue;
						}
						if( (difficulty[1] != -1) && (answer.getDifficulty() > difficulty[1]))
						{
							removeIndex.add(i);
							continue;
						}
					}
					if((FirstAnswerDuration[0] != -1) || (FirstAnswerDuration[1] != -1))
					{
						if(answer.getOrderInWorkerSession()==1){
							if( (FirstAnswerDuration[0] != -1) && ((Double.valueOf(answer.getElapsedTime())/1000) < FirstAnswerDuration[0]))
							{
								///System.out.println("removing first, duration:"+FirstAnswerDuration[0]+":value: "+Double.valueOf(answer.getElapsedTime())/1000);
								removeIndex.add(i);
								continue;
							}
							if( (FirstAnswerDuration[1] != -1) && ((Double.valueOf(answer.getElapsedTime())/1000) > FirstAnswerDuration[1]))
							{
								removeIndex.add(i);
								continue;
							}
						}
					}
					if((SecondThirdAnswerDuration[0] != -1) || (SecondThirdAnswerDuration[1] != -1))
					{
						if(answer.getOrderInWorkerSession()==2 || answer.getOrderInWorkerSession()==3){
							if( (SecondThirdAnswerDuration[0] != -1) && ((Double.valueOf(answer.getElapsedTime())/1000) < SecondThirdAnswerDuration[0]))
							{
								//System.out.println("removing second/third, duration:"+SecondThirdAnswerDuration[0]);
								removeIndex.add(i);
								continue;
							}
							if( (SecondThirdAnswerDuration[1] != -1) && ((Double.valueOf(answer.getElapsedTime())/1000) > SecondThirdAnswerDuration[1]))
							{
								removeIndex.add(i);
								continue;
							}
						}
					}
					if((workerScore[0] != -1) || (workerScore[1] != -1) )
					{
						if(worker!=null && (workerScore[0] != -1) && (worker.getGrade() < workerScore[0]))
						{
							//if(worker.getGrade()==4 || worker.getGrade()==3 )
								//System.out.println("removing workerScore:"+worker.getGrade());
							removeIndex.add(i);
							continue;
						}
						if( worker!=null && (workerScore[1] != -1) && (worker.getGrade() > workerScore[1]))
						{
							//if(worker.getGrade()==4)
								//System.out.println("removing workerScore:"+worker.getGrade());
							removeIndex.add(i);
							continue;
						}
						if(worker!=null && this.workerScoreExclusionMap.containsKey(worker.getGrade().toString()) )
						{
							removeIndex.add(i);
						}
					}
					if((sessionDuration[0] != -1) || (sessionDuration[1] != -1) )
					{
						calculateSessionsDuration();
						if( (sessionDuration[0] != -1) && (sessionDurationMap.get(questionID+":"+answer.getWorkerId()) < sessionDuration[0]))
						{
							removeIndex.add(i);
							continue;
						}
						if( (sessionDuration[1] != -1) && (sessionDurationMap.get(questionID+":"+answer.getWorkerId()) > sessionDuration[1]))
						{
							removeIndex.add(i);
							continue;
						}
					}
					if((workerIDKPercentage[0] != -1) || (workerIDKPercentage[1] != -1))
					{
						calculateWorkersIDK();
						if( (workerIDKPercentage[0] != -1) && (workerIDKMap.get(answer.getWorkerId()) < workerIDKPercentage[0]))
						{
							//System.out.println("min IDK: "+ workerIDKPercentage[0] +" Removig worker: "+answer.getWorkerId());
							removeIndex.add(i);
							continue;
						}
						if( (workerIDKPercentage[1] != -1) && (workerIDKMap.get(answer.getWorkerId()) > workerIDKPercentage[1]))
						{
							//System.out.println("max IDK: "+ workerIDKPercentage[1] +" Removig worker: "+answer.getWorkerId());
							removeIndex.add(i);
							continue;
						}
					}
					if(workerProfessionMap.size()>0)
					{
						String workerProfession = worker.getSurveyAnswer("Experience");
						if(workerProfession.contains("Other"))
							workerProfession = "Other";
						if( (workerProfessionMap.containsKey(workerProfession)))
						{
							//System.out.println("profession: "+ workerProfession +" Removig worker: "+answer.getWorkerId());
							removeIndex.add(i);
							continue;
						}
						if( (workerProfessionMap.containsKey(workerProfession)))
						{
							//System.out.println("profession: "+ workerProfession +" Removig worker: "+answer.getWorkerId());
							removeIndex.add(i);
							continue;
						}
					}
					//System.out.println(yearsOfExperience[0] + ": "+yearsOfExperience[1]);
					if((yearsOfExperience[0] != -1.0) || (yearsOfExperience[1] != -1.0) )
					{

						Double workerYearsOfExperience = new Double(worker.getSurveyAnswer("YearsProgramming"));
						//	System.out.println(workerYearsOfExperience);

						if(worker!=null && (yearsOfExperience[0] != -1) && ( workerYearsOfExperience.doubleValue()< yearsOfExperience[0]))
						{
							removeIndex.add(i);
							continue;
						}
						if( worker!=null && (yearsOfExperience[1] != -1) && (workerYearsOfExperience.doubleValue() > yearsOfExperience[1]))
						{
							removeIndex.add(i);
							continue;
						}
					}
					if(confidenceDifficultyPairMap.size()>0){

						
						for(Tuple tuple: confidenceDifficultyPairMap.values()){
							//System.out.println(tuple.toString()+": answer:"+answer.getConfidenceOption()+","+answer.getDifficulty());

							if(( (tuple.confidence != -1) && (answer.getConfidenceOption() == tuple.confidence)) &&
									((tuple.difficulty != -1) && (answer.getDifficulty() == tuple.difficulty)))
							{
								//System.out.print("removed ["+answer.getConfidenceOption()+","+answer.getDifficulty()+"]");
								//removedCount++;
								removeIndex.add(i);

								continue;
							}
						}
					
					}

					if((stardEndDates[0] !=null) || (stardEndDates[0] !=null) ){

						Date answerDate = answer.getTimeStampDate();
						if(answerDate.compareTo(stardEndDates[0])<0){
							System.out.print("removed:" + answerDate);
							removeIndex.add(i);
						}

						if(answerDate.compareTo(stardEndDates[1])>0){
							System.out.print("removed:" + answerDate);
							removeIndex.add(i);
						}
					}
					
				}
			}//for


			//System.out.println("Filter, removeIndex size: "+removeIndex.size());

			Collections.reverse(removeIndex);
			for (Integer index : removeIndex) {
				Microtask question = content.get(questionID);
				if(question.getAnswerList().size()>index) //Might happen to try to remove twice due to a pair filter (e.g., confidence/difficulty)
					question.getAnswerList().removeElementAt(index);
			}
			removeIndex.clear();

		}//for		
		
		//System.out.println();
		//System.out.println("removed: "+ removedCount+" answers");
		
		return content;
	}

	public boolean validMicrotask(Microtask microtask){

		HashMap<String, Microtask> map = new HashMap<String,Microtask>();
		map.put(microtask.getID().toString(), microtask);
		HashMap<String,Microtask> filteredMap = (HashMap<String, Microtask>) this.apply(map);
		if(filteredMap.size()==0)
			return false;
		else 
			return true;
	}


	private void calculateSessionsDuration()
	{
		if(sessionDurationMap.size() == 0)
		{
			FileSessionDTO dto = new FileSessionDTO();
			Map<String, WorkerSession> sessions = dto.getSessions();
			for (WorkerSession session : sessions.values()) {
				int total = 0;
				for (Microtask question : session.getMicrotaskList()) {
					Answer answer = question.getAnswerList().firstElement();
					String firstNumber = answer.getElapsedTime().replaceFirst(".*?(\\d+).*", "$1");
					total += (Integer.valueOf( firstNumber ) / 1000 );
				}
				for (Microtask question : session.getMicrotaskList()) {
					Answer answer = question.getAnswerList().firstElement();
					sessionDurationMap.put((question.getID().toString() + ":" + answer.getWorkerId()), total );
				}
			}
		}
	}

	private void calculateWorkersIDK()
	{
		if(workerIDKMap.size() == 0)
		{
			FileSessionDTO sessionDTO = new FileSessionDTO();
			Map<String, Microtask> questions = sessionDTO.getMicrotasks();
			Map<String, Integer[]> aux = new HashMap<String, Integer[]>();
			for (Microtask question : questions.values()) {
				for (Answer answer : question.getAnswerList()) {
					Integer[] values = null;
					if(aux.containsKey(answer.getWorkerId()))
						values = aux.get(answer.getWorkerId());
					else
					{
						values = new Integer[2];
						Arrays.fill(values, 0);
					}
					values[0] += (answer.getOption().equals(Answer.I_DONT_KNOW)) ? 1 : 0;
					values[1]++;
					aux.put(answer.getWorkerId(), values);
				}
			}
			for (String workerID : aux.keySet()) {
				Integer[] values = aux.get(workerID);
				Double percentage = 0.0;
				if(values[1] != 0){
					percentage = (((double)(100 * values[0])) / values[1]);
					//System.out.println("IDK percentage: "+ percentage );
				}
				workerIDKMap.put(workerID, percentage);
			}
		}
	}

	public void setFirstAnswerDurationCriteria(double minD, double maxD) {
		{
			if((minD > maxD) && (maxD != -1.0))
			{
				System.out.println("The minimum value of the filter cannot be greater than the maximum");
			}
			else{
				this.FirstAnswerDuration[0] = minD;
				this.FirstAnswerDuration[1] = maxD;
			}
		}
	}

	public void setSecondThirdAnswerDurationCriteria(double minD,
			double maxD) {
		{
			if((minD > maxD) && (maxD != -1.0))
			{
				System.out.println("The minimum value of the filter cannot be greater than the maximum");
			}
			else{
				this.SecondThirdAnswerDuration[0] = minD;
				this.SecondThirdAnswerDuration[1] = maxD;
			}
		}

	}

	public void setConfidenceDifficultyPairList(
			HashMap<String, Tuple> confidenceDifficultyPairMap) {
		this.confidenceDifficultyPairMap = confidenceDifficultyPairMap;
	}

	public void setQuestionToExcludeMap(TreeMap<String, String> questionsToExcludeMap){
		this.questionToExcludeMap = questionsToExcludeMap;
	}

	public void setStartEndDates(Date[] startEndDates) {
		this.stardEndDates = startEndDates;
	}

	public int getMaxAnswers() {
		return maxAnswers;
	}

	public void setMaxAnswers(int maxAnswers) {
		this.maxAnswers = maxAnswers;
	}
	
	public double getMinYoE(){
		return this.yearsOfExperience[0];
	}

	public static HashMap<String,Microtask> selectMicrotaskFromFileName(HashMap<String, Microtask> originalMicrotaskMap, 
																	String fileName){
	
		HashMap<String, Microtask> selectedMap =  new HashMap<String, Microtask>();
		
		Iterator<String> iter = originalMicrotaskMap.keySet().iterator();
		while(iter.hasNext()){
			String id = iter.next();
			Microtask task = originalMicrotaskMap.get(id);
			if(task.getFileName().matches(fileName)){
				selectedMap.put(id, task);
			}
		}
		
		return selectedMap;
	}
	
	
}

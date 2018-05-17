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
import edu.uci.ics.sdcl.firefly.QuestionFactory;
import edu.uci.ics.sdcl.firefly.QuestionType;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.QuestionTypeFactory;
import edu.uci.ics.sdcl.firefly.report.analysis.*;

/**
 * Select all explanations that are not empty and print them.
 * 
 * @author Christian Adriano
 *
 */
public class ExplanationReport {

	private LogData data;
	protected AnalysisPath analysisPath;
	protected HashMap<String, String> filteredWorkerMap = new HashMap<String, String>();
	protected QuestionTypeFactory questionTypeFactory;


	public ExplanationReport(){
		this.analysisPath = AnalysisPath.getInstance(); 
		this.initializeLogs();
		this.initializeFilteredWorkerMap();
		this.questionTypeFactory = new QuestionTypeFactory();
		this.questionTypeFactory.generateQuestionTypes();
	}

	private void initializeFilteredWorkerMap(){
		for(String name: filteredWorkers){
			this.filteredWorkerMap.put(name, name);
		}
	}



	/** 92 workers who produced the best data point of 57% precision, 71% recall from cutting at 17 answers/questions */
	private String[] filteredWorkers = {"2-490","1-1280","1-540","1-1051","1-2183","1-544","1-700","2-1631","1-1758",
			"1-1755","2-1231","2-1279","1-743","2-302","1-780","1-692","1-1819","2-604","1-742","2-445","1-748","2-1543",
			"1-434","1-2096","1-1644","1-1312","1-1814","2-400","2-1024","1-338","2-1168","2-204","1-656","1-1567","1-1159",
			"1-2155","1-132","1-1612","1-705","2-1268","1-232","1-1474","2-1316","1-1025","1-235","2-2266","1-1026","2-298",
			"1-1850","1-91","1-2160","1-349","2-1517","1-389","2-567","2-1709","2-38","1-579","2-127","2-2231","1-931","1-764",
			"1-763","1-451","1-1589","2-1995","1-297","2-1772","2-702","2-701","2-1567",
			"1-399","1-2127","2-1737","1-635","1-2019","1-87","1-1725","1-85","2-220","2-1532","1-490","1-1634","1-259","1-1140",
			"1-1208","1-1101","1-1293","1-1699","1-110","1-1967","1-1300"};

	private void initializeLogs(){
		LogData data = new LogData(false, 0);
		String path = analysisPath.rawDataLogPath;
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

		this.data = data;
	}

	public class ExplanationTuple{
		public String explanation;
		public Integer explanationSize;
		public String answerOption;
		public String duration;
		public String taskID;
		public String questionType;
		public String bugPointing;

		public ExplanationTuple(String explanation, String answerOption, String duration, String taskID, String questionType, String bugPointing ){
			this.explanation = explanation;
			this.explanationSize = explanation.length();
			this.answerOption = answerOption;
			this.duration = duration;
			this.taskID = taskID;
			this.questionType = questionType;
			this.bugPointing = bugPointing;
		}

	}

	/**
	 * @param workerList worker id's that have to be considered
	 * @param questionType the question for which we need
	 * @return
	 */
	public ArrayList<ExplanationTuple> selectExplanations(HashMap<String, String> workerMap, String questionType){

		Iterator<String> iter = data.microtaskMap.keySet().iterator();

		ArrayList<ExplanationTuple> contentList = new ArrayList<ExplanationTuple>();

		while(iter.hasNext()){
			String id = iter.next();
			if(this.questionTypeFactory.isQuestionIdOfType(id, questionType)){//ignore questions from different types
				Microtask task = data.microtaskMap.get(id);
				String bugPointing="no";
				if(this.questionTypeFactory.bugPointingMap.containsKey(new Integer(id)))
					bugPointing="yes";
				Vector<Answer> answerList = task.getAnswerList();
				for(int i=0;i<answerList.size();i++){
					Answer answer = answerList.get(i);		
					String explanation = answer.getExplanation();
					if(explanation!=null && explanation.trim().length()>0){

						String workerId = answer.getWorkerId();
						if(workerMap!=null){
							if(workerMap.containsKey(workerId)){
								ExplanationTuple tuple = new ExplanationTuple(explanation,answer.getOption(),answer.getElapsedTime(),id, questionType, bugPointing);
								contentList.add(tuple);
							}//workerId not in the desired map
						}
						else{//no filter by worker was requested.
							ExplanationTuple tuple = new ExplanationTuple(explanation,answer.getOption(),answer.getElapsedTime(),id, questionType, bugPointing);
							contentList.add(tuple);							
						}
					}//empty explanation
				}
			}//question from undesired type
		}
		return contentList;
	}

	//------------------------------------------------------------------------------------------------

	private void printToFile(String fileNamePath, ArrayList<ExplanationTuple> contentList){
		try{
			File file = new File(fileNamePath);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(ExplanationTuple tuple: contentList){
				writer.write(tuple.taskID +"|" + tuple.answerOption +"|" +tuple.bugPointing+"|" +tuple.duration +"|"+tuple.explanation+"|"+tuple.explanationSize);
				writer.newLine();
			}
			writer.close();
		}
		catch(Exception e){
			System.err.println(e.toString());
		}
	}



	//-----------------------------------------------------------------------------------
	public static void main(String[] args){

		ExplanationReport reportData = new ExplanationReport();
		reportData.initializeLogs();

		String path =  reportData.analysisPath.currentPath;
		path = path +"\\1.DataAnalysis(CutFirst)\\data\\explanation\\";

		String[] questionTypeList = {QuestionType.METHOD_INVOCATION}; //{QuestionType.METHOD_INVOCATION, QuestionType.METHOD_PARAMETERS, QuestionType.METHOD_DECLARATION,
				//QuestionType.METHOD_BODY, QuestionType.LOOP_BODY, QuestionType.CONDITIONAL_BODY,QuestionType.LOOP_STATEMENT,QuestionType.CONDITIONAL_STATEMENT};

		//reportData.filteredWorkerMap

		int i=0;
		while(i<questionTypeList.length){
			String fileName = questionTypeList[i]+".txt";
			ArrayList<ExplanationTuple> contentList = reportData.selectExplanations(null, questionTypeList[i]);
			reportData.printToFile(path+fileName, contentList);
			i++;
		}
		System.out.println("files written, look at: "+path);
	}


}

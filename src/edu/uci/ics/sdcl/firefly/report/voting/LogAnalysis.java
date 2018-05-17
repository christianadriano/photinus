package edu.uci.ics.sdcl.firefly.report.voting;

import java.awt.Point;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;

public class LogAnalysis {

	private NumberFormat formatter = new DecimalFormat("#0.00"); 

	LogData data;
	
	public HashMap<String,Counter> counterMap;
	public HashMap<String, Result> bugReportResultMap;
	
	public LogAnalysis(LogData data){
		this.data = data;
		counterMap = new HashMap<String,Counter>(); 
		bugReportResultMap = new HashMap<String, Result>();
	}
	


	

	
	

	/**
	 * Removed Method Body questions from the set.
	 */
	public void answersPerMethod(boolean include,HashMap<String,String> methodMap, HashMap<String, Microtask>taskMap){
		//System.out.println(" *** ANSWERS PER MICROTASK QUESTION: ");
		double yes=0;
		double total=0;
		double no=0;
		double iDontKnow=0;
		
		Iterator<String> iter = taskMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			Microtask task = taskMap.get(key);
			
			//takes the situation for Yes and Nos questions
			if(include && methodMap.containsKey(task.getID().toString()) ||
					(!include && !methodMap.containsKey(task.getID().toString())))
			{
				yes=0;
				no=0;
				total=0;
				iDontKnow=0;
				System.out.print(task.getID()+"|"+ task.getQuestion()+"|");
				Vector<Answer> answerList = task.getAnswerList();
				for(Answer answer: answerList){
					
					if(answer.getOption().trim().matches(Answer.YES))
						yes++;
					else
						if(answer.getOption().trim().matches(Answer.NO))
							no++;
						else
							if(answer.getOption().matches(Answer.I_DONT_KNOW))
								iDontKnow++;
				}
				total = yes+ no+ iDontKnow;
				System.out.print("total|"+total);
				System.out.print("| #Yes|"+yes+ "|"+ formatter.format(yes/(total)*100)+ "%");
				System.out.print("| #Nos|"+no + "|"+ formatter.format(no/(total)*100)+ "%");
				System.out.print("| #IDontKnow|"+iDontKnow + "|"+ formatter.format(iDontKnow/(total)*100)+ "%");
				System.out.println();
			}		
		}

		
	
			
		System.out.println("-------------------------------------------");
	

	}
	
	public void expectedYesAnswers(Point point, String fileName, HashMap<String, Microtask>microtaskMap, HashMap<String,String> yesMap){
		double counterYes=0;
		double total=0;
		double counterNos=0;
		double counterIDontKnow=0;
		Iterator<String> iter = microtaskMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			Microtask task = microtaskMap.get(key);
		
			int id = task.getID().intValue();
			if(yesMap.containsKey(task.getID().toString())&& id>=point.x && id<=point.y){
				Vector<Answer> answerList = task.getAnswerList();
				
				for(Answer answer: answerList){
					//if(fileName.trim().matches("1buggy_ApacheCamel.txt") && id==1)
						//System.out.println("####### counting TOTALs taskId:"+id+" to 1buggy_ApacheCamel.txt!" + " answer="+answer.getOption());
					if(answer.getOption().trim().matches(Answer.YES)){
						
						counterYes++;
					}
					else
						if(answer.getOption().trim().matches(Answer.NO)){

							counterNos++;
						}
						else
							if(answer.getOption().matches(Answer.I_DONT_KNOW)){
							
								counterIDontKnow++;
							}		
				}
			}
		}

		//System.out.println(" *** BUG REVEALING QUESTIONS - EXPECTED YES/Prob YES ANSWERS: ");
		total = counterYes+counterNos+counterIDontKnow;
		/*System.out.println(fileName+ " Bug Revealing Answers="+total);
		System.out.println("counterYes="+counterYes+ " = "+ formatter.format(counterYes/(total)*100)+ "%");
		System.out.println("counterNos="+counterNos + " = "+ formatter.format(counterNos/(total)*100)+ "%");
		System.out.println("counterICantTell="+counterICantTell + " = "+ formatter.format(counterICantTell/(total)*100)+ "%");
		System.out.println("-------------------------------------------");
	*/
		this.counterMap.put(fileName,new Counter(counterYes,counterNos, counterIDontKnow, fileName,total));

		Result result = this.bugReportResultMap.get(fileName);
		if(result==null)
			result = new Result();

		result.truePositives = new Double(counterYes).toString();
		result.falseNegatives = new Double(counterNos).toString();
		result.percent_FalseNegatives = formatter.format(counterNos/(total)*100)+ "%";
		result.percent_TruePositives = formatter.format(counterYes/(total)*100)+ "%";

		result.total = result.total + total;
		
		result.inconclusive = result.inconclusive + counterIDontKnow;
		result.percent_Inconclusive = formatter.format(result.inconclusive/result.total*100) + "%";
		
		result.fileName = fileName;
		
		this.bugReportResultMap.put(fileName, result);
	}
	
	
	public void expectedNoAnswers(Point point,String fileName, HashMap<String, Microtask>microtaskMap, HashMap<String,String> yesMap){
			double counterYes=0;
			double total=0;
			double counterNos=0;
			double counterIDontKnow=0;
			Iterator<String> iter = microtaskMap.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				Microtask task = microtaskMap.get(key);
				int id = task.getID().intValue();
				if(!yesMap.containsKey(task.getID().toString())&& id>=point.x && id<=point.y){
					Vector<Answer> answerList = task.getAnswerList();
					for(Answer answer: answerList){
						if(answer.getOption().trim().matches(Answer.YES))
							counterYes++;
						else
							if(answer.getOption().trim().matches(Answer.NO))
								counterNos++;
							else
								if(answer.getOption().trim().matches(Answer.I_DONT_KNOW))
									counterIDontKnow++;
					}
				}
			}

			//System.out.println(" *** NO-BUG REVEALING QUESTIONS - EXPECTED No/Prob No ANSWERS: ");
			total = counterYes+counterNos+counterIDontKnow;
			/*System.out.println(fileName+" NO-BUG Expected Answers="+total);
			System.out.println("counterYes="+counterYes+ " = "+ formatter.format(counterYes/(total)*100)+ "%");
			System.out.println("counterNos="+counterNos + " = "+ formatter.format(counterNos/(total)*100)+ "%");
			System.out.println("counterICantTell="+counterICantTell + " = "+ formatter.format(counterICantTell/(total)*100)+ "%");
			System.out.println("-------------------------------------------");
			*/
			
			Result result = this.bugReportResultMap.get(fileName);
			if(result==null)
				result = new Result();

			result.trueNegatives = new Double(counterNos).toString();
			result.falsePositives = new Double(counterYes).toString();
			result.percent_TrueNegatives = formatter.format(counterNos/(total)*100)+ "%";
			result.percent_FalsePositives = formatter.format(counterYes/(total)*100)+ "%";
			
			result.total = result.total + total;
			
			result.inconclusive = result.inconclusive + counterIDontKnow;
			result.percent_Inconclusive = formatter.format(result.inconclusive/result.total*100) + "%";
			
			result.fileName = fileName;
			
			this.bugReportResultMap.put(fileName, result);
			
			this.counterMap.put(fileName,new Counter(counterYes,counterNos, counterIDontKnow, fileName,total));
		}
	
	public class Counter{
		public Counter(double counterYes, double counterNos, double counterICantTell, String fileName,
				double counterAnswers) {
			super();
			this.yes = counterYes;
			this.no = counterNos;
			this.iCantTell = counterICantTell;
			this.fileName = fileName;
			this.totalAnswers = counterAnswers;
		}
		public double yes;
		public double no;
		public double iCantTell;
		public String fileName;
		public double totalAnswers;
	}		


	
	private static LogAnalysis initializeLogs(){
		
		LogData data = new LogData(true, 0);
		
		String path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-1\\Oct25\\SnapShot12\\";
		data.processLogProduction1(path);
		
		path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\1.Fall2014-Experiments\\Production-2October25\\1Final\\";
		data.processLogProduction2(path);	
		
		System.out.println("----------------------------------------------------------------");
		System.out.println("--- CONSOLIDATED BATCHES ---------------------------------------");
		
		System.out.println("Logs loaded! Totals are:");
		System.out.println("Consents: "+data.getConsents());
		System.out.println("SkillTests: "+data.getSkillTests());
		System.out.println("Surveys: "+data.getSurveys());
		System.out.println("Sessions Opened: "+data.getOpenedSessions());
		System.out.println("Sessions Closed: "+data.getClosedSessions());
		System.out.println("Answers in Map: "+data.getNumberOfMicrotasks());
		System.out.println("Microtasks in List: "+data.microtaskList.size());
		
		System.out.println("Consents: "+data.getConsents());
		System.out.println("SkillTests: "+data.getSkillTests());
		System.out.println("Surveys: "+data.getSurveys());
		System.out.println("Sessions Opened: "+data.getOpenedSessions());
		System.out.println("Sessions Closed: "+data.getClosedSessions());
		System.out.println("Answers: "+data.getNumberOfMicrotasks());
		
		LogAnalysis analysis = new LogAnalysis(data);
		return analysis;
	}
	
	public static void main(String[] args){
		
		LogAnalysis analysis = initializeLogs();
		LogData data = analysis.data;
		System.out.println("EXPECTED YES ANSWERS");
		analysis.answersPerMethod(true, data.yesMap, data.microtaskMap);
		
		//System.out.println("EXPECTED NO ANSWERS");
		//analysis.answersPerMethod(false, data.yesMap, data.microtaskMap);

	
	}
	
	
	public void expectedYesNos_Unfiltered(HashMap<String, Microtask> microtaskMap){
		//General
		this.expectedYesAnswers(new Point(0,214),"Total", microtaskMap, data.yesMap);
		this.expectedNoAnswers(new Point(0,214),"Total", microtaskMap, data.yesMap);
		//analysis.expectedYesAnswersLessMethodBody();
		
		
		//Configure the MicrotaskMap
		Iterator<String> iter;
		System.out.println("----------------------------------------------------------------");
		System.out.println("--- ANALYSIS PER FILE -----------------------------------------");
		System.out.println();
		
		System.out.println("---- EXPECTED YES ----");
		iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			System.out.print("BUG REPORT: "+ fileName+", ");
			Point point = data.fileNameTaskRange.get(fileName);
			this.expectedYesAnswers(point,fileName,microtaskMap, data.yesMap);
		}
		
		System.out.println();
		
		System.out.println("---- EXPECTED NO ----");
		iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			System.out.print("BUG REPORT: "+ fileName+", ");
			Point point = data.fileNameTaskRange.get(fileName);
			this.expectedNoAnswers(point,fileName,microtaskMap, data.yesMap);
		}
	
	}
	
	
	private void expectedFilterCompleteSessions(){
		
		data.computeMicrotaskFromCompleteSessions();

		data.workingMicrotaskMap = data.completeSessions_microtaskMap;
		
		System.out.println("---- EXPECTED YES >>FILTER: ONLY COMPLETE SESSIONS ----");
		Iterator<String> iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			System.out.print("BUG REPORT: "+ fileName+", ");
			Point point = data.fileNameTaskRange.get(fileName);
			this.expectedYesAnswers(point,fileName,data.microtaskMap, data.yesMap);
		}
		
		System.out.println();
		
		System.out.println("---- EXPECTED NO >>FILTER: ONLY COMPLETE SESSIONS ----");
		iter = data.fileNameTaskRange.keySet().iterator();
		while(iter.hasNext()){
			String fileName = iter.next();
			System.out.print("BUG REPORT: "+ fileName+", ");
			Point point = data.fileNameTaskRange.get(fileName);
			this.expectedNoAnswers(point,fileName,data.microtaskMap, data.yesMap);
		}
	}
	
	/*
	public void expectedYesAnswersLessMethodBody(){
		double counterYes=0;
		double counterAnswers=0;
		double counterNos=0;
		double counterICantTell=0;
		Iterator<String> iter = data.workingMicrotaskMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			Microtask task = data.workingMicrotaskMap.get(key);
			if(data.yesNotBodyFunctionMap.containsKey(task.getID().toString())){
				Vector<Answer> answerList = task.getAnswerList();
				for(Answer answer: answerList){
					if(answer.getOption().trim().matches(Answer.YES) || answer.getOption().trim().matches(Answer.PROBABLY_YES))
						counterYes++;
					else
						if(answer.getOption().trim().matches(Answer.NO) || answer.getOption().trim().matches(Answer.PROBABLY_NOT))
							counterNos++;
						else
							if(answer.getOption().matches(Answer.I_CANT_TELL))
								counterICantTell++;
				}
			}

		}

		//System.out.println(" *** BUG REVEALING QUESTIONS BUT METHOD BODY QUESTIONS - EXPECTED YES/Prob YES ANSWERS: ");
		counterAnswers = counterYes+counterNos+counterICantTell;
		System.out.println("Total Bug Revealing Answers="+counterAnswers);
		System.out.println("counterYes="+counterYes+ " = "+ formatter.format(counterYes/(counterAnswers)*100)+ "%");
		System.out.println("counterNos="+counterNos + " = "+ formatter.format(counterNos/(counterAnswers)*100)+ "%");
		System.out.println("counterICantTell="+counterICantTell + " = "+ formatter.format(counterICantTell/(counterAnswers)*100)+ "%");
			
		System.out.println("-------------------------------------------");

	}
	 */
	
}

package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;

public class AverageWorkerProfileReport extends CountReport{
	
	public AverageWorkerProfileReport() {
		super();
	}
	
	@Override
	public String getType() {
		return "Averages Worker Profile";
	}

	@Override
	public Map<String, List<String>> generateReport(HeaderReport headerReport, AnswerReport answerReport, FilterCombination filter) {
		Map<String, List<String>> content = mergeContent(headerReport, answerReport);
		SessionDTO database = new FileSessionDTO();
		Map<String, Microtask> microtasks = filter.apply((HashMap<String, Microtask>) database.getMicrotasks());
		List<String> questionIDList = content.get("Question ID"); // this is the data that came form the HeaderReport
		List<String> averageWorkerScores = new ArrayList<String>();
		List<String> averageYears = new ArrayList<String>();
		List<String> averageDifficulties = new ArrayList<String>();
		FileConsentDTO fc = new FileConsentDTO();
		
		for (String questionID : questionIDList) {
			List<String> workerIDs = microtasks.get(questionID).getWorkerIds();
			if(workerIDs != null){
				int workerScore = 0;
				int yearsProgramming = 0;
				int perceivedDifficulty = 0;
				
				for (String workerID : workerIDs) {	
					Iterator<Worker> workersIter = fc.getWorkers().values().iterator();
					while (workersIter.hasNext()) {
						Worker worker = workersIter.next();
						if(worker.getWorkerId().compareTo(workerID)==0){
							workerScore += worker.getGrade();
							String value = worker.getSurveyAnswer(" YearsProgramming");
							yearsProgramming += Double.parseDouble((value != null) ? value : "0.0");
							perceivedDifficulty += microtasks.get(questionID).getAnswerByUserId(workerID).getDifficulty();
						}
					}
				}
				averageWorkerScores.add(String.format("%.2f",((float)workerScore)/workerIDs.size()));
				averageYears.add(String.format("%.2f", (float)yearsProgramming/workerIDs.size()));
				averageDifficulties.add(String.format("%.2f", (float)perceivedDifficulty/workerIDs.size()));
			}
		}
		this.countContent.put("Average worker score", averageWorkerScores);
		this.countContent.put("Average worker years experience", averageYears);
		this.countContent.put("Average worker perceived difficulty", averageDifficulties);
		
		return content;
	}
	
	private Map<String, List<String>> mergeContent(HeaderReport headerReport, AnswerReport answerReport)
	{
		Map<String, List<String>> content = new LinkedHashMap<String, List<String>>();
		content.putAll(headerReport.getContent());
		content.putAll(answerReport.getContent());
		return content;
	}

}

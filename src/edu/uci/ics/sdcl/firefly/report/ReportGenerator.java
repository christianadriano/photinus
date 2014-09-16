package edu.uci.ics.sdcl.firefly.report;

import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.FileDebugSession;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.storage.MicrotaskStorage;
import edu.uci.ics.sdcl.firefly.storage.WorkerSessionStorage;
import edu.uci.ics.sdcl.firefly.storage.WorkerStorage;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class ReportGenerator {
	
	public static void main(String[] args) {
		
		PropertyManager manager = new PropertyManager();
		
		ReportGenerator reports = new ReportGenerator();
		System.out.println(reports.createMicrotasksReport());
		System.out.println(reports.createWorkersReport());
		System.out.println(reports.createAnswersReport());
	}
	
	public boolean createMicrotasksReport(){
		MicrotaskStorage microtaskStore = new MicrotaskStorage();
		HashMap<String, FileDebugSession> microtasks = microtaskStore.readAllDebugSessions();
		MicrotasksReportGenerator microtaskReport = new MicrotasksReportGenerator();
		return microtaskReport.writeToXlsx(microtasks);
	}

	public boolean createWorkersReport(){
		WorkerStorage workersStore = new WorkerStorage();
		HashMap<String, Worker> workers= workersStore.readAllWorkers();
		// for debug purposes:
		/* Set<Map.Entry<String, Worker>> setWorkers = workers.entrySet();
		Iterator<Entry<String, Worker>> iterateWorkers = setWorkers.iterator();
		while(iterateWorkers.hasNext())
		{
			Map.Entry<String, Worker> mapEntryWorker = (Map.Entry<String, Worker>)iterateWorkers.next();
			System.out.println(mapEntryWorker.getKey() + " " + mapEntryWorker.getValue().getHitId() + 
				" " + mapEntryWorker.getValue().getGradeMap() + "  " + mapEntryWorker.getValue().getSurveyAnswers());
		} */
		if (null != workers){
			WorkersReportGenerator generator = new WorkersReportGenerator();
			generator.writeToXlsx(workers);
			return true;
		} else
			return false;
	}
	
	public boolean createAnswersReport(){
		WorkerSessionStorage workerSessionStorage = new WorkerSessionStorage();
		HashMap<String, Object> storage = workerSessionStorage.readStorage();
		if (null != storage){
			SessionsReportGenerator excelAnswersReport = new SessionsReportGenerator();
			return excelAnswersReport.writeToXlsx(storage);
		} else
			return false;
	}
}
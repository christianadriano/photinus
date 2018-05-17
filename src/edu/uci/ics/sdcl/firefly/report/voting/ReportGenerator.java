package edu.uci.ics.sdcl.firefly.report.voting;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.FileDebugSession;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.storage.MicrotaskStorage;
import edu.uci.ics.sdcl.firefly.storage.WorkerSessionStorage;
import edu.uci.ics.sdcl.firefly.storage.WorkerStorage;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class ReportGenerator {
	
	public static void main(String[] args) {
		ReportGenerator reports = new ReportGenerator();
		System.out.println(reports.createMicrotasksReport());
		System.out.println(reports.createWorkersReport());
		System.out.println(reports.createAnswersReport());
	}
	
	public void runReports(){
		System.out.println(createMicrotasksReport());
		System.out.println(createWorkersReport());
		System.out.println(createAnswersReport());
	}
	
	public boolean createMicrotasksReport(){
		MicrotaskStorage microtaskStore = MicrotaskStorage.initializeSingleton();
		Hashtable<String, FileDebugSession> microtasks = microtaskStore.readAllDebugSessions();
		MicrotasksReportGenerator microtaskReport = new MicrotasksReportGenerator();
		return microtaskReport.writeToXlsx(microtasks);
	}

	public boolean createWorkersReport(){
		WorkerStorage workersStore =  WorkerStorage.initializeSingleton();
		Hashtable<String, Worker> workers= workersStore.readAllWorkers();
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
		WorkerSessionStorage workerSessionStorage = WorkerSessionStorage.initializeSingleton();
		Vector<WorkerSession> closedSessions = workerSessionStorage.retrieveClosedSessionStorage();
		Hashtable<String, WorkerSession> activeSessions = workerSessionStorage.retrieveActiveSessionStorage();
		Hashtable<String, Stack<WorkerSession>> newSessions = workerSessionStorage.retrieveNewSessionStorage();
		
		if(closedSessions==null)
			closedSessions= new Vector<WorkerSession>();
		if(activeSessions==null)
			activeSessions= new Hashtable<String, WorkerSession>();
		if(newSessions==null)
			newSessions= new Hashtable<String, Stack<WorkerSession>>();
		
		if (closedSessions.size()==0 && activeSessions.size()==0 && newSessions.size()==0){ //Don't even generate the report, because it is empty
			return false;
		}else{
			SessionsReportGenerator excelAnswersReport = new SessionsReportGenerator();
			return excelAnswersReport.writeToXlsx(closedSessions,activeSessions, newSessions);
		}
	}
}

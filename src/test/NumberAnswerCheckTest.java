package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;


public class NumberAnswerCheckTest {

	HashMap<String,ArrayList<Answer>> sessionWorkerAnswerMap = new HashMap<String,ArrayList<Answer>>();

	HashMap<String,ArrayList<Answer>> microtaskWorkerAnswerMap = new HashMap<String,ArrayList<Answer>>();

	HashMap<String,WorkerSession> workerSessionMap = new HashMap<String,WorkerSession>();

	HashMap<String, Microtask> microtaskMap = new HashMap<String, Microtask>();

	private void loadData(){
		FileSessionDTO dto = new FileSessionDTO();
		this.workerSessionMap = (HashMap<String, WorkerSession>) dto.getSessions();
		this.microtaskMap = (HashMap<String, Microtask>) dto.getMicrotasks();
	}


	@Test
	public void compareAnswers(){

		loadData();
		loadMaps();

		Iterator<String> iter = this.microtaskWorkerAnswerMap.keySet().iterator();
		while(iter.hasNext()){
			String workerID = iter.next();
			ArrayList<Answer> sessionAnswerList = this.sessionWorkerAnswerMap.get(workerID);
			ArrayList<Answer> taskAnswerList = this.microtaskWorkerAnswerMap.get(workerID);
			int sessionAnswerCount = sessionAnswerList.size();
			int taskAnswerCount = taskAnswerList.size();

			if(sessionAnswerCount != taskAnswerCount){
				System.out.println("WorkerID: "+ workerID+" has different answers, sessionCount= "+sessionAnswerCount+", taskCount= "+taskAnswerCount);
			}
		}
	}

	private void loadMaps(){
		//Load worker session answers
		Iterator<String> iter = this.workerSessionMap.keySet().iterator();
		while(iter.hasNext()){
			String sessionID = iter.next();
			WorkerSession session = workerSessionMap.get(sessionID);
			String workerID  = session.getWorkerId();
			Vector<Microtask> taskList = session.getMicrotaskList();
			for(Microtask task: taskList){
				Answer answer = task.getAnswerByUserId(workerID);
				this.sessionWorkerAnswerMap = addAnswerToSessionWorker(workerID,answer,this.sessionWorkerAnswerMap);
			}
		}

		//Load worker microtask answers
		iter = this.microtaskMap.keySet().iterator();
		while(iter.hasNext()){
			String microtaskID = iter.next();
			Microtask task = this.microtaskMap.get(microtaskID);
			for(Answer answer: task.getAnswerList()){
				this.microtaskWorkerAnswerMap = addAnswerToSessionWorker(answer.getWorkerId(),answer,this.microtaskWorkerAnswerMap);
			}
		}


	}

	private  HashMap<String, ArrayList<Answer>> addAnswerToSessionWorker(String workerID, Answer answer, 
			HashMap<String, ArrayList<Answer>> workerAnswerMap) {

		ArrayList<Answer> answerList = workerAnswerMap.get(workerID);
		if(answerList==null)
			answerList = new ArrayList<Answer>();
		answerList.add(answer);
		workerAnswerMap.put(workerID, answerList);		
		return workerAnswerMap;
	}

	/** Investigate one specific workerID 85EG-9G-9A73-8:1686gc9a4I6-23 */
	@Test
	public void printWorkerIDData(){
		loadData();
		//Load worker microtask answers
		System.out.println("MicrotaskMap data");
		Iterator<String> iter = this.microtaskMap.keySet().iterator();
		while(iter.hasNext()){
			String microtaskID = iter.next();
			Microtask task = this.microtaskMap.get(microtaskID);
			for(Answer answer: task.getAnswerList()){
				if(answer.getWorkerId().matches("85EG-9G-9A73-8:1686gc9a4I6-23"))
					System.out.println("taskID: "+ microtaskID);
			}
		}
		
		System.out.println("-------------------------------------");
		System.out.println("WorkerSessioMap data");
		iter = this.workerSessionMap.keySet().iterator();
		while(iter.hasNext()){
			String sessionID = iter.next();
			WorkerSession session = workerSessionMap.get(sessionID);
			String workerID  = session.getWorkerId();
			if(workerID.matches("85EG-9G-9A73-8:1686gc9a4I6-23")){
				Vector<Microtask> taskList = session.getMicrotaskList();
				for(Microtask task: taskList){
					Answer answer = task.getAnswerByUserId(workerID);
					System.out.println("taskID: "+ task.getID());
				}
			}
		}
	}
}

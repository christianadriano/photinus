package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class FileSessionDTO extends SessionDTO{

	//Full path of the log file. Ex.: myFolder/myFile.lo
	private String logPath;// = //"C:/Users/igMoreira/Desktop/Dropbox/1.CrowdDebug-Summer2015/sampleDatalogs/session-log-TestSample - Copy.log";
	private static int dayDate = 7; //July 7th
	private static int lastHourRead = -1;


	public FileSessionDTO() {
		PropertyManager manager = PropertyManager.initializeSingleton();
		this.logPath = manager.reportPath + manager.sessionLogFileName;
		System.out.println("sessionDTO, logPath: "+logPath);
	}

	public FileSessionDTO(String path) {
		this.logPath = path;
	}


	@Override
	public Map<String, WorkerSession> getSessions() {
		if((this.openSessions.isEmpty()) || (this.closedSessions.isEmpty()))
			loadSessions();
		Map<String, WorkerSession> conc = concatenateSessionTable();
		return conc;
	}

	@Override
	public Map<String, Microtask> getMicrotasks() {
		if((this.openSessions.isEmpty()) || (this.closedSessions.isEmpty()))
			loadSessions();
		return microtasks;
	}

	@Override
	protected void loadSessions() {
		//CLEAN THE TABLES
		this.closedSessions = new LinkedHashMap<String, WorkerSession>();
		this.openSessions = new LinkedHashMap<String, WorkerSession>();
		this.microtasks = new LinkedHashMap<String, Microtask>();

		BufferedReader log = null;
		try {
			log = new BufferedReader(new FileReader(logPath));
			String line = null;
			WorkerSession session = null;
			while ((line = log.readLine()) != null) {
				if(line.equals(""))
					continue;
				String event = line.split("%")[1];
				switch (event) {
				case "OPEN SESSION":
					session = loadSession(line);
					this.openSessions.put(session.getId()+ ":" +session.getWorkerId(), session);
					break;
				case "CLOSE SESSION":
					session = loadSession(line);
					if(this.openSessions.containsKey(session.getId()+ ":" +session.getWorkerId()))
					{
						session = this.openSessions.get(session.getId()+ ":" +session.getWorkerId());
						this.openSessions.remove(session.getId()+ ":" +session.getWorkerId());
					}
					this.closedSessions.put(session.getId()+ ":" +session.getWorkerId(), session);
					break;
				case "MICROTASK":
					loadMicrotask(line);
					break;
				default:
					System.out.println("INVALID EVENT ON LOG " + event  );
					System.exit(0);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("DATABASE ERROR: Could not open database file.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("DATABASE ERROR: Could not read database file.");
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + logPath);
			e.printStackTrace();
		}
		finally
		{
			try {
				log.close();
			} catch (IOException e) {
				System.out.println("DATABASE ERROR: Could not close database file:"+logPath);
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	/**
	 * Responsible for loading data from the database and
	 * setting to the microtasks attribute.
	 */
	private void loadMicrotask(String line) {

		String[] result = line.split("%");		
		Integer microtaskID = 0;
		String sessionID = null;
		String workerID = null;
		String explanation = null;
		String fileName = null;
		String question = null;
		String answer = null;
		String duration = null;
		Integer confidenceLevel = 0;
		Integer diffculty = 0;
		String questionType = null;
		String questionTimestamp = line.substring(0, 27);

		for (int i = 0; i < result.length; i++) {
			
			String field = result[i];
			field = field.replaceAll("\\s", "");
			field = field.replaceAll("\n", "");
			switch (field) {
			case "workerId": workerID = result[i+1]; break;
			case "explanation": explanation = ( i < result.length) ? result[i+1] : "";	break;
			case "fileName": fileName = result[i+1]; break;
			case "question": question = result[i+1]; break;
			case "answer": answer = result[i+1]; break;
			case "duration": duration = result[i+1]; break;
			case "sessionId": sessionID = result[i+1]; break;
			case "microtaskId":	microtaskID = Integer.parseInt(result[i+1]); break;
			case "confidenceLevel":	confidenceLevel = Integer.parseInt(result[i+1]);break;
			case "difficulty":diffculty = Integer.parseInt(result[i+1]);break;
			case "questionType":questionType = result[i+1]; break;
			}
		}//for

		Vector<Answer> answerList = new Vector<Answer>();
		Microtask microtask=null;
		int currentNumberMicrotasks = 0;

		// Bind the microtask with the session
		Map<String, WorkerSession> conc = concatenateSessionTable();
		WorkerSession session = conc.get(sessionID+ ":" + workerID);

		if(session != null)
		{
			currentNumberMicrotasks = session.getMicrotaskListSize() + 1;//how many microtasks are there = index +1;

			answerList.add(new Answer(answer,confidenceLevel, explanation, workerID, duration, questionTimestamp,diffculty,currentNumberMicrotasks));
			microtask = new Microtask(question, microtaskID, answerList , fileName);
			microtask.setQuestionType(questionType);

			//Add this microtask to the session
			session.getMicrotaskList().add(microtask);
			conc.put(session.getId(),session);

		}
		else{
			System.out.println("ERROR SESSION NULL when adding Microtask, sessionID:"+sessionID);
		}

		if(this.microtasks.get(microtaskID.toString()) == null)
		{
			// The microtask does not exist yet so insert on table
			this.microtasks.put(microtaskID.toString(), microtask);

		}
		else
		{
			//The microtask already exists so add the answer to it
			answerList = this.microtasks.get(microtaskID.toString()).getAnswerList();
			answerList.add(new Answer(answer,confidenceLevel, explanation, workerID, duration, questionTimestamp,diffculty,currentNumberMicrotasks));
		}
	}

	/**
	 * Will extract the session content of the
	 * line on the database log.
	 * @param line: line containing the session information
	 */
	private WorkerSession loadSession(String line){
		String[] result = line.split("%");
		String workerID = (result[3].replaceAll("\\s", ""));
		workerID = workerID.replaceAll("\n", "");
		String sessionID = (result[5].replaceAll("\\s", ""));
		sessionID = sessionID.replaceAll("\n", "");
		String fileName = (result[7].replaceAll("\\s", ""));
		WorkerSession session = new WorkerSession(sessionID, new Vector<Microtask>());
		session.setWorkerId(workerID);
		session.setFileName(fileName);
		return session;
	}

	/**
	 * Concatenates the open and the closed session in 
	 * just one table.
	 * @return: A hashMap containing all sessions.
	 */
	public Map<String, WorkerSession> concatenateSessionTable()
	{
		HashMap<String, WorkerSession> conc = new LinkedHashMap<String, WorkerSession>();
		conc.putAll(openSessions);
		conc.putAll(closedSessions);
		return conc;
	}

	/**
	 * gets the sessions of a single worker
	 * @return: A hashMap containing all the sessions related to the worker
	 */
	@Override
	public ArrayList<WorkerSession> getSessionsByWorkerID(String workerID) {
		ArrayList<WorkerSession> workerSessions = new ArrayList<WorkerSession>();
		Collection<WorkerSession>  sessions = getSessions().values();
		for (WorkerSession session : sessions) {
			if(session.getWorkerId().compareTo(workerID)==0){
				workerSessions.add(session);
			}
		}
		return workerSessions;
	}

	public Map<String,WorkerSession> getClosedSessions(){
		return this.closedSessions;
	}
}

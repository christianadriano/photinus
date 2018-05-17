package edu.uci.ics.sdcl.firefly.report.voting;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class SessionsReportGenerator {
	private HashMap<Integer, Integer> questionsInSheet;	// HashMap with the microtask's ID and respective column number
	private Integer columnNumber, key;
	private Map<Integer, Object[]> data;
	private Map<Integer, Object[]> dataTimeSheet;
	private Object[] lineContent;
	private XSSFWorkbook workbook;
	private String fileName = "SessionsReport.xlsx";
	
	public SessionsReportGenerator() {
		PropertyManager manager = PropertyManager.initializeSingleton();
		String path = manager.reportPath;
		this.fileName = path + this.fileName;
		this.questionsInSheet = new HashMap<Integer, Integer>();
		this.columnNumber = 3;
		this.key = 0;	// for the 'data' map
		data = new TreeMap<Integer, Object[]>();
		dataTimeSheet = new TreeMap<Integer, Object[]>();
		this.workbook = new XSSFWorkbook();
	}
	
	@SuppressWarnings("unchecked")
	public boolean writeToXlsx(Vector<WorkerSession> closedSessions, Hashtable<String,WorkerSession> activeSessions, Hashtable<String,Stack<WorkerSession>> newSessions)
	{
		/* setting a HashMap with the microtask's ID and the respective column number  */
		// reading closed session's questions
		for (WorkerSession workerSession : closedSessions) {
			this.populateQuestionMap(workerSession.getMicrotaskList());
		}
		// reading active sessions questions
		Vector<WorkerSession> activeSessionsAL = new Vector<WorkerSession>();	// to cache all the workerSessions
		Iterator<String> activeIterator = activeSessions.keySet().iterator();
		while(activeIterator.hasNext())
		{
			WorkerSession session = activeSessions.get(activeIterator.next());
			this.populateQuestionMap(session.getMicrotaskList());
			activeSessionsAL.add(session);	// storing worker sessions to not loop again
		}
		// reading new session's questions
		Vector<WorkerSession> newSessionsAL = new Vector<WorkerSession>();
		Set<String> keys = newSessions.keySet();
		for (String key : keys) {
			Stack<WorkerSession> workerSession = newSessions.get(key);
			Iterator<WorkerSession> iter = workerSession.iterator();
			while(iter.hasNext()){
				this.populateQuestionMap((iter.next()).getMicrotaskList());
				newSessionsAL.add(iter.next());		// converting to array list (AL)
			}
		}
		
		/* preparing the data structure */
		this.lineContent = new Object[this.questionsInSheet.size()+4];	// represents the lines on the sheet
		/* creating first header line */								// +3 for the User Id, Session Id, and Duration
		this.lineContent[0] = "Worker ID";
		this.lineContent[1] = "Session ID";
		this.lineContent[2] = "Duration"; 		// the time the worker took in the all microtasks	
		
		Iterator<Integer> iteratorQIS = this.questionsInSheet.keySet().iterator();	// QIS same as question in sheet
		while(iteratorQIS.hasNext()){
			Integer microtaskId = iteratorQIS.next();
			Integer currentColumn = questionsInSheet.get(microtaskId);
			
			if (null == currentColumn)	// critical error
				return false;
			else
				this.lineContent[currentColumn] = microtaskId;	
		}
		
		//Header ready, now fill up with data from sessions
		
		this.data.put(new Integer(this.key++), lineContent);
		this.dataTimeSheet.put(new Integer(this.key++), lineContent);
		
		/* getting data from the closed sessions */								
		if (!this.populateDataLines(closedSessions))
			return false;
		/* now from the active sessions */								
		if (!this.populateDataLines(activeSessionsAL))
			return false;
		
		/* now for the new sessions implement the not answered counter */
		this.lineContent = new Object[questionsInSheet.size()+4];
		Arrays.fill(this.lineContent, (Integer)0);	// populating the array with 0's
		this.lineContent[0] = "Unanswered";
		this.lineContent[1] = "-";
		this.lineContent[2] = "-";
		
		for (WorkerSession workerSession : newSessionsAL) {
			// counting unanswered questions 
			Vector<Microtask> microtasks = workerSession.getMicrotaskList();
			for (Microtask microtask : microtasks) {
				Integer currentColumn = this.questionsInSheet.get(microtask.getID());
				if (null == currentColumn)
					return false;
				if (microtask.getAnswerList().isEmpty())
					this.lineContent[currentColumn] = (Integer)this.lineContent[currentColumn] + 1;	
			}
		}
		this.data.put(new Integer(this.key++), this.lineContent);
		this.dataTimeSheet.put(new Integer(this.key++), this.lineContent);
		
		
		
		return (createWorkbook("Answers", this.data) && createWorkbook("Durations", this.dataTimeSheet) && writeWorkbookToFile());
	}
	
	private boolean createWorkbook(String name, Map<Integer, Object[]> content){
		/* creating excel workbook */
		

		//Create a blank sheet (for the scores)
		XSSFSheet answerSheet = this.workbook.createSheet(name);

		int rownum = 0;
		Row row;
		Cell cell;

		/* filling the Answer sheet */
		// Iterate over data and write to score sheet
		Set<Integer> keyset = content.keySet();
		for (Integer singleKey : keyset)
		{	// new row for each entry
			row = answerSheet.createRow(rownum++);
			Object [] objArr = content.get(singleKey);
			int cellnum = 0;
			for (Object obj : objArr)
			{	// new cell for each object on the object Array
				cell = row.createCell(cellnum++);
				if(obj instanceof String)
				{
					String text = (String)obj;
					if (text.length() > 30)	// wrapping text
					{
						CellStyle style = workbook.createCellStyle(); //Create new style
						style.setWrapText(true); 	//Set word wrap
						cell.setCellStyle(style); 	//Apply style to cell
					}
					cell.setCellValue(text);
				}
				else if(obj instanceof Integer)
					cell.setCellValue((Integer)obj);
			}
		}
		// sizing columns for method sheet
		for (short c=0; c<questionsInSheet.size(); c++){
			answerSheet.autoSizeColumn(c);
		}
		return true;
	}
	
	private boolean writeWorkbookToFile(){
	
		try
		{	//Write the workbook in file system
			FileOutputStream out = new FileOutputStream(new File(this.fileName));
			this.workbook.write(out);
			out.flush();
			out.close();
			System.out.println("SessionReport.xlsx written successfully on disk.");
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean populateDataLines(Vector<WorkerSession> workerSessions){
		for (WorkerSession workerSession : workerSessions) {
			this.lineContent = new Object[this.questionsInSheet.size()+4]; 
			this.lineContent[0] = workerSession.getWorkerId();			// worker ID
			this.lineContent[1] = workerSession.getId();				// session ID
			this.lineContent[2] = workerSession.getTotalElapsedTime(); 		// the time the worker took to perform all microtasks	
			
			Object[] lineTimeContent = new Object[this.questionsInSheet.size()+4]; 
			lineTimeContent[0] = workerSession.getWorkerId();			// worker ID
			lineTimeContent[1] = workerSession.getId();				// session ID
			lineTimeContent[2] = workerSession.getTotalElapsedTime(); 		// the time the worker took to perform all microtasks	
			
			Vector<Microtask> microtasks = workerSession.getMicrotaskList();
			for (Microtask microtask : microtasks) {
				Integer currentColumn = this.questionsInSheet.get(microtask.getID());
				
				if (null == currentColumn)
					return false;
				
				Answer answer = microtask.getAnswerByUserId(workerSession.getWorkerId());
				
				if (answer==null){
					this.lineContent[currentColumn] = "?";
					lineTimeContent[currentColumn] = "?";
				}
				else{
					this.lineContent[currentColumn] = answer.getOption();	// the answer for that workerId
					lineTimeContent[currentColumn] = answer.getElapsedTime().toString();
				}
			}
			this.data.put(new Integer(this.key++), this.lineContent);
			this.dataTimeSheet.put(new Integer(this.key++), lineTimeContent);
			/* for debug purposes 
			Set<Map.Entry<Integer, Object[]>> setActiveSessions = data.entrySet();
			Iterator<Entry<Integer, Object[]>> iterateActiveSessions = setActiveSessions.iterator();
			while(iterateActiveSessions.hasNext())
			{
				Map.Entry<Integer, Object[]> me = (Map.Entry<Integer, Object[]>)iterateActiveSessions.next();
				System.out.print(me.getKey() + " | ");
				for(Object obj : me.getValue()){
					System.out.print(obj + " | ");
				}
				System.out.println();
				
			}
			System.out.println();
			System.out.println(); */
		}
		return true;
	}
	
	private void populateQuestionMap(Vector<Microtask> vector){
		for (Microtask microtask : vector) {
			if ( !this.questionsInSheet.containsKey(microtask.getID()) ){	
				this.questionsInSheet.put(microtask.getID(), this.columnNumber++);
			}	// if contains the question, do nothing
		}
	}
}

package edu.uci.ics.sdcl.firefly.report.voting;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.servlet.SurveyServlet;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class WorkersReportGenerator {
	
private String fileName = "WorkersReport.xlsx";
	
	public WorkersReportGenerator() {
		PropertyManager manager = PropertyManager.initializeSingleton();
		String path = manager.reportPath;
		this.fileName = path + this.fileName;
	}
	
	public void writeToXlsx(Hashtable<String, Worker> workers)
	{
		/* creating excel workbook */
		//Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		//Create a blank sheet (for the scores)
		XSSFSheet scoreSheet = workbook.createSheet("Scores");

		int key = 0;	// for the 'data' below
		int rownum = 0;	
		Row row;

		/* creating first header line */
		row = scoreSheet.createRow(rownum++);
		int cellNum = 0;
		Cell cell = row.createCell(cellNum++);
		cell.setCellValue("User ID");
		cell = row.createCell(cellNum++);
		
		// the time the worker provided the consent
		cell.setCellValue("Date");
		cell = row.createCell(cellNum++);
		
		cell.setCellValue("Skill Score");
		cell = row.createCell(cellNum++);
				
		// skill test answers
		for (Integer k=1; k<=5; k++){
			cell.setCellValue("Q" + k.toString());
			cell = row.createCell(cellNum++);
		}	
		// the time the worker took in the skill test
		cell.setCellValue("Test Duration");
		cell = row.createCell(cellNum++);
		
		// now for the survey
		for (int i=0; i<SurveyServlet.question.length; i++){
			cell.setCellValue(SurveyServlet.question[i]);
			cell = row.createCell(cellNum++);
		}
		
	
		// populating new data structure to later fill score sheet
		Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
		// iterating users
		Set<Map.Entry<String, Worker>> setWorkers = workers.entrySet();
		Iterator<Entry<String, Worker>> iterateWorkers = setWorkers.iterator();
		while(iterateWorkers.hasNext())
		{
			Map.Entry<String, Worker> mapEntryWorker = (Map.Entry<String, Worker>)iterateWorkers.next();
			Worker worker = mapEntryWorker.getValue();
			
			// preparing line (object), which index is a cell
			Object[] lineContent = new Object[16]; 		// 16 columns
			lineContent[0] = worker.getWorkerId();	// user ID (cell 0)
			
			try{
				
				// date of consent (cell 1)
				Date date = new Date(new Integer(worker.getConsentDate()).longValue());
				 DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
				lineContent[1] = df.format(date);
			}
			catch(Exception e){
				lineContent[1] = worker.getConsentDate() + "ms";
			}
			lineContent[2] = worker.getGrade();	// Skill score (cell 2)
			
			// iterating over the skill questions
			int j = 3;
			if (null != worker.getGradeMap()){
				boolean[] setSkillTest = worker.getGradeMap();
				for (boolean b : setSkillTest) {
					lineContent[j++] = b ? "OK" : "Not";
				}
				// add the duration of the test
				lineContent[j++] = worker.getSkillTestDuration();
			} 
			else{ //No answers, so jump to the 8th cell keeping the previous ones empty.
				j = 8;
			}
			
			// iterating over the Survey questions
			Hashtable<String, String> survey = mapEntryWorker.getValue().getSurveyAnswers();
			for (int i=0; i<SurveyServlet.question.length; i++){
				if (survey.containsKey(SurveyServlet.question[i])){
					lineContent[j++] = survey.get(SurveyServlet.question[i]);
				} else
					lineContent[j++] = "-";
			}
			
			// putting customized line
			data.put(new Integer(key++), lineContent); 
		}
		
		/* filling the score sheet */
		// Iterate over data and write to score sheet
		Set<Integer> keyset = data.keySet();
		for (Integer singleKey : keyset)
		{	// new row for each entry
			row = scoreSheet.createRow(rownum++);
			Object [] objArr = data.get(singleKey);
			int cellnum = 0;
			for (Object obj : objArr)
			{	// new cell for each object on the object Array
				cell = row.createCell(cellnum++);
				if(obj instanceof String)
				{
					String text = (String)obj;
					if (text.length() > 30)	// wraping text
					{
						CellStyle style = workbook.createCellStyle(); //Create new style
						style.setWrapText(true); 	//Set wordwrap
						cell.setCellStyle(style); 	//Apply style to cell
					}
					cell.setCellValue(text);
				}
				else if(obj instanceof Integer)
					cell.setCellValue((Integer)obj);
			}
		}
		// sizing columns for method sheet
		for (short c=0; c<14; c++){
			scoreSheet.autoSizeColumn(c);
		}
		scoreSheet.setColumnWidth(13, 20000);	// the one that can have a long text (wrapped)

		try
		{
			//Write the workbook in file system
			FileOutputStream out = new FileOutputStream(new File(this.fileName));
			workbook.write(out);
			out.flush();
			out.close();

			System.out.println("WorkerReport.xlsx written successfully on disk.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * private class WorkerTimeData{
		String elapsedTime;
		String timeStamp;
	}
	
	private TimeData obtainTimeData(Worker worker){
		String sessionId= worker.getSessionId();
		WorkerSessionStorage workerSessionStorage = new WorkerSessionStorage();
		WorkerSession session = workerSessionStorage.readActiveWorkerSessionByID(sessionId);
		ArrayList<Microtask> microtaskList = session.getMicrotaskList();
		Strin
		for(Microtask mtask: microtaskList){
			mtask.
			ArrayList<Answer> answerList = mtask.getAnswerList();
			for(Answer answer: answerList){
				answer.g
			}

		}
	}
	*/
	
}
package edu.uci.ics.sdcl.firefly.report.voting;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.FileDebugSession;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.util.PathUtil;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;


public class MicrotasksReportGenerator{

	private String fileName = "MicrotasksReport.xlsx";

	public MicrotasksReportGenerator(){
		PropertyManager manager = PropertyManager.initializeSingleton();
		String path = manager.reportPath;
		this.fileName = path+ this.fileName;
	}

	public boolean writeToXlsx(Hashtable<String, FileDebugSession> microtasksMappedPerFile){
		int numberOfQuestions = 0;
		int numberOfAnswers = 0;
		int numberOfFiles = microtasksMappedPerFile.size();

		/* creating the excel workbook */
		//Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		//Create a blank sheet for the summary
		XSSFSheet summarySheet = workbook.createSheet("Summary");

		Integer mapKey = 0;
		// creating a map containing all the microtasks obtained from all files uploaded
		HashMap<Integer, Microtask> allMicrotasksMap = new HashMap<>();
		// keep in mind that each file is a key for a HashMap and I want to read the microtasks Map inside it
		Set<Map.Entry<String, FileDebugSession>> setMPF = microtasksMappedPerFile.entrySet();	// MPF = microtasks per File
		Iterator<Entry<String, FileDebugSession>> iMPF = setMPF.iterator();
		while(iMPF.hasNext())
		{
			Map.Entry<String, FileDebugSession> meMPF = (Map.Entry<String, FileDebugSession>)iMPF.next();
			// iterating microtasks per file
			Set<Map.Entry<Integer, Microtask>> setMPF2 = meMPF.getValue().getMicrotaskMap().entrySet();
			Iterator<Entry<Integer, Microtask>> iMPF2 = setMPF2.iterator();
			while(iMPF2.hasNext())
			{
				Map.Entry<Integer, Microtask> meMPF2 = (Map.Entry<Integer, Microtask>)iMPF2.next();
				allMicrotasksMap.put(new Integer(mapKey++), meMPF2.getValue());
			}
		}
		numberOfQuestions = allMicrotasksMap.size();

		// converting microtasks per file into microtasks per method
		HashMap<String, ArrayList<Microtask>> microtasksPerMethod = PathUtil.convertToMicrotasksPerMethod(allMicrotasksMap);
		// iterating methods
		Iterator<String> iterator = microtasksPerMethod.keySet().iterator();
		while(iterator.hasNext()){
			String methodName = iterator.next();
			ArrayList<Microtask> list = microtasksPerMethod.get(methodName);
			int answersPerMicrotask = this.maxNumberOfAnswers(list);

			//Create a blank sheet for the method
			XSSFSheet methodSheet = workbook.createSheet(methodName);

			int dataKey = 0;	// for the 'data' below
			int rownum = 0;	
			Row row;
			/* creating first header line */
			row = methodSheet.createRow(rownum++);
			int cellnum2 = 0;
			Cell cell = row.createCell(cellnum2++);
			cell.setCellValue("File Name");
			cell = row.createCell(cellnum2++);
			cell.setCellValue("ID");
			cell = row.createCell(cellnum2++);
			cell.setCellValue("Questions");
			//Create N cells for answers
			for(int j=1;j<=answersPerMicrotask;j++){
				cell = row.createCell(cellnum2++);
				cell.setCellValue("Answer-"+j);
			}

			//Create N cells for explanations
			for(int j=1;j<=answersPerMicrotask;j++){
				cell = row.createCell(cellnum2++);
				cell.setCellValue("Explanation-"+j);
			}

			//Create N cells for elapsed time
			for(int j=1;j<=answersPerMicrotask;j++){
				cell = row.createCell(cellnum2++);
				cell.setCellValue("Duration-"+j);
			}

			//Create N cells for time stamp
			for(int j=1;j<=answersPerMicrotask;j++){
				cell = row.createCell(cellnum2++);
				cell.setCellValue("Time-"+j);
			}


			// preparing the TreeMap for later fill the method sheet
			Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();

			int lastColumn = 0;		// for auto sizing later
			// iterating questions (per method)
			for (Microtask microtask : list)
			{	
				numberOfAnswers += microtask.getNumberOfAnswers();

				// preparing line (object), which index is a cell
				Object[] lineContent = new Object[(4*answersPerMicrotask)+3]; // FileName(1) + ID(1) + question(1) + explanations(size) + answers(size) + elapsed time(size) + timestamp (size)
				lineContent[0] = PathUtil.removePath(microtask.getCodeSnippet().getFileName(), true);	// FileName (cell 0)
				lineContent[1] = microtask.getID();						// ID (cell 1)
				lineContent[2] = microtask.getQuestion();				// Question (cell 2)

				Vector<Answer> answerList = microtask.getAnswerList();

				int k = 3;

				if(answerList==null || answerList.size()<0){ //Just add a line with no answers for the current microtask
					lineContent = completeEmptyCells(lineContent,k,answersPerMicrotask-microtask.getNumberOfAnswers());
				}
				else{
					//Answers
					Answer singleAnswer;
					int index=0;
					while(index<answerList.size() && index< answersPerMicrotask) {
						singleAnswer = answerList.get(index);
						lineContent[k++] = singleAnswer.getOption();		// adding answers per question
						index++;
					}
					lineContent = completeEmptyCells(lineContent,k,answersPerMicrotask-microtask.getNumberOfAnswers());

					//Explanations
					k= answersPerMicrotask+3;//Position for the Explanation data.
					index=0;
					while(index<answerList.size() && index< answersPerMicrotask) {// got some answers, now the explanation:
						singleAnswer = answerList.get(index);
						lineContent[k++] = singleAnswer.getExplanation();	// adding explanation per question	
						index++;
					}
					lineContent = completeEmptyCells(lineContent,k,answersPerMicrotask-microtask.getNumberOfAnswers());

					//Elapsed Time
					k= 2*answersPerMicrotask+3;//Position for the Elapsed time data.
					index=0;
					while(index<answerList.size() && index< answersPerMicrotask) {// got some answers, now the explanation:
						singleAnswer = answerList.get(index);
						lineContent[k++] = singleAnswer.getElapsedTime();	// adding the elapsed time
						index++;
					}
					lineContent = completeEmptyCells(lineContent,k,answersPerMicrotask-microtask.getNumberOfAnswers());

					//Time Stamp
					k= 3*answersPerMicrotask+3;//Position for the Time stamp data.
					index=0;
					while(index<answerList.size() && index< answersPerMicrotask) {// got some answers, now the explanation:
						singleAnswer = answerList.get(index);
						lineContent[k++] = singleAnswer.getTimeStamp();	// adding the elapsed time
						index++;
					}

					lineContent = completeEmptyCells(lineContent,k,answersPerMicrotask-microtask.getNumberOfAnswers());

					data.put(new Integer(dataKey++), lineContent);	// putting customized line 
					lastColumn = lastColumn < k ? k : lastColumn;	
				}
			}

			/* filling the method sheet */
			//Iterate over data and write to method sheet
			Set<Integer> keyset = data.keySet();
			for (Integer singleKey : keyset)
			{	// new row for each entry
				row = methodSheet.createRow(rownum++);
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
			//row = methodSheet.createRow(rownum++);	// blank row

			// sizing columns for method sheet
			for (int j=0; j < lastColumn; j++){
				methodSheet.autoSizeColumn(j);
			}
			methodSheet.setColumnWidth(2, 30000);
		}

		/* filling the summary sheet */
		//This data needs to be written (Object[])
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1", new Object[] {"Number of files: ", numberOfFiles});
		data.put("2", new Object[] {"Number of Snippets: ", microtasksPerMethod.size()});
		data.put("3", new Object[] {"Total number of questions: ", numberOfQuestions});
		data.put("4", new Object[] {"Total number of answers: ", numberOfAnswers});

		//Iterate over data and write to the Summary sheet
		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset)
		{
			Row row = summarySheet.createRow(rownum++);
			Object [] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr)
			{
				Cell cell = row.createCell(cellnum++);
				if(obj instanceof String)
					cell.setCellValue((String)obj);
				else if(obj instanceof Integer)
					cell.setCellValue((Integer)obj);
			}
		}
		// auto-sizing columns
		for (int columnPosition = 0; columnPosition< 5; columnPosition++) {
			summarySheet.autoSizeColumn((short) (columnPosition));
		}
		try
		{
			//Write the workbook in file system

			FileOutputStream out = new FileOutputStream(new File(this.fileName));
			workbook.write(out);
			out.flush();
			out.close();

			System.out.println("MicrotasksReport.xlsx written successfully on disk at: "+this.fileName);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Complete the empty cells until to the gapSize (so we keep content aligned)
	 * It is done at the end of the array.
	 * @param lineContent
	 * @param gapSize
	 * @return
	 */
	private Object[] completeEmptyCells(Object[] lineContent, int pos, int gapSize ) {
		for (int j=0;j<gapSize;j++){
			lineContent[pos++]="";
		}
		return lineContent;
	}
	
	private int maxNumberOfAnswers(ArrayList<Microtask> list){
		int max = 0;
		for(Microtask task: list){
			if(task.getNumberOfAnswers()>max)
				max = task.getNumberOfAnswers();
		}
		return max;
	}
	
}

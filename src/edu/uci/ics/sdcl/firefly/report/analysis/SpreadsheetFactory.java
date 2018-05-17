package edu.uci.ics.sdcl.firefly.report.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.voting.AnalysisPath;
import edu.uci.ics.sdcl.firefly.report.voting.Result;


/**
 * Create spreadsheets that calculate multiple results. The whole process consist of two steps:
 * 1- instantiate new spreadsheet based on a template
 * 2- copy the cut/filtered data from data files to a worksheet in the spreadsheet
 * 
 * @author adrianoc
 *
 */
public class SpreadsheetFactory {

	String templateName = "MultipleAnalyzes_template.xlsx";
	String templatePath;
	String templateDataWorksheet = "data";

	Double minPrecision = new Double(0.0);
	Double minRecall = new Double(0.7);
	AnalysisPath analysisPath;

	public SpreadsheetFactory(){
		this.analysisPath = AnalysisPath.getInstance(); 
		this.templatePath = analysisPath.analysisTypePath;
	}


	private boolean copyTemplateFile(String copyPath, String copyName){

		Path baseExcelPath = FileSystems.getDefault().getPath(this.templatePath, this.templateName);
		Path newFilePath = FileSystems.getDefault().getPath(copyPath,copyName);

		try {
			Files.copy(baseExcelPath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;			
		}
	}

	private int populateWorksheet(HashMap<String,ArrayList<String>> contentMap, String calculationSheetNamePath,String worksheetName){
		try{
			//Open workbook
			FileInputStream file = new FileInputStream(new File(calculationSheetNamePath));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet(worksheetName);

			int answerCount=0;
			Iterator<String> iter = contentMap.keySet().iterator();
			while(iter.hasNext()){
				String taskId = iter.next();
				Integer rowInt = new Integer(taskId) + 1;
				ArrayList<String> answerList = contentMap.get(taskId);
				Row row = sheet.getRow(rowInt.intValue());
				if(answerList!=null && answerList.size()>0){
					int columnIndex=1;//skips the first column, because it has the titles.
					for(String answer: answerList){
						Cell cell = row.getCell(columnIndex);
						cell.setCellValue(answer);
						columnIndex++;
					}
					//System.out.println("TaskID: "+ taskId+", Row: "+rowInt+", answers: "+ answerList.size());
					answerCount = answerCount + answerList.size(); 
				}
			}
			
			file.close(); //close the input.
			
			//update and close workbook
			FileOutputStream out = new FileOutputStream(new File(calculationSheetNamePath));
			workbook.write(out);
			out.flush();
			out.close();
			
			return answerCount;
		}
		catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}

	/** Reads the Panel worksheet from the template spreadsheet */
	public Result readStrengthSignalResults(String spreadsheetFileNamePath,String worksheetName){

		try{
			//columnList = {"E", "J", "O", "T", "Y", "AD", "AI", "AN", "AS", "AX"};
			Integer[] columnList = {4, 9, 14, 19, 24, 29, 34, 39, 44, 49}; 
			Integer[] recallRowList = {2, 5, 8, 11};

			Double precision=0.0;
			Double recall=0.0;
			Double auxPrecision=0.0;
			Double auxRecall=0.0;

			FileInputStream file = new FileInputStream(new File(spreadsheetFileNamePath));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet(worksheetName);
			
			for(Integer column: columnList){
				for(Integer row: recallRowList){
					auxRecall = getCellValue(column, row, sheet);
					if(auxRecall.doubleValue()>=this.minRecall.doubleValue()){
						auxPrecision = getCellValue(column, row-1, sheet);
						if((auxPrecision.doubleValue()>precision.doubleValue()) || 
								(auxPrecision.doubleValue()==precision.doubleValue() && auxRecall.doubleValue()>recall.doubleValue())){
							precision = auxPrecision;
							recall = auxRecall;
						}
					}
				}
			}
			//close
			file.close();
			
			Result result = new Result();
			result.precision = precision;
			result.recall = recall;
			return result;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/** Reads the MajorityVoting worksheet from the template spreadsheet */
	public Result readMajorityVotingResults(String spreadsheetFileNamePath,String worksheetName){

		try{
		Integer column = 5;
		Integer precisionRow = 1;
		Integer recallRow = 2;
		
		FileInputStream file = new FileInputStream(new File(spreadsheetFileNamePath));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheet(worksheetName);

		Result result = new Result();
		result.precision = getCellValue(column, precisionRow, sheet);
		result.recall = getCellValue(column, recallRow, sheet);
		file.close();
		return result;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


	private Double getCellValue(Integer columnInt, Integer rowInt, XSSFSheet sheet){

		try{
			//System.out.println("row:"+rowInt.toString()+", column:"+columnInt.toString());
			Double cellValue = 0.0;
			
			Row row = sheet.getRow(rowInt.intValue());
			Cell cell = row.getCell(columnInt.intValue());
			double cellValueStr = cell.getNumericCellValue();//getCellFormula();
			//System.out.println("cellValue: "+ cellValueStr+"at row:"+rowInt.toString()+", column:"+columnInt.toString());
			cellValue = new Double(cellValueStr);

			return cellValue;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public boolean writeResult(String reportFileNamePath, String worksheetName, Integer initialRow, Integer initialColumn, ArrayList<ArrayList<Double>> resultMatrix){

		try{
			//Open workbook
			FileInputStream file = new FileInputStream(new File(reportFileNamePath));      
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet(worksheetName);

			int rowIndex = initialRow.intValue();
			for(ArrayList<Double> line: resultMatrix){
				int columnIndex = initialColumn.intValue();
				for(Double result: line){
					Row row = sheet.getRow(rowIndex);
					Cell cell = row.getCell(columnIndex);
					cell.setCellValue(result);
					columnIndex++;
				}
				rowIndex++;
			}       

			file.close();
			
			//update and close workbook
			FileOutputStream out = new FileOutputStream(new File(reportFileNamePath));
			workbook.write(out);
			out.flush();
			out.close();
			
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public void calculateResults(String fileName, FilterContent content){
		if(this.copyTemplateFile(analysisPath.calculationsPath, fileName)){
			int answerCount = this.populateWorksheet(content.answerMap, analysisPath.calculationsPath+fileName, this.templateDataWorksheet);
			if(answerCount!=content.validAnswers){
				System.out.println("ERROR: wrong number of anwers written in spreadsheet "+fileName);
				System.out.println("Expected:"+content.validAnswers+", actual: "+answerCount);
			}
		}	
	}


	public static void main(String[] args){
		SpreadsheetFactory factory = new SpreadsheetFactory();
		factory.testWriteResults();
	}	
		
	public void testCalculateResults(){
		AnalysisPath analysisPath = AnalysisPath.getInstance();
		SpreadsheetFactory factory = new SpreadsheetFactory();

		factory.copyTemplateFile(analysisPath.analysisTypePath, "copy.xlsx");

		CutFilter cutFilter = new CutFilter();

		HashMap<String, Microtask> cutMap = cutFilter.filterCutMicrotasks(10,1);
		FilterContent content = cutFilter.writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(cutMap, new Double(0.0), Double.MAX_VALUE, 2, 5, 11, -1);

		factory.calculateResults("copy.xlsx", content);
	}

	
	public void testWriteResults(){
		AnalysisPath analysisPath = AnalysisPath.getInstance();
		SpreadsheetFactory factory = new SpreadsheetFactory();
	
		FilterContent content = new FilterContent(0.0,1,-1);
		content.majorityResult = factory.readMajorityVotingResults(analysisPath.calculationsPath+"copy.xlsx", "MajorityVoting");
		content.strengthSignalResult = factory.readStrengthSignalResults(analysisPath.calculationsPath+"copy.xlsx", "Panel");	
		
		System.out.println("majority precision:recall= "+ content.majorityResult.precision.toString()+":"+content.majorityResult.recall.toString());
		System.out.println("strengthSignal precision:recall= "+ content.strengthSignalResult.precision.toString()+":"+content.strengthSignalResult.recall.toString());
		
		ArrayList<FilterContent> contentList = new ArrayList<FilterContent>();
		contentList.add(content);
		
		ArrayList<ArrayList<Double>> reportTable = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> line = new ArrayList<Double>();
		//String durationStr = content.minimalDuration.toString();
		line.add(content.minimalDuration/1000);
		//String workerScoreStr = content.convertWorkerScore();
		line.add(new Double(content.workerScore.doubleValue()));
		//String ictStr = content.maxICantTell.toString();
		line.add(new Double(content.maxICantTell.doubleValue()));
		
		line.add(new Double(2089));//content.validAnswers.toString());
		line.add(new Double(522));//content.activeWorkers.toString());
		
		line.add(content.majorityResult.precision);
		line.add(content.majorityResult.recall);
		line.add(content.strengthSignalResult.precision);
		line.add(content.strengthSignalResult.recall);
		
		reportTable.add(line);
		
		factory.writeResult(analysisPath.analysisTypePath+"Results.xlsx", "Results", new Integer(5), new Integer(1), reportTable);
	}

}

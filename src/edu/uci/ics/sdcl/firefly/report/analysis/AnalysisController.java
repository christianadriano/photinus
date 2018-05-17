package edu.uci.ics.sdcl.firefly.report.analysis;

import java.util.ArrayList;
import java.util.HashMap;



import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.report.voting.AnalysisPath;
import edu.uci.ics.sdcl.firefly.storage.FilterContentStorage;

public class AnalysisController {

	ArrayList<ArrayList<String>> resultsMatix; 
	CutFilter cutFilter;

	ArrayList<FilterContent> filterContentList;
	AnalysisPath analysisPath;

	String reportFileName = "Results.xlsx";

	public AnalysisController(){
		this.analysisPath = AnalysisPath.getInstance();
		resultsMatix = new ArrayList<ArrayList<String>>();
		filterContentList = new ArrayList<FilterContent>(); 
		cutFilter = new CutFilter();
	}

	

	public void run(){	
		Double maxDuration = new Double(Double.MAX_VALUE);
		Integer[] durationList = {0,10,15,20,30,45,60,120}; //Minimal duration to be considered
		Integer[] scoreList = {2,3,4};  //Minimal Score to be considered		
		Integer[] idkList = {2,4,6,8,10,11}; //I can't tell answer count that would eliminate workers
		Integer lowerCut_idk = -1;  //Worker that has an equal amount below will be cut out of the set.
		Integer maxScore=5; //Worker has to have grade below that.
		int i=0;

		filterContentList = new ArrayList<FilterContent>(); 		
		HashMap<String, Microtask> cutMap = cutFilter.filterCutMicrotasks(10,1); //cut level, score level
		SpreadsheetFactory factory = new SpreadsheetFactory();

		while(i<durationList.length){
			int j=0;
			Double minDuration = new Double(durationList[i].doubleValue()*1000);
			while(j<scoreList.length){
				int k=0;
				while(k<idkList.length){
					FilterContent content = cutFilter.writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(cutMap, minDuration, maxDuration,
							scoreList[j],maxScore,
							idkList[k],lowerCut_idk);

					String fileName = content.getFilterName() + ".xlsx";
					content.fileName = fileName;
					factory.calculateResults(fileName, content);
					filterContentList.add(content);
					k++;
				}
				j++;
			}
			i++;
		}
		FilterContentStorage storage = new FilterContentStorage();
		storage.write(filterContentList);
		System.out.println("files written, look at: "+analysisPath.calculationsPath);
	}

	public void generateReport(){
		FilterContentStorage storage = new FilterContentStorage();
		filterContentList = storage.read();
		if(this.filterContentList==null || this.filterContentList.size()==0){
			System.out.println("ERROR: there aren't spreadsheets to process the report. Check path: "+analysisPath.calculationsPath);
		}
		else{
			SpreadsheetFactory factory = new SpreadsheetFactory();
			//collects all the precision and recall generate in the results tabs of the calculation spreadsheets
			for(int i=0; i<this.filterContentList.size();i++){
				FilterContent content = this.filterContentList.get(i);
				content.majorityResult = factory.readMajorityVotingResults(analysisPath.calculationsPath+content.fileName, "MajorityVoting");
				content.strengthSignalResult = factory.readStrengthSignalResults(analysisPath.calculationsPath+content.fileName, "Panel");	
				this.filterContentList.set(i,content);
				System.out.println("content obtained from: "+content.fileName);
			}
			ArrayList<ArrayList<Double>> reportTable = generateReportTable();
			factory.writeResult(analysisPath.analysisTypePath+this.reportFileName, "Results", new Integer(5), new Integer (1), reportTable);
			System.out.println("Report written, look at: "+analysisPath.analysisTypePath+this.reportFileName);
		}
	}

	private ArrayList<ArrayList<Double>> generateReportTable(){
		ArrayList<ArrayList<Double>> reportTable = new ArrayList<ArrayList<Double>>();

		for(FilterContent content : this.filterContentList){

			ArrayList<Double> line = new ArrayList<Double>();

			Double seconds = content.minimalDuration/1000;
			//String durationStr = seconds.toString();
			//durationStr = durationStr.substring(0, durationStr.length()-2);//remove the .0
			line.add(seconds);
			//String workerScoreStr = content.convertWorkerScore();
			line.add(new Double(content.convertWorkerScore()));
			//String ictStr = content.maxICantTell.toString();
			line.add(new Double(content.maxICantTell.doubleValue()));


			line.add(content.validAnswers.doubleValue());
			line.add(content.activeWorkers.doubleValue());

			line.add(content.majorityResult.precision);
			line.add(content.majorityResult.recall);
			line.add(content.strengthSignalResult.precision);
			line.add(content.strengthSignalResult.recall);

			reportTable.add(line);
		}
		return reportTable;
	}


	/** How to run.
	 * 1.call run().
	 * 2.open spreadsheets to be calculated
	 * 3.call generatedReport()
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		AnalysisController controller = new AnalysisController();
		//controller.run(); //first
		controller.generateReport();  //second
	}

	/**
	 * Test method to check whether quantities match the expected.
	 */
	public void testCutFilter(){
		HashMap<String, Microtask> cutMap = cutFilter.filterCutMicrotasks(17, 2);
		cutFilter.writeAnswerLabels_Filtered_by_DURATION_GRADE_IDK(cutMap, new Double(10000), Double.MAX_VALUE, 3, 5, 2, -1);
		cutFilter.printActiveWorkerMap();
	}

}

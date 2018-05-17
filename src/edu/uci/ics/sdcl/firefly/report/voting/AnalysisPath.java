package edu.uci.ics.sdcl.firefly.report.voting;

public class AnalysisPath {

	public  String samsungPath = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	public  String dellPath = "C:\\Users\\Christian Adriano\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\";
	public  String currentPath = dellPath;
	
	public  String rawDataLogPath; //the raw data
	public  String calculationsPath; //the spreadsheets with formulas
	public  String analysisTypePath; //the results and the template spreadsheet
	
	private static AnalysisPath analysisPath;
	
	private AnalysisPath(){
		this.rawDataLogPath = currentPath + "\\RawDataLogs\\";
		this.analysisTypePath = currentPath + "\\1.DataAnalysis(CutFirst)\\";
		this.calculationsPath = this.analysisTypePath + "\\Calculations\\";
	}
	
	public static AnalysisPath getInstance(){
		if(analysisPath==null)
			analysisPath =  new AnalysisPath();
		return analysisPath;
	}
	
}

package edu.uci.ics.sdcl.firefly.report.descriptive;


public class ReportApplication {

	private DescriptiveReportBuilder builder;
	private FilterCombination filter;
	private DescriptiveReportWriter exporter;
	
	public static void main(String[] args) {

		ReportApplication application = new ReportApplication(new FilterCombination(),new ExcelExporter());
		application.runAnswerOptionReport(); //Instantiates with an empty (no effect) filter
		
		//workers demographics is different
		//application.workerDemographicsReport();
	}
	
	
	public ReportApplication(FilterCombination filter, DescriptiveReportWriter exporter)
	{
		this.filter = filter;
		this.exporter = exporter;
	}
	
	public void runAnswerOptionReport()
	{
		builder = new DescriptiveReportBuilder(new AnswerOption(), null , new RegularCorrectness(), exporter, filter);
		builder.generateDescriptiveReport().exportReport();
	}
	
	public void runConfidenceLevelReport()
	{
		builder = new DescriptiveReportBuilder(new AnswerConfidence(), new ConfidenceCounting(), new RegularCorrectness(), exporter, filter);
		builder.generateDescriptiveReport().exportReport();
	}
	
	public void runDifficultyLevelReport()
	{
		builder = new DescriptiveReportBuilder(new AnswerDifficulty(), new ConfidenceCounting(), new RegularCorrectness(), exporter,filter);
		builder.generateDescriptiveReport().exportReport();
	}
	
	public void runAnswerDurationReport()
	{
		builder = new DescriptiveReportBuilder(new AnswerDuration(), new AverageCount(), new AverageCorrectness(), exporter, filter);
		builder.generateDescriptiveReport().exportReport();
	}
	
	public void runSizeOfExplanationReport()
	{
		builder = new DescriptiveReportBuilder(new AnswerExplanationSize(), new AverageCount(), new AverageCorrectness(), exporter, filter);
		builder.generateDescriptiveReport().exportReport();
	}
	
	public void runDurationOfAnswersAgroupedByOrder()
	{
		builder = new DescriptiveReportBuilder(new OrderDuration(), new AverageCount(), new AverageCorrectness(), exporter, filter);
		builder.generateDescriptiveReport().exportReport();
	}
	
	public void runSessionDurationReport()
	{
		builder = new DescriptiveReportBuilder(new SessionDuration(), new AverageCount(), new AverageCorrectness(), exporter, filter);
		builder.generateDescriptiveReport().exportReport();
	}
	
	public void runWorkerScoreReport()
	{
		builder = new DescriptiveReportBuilder(new WorkerScoreReport(), new AverageWorkerProfileReport(), new AverageCorrectness(), exporter, filter);
		builder.generateDescriptiveReport().exportReport();
	}
	
	public void workerDemographicsReport()
	{
		new WorkerDemographicsReport();
	}
	
}

package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.sdcl.firefly.Microtask;


/**
 * Provides an interface for different builders.
 * The reports have similarities on the structure,
 * but their content is different, so the parts construction
 * are separated and permitted to vary on concrete implementations
 * of this class.
 *
 * @author igMoreira 
 * @author adrianoc
 *
 */
public class DescriptiveReportBuilder {

	private HeaderReport header;
	private AnswerReport answers;
	private CountReport counters;
	private CorrectnessReport correctness;
	private DescriptiveReportWriter exporter;
	private FilterCombination filter;
	String reportName = "";
	
	public DescriptiveReportBuilder(AnswerReport answer, CountReport counter, CorrectnessReport correctness, DescriptiveReportWriter exporter, FilterCombination filter) {
		this.answers = answer;
		this.counters = counter;
		this.correctness = correctness;
		this.exporter = exporter;
		this.filter = filter;
	}
	
	public void setAnswerReport(AnswerReport answers) {
		this.answers = answers;
	}

	public void setCounterReport(CountReport counters) {
		this.counters = counters;
	}

	public void setCorrectnessReport(CorrectnessReport correctness) {
		this.correctness = correctness;
	}

	/**
	 * Responsible for building the HeaderReport,
	 * the blue part of the tables. 
	 * @return: The HeaderReport containing all the necessary data.
	 */
	private Map<String, List<String>> buildHeaderReport()
	{
		SessionDTO database = new FileSessionDTO();
		this.header = new HeaderReport(this.filter.apply((HashMap<String, Microtask>) database.getMicrotasks()));
		return header.generateReport();
	}
	
	/**
	 * Responsible for building the AnswerReport,
	 * the green parts of the tables.
	 * @param filter 
	 * @return: The AnswerReport containing the filtered data for the report type X
	 */
	private Map<String, List<String>> buildAnswerReport(Map<String, List<String>> content, FilterCombination filter)
	{
		return answers.generateReport(content,filter);
	}
	
	/**
	 * Responsible for building the CountReport,
	 * the yellow parts of the tables.
	 * @return: The CountReport containing the filtered data for the report type X
	 */
	private Map<String, List<String>> buildCountReport(HeaderReport header, AnswerReport answers, FilterCombination filter) {
		return counters.generateReport(header,answers, filter);
	}
	
	/**
	 * Responsible for building the CorrectnessReport,
	 * the orange parts of the tables.
	 * @return: The CorrectnessReport containing the filtered data for the report type X
	 */
	private Map<String, List<String>> buildCorrectnessReport(HeaderReport headerReport, AnswerReport answerReport, FilterCombination filter)
	{
		return this.correctness.generateReport(headerReport, answerReport, filter);
	}
	
	/**
	 * This is the service in which will provide the desired
	 * DescritiveReport for a external agent.
	 * @return: The built DescriptveReport with all parts assembled.
	 */
	public DescriptiveReport generateDescriptiveReport()
	{
		Map<String, List<String>> headerContent = buildHeaderReport();
		buildAnswerReport(headerContent,filter);
		if(this.counters != null)
		{
			buildCountReport(header,answers, filter);
		}
		buildCorrectnessReport(header,answers,filter);
		exporter.setReportName(reportName);
		DescriptiveReport report = new DescriptiveReport(this.header, this.answers, this.counters, this.correctness, exporter);
		return report;
	}

}

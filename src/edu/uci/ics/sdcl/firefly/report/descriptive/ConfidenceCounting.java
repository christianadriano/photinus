package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;

/**
 * 
 * @author igMoreira
 * 
 * Counting part of the report, offers the
 * number of answers grouped by confidence
 * level of a given question.
 *
 */
public class ConfidenceCounting extends CountReport {

	/**
	 * Generates the CountReport content part. 
	 */
	@Override
	public Map<String, List<String>> generateReport( HeaderReport headerReport, AnswerReport answerReport, FilterCombination filter) {
		Map<String, List<String>> content = mergeContent(headerReport, answerReport);
		List<String> questionIDList = content.get("Question ID");
		List<List<String>> confidenceList = new ArrayList<List<String>>();
		for (int i = 0; i < 6; i++) {
			confidenceList.add(new ArrayList<String>());
		}
		if(questionIDList != null)
		{
			SessionDTO dto = new FileSessionDTO();
			Map<String, Microtask> microtasks = filter.apply((HashMap<String, Microtask>) dto.getMicrotasks());
			int[] confidence = null;
			for (String questionID : questionIDList) {
				confidence = new int[6];
				Arrays.fill(confidence, 0);
				Microtask question = microtasks.get(questionID);
				for (Answer answer : question.getAnswerList()) {
					confidence[answer.getConfidenceOption()]++;
				}
				for (int i = 0; i < confidence.length; i++) {
					confidenceList.get(i).add(Integer.toString(confidence[i]));
				}
			}
			for (int i = 0; i < confidenceList.size(); i++) {
				this.countContent.put("# Confidence level - "+i, confidenceList.get(i));
			}
		}
		else
		{
			System.out.println("Invalid question ID came from the HeaderReport");
			System.exit(0);
		}
		return this.countContent;
	}

	/**
	 * Returns the type of the report,
	 * this will be used on the ReportWriter
	 * to build the titles.
	 */
	@Override
	public String getType() {
		return "Derived Data - Counting";
	}
	
	private Map<String, List<String>> mergeContent(HeaderReport headerReport, AnswerReport answerReport)
	{
		Map<String, List<String>> content = new LinkedHashMap<String, List<String>>();
		content.putAll(headerReport.getContent());
		content.putAll(answerReport.getContent());
		return content;
	}

}

package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;


/**
 * This class holds a part of the DescriptiveReport,
 * this will be the part will which will vary most in
 * content. This is the GREEN part of the tables.
 * 
 * @author igMoreira
 *
 */
public abstract class AnswerReport {
	
	protected final int NUMBER_OF_ANSWERS;
	
	/**
	 * Holds the content of the GREEN part of the report
	 */
	protected Map<String, List<String>> answerContent = new LinkedHashMap<String, List<String>>();
	
	public AnswerReport() {
		PropertyManager properties = PropertyManager.initializeSingleton();
		this.NUMBER_OF_ANSWERS = properties.answersPerMicrotask;
	}
	
	/**
	 * Filters the content and returns the HashMap containing
	 * the Headers and the contents of the table.
	 * @param filter 
	 * @return: The map containing the columns and values of the table
	 */
	public Map<String, List<String>> generateReport( Map<String, List<String>> content, FilterCombination filter)
	{
		SessionDTO database = new FileSessionDTO();
		Map<String, Microtask> microtasks = filter.apply((HashMap<String, Microtask>) database.getMicrotasks());
		List<String> questionIDList = content.get("Question ID"); // this is the data that came form the HeaderReport

		if(questionIDList != null) // auto defense, just to make sure I don't get a null pointer exception
		{
			List<String> answers = null;
			for (int i = 0; i < NUMBER_OF_ANSWERS; i++) { // The maximum number of answers showed on the report
				answers = new ArrayList<String>();
				for (String questionID : questionIDList) { // for each microtask get the answer number i
					Microtask microtask = microtasks.get(questionID);
					List<Answer> answerList = microtask.getAnswerList();
					if((answerList.size()-1) >= i)
					{
						Answer answer = microtask.getAnswerList().get(i);
						answers.add(reportData(answer));
					}
					else
					{
						answers.add("");
					}
				}
				this.answerContent.put("Answer "+(i+1), answers);
			}
		}
		else
		{
			System.out.println("Invalid question ID came from the HeaderReport");
			System.exit(0);
		}
		return this.answerContent;
	}
	
	public Map<String, List<String>> getContent() {
		return answerContent;
	}

	public abstract String getType();
	
	public abstract String getDataNature();
	
	protected abstract String reportData(Answer answer);
	
}

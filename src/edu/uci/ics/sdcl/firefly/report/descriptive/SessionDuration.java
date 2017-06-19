package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.WorkerSession;

public class SessionDuration extends AnswerReport {
	private Map<String, Integer> sessionDuration = new HashMap<String, Integer>();

	@Override
	public String getType() {
		return "Session duration (in seconds)";
	}

	@Override
	public Map<String, List<String>> generateReport( Map<String, List<String>> content, FilterCombination filter) {
		calculateSessionsDuration(filter);

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
					if((answerList.size()-1) >= i) // IF THE MICROTASK HAS NOT GOT AN Ith ANSWER
					{
						Answer answer = answerList.get(i);
						answers.add(sessionDuration.get((microtask.getID().toString() + ":" + answer.getWorkerId())).toString());
					}
					else
						answers.add("");
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

	@Override
	protected String reportData(Answer answer) {
		Integer test = sessionDuration.get(answer.getTimeStamp());
		return test.toString();
	}

	private void calculateSessionsDuration(FilterCombination filter)
	{
		FileSessionDTO dto = new FileSessionDTO();
		Map<String, WorkerSession> sessions = dto.getSessions();
		for (WorkerSession session : sessions.values()) {
			int total = 0;
			for (Microtask question : session.getMicrotaskList()) {
				if(filter.validMicrotask(question)){
					Answer answer = question.getAnswerList().firstElement();
					String firstNumber = answer.getElapsedTime().replaceFirst(".*?(\\d+).*", "$1");
					total += (Integer.valueOf( firstNumber ) / 1000 );
				}
			}
			for (Microtask question : session.getMicrotaskList()) {
				if(filter.validMicrotask(question)){
					Answer answer = question.getAnswerList().firstElement();
					sessionDuration.put((question.getID().toString() + ":" + answer.getWorkerId()), total );
				}
			}
		}
	}

	@Override
	public String getDataNature() {
		return "Duration";
	}

}

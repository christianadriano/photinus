package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;

public class RegularCorrectness extends CorrectnessReport {

	public RegularCorrectness() {
		super();
	}

	@Override
	public Map<String, List<String>> generateReport(HeaderReport headerReport, AnswerReport answerReport, FilterCombination filter) {
		Map<String,List<String>> headerContent = headerReport.getContent();
		List<String> questions = headerContent.get("Question ID");
		List<String> bugCoveringList = headerContent.get("Bug Covering");
		
		SessionDTO dto = new FileSessionDTO();
		Map<String, Microtask> microtasks = filter.apply((HashMap<String, Microtask>) dto.getMicrotasks());
		
		List<String> truePositive = new ArrayList<String>();
		List<String> falsePositive = new ArrayList<String>();
		List<String> trueNegative = new ArrayList<String>();
		List<String> falseNegative = new ArrayList<String>();
		List<String> idk = new ArrayList<String>();
		
		int[] aux = new int[5];
		
		for (int i=0; i< questions.size(); i++) {
			Arrays.fill(aux, 0);
			Microtask microtask = microtasks.get(questions.get(i));
			String covering = bugCoveringList.get(i);
			for (Answer answer : microtask.getAnswerList()) {
				if(answer.getOption().equals(Answer.YES))
				{
					if(covering.equals("yes"))
						aux[0]++;
					else
						aux[2]++;
				}
				else if(answer.getOption().equals(Answer.I_DONT_KNOW))
					aux[4]++;
				else
				{
					if(covering.equals("no"))
						aux[1]++;
					else
						aux[3]++;
				}
			}
			truePositive.add(Integer.toString(aux[0]));
			trueNegative.add(Integer.toString(aux[1]));
			falsePositive.add(Integer.toString(aux[2]));
			falseNegative.add(Integer.toString(aux[3]));
			idk.add(Integer.toString(aux[4]));
		}
		this.correctnessContent.put("True Positives", truePositive);
		this.correctnessContent.put("True Negatives", trueNegative);
		this.correctnessContent.put("False Positives", falsePositive);
		this.correctnessContent.put("False Negatives", falseNegative);
		this.correctnessContent.put("IDK", idk);
		
		return this.correctnessContent;
	}

	@Override
	public String getType() {
		return "Categorizing";
	}

}

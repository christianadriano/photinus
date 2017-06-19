package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class AverageCorrectness extends CorrectnessReport {
	private final int NUMBER_OF_QUESTIONS;
	private final int QUESTIONS_PER_SESSION;

	public AverageCorrectness() {
		PropertyManager property = PropertyManager.initializeSingleton();
		this.NUMBER_OF_QUESTIONS = property.answersPerMicrotask;
		this.QUESTIONS_PER_SESSION = property.microtasksPerSession;
	}

	@Override
	public Map<String, List<String>> generateReport(HeaderReport headerReport, AnswerReport answerReport, FilterCombination filter) {

		Map<String,List<String>> headerContent = headerReport.getContent();
		Map<String, List<String>>  answerContent = answerReport.getContent();
		SessionDTO database = new FileSessionDTO();
		Map<String, Microtask> microtasks = filter.apply((HashMap<String, Microtask>) database.getMicrotasks());
		List<String> questionIDList = headerContent.get("Question ID"); // this is the data that came form the HeaderReport
		List<String> averageTP = new ArrayList<String>();
		List<String> averageTN = new ArrayList<String>();
		List<String> averageFP = new ArrayList<String>();
		List<String> averageFN = new ArrayList<String>();
		List<String> averageIDK = new ArrayList<String>();

		for (int i =0; i< questionIDList.size(); i++) {
			double truePositiveAverage = 0;
			double trueNegativeAverage = 0;
			double falsePositiveAverage = 0;
			double falseNegativeAverage = 0;
			double idkAverage = 0;
			int truePositiveSize = 0;
			int trueNegativeSize = 0;
			int falsePositiveSize = 0;
			int falseNegativeSize = 0;
			int idkSize = 0;

			Microtask question = microtasks.get(questionIDList.get(i));
			List<Answer> answerList = question.getAnswerList();
			Iterator<Entry<String, List<String>>> it = answerContent.entrySet().iterator();
			int answersLimitSize = (answerReport.getContent().size() == QUESTIONS_PER_SESSION) ? QUESTIONS_PER_SESSION : NUMBER_OF_QUESTIONS;
			for (int j =0; j < answersLimitSize; j++){
				if((answerList.size()-1) >= j)
				{
					String text = (it.next().getValue().get(i));
					if(text==null)
						System.out.println("null text for:"+ questionIDList.get(i));
					else{
						String answerOption = answerList.get(j).getOption();
						boolean bugCovering = isBugCovering(Integer.valueOf(questionIDList.get(i)));
						if(answerOption!=null){
							if (bugCovering && answerOption.equals(Answer.YES)) {
								truePositiveAverage+= (text.equals("") ? 0 : Double.valueOf(text));
								truePositiveSize++;
							} else if (!bugCovering && answerOption.equals(Answer.NO)) {
								trueNegativeAverage+= (text.equals("") ? 0 : Double.valueOf(text));
								trueNegativeSize++;
							} else if (!bugCovering && answerOption.equals(Answer.YES)) {
								falsePositiveAverage+= (text.equals("") ? 0 : Double.valueOf(text));
								falsePositiveSize++;
							} else if (bugCovering && answerOption.equals(Answer.NO)) {
								falseNegativeAverage+= (text.equals("") ? 0 : Double.valueOf(text));
								falseNegativeSize++;
							} else {
								idkAverage += (text.equals("") ? 0 : Double.valueOf(text));
								idkSize++;
							}
						}
					}
				}
			}
			if(truePositiveSize != 0)
				truePositiveAverage /= truePositiveSize;
			if(trueNegativeSize != 0)
				trueNegativeAverage /= trueNegativeSize;
			if(falsePositiveSize != 0)
				falsePositiveAverage /= falsePositiveSize;
			if(falseNegativeSize != 0)
				falseNegativeAverage /= falseNegativeSize;
			if(idkSize != 0)
				idkAverage /= idkSize;

			averageTP.add(String.format("%.2f",((float) truePositiveAverage)));
			averageTN.add(String.format("%.2f",((float) trueNegativeAverage)));
			averageFP.add(String.format("%.2f",((float)falsePositiveAverage)));
			averageFN.add(String.format("%.2f",((float) falseNegativeAverage)));
			averageIDK.add(String.format("%.2f",((float) idkAverage)));
		}

		this.correctnessContent.put("Average "+ answerReport.getDataNature() +" of True Positives", averageTP);
		this.correctnessContent.put("Average "+ answerReport.getDataNature() +" of True Negatives", averageTN);
		this.correctnessContent.put("Average "+ answerReport.getDataNature() +" of False Positives", averageFP);
		this.correctnessContent.put("Average "+ answerReport.getDataNature() +" of False Negatives", averageFN);
		this.correctnessContent.put("Average "+ answerReport.getDataNature() +" of IDKs", averageIDK);

		return correctnessContent;
	}

	public boolean isBugCovering(Integer microtaskID){
		PropertyManager manager = PropertyManager.initializeSingleton();
		String[] bugCoveringQuestionList = manager.bugCoveringList.split(";");
		
		for(int i=0; i<bugCoveringQuestionList.length; i++){
			if(microtaskID.toString().compareTo(bugCoveringQuestionList[i])==0){
				return true;
			}
		}
		return false;
	}

	public String getType() {
		return "Correct/Wrong Answers";
	}

}

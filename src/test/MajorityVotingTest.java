package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.report.predictive.AcrossQuestionsConsensus;
import edu.uci.ics.sdcl.firefly.report.predictive.AnswerData;
import edu.uci.ics.sdcl.firefly.report.predictive.WithinQuestionConsensus;

public class MajorityVotingTest {

	HashMap<String, ArrayList<String>> answerMap = new HashMap<String, ArrayList<String>>();
	HashMap<String,String> bugCoveringMap = new HashMap<String,String>();

	AnswerData data;
	
	public void setup1(){
		
		bugCoveringMap.put("1","1");//1 yes
		bugCoveringMap.put("3","3");//4 yes's

		ArrayList<String> answerList = new ArrayList<String>();//2 yes's False Positive
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add("IDK");
		answerList.add(Answer.YES);
		answerMap.put("0",answerList);

		answerList = new ArrayList<String>();//1 yes False Negative
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("1",answerList);

		answerList = new ArrayList<String>();//2 yes's True Negative
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("2",answerList);

		answerList = new ArrayList<String>();//4 yes's True positive
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerMap.put("3",answerList);

		String hitFileName = "HIT00_0";

		data = new AnswerData(hitFileName,answerMap,bugCoveringMap,4,4);		
	}
	
	@Test
	public void testComputeSignal_FirstThreshold() {
		this.setup1();
		WithinQuestionConsensus predictor = new WithinQuestionConsensus();
		assertTrue(predictor.computeSignal(this.data));	
		
		assertEquals(1, predictor.getFalsePositives().intValue());
		assertEquals(1, predictor.getTruePositives().intValue());
		assertEquals(1, predictor.getFalseNegatives().intValue());
		assertEquals(1, predictor.getTrueNegatives().intValue());
		
		double extraVote = 3;
		double rateOfTP =0.5;
		double expectedSignalStrength = extraVote* rateOfTP;
		System.out.println(predictor.computeSignalStrength(data).doubleValue());
		assertEquals("Signal Strength", expectedSignalStrength, predictor.computeSignalStrength(data).doubleValue(),0.0);
		
		//Test for number of workers
		assertEquals("Workers", 4, predictor.computeNumberOfWorkers(data).intValue());
	}
	
	
	
	
	public void setup2(){
		
		bugCoveringMap.put("0","0");//2 yes's
		bugCoveringMap.put("1","1");//1 yes

		ArrayList<String> answerList = new ArrayList<String>();//2 yes's True Positive
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add("IDK");
		answerList.add(Answer.YES);
		answerMap.put("0",answerList);

		answerList = new ArrayList<String>();//1 yes False Negative
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("1",answerList);

		answerList = new ArrayList<String>();//2 yes's True Negative
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("2",answerList);

		answerList = new ArrayList<String>();//4 yes's False Positive
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerMap.put("3",answerList);

		String hitFileName = "HIT00_0";

		data = new AnswerData(hitFileName,answerMap,bugCoveringMap,4,4);		
	}
	
	
	@Test
	public void testComputeSignal_SecondThreshold() {
		this.setup2();
		WithinQuestionConsensus predictor = new WithinQuestionConsensus();
		
		assertTrue(predictor.computeSignal(this.data));
		
		assertEquals(1, predictor.getFalsePositives().intValue());
		assertEquals(1, predictor.getTruePositives().intValue());
		assertEquals(1, predictor.getFalseNegatives().intValue());
		assertEquals(1, predictor.getTrueNegatives().intValue());
		
		double extraVote = 0.0;
		double rateOfTP = 0.5;
		double expectedSignalStrength = extraVote* rateOfTP;
		assertEquals("Signal Strength",expectedSignalStrength, predictor.computeSignalStrength(data).doubleValue(),0.0);
		
		//Test for number of workers
		assertEquals("Workers", 4, predictor.computeNumberOfWorkers(data).intValue());
	}

}

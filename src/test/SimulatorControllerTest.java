package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test; 




import edu.uci.ics.sdcl.firefly.report.predictive.AttributeRangeGenerator;
import edu.uci.ics.sdcl.firefly.report.predictive.SubCrowd;
import edu.uci.ics.sdcl.firefly.report.predictive.montecarlo.*;

public class SimulatorControllerTest {

	HashMap<String, Integer> numberWorkersPerCrowd = new HashMap<String, Integer>();
	HashMap<String, Integer> numberMaxAnswersPerCrowd = new HashMap<String, Integer>();
	
	@Before
	public void setup(){

		//Setup number of workers expected 
		numberWorkersPerCrowd.put(AttributeRangeGenerator.WORKER_SCORE_100_NON_STUDENT, 133);
		numberWorkersPerCrowd.put(AttributeRangeGenerator.WORKER_NON_STUDENT, 316);
		numberWorkersPerCrowd.put(AttributeRangeGenerator.WORKER_SCORE_100, 194);
		numberWorkersPerCrowd.put(AttributeRangeGenerator.WORKER_SCORE_100_80, 351);
		numberWorkersPerCrowd.put(AttributeRangeGenerator.WORKER_SCORE_100_80_NON_STUDENT, 231);
		numberWorkersPerCrowd.put(AttributeRangeGenerator.ANSWER_DURATION_MIN_q1_q1, 445);
		numberWorkersPerCrowd.put(AttributeRangeGenerator.CONFIDENCE_DIFFICULTY_UP_3_PERCENT, 412);
		numberWorkersPerCrowd.put(AttributeRangeGenerator.EXPLANATION_2_3_4_QT_57_2383, 412);
		numberWorkersPerCrowd.put(AttributeRangeGenerator.CONDITIONAL_CLAUSE_ABOVE_3LINES, 494);
		numberWorkersPerCrowd.put(AttributeRangeGenerator.NO_FILTERS, 497);
	}

	@Test
	public void testMicrotaskFiltering(){
		
		setup();
		//Test filter generation
		SimulationController controller = new SimulationController();

		ArrayList<SubCrowd> list = controller.generateSubCrowdFilters();
		list = controller.generateSubCrowdMicrotasks(list);


		for(SubCrowd crowd : list){
			String filterName = crowd.name;
			Integer actualNumberWorkers = crowd.totalWorkers;
			Integer expectedNumberWorkers = this.numberWorkersPerCrowd.get(filterName);
			if(expectedNumberWorkers==null)
				System.out.println(filterName+" null number of workers");
			else
				assertEquals(filterName +" crowd does not have same number of workers: ", expectedNumberWorkers.intValue(), actualNumberWorkers.intValue() );
		}
	}

	@Test
	public void testCutMaximumCommonAnswer(){
		
		setup();
		//Test filter generation
		SimulationController controller = new SimulationController();

		ArrayList<SubCrowd> list = controller.generateSubCrowdFilters();
		list = controller.generateSubCrowdMicrotasks(list);
		list = controller.setMaximumCommonAnswers(list);
		list = controller.cutAnswerListsToMaximum(list);

		for(SubCrowd crowd : list){
				System.out.println(crowd.name+",total  answers,"+crowd.totalAnswers+", common max answers,"+crowd.maxCommonAnswers
						+", total workers,"+crowd.totalWorkers);
		}
	}
}

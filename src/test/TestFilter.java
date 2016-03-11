package test;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileConsentDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;
import edu.uci.ics.sdcl.firefly.report.predictive.AnswerData;
import edu.uci.ics.sdcl.firefly.report.predictive.FilterCombination;
import edu.uci.ics.sdcl.firefly.report.predictive.FilterGenerator;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMapLoader;
import edu.uci.ics.sdcl.firefly.util.ElapsedTimeUtil;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;
import edu.uci.ics.sdcl.firefly.util.mturk.AnswerCounter;

public class TestFilter {

	public static void main(String[] args){

		//Obtain bug covering question list
		PropertyManager manager = PropertyManager.initializeSingleton();
		HashMap<String,String> bugCoveringMap = new HashMap<String,String>();
		String[] listOfBugPointingQuestions = manager.bugCoveringList.split(";");
		for(String questionID:listOfBugPointingQuestions){
			bugCoveringMap.put(questionID,questionID);
		}

		//Produce the list of filters
		ArrayList<FilterCombination> filterList = FilterGenerator.generateAnswerFilterCombinationList();

		String[] fileNameList = {"HIT01_8", "HIT02_24", "HIT03_6", "HIT04_7",
				"HIT05_35","HIT06_51","HIT07_33","HIT08_54"};

		QuestionLinesMapLoader loader = new QuestionLinesMapLoader();
		HashMap<String, QuestionLinesMap> lineMapping =  loader.loadList();
		
		ArrayList<HashMap<FilterCombination,AnswerData>> processingList = new 	ArrayList<HashMap<FilterCombination,AnswerData>> ();

		//Apply filter and extract data by fileName
		//System.out.println("FilterList size: "+ filterList.size());
		//for(FilterCombination combination :  filterList){
			FilterCombination combination =  filterList.get(0);
			FileSessionDTO sessionDTO = new FileSessionDTO();
			HashMap<String, Microtask> microtaskMap = (HashMap<String, Microtask>) sessionDTO.getMicrotasks();

			Filter filter = combination.getFilter();

			HashMap<String, Microtask> filteredMicrotaskMap = (HashMap<String, Microtask>) filter.apply(microtaskMap);


			Integer totalDifferentWorkersAmongHITs = countWorkers(filteredMicrotaskMap, null);

			System.out.println("Elapsed time: "+ElapsedTimeUtil.getElapseTime(filteredMicrotaskMap)+
					", number of answers: "+AnswerCounter.countAnswers(filteredMicrotaskMap) + 
					", number of workers: "+totalDifferentWorkersAmongHITs);


			/*for(String fileName: fileNameList){
				HashMap<String, ArrayList<String>> answerMap = extractAnswersForFileName(filteredMicrotaskMap,fileName);
				Integer workerCountPerHIT = countWorkers(filteredMicrotaskMap,fileName);
				AnswerData data = new AnswerData(fileName,answerMap,bugCoveringMap,workerCountPerHIT,totalDifferentWorkersAmongHITs);
				HashMap<FilterCombination, AnswerData> map = new HashMap<FilterCombination, AnswerData>();
				map.put(combination, data);
				processingList.add(map);
			}*/
		//}
		
	}
		private static Integer countWorkers(
				HashMap<String, Microtask> filteredMicrotaskMap, String fileName) {

			FileConsentDTO dto = new FileConsentDTO();
			HashMap<String,Worker> workerObjectMap = dto.getWorkers();
			HashMap<String,String> workerMap = new HashMap<String, String>();
			for(Microtask task: filteredMicrotaskMap.values()){
				if(fileName==null || task.getFileName().compareTo(fileName)==0){
					for(Answer answer:task.getAnswerList()){
						String workerID = answer.getWorkerId();
						Worker workerObj = workerObjectMap.get(workerID);
						//System.out.println(workerID+","+workerObj.getSurveyAnswer("Experience")+","+workerObj.getGrade());
						workerMap.put(workerID, workerID);
					}
				}
			}
			return workerMap.size();
		}
}

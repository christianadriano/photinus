package edu.uci.ics.sdcl.firefly.report.predictive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileConsentDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;
import edu.uci.ics.sdcl.firefly.util.BugCoveringMap;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class WorkerSamplePerformance {

	public class WorkerOutcome{

		String workerID;

		Integer TP;
		Integer FP;
		Integer TN;
		Integer FN;

		String profession;
		Integer score;
		String gender;
		String fileName;
		String yearsOfProgramming;
		Integer IDKLevel;
		String country;
		String programmingLanguage;
		String HITList;

		public WorkerOutcome(Worker worker, String fileName, Integer IDKLevel, HashMap<String,Integer> correctnessMap){

			this.workerID = worker.getWorkerId();
			this.TP = correctnessMap.get("TP");
			this.FP = correctnessMap.get("FP");
			this.TN = correctnessMap.get("TN");
			this.FN = correctnessMap.get("FN");

			this.profession = worker.getSurveyAnswer("Experience").replace(";"," ");
			if(profession.contains("Other"))
				this.profession = "Other";
			this.score = worker.getGrade();
			this.fileName = fileName.replace("HIT0","J").replace("_",".");
			this.IDKLevel = IDKLevel;
			this.gender = worker.getSurveyAnswer("Gender");
			this.yearsOfProgramming = worker.getSurveyAnswer( "YearsProgramming");
			this.country = worker.getSurveyAnswer("Country").replace(".","");
			if(this.country.toUpperCase().contains("US") || (this.country.toUpperCase().contains("UNITED STATES")))
				this.country = "US";

			if(this.country.toUpperCase().contains("INDIA") || (this.country.toUpperCase().contains("INDIAN")))
				this.country = "INDIA";

			this.programmingLanguage = worker.getSurveyAnswer("Language");
			if(this.programmingLanguage.toUpperCase().contains("JAVA"))
				this.programmingLanguage = "JAVA";
		}

		public void appendHIT(String hitName){
			hitName = hitName.replace("HIT0","J").replace("_",".");
			this.fileName = this.fileName + ";" +hitName;
		}

		public String toString(){
			return workerID +";"+score+";"+profession+";"+gender+";"+yearsOfProgramming+";"+country+";"+programmingLanguage+";"+IDKLevel+";"+ TP +";"+ TN +";"+ FP +";"+ FN+";"+fileName+";";
		}
	}

	public String getHeader(){
		return "workerID;score;profession;gender;yearsOfProgramming;country;programmingLanguage;IDKLevel;TP;TN;FP;FN;fileNames1;fileNames2;fileNames3;fileNames4;fileNames5;fileNames6;fileNames7;fileNames8;fileNames9;fileNames10;";
	}

	/**
	 * Get one session per worker and compute the TP, FP, TN, FN 
	 * @return Map indexed by worker <WorkerID,HashMap<ResultKey,String>> 
	 * ResultKeys are "TP", "FP", "TN", "FN", "FileName", "WorkerScore", 
	 * "WorkerProfession", "YearsOfExperience", "Gender", "Country","IDKs" 
	 */
	public HashMap<String,WorkerOutcome> printAllWorkerSessionSessionOutcomes(){

		HashMap<String, WorkerOutcome> workerOutputMap = new HashMap<String, WorkerOutcome> ();
		FileSessionDTO sessionDTO = new FileSessionDTO();
		FileConsentDTO consentDTO = new FileConsentDTO();
		HashMap<String,Worker> workerMap = consentDTO.getWorkers();

		for(Worker worker: workerMap.values()){
			if(worker.hasPassedTest()){

				ArrayList<WorkerSession> sessionList = sessionDTO.getSessionsByWorkerID(worker.getWorkerId());
				if(sessionList!=null && sessionList.size()>0){
					for(int i=0;i<sessionList.size();i++){
						WorkerSession session = sessionList.get(i);
						if(session!=null){
							HashMap<String, Integer> correctnessMap = computeCorrectness(session);
							Integer idkLevel = computerIDKLevel(session);
							WorkerOutcome outcome;
							if(workerOutputMap.containsKey(worker.getWorkerId())){
								outcome = workerOutputMap.get(worker.getWorkerId());
								String hitName = session.getFileName();
								outcome.appendHIT(hitName);
								workerOutputMap.put(worker.getWorkerId(),outcome);
								System.out.println("worker ID:"+worker.getWorkerId()+";"+hitName);
							}
							else{
								outcome = new WorkerOutcome(worker, session.getFileName(), idkLevel, correctnessMap);	
								workerOutputMap.put(worker.getWorkerId(),outcome);
							}
							
						}
					}
				}
			}
		}
		return workerOutputMap;
	}


	/**
	 * Get one session per worker and compute the TP, FP, TN, FN 
	 * @return Map indexed by worker <WorkerID,HashMap<ResultKey,String>> 
	 * ResultKeys are "TP", "FP", "TN", "FN", "FileName", "WorkerScore", 
	 * "WorkerProfession", "YearsOfExperience", "Gender", "Country","IDKs" 
	 */
	public HashMap<String,WorkerOutcome> printOneWorkerSessionSessionOutcomes(){

		HashMap<String, WorkerOutcome> workerOutputMap = new HashMap<String, WorkerOutcome> ();
		FileSessionDTO sessionDTO = new FileSessionDTO();
		FileConsentDTO consentDTO = new FileConsentDTO();
		HashMap<String,Worker> workerMap = consentDTO.getWorkers();

		for(Worker worker: workerMap.values()){
			if(worker.hasPassedTest()){

				ArrayList<WorkerSession> sessionList = sessionDTO.getSessionsByWorkerID(worker.getWorkerId());
				if(sessionList!=null && sessionList.size()>0){
					WorkerSession session = this.getFirstNonEmptySession(worker,sessionDTO);
					if(session!=null){
						HashMap<String, Integer> correctnessMap = computeCorrectness(session);
						Integer idkLevel = computerIDKLevel(session);
						WorkerOutcome outcome;
						if(workerOutputMap.containsKey(worker.getWorkerId())){
							outcome = workerOutputMap.get(worker.getWorkerId());
							String hitName = session.getFileName();
							outcome.appendHIT(hitName);
						}
						else{
							outcome = new WorkerOutcome(worker, session.getFileName(), idkLevel, correctnessMap);	
						}
						workerOutputMap.put(worker.getWorkerId(),outcome);
					}
				}
			}
		}
		return workerOutputMap;
	}


	private WorkerSession getFirstNonEmptySession(Worker worker, FileSessionDTO sessionDTO){

		int i=0;
		ArrayList<WorkerSession> sessionList = sessionDTO.getSessionsByWorkerID(worker.getWorkerId());
		WorkerSession session=null;
		Vector<Microtask> microtaskList =null;
		while((i<sessionList.size()) && (microtaskList==null || microtaskList.size()==0)){
			session = sessionList.get(i);  
			microtaskList = session.getMicrotaskList();
			i++;
		}

		if(microtaskList!=null && microtaskList.size()>0)
			return session;
		else 
			return null;
	}

	private Integer computerIDKLevel(WorkerSession session) {

		int idkLevel=0;

		Vector<Microtask> microtaksList = session.getMicrotaskList();
		for(Microtask microtask: microtaksList){
			Answer answer = microtask.getAnswerByUserId(session.getWorkerId());
			String answerOption = answer.getOption();
			if(answerOption.compareTo(Answer.I_DONT_KNOW)==0)
				idkLevel++;
		}
		return idkLevel;
	}

	private HashMap<String, Integer> computeCorrectness(WorkerSession session) {

		HashMap<String,Integer> correctness = new 	HashMap<String,Integer> ();

		int tp=0;
		int fp=0;
		int fn=0;
		int tn=0;

		HashMap<String, String> bugCoveringMap = BugCoveringMap.initialize();

		Vector<Microtask> microtaksList = session.getMicrotaskList();
		for(Microtask microtask: microtaksList){
			String questionID = microtask.getID().toString();
			Answer answer = microtask.getAnswerByUserId(session.getWorkerId());
			String answerOption = answer.getOption();

			if(bugCoveringMap.containsKey(questionID)){
				if(answerOption.compareTo(Answer.YES)==0)
					tp++;
				else
					if(answerOption.compareTo(Answer.NO)==0)
						fn++;
			}
			else{
				if(answerOption.compareTo(Answer.NO)==0)
					tn++;
				else
					if(answerOption.compareTo(Answer.YES)==0)					
						fp++;
			}
		}

		correctness.put("TP",tp);
		correctness.put("FP",fp);
		correctness.put("FN",fn);
		correctness.put("TN",tn);

		return correctness;
	}


	private HashMap<String, String> getBugCoveringMap(String bugCoveringList){

		HashMap<String, String> resultMap = new HashMap<String, String>();
		String[] list = bugCoveringList.split(";");

		for(String bug: list){
			resultMap.put(bug, bug);
		}

		return resultMap;
	}

	public void printResults(HashMap<String,WorkerOutcome> outcomeMap){	

		String destination = "C://firefly//SessionsFromWorkers.txt";
		BufferedWriter log;
		try {
			log = new BufferedWriter(new FileWriter(destination));
			//Print file header

			log.write(getHeader()+"\n");

			for(WorkerOutcome outcome: outcomeMap.values()){
				String line= outcome.toString();
				log.write(line+"\n");
			}
			log.close();
			System.out.println("file written at: "+destination);
		} 
		catch (Exception e) {
			System.out.println("ERROR while processing file:" + destination);
			e.printStackTrace();
		}
	}


	public static void main(String[] args){

		WorkerSamplePerformance performance = new WorkerSamplePerformance();
		//HashMap<String,WorkerOutcome> outcomeMap = performance.printOneWorkerSessionSessionOutcomes();
		HashMap<String,WorkerOutcome> outcomeMap = performance.printAllWorkerSessionSessionOutcomes();
		performance.printResults(outcomeMap);

	}

	//-------------------------------------------------------------
	//

	/** Workers who took 3 questions. Out of which, only one is bug covering. */
	static String[] workerSampledList = {"139ga6a-3G761",
		"1005eg-1g2c-9-80",
		"1010Cc7a3A2-9-6",
		"101CE0E8G-6-88",
		"1023eg-8i1C506",
		"1026Ae-6g1E-5-62",
		"1029gC9I4C-463",
		"1033Ae4A5G2-3-4",
		"1035cc-5g1C3-98",
		"1049cG0C6I034",
		"104eE-2E-5a05-8",
		"1050eE-1c-6C0-2-1",
		"1098Ai-4G6a330",
		"109CE-9e-3I1-37",
		"10eA-4G6I98-6",
		"10Ei5i-5c-61-9",
		"1110Ge7a2g-1-70",
		"1114Ii7g4i-76-9",
		"1120Ea6I-1G6-6-6",
		"1126Ai0i0i-74-1",
		"1147iA9a5c196",
		"1160gG0A0g516",
		"1161Ag9E2A015",
		"1176aG-6g3A-509",
		"1183aG3E2c-60-7",
		"118Cc-2C-6I-1-83",
		"1194GI0G8E7-20",
		"1197gc5c3c27-5",
		"1206iA4i-9C6-8-3",
		"1209ci-9E-1I0-11",
		"1222ae-7A3c2-7-6",
		"1229cG-8G1G-915",
		"1231Ce3g6C290",
		"1244ie-4G-4g486",
		"1245CI-3g-1a1-72",
		"1249cG4e7C7-70",
		"1254gg5C-2C06-7",
		"1256Ii4I4g84-2",
		"1270GI8C5i-6-19",
		"1274Ie3c0A69-4",
		"127IC0c0A-56-3",
		"1297gA-5c-6e-204",
		"12gG3I-4G95-8",
		"1303cA-9c3g-609",
		"1330Ie9C-8c236",
		"1332CE5a-2e-9-3-1",
		"1367Cg2a9C-61-4",
		"1371aa-3i6A-67-2",
		"1388Ce-6C9A-683",
		"1394iC9g0e-7-66",
		"139ga6a-3G761",
		"1418aA4E-4c5-34",
		"1432Ge-2A-6a-382",
		"1449GA0I-4C14-1",
		"1458GG9g6A2-9-9",
		"1493Ai0E9A4-49",
		"1499aa-2I7G-47-7",
		"14Gg6e1I7-56",
		"1503ge-6g-9C95-7",
		"1541iI-2a2i3-42",
		"1583cG-5E-8C-6-50",
		"1591gc8E-5I9-17",
		"15cg-2i-7c-29-1",
		"1635CE6g8g-39-8",
		"1638EA0g0c-27-2",
		"166IC5e-3e-7-6-5",
		"16GI2i-7c70-7",
		"178Ci-7G0E5-62",
		"17aA-9e6i460",
		"181gi-1i8G-4-2-9",
		"185ci-3a-7c2-76",
		"19EC-9E-6c37-8",
		"1AI-7e-8i090",
		"205Ai1c-6A95-2",
		"207GI-1i3I004",
		"208ae5c-5e-2-5-1",
		"214ea9G9i04-1",
		"220aC0I-6a0-6-3",
		"23Ai-8E5A-1-1-1",
		"242Cg7E7G-60-3",
		"244AC3a5C68-6",
		"247iI-1a-8i-2-2-3",
		"248aG-4a-2E723",
		"249iI-4G-6A9-26",
		"24cc-9i1a-4-81",
		"257iC-1e8c9-56",
		"259AI0i0a84-8",
		"262IG-2i-1e-860",
		"268Ai-3a-7G689",
		"273EI-8I-8g2-1-7",
		"276ee1A-3E7-54",
		"278Ie8e9e091",
		"293iG4A-3I005",
		"299EC-4G-4a-3-89",
		"30eI-1g0G8-78",
		"318ei6C-4e59-7",
		"327CC-3i7A146",
		"33Ea8A-6e1-73",
		"346Gg6E5E475",
		"352iG-9c-2C29-1",
		"355Aa-2E7e8-97",
		"356aC-2E-3i-1-9-2",
		"357Ac8I-6G-770",
		"358Ce0A6e-50-7",
		"375iA6E-5g91-6",
		"376Ii4i9e2-83",
		"37gg5C-5i149",
		"390iE-1e-1e-908",
		"391Ce-9e0c133",
		"396ie3E5A9-5-3",
		"398Ca1e4I7-8-4",
		"426AC1I-8a919",
		"428iG0c6A-7-8-1",
		"42iE-7g1i478",
		"431IC5c-9A-716",
		"458eA4a-4i44-9",
		"468Ga1a7a1-81",
		"46Gi0c0C9-16",
		"470aA4i3I-404",
		"471gc-7A-8e4-73",
		"474ic-2g1i-23-3",
		"481cg-5C9A77-5",
		"486cI5E-5e4-98",
		"490Ae3C-5I-6-75",
		"498ce-2a7G1-27",
		"499Ig1e5E736",
		"502Ia1G-7i-502",
		"507CG0A-2i1-4-3",
		"509ag3C6G-120",
		"50gG-8i8g-16-5",
		"515AA4c-5g-9-77",
		"516eI-1a3E900",
		"51EA-5e8I01-5",
		"527ei-7g-8c1-70",
		"529EI4e7C696",
		"52Ce2i0i-614",
		"532iI4C2G-8-35",
		"537Cg0e-7E-5-68",
		"540Ec0I1E97-8",
		"545gg-4G7e00-1",
		"54ee2E5G30-4",
		"553Ic-5c-3E0-81",
		"556ce9C0G5-9-7",
		"559CG5c-6C171",
		"55eg3A1g1-8-9",
		"57Ai4C-7E67-1",
		"581Aa2A0a9-60",
		"585Eg7c9e716",
		"588IC-1C8c-145",
		"594eG-2e-7C12-9",
		"5Ag-2g5E106",
		"5ai0i-8E-72-1",
		"600Ea-6C6E443",
		"602Cc9g-4i1-3-4",
		"604Ic-1I5i105",
		"607iG-5I0i314",
		"619cG-3e8i71-7",
		"61ae4E2e164",
		"629Ee-9e7e9-32",
		"630AE4g-9A-5-50",
		"647GI-5i-2i140",
		"64eA1C3G808",
		"651iA8E0i-867",
		"654eA-3g-6E451",
		"657eE-7g8g0-33",
		"664Ge2a-4e-66-2",
		"666cg2a-4C2-83",
		"66Ig3i-8i-7-4-5",
		"673eG3G4e074",
		"676ec6g2C1-2-9",
		"690eg9E3e-97-9",
		"702ci-6A7G-38-5",
		"702gc9c4a9-86",
		"707eg-6e-2a05-2",
		"71gE-6g-5A-3-20",
		"722ce2G0i-857",
		"732aC-4C4C-7-2-1",
		"733aA0i-2G-80-7",
		"73EA0a-7i-7-8-9",
		"741Ec-1c3C-7-2-5",
		"747Ee7E0G0-10",
		"749ei0A6I-20-1",
		"74iC-2e-1C5-78",
		"751gC1a0c-7-3-2",
		"754cC2G-2A-4-8-2",
		"755ec4i1a-48-7",
		"75Ei-8A9C4-76",
		"762AG-5i5a030",
		"76Ig1I7i943",
		"771eE-2I5c-40-7",
		"772ia-5e-2C4-55",
		"775GI0a2G-680",
		"777Ci4A2a213",
		"781gE-5a0I-2-8-5",
		"789ga-6I1C50-4",
		"78iA-8i-7C7-99",
		"794gi1e-9c3-70",
		"7gG-1G-3C163",
		"805aA4C3C-174",
		"807iG4c-8c-64-7",
		"809cE-2g-5i-8-5-7",
		"815IG-6E-2c4-2-5",
		"816iE-9I-4e-87-8",
		"817Ec-1A8c-194",
		"81aa3e6E7-48",
		"81Eg-4I3c241",
		"820ga-8C6c5-33",
		"82Ai8C-3i739",
		"831AG1c-9G-3-45",
		"83aa-2a3G4-14",
		"84CE-1i3I00-5",
		"850CI-4G6E-6-4-8",
		"867ee0c4I-76-3",
		"891Ia8E-1I7-93",
		"893ec6i-1a-49-2",
		"901AG-9g1g270",
		"90Ai-9A0a-8-28",
		"918aC-7a-9A-710",
		"91Ca-7G6e-5-5-6",
		"91gI-3I1g008",
		"92iC-9I-5C-1-9-7",
		"931EE-1g8a-456",
		"935Ei-8e-1E-7-9-7",
		"939aC8g5A220",
		"940IE-6c3C5-6-5",
		"945eg8c3A220",
		"956GG7A0C-223",
		"957cC5C0E4-4-5",
		"96Ca-7E2i-14-2",
		"96ge9G-1C-360",
		"970AG-5G9G0-86",
		"973gC1A-4g040",
		"979ei-5I-9C5-61",
		"985AG2C4g-61-6",
		"98ce7A-4i-507",
		"99IG-7e4g019",
		"9GI2A-8C06-5",
	};

	public static void printWorkerSessionFilenName(){
		FileSessionDTO sessionDTO = new FileSessionDTO();

		System.out.println("WorkerID:FileName");
		for(int i=0; i<workerSampledList.length; i++){
			String workerID = workerSampledList[i];

			ArrayList<WorkerSession> sessionList = sessionDTO.getSessionsByWorkerID(workerID);
			//System.out.println(i+":"+workerID+ ":"+sessionList.size());
			String fileName="";
			for(WorkerSession session : sessionList){
				if(session.getMicrotaskListSize()>0){
					fileName = session.getFileName();
				}
				else{
					//System.out.println(workerID+": empty session");
					//System.out.print(": empty session");
				}
			}
			System.out.println(workerID+":"+fileName);
			//	System.out.print(":"+fileName);

		}

	}


}


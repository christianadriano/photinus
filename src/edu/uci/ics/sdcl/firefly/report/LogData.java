package edu.uci.ics.sdcl.firefly.report;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.Microtask;

public class LogData {

	/** this is the map that will be used to compute statistics, currently can be the microtaskMap (superset) or 
	 * the completeSessions_microtaskMap, other subsets will be created.
	 */
	public HashMap<String, Microtask> workingMicrotaskMap;

	//Session Map
	public HashMap<String, WorkerSession> sessionMap = new HashMap<String, WorkerSession>(); 

	public	HashMap<String, WorkerSession> closedSessionMap = new HashMap<String, WorkerSession>();

	public	HashMap<String, WorkerSession> openedSessionMap = new HashMap<String, WorkerSession>();

	public	HashMap<String, Microtask> completeSessions_microtaskMap = new HashMap<String, Microtask>();

	public	HashMap<String, Microtask> microtaskMap = new HashMap<String, Microtask>();

	public ArrayList<Microtask> microtaskList = new ArrayList<Microtask>();

	public	HashMap<String, Worker> workerMap = new HashMap<String, Worker>();

	public ArrayList<Worker> workerList = new ArrayList<Worker>();

	public	HashMap<String, Worker> surveyMap = new HashMap<String, Worker>();

	public	HashMap<String, Worker> skillTestMap = new HashMap<String, Worker>();

	public	HashMap<String, Worker> consentMap = new HashMap<String, Worker>();

	public	ArrayList<String> timeStampList = new ArrayList<String>();

	//Sessions that are considered spurious
	public	HashMap<String,String> batch1RejectMap = new HashMap<String,String>();

	public	HashMap<String,String> batch2RejectMap = new HashMap<String,String>();

	//Workers with nonsense explanations in Batch-1
	public String[] batch1Reject= {"109ca-4A-5e-204","119Ia6C3e-214","144aE-9e0e008","152ie5a-2A-74-7",
			"167iA-7A2E-5-2-1","20ea-3I0I-256","315cA-2A9i9-50","321aG-4c0i3-39",
			"331Ae4a-4a-201","345GG2a6c-19-9","403gE-8A-5E-6-71","421gg4C-5A7-9-7",
			"435ci2A-4e304","438Aa6a0e81-9","441aG5g9A-83-4","462ag-3i1A537","496ce9A-5C-8-56",
			"72ga8I-1e-885","304IA-4a-6i5-28","174ga8a6i2-30",
			"453ec4C2E-1-12","365ci-2i0i-47-4"};

	//Workers with nonsense explanations in Batch-2
	public String[] batch2Reject= {
			"497iG2c-8C-94-7","492gc2C-2g-397",
			"490Cg4a-9G-25-4","483iA4E3I70-1",
			"463cg-4G4a-780","444Ae0A-2i-16-7",
			"439aC2g-6C-3-19","436ia-9a-4A-4-7-2",
			"432gg-1G-3A22-3","433AE9I-9e-56-4",
			"429CI-6I-5A3-8-4","425ag7c0c8-5-6",
			"422Ce-6E-9I71-3","415Ig1G3a7-35",
			"398aC1I6A-6-5-2","395ii4A6A0-95",
			"394aa-5E-8C-132","393cc0i-8i3-6-6",
			"392cE2e4i1-77","385ag-6G6I-1-5-1",
			"378Cc7G1E125",	"373aa8i-2G734",
			"358Ee1G0i-936","355gi-9C7g-664","356CA-3G-4I099",
			"349gE8A4G-8-8-6","348CI7I8C-90-7","312gi7C-8c-973","301Ic5G-6c04-3","284aA-1i7a-701","268IA2C-8G757","267EI3A0i5-69","249aI-2A-8g50-7","255gg-8i-8a04-8","247CC0A8c302","224II0C-6c1-7-6","219iA-1A2G-11-8","215Ec-6E-9i0-6-5",	"179ag6G9A-51-8","166EA3G2E-503","153cc-9G-9g-15-8",	"150gg-3c0a-572","135gE7e-5C4-94","115II-2i2g-3-57","102cI4C9A631","77Ic-4c0e-21-3","67Ea8g7I551","29eC0e1G144"};

	//Tracks the number of times a worker answered "I Can't Tell"
	public HashMap<String,Integer> workerICantTellMap= new HashMap<String,Integer>();

	//Bug revealing Microtask IDs that we expect YES or Probably Yes
	public	int[] yesArray = {1, 3, 20, 25, 18, 61, 53, 51, 33, 69,71, 139,137,119,132,147,145,151,149,156,153,163,164,171,170,167,176,174,178,188,180};
	public HashMap<String,String> yesMap = new HashMap<String,String>();

	//Bug revealing Microtask IDs for not methodBody Yes or Probably Yes
	public	int[] yesNotBodyFunctionArray = {3, 20, 25, 18, 61, 53, 51, 71, 139,137,132,147,151,149,156,164,171,170,176,188,180};
	public HashMap<String,String> yesNotBodyFunctionMap = new HashMap<String,String>();

	public int totalNumberMicrotasks = 215;

	//File Name, Point (x=start;y=end)
	public HashMap<String, Point> fileNameTaskRange = new HashMap<String,Point>();

	public static String question[] = {"Gender", "Age", "Country", "Years progr.", "Difficulty", "Feedback"};   

	private String logPath = "C:/Users/igMoreira/Documents/GitHub/photinus/logs";


	private File consentFile; 
	private File sessionFile;

	private String batchNumber;
	/** if true, then discard all sessions listed in the reject arrays (batch1Reject and batch2Reject) */
	private boolean doReject; 

	/**  minimal time in milliseconds for an answer to be considered. */
	private double minDuration;

	public int ZeroToOne=0;
	public int OneToTwo=0;
	public int TwoToThree=0;
	public int ThreeToFour=0;
	public int FourToFive=0;
	public int FiveToSix=0;
	public int aboveSix=0;

	/**
	 * 
	 * @param doReject if true, then discard all sessions listed in the reject arrays (batch1Reject and batch2Reject)
	 * @param minDuration minimal time in milliseconds for an answer to be considered.
	 */
	public LogData(boolean doReject, double minDuration){

		this.minDuration = minDuration;

		this.doReject=doReject;
		if(doReject){//Initialize Maps
			for(String id:this.batch1Reject){
				this.batch1RejectMap.put(id,id);
			}

			for(String id:this.batch2Reject){
				this.batch2RejectMap.put(id,id);
			}
		}

		for(int id:yesArray){
			this.yesMap.put(new Integer(id).toString(),"YES");
		}

		for(int id:this.yesNotBodyFunctionArray){
			this.yesNotBodyFunctionMap.put(new Integer(id).toString(),"YES");
		}

		fileNameTaskRange.put("9buggy_Hystrix_buggy.txt",new Point(162,165));
		fileNameTaskRange.put("8buggy_AbstractReviewSection_buggy.txt",new Point(152,161));
		fileNameTaskRange.put("7buggy_ReviewTaskMapper_buggy.txt",new Point(144,151));
		fileNameTaskRange.put("6ReviewScopeNode_buggy.java",new Point(118,143));
		fileNameTaskRange.put("3buggy_PatchSetContentRemoteFactory_buggy.txt",new Point(68,117));
		fileNameTaskRange.put("2SelectTranslator_buggy.java",new Point(32,67));
		fileNameTaskRange.put("1buggy_ApacheCamel.txt",new Point(0,31));
		fileNameTaskRange.put("13buggy_VectorClock_buggy.txt",new Point(177,214));
		fileNameTaskRange.put("11ByteArrayBuffer_buggy.java",new Point(173,176));
		fileNameTaskRange.put("10HashPropertyBuilder_buggy.java",new Point(166,172));

		System.out.println("Total of different questions = 215");
		System.out.println("Total different bug revealing questions = "+this.yesArray.length);


	}

	private void countDuration(Double duration){

		if(duration<=60000.0)
			this.ZeroToOne++;
		else
			if(duration>60000.0 && duration <=120000.0)
				this.OneToTwo++;
			else
				if(duration>120000.0 && duration <=180000.0)
					this.TwoToThree++;
				else
					if(duration>180000.0 && duration <=240000.0)
						this.ThreeToFour++;
					else
						if(duration>240000.0 && duration <=300000.0)
							this.FourToFive++;
						else
							if(duration>300000.0 && duration <=360000.0)
								this.FiveToSix++;
							else
								this.aboveSix++;
	}

	/**
	 * Get answers only from session which were completed,i.e., closed
	 */
	public void computeMicrotaskFromCompleteSessions(){
		int counter=0;
		int microtaskCounter = 0;
		Iterator<String> iter = this.closedSessionMap.keySet().iterator();
		while(iter.hasNext()){
			String sessionId = iter.next();

			WorkerSession session = this.closedSessionMap.get(sessionId);
			String workerId = session.getWorkerId();

			Vector<Microtask> answerList = session.getMicrotaskList();


			if(answerList!=null  && answerList.size()>10)
				System.out.println("Session id="+sessionId+" has "+answerList.size()+" answers!");


			for(Microtask task: answerList){
				Microtask existing = this.completeSessions_microtaskMap.get(task.getID().toString());
				Answer answer = task.getAnswerByUserId(workerId);


				if(existing==null){
					existing = task.getSimpleVersion(); //create a new copy of the task, so we don't mixed with the old one.
				}
				else{
					existing.addAnswer(answer);					
				}

				this.completeSessions_microtaskMap.put(task.getID().toString(), existing);
				microtaskCounter++;
			}
			counter ++;
		}
		//System.out.println("counter complete sessions:"+counter+ ", complete microtasks:"+microtaskCounter);				
	}

	/**
	 * 
	 * @param batchNumber to take the batch into consideration (marks the sessionId with the batchNumber
	 * @return
	 */
	public boolean processLogProduction2(String path){
		this.batchNumber = "2-";

		if(path!=null) this.logPath = path;
		this.sessionFile = new File(logPath+"session_Run2-28oct.log");
		this.consentFile = new File(logPath+"consent_Run2-28oct.log");

		return loadConsents(2) && loadSessions(2);
	}

	private boolean loadConsents(int type){
		try(BufferedReader br = new BufferedReader(new FileReader(this.consentFile))) {
			for(String line; (line = br.readLine()) != null; ) {
				if(type==2)
					processConsentLine(line);
				else
					this.processConsentLineProduction1(line);
			}
			return true;
		}
		catch(Exception e){
			System.err.println(e);	
			return false;
		}
	}

	public boolean processConsentLine(String line){ 

		StringTokenizer tokenizer = new StringTokenizer(line, "%");

		//Time extraction
		String token = tokenizer.nextToken();
		String timeStamp = extractTimeStamp(token);
		this.timeStampList.add(timeStamp);

		//Event extraction
		token = tokenizer.nextToken();

		tokenizer.nextToken(); //"workerId label"
		String workerId = this.batchNumber+tokenizer.nextToken().trim();

		Worker worker;
		//Event resolution
		if(token.trim().matches("CONSENT")){
			tokenizer.nextToken(); //consentDate label
			String consentDate = tokenizer.nextToken();
			worker = new Worker(workerId,consentDate, null);

			if(!workerMap.containsKey(worker.getWorkerId())){
				//Add to the sequential list
				this.workerList.add(worker);

				//Add to the maps
				this.workerMap.put(worker.getWorkerId(), worker);
				this.consentMap.put(worker.getWorkerId(), worker);
			}

		}
		else
			if(token.trim().matches("SKILLTEST")){

				worker = this.workerMap.get(workerId);

				boolean[] gradeMap = new boolean[5];
				tokenizer.nextToken(); //test 1 label
				gradeMap[0] = new Boolean(tokenizer.nextToken()); 
				tokenizer.nextToken(); //test 2 label
				gradeMap[1] = new Boolean(tokenizer.nextToken());
				tokenizer.nextToken(); //test 3 label
				gradeMap[2] = new Boolean(tokenizer.nextToken());
				tokenizer.nextToken(); //test 4 label
				gradeMap[3] = new Boolean(tokenizer.nextToken());
				/*tokenizer.nextToken(); //test 5 label
				gradeMap[4] = new Boolean(tokenizer.nextToken());*/
				
				tokenizer.nextToken(); //grade label
				String grade = tokenizer.nextToken();
				tokenizer.nextToken(); //duration label
				String duration = tokenizer.nextToken();
				worker.setGrade(new Integer(grade).intValue());

				//Retrieve answers
				worker.setSkillAnswers(null, gradeMap, null, new Integer(grade).intValue(), duration);

				this.workerMap.put(worker.getWorkerId(), worker);

				this.skillTestMap.put(worker.getWorkerId(), worker);
			}
			else
				if(token.trim().matches("SURVEY")){
					tokenizer.nextToken(); //sessionId label
					String sessionId = this.batchNumber + tokenizer.nextToken();
					worker = this.workerMap.get(workerId);
					tokenizer.nextToken(); //Feedback label
					tokenizer.nextToken(); //Feedback value
					tokenizer.nextToken(); //Gender label
					String gender = tokenizer.nextToken(); //Gender value
					tokenizer.nextToken(); //Years Prog. label
					String experience = tokenizer.nextToken(); //Years Prog value					
					tokenizer.nextToken(); //Difficulty label
					String difficulty = tokenizer.nextToken(); //Difficulty value
					tokenizer.nextToken(); //Country label
					String country = tokenizer.nextToken(); //Country value
					tokenizer.nextToken(); //Age label
					String age = tokenizer.nextToken(); //Age value

					worker.setSessionId(sessionId);

					worker.addSurveyAnswer(question[0], gender);
					worker.addSurveyAnswer(question[1], age);
					worker.addSurveyAnswer(question[2], country);
					worker.addSurveyAnswer(question[3], experience);
					worker.addSurveyAnswer(question[4], difficulty);

					this.workerMap.put(worker.getWorkerId(), worker);

					this.surveyMap.put(worker.getWorkerId(), worker);
				}


		return false;

	}

	private boolean loadSessions(int type){
		try(BufferedReader br = new BufferedReader(new FileReader(this.sessionFile))) {
			for(String line; (line = br.readLine()) != null; ) {
				if(type==2)
					processSessionLine(line);
				else
					this.processSessionLineProduction1(line);
			}
			return true;
		}
		catch(Exception e){
			System.err.println(e.toString());	
			return false;
		}
	}

	public boolean processSessionLine(String line){
		StringTokenizer tokenizer = new StringTokenizer(line, "%");
		int totalTokens = tokenizer.countTokens();

		//Time extraction
		String token = tokenizer.nextToken();
		String timeStamp = extractTimeStamp(token);
		this.timeStampList.add(timeStamp);

		//Event extraction
		token = tokenizer.nextToken();

		tokenizer.nextToken(); //"workerId label"
		String workerId = this.batchNumber+tokenizer.nextToken().trim();
		tokenizer.nextToken(); //"sessionId label"
		String sessionId = tokenizer.nextToken().trim();

		//Check if session should be rejected
		if(this.batch2RejectMap.containsKey(sessionId)){
			return false;
		}

		sessionId = this.batchNumber + sessionId;  
		WorkerSession session;



		//Event resolution
		if(token.trim().matches("OPEN SESSION")){
			session = new WorkerSession(sessionId, new Vector<Microtask>());
			session.setWorkerId(workerId);
			Worker worker = this.workerMap.get(workerId);
			worker.setSessionId(sessionId);

			this.workerMap.put(workerId, worker);
			this.sessionMap.put(session.getId(), session);
			this.openedSessionMap.put(session.getId(), session);
		}
		else
			if(token.trim().matches("MICROTASK")){

				session = this.sessionMap.get(sessionId);
				tokenizer.nextToken(); //"microtaskId label"
				String microtaskId = tokenizer.nextToken().trim();
				tokenizer.nextToken(); //FileName label
				String fileName = tokenizer.nextToken().trim();

				//Some answers are associated to the wrong file name
				if(tasksDoesNotPertainToFileName(microtaskId,fileName))
					return false;

				//Some answers are duplicated
				if(taskAlreadyInSession(microtaskId,session))
					return false;

				tokenizer.nextToken(); //question label
				String question = tokenizer.nextToken().trim();
				tokenizer.nextToken(); // answer label
				String answer = tokenizer.nextToken().trim();

				//Update the counter of answer types for the worker
				updateWorkerICantTellMap(workerId,answer);

				tokenizer.nextToken(); // duration label
				String duration = tokenizer.nextToken().trim();
				tokenizer.nextToken(); // explanation label
				String explanation;
				if(totalTokens==18)
					explanation= tokenizer.nextToken().trim();
				else 
					explanation="";

				double durationDouble = new Double(duration);
				//System.out.println("durationDouble: "+ durationDouble + ", minDuration: "+minDuration);
				//Discard answers that are too short
				if(durationDouble<this.minDuration){
					//	System.out.println("durationDouble: "+ durationDouble + "< minDuration: "+minDuration);
					return false;
				}
				//	else
				//	System.out.println("durationDouble: "+ durationDouble + "> minDuration: "+minDuration);
				this.countDuration(durationDouble);
				Answer answerObj = new Answer(answer, explanation, workerId,duration, timeStamp);

				//List of sequential Microtasks
				Vector<Answer> singleAnswerList = new Vector<Answer>();
				singleAnswerList.add(answerObj);
				Microtask uniqueMicrotask = new Microtask(question, new Integer(microtaskId), singleAnswerList,fileName,0);
				this.microtaskList.add(uniqueMicrotask);


				//Map of microtasks
				Vector<Answer> answerList = new Vector<Answer>();
				answerList.add(answerObj);

				Microtask microtask = this.microtaskMap.get(microtaskId);
				if(microtask==null){
					microtask = new Microtask(question, new Integer(microtaskId), answerList,fileName,0);
				}
				else{
					microtask.addAnswer(answerObj);
				}
				this.microtaskMap.put(microtaskId, microtask);

				//Session Microtask
				answerList = new Vector<Answer>();
				answerList.add(answerObj);
				Microtask sessionMicrotask = new Microtask(question, new Integer(microtaskId), answerList,fileName,0);
				Vector<Microtask> microtaskList = session.getMicrotaskList();
				microtaskList.add(sessionMicrotask);
				session.setMicrotaskList(microtaskList);
				this.sessionMap.put(session.getId(),session);

			}
			else
				if(token.trim().matches("CLOSE SESSION")){
					if(!this.closedSessionMap.containsKey(sessionId)){
						session = this.sessionMap.get(sessionId);
						this.closedSessionMap.put(session.getId(),session);
					}
				}

		return true;
	}


	private boolean taskAlreadyInSession(String microtaskId,
			WorkerSession session) {
		boolean repeated=false;
		Vector<Microtask> microtaskList = session.getMicrotaskList();
		for(Microtask task: microtaskList){
			if(task.getID().intValue()==new Integer(microtaskId).intValue()){
				//System.out.println("!!!!!!! repeated microtask "+ microtaskId+" in session " + session.getId());
				repeated=true;
				break;
			}
		}
		return repeated;
	}

	public boolean tasksDoesNotPertainToFileName(String microtaskId, String fileName) {

		Point point = this.fileNameTaskRange.get(fileName);
		int id = new Integer(microtaskId).intValue();
		if((id<point.x)||(id>point.y)){ //Out of expected range for the fileName
			//System.out.println("%%%%%%% microtask "+ microtaskId+" does not pertain to "+ fileName);
			return true;
		}

		else
			return false;
	}


	public boolean checkWorkerDidMinimalOneTask(String workerId) {
		Worker worker = this.workerMap.get(workerId);
		String sessionId = worker.getSessionId();
		if(sessionId==null)
			return false;
		else{
			WorkerSession session = this.sessionMap.get(sessionId);
			if(session==null)
				return false;
			else{
				Vector<Microtask> microtaskList = session.getMicrotaskList();
				if(microtaskList==null || microtaskList.size()==0)
					return false;
				else
					return true;
			}
		}
	}

	public String extractTimeStamp(String token){
		StringTokenizer tokenizer = new StringTokenizer(token,"[");
		return tokenizer.nextToken().trim();
	}



	//-------------------------------------------------------------------------

	/**
	 * 
	 * @param batchNumber to take the batch into consideration (marks the sessionId with the batchNumber
	 * @return
	 */
	public boolean processLogProduction1(String path){
		this.batchNumber = "1-";

		if(path!=null) this.logPath = path;
		this.sessionFile = new File(logPath+"session_Run1-Total-25oct.log");
		this.consentFile = new File(logPath+"consent_Run1-Total-25oct.log");

		boolean ok = loadConsents(1) && loadSessions(1);
		return ok;
	}


	private String nextTokenValue(String label, String line){
		if(line.length()<=0) return "";
		else{
			//System.out.print("label:"+ label);
			int labelStart = line.indexOf(label);
			int labelEnd = line.indexOf('=',labelStart+1);

			int valueEnd = line.indexOf(';',labelEnd);
			if(valueEnd<0 || valueEnd>=line.length())
				valueEnd = line.length();

			String value = line.substring(labelEnd+1, valueEnd).trim();
			//System.out.println(",value:"+value);
			return value;
		}
	}


	//---------------------------------------------------------------------------

	public boolean processConsentLineProduction1(String line){ 
		//System.out.println("Line : "+line);

		if(line==null || line.length()<=0 || line.indexOf("workerId")<0) //invalid line
			return false;


		//Time extraction
		String token = line.substring(0,line.indexOf("["));
		String timeStamp = extractTimeStamp(token.trim());
		this.timeStampList.add(timeStamp);

		//Event extraction
		//tokenizer.nextToken(""); //"workerId label"
		String workerId = this.batchNumber + nextTokenValue("workerId",line);

		token = nextTokenValue("EVENT", line);

		Worker worker;
		//Event resolution
		if(token.trim().matches("CONSENT")){
			//			tokenizer.nextToken(); //consentDate label
			String consentDate = nextTokenValue("consentDate",line);
			worker = new Worker(workerId,consentDate,null);

			if(!workerMap.containsKey(worker.getWorkerId())){
				//Add to the sequential list
				this.workerList.add(worker);

				//Add to the maps
				this.workerMap.put(worker.getWorkerId(), worker);
				this.consentMap.put(worker.getWorkerId(), worker);
			}

		}
		else
			if(token.trim().matches("SKILLTEST")){

				worker = this.workerMap.get(workerId);

				boolean[] gradeMap = new boolean[5];
				//test 1 label
				gradeMap[0] =  new Boolean(nextTokenValue("test1",line)); 
				//test 2 label
				gradeMap[1] =  new Boolean(nextTokenValue("test2",line)); 
				//test 3 label
				gradeMap[2] =  new Boolean(nextTokenValue("test3",line)); 
				//test 4 label
				gradeMap[3] =  new Boolean(nextTokenValue("test4",line)); 
				//test 5 label
				gradeMap[4] =  new Boolean(nextTokenValue("test5",line)); 
				
				//grade label
				String grade = nextTokenValue("grade",line);
				//testDuration label
				String duration = nextTokenValue("testDuration",line);
				worker.setGrade(new Integer(grade).intValue());

				//Retrieve answers
				worker.setSkillAnswers(null, gradeMap, null, new Integer(grade).intValue(), duration);

				this.workerMap.put(worker.getWorkerId(), worker);

				this.skillTestMap.put(worker.getWorkerId(), worker);
			}
			else
				if(token.trim().matches("SURVEY")){
					//sessionId label
					String sessionId = nextTokenValue("sessionId",line);
					sessionId=this.batchNumber+sessionId;
					String country = nextTokenValue("Country",line);
					String age = nextTokenValue("Age",line);
					String experience = nextTokenValue("Years progr.",line);
					String difficulty = nextTokenValue("Difficulty",line);
					String gender = nextTokenValue("Gender",line);
					worker = this.workerMap.get(workerId);

					worker.setSessionId(sessionId);
					worker.addSurveyAnswer(question[0], gender);
					worker.addSurveyAnswer(question[1], age);
					worker.addSurveyAnswer(question[2], country);
					worker.addSurveyAnswer(question[3], experience);
					worker.addSurveyAnswer(question[4], difficulty);
					//worker.addSurveyAnswer(question[5], feedback);

					this.workerMap.put(worker.getWorkerId(), worker);

					this.surveyMap.put(worker.getWorkerId(), worker);
				}


		return false;

	}


	public boolean processSessionLineProduction1(String line){

		//System.out.println("Line : "+line);

		if(line==null ||line.length()<=0 || line.indexOf("workerId")<0) //invalid line
			return false;


		//Time extraction
		String token = line.substring(0,line.indexOf("["));
		String timeStamp = extractTimeStamp(token.trim());
		this.timeStampList.add(timeStamp);

		String workerId = this.batchNumber + nextTokenValue("workerId",line);

		String sessionId = nextTokenValue("sessionId",line);

		//Check if session should be rejected
		if(this.batch1RejectMap.containsKey(sessionId)){
			return false;
		}

		sessionId=this.batchNumber+ sessionId; 
		WorkerSession session;



		//Event extraction
		token = nextTokenValue("EVENT", line);		

		//Event resolution
		if(token.matches("OPEN SESSION")){
			session = new WorkerSession(sessionId, new Vector<Microtask>()); 
			session.setWorkerId(workerId);
			Worker worker = this.workerMap.get(workerId);
			worker.setSessionId(sessionId);

			this.workerMap.put(workerId, worker);
			this.sessionMap.put(session.getId(), session);
			this.openedSessionMap.put(session.getId(), session);
		}
		else
			if(token.matches("MICROTASK")){

				session = this.sessionMap.get(sessionId);
				String microtaskId = nextTokenValue("microtaskId", line);
				//fileName label
				String fileName = nextTokenValue("fileName", line);

				//Some answers are associated to the wrong file name
				if(tasksDoesNotPertainToFileName(microtaskId,fileName))
					return false;

				//Some answers are duplicated
				if(taskAlreadyInSession(microtaskId,session))
					return false;

				//question label
				String question = nextTokenValue("question", line);
				// answer label
				String answer = nextTokenValue("answer", line);

				this.updateWorkerICantTellMap(workerId, answer);

				// duration label
				String duration = nextTokenValue("duration", line);
				// explanation label
				String explanation = nextTokenValue("explanation", line);

				double durationDouble = new Double(duration);

				//Discard answers that are too short
				if(durationDouble<this.minDuration)
					return false;

				this.countDuration(durationDouble);

				Answer answerObj = new Answer(answer, explanation, workerId,duration, timeStamp);

				//List of sequential microtasks
				Vector<Answer> singleAnswerList = new Vector<Answer>();
				singleAnswerList.add(answerObj);
				Microtask uniqueMicrotask = new Microtask(question, new Integer(microtaskId), singleAnswerList,fileName,0);
				this.microtaskList.add(uniqueMicrotask);

				//Map of Microtasks
				Vector<Answer> answerList = new Vector<Answer>();
				answerList.add(answerObj);

				Microtask microtask = this.microtaskMap.get(microtaskId);
				if(microtask==null){
					microtask = new Microtask(question, new Integer(microtaskId), answerList,fileName,0);
				}
				else{
					microtask.addAnswer(answerObj);
				}
				this.microtaskMap.put(microtaskId, microtask);

				//Session Microtask
				answerList = new Vector<Answer>();
				answerList.add(answerObj);
				Microtask sessionMicrotask = new Microtask(question, new Integer(microtaskId), answerList,fileName,0);
				Vector<Microtask> microtaskList = session.getMicrotaskList();
				microtaskList.add(sessionMicrotask);
				session.setMicrotaskList(microtaskList);
				this.sessionMap.put(session.getId(),session);
			}
			else
				if(token.matches("CLOSE SESSION")){
					if(!this.closedSessionMap.containsKey(sessionId)){
						session = this.sessionMap.get(sessionId);
						this.closedSessionMap.put(session.getId(),session);
					}
				}
		return true;
	}


	private void updateWorkerICantTellMap(String workerId, String answer){

		Integer count = this.workerICantTellMap.get(workerId);
		if(count==null) 
			count =new Integer(0);

		if (answer.matches(Answer.I_DONT_KNOW))
			count++;

		this.workerICantTellMap.put(workerId, count);
	}


	//---------------------------------------------------------------------------

	public int getNumberOfMicrotasks(){
		int counter=0;
		Iterator<String> iter = this.microtaskMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			Microtask task = this.microtaskMap.get(key);
			counter = counter + task.getNumberOfAnswers();
		}
		return counter;
	}


	public int getSurveys(){
		return this.surveyMap.size();
	}

	public int getConsents(){
		return this.consentMap.size();
	}

	public int getSkillTests(){
		return this.skillTestMap.size();
	}

	public int getOpenedSessions(){
		return this.openedSessionMap.size();
	}

	public int getClosedSessions(){
		return this.closedSessionMap.size();
	}

	public void printSummary(){
		System.out.println("Logs loaded! Totals are:");
		System.out.println("Consents: "+this.getConsents());
		System.out.println("SkillTests: "+this.getSkillTests());
		System.out.println("Surveys: "+this.getSurveys());
		System.out.println("Sessions Opened: "+this.getOpenedSessions());
		System.out.println("Sessions Closed: "+this.getClosedSessions());
		System.out.println("Answers in Map: "+this.getNumberOfMicrotasks());
		System.out.println("Microtasks in List: "+this.microtaskList.size());
		System.out.println("Workers in Map: "+this.workerMap.size());
	}


	private static LogData initializeLogs(){
		//String path = "C:\\Users\\adrianoc\\Dropbox (PE-C)\\3.Research\\2.Fall2014-Experiments\\RawDataLogs\\";
		String path = "C:/Users/igMoreira/Documents/GitHub/photinus/logs";
		LogData data = new LogData(true, 0);
		data.processLogProduction2(path);
		data.processLogProduction1(path);
		
		data.printSummary();

		return data;
	}



}

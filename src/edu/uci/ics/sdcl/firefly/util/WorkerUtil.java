package edu.uci.ics.sdcl.firefly.util;

import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileConsentDTO;

public class WorkerUtil {

	static HashMap<String, Worker> map; 
	
	private static void initialize(){
		FileConsentDTO consentDTO = new FileConsentDTO();
		map = consentDTO.getWorkers();
	}
	
	public static String getWorkerProfession(String workerID){
		if(map==null)
			initialize();
		
		Worker worker = map.get(workerID);
		return worker.getProfession();	
	}
	
	public static String getWorkerScore(String workerID){
		if(map==null)
			initialize();
		
		Worker worker = map.get(workerID);
		return worker.getGrade().toString();
	}
	
}

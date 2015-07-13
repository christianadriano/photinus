package edu.uci.ics.sdcl.firefly.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileConsentDTO;
import edu.uci.ics.sdcl.firefly.report.descriptive.FileSessionDTO;

/** 
 * This class holds methods to compare the completion code reported by workers in MTurk versus the 
 * completion code that was actually logged in the server.
 *  
 * @author adrianoc
 *
 */
public class HITApprovalUtil {
	
	public HashMap<String, String> getHITCodes(String path){
		
		HashMap<String, String> hitMap = new HashMap<String,String>();
		
		BufferedReader log = null;
		try {
			System.out.println("Batch: "+path);
			log = new BufferedReader(new FileReader(path));
			String line = log.readLine(); //discards first line
			if(line==null){ 
				log.close();
				return null;
			}
			int i=1;
			String repeated="repeated";
			while ((line = log.readLine()) != null) {
				if(line.equals(""))
					continue;
				String turkerID = line.split(",")[0];
				String code = line.split(",")[1]; //completion code entered by the worker
				//System.out.println(turkerID+":"+code);
				
				if(hitMap.containsKey(code)){
					System.out.println("repeated: "+turkerID+":"+code);
					hitMap.put(code + "_"+ repeated+"_"+i, code);
					i++;
				}

				hitMap.put(code, turkerID);
			}
			log.close();
			return hitMap;
			
		} catch (FileNotFoundException e) {
			System.out.println("DATABASE ERROR: Could not open database file.");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println("DATABASE ERROR: Could not read database file.");
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public static void main(String[] args){
		
		String logPath = "C:/firefly/logs/";
		String firstSession = "session-log-6.log";
		
		String sessionFile = firstSession; 
		
		
		FileSessionDTO sessionDTO = new FileSessionDTO(logPath+sessionFile);
		HashMap<String, WorkerSession> sessionMap = sessionDTO.getSessions();
		System.out.println("sessionMap size: "+ sessionMap.size());
		
		
		String mturkHITFile = "hit04_7batch1.csv";
		HITApprovalUtil hitUtil = new HITApprovalUtil();
		HashMap<String, String> hitMap = hitUtil.getHITCodes(logPath + mturkHITFile);
		System.out.println("hitMap size: "+ hitMap.size());
		
		//hitUtil.listInvalidSessions(hitMap, sessionMap); //1st step
		
		HashMap<String,String> map = hitUtil.listTuple(hitMap,sessionMap); //2nd step
		
		
		String bonusControl = "bonusControl.csv";
		
		HashMap<String,String> bonusMap = hitUtil.listBonus(map, logPath + bonusControl);//3rd step
		System.out.println("bonusMap size: "+ bonusMap.size());
		
		hitUtil.printBonusMap(bonusMap);
		
	}

	
	public HashMap<String,String> listTuple(HashMap<String, String> hitMap,
			HashMap<String, WorkerSession> sessionMap) {

		HashMap<String,String> map = new HashMap<String,String>();
		System.out.println("turkerId;workerId;sessionId");
		
		Iterator<String> codeIter = hitMap.keySet().iterator();
		while(codeIter.hasNext()){
			String code = codeIter.next();
			if(sessionMap.containsKey(code)){
				String turkerId = hitMap.get(code);
				WorkerSession worker =  sessionMap.get(code);
				String workerId = worker.getWorkerId();
				System.out.println(turkerId+";"+workerId+";"+code);
				map.put(turkerId, workerId);
			}
		}
		return map;
	}
	
	
	
	/** @return the list of invalid code and turkerID 
	 * */
	public HashMap<String,String> listInvalidSessions(HashMap<String, String> hitMap,
			HashMap<String, WorkerSession> sessionMap) {
		
		HashMap<String,String> invalidMap = new HashMap<String,String>();
		
		Iterator<String> codeIter = hitMap.keySet().iterator();
		while(codeIter.hasNext()){
			String code = codeIter.next();
			if(!sessionMap.containsKey(code)){
				invalidMap.put(code, hitMap.get(code));
				System.out.println("Code not found: "+ code);
			}
		}
		return invalidMap;
	}
	
	
	/** Lists all workers that still must be paid bonuses */
	public HashMap<String,String> listBonus(HashMap<String,String> map, String path){
		
		BufferedReader log = null;
		try {
			
			log = new BufferedReader(new FileReader(path));
			String line = log.readLine(); //discards first line
			if(line==null){ 
				log.close();
				return null;
			}
			while ((line = log.readLine()) != null) {
				if(line.equals(""))
					continue;
				String turkerID = line.split(",")[0].trim();
				String workerID = line.split(",")[1].trim(); //completion code entered by the worker
				//String sessionID = line.split(",")[2].trim();
				//System.out.println(turkerID+","+workerID+","+sessionID);
				
				if(map.containsKey(turkerID)){
					map.remove(turkerID);
				}
			}
			log.close();
			return map;
			
		} catch (FileNotFoundException e) {
			System.out.println("DATABASE ERROR: Could not open database file.");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println("DATABASE ERROR: Could not read database file.");
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void printBonusMap(HashMap<String,String> map){
		
		System.out.println("Pay bonus to following workers: ");
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			String turkerID = iter.next();
			String workerID = map.get(turkerID);
			System.out.println(turkerID+";"+workerID);
			
		}
	}
	
}
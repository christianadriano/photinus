package edu.uci.ics.sdcl.firefly.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.FileDebugSession;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

/** Decouples the persistence mechanism for Microtasks
 * 
 * Holds the index for all microtasks for all files. 
 * String key = file name, ArrayList is the list of microtasks
 * 
 * Hashtable<String,FileDebugSession> debugSessionMap;
 * 
 * @author Christian Adriano
 *
 */
public class MicrotaskStorage {

	private String persistentFileName = "microtasks.ser";
	private static MicrotaskStorage storage;
	private static Logger logger;
	private static Hashtable<String,FileDebugSession> debugSessionMap;

	public static synchronized MicrotaskStorage initializeSingleton(){
		if(storage == null){
			storage = new MicrotaskStorage();
		}
		return storage;
	}

	private MicrotaskStorage(){
		try{

			PropertyManager manager = PropertyManager.initializeSingleton();
			String path = manager.serializationPath;
			this.persistentFileName = path + this.persistentFileName;
			logger = LoggerFactory.getLogger("microtask");


			File file = new File(this.persistentFileName);
			if(!file.exists() ||  file.isDirectory()){
				// No files has been created yet. 

				// Create a sample object, that contains the default values.
				debugSessionMap = new Hashtable<String,FileDebugSession>();

				ObjectOutputStream objOutputStream = new ObjectOutputStream( 
						new FileOutputStream(new File(this.persistentFileName)));

				objOutputStream.writeObject( debugSessionMap );
				objOutputStream.close();
			}
			else
				debugSessionMap = this.retrieveIndex();
		}
		catch(IOException exception){
			exception.printStackTrace();
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
	}


	/** Retrieves the list of microtasks for provided file
	 * 
	 * @param fileName
	 * @return
	 */
	public synchronized FileDebugSession read(String fileName){

		if(debugSessionMap!=null && debugSessionMap.containsKey(fileName))
			return debugSessionMap.get(fileName);
		else
			return null;
	}

	/** Retrieves a map with all FileDebugSessions indexed by file name
	 * Each FileDebugSession corresponds to a buggy file (e.g., HIT02_24)
	 * @param 
	 * @return  HashMap<String,FileDebugSession>
	 */
	public synchronized Hashtable<String,FileDebugSession> readAllDebugSessions(){
		return debugSessionMap;
	}

	/** Retrieves all file debugging sessions .
	 * A debugging session is identified by a file name
	 * 
	 * @return a set of debugging sessions
	 */
	public synchronized Set<String> retrieveDebuggingSessionNames(){	

		if(debugSessionMap!=null && !debugSessionMap.isEmpty())
			return debugSessionMap.keySet();
		else
			return null;			
	}

	/** 
	 * Add a new answer to a microtask
	 * @param fileName  the file from which the codesnippet was obtained
	 * @param microtaskId the unique identifier for the microtask
	 * @param answer the answer obtained from the user
	 * @param elapsedTime 
	 * @param timeStamp 
	 * @return true if operation was successful, false otherwise
	 */
	public synchronized boolean insertAnswer(String fileName, Integer microtaskId, Answer answer){ 

		try{
			FileDebugSession newfileDebugSession = this.read(fileName);
			Microtask mtask = newfileDebugSession.getMicrotask(microtaskId);
			mtask.addAnswer(answer);
			newfileDebugSession.incrementAnswersReceived(mtask.getNumberOfAnswers());
			newfileDebugSession.insertMicrotask(microtaskId, mtask);
			logger.info("EVENT%MICROTASK% WorkerId%"+answer.getWorkerId()+"% SessionId%-% MicrotaskId%"+microtaskId+"% Question%"+ mtask.getQuestion()+"% Answer%"+answer.getOption()+"% Confidence Level%"+answer.getConfidenceOption()+"% Duration%"+answer.getElapsedTime()+"% Explanation%"+answer.getExplanation());
			return this.insert(fileName, newfileDebugSession);
		}
		catch(Exception e){
			return false;
		}
	}

	/** Insert a new List of Microtasks. It overwrites any existing one for the same file.
	 * 
	 * @param fileName the name of the file containing the methods for which microtasks were generated
	 * @param fileDebugSession the list of microtasks associated to the file
	 * @return true if operation succeeded, otherwise false.
	 */
	public synchronized boolean insert(String fileName, FileDebugSession newfileDebugSession){

		if(debugSessionMap!=null){

			//if(debugSessionMap.containsKey(fileName))
			//Has to merge the new one with the existing
			//	newfileDebugSession.append(debugSessionMap.get(fileName));	

			debugSessionMap.put(fileName, newfileDebugSession);
			return this.updateIndex(debugSessionMap);	
		}		
		else
			return false;
	}

	/** 
	 * Index is used to create the unique ID for the microtasks within the same file
	 * @param fileName
	 * @return the number of existing microtask for the file
	 */
	public int getNumberOfMicrotask() {

		int total=0;
		Iterator<String> iter = debugSessionMap.keySet().iterator();

		while(iter.hasNext()){
			String fileName = iter.next();
			if(debugSessionMap.containsKey(fileName))
				//Has to merge the new one with the existing
				total = total + debugSessionMap.get(fileName).getNumberOfMicrotasks();
		}
		return total;
	}


	/** Delete all data from the Storage */
	public void cleanUp(){
		debugSessionMap= new Hashtable<String,FileDebugSession>();
		this.updateIndex(debugSessionMap);
	}

	/**
	 * 
	 * @return the index of microtasks stored in the file
	 */
	@SuppressWarnings("unchecked")
	private synchronized Hashtable<String,FileDebugSession> retrieveIndex(){
		try{

			ObjectInputStream objInputStream = new ObjectInputStream( 
					new FileInputStream(new File(this.persistentFileName)));

			debugSessionMap = (Hashtable<String, FileDebugSession>) objInputStream.readObject();

			if(debugSessionMap==null)
				debugSessionMap = new Hashtable<String, FileDebugSession>();
			
			objInputStream.close();
			return debugSessionMap;
		}
		catch(IOException exception){
			logger.error(exception.toString());
			return null;
		}
		catch(Exception exception){
			logger.error(exception.toString());
			return null;
		}
	}

	/**
	 * 
	 * @param the index of microtasks stored in the file
	 * @return true if operation succeeded, otherwise false.
	 */
	private synchronized boolean updateIndex(Hashtable<String,FileDebugSession> debugSessionMap){
		try{
			if(debugSessionMap==null){
				logger.error("EVENT%ERROR% Trying to write a nullpointer to the microtask repository");
				return false;
			}else{

				ObjectOutputStream objOutputStream = new ObjectOutputStream( 
						new FileOutputStream(new File(this.persistentFileName)));

				objOutputStream.writeObject( debugSessionMap );
				objOutputStream.close();
				return true;
			}
		}
		catch(IOException exception){
			logger.error(exception.toString());
			return false;
		}
		catch(Exception exception){
			logger.error(exception.toString());
			return false;
		}
	}





}

package edu.uci.ics.sdcl.firefly.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Stack;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

/** 
 *  Writes, updates, and reads WorkerSession objects
 * 
 * WorkerSessions are organized in three groups: "New", "Active", "Closed"
 * 
 * @author Christian Adriano
 *
 */
public class WorkerSessionStorage {

	private static String persistentFileNameNew;
	private static String persistentFileNameClosed;

	private static String fileNameNew = "workersession_new.ser";
	private static String fileNameClosed = "workersession_closed.ser";

	private Hashtable<String, WorkerSession> activeSessionTable;

	private String path;

	private static WorkerSessionStorage storage=null;

	private WorkerSessionStorage(){
		PropertyManager manager = new PropertyManager();
		this.path = manager.serializationPath;

		try{		
			persistentFileNameNew = path + fileNameNew;
			File fileNewSession = new File(persistentFileNameNew);

			persistentFileNameClosed = path + fileNameClosed;
			File fileClosedSession = new File(persistentFileNameClosed);

			//If any of the files does not exist, them create them from scratch
			if(!fileNewSession.exists() ||  fileNewSession.isDirectory() || 
					!fileClosedSession.exists() ||  fileClosedSession.isDirectory() ){
				// No files has been created yet. 
				this.cleanUp();
			}
			else//No clean-up needed, just initialize the temporary datastructure.
				this.activeSessionTable=new Hashtable<String,WorkerSession>();
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
	}

	public static WorkerSessionStorage initializeSingleton(){
		if(storage==null)
			storage = new WorkerSessionStorage();
		return storage;
	}

	/** Create new storages */
	public  void cleanUp(){

		File file; 
		ObjectOutputStream objOutputStream; 

		try{
			//Initialize the NEW sessions FILE
			file = new File(persistentFileNameNew);

			Stack<WorkerSession> stack = new Stack<WorkerSession>();

			objOutputStream = new ObjectOutputStream( 
					new FileOutputStream(file));

			objOutputStream.writeObject( stack );
			objOutputStream.close();

			//Initialize the CLOSED sessions FILE
			file = new File(persistentFileNameClosed);

			Vector<WorkerSession> list = new Vector<WorkerSession>();

			objOutputStream = new ObjectOutputStream( 
					new FileOutputStream(file));

			objOutputStream.writeObject( list );
			objOutputStream.close();

			//Initialize the ACTIVE sessions TABLE
			activeSessionTable = new   Hashtable<String, WorkerSession>();

		}
		catch(IOException exception){
			exception.printStackTrace();
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
	}



	//-----------------------------------------------------------------------------------------------------------
	// Manage the Storage of New WorkerSession 

	/**
	 * 
	 * @param newStack to be persisted (overwrites the existing one)
	 * @param type specify if this is a stack of copies or a stack of original, because they are stored separately
	 * @return true is operation was successful, otherwise, false.
	 */
	public boolean writeNewWorkerSessionStack(Stack<WorkerSession> newStack){
		try{
			ObjectOutputStream objOutputStream = new ObjectOutputStream( 
					new FileOutputStream(new File(persistentFileNameNew)));
			objOutputStream.writeObject( newStack );
			objOutputStream.close();
			return true;
		}
		catch(IOException exception){
			exception.printStackTrace();
			return false;
		}
		catch(Exception exception){
			exception.printStackTrace();
			return false;
		}
	}

	/**
	 * Obtains a WorkerSession from the stack of new WorkerSessions or the copies stack. Prioritizes the 
	 * the stack with new original WorkerSessions. Only when this last one is empty, then reads from the 
	 * copies stack. After that the method moves the retrieved WorkerSession to the list of active ones.
	 * 
	 * @return a workerSession from the stack new WorkerSessions. If the stack is empty returns null.
	 */
	public WorkerSession readNewWorkerSession(){

		Stack<WorkerSession> stack = retrieveNewSessionStorage();
		if(stack.isEmpty())
			return null;
		else{
			WorkerSession session = stack.pop(); 
			if(writeNewWorkerSessionStack(stack)) //Save the updated stack
				return session;
			else
				return null;
		}
	}

	/**
	 * @return the size of the stack of new WorkerSessions
	 */
	public int getNumberOfNewWorkerSessions(){
		Stack<WorkerSession> stack = retrieveNewSessionStorage();
		if(stack!=null && !stack.isEmpty())
			return stack.size();
		else
			return 0;
	}


	//-----------------------------------------------------------------------------------------------------------
	// Manage the Storage of Active WorkerSessions

	/** 
	 * @return true if operation succeeded, otherwise false.
	 */
	public boolean updateActiveWorkerSession(WorkerSession session){

		if(activeSessionTable !=null){
			if(!session.hasCurrent()){ //it means that the Session was completed
				activeSessionTable.remove(session.getId()); //removes from active map
				return (addClosedWorkerSession(session)); //moves to closed list
			}
			else{//Session still have uncompleted microtasks
				activeSessionTable.put(session.getId(),session);
				return true;
			}
		}
		else
			return false;
	}

	/**
	 * @param sessionId the identifier for the session
	 * @param microtaskId the identifier of a microtask in that session
	 * @param answer the answer provided by a user
	 * @return true if operation was successful, otherwise false
	 */
	public boolean setSessionMicrotaskAnswer(String sessionId,Integer microtaskId, Answer answer) {

		WorkerSession session = this.readActiveWorkerSessionByID(sessionId);
		if(session==null){
			System.out.println("Session is null, id= "+sessionId);
			return false;
		}
		else{
			if(session.insertMicrotaskAnswer(microtaskId,answer)){
				//log the operations in a text file
				//writeLog (SessionId, File Name, Method Name, Question, Answer, Time Duration);
				return(this.updateActiveWorkerSession(session));
			}
			else
				return false;
		}
	}

	/** Retrieves a WorkerSession
	 * 
	 * @param userId the unique identifier for a WorkerSession
	 * @return a WorkerSession, in case any was found, return null.
	 */
	public WorkerSession readActiveWorkerSessionByID(String sessionId){

		if(activeSessionTable!=null && activeSessionTable.containsKey(sessionId))
			return activeSessionTable.get(sessionId);
		else
			return null;
	}



	//----------------------------------------------------------------------------------------------------------
	//Manage Closed Sessions


	public boolean addClosedWorkerSession(WorkerSession session){
		Vector<WorkerSession> closedSessionList = retrieveClosedSessionStorage();
		if(closedSessionList!=null){
			closedSessionList.add(session);
			if(this.overwriteClosedWorkerSessionList(closedSessionList))
				return true;
			else
				return false;
		}
		else
			return false;
	}


	/**
	 * 
	 * @param closedSessionList the list of closed WorkerSession (it will overwrite the existing one)
	 * @return true if operation succeeded, otherwise false.
	 */
	private boolean overwriteClosedWorkerSessionList(Vector<WorkerSession> closedSessionList){
		try{
			ObjectOutputStream objOutputStream = new ObjectOutputStream( 
					new FileOutputStream(new File(persistentFileNameClosed)));

			objOutputStream.writeObject( closedSessionList );
			objOutputStream.close();
			return true;
		}
		catch(IOException exception){
			exception.printStackTrace();
			return false;
		}
		catch(Exception exception){
			exception.printStackTrace();
			return false;
		}
	}

	//------------------------------------------------------------------------------------------------------------------------
	// Retrieve the storages
	
	public Stack<WorkerSession> retrieveNewSessionStorage(){
		try{

			File file = new File(persistentFileNameNew); 

			ObjectInputStream objInputStream = new ObjectInputStream( 
					new FileInputStream(file));

			Stack<WorkerSession> stack  = (Stack<WorkerSession> ) objInputStream.readObject();
			objInputStream.close();

			return stack;
		}
		catch(IOException exception){
			exception.printStackTrace();
			return null;
		}
		catch(Exception exception){
			exception.printStackTrace();
			return null;
		}
	}


	public  Vector<WorkerSession> retrieveClosedSessionStorage(){
		try{

			File file = new File(persistentFileNameClosed); 

			ObjectInputStream objInputStream = new ObjectInputStream( 
					new FileInputStream(file));

			Vector<WorkerSession> list = (Vector<WorkerSession>) objInputStream.readObject();
			objInputStream.close();

			return list;
		}
		catch(IOException exception){
			exception.printStackTrace();
			return null;
		}
		catch(Exception exception){
			exception.printStackTrace();
			return null;
		}
	}

	public Hashtable<String, WorkerSession> retrieveActiveSessionStorage() {
		return this.activeSessionTable;
	}

}


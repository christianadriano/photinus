package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;
import java.util.ArrayList;

public class WorkerSession implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/** unique identifier for the working session object. It uniquely maps to a HIT in Mechanical Turk */
	private Integer id;
	
	/** All microtask package in the same worker session. See WorkerSessionFactory for better context. */
	private ArrayList<Microtask> microtaskList;
	
	/** Keeps track of the position of the current microtask */
	private Integer currentIndex;

	/** 
	 * Initializes the array and the counter to the first position in 
	 * @param microtaskList
	 * the array */
	public WorkerSession(Integer id, ArrayList<Microtask> microtaskList){
		this.id = id;
		this.microtaskList = microtaskList;
		if(this.microtaskList!=null && this.microtaskList.size()>0)
			this.currentIndex = 0; //points to the first element
		else
			this.currentIndex = -1;
	}

	/** 
	 * Save a microtask and increments the index to point to the next in the list
	 * @param task the microtask that was answered
	 * @return true if the microtask was effectively stored, false otherwise.
	 */
	public boolean storeCurrentMicrotask(Microtask task){
		if(hasCurrent()){
			this.microtaskList.add(this.currentIndex,task);
			this.currentIndex++; 
			return true;
		}
		else
			return false;
	}
	
	/**
	 * 
	 * @return true if the counter points to a position within the list, otherwise false.
	 */
	public boolean hasCurrent(){
		if(currentIndex<0 || currentIndex>microtaskList.size())
			return false;
		else
			return true;
	}

	public Integer getId(){
		return this.id;
	}
	
	/**
	 * 
	 * @return null if the list is empty or the counter already reached the end of the list.
	 */
	public Microtask getCurrentMicrotask(){
		if(!hasCurrent())
			return null;
		else
			return this.microtaskList.get(currentIndex);
	}

	/** 
	 * 
	 * @return the position for the current microtask in the list. The position can be out of the range of the list. For that, use the method hasCurrent(); 
	 */
	public Integer getCurrentIndex(){
		return this.currentIndex;
	}

	public ArrayList<Microtask> getMicrotaskList() {
		return this.microtaskList;
	}

	
}

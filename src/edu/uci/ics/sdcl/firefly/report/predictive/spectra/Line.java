package edu.uci.ics.sdcl.firefly.report.predictive.spectra;

import java.util.Vector;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;

/** 
 * Represents a line in the source code that under a fault localization investigation.
 * 
 * @author adrianoc
 *
 */
public class Line extends Microtask{


	/** Simplified version with only the data needed to write a Session Report */
	public Line(String question, Integer ID, Vector<Answer> answerList, String fileName)	{
		
		super(question,ID,answerList,fileName,0);
		this.answerList = answerList;
		this.ID = ID;
		this.question = question;
		this.fileName = fileName;
	}

}

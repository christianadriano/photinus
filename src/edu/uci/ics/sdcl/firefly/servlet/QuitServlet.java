package edu.uci.ics.sdcl.firefly.servlet;

import java.io.IOException; 

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.uci.ics.sdcl.firefly.MicrotaskContextFactory;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.controller.StorageStrategy;

/**
 * Servlet implementation class MicrotaskController
 * 
 *  * @author Christian Medeiros Adriano
 */
public class QuitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String ErrorPage = "/ErrorPage.jsp";
	private String ThanksPage = "/ThanksQuit.jsp";
	private StorageStrategy storage ;
	private String workerId;
	private String answer;
	
	public static final String DIFFICULT="THE TASK IS TOO DIFFICULT";
	public static final String BORING="THE TASK IS TOO BORING";
	public static final String LONG="THE TASK IS TOO LONG";
	public static final String OTHER="OTHER";

	private MicrotaskContextFactory workerSessionSelector;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QuitServlet() {
		super();
		this.workerSessionSelector = new MicrotaskContextFactory();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	/**
	 * 	Collects and persist the answer. Also marks the microtask as already answered
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("In MicrotaskServlet ");
		if(request.getParameter("workerId").equals("consentForm")){
			request.setAttribute("workerId", request.getParameter("workerId")); 
			request.getRequestDispatcher(ThanksPage).include(request, response);
			storage = StorageStrategy.initializeSingleton();
			storage.insertQuitReason(null, "consentForm");
		}else if(request.getParameter("reason")!=null){
			request.setAttribute("workerId", request.getParameter("workerId")); 
			this.workerId = request.getParameter("workerId");
			this.answer = mapAnswerValue(request.getParameter("reason"),request.getParameter("otherReason"));
			//Restore data for next Request
			request.setAttribute("workerId",this.workerId);

			//String subAction = request.getParameter("subAction");

			storage = StorageStrategy.initializeSingleton();
			Worker worker = storage.readExistingWorker(this.workerId);
			storage.insertQuitReason(worker, this.answer);
			request.getRequestDispatcher(ThanksPage).include(request, response);
		}
		

	}

	private void showErrorPage(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException {
		request.setAttribute("error", message);
		request.setAttribute("executionId", this.workerId);
		request.getRequestDispatcher(ErrorPage).include(request, response);
	}
	
	protected String mapAnswerValue(String answer, String otherOption){
		answer = answer.toLowerCase();
		if(answer.equals("difficult")){
			return DIFFICULT;
		}else if(answer.equals("boring")){
			return BORING;
		}else if(answer.equals("long")){
			return LONG;
		}else{
			return OTHER+" "+otherOption;
		}
	}
	
	protected String mapAnswerValue(String answer){
		answer = answer.toLowerCase();
		if(answer.equals("difficult")){
			return DIFFICULT;
		}else if(answer.equals("boring")){
			return BORING;
		}else if(answer.equals("long")){
			return LONG;
		}else{
			return OTHER;
		}
	}

}

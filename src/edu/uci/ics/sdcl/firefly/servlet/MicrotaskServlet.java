package edu.uci.ics.sdcl.firefly.servlet;

import java.io.IOException; 

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.core.dom.ThisExpression;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.MicrotaskContextFactory;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.controller.StorageStrategy;
import edu.uci.ics.sdcl.firefly.util.TimeStampUtil;

/**
 * Servlet implementation class MicrotaskController
 * 
 *  * @author Christian Medeiros Adriano
 */
public class MicrotaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String FeedbackPage = "/Feedback.jsp";
	private String ErrorPage = "/ErrorPage.jsp";
	private String QuestionMicrotaskPage = "/QuestionMicrotask.jsp";
	private final String MicrotaskLoadTestPage = "/MicrotaskLoadTest.jsp";
	private StorageStrategy storage ;
	private Worker worker;

	private MicrotaskContextFactory workerSessionSelector;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MicrotaskServlet() {
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String workerId = request.getParameter("workerId");
		if(workerId!=null){//Discard request silently. This is necessary for spurious requests from IE
			storage = StorageStrategy.initializeSingleton();
			this.worker = storage.readExistingWorker(workerId);
			String microtaskId = request.getParameter("microtaskId");
			if(this.worker!=null && microtaskId!=null){//Discard request silently. Necessary for repeated request from Chrome
				String sessionId = this.worker.getSessionId();
				//Restore data for next Request
				request.setAttribute("workerId",workerId);
				request.setAttribute("sessionId", sessionId);
				//System.out.println("In MicrotaskServlet: "+sessionId);
				loadNextMicrotask(request, response);
			}
		}
	}

	private void loadNextMicrotask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		int answer = new Integer(request.getParameter("answer")).intValue();
		int confidenceAnswer;
		String test = request.getParameter("confidence");
		if(request.getParameter("confidence")==null){
			confidenceAnswer = 0;
		}else{
			confidenceAnswer = new Integer(request.getParameter("confidence")).intValue();
		}
		String microtaskId = request.getParameter("microtaskId");
		String explanation = request.getParameter("explanation");
		if(explanation!=null)
			explanation = explanation.replaceAll("[\n\r]"," ");
		String timeStamp = request.getParameter("timeStamp");
		int difficulty = new Integer(request.getParameter("difficulty")).intValue();
		String elapsedTime = TimeStampUtil.computeElapsedTime(timeStamp, TimeStampUtil.getTimeStampMillisec());


		//Save answers from the previous microtask
		boolean success=false;
		if(this.worker.getSessionId()!=null && microtaskId!=null){
			success = storage.updateMicrotaskAnswer(
					this.worker.getSessionId(), new Integer(microtaskId),
					new Answer(Answer.mapOptionToString(answer), confidenceAnswer,explanation, 
							this.worker.getWorkerId(), elapsedTime, timeStamp, difficulty,0,
							this.worker.getSessionId())
					);
		}
		
		if(!success){
			this.showErrorPage(request, response, "Your answer could not be stored. In case you have used the back button, "
					+ "please restart the HIT by going to the following link: http://dellserver.ics.uci.edu:8080/firefly/ConsentForm.jsp");
		}
		else{
			//Restore data for next Request
			request.setAttribute("timeStamp", TimeStampUtil.getTimeStampMillisec());

			//Continue working on existing session
			Microtask microtask = storage.getNextMicrotask(this.worker.getSessionId());	

			//Decide where to send to send the worker
			if(microtask==null){
				//No more microtasks, move to the Survey page
				request.setAttribute("key", this.worker.getSessionId());
				request.getRequestDispatcher(FeedbackPage).include(request, response);
			}
			else{
				//Displays a new microtask
				WorkerSession session = storage.readActiveSessionById(this.worker.getSessionId());
			
				//Data for the Progress bar
				request.setAttribute("currentTask", session.getCurrentIndexPlus());
				request.setAttribute("totalTasks", session.getMicrotaskListSize());
				request = MicrotaskServlet.generateRequest(request, microtask);
		
				//For load test
				Boolean isTest = Boolean.valueOf(request.getParameter("isTest"));
				//System.out.println("MicrotaskServlet - isTest: "+isTest);
				
				if(isTest)
					request.getRequestDispatcher(MicrotaskLoadTestPage).forward(request, response);
				else
					request.getRequestDispatcher(QuestionMicrotaskPage).forward(request, response);
				
				
			}
		}
	}

	private void showErrorPage(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException {
		request.setAttribute("error", message);
		request.setAttribute("executionId", this.worker.getWorkerId());
		request.getRequestDispatcher(ErrorPage).include(request, response);
	}

	
	
	/** Populate needed attributes from the microtask in the HTTP request 
	 * 
	 * @param request
	 * @param task
	 * @return the new request with data to be displayed on the web page
	 */
	public static HttpServletRequest generateRequest(HttpServletRequest request, Microtask task){

		//System.out.println("Retrieved microtask id:"+task.getID()+" answers: "+task.getAnswerList().toString());
		//	System.out.println("Retrieved microtask bug report:" + task.getFailureDescription() +  " from fileName: "+task.getCodeSnippet().getFileName());
		
		request.setAttribute("microtaskId", task.getID());  
		request.setAttribute("bugReport", task.getFailureDescription());
		request.setAttribute("testCase", task.getTestCase());
		request.setAttribute("question", task.getQuestion());
	
		//set source code of codeSnippet - First ACE Editor
		request.setAttribute("source", task.getCodeSnippet().getCodeSnippetFromFileContent()); 	
		request.setAttribute("sourceLOCS", task.getCodeSnippet().getLOCS());
		request.setAttribute("calleesInMain", task.getSnippetHightlights());

		//sets caller codeSnippets - Second ACE Editor
		request.setAttribute("callerLOCS", task.getCallerLOCS());
		request.setAttribute("caller", task.getCallerFileContent());
		request.setAttribute("positionsCaller", task.getCallerHightlights());

		//sets callee codeSnippets - Third ACE Editor
		request.setAttribute("callee", task.getCalleeFileContent());
		request.setAttribute("calleeLOCS", task.getCalleeLOCS());
		request.setAttribute("positionsCallee", task.getCalleeHightlights());
		
		request.setAttribute("explanation",""); //clean up the explanation field.
		request.setAttribute("startLine", task.getStartingLine());
		request.setAttribute("startColumn", task.getStartingColumn());
		request.setAttribute("endLine", task.getEndingLine());
		request.setAttribute("endColumn", task.getEndingColumn());

		request.setAttribute("methodStartingLine", task.getCodeSnippet().getElementStartingLine());

		return request;
	}


}

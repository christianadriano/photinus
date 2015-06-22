package edu.uci.ics.sdcl.firefly.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.internal.core.dom.rewrite.RewriteEventStore;

import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.controller.StorageStrategy;
import edu.uci.ics.sdcl.firefly.storage.SkillTestSource;
import edu.uci.ics.sdcl.firefly.util.TimeStampUtil;

/**
 * Servlet implementation class SurveyServlet
 */
public class SurveyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public static String question[] = {"Gender", "Age", "Country", "Experience", "Language", "YearsProgramming", "Learned"};   
	
    private final String SkillTestPage = "/SkillTest.jsp";
    
    public SurveyServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StorageStrategy storage = StorageStrategy.initializeSingleton();
		String workerId = request.getParameter("workerId");
		Worker worker = storage.readExistingWorker(workerId);
		if (null != worker){
			//WorkerSession  session = storage.readActiveSessionById(worker.getSessionId());
			worker.addSurveyAnswer(question[0], request.getParameter("gender"));
			worker.addSurveyAnswer(question[1], request.getParameter("age"));
			worker.addSurveyAnswer(question[2], request.getParameter("country"));
			worker.addSurveyAnswer(question[3], mapExperience(request.getParameter("experience"),request.getParameter("otherexperience")));
			worker.addSurveyAnswer(question[4], request.getParameter("language"));
			worker.addSurveyAnswer(question[5], request.getParameter("years"));
			worker.addSurveyAnswer(question[6], request.getParameter("hlearned"));

			//Store result
			storage.insertSurvey(worker);
			request.setAttribute("workerId",worker.getWorkerId());
			//request.setAttribute("sessionId",worker.getSessionId());
			request.setAttribute("timeStamp", TimeStampUtil.getTimeStampMillisec());
			request = loadQuestions(request, response);
			//request = MicrotaskServlet.generateRequest(request, storage.getNextMicrotask(session.getId()));
			request.getRequestDispatcher(SkillTestPage).forward(request, response);
		}
		else{
			request.setAttribute("executionId", request.getParameter("workerId"));
			request.setAttribute("error", "@SurveyServlet - object 'worker' is null");
			//Displays the error page
			request.getRequestDispatcher("/ErrorPage.jsp").forward(request, response);
		}	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	protected String mapExperience(String level, String other){
		if(level.equals("1")){
			return "Professional_Developer";
		}else if(level.equals("2")){
			return "Graduate_Student";
		}else if(level.equals("3")){
			return "Undergraduate_Student";
		}else if(level.equals("4")){
			return "Hobbyist";
		}else{
			return "Other "+other;
		}
	}
	
	private HttpServletRequest loadQuestions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SkillTestSource source = new SkillTestSource();
		request.setAttribute("editor1", source.getSourceOne());
		request.setAttribute("subAction", "gradeAnswers");
		return request;
	}

}

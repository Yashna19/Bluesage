package com.wtc.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.wtc.Database.DatabaseConnection;

/**
 * Servlet implementation class DeleteScheduleScriptServlet
 */
public class DeleteScheduledScripts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatabaseConnection obj = new DatabaseConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteScheduledScripts() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			String reqType = request.getParameter("scriptRequest");
			String scriptId = request.getParameter("scriptId");
			int sId = 0;
			if (scriptId != null) {
				sId = Integer.parseInt(scriptId);
			}
			JSONObject json = new JSONObject();
			if ((sId > 0) && (reqType.equalsIgnoreCase("delScript"))) {
				json = obj.deleteScript(sId);
			} else {
				json.put("success", false);
				json.put("message", "Failed due to insufficient Data");
			}
			out.print(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.wtc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.wtc.Database.DatabaseConnection;

/**
 * Servlet implementation class ScheduleScriptServlet
 */
public class ScheduleScriptServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ScheduleScriptServlet() {
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
		DatabaseConnection obj = null;
		JSONObject json = new JSONObject();
		String scriptValue = null;
		String scriptName = null;
		String executionCount = null;
		String startD = null;
		String startDTtime = null;
		String startDT = null;
		String endD = null;
		String endDTtime = null;
		String endDT = null;
		String filePath = null;

		try {
			scriptValue = (String) request.getAttribute("scriptValue");
			scriptName = (String) request.getAttribute("scriptName");
			executionCount = (String) request.getAttribute("executionCount");
			startD = (String) request.getAttribute("startD");
			startDTtime = (String) request.getAttribute("startDTtime");
			endD = (String) request.getAttribute("endD");
			endDTtime = (String) request.getAttribute("endDTtime");
			filePath = (String) request.getAttribute("filePath");

			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

			startDT = startD + " " + startDTtime;
			Date d1 = df2.parse(startDT);
			String sDT = df1.format(d1);

			endDT = endD + " " + endDTtime;
			Date d2 = df2.parse(endDT);
			String eDT = df1.format(d2);

			if (executionCount.length() == 0) {
				executionCount = "0";

			}
			int count = Integer.parseInt(executionCount);

			if ((scriptValue != null) && (scriptName != null) && (startDT != null) && (endDT != null) && (count > 0)) {
				obj = new DatabaseConnection();
				json = obj.scheduleScript(scriptName, scriptValue, sDT, eDT, count, filePath);
			} else {
				json.put("success", false);
				json.put("message", "Please provide the complete data");
			}

			//File uploaded successfully
			request.setAttribute("message", "File Uploaded Successfully");
		} catch (Exception e) {
			json.put("success", false);
			json.put("message", "Some thing went wrong");
			e.printStackTrace();
		}
		out.print(json);
	}
}

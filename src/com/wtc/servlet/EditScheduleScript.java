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
 * Servlet implementation class EditScheduleScript
 */
public class EditScheduleScript extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatabaseConnection obj = new DatabaseConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditScheduleScript() {
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
		JSONObject json = new JSONObject();
		String scriptValue = null;
		String executionCount = null;
		String startD = null;
		String startDTtime = null;
		String startDT = null;
		String endD = null;
		String endDTtime = null;
		String endDT = null;
		String filePath = null;
		String scriptId = null;
		String scriptName=null;

		try {
			scriptValue = (String) request.getAttribute("scriptValue");
			executionCount = (String) request.getAttribute("executionCount");
			startD = (String) request.getAttribute("startD");
			startDTtime = (String) request.getAttribute("startDTtime");
			endD = (String) request.getAttribute("endD");
			endDTtime = (String) request.getAttribute("endDTtime");
			filePath = (String) request.getAttribute("filePath");
			scriptId = (String) request.getAttribute("scriptId");
			scriptName=(String)request.getAttribute("scriptName");

			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			DateFormat df3=new SimpleDateFormat("MM/dd/yyyy hh:mm a");

			startDT = startD + " " + startDTtime;
			Date d1=null;
			if(startDT.contains("AM")||startDT.contains("PM")){
				d1= df3.parse(startDT);
			}else{
			 d1= df2.parse(startDT);
			}
			String sDT = df1.format(d1);

			endDT = endD + " " + endDTtime;
			Date d2=null;
			if(endDT.contains("AM")||endDT.contains("PM")){
				d2= df3.parse(endDT);
			}else{
			 d2= df2.parse(endDT);
			}
			String eDT = df1.format(d2);

			int count = Integer.parseInt(executionCount);
			int sId = Integer.parseInt(scriptId);
			json = obj.editScript(scriptName,scriptValue, sId, sDT, eDT, count, filePath);
		} catch (Exception e) {
			json.put("success", false);
			json.put("message", "Something went wrong");
			e.printStackTrace();
		}

		out.print(json);
	}

}

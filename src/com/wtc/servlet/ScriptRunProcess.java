package com.wtc.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.json.simple.JSONObject;
import org.testng.TestListenerAdapter;
import com.beust.testng.TestNG;
import com.google.gson.JsonObject;

public class ScriptRunProcess extends HttpServlet {
	
	public int errorCount;

	public ScriptRunProcess() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = null;
		JSONObject json = new JSONObject();
		String script_id = null;
		String filePath = null;
		String scriptName = null;
		if (request.getParameter("comboValue") != null) {
			script_id = request.getParameter("comboValue");
			filePath = request.getParameter("filePath");
			scriptName = request.getParameter("scriptName");
		} else {
			script_id = (String) request.getAttribute("comboValue");
			filePath = (String) request.getAttribute("filePath");
			scriptName = (String) request.getAttribute("scriptName");
		}
		String report = (String) request.getAttribute("report");
		out = response.getWriter();
		response.setContentType("text/html");
		if (report == null || report == "") {
			try {
				TestListenerAdapter tla = new TestListenerAdapter();
				ClassLoader classLoader = ScriptRunProcess.class.getClassLoader();
				TestNG testng = new TestNG();

				if (script_id.equals("PRIMECONV11_2")) {
					Class aClass = classLoader.loadClass("com.wtc.conventionalscripts.DCUTwoBorrower");
					aClass.getMethod("setScript_id", String.class).invoke(aClass.newInstance(), script_id);
					System.out.println("aClass.getName() = " + aClass.getName());
					Class[] classes = new Class[] { aClass };
					testng.setTestClasses(classes);
					testng.addListener(tla);
					testng.run();
				}  else if (script_id.equals("PRIMECONV_2")) {
					Class aClass = classLoader.loadClass("com.wtc.conventionalscripts.DCUTwoBorrower");
					Object o = aClass.newInstance();
					aClass.getMethod("setScript_id", String.class).invoke(o, script_id);
      				testng.setOutputDirectory("../DCU/testOutput"); 
					aClass.getMethod("setFilePath", String.class).invoke(o, filePath);
					System.out.println("aClass.getName() = " + aClass.getName());
					Class[] classes = new Class[] { aClass };
					testng.setTestClasses(classes);
					testng.addListener(tla);
					testng.run();
//					errorCount = ((com.wtc.conventionalscripts.DCUTwoBorrower) o).getErrorCount();
//					((com.wtc.conventionalscripts.DCUTwoBorrower) o).setErrorCount(0);
//					System.out.println(errorCount + "   :errorCount");

				}
				ScriptHtml o = new ScriptHtml();
				o.saveErrorLogHtml(script_id, scriptName, errorCount, ((ServletConfig) request).getServletContext());
				json.put("success", true);
				json.put("Data", "script executed successfully");
			} catch (Exception ex) {
				json.put("success", false);
				json.put("message", "Some thing went wrong");

				ex.printStackTrace();
			}
			out.print(json);

		} else if (report.equalsIgnoreCase("report")) {
//			XLSTReportGen(response);

		}

	}

	public void XLSTReportGen(HttpServletResponse response) {/*
		try {
			File buildFile = new File("D:\\newworkspace\\DCU\\build.xml");
			Project project = new Project();
			project.setUserProperty("ant.file", buildFile.getAbsolutePath());
			project.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			project.addReference("ant.projectHelper", helper);
			helper.parse(project, buildFile);
			project.executeTarget(project.getDefaultTarget());
			Thread.sleep(15000);
			PrintWriter out = response.getWriter();
			JsonObject myObj = new JsonObject();
			myObj.addProperty("success", true);
				out.println(myObj.toString());
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	*/}

}

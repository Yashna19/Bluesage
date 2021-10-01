package com.wtc.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;

import com.wtc.Database.DatabaseConnection;

/**
 * Servlet implementation class NavigationServlet
 */
public class NavigationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SAVE_DIR = "Scripts";
	public Properties properties;
	DatabaseConnection obj = new DatabaseConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NavigationServlet() {
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
		String scriptParams[] = null;
		String scriptValue = null;
		String scriptName = null;
		String executionCount = null;
		String startD = null;
		String startDTtime = null;
		String endD = null;
		String endDTtime = null;
		String filePath = null;
		String method = null;
		String scriptId = null;
		//		String appRealPath = request.getServletContext().getRealPath("");
		obj.readPropertiesFile();

		String appPath = obj.properties.getProperty("local.project.path");
		System.out.println(appPath);

		String tmpDir = System.getProperty("java.io.tmpdir");

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (isMultipart) {
			try {
				List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

				for (FileItem item : multiparts) {
					if (!item.isFormField()) {
						String name = new File(item.getName()).getName();
						if (scriptValue != null) {
							String fileName = FilenameUtils.getName(item.getName());

							if (fileName != null && fileName.length() > 0) {
								Long curMilliSecs = System.currentTimeMillis();
								curMilliSecs.toString();
								String savePath = null;

								if (method.equalsIgnoreCase("1")) {
									savePath = tmpDir + File.separator + SAVE_DIR + File.separator + curMilliSecs;

								} else {
									savePath = appPath + File.separator + SAVE_DIR;

								}

								String date = startD;

								if (startD == null) {
									date = new Timestamp(System.currentTimeMillis()).toString();

								}
								File finalDir = new File(savePath + File.separator + scriptValue + File.separator + curMilliSecs + File.separator
												+ date.replaceAll("[^\\w\\s]", ""));
								if (!finalDir.exists()) {
									finalDir.mkdirs();
								}
								File file = new File(finalDir.toString() + File.separator + fileName);
								item.write(file);
								filePath = finalDir.toString() + File.separator + name;

							}
						}
						System.out.println(name);
					} else {
						System.err.println(item.getString());
						item.getFieldName();
						System.out.println(item.getFieldName() + item.getString());
						if (item.getFieldName().equalsIgnoreCase("scriptValue")) {
							scriptParams = item.getString().split(",");
							scriptValue = scriptParams[0];
							scriptName = scriptParams[1];
							request.setAttribute("scriptValue", scriptValue);
							request.setAttribute("scriptName", scriptName);
						} else if (item.getFieldName().equalsIgnoreCase("executionCount")) {
							executionCount = item.getString();
							request.setAttribute("executionCount", executionCount);
						} else if (item.getFieldName().equalsIgnoreCase("startDT")) {
							startD = item.getString();
							request.setAttribute("startD", startD);
						} else if (item.getFieldName().equalsIgnoreCase("startDTtime")) {
							startDTtime = item.getString();
							request.setAttribute("startDTtime", startDTtime);
						} else if (item.getFieldName().equalsIgnoreCase("endDT")) {
							endD = item.getString();
							request.setAttribute("endD", endD);
						} else if (item.getFieldName().equalsIgnoreCase("endDTtime")) {
							endDTtime = item.getString();
							request.setAttribute("endDTtime", endDTtime);
						} else if (item.getFieldName().equalsIgnoreCase("method")) {
							method = item.getString();
							request.setAttribute("method", method);
						} else if (item.getFieldName().equalsIgnoreCase("scriptId")) {
							scriptId = item.getString();
							request.setAttribute("scriptId", scriptId);
						}

					}
				}
				request.setAttribute("filePath", filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ((executionCount != null) && (startD != null) && (startDTtime != null) && (endD != null) && (endDTtime != null)
							&& (scriptParams != null) && (method.equals("2")) && (scriptId.length() == 0)) {
				RequestDispatcher rd = request.getRequestDispatcher("ScheduleScriptServlet");
				rd.forward(request, response);
			} else if ((scriptParams != null) && (method.equals("1"))) {
				
				
				try {
					request.setAttribute("comboValue", scriptValue);
					RequestDispatcher rd = request.getRequestDispatcher("ScriptRunProcess");
					rd.forward(request, response);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			} else if ((executionCount != null) && (startD != null) && (startDTtime != null) && (endD != null) && (endDTtime != null)
							&& (scriptParams != null) && (scriptId.length() > 0)) {
				RequestDispatcher rd = request.getRequestDispatcher("EditScheduleScript");
				rd.forward(request, response);
			} else {
				json.put("success", false);
				json.put("message", "some thing went wrong");
			}
		}
		out.print(json);
	}
}

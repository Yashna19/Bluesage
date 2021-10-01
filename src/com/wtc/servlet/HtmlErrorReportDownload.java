/**
 * 
 */
package com.wtc.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wtc.Database.DatabaseConnection;

/**
 * @author Anusha
 *
 */
public class HtmlErrorReportDownload extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7803288272853475537L;

	/**
	 * 
	 */
	public HtmlErrorReportDownload() {
		// TODO Auto-generated constructor stub
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

	//	@SuppressWarnings("unchecked")
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DatabaseConnection obj = new DatabaseConnection();
		//		response.setContentType("text/html");
		//		PrintWriter out = response.getWriter();
		//		JSONObject json = new JSONObject();
		try {

			String id = request.getParameter("id");
			String filePath = obj.getErrorReport(id);

			File downloadFile = new File(filePath);
			FileInputStream inStream = new FileInputStream(downloadFile);

			response.setContentType("application/html");
			response.setContentLength((int) downloadFile.length());

			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// obtains response's output stream
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[4096];
			int bytesRead = -1;

			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inStream.close();
			outStream.close();

		} catch (Exception e) {
			//			json.put("success", false);
			//			json.put("message", "Somewhere went wrong");
			e.printStackTrace();
		}
		//		out.print(json);

	}

}
